package codes.wilma24.Skype.v1_0_R1.forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import codes.wilma24.Skype.v1_0_R1.fontio.FontIO;
import codes.wilma24.Skype.v1_0_R1.imageio.ImageIO;
import codes.wilma24.Skype.v1_0_R1.uicommon.JVerticalLayout;

public class DialogForm extends JDialog {

	public JButton cancelButton = new JButton("Cancel");

	public DialogForm(JFrame parent, String windowTitle, String dialogTitle,
			String message, String primaryButtonText, Runnable callback) {
		super(parent, windowTitle, true);

		JDialog dialog = this;

		/**
		 * Construct layered pane to float components in z order
		 */
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, 400, 197);
		setContentPane(layeredPane);

		/**
		 * Construct main panel for all components to reside inside
		 */
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		panel.setBounds(0, 0, 400, 197);
		panel.setOpaque(true);
		layeredPane.add(panel, new Integer(0), 0);

		/* Start */
		/**
		 * Construct header
		 */

		/**
		 * Construct header panel featuring a light blue background
		 */
		JPanel headerPanel = new JPanel();
		headerPanel.setBackground(new Color(245, 252, 254));
		headerPanel.setBounds(0, 0, 400, 44);
		headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
				new Color(228, 236, 239)));
		layeredPane.add(headerPanel, new Integer(1), 0);

		/**
		 * Construct title panel featuring the title for the dialog
		 */
		JPanel title = new JPanel();
		title.setLayout(new FlowLayout(FlowLayout.LEFT));
		title.setBounds(65, 8, 336, 40);
		title.setOpaque(false);
		JLabel titleLabel = new JLabel(dialogTitle);
		titleLabel.setFont(FontIO.SEGOE_UI_BOLD.deriveFont(Font.TRUETYPE_FONT,
				13));
		title.add(titleLabel);
		layeredPane.add(title, new Integer(2), 0);

		/**
		 * Add icon floating in window at x 11 y 6 width 48 height 71
		 */
		JPanel iconLabelPanel = new JPanel();
		ImageIcon imageIcon = ImageIO.getResourceAsImageIcon("/1986692333.png");
		JLabel iconLabel = new JLabel(imageIcon);
		iconLabelPanel.setBounds(11, 6, 48, 71);
		iconLabelPanel.setOpaque(false);
		iconLabelPanel.add(iconLabel);
		layeredPane.add(iconLabelPanel, new Integer(3), 0);

		/* End */

		/**
		 * Construct message panel featuring the message for the dialog
		 */
		JPanel messagePanel = new JPanel();
		messagePanel.setLayout(new JVerticalLayout(0, JVerticalLayout.LEFT,
				JVerticalLayout.TOP));
		messagePanel.setBackground(Color.white);
		messagePanel.setBounds(68, 56, 332, 97);
		messagePanel.setOpaque(false);
		int messagePanelHeight = 0;
		for (String split : message.split("\\r?\\n")) {
			JPanel messagePanel2 = new JPanel();
			messagePanel2.setBackground(Color.white);
			messagePanel2.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			JLabel label = new JLabel(split);
			label.setFont(FontIO.SEGOE_UI.deriveFont(Font.TRUETYPE_FONT, 11));
			messagePanel2.add(label);
			messagePanelHeight += messagePanel2.getPreferredSize().height;
			messagePanel.add(messagePanel2);
		}
		messagePanel.setBounds(74, 61, 332, messagePanelHeight);
		layeredPane.add(messagePanel, new Integer(1), 0);

		/* Start */
		/**
		 * Construct footer
		 */

		/**
		 * Construct footer panel featuring a light blue background
		 */
		JPanel footerPanel = new JPanel();
		footerPanel.setBackground(new Color(245, 252, 254));
		footerPanel.setBounds(0, 153, 400, 44);
		footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
				new Color(228, 236, 239)));
		layeredPane.add(footerPanel, new Integer(1), 0);

		/**
		 * Construct primary action button
		 */
		if (primaryButtonText != null) {
			JPanel primaryButtonPanel = new JPanel();
			primaryButtonPanel.setBounds(223, 158, 78, 39);
			primaryButtonPanel.setOpaque(false);
			JButton primaryButton = new JButton(primaryButtonText);
			primaryButton.setPreferredSize(new Dimension(78, 23));
			primaryButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					dialog.dispatchEvent(new WindowEvent(dialog,
							WindowEvent.WINDOW_CLOSING));
					callback.run();
				}

			});
			primaryButtonPanel.add(primaryButton);
			layeredPane.add(primaryButtonPanel, new Integer(2), 0);
		}

		/**
		 * Construct cancel button
		 */
		JPanel cancelButtonPanel = new JPanel();
		cancelButtonPanel.setBounds(223 + 88, 158, 78, 39);
		cancelButtonPanel.setOpaque(false);
		cancelButton.setPreferredSize(new Dimension(78, 23));
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispatchEvent(new WindowEvent(dialog,
						WindowEvent.WINDOW_CLOSING));
			}

		});
		cancelButtonPanel.add(cancelButton);
		layeredPane.add(cancelButtonPanel, new Integer(2), 0);

		/* End */

		/**
		 * Note to self! setResizable() must come before pack()
		 * 
		 * If you do not do this before pack() then the size is wrong
		 */
		setResizable(false);
		getContentPane().setPreferredSize(new Dimension(400, 197));
		pack();
		setLocationRelativeTo(null);

		/**
		 * Construct icon for application using the images in 151845799.ico
		 */
		try {
			setIconImages(Imaging.getAllBufferedImages(
					ImageIO.getResourceAsStream("/151845799.ico"),
					"151845799.ico"));
		} catch (ImageReadException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void show() {
		cancelButton.grabFocus();
		super.show();
	}
}
