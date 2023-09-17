package codes.wilma24.Skype.v1_0_R1.imgur;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Optional;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.util.io.Streams;

public class ImgurUploader {

	public Optional<String> uploadImg(byte[] imageData) {
		try {
			URL url = new URL("https://api.imgur.com/3/image");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Authorization", "Client-ID "
					+ "8e00266922a37ba");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			conn.connect();
			String data = URLEncoder.encode("image", "UTF-8")
					+ "="
					+ URLEncoder.encode(Base64.encodeBase64String(imageData)
							.toString(), "UTF-8");
			conn.getOutputStream().write(data.getBytes("UTF-8"));
			conn.getOutputStream().close();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Streams.pipeAll(conn.getInputStream(), baos);
			String arg0 = new String(baos.toByteArray());
			if (arg0.indexOf("\"link\":\"") != -1) {
				arg0 = arg0.replace("\\/", "/");
				arg0 = arg0.substring(arg0.indexOf("\"link\":\"")
						+ "\"link\":\"".length());
				arg0 = arg0.substring(0, arg0.indexOf("\""));
				return Optional.of(arg0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public Optional<String> uploadFile(File pathToFile) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Streams.pipeAll(new FileInputStream(pathToFile), baos);
			return uploadImg(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
