package codes.wilma24.Skype.v1_0_R1.forms;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.api.v1_0_R1.voip.VoIP;
import codes.wilma24.Skype.v1_0_R1.AppDelegate;
import codes.wilma24.Skype.v1_0_R1.audioio.AudioIO;
import codes.wilma24.Skype.v1_0_R1.data.types.Conversation;
import codes.wilma24.Skype.v1_0_R1.data.types.Message;
import codes.wilma24.Skype.v1_0_R1.data.types.VoIPContact;
import codes.wilma24.Skype.v1_0_R1.imageio.ImageIO;
import codes.wilma24.Skype.v1_0_R1.plugin.Skype;

public class VoIPRegisterForm extends JDialog {

	private JButton cancelButton = new JButton("Cancel");

	private JTextField fullNameTextField = new JTextField();

	private JTextField createSkypeNameTextField = new JTextField(
			"2-40 characters needed");

	private JPasswordField passwordTextField = new JPasswordField(
			"2-40 characters needed");

	private JLabel passwordOkLabel = new JLabel("Password OK");

	private JPasswordField repeatPasswordTextField = new JPasswordField();

	private JLabel passwordsMatchLabel = new JLabel("Passwords match");

	private JCheckBox termsAndConditionsCheckBox = new JCheckBox();

	public VoIPRegisterForm(JFrame parent, Runnable callback) {
		super(parent, "Skype\u2122 - Register SIP Account", true);

		int panelWidth = 510;
		int panelHeight = 389;

		JDialog dialog = this;

		Font textFieldFont = new Font("Seriff", Font.TRUETYPE_FONT, 13);

		/**
		 * Construct layered pane to float components in z order
		 */
		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, panelWidth, panelHeight);
		setContentPane(layeredPane);

