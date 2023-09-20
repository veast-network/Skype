package codes.wilma24.Skype.v1_0_R1;

import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import codes.wilma24.Skype.v1_0_R1.fontio.FontIO;
import codes.wilma24.Skype.v1_0_R1.forms.LoginForm;

public class AppDelegate {

	public static final long VERSION = 3424;

	public static long TIME_OFFSET = 0L;

	public static void main(String[] args) {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		try {
			String TIME_SERVER = "time-a.nist.gov";
			NTPUDPClient timeClient = new NTPUDPClient();
			InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
			TimeInfo timeInfo = timeClient.getTime(inetAddress);
			long returnTime = timeInfo.getMessage().getTransmitTimeStamp()
					.getTime();
			TIME_OFFSET = returnTime - System.currentTimeMillis();
		} catch (Exception e) {
			e.printStackTrace();
		}
		UIManager.put("OptionPane.buttonFont", new FontUIResource(
				FontIO.SEGOE_UI.deriveFont(Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"List.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"TableHeader.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"Panel.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"ToggleButton.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"ComboBox.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"ScrollPane.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"Spinner.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put("RadioButtonMenuItem.font", new FontUIResource(
				FontIO.SEGOE_UI.deriveFont(Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"Slider.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"EditorPane.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"OptionPane.font",
				new FontUIResource(FontIO.SEGOE_UI.deriveFont(
						Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"ToolBar.font",
				new FontUIResource(FontIO.SEGOE_UI.deriveFont(
						Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"Tree.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put("CheckBoxMenuItem.font", new FontUIResource(
				FontIO.SEGOE_UI.deriveFont(Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"TitledBorder.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put("FileChooser.listFont", new FontUIResource(
				FontIO.SEGOE_UI.deriveFont(Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"Table.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"MenuBar.font",
				new FontUIResource(FontIO.SEGOE_UI.deriveFont(
						Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"PopupMenu.font",
				new FontUIResource(FontIO.SEGOE_UI.deriveFont(
						Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"Label.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"MenuItem.font",
				new FontUIResource(FontIO.SEGOE_UI.deriveFont(
						Font.TRUETYPE_FONT, 12)));
		UIManager.put("MenuItem.acceleratorFont", new FontUIResource(
				FontIO.SEGOE_UI.deriveFont(Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"TextField.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"TextPane.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"CheckBox.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"ProgressBar.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put("FormattedTextField.font", new FontUIResource(
				FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT, 11)));
		UIManager.put(
				"Menu.font",
				new FontUIResource(FontIO.SEGOE_UI.deriveFont(
						Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"PasswordField.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put("InternalFrame.titleFont", new FontUIResource(
				FontIO.SEGOE_UI.deriveFont(Font.TRUETYPE_FONT, 12)));
		UIManager.put("OptionPane.messageFont", new FontUIResource(
				FontIO.SEGOE_UI.deriveFont(Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"Viewport.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"TabbedPane.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"RadioButton.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));
		UIManager.put(
				"ToolTip.font",
				new FontUIResource(FontIO.SEGOE_UI.deriveFont(
						Font.TRUETYPE_FONT, 12)));
		UIManager.put(
				"Button.font",
				new FontUIResource(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
						11)));

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				// HealthMonitor.start();
				if (System.getProperty("os.name").startsWith("Windows")) {
					try {
						URL url = new URL(
								"https://raw.githubusercontent.com/wilma242008/wilma242008.github.io/main/skype/version_client.txt");
						URLConnection con = url.openConnection();
						con.setReadTimeout(4000);
						con.setRequestProperty("User-Agent",
								"Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:72.0) Gecko/20100101 Firefox/72.0");
						Scanner s = new Scanner(con.getInputStream());
						String nextLine = s.nextLine();
						File file = new File("Updater.exe");
						System.out.println(nextLine);
						System.out.println(file.getAbsolutePath());
						if (Long.parseLong(nextLine) > VERSION) {
							if (file.exists()) {
								ProcessBuilder pb = new ProcessBuilder(
										"Updater.exe");
								try {
									pb.start();
								} catch (IOException e) {
									e.printStackTrace();
								}
								s.close();
								System.exit(-1);
							}
						}
						s.close();
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}
				LoginForm loginForm = new LoginForm();
				loginForm.show();
			}
		});
	}
}
