package codes.elisa32.Skype.server.v1_0_R1.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInGroupChatParticipantsChanged;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutUpdateGroupChatParticipants;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

import com.google.gson.Gson;

public class UpdateGroupChatParticipantsCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutUpdateGroupChatParticipants packet = Packet.fromJson(
				msg.toString(), PacketPlayOutUpdateGroupChatParticipants.class);
		UUID authCode = packet.getAuthCode();
		Connection con = Skype.getPlugin().getUserManager()
				.getConnection(authCode);
		if (con == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		String json = packet.getPayload().toString();
		UUID conversationId = con.getUniqueId();
		Gson gson = GsonBuilder.create();
		List<String> participants = gson.fromJson(json, List.class);
		if (!Skype.getPlugin().getUserManager().isGroupChat(conversationId)) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		List<UUID> participantIds = new ArrayList<>();
		for (String skypeName : participants) {
			participantIds.add(Skype.getPlugin().getUserManager()
					.getUniqueId(skypeName));
		}
		List<UUID> participantsToBeRemoved = new ArrayList<>();
		Optional<List<UUID>> participantIdsLookup = Skype.getPlugin()
				.getConversationManager().getParticipants(conversationId);
		if (participantIdsLookup.isPresent()) {
			List<UUID> temp = new ArrayList<>();
			temp.addAll(participantIdsLookup.get());
			for (UUID participantId : temp.toArray(new UUID[0]).clone()) {
				PacketPlayInGroupChatParticipantsChanged callParticipantsChangedPacket = new PacketPlayInGroupChatParticipantsChanged(
						conversationId, packet.getPayload());
				for (Connection listeningParticipant : Skype.getPlugin()
						.getUserManager()
						.getListeningConnections(participantId)) {
					Thread thread = new Thread(
							() -> {
								listeningParticipant
										.getSocketHandlerContext()
										.getOutboundHandler()
										.dispatch(
												listeningParticipant
														.getSocketHandlerContext(),
												callParticipantsChangedPacket);
							});
					thread.start();
				}
			}
			for (UUID participantId : participantIds.toArray(new UUID[0])
					.clone()) {
				for (UUID participantId2 : temp.toArray(new UUID[0]).clone()) {
					if (participantId2.equals(participantId)) {
						temp.remove(participantId);
					}
				}
			}
			participantsToBeRemoved = temp;
		}
		if (!Skype
				.getPlugin()
				.getConversationManager()
				.setParticipants(conversationId,
						participantIds.toArray(new UUID[0]))) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		for (UUID participantId : participantIds) {
			Skype.getPlugin().getConversationManager()
					.addParticipants(participantId, conversationId);
		}
		for (UUID participantId : participantsToBeRemoved) {
			Skype.getPlugin().getConversationManager()
					.removeParticipants(participantId, conversationId);
		}
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}

}
