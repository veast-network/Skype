package codes.elisa32.Skype.server.v1_0_R1.command;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLookupUser;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class LookupOnlineStatusCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutLookupUser packet = Packet.fromJson(msg.toString(),
				PacketPlayOutLookupUser.class);
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
		for (UUID authCode2 : Skype.getPlugin().getConnectionMap().keySet()
				.toArray(new UUID[0]).clone()) {
			Connection con2 = Skype.getPlugin().getUserManager()
					.getConnection(authCode2);
			if (con2 == null) {
				continue;
			}
			if (!con2.getUniqueId().equals(conversationId)) {
				continue;
			}
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.OK, "ONLINE");
			return replyPacket;
		}
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, "OFFLINE");
		return replyPacket;
	}

}
