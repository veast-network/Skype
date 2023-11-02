package codes.wilma24.Skype.v1_0_R1.command;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.bouncycastle.util.Arrays;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInVideoCallRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptVideoCallRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutVideoCallResolutionChanged;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.cipher.CipherOutputStream;
import codes.wilma24.Skype.v1_0_R1.cipher.CipherUtilities;
import codes.wilma24.Skype.v1_0_R1.data.types.Conversation;
import codes.wilma24.Skype.v1_0_R1.forms.DialogForm;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;
import codes.wilma24.Skype.v1_0_R1.pgp.PGPUtilities;
import codes.wilma24.Skype.v1_0_R1.pgp.PGPUtilities.DecryptionResult;
import codes.wilma24.Skype.v1_0_R1.plugin.Skype;

public class VideoCallRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInVideoCallRequest packet = Packet.fromJson(msg.toString(),
				PacketPlayInVideoCallRequest.class);
		UUID callId = packet.getCallId();
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
		UUID conversationId = packet.getConversationId();
		UUID participantId = packet.getParticipantId();

		final byte[] cipher;

		{
			String pgp = packet.getCipher();
			Optional<Conversation> userLookup = MainForm.get().lookupUser(
					participantId);
			if (!userLookup.isPresent()) {
				return PacketPlayInReply.empty();
			}
			DecryptionResult result = PGPUtilities.decryptAndVerify(pgp,
					userLookup.get());
			if (!result.isSuccessful() || !result.isSignatureVerified()) {
				return PacketPlayInReply.empty();
			}
			cipher = CipherUtilities.decodeBase64(result.getMessage());
		}

		Conversation personWhoIsCalling = null;
		for (Conversation conversation : MainForm.get().getConversations()) {
			if (conversation.getUniqueId().equals(conversationId)) {
				personWhoIsCalling = conversation;
			}
		}
		boolean hit = false;
		if (personWhoIsCalling == null) {
			Optional<Conversation> userLookup = MainForm.get().lookupUser(
					conversationId);
			if (userLookup.isPresent()) {
				personWhoIsCalling = userLookup.get();
				hit = true;
			}
		}
		if (participantId.equals(loggedInUser)) {
			MainForm.get().ongoingVideoCallId = callId;
			MainForm.get().ongoingVideoCallCipher = cipher;
			reply = ctx2
					.get()
					.getOutboundHandler()
					.dispatch(
							ctx2.get(),
							new PacketPlayOutAcceptVideoCallRequest(authCode,
									callId));
			if (!reply.isPresent()) {
				return PacketPlayInReply.empty();
			}
			if (reply.get().getStatusCode() != 200) {
				return PacketPlayInReply.empty();
			}
			Thread thread = new Thread(
					() -> {

						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						CipherOutputStream cos = new CipherOutputStream(baos,
								cipher);
						Object webcam = MainForm.webcam;
						BufferedImage image = null;
						boolean err2 = false;
						if (MainForm.get().videoMode == MainForm.get().WEBCAM_CAPTURE_MODE) {
							try {
								if (System.getProperty("os.name").startsWith(
										"Windows")) {
									Class<?> clazz = Class
											.forName("com.github.sarxos.webcam.Webcam");
									Method getDefault = clazz.getMethod(
											"getDefault", null);
									Method open = clazz.getMethod("open", null);
									Method close = clazz.getMethod("close",
											null);
									Method getImage = clazz.getMethod(
											"getImage", null);
									if (webcam == null) {
										Object o = getDefault.invoke(clazz,
												null);
										webcam = o;
									}
									boolean ret = (boolean) open.invoke(webcam,
											null);
									if (!ret) {
										err2 = true;
									} else {
										Object ret2 = getImage.invoke(webcam,
												null);
										if (ret2 == null) {
											err2 = true;
										} else {
											image = (BufferedImage) ret2;
										}
									}
								} else {
									err2 = true;
								}
							} catch (Exception e) {
								e.printStackTrace();
								err2 = true;
							}
						}
						if (err2) {
							DialogForm form = new DialogForm(
									MainForm.get(),
									"Skype"+"\u2122"+" - "
											+ MainForm.get().getLoggedInUser()
													.getSkypeName(),
									"Webcam sharing failed",
									"The webcam could not be opened, please check to make"
											+ '\n'
											+ "sure no other programs are using your webcam."
											+ '\n'
											+ '\n'
											+ " "
											+ '\n'
											+ "Having issues? Please check to make sure your webcam driver"
											+ '\n'
											+ "is working by checking the device status in devmgmt.msc.",
									null, null);
							form.show();
							try {
								Class<?> clazz = Class
										.forName("com.github.sarxos.webcam.Webcam");
								Method close = clazz.getMethod("close", null);
								close.invoke(MainForm.webcam, null);
							} catch (Exception e) {
								e.printStackTrace();
							}
							return;
						}
						Rectangle screenRect = new Rectangle(Toolkit
								.getDefaultToolkit().getScreenSize());
						if (MainForm.get().videoMode == MainForm.get().WEBCAM_CAPTURE_MODE) {
							screenRect = new Rectangle(0, 0, image.getWidth(),
									image.getHeight());
						}
						try {
							Socket socket2 = ctx2.get().getSocket();
							Optional<SocketHandlerContext> ctx3 = Skype
									.getPlugin().createHandle();
							if (!ctx3.isPresent()) {
								return;
							}
							Optional<PacketPlayInReply> reply2 = ctx3
									.get()
									.getOutboundHandler()
									.dispatch(
											ctx3.get(),
											new PacketPlayOutLogin(MainForm
													.get().getAuthCode()));
							if (!reply2.isPresent()) {
								return;
							}
							if (reply2.get().getStatusCode() != 200) {
								return;
							}
							UUID authCode2 = UUID.fromString(reply2.get()
									.getText());
							reply2 = ctx3
									.get()
									.getOutboundHandler()
									.dispatch(
											ctx3.get(),
											new PacketPlayOutVideoCallResolutionChanged(
													authCode2, callId,
													screenRect.width,
													screenRect.height));
							if (!reply2.isPresent()) {
								return;
							}
							if (reply2.get().getStatusCode() != 200) {
								return;
							}
							MainForm.get().videoCallOutgoingAudioSockets
									.add(socket2);
							socket2.setSoTimeout(2000);
							Socket socket = null;
							{
								InputStream dis = socket2.getInputStream();
								OutputStream dos = socket2.getOutputStream();
								boolean err = false;
								int port = 0;
								do {
									try {
										dos.write("200 OK".getBytes());
										dos.flush();
										byte[] b2 = new byte[1024];
										int bytesRead2 = dis.read(b2);
										port = Integer.parseInt(new String(
												Arrays.copyOf(b2, bytesRead2)));
										err = false;
									} catch (SocketException e) {
										e.printStackTrace();
										break;
									} catch (Exception e) {
										e.printStackTrace();
										err = true;
									}
								} while (err == true);
								if (port != 0) {
									String hostname = Skype.getPlugin()
											.getHostname();
									socket = new Socket(hostname, port);
								}
							}
							if (socket != null) {
								MainForm.get().videoCallOutgoingAudioSockets
										.add(socket);
								JFrame mainForm = MainForm.get();
								DataOutputStream dos = new DataOutputStream(
										socket.getOutputStream());
								Robot robot = new Robot();
								Class<?> clazz = Class
										.forName("com.github.sarxos.webcam.Webcam");
								Method getImage = clazz.getMethod("getImage",
										null);
								while (mainForm.isVisible()) {
									if (MainForm.get().videoMode == MainForm
											.get().WEBCAM_CAPTURE_MODE) {
										image = (BufferedImage) getImage
												.invoke(webcam, null);
										ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
										ImageIO.write(image, "jpg", baos2);
										byte[] b2 = baos2.toByteArray();
										baos.reset();
										cos.write(b2);
										byte[] b = baos.toByteArray();
										dos.writeInt(b.length);
										dos.flush();
										dos.write(b);
										dos.flush();
										image.flush();
										baos2.close();
										System.gc();
									} else if (MainForm.get().videoMode == MainForm
											.get().SCREEN_CAPTURE_MODE) {
										BufferedImage bi = robot
												.createScreenCapture(screenRect);
										ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
										ImageIO.write(bi, "jpg", baos2);
										byte[] b2 = baos2.toByteArray();
										bi.flush();
										baos.reset();
										cos.write(b2);
										byte[] b = baos.toByteArray();
										dos.writeInt(b.length);
										dos.flush();
										dos.write(b);
										dos.flush();
										System.gc();
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						try {
							cos.close();
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						try {
							Class<?> clazz = Class
									.forName("com.github.sarxos.webcam.Webcam");
							Method close = clazz.getMethod("close", null);
							close.invoke(webcam, null);
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
									for (Socket socket : MainForm.get().videoCallIncomingAudioSockets) {
										socket.close();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								try {
									for (Socket socket : MainForm.get().videoCallOutgoingAudioSockets) {
										socket.close();
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
									Method close = clazz.getMethod("close",
											null);
									close.invoke(MainForm.webcam, null);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
					});
			thread.start();
		} else {
			if (hit) {
				MainForm.get().getConversations().add(personWhoIsCalling);
			}
			MainForm.get().ongoingVideoCallId = callId;
			MainForm.get().ongoingVideoCallCipher = cipher;
			reply = ctx2
					.get()
					.getOutboundHandler()
					.dispatch(
							ctx2.get(),
							new PacketPlayOutAcceptVideoCallRequest(authCode,
									callId));
			if (!reply.isPresent()) {
				return PacketPlayInReply.empty();
			}
			if (reply.get().getStatusCode() != 200) {
				return PacketPlayInReply.empty();
			}
			MainForm.get().videoCallOutgoingAudioSockets.clear();
			try {
				Socket socket2 = ctx2.get().getSocket();
				socket2.setSoTimeout(2000);
				MainForm.get().videoCallOutgoingAudioSockets.add(socket2);
				Socket socket = null;
				{
					InputStream dis = socket2.getInputStream();
					OutputStream dos = socket2.getOutputStream();
					boolean err = false;
					int port = 0;
					do {
						try {
							dos.write("200 OK".getBytes());
							dos.flush();
							byte[] b2 = new byte[1024];
							int bytesRead2 = dis.read(b2);
							port = Integer.parseInt(new String(Arrays.copyOf(
									b2, bytesRead2)));
							err = false;
						} catch (SocketException e) {
							e.printStackTrace();
							break;
						} catch (Exception e) {
							e.printStackTrace();
							err = true;
						}
					} while (err == true);
					if (port != 0) {
						String hostname = Skype.getPlugin().getHostname();
						socket = new Socket(hostname, port);
					}
				}
				if (socket != null) {
					MainForm.get().videoCallOutgoingAudioSockets.add(socket);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return PacketPlayInReply.empty();
	}
}
