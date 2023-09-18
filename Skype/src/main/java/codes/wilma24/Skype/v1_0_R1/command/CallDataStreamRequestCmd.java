package codes.wilma24.Skype.v1_0_R1.command;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Optional;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;

import org.bouncycastle.util.io.Streams;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInCallDataStreamRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptCallDataStreamRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.cipher.CipherInputStream;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;
import codes.wilma24.Skype.v1_0_R1.plugin.Skype;

public class CallDataStreamRequestCmd extends CommandExecutor {

	public void run2(SourceDataLine speaker, AudioFormat format, byte[] b,
			int len) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		AudioInputStream ais = new AudioInputStream(bais, format, len);
		int bytesRead = 0;
		byte[] data = new byte[1616];
		bytesRead = ais.read(data);
		speaker.write(data, 0, bytesRead);
		ais.close();
		bais.close();
	}

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInCallDataStreamRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayInCallDataStreamRequest.class);
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
		authCode = UUID.fromString(reply.get().getText());
		ctx2.get()
				.getOutboundHandler()
				.write(ctx2.get(),
						new PacketPlayOutAcceptCallDataStreamRequest(authCode,
								participantId, callId));
		final Socket socket = ctx2.get().getSocket();
		Thread thread = new Thread(
				() -> {
					float sampleRate = 8000.0F;
					int sampleSizeBits = 16;
					int channels = 1;
					boolean signed = true;
					boolean bigEndian = false;
					AudioFormat format = new AudioFormat(sampleRate,
							sampleSizeBits, channels, signed, bigEndian);
					SourceDataLine speaker = null;
					try {
						DataLine.Info speakerInfo = new DataLine.Info(
								SourceDataLine.class, format);
						speaker = (SourceDataLine) AudioSystem
								.getLine(speakerInfo);
						speaker.open(format);
						speaker.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						socket.setSoTimeout(0);
						MainForm.get().callIncomingAudioSockets.add(ctx2.get()
								.getSocket());
						MainForm.get().rightPanelPage = "OngoingCall";
						MainForm.get().ongoingCall = true;
						MainForm.get().ongoingCallStartTime = System
								.currentTimeMillis();
						MainForm.get().refreshWindow();
						JFrame mainForm = MainForm.get();
						byte[] cipher = MainForm.get().ongoingCallCipher;
						DataInputStream dis = new DataInputStream(socket
								.getInputStream());
						while (mainForm.isVisible()) {
							try {
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								byte[] b = new byte[1616];
								dis.readFully(b);
								baos.write(b);
								CipherInputStream cis = new CipherInputStream(
										new ByteArrayInputStream(baos
												.toByteArray()), cipher);
								ByteArrayOutputStream bos = new ByteArrayOutputStream();
								Streams.pipeAll(cis, bos);
								cis.close();
								run2(speaker, format, bos.toByteArray(),
										bos.size());
								bos.close();
							} catch (Exception e) {
								e.printStackTrace();
								break;
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					speaker.drain();
					speaker.close();
					if (MainForm.get().ongoingCallId != null)
						if (callId.equals(MainForm.get().ongoingCallId)) {
							MainForm.get().ongoingCall = false;
							MainForm.get().ongoingCallConversation = null;
							MainForm.get().ongoingCallParticipants.clear();
							MainForm.get().ongoingCallId = null;
							MainForm.get().ongoingCallCipher = null;
							MainForm.get().rightPanelPage = "Conversation";
							MainForm.get().ongoingCallStartTime = 0L;
							MainForm.get().refreshWindow(
									MainForm.get().SCROLL_TO_BOTTOM);
							try {
								for (Socket socket2 : MainForm.get().callIncomingAudioSockets) {
									socket2.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								for (Socket socket2 : MainForm.get().callOutgoingAudioSockets) {
									socket2.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							MainForm.get().ongoingVideoCall = false;
							MainForm.get().ongoingVideoCallId = null;
							MainForm.get().ongoingVideoCallCipher = null;
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
							MainForm.get().microphoneEnabled = true;
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
