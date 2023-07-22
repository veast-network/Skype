package codes.elisa32.Skype;

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
		File dataFolder = new File(System.getenv("APPDATA"), "Skype");
		if (!dataFolder.exists()) {
			File file = new File("Updater.exe");
			if (file.exists()) {
				ProcessBuilder pb = new ProcessBuilder("Updater.exe");
				try {
					pb.start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				try {
					Desktop.getDesktop()
							.browse(new URI(
									"https://raw.skypeusercontent.com/elisa322008/elisa322008.github.io/main/skype/Skype.msi"));
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
			System.exit(-1);
		}
		File jarFile = new File(dataFolder, "Skype.jar");
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
