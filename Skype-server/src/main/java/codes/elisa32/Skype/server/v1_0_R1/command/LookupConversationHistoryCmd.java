package codes.elisa32.Skype.server.v1_0_R1.command;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLookupMessageHistory;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class LookupConversationHistoryCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutLookupMessageHistory packet = Packet.fromJson(
				msg.toString(), PacketPlayOutLookupMessageHistory.class);
		UUID authCode = packet.getAuthCode();
		Date from = packet.getFrom();
		Date to = packet.getTo();
		Connection con = Skype.getPlugin().getUserManager()
				.getConnection(authCode);
		if (con == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		UUID conversationId = con.getUniqueId();
		Optional<List<UUID>> participantIds = Skype.getPlugin()
				.getConversationManager().getParticipants(conversationId);
		List<String> participantIdsWithinDateRange = new ArrayList<>();
		if (participantIds.isPresent()) {
			for (UUID participantId : participantIds.get()) {
				List<String> messages = Skype.getPlugin()
						.getConversationManager()
						.getMessages(conversationId, participantId, from, to);
				messages.addAll(Skype.getPlugin().getConversationManager()
						.getMessages(participantId, conversationId, from, to));
				if (messages.size() > 0) {
					participantIdsWithinDateRange.add(participantId.toString());
				}
			}
		}
		if (participantIdsWithinDateRange.size() == 0) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, GsonBuilder.create().toJson(
						participantIdsWithinDateRange));
		return replyPacket;
	}

}
