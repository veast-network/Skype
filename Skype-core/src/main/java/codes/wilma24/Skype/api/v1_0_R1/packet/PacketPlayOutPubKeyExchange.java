package codes.wilma24.Skype.api.v1_0_R1.packet;

import java.io.IOException;

import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.pgpainless.PGPainless;

public class PacketPlayOutPubKeyExchange extends Packet {

	private String pubKey;

	public PacketPlayOutPubKeyExchange(String pubKey) {
		super(PacketType.PUB_KEY_EXCHANGE);
		this.setPubKey(pubKey);
	}

	public PacketPlayOutPubKeyExchange(PGPPublicKeyRing pubKey) {
		super(PacketType.PUB_KEY_EXCHANGE);
		try {
			this.setPubKey(PGPainless.asciiArmor(pubKey));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getPubKey() {
		return pubKey;
	}

	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}

}
