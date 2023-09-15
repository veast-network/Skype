package codes.wilma24.Skype.v1_0_R1.cipher;

import static codes.wilma24.Skype.v1_0_R1.cipher.CipherUtilities.createEncryptionCipher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;

public class CipherOutputStream extends OutputStream {

	private OutputStream os;

	private byte[] cipher;

	private javax.crypto.CipherOutputStream cos;

	private ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public CipherOutputStream(OutputStream os, byte[] cipher) {
		this.os = os;
		this.cipher = cipher;
	}

	@Override
	public void write(byte[] b, int offset, int len) throws IOException {
		try {
			cos = new javax.crypto.CipherOutputStream(baos,
					createEncryptionCipher(this.cipher));
			cos.write(b, offset, len);
			cos.flush();
			cos.close();
			os.write(baos.toByteArray());
			os.flush();
			baos.reset();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	public void close() throws IOException {
		baos.close();
		cos.close();
	}

	@Override
	public void write(int arg0) throws IOException {
		// TODO Auto-generated method stub
	}

}
