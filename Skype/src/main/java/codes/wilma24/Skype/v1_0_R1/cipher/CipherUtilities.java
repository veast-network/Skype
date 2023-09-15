package codes.wilma24.Skype.v1_0_R1.cipher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base32;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class CipherUtilities {

	private static Cipher createCipher(String password, int mode)
			throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, InvalidKeyException,
			UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		byte[] bytes = md
				.digest(password.getBytes(StandardCharsets.ISO_8859_1));
		Base32 base32 = new Base32();
		byte[] iv = Arrays.copyOf(
				base32.encodeToString(bytes).getBytes("ISO-8859-1"), 16);
		SecretKeySpec key = new SecretKeySpec(iv, "AES");
		IvParameterSpec paramSpec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(mode, key, paramSpec);
		return cipher;
	}

	public static Cipher createEncryptionCipher(byte[] iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, InvalidKeyException {
		SecretKeySpec key = new SecretKeySpec(iv, "AES");
		IvParameterSpec paramSpec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
		return cipher;
	}

	public static Cipher createDecryptionCipher(byte[] iv)
			throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, InvalidKeyException {
		SecretKeySpec key = new SecretKeySpec(iv, "AES");
		IvParameterSpec paramSpec = new IvParameterSpec(iv);
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
		return cipher;
	}

	private static Cipher createEncryptionCipher(String password)
			throws NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, InvalidKeyException,
			UnsupportedEncodingException {
		return createCipher(password, Cipher.ENCRYPT_MODE);
	}

	public static byte[] decodeBase64(String encodedStr) {
		return java.util.Base64.getDecoder().decode(encodedStr);
	}

	public static String encodeBase64(byte[] decodedByteArr) {
		return java.util.Base64.getEncoder().encodeToString(decodedByteArr);
	}

	public static byte[] randomCipher() throws InvalidKeyException,
			NoSuchPaddingException, NoSuchAlgorithmException,
			InvalidAlgorithmParameterException, UnsupportedEncodingException {
		javax.crypto.Cipher cipher = createEncryptionCipher(UUID.randomUUID()
				.toString());
		return new String(cipher.getIV(), "ISO-8859-1").getBytes("ISO-8859-1");
	}

	public static byte[] encryptData(byte[] cipher, byte[] data)
			throws IOException, InvalidKeyException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidAlgorithmParameterException {
		javax.crypto.Cipher arg0 = CipherUtilities
				.createEncryptionCipher(cipher);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		javax.crypto.CipherOutputStream cipherOutputStream = new javax.crypto.CipherOutputStream(
				baos, arg0);
		cipherOutputStream.write(data, 0, data.length);
		cipherOutputStream.flush();
		cipherOutputStream.close();
		return baos.toByteArray();
	}

	public static byte[] decryptData(byte[] cipher, byte[] data)
			throws InvalidKeyException, NoSuchPaddingException,
			NoSuchAlgorithmException, InvalidAlgorithmParameterException,
			IOException {
		Cipher arg0 = CipherUtilities.createDecryptionCipher(cipher);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		CipherOutputStream cipherOutputStream = new CipherOutputStream(baos,
				arg0);
		cipherOutputStream.write(data, 0, data.length);
		cipherOutputStream.flush();
		cipherOutputStream.close();
		return baos.toByteArray();
	}

}
