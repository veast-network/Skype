package codes.elisa32.Skype.server.v1_0_R1.command;

import java.util.List;
import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.data.types.Call;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutSendCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class SendCallRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutSendCallRequest packet = Packet.fromJson(msg.toString(),
				PacketPlayOutSendCallRequest.class);
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
		Call call = new Call(callId);
		int hits = 0;
		if (Skype.getPlugin().getConversationManager()
				.isGroupChat(conversationId)) {
			Optional<List<UUID>> participants = Skype.getPlugin()
					.getConversationManager().getParticipants(conversationId);
			PacketPlayInCallRequest callRequestPacket = new PacketPlayInCallRequest(
					participantId, callId);
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
			PacketPlayInCallRequest callRequestPacket = new PacketPlayInCallRequest(
					participantId, callId);
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
