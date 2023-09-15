package codes.wilma24.Skype.server.v1_0_R1.command;

import java.util.List;
import java.util.Optional;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.data.types.Call;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInVideoCallRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutSendVideoCallRequest;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.server.v1_0_R1.Skype;
import codes.wilma24.Skype.server.v1_0_R1.data.types.Connection;

public class SendVideoCallRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutSendVideoCallRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayOutSendVideoCallRequest.class);
		UUID authCode = packet.getAuthCode();
		UUID conversationId = packet.getConversationId();
		Connection con = Skype.getPlugin().getUserManager()
				.getConnection(authCode);
		if (con == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		UUID callId = UUID.randomUUID();
		UUID participantId = con.getUniqueId();
		String cipher = packet.getCipher();
		Call call = new Call(callId);
		int hits = 0;
		if (Skype.getPlugin().getUserManager().isGroupChat(conversationId)) {
			Optional<List<UUID>> participants = Skype.getPlugin()
					.getConversationManager().getParticipants(conversationId);
			PacketPlayInVideoCallRequest callRequestPacket = new PacketPlayInVideoCallRequest(
					conversationId, participantId, callId, cipher);
			if (participants.isPresent()) {
				for (UUID participant : participants.get()) {
					if (participant.equals(participantId)) {
						continue;
					}
					call.addParticipant(participant);
					for (Connection listeningParticipant : Skype.getPlugin()
							.getUserManager()
							.getListeningConnections(participant)) {
						try {
							listeningParticipant
									.getSocketHandlerContext()
									.getOutboundHandler()
									.dispatchAsync(
											listeningParticipant
													.getSocketHandlerContext(),
											callRequestPacket, null);
							hits++;
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						}
					}
				}
			}
			for (Connection listeningParticipant : Skype.getPlugin()
					.getUserManager().getListeningConnections(participantId)) {
				try {
					listeningParticipant
							.getSocketHandlerContext()
							.getOutboundHandler()
							.dispatchAsync(
									listeningParticipant
											.getSocketHandlerContext(),
									callRequestPacket, null);
					hits++;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		} else {
			PacketPlayInVideoCallRequest callRequestPacket = new PacketPlayInVideoCallRequest(
					participantId, participantId, callId, cipher);
			call.addParticipant(conversationId);
			for (Connection listeningParticipant : Skype.getPlugin()
					.getUserManager().getListeningConnections(conversationId)) {
				try {
					listeningParticipant
							.getSocketHandlerContext()
							.getOutboundHandler()
							.dispatchAsync(
									listeningParticipant
											.getSocketHandlerContext(),
									callRequestPacket, null);
					hits++;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			for (Connection listeningParticipant : Skype.getPlugin()
					.getUserManager().getListeningConnections(participantId)) {
				try {
					listeningParticipant
							.getSocketHandlerContext()
							.getOutboundHandler()
							.dispatchAsync(
									listeningParticipant
											.getSocketHandlerContext(),
									callRequestPacket, null);
					hits++;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		call.addParticipant(participantId);

		if (hits == 0) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}

		Skype.getPlugin().getCallMap().put(callId, call);

		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}
}
