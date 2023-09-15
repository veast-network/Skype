package codes.wilma24.Skype.server.v1_0_R1.command;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInPing;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLookupOnlineStatus;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.server.v1_0_R1.Skype;
import codes.wilma24.Skype.server.v1_0_R1.data.types.Connection;

public class LookupOnlineStatusCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutLookupOnlineStatus packet = Packet.fromJson(
				msg.toString(), PacketPlayOutLookupOnlineStatus.class);
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
			if (con2.isListening() == false) {
				continue;
			}
			if (!con2.getUniqueId().equals(conversationId)) {
				continue;
			}
			if (con2.getExpiryTime() - System.currentTimeMillis() < (28.5 * (60 * 1000L))) {
				/**
				 * We want to set the online status to offline after 1.5 minutes
				 * 
				 * If they refresh the token before 1.5 minutes it does not go
				 * offline
				 */
				continue;
			}
			try {
				con2.getSocketHandlerContext()
						.getSocket()
						.getOutputStream()
						.write(new PacketPlayInPing().toString().getBytes(
								"UTF-8"));
				con2.getSocketHandlerContext().getSocket().getOutputStream()
						.flush();
			} catch (Exception e) {
				Skype.getPlugin().getConnectionMap().remove(authCode2);
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
