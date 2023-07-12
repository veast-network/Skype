package codes.elisa32.Skype.server.v1_0_R1.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptContactRequest;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

import com.google.gson.Gson;

public class LookupContactsCmd extends CommandExecutor {

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
		Optional<List<UUID>> contactuids = Skype.getPlugin().getUserManager()
				.getContacts(participantId);
		if (!contactuids.isPresent()) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		if (contactuids.get().size() == 0) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		List<String> contacts = new ArrayList<>();
		for (UUID uid : contactuids.get()) {
			contacts.add(uid.toString());
		}
		Gson gson = GsonBuilder.create();
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, gson.toJson(contacts));
		return replyPacket;
	}

}
