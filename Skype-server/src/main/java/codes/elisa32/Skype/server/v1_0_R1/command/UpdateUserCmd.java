package codes.elisa32.Skype.server.v1_0_R1.command;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInUpdateUser;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutUpdateUser;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class UpdateUserCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutUpdateUser packet = Packet.fromJson(msg.toString(),
				PacketPlayOutUpdateUser.class);
		UUID authCode = packet.getAuthCode();
		UUID conversationId = packet.getConversationId();
		Object payload = packet.getPayload();
		Connection con = Skype.getPlugin().getUserManager()
				.getConnection(authCode);
		if (con == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		if (!Skype.getPlugin().getUserManager().getUniqueId(con.getSkypeName())
				.equals(conversationId)) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		if (!Skype.getPlugin().getConversationManager()
				.updateConversation(conversationId, payload)) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		if (packet.isSilent() == false) {
			for (UUID authCode2 : Skype.getPlugin().getConnectionMap().keySet()
					.toArray(new UUID[0]).clone()) {
				Connection con2 = Skype.getPlugin().getUserManager()
						.getConnection(authCode2);
				if (con2 == null) {
					continue;
				}
				if (con2.isListening() && !con2.isInCall()
						&& !con2.isCallDataStream()) {
					try {
						con2.getSocketHandlerContext()
								.getOutboundHandler()
								.write(con2.getSocketHandlerContext(),
										new PacketPlayInUpdateUser(
												conversationId, payload));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}

}
