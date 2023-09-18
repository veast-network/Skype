package codes.wilma24.Skype.v1_0_R1.command;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Arrays;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInVideoCallDataStreamRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptVideoCallDataStreamRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.cipher.CipherInputStream;
import codes.wilma24.Skype.v1_0_R1.data.types.Conversation;
import codes.wilma24.Skype.v1_0_R1.fontio.FontIO;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;
import codes.wilma24.Skype.v1_0_R1.plugin.Skype;

public class VideoCallDataStreamRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInVideoCallDataStreamRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayInVideoCallDataStreamRequest.class);
		UUID callId = packet.getCallId();
		UUID participantId = packet.getParticipantId();
		UUID authCode = MainForm.get().getAuthCode();
		UUID loggedInUser = MainForm.get().getLoggedInUser().getUniqueId();
		Optional<SocketHandlerContext> ctx2 = Skype.getPlugin().createHandle();
		if (!ctx2.isPresent()) {
			return PacketPlayInReply.empty();
		}
		Optional<PacketPlayInReply> reply = ctx2.get().getOutboundHandler()
				.dispatch(ctx2.get(), new PacketPlayOutLogin(authCode));
		if (!reply.isPresent()) {
			return PacketPlayInReply.empty();
		}
		if (reply.get().getStatusCode() != 200) {
			return PacketPlayInReply.empty();
		}
		if (MainForm.get().ongoingCall == false) {
			return PacketPlayInReply.empty();
		}
		authCode = UUID.fromString(reply.get().getText());
		ctx2.get()
				.getOutboundHandler()
				.write(ctx2.get(),
						new PacketPlayOutAcceptVideoCallDataStreamRequest(
								authCode, participantId, callId));
		Optional<Conversation> userLookup = MainForm.get().lookupUser(
				participantId);
		final Socket socket = ctx2.get().getSocket();
		Thread thread = new Thread(
				() -> {
					try {
						socket.setSoTimeout(0);
						MainForm.get().videoCallIncomingAudioSockets.add(ctx2
								.get().getSocket());
						MainForm.get().ongoingVideoCall = true;
						MainForm.get().refreshWindow();
						JFrame mainForm = MainForm.get();
						byte[] cipher = MainForm.get().ongoingVideoCallCipher;
						DataInputStream dis = new DataInputStream(socket
								.getInputStream());
						while (MainForm.get().isVisible()) {
							int length = dis.readInt();
							if (length == -1) {
								break;
							}
							byte[] b2 = new byte[length];
							dis.readFully(b2);
							CipherInputStream cis = new CipherInputStream(
									new ByteArrayInputStream(b2), cipher);
							byte[] b = new byte[length + 1000];
							int read = cis.read(b);
							b = Arrays.copyOf(b, read);
							cis.close();
							JLabel iconLabel = MainForm.get().ongoingCallProfilePictureImageLabel;
							int width = MainForm.ongoingCallProfilePictureImageLabelWidth;
							int height = MainForm.ongoingCallProfilePictureImageLabelHeight;
							BufferedImage resizedImg = new BufferedImage(width,
									height, BufferedImage.TYPE_INT_RGB);
							Graphics2D g2 = resizedImg.createGraphics();

							g2.setRenderingHint(
									RenderingHints.KEY_INTERPOLATION,
									RenderingHints.VALUE_INTERPOLATION_BILINEAR);
							try {
								ByteArrayInputStream bais = new ByteArrayInputStream(
										b);
								BufferedImage image = ImageIO.read(bais);
								g2.drawImage(image, 0, 0, width, height, null);
								if (userLookup.isPresent()) {
									String name = userLookup.get()
											.getDisplayName();
									Font font = FontIO.SEGOE_UI
											.deriveFont(15.0f);
									FontMetrics fm = Toolkit
											.getDefaultToolkit()
											.getFontMetrics(font);
									Rectangle2D rect = fm.getStringBounds(name,
											g2);
									g2.setFont(font);
									g2.setColor(new Color(0, 0, 0, 155));
									g2.fillRect(
											(int) rect.getX(),
											(int) rect.getY()
													+ (int) rect.getHeight()
													- 3, (int) rect.getWidth(),
											(int) rect.getHeight());
									g2.setColor(new Color(255, 255, 255, 155));
									g2.drawString(name, 0,
											(int) rect.getHeight() - 3);
								}
								image.flush();
								bais.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
							g2.dispose();

							final ImageIcon imageIcon = new ImageIcon(
									resizedImg);
							resizedImg.flush();
							iconLabel.setIcon(imageIcon);
							iconLabel.validate();
							iconLabel.repaint();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (MainForm.get().ongoingVideoCallId != null)
						if (callId.equals(MainForm.get().ongoingVideoCallId)) {
							MainForm.get().ongoingVideoCall = false;
							MainForm.get().ongoingVideoCallId = null;
							MainForm.get().ongoingVideoCallCipher = null;
							MainForm.get().refreshWindow(
									MainForm.get().SCROLL_TO_BOTTOM);
							try {
								for (Socket socket2 : MainForm.get().videoCallIncomingAudioSockets) {
									socket2.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								for (Socket socket2 : MainForm.get().videoCallOutgoingAudioSockets) {
									socket2.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							MainForm.get().videoEnabled = false;
							MainForm.get().videoMode = MainForm.get().WEBCAM_CAPTURE_MODE;
							MainForm.ongoingVideoCallWidth = 0;
							MainForm.ongoingVideoCallHeight = 0;
							try {
								Class<?> clazz = Class
										.forName("com.github.sarxos.webcam.Webcam");
								Method close = clazz.getMethod("close", null);
								close.invoke(MainForm.webcam, null);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
				});
		thread.start();

		return PacketPlayInReply.empty();
	}

}