		/**
		 * Construct main panel for all components to reside inside
		 */
		JPanel panel = new JPanel();
		panel.setBackground(Color.white);
		panel.setBounds(0, 0, panelWidth, panelHeight);
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
		headerPanel.setBounds(0, 0, panelWidth, 44);
		headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
				new Color(228, 236, 239)));
		layeredPane.add(headerPanel, new Integer(1), 0);

		/**
		 * Construct title panel featuring the title for the dialog
		 */
		JPanel title = new JPanel();
		title.setLayout(new FlowLayout(FlowLayout.LEFT));
		title.setBounds(64, 8, panelWidth - 64, 40);
		title.setOpaque(false);
		JLabel titleLabel = new JLabel("Register a SIP Account.");
		titleLabel.setFont(textFieldFont.deriveFont(Font.BOLD,
				textFieldFont.getSize()));
		title.add(titleLabel);
		layeredPane.add(title, new Integer(2), 0);

		/**
		 * Add icon floating in window at x 11 y 6 width 48 height 71
		 */
		JPanel iconLabelPanel = new JPanel();
		ImageIcon imageIcon = ImageIO.getResourceAsImageIcon("/1507329840.png");
		JLabel iconLabel = new JLabel(imageIcon);
		iconLabelPanel.setBounds(11, 6, 48, 71);
		iconLabelPanel.setOpaque(false);
		iconLabelPanel.add(iconLabel);
		layeredPane.add(iconLabelPanel, new Integer(3), 0);

		/* End */

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Already have a Skype account?");

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(83, 65, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Sign in");
			label.setForeground(new Color(0, 149, 204));

			/**
			 * Reserved for future use
			 */
			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(237, 65, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("*");
			label.setForeground(Color.red);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(73, 90, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("SIP Server");

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(83, 90, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		/**
		 * Full name text field
		 */
		{
			JPanel textFieldPanel = new JPanel();
			textFieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			textFieldPanel.setOpaque(false);

			fullNameTextField.setPreferredSize(new Dimension(249, 20));

			fullNameTextField.setText("talk.yay.com");

			textFieldPanel.add(fullNameTextField);

			int width = fullNameTextField.getPreferredSize().width;
			int height = fullNameTextField.getPreferredSize().height;

			textFieldPanel.setBounds(205, 86, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(textFieldPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("*");
			label.setForeground(Color.red);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(73, 122, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("SIP User ID");

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(83, 122, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		/**
		 * Create skype name text field
		 */
		{
			JPanel textFieldPanel = new JPanel();
			textFieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			textFieldPanel.setOpaque(false);

			createSkypeNameTextField.setPreferredSize(new Dimension(249, 20));

			// Placeholder
			createSkypeNameTextField.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent arg0) {
					if (createSkypeNameTextField.getText().equals(
							"2-40 characters needed")) {
						createSkypeNameTextField.setText("");
					}
					createSkypeNameTextField.selectAll();
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					if (createSkypeNameTextField.getText().equals("")) {
						createSkypeNameTextField
								.setText("2-40 characters needed");
					}
				}

			});

			textFieldPanel.add(createSkypeNameTextField);

			int width = createSkypeNameTextField.getPreferredSize().width;
			int height = createSkypeNameTextField.getPreferredSize().height;

			textFieldPanel.setBounds(205, 118, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(textFieldPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("*");
			label.setForeground(Color.red);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(73, 172, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Password");

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(83, 172, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		/**
		 * Password text field
		 */
		{
			JPanel textFieldPanel = new JPanel();
			textFieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			textFieldPanel.setOpaque(false);

			passwordTextField.setPreferredSize(new Dimension(249, 20));

			final char echo = passwordTextField.getEchoChar();

			passwordTextField.getDocument().addDocumentListener(
					new DocumentListener() {

						@Override
						public void changedUpdate(DocumentEvent e) {
							valueChanged();
						}

						@Override
						public void removeUpdate(DocumentEvent e) {
							valueChanged();
						}

						@Override
						public void insertUpdate(DocumentEvent e) {
							valueChanged();
						}

						public void valueChanged() {
							int len = passwordTextField.getText().length();
							if (len < 2 || len > 40) {
								passwordOkLabel.setVisible(false);
							} else {
								passwordOkLabel.setVisible(true);
							}
							if (repeatPasswordTextField.getText().equals(
									passwordTextField.getText())
									&& passwordTextField.getText().length() > 0) {
								passwordsMatchLabel.setVisible(true);
							} else {
								passwordsMatchLabel.setVisible(false);
							}
						}
					});

			// Placeholder
			passwordTextField.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent arg0) {
					if (passwordTextField.getText().equals(
							"2-40 characters needed")) {
						passwordTextField.setText("");
						passwordTextField.setEchoChar(echo);
					}
					passwordTextField.selectAll();
				}

				@Override
				public void focusLost(FocusEvent arg0) {
					if (passwordTextField.getText().equals("")) {
						passwordTextField.setText("2-40 characters needed");
						passwordTextField.setEchoChar((char) 0);
					}
				}

			});

			passwordTextField.setEchoChar((char) 0);

			textFieldPanel.add(passwordTextField);

			int width = passwordTextField.getPreferredSize().width;
			int height = passwordTextField.getPreferredSize().height;

			textFieldPanel.setBounds(205, 168, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(textFieldPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			passwordOkLabel.setVisible(false);

			int width = passwordOkLabel.getPreferredSize().width;
			int height = passwordOkLabel.getPreferredSize().height;

			labelPanel.add(passwordOkLabel);

			labelPanel.setBounds(205, 191, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("*");
			label.setForeground(Color.red);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(73, 224, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Repeat password");

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(83, 224, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		/**
		 * Repeat password text field
		 */
		{
			JPanel textFieldPanel = new JPanel();
			textFieldPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			textFieldPanel.setOpaque(false);

			repeatPasswordTextField.setPreferredSize(new Dimension(249, 20));

			repeatPasswordTextField.getDocument().addDocumentListener(
					new DocumentListener() {

						@Override
						public void changedUpdate(DocumentEvent e) {
							valueChanged();
						}

						@Override
						public void removeUpdate(DocumentEvent e) {
							valueChanged();
						}

						@Override
						public void insertUpdate(DocumentEvent e) {
							valueChanged();
						}

						public void valueChanged() {
							if (repeatPasswordTextField.getText().equals(
									passwordTextField.getText())
									&& passwordTextField.getText().length() > 0) {
								passwordsMatchLabel.setVisible(true);
							} else {
								passwordsMatchLabel.setVisible(false);
							}
						}
					});

			// Placeholder
			repeatPasswordTextField.addFocusListener(new FocusListener() {

				@Override
				public void focusGained(FocusEvent arg0) {
					repeatPasswordTextField.selectAll();
				}

				@Override
				public void focusLost(FocusEvent arg0) {
				}

			});

			textFieldPanel.add(repeatPasswordTextField);

			int width = repeatPasswordTextField.getPreferredSize().width;
			int height = repeatPasswordTextField.getPreferredSize().height;

			textFieldPanel.setBounds(205, 220, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(textFieldPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			passwordsMatchLabel.setVisible(false);

			int width = passwordsMatchLabel.getPreferredSize().width;
			int height = passwordsMatchLabel.getPreferredSize().height;

			labelPanel.add(passwordsMatchLabel);

			labelPanel.setBounds(205, 243, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("*");
			label.setForeground(Color.red);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(73, 271, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel checkBoxPanel = new JPanel();
			checkBoxPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			checkBoxPanel.setOpaque(false);

			termsAndConditionsCheckBox.setOpaque(false);

			int width = termsAndConditionsCheckBox.getPreferredSize().width;
			int height = termsAndConditionsCheckBox.getPreferredSize().height;

			checkBoxPanel.add(termsAndConditionsCheckBox);

			checkBoxPanel.setBounds(79, 268, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(checkBoxPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Yes, I have read and I accept the ");

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(101, 268, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Skype End User License Agreement");
			label.setForeground(new Color(0, 149, 204));

			/**
			 * Reserved for future use
			 */
			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(266, 268, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel(", the");

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(435, 268, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Skype Terms of Service");
			label.setForeground(new Color(0, 149, 204));

			/**
			 * Reserved for future use
			 */
			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(101, 284, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("and the");

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(216, 284, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Skype Privacy Statement");
			label.setForeground(new Color(0, 149, 204));

			/**
			 * Reserved for future use
			 */
			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(256, 284, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			Color color = new Color(134, 134, 134);
			/* labelPanel.setBackground(color); */
			labelPanel.setBackground(Color.black);
			labelPanel.setBounds(73, 314, panelWidth - 138, 1);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("*");
			label.setForeground(Color.red);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(73, 319, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel(
					"Fields marked with an asterisk are required.");
			Color color = new Color(134, 134, 134);
			/* label.setForeground(color); */
			label.setForeground(Color.black);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(86, 319, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Get help");
			label.setForeground(new Color(0, 149, 204));

			/**
			 * Reserved for future use
			 */
			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(408, 319, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		/* Start */
		/**
		 * Construct footer
		 */

		/**
		 * Construct footer panel featuring a light blue background
		 */
		JPanel footerPanel = new JPanel();
		footerPanel.setBackground(new Color(245, 252, 254));
		footerPanel.setBounds(0, panelHeight - 44, panelWidth, 44);
		footerPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
				new Color(228, 236, 239)));
		layeredPane.add(footerPanel, new Integer(1), 0);

		/**
		 * Construct primary action button
		 */
		JPanel primaryButtonPanel = new JPanel();
		primaryButtonPanel.setBounds(510 - 177, panelHeight - 39, 78, 39);
		primaryButtonPanel.setOpaque(false);
		JButton primaryButton = new JButton("Next \u00BB");
		primaryButton.setPreferredSize(new Dimension(78, 23));
		primaryButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (createSkypeNameTextField.getText().equals(
						"2-40 characters needed")) {
					createSkypeNameTextField.grabFocus();
					Toolkit.getDefaultToolkit().beep();
					return;
				} else {
					int len = createSkypeNameTextField.getText().length();
					if (len < 2 || len > 40) {
						createSkypeNameTextField.grabFocus();
						Toolkit.getDefaultToolkit().beep();
						return;
					}
				}
				if (!createSkypeNameTextField.getText().replace(".", "")
						.replace(":", "").replace("_", "").replace("-", "")
						.replace(",", "").chars()
						.allMatch(Character::isLetterOrDigit)) {
					createSkypeNameTextField.grabFocus();
					Toolkit.getDefaultToolkit().beep();
					return;
				}
				if (passwordTextField.getText()
						.equals("2-40 characters needed")) {
					passwordTextField.grabFocus();
					Toolkit.getDefaultToolkit().beep();
					return;
				}
				if (passwordTextField.getText().equals(
						repeatPasswordTextField.getText())) {
					int len = passwordTextField.getText().length();
					if (len < 2 || len > 40) {
						passwordTextField.grabFocus();
						Toolkit.getDefaultToolkit().beep();
						return;
					}
				} else {
					repeatPasswordTextField.grabFocus();
					Toolkit.getDefaultToolkit().beep();
					return;
				}
				if (termsAndConditionsCheckBox.isSelected()) {
				} else {
					termsAndConditionsCheckBox.grabFocus();
					Toolkit.getDefaultToolkit().beep();
					return;
				}
				if (fullNameTextField.getText().length() == 0) {
					fullNameTextField.setText(createSkypeNameTextField
							.getText());
				}
				callback.sipserver = fullNameTextField.getText();
				callback.username = createSkypeNameTextField.getText();
				callback.password = passwordTextField.getText();
				primaryButton.setEnabled(false);
				VoIP.Runnable incomingCallCallback = new VoIP.Runnable() {

					@Override
					public void run() {
						String peer = this.peer;
						int line = this.line;
						VoIPContact testvoip = new VoIPContact();
						testvoip.setSkypeName(peer);
						testvoip.setUniqueId(Skype.getPlugin().getUniqueId(
								testvoip.getSkypeName()));
						testvoip.setDisplayName(testvoip.getSkypeName());
						testvoip.setLastModified(new Date(new Date().getTime()
								+ AppDelegate.TIME_OFFSET));
						boolean hit = false;
						for (Conversation conversation : MainForm.get().conversations) {
							if (conversation.getUniqueId().equals(
									testvoip.getUniqueId())) {
								testvoip = (VoIPContact) conversation;
								hit = true;
								break;
							}
						}
						if (!hit) {
							MainForm.get().conversations.add(testvoip);
							MainForm.get().saveVoipContactList();
						}
						IncomingVoIPCallForm form = new IncomingVoIPCallForm(
								testvoip, true, line);
						form.show();
					}
				};
				VoIP.Runnable incomingMessageCallback = new VoIP.Runnable() {

					@Override
					public void run() {
						String peer = this.peer;
						String msg = this.msg;
						VoIPContact testvoip = new VoIPContact();
						testvoip.setSkypeName(peer);
						testvoip.setUniqueId(Skype.getPlugin().getUniqueId(
								testvoip.getSkypeName()));
						testvoip.setDisplayName(testvoip.getSkypeName());
						testvoip.setLastModified(new Date(new Date().getTime()
								+ AppDelegate.TIME_OFFSET));
						boolean hit = false;
						for (Conversation conversation : MainForm.get().conversations) {
							if (conversation.getUniqueId().equals(
									testvoip.getUniqueId())) {
								testvoip = (VoIPContact) conversation;
								hit = true;
								break;
							}
						}
						if (!hit) {
							MainForm.get().conversations.add(testvoip);
							MainForm.get().saveVoipContactList();
						}
						UUID messageId = UUID.randomUUID();
						testvoip.setNotificationCount(testvoip
								.getNotificationCount() + 1);
						Message message = new Message(messageId, testvoip
								.getUniqueId(), msg,
								System.currentTimeMillis(), testvoip);
						testvoip.getMessages().add(message);
						NotificationForm notif = new NotificationForm(testvoip,
								message, true);
						notif.show();
						MainForm.get().refreshWindow();
					}
				};
				if (VoIP.getPlugin().API_Start(callback.sipserver,
						callback.username, callback.password,
						incomingCallCallback, incomingMessageCallback,
						new VoIP.Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								dialog.dispatchEvent(new WindowEvent(dialog,
										WindowEvent.WINDOW_CLOSING));
								callback.run();
							}
						}, new VoIP.Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toolkit.getDefaultToolkit().beep();
								primaryButton.setEnabled(true);
							}
						})) {

				} else {
					Toolkit.getDefaultToolkit().beep();
					primaryButton.setEnabled(true);
				}
			}

		});
		primaryButtonPanel.add(primaryButton);
		layeredPane.add(primaryButtonPanel, new Integer(2), 0);

		/**
		 * Construct cancel button
		 */
		JPanel cancelButtonPanel = new JPanel();
		cancelButtonPanel.setBounds(panelWidth - 89, panelHeight - 39, 78, 39);
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
		getContentPane().setPreferredSize(
				new Dimension(panelWidth, panelHeight));
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

	public static abstract class Runnable implements java.lang.Runnable {

		public String sipserver, username, password;

		public String getSIPServer() {
			return sipserver;
		}

		public String getUsername() {
			return username;
		}

		public String getPassword() {
			return password;
		}

	}
}
