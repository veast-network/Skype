package codes.wilma24.Skype.v1_0_R1.data.types;

import javax.swing.ImageIcon;

import codes.wilma24.Skype.v1_0_R1.imageio.ImageIO;

public class VoIPContact extends Conversation {

	@Override
	public ImageIcon getImageIcon() {
		return ImageIO.getResourceAsImageIcon("/22744.png");
	}

}
