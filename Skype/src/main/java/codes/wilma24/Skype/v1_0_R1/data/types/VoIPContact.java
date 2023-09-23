package codes.wilma24.Skype.v1_0_R1.data.types;

import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import codes.wilma24.Skype.api.v1_0_R1.voip.VoIP;
import codes.wilma24.Skype.v1_0_R1.imageio.ImageIO;

public class VoIPContact extends Conversation {

	private boolean contact = false;

	private boolean favorite = false;

	public void setContact(boolean val) {
		this.contact = val;
		Status onlineStatus = Status.NOT_A_CONTACT;
		if (contact) {
			onlineStatus = Status.OFFLINE;
			if (VoIP.getPlugin().isConnected()) {
				onlineStatus = Status.ONLINE;
			}
		}
		Map.Entry<JPanel, JLabel> entry = ImageIO.getConversationIconPanel(
				this.getImageIcon(), onlineStatus);
		onlineStatusPanel = entry.getKey();
		onlineStatusLabel = entry.getValue();
	}

	public boolean isContact() {
		return contact;
	}

	public void setFavorite(boolean val) {
		this.favorite = val;
	}

	public boolean isFavorite() {
		return favorite;
	}

	@Override
	public ImageIcon getImageIcon() {
		return ImageIO.getResourceAsImageIcon("/22744.png");
	}

}
