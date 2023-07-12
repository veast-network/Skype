package codes.elisa32.Skype.server.v1_0_R1.command;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInMessage;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutSendMessage;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class SendMessageCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutSendMessage packet = Packet.fromJson(msg.toString(),
				PacketPlayOutSendMessage.class);
		UUID authCode = packet.getAuthCode();
		UUID conversationId = packet.getConversationId();
		UUID messageId = packet.getMessageId();
		Object payload = packet.getPayload();
		long timestamp = packet.getTimestamp();
		Connection con = Skype.getPlugin().getUserManager()
				.getConnection(authCode);
		if (con == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		UUID participantId = con.getUniqueId();
		if (Skype
				.getPlugin()
				.getConversationManager()
				.addMessage(conversationId, participantId, messageId, payload,
						timestamp) == false) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		Skype.getPlugin().getConversationManager()
				.addParticipants(conversationId, participantId);
		Skype.getPlugin().getConversationManager()
				.addParticipants(participantId, conversationId);
		PacketPlayInMessage messageReceivePacket = new PacketPlayInMessage(
				participantId, participantId, payload);
		for (Connection listeningParticipant : Skype.getPlugin()
				.getUserManager().getListeningConnections(conversationId)) {
			try {
				listeningParticipant
						.getSocketHandlerContext()
						.getOutboundHandler()
						.dispatchAsync(
								listeningParticipant.getSocketHandlerContext(),
								messageReceivePacket, null);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}

}
