package codes.elisa32.Skype.api.v1_0_R1.capture;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Optional;

import javax.imageio.ImageIO;

public class Capture {

	private static Robot robot;

	private static Rectangle screenRect;

	static {
		screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
	}

	public static Optional<byte[]> captureRegion(int x, int y, int width,
			int height) {
		try {
			if (robot == null) {
				robot = new Robot();
			}
			BufferedImage bi = robot.createScreenCapture(screenRect);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(bi, "jpg", baos);
			return Optional.of(baos.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}

}
