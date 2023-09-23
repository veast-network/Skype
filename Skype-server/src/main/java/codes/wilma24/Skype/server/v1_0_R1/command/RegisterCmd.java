package codes.wilma24.Skype.server.v1_0_R1.command;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.openpgp.PGPException;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInUserRegistryChanged;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutRegister;
import codes.wilma24.Skype.api.v1_0_R1.pgp.PGPUtilities;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.server.v1_0_R1.Skype;
import codes.wilma24.Skype.server.v1_0_R1.data.types.Connection;

public class RegisterCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutRegister packet = Packet.fromJson(msg.toString(),
				PacketPlayOutRegister.class);
		String fullName = packet.getFullName();
		String skypeName = packet.getSkypeName();
		if (ctx.getPubKey() == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		try {
			packet.setPassword(PGPUtilities.decryptAndVerify(
					packet.getPassword(),
					PGPUtilities.createOrLookupPrivateKey(), ctx.getPubKey())
					.getMessage());
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (PGPException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		if ((password.length() < 6 || password.length() > 20)
				&& !Skype.getPlugin().getTokenMap().containsKey(password)) {
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
		if (Skype.getPlugin().testRateLimitRegistration(
				ctx.getSocket().getInetAddress().getHostAddress())) {
			try {
				ctx.getSocket().close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return PacketPlayInReply.empty();
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
								.write(con.getSocketHandlerContext(),
										new PacketPlayInUserRegistryChanged());
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}

		if (packet.isGroupChat()) {
			if (packet.getGroupChatAdmin() != null) {
				UUID conversationId = Skype.getPlugin().getUserManager()
						.getUniqueId(skypeName);
				Skype.getPlugin()
						.getConversationManager()
						.setGroupChatAdmins(conversationId,
								packet.getGroupChatAdmin());
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
