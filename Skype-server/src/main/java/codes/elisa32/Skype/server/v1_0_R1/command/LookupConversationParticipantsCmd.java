package codes.elisa32.Skype.server.v1_0_R1.command;

import java.util.ArrayList;
import java.util.List;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLookupOnlineStatus;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class LookupConversationParticipantsCmd extends CommandExecutor {

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
		if (!Skype.getPlugin().getUserManager().isGroupChat(conversationId)) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		List<String> participantIds = new ArrayList<>();
		for (UUID participantId : Skype.getPlugin().getConversationManager()
				.getParticipants(conversationId).get()) {
			participantIds.add(participantId.toString());
		}
		String json = GsonBuilder.create().toJson(participantIds);
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, json);
		return replyPacket;
	}

}
