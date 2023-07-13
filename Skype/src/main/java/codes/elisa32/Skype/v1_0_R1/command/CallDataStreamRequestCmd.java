package codes.elisa32.Skype.v1_0_R1.command;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Optional;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JFrame;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInCallDataStreamRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptCallDataStreamRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.audioio.AudioIO;
import codes.elisa32.Skype.v1_0_R1.data.types.Conversation;
import codes.elisa32.Skype.v1_0_R1.forms.MainForm;
import codes.elisa32.Skype.v1_0_R1.plugin.Skype;

public class CallDataStreamRequestCmd extends CommandExecutor {

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
		reply = ctx2
				.get()
				.getOutboundHandler()
				.dispatch(
						ctx2.get(),
						new PacketPlayOutAcceptCallDataStreamRequest(authCode,
								participantId, callId));
		if (!reply.isPresent()) {
			return PacketPlayInReply.empty();
		}
		if (reply.get().getStatusCode() != 200) {
			return PacketPlayInReply.empty();
		}
		final Socket socket = ctx2.get().getSocket();
		Thread thread = new Thread(
				() -> {
					float sampleRate = 16000.0F;
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
						MainForm.get().callIncomingAudioSocket = ctx2.get()
								.getSocket();
						for (Conversation conversation : MainForm.get()
								.getConversations()) {
							if (conversation.getUniqueId()
									.equals(participantId)) {
								MainForm.get().setSelectedConversation(
										conversation);
								MainForm.get().ongoingCallConversation = conversation;
							}
						}
						MainForm.get().rightPanelPage = "OngoingCall";
						MainForm.get().ongoingCall = true;
						MainForm.get().ongoingCallStartTime = System
								.currentTimeMillis();
						MainForm.get().refreshWindow();
						JFrame mainForm = MainForm.get();
						while (mainForm.isVisible()) {
							try {
								byte[] b = new byte[1024];
								int len = socket.getInputStream().read(b, 0,
										b.length);
								System.out.println(len);
								if (len == -1) {
									break;
								}
								b = Arrays.copyOf(b, len);
								ByteArrayInputStream bais = new ByteArrayInputStream(
										b);
								AudioInputStream ais = new AudioInputStream(
										bais, format, len);
								int bytesRead = 0;
								byte[] data = new byte[1024];
								bytesRead = ais.read(data);
								if (bytesRead == -1) {
									break;
								}
								speaker.write(data, 0, bytesRead);
								ais.close();
								bais.close();
								socket.setSoTimeout(8000);
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
					AudioIO.HANGUP.playSound();
					MainForm.get().ongoingCall = false;
					MainForm.get().ongoingCallConversation = null;
					MainForm.get().rightPanelPage = "Conversation";
					MainForm.get().ongoingCallStartTime = 0L;
					MainForm.get().refreshWindow(true);
					try {
						MainForm.get().callIncomingAudioSocket.close();
						MainForm.get().callOutgoingAudioSocket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
		thread.start();

		return PacketPlayInReply.empty();
	}

}
