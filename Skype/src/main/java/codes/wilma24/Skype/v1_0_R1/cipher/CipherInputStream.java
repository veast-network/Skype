package codes.wilma24.Skype.v1_0_R1.cipher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;

import org.bouncycastle.util.io.Streams;

public class CipherInputStream extends InputStream {

	private InputStream is;

	private byte[] cipher;

	private javax.crypto.CipherOutputStream cos;

	private ByteArrayOutputStream baos = new ByteArrayOutputStream();

	public CipherInputStream(InputStream is, byte[] cipher) {
		this.is = is;
		this.cipher = cipher;
	}

	@Override
	public int read(byte[] b, int offset, int len) throws IOException {
		try {
			byte[] data = new byte[b.length];
			int bytesRead = is.read(data, offset, len);
			if (bytesRead < 1) {
				return bytesRead;
			}
			Cipher arg0 = CipherUtilities.createDecryptionCipher(cipher);
			baos = new ByteArrayOutputStream();
			cos = new CipherOutputStream(baos, arg0);
			cos.write(data, 0, bytesRead);
			cos.flush();
			cos.close();
			ByteArrayInputStream bis = new ByteArrayInputStream(
					baos.toByteArray());
			bytesRead = Streams.readFully(bis, b);
			baos.reset();
			return bytesRead;
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public void close() throws IOException {
		baos.close();
		cos.close();
	}

	@Override
	public int read() throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
