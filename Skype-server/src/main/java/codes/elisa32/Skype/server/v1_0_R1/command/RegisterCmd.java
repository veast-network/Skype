package codes.elisa32.Skype.server.v1_0_R1.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInUserRegistryChanged;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutRegister;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class RegisterCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutRegister packet = Packet.fromJson(msg.toString(),
				PacketPlayOutRegister.class);
		String fullName = packet.getFullName();
		String skypeName = packet.getSkypeName();
		String password = packet.getPassword();
		boolean isGroupChat = packet.isGroupChat();
		if (fullName == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST,
					"Full name for user is required.");
			return replyPacket;
		}
		if (skypeName == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST,
					"Skype name for user is required.");
			return replyPacket;
		}
		if (password == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST,
					"Password for user is required.");
			return replyPacket;
		}
		if (skypeName.length() < 3 || skypeName.length() > 40) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST,
					"Skype name length is less then 3 or greater then 40.");
			return replyPacket;
		}
		if (password.length() < 6 || password.length() > 20) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST,
					"Password length is less then 6 or greater then 20.");
			return replyPacket;
		}
		if (skypeName != null
				&& !skypeName.replace(".", "").replace(":", "")
						.replace("_", "").replace("-", "").replace(",", "")
						.chars().allMatch(Character::isLetterOrDigit)) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST,
					"Skype name does not contain only alpha-numeric characters.");
			return replyPacket;
		}
		if (Skype.getPlugin().getUserManager().userExists(skypeName)) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST,
					"User is already registered.");
			return replyPacket;
		}
		if (packet.getProtocolVersion() != PacketPlayOutLogin.PROTOCOL_VERSION) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		if (!Skype.getPlugin().getUserManager()
				.createUser(ctx, fullName, skypeName, password, isGroupChat)) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}

		UUID authCode = UUID.randomUUID();

		/**
		 * We want to set the auth code to expire after 30 minutes
		 * 
		 * If they refresh the token before 30 minutes it does not expire
		 */
		long expiryTime = System.currentTimeMillis() + (30 * (60 * 1000L));

		if (packet.isSilent() == false) {

			List<String> skypeNames = new ArrayList<String>();
			for (String key : Skype.getPlugin().getConfig()
					.getConfigurationSection("registry").getKeys(false)) {
				UUID participantId = UUID.fromString(key);
				Optional<String> skypeName2 = Skype.getPlugin()
						.getUserManager().getSkypeName(participantId);
				if (skypeName2.isPresent()) {
					if (!skypeNames.contains(skypeName2.get())) {
						skypeNames.add(skypeName2.get());
					}
				}
			}
			Object payload = GsonBuilder.create().toJson(skypeNames);

			for (UUID authCode2 : Skype.getPlugin().getConnectionMap().keySet()
					.toArray(new UUID[0]).clone()) {
				Connection con = Skype.getPlugin().getUserManager()
						.getConnection(authCode2);
				if (con == null) {
					continue;
				}
				if (con.isListening() && !con.isInCall()
						&& !con.isCallDataStream()) {
					try {
						con.getSocketHandlerContext()
								.getOutboundHandler()
								.dispatchAsync(
										con.getSocketHandlerContext(),
										new PacketPlayInUserRegistryChanged(
												payload), null);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}

		}

		/**
		 * Store the connection in memory for reference
		 */
		Connection con = new Connection(authCode, expiryTime, skypeName, ctx);
		Skype.getPlugin().getConnectionMap().put(authCode, con);

		/**
		 * Construct reply packet with the text being the auth code
		 */
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, authCode.getUUID().toString());
		return replyPacket;
	}

}
