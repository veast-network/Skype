package codes.wilma24.Skype.v1_0_R1.uicommon;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import codes.wilma24.Skype.v1_0_R1.fontio.FontIO;

public class CopyOfJButtonRounded extends JPanel {

	private JButtonRounded addToContactsButton;

	public CopyOfJButtonRounded(Dimension size, String label, int radius) {
		super();
		int buttonWidth = (int) size.getWidth();
		int buttonHeight = (int) size.getHeight();

		addToContactsButton = new JButtonRounded(radius);

		addToContactsButton.setBackground(getBackground());
		addToContactsButton.setForeground(getForeground());

		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setBounds(0, 0, buttonWidth, buttonHeight);
		setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		setOpaque(false);

		/**
		 * Defaults
		 */
		setBackground(new Color(0, 175, 240));
		setForeground(Color.white);
		setFont(FontIO.TAHOMA_BOLD.deriveFont(Font.BOLD, 11));

		JLayeredPane addToContactsButtonPanel = new JLayeredPane();
		addToContactsButtonPanel.setOpaque(false);
		addToContactsButtonPanel.setBounds(0, 0, buttonWidth, buttonHeight);
		addToContactsButtonPanel.setPreferredSize(new Dimension(buttonWidth,
				buttonHeight));

		addToContactsButton.setFont(getFont());
		addToContactsButton.setBounds(0, 0, buttonWidth, buttonHeight);
		addToContactsButton.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));
		addToContactsButton.setLabel(label);

		addToContactsButtonPanel.add(addToContactsButton, new Integer(0), 0);

		add(addToContactsButtonPanel);
	}

	public void addActionListener(ActionListener l) {
		addToContactsButton.addActionListener(l);
	}

	public void removeActionListener(ActionListener l) {
		addToContactsButton.removeActionListener(l);
	}

	public void setForeground(Color arg0) {
		if (addToContactsButton == null) {
			return;
		}
		addToContactsButton.setForeground(arg0);
	}

	public void setBackground(Color arg0) {
		if (addToContactsButton == null) {
			return;
		}
		addToContactsButton.setBackground(arg0);
	}

}
