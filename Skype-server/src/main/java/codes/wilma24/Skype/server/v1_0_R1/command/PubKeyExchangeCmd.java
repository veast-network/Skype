package codes.wilma24.Skype.server.v1_0_R1.command;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.pgpainless.PGPainless;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutPubKeyExchange;
import codes.wilma24.Skype.api.v1_0_R1.pgp.PGPUtilities;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;

public class PubKeyExchangeCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutPubKeyExchange packet = Packet.fromJson(msg.toString(),
				PacketPlayOutPubKeyExchange.class);
		String pubKey = null;
		try {
			pubKey = PGPUtilities.createOrLookupPublicKey();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (PGPException e) {
			e.printStackTrace();
		}
		if (pubKey == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		String pubKeyStr = packet.getPubKey();
		try {
			PGPPublicKeyRing pubKey2 = PGPainless.readKeyRing().publicKeyRing(
					pubKeyStr);
			ctx.setPubKey(pubKey2);
		} catch (IOException e) {
			e.printStackTrace();
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, pubKey);
		return replyPacket;
	}

}
