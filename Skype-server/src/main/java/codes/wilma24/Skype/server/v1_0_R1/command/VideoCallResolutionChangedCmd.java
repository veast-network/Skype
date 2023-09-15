package codes.wilma24.Skype.server.v1_0_R1.command;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.data.types.Call;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInVideoCallResolutionChanged;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutVideoCallResolutionChanged;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.server.v1_0_R1.Skype;
import codes.wilma24.Skype.server.v1_0_R1.data.types.Connection;

public class VideoCallResolutionChangedCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutVideoCallResolutionChanged packet = Packet.fromJson(
				msg.toString(), PacketPlayOutVideoCallResolutionChanged.class);
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
		int width = packet.getWidth();
		int height = packet.getHeight();
		Call call = Skype.getPlugin().getCallMap().get(callId);
		if (call == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		if (!call.isParticipant(con.getUniqueId())) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		if (con.isListening()) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		PacketPlayInVideoCallResolutionChanged videoCallResolutionChangedPacket = new PacketPlayInVideoCallResolutionChanged(
				participantId, callId, width, height);
		{
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
										.write(listeningParticipant
												.getSocketHandlerContext(),
												videoCallResolutionChangedPacket);
							});
					thread.start();
				}
			}
		}
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}

}
