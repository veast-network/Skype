package codes.elisa32.Skype.server.v1_0_R1.command;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInRemoveContact;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutRemoveContact;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class RemoveContactCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutRemoveContact packet = Packet.fromJson(msg.toString(),
				PacketPlayOutRemoveContact.class);
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

		if (!Skype.getPlugin().getUserManager()
				.hasContact(conversationId, participantId)) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}

		Skype.getPlugin().getUserManager()
				.removeContact(conversationId, participantId);
		Skype.getPlugin().getUserManager()
				.removeContact(participantId, conversationId);

		PacketPlayInRemoveContact removeContactPacket = new PacketPlayInRemoveContact(
				participantId, conversationId);
		for (Connection listeningParticipant : Skype.getPlugin()
				.getUserManager().getListeningConnections(conversationId)) {
			try {
				listeningParticipant
						.getSocketHandlerContext()
						.getOutboundHandler()
						.dispatchAsync(
								listeningParticipant.getSocketHandlerContext(),
								removeContactPacket, null);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}

}
