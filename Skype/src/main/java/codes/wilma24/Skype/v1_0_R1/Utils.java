package codes.wilma24.Skype.v1_0_R1;

import java.awt.FontMetrics;

public class Utils {

	public static String concatStringEllipses(FontMetrics fm, int desiredWidth,
			String text) {
		String ellipses = "...";
		String textToBeDisplayed = "";

		for (int i = 0; i <= text.length() - 1; i++) {
			if (fm.stringWidth(textToBeDisplayed + ellipses) <= desiredWidth) {
				textToBeDisplayed = textToBeDisplayed + text.charAt(i);
			}
		}

		String finalText;
		if (textToBeDisplayed.equals(text)) {
			finalText = text;
		} else {
			finalText = textToBeDisplayed.concat(ellipses);
		}

		return finalText;
	}

}
