package codes.wilma24.Skype;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class AppDelegate {

	public static String getJavaExecutable() {
		String exec = "java";
		try {
			exec = System.getProperty("java.home") + File.separator + "bin"
					+ File.separator + "java";
		} catch (Exception ignored) {
		}
		return exec;
	}

	public static void main(String... args) {
		File dataFolder = new File(System.getenv("APPDATA"), "Skype Technologies S.A.");
		dataFolder = new File(dataFolder, "Skype" + "\u2122" + " 7.11");
		File jarFile = new File(dataFolder, "Skype-1.2-SNAPSHOT.jar");
		RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
		List<String> jvmArgs = bean.getInputArguments();
		System.out.println(getJavaExecutable());
		System.out.println(jarFile.getPath());
		ProcessBuilder pb = new ProcessBuilder(new String[] {
				"cmd",
				"/c",
				"\"\"" + getJavaExecutable() + "\" "
						+ String.join(" ", jvmArgs) + " -jar \""
						+ jarFile.getPath() + "\"\"" });
		try {
			pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static final long VERSION = 1200;
	public static final String VERSION_IDENTIFIER = "1.2";

}
