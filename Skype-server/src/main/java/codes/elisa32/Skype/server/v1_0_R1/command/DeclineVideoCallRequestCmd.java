package codes.elisa32.Skype.server.v1_0_R1.command;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.data.types.Call;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInDeclineVideoCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutDeclineVideoCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class DeclineVideoCallRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutDeclineVideoCallRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayOutDeclineVideoCallRequest.class);
		UUID authCode = packet.getAuthCode();
		Connection con = Skype.getPlugin().getUserManager()
				.getConnection(authCode);
		if (con == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		UUID callId = packet.getCallId();
		UUID participantId = con.getUniqueId();
		Call call = Skype.getPlugin().getCallMap().getOrDefault(callId, null);
		if (call == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		if (!call.isParticipant(participantId)) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		PacketPlayInDeclineVideoCallRequest declineCallRequestPacket = new PacketPlayInDeclineVideoCallRequest(
				participantId, callId);
		int hits = 0;
		for (UUID participant : call.getParticipants()) {
			boolean hasParticipantAnsweredCall = Skype.getPlugin()
					.getUserManager().getConnectionsInCall(participant, callId)
					.size() > 0;
			if (hasParticipantAnsweredCall) {
				for (Connection listeningParticipant : Skype.getPlugin()
						.getUserManager().getListeningConnections(participant)) {
					try {
						listeningParticipant
								.getSocketHandlerContext()
								.getOutboundHandler()
								.dispatchAsync(
										listeningParticipant
												.getSocketHandlerContext(),
										declineCallRequestPacket, null);
						hits++;
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}

		call.removeParticipant(participantId);

		if (hits == 0) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}

		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}

}
