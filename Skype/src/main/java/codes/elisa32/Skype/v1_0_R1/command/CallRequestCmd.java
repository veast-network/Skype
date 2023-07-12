package codes.elisa32.Skype.v1_0_R1.command;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Optional;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.audioio.AudioIO;
import codes.elisa32.Skype.v1_0_R1.data.types.Conversation;
import codes.elisa32.Skype.v1_0_R1.forms.IncomingCallForm;
import codes.elisa32.Skype.v1_0_R1.forms.MainForm;
import codes.elisa32.Skype.v1_0_R1.plugin.Skype;

public class CallRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInCallRequest packet = Packet.fromJson(msg.toString(),
				PacketPlayInCallRequest.class);
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
		authCode = UUID.fromString(reply.get().getText());
		UUID conversationId = packet.getConversationId();
		Conversation personWhoIsCalling = null;
		for (Conversation conversation : MainForm.get().getConversations()) {
			if (conversation.getUniqueId().equals(conversationId)) {
				personWhoIsCalling = conversation;
			}
		}
		if (personWhoIsCalling == null || personWhoIsCalling.getUniqueId().equals(loggedInUser)) {
			reply = ctx2
					.get()
					.getOutboundHandler()
					.dispatch(
							ctx2.get(),
							new PacketPlayOutAcceptCallRequest(authCode, callId));
			if (!reply.isPresent()) {
				return PacketPlayInReply.empty();
			}
			if (reply.get().getStatusCode() != 200) {
				return PacketPlayInReply.empty();
			}
			MainForm.get().mic.stop();
			MainForm.get().mic.drain();
			Thread thread = new Thread(
					() -> {
						try {
							DataOutputStream out = new DataOutputStream(ctx2
									.get().getSocket().getOutputStream());
							byte tmpBuff[] = new byte[MainForm.get().mic
									.getBufferSize() / 5];
							MainForm.get().mic.start();
							MainForm.get().callOutgoingAudioSocket = ctx2.get()
									.getSocket();
							while (true) {
								try {
									int count = MainForm.get().mic.read(
											tmpBuff, 0, tmpBuff.length);
									if (count > 0) {
										out.write(tmpBuff, 0, count);
									}
								} catch (Exception e) {
									e.printStackTrace();
									break;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						try {
							MainForm.get().mic.stop();
							MainForm.get().mic.drain();
						} catch (Exception e2) {
							e2.printStackTrace();
						}
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
		} else {
			IncomingCallForm incomingCallForm = new IncomingCallForm(packet, personWhoIsCalling, true);
			incomingCallForm.show();
		}
		return PacketPlayInReply.empty();
	}
}
