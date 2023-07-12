package codes.elisa32.Skype.server.v1_0_R1.command;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.data.types.Call;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInAcceptCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInCallDataStreamRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;
import codes.elisa32.Skype.server.v1_0_R1.socket.CallingInboundHandler;

public class AcceptCallRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutAcceptCallRequest packet = Packet.fromJson(msg.toString(),
				PacketPlayOutAcceptCallRequest.class);
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
		PacketPlayInAcceptCallRequest acceptCallRequestPacket = new PacketPlayInAcceptCallRequest(
				participantId, callId);
		{
			PacketPlayInCallDataStreamRequest callDataStreamRequestPacket = new PacketPlayInCallDataStreamRequest(
					participantId, callId);

			for (UUID callParticipant : call.getParticipants()) {
				boolean hasParticipantAnsweredCall = Skype.getPlugin()
						.getUserManager()
						.getConnectionsInCall(callParticipant, callId).size() > 0;
				if (!hasParticipantAnsweredCall) {
					continue;
				}
				for (Connection listeningParticipant : Skype.getPlugin()
						.getUserManager()
						.getListeningConnections(callParticipant)) {
					Thread thread = new Thread(
							() -> {
								listeningParticipant
										.getSocketHandlerContext()
										.getOutboundHandler()
										.dispatch(
												listeningParticipant
														.getSocketHandlerContext(),
												acceptCallRequestPacket);
								listeningParticipant
										.getSocketHandlerContext()
										.getOutboundHandler()
										.dispatch(
												listeningParticipant
														.getSocketHandlerContext(),
												callDataStreamRequestPacket);
							});
					thread.start();
				}
			}
		}

		{
			for (UUID callParticipant : call.getParticipants()) {
				boolean hasParticipantAnsweredCall = Skype.getPlugin()
						.getUserManager()
						.getConnectionsInCall(callParticipant, callId).size() > 0;
				if (!hasParticipantAnsweredCall) {
					continue;
				}
				PacketPlayInCallDataStreamRequest callDataStreamRequestPacket = new PacketPlayInCallDataStreamRequest(
						callParticipant, callId);
				for (Connection listeningParticipant : Skype.getPlugin()
						.getUserManager()
						.getListeningConnections(participantId)) {
					Thread thread = new Thread(
							() -> {
								listeningParticipant
										.getSocketHandlerContext()
										.getOutboundHandler()
										.dispatch(
												listeningParticipant
														.getSocketHandlerContext(),
												acceptCallRequestPacket);
								listeningParticipant
										.getSocketHandlerContext()
										.getOutboundHandler()
										.dispatch(
												listeningParticipant
														.getSocketHandlerContext(),
												callDataStreamRequestPacket);
							});
					thread.start();
				}
			}
		}

		con.setCallId(callId);
		ctx.fireInboundHandlerInactive();

		CallingInboundHandler inboundHandler = new CallingInboundHandler(call,
				participantId, ctx, con);
		Thread thread = new Thread(inboundHandler);
		thread.start();

		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}

}
