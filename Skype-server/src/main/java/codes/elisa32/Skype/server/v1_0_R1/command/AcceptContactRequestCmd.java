package codes.elisa32.Skype.server.v1_0_R1.command;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInAcceptContactRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInRemoveMessage;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptContactRequest;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class AcceptContactRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutAcceptContactRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayOutAcceptContactRequest.class);
		UUID authCode = packet.getAuthCode();
		Connection con = Skype.getPlugin().getUserManager()
				.getConnection(authCode);
		if (con == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		UUID participantId = con.getUniqueId();
		UUID conversationId = packet.getConversationId();
		UUID messageId = packet.getContactRequestMessageId();
		long timestamp = packet.getContactRequestTimestamp();

		if (!Skype.getPlugin().getUserManager()
				.hasContactRequest(participantId, conversationId)) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}

		Skype.getPlugin().getUserManager()
				.addContact(conversationId, participantId);
		Skype.getPlugin().getUserManager()
				.addContact(participantId, conversationId);

		Skype.getPlugin()
				.getConversationManager()
				.addMessage(participantId, conversationId, messageId, null,
						timestamp);

		{
			PacketPlayInAcceptContactRequest acceptContactRequestPacket = new PacketPlayInAcceptContactRequest(
					participantId, conversationId);

			for (Connection listeningParticipant : Skype.getPlugin()
					.getUserManager().getListeningConnections(conversationId)) {
				try {
					listeningParticipant
							.getSocketHandlerContext()
							.getOutboundHandler()
							.dispatchAsync(
									listeningParticipant
											.getSocketHandlerContext(),
									acceptContactRequestPacket, null);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}

		{
			PacketPlayInAcceptContactRequest acceptContactRequestPacket = new PacketPlayInAcceptContactRequest(
					conversationId, participantId);

			for (Connection listeningParticipant : Skype.getPlugin()
					.getUserManager().getListeningConnections(participantId)) {
				try {
					listeningParticipant
							.getSocketHandlerContext()
							.getOutboundHandler()
							.dispatchAsync(
									listeningParticipant
											.getSocketHandlerContext(),
									acceptContactRequestPacket, null);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}

		PacketPlayInRemoveMessage removeMessagePacket = new PacketPlayInRemoveMessage(
				conversationId, messageId);

		for (Connection listeningParticipant : Skype.getPlugin()
				.getUserManager().getListeningConnections(participantId)) {
			try {
				listeningParticipant
						.getSocketHandlerContext()
						.getOutboundHandler()
						.dispatchAsync(
								listeningParticipant.getSocketHandlerContext(),
								removeMessagePacket, null);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}

}
