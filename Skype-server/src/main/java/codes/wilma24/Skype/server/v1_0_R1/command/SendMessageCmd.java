package codes.wilma24.Skype.server.v1_0_R1.command;

import java.util.List;
import java.util.Optional;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInMessage;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutSendMessage;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.server.v1_0_R1.Skype;
import codes.wilma24.Skype.server.v1_0_R1.data.types.Connection;

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
		if (Skype.getPlugin().getUserManager().isGroupChat(conversationId)) {
			Optional<List<UUID>> participants = Skype.getPlugin()
					.getConversationManager().getParticipants(conversationId);
			if (participants.isPresent()) {
				for (UUID participantId2 : participants.get()) {
					PacketPlayInMessage messageReceivePacket = new PacketPlayInMessage(
							conversationId, participantId, payload);
					for (Connection listeningParticipant : Skype.getPlugin()
							.getUserManager()
							.getListeningConnections(participantId2)) {
						try {
							listeningParticipant
									.getSocketHandlerContext()
									.getOutboundHandler()
									.dispatchAsync(
											listeningParticipant
													.getSocketHandlerContext(),
											messageReceivePacket, null);
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} else {
			PacketPlayInMessage messageReceivePacket = new PacketPlayInMessage(
					participantId, participantId, payload);
			for (Connection listeningParticipant : Skype.getPlugin()
					.getUserManager().getListeningConnections(conversationId)) {
				try {
					listeningParticipant
							.getSocketHandlerContext()
							.getOutboundHandler()
							.dispatchAsync(
									listeningParticipant
											.getSocketHandlerContext(),
									messageReceivePacket, null);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}

}
