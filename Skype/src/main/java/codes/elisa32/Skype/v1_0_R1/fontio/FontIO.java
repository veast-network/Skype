package codes.elisa32.Skype.v1_0_R1.fontio;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

public class FontIO {

	public static final Font SEGOE_UI_SEMILIGHT = getResourceAsFont(
			"/Segoe UI Semilight.ttf", 12);

	public static final Font SEGOE_UI_SEMIBOLD = getResourceAsFont(
			"/Segoe UI Semibold.ttf", 12);

	public static final Font SEGOE_UI_BOLD = getResourceAsFont(
			"/Segoe UI Bold.ttf", 12);

	public static final Font SEGOE_UI = getResourceAsFont("/Segoe UI.ttf", 12);

	public static final Font TAHOMA = getResourceAsFont("/Tahoma.ttf", 11);
	
	public static final Font TAHOMA_BOLD = getResourceAsFont("/Tahoma Bold.ttf", 11);

	private static Font getResourceAsFont(String resource, int fontSize) {
		if (resource.startsWith("/")) {
			resource = resource.substring(1);
		}
		try {
			InputStream is = FontIO.class.getResource("/font/" + resource)
					.openStream();
			return Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(
					Font.TRUETYPE_FONT, fontSize);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (FontFormatException e) {
			e.printStackTrace();
		}
		return ((FontUIResource) UIManager.get("Label.font")).deriveFont(
				Font.TRUETYPE_FONT, fontSize);
	}
}
