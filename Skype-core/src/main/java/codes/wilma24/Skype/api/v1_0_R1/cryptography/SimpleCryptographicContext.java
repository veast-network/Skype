package codes.wilma24.Skype.api.v1_0_R1.cryptography;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Base32;

public class SimpleCryptographicContext extends CryptographicContext {

	@Override
	public String encodeToString(byte[] input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] bytes = md.digest(input);
			Base32 base32 = new Base32();
			return base32.encodeToString(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return new String(input, StandardCharsets.UTF_8);
		}
	}

}
