package codes.elisa32.Skype.server.v1_0_R1.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutUserSearch;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class UserSearchCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutUserSearch packet = Packet.fromJson(msg.toString(),
				PacketPlayOutUserSearch.class);
		UUID authCode = packet.getAuthCode();
		String input = packet.getInput();
		if (input == null || input.length() == 0) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		Connection con = Skype.getPlugin().getUserManager()
				.getConnection(authCode);
		if (con == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		List<String> skypeNames = new ArrayList<String>();
		for (String key : Skype.getPlugin().getConfig()
				.getConfigurationSection("registry").getKeys(false)) {
			UUID participantId = UUID.fromString(key);
			Optional<String> skypeName = Skype.getPlugin().getUserManager()
					.getSkypeName(participantId);
			if (skypeName.isPresent()) {
				if (skypeName.get().startsWith(input)) {
					if (!skypeNames.contains(skypeName.get())) {
						skypeNames.add(skypeName.get());
					}
				}
			}
		}
		if (skypeNames.size() == 0) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, GsonBuilder.create().toJson(skypeNames));
		return replyPacket;
	}

}
