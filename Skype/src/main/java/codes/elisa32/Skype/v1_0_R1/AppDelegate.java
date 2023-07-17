package codes.elisa32.Skype.v1_0_R1;

import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;

import org.bouncycastle.util.io.Streams;

import codes.elisa32.Skype.v1_0_R1.cipher.CipherInputStream;
import codes.elisa32.Skype.v1_0_R1.cipher.CipherOutputStream;
import codes.elisa32.Skype.v1_0_R1.cipher.CipherUtilities;
import codes.elisa32.Skype.v1_0_R1.fontio.FontIO;
import codes.elisa32.Skype.v1_0_R1.forms.LoginForm;

public class AppDelegate {

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

		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] cipher = CipherUtilities.randomCipher();
			CipherOutputStream cos = new CipherOutputStream(baos, cipher);
			byte[] b = "Hello World".getBytes();
			long before = System.currentTimeMillis();
			for (int i = 0; i < 100; i++) {
				cos.write(b, 0, b.length);
				byte[] enc = baos.toByteArray();
				ByteArrayInputStream bais = new ByteArrayInputStream(enc);
				CipherInputStream cis = new CipherInputStream(bais, cipher);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				Streams.pipeAll(cis, bos);
				System.out.println(new String(bos.toByteArray()));
				baos.reset();
			}
			cos.close();
			long elapsed = System.currentTimeMillis() - before;
			System.out.println(elapsed + "ms");
		} catch (Exception e) {
			e.printStackTrace();
		}

		LoginForm loginForm = new LoginForm();
		loginForm.show();
	}
}
