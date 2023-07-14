package codes.elisa32.Skype.v1_0_R1.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.TextAttribute;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;

import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLookupUser;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutRefreshToken;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutRegister;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutUpdateUser;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketType;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.data.types.Contact;
import codes.elisa32.Skype.v1_0_R1.imageio.ImageIO;
import codes.elisa32.Skype.v1_0_R1.pgp.PGPUtilities;
import codes.elisa32.Skype.v1_0_R1.plugin.Skype;
import codes.elisa32.Skype.v1_0_R1.uicommon.JButtonRounded;

public class LoginForm extends JFrame {

	/**
	 * UI Settings
	 */
	protected boolean retainDarkLoginFooterPanel = false;
	protected boolean simulatePageLoadingDelay = true;
	protected long pageLoadingDelayInMs = 100;
	protected boolean whiteBackgroundTextFieldOnFocus = true;
	protected boolean loginDetailsMismatchRedirectsToHomePage = false;

	/**
	 * UI
	 */
	ImageIcon skypeLogoImageIcon;

	KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

	Font textFieldFont = new Font("Seriff", Font.TRUETYPE_FONT, 13);

	Map<TextAttribute, Integer> underLineFontAttributes = new HashMap<TextAttribute, Integer>();

	/**
	 * UI Controls
	 */
	JPanel homePagePanel = new JPanel();
	JPanel homePageFooterPanel = new JPanel();

	JLabel createAccountLabel = new JLabel("Create an account");
	JPanel createAccountLabelPanel = new JPanel();

	JPanel loginPanel = new JPanel();
	JPanel loginFooterPanel = new JPanel();

	JPanel connectionSetupPanel = new JPanel();

	JLabel signInWithAnotherAccountLabel = new JLabel(
			"\u2190 Sign in with a different account");
	JPanel signInWithAnotherAccountLabelPanel = new JPanel();

	JMenuBar menuBar = new JMenuBar();

	JMenu skypeMenu = new JMenu("Skype");
	JMenuItem closeMenuItem = new JMenuItem("Close");

	JMenu toolsMenu = new JMenu("Tools");
	JMenuItem connectionOptionsMenuItem = new JMenuItem("Connection options...");

	JMenu helpMenu = new JMenu("Help");
	JMenuItem checkForUpdatesMenuItem = new JMenuItem("Check for Updates");
	JMenuItem aboutSkype = new JMenuItem("About Skype");

	JLabel signInLabel = new JLabel("Sign in");
	JPanel signInLabelPanel = new JPanel();

	JLabel loginDetailsMismatchLabel = new JLabel(
			"\u24D8 Sorry, we didn't recognize your sign-in details. Please check");
	JLabel loginDetailsMismatchLabel2 = new JLabel(
			"your Skype Name and password, then try again.");
	JPanel loginDetailsMismatchPanel = new JPanel();

	JLabel connectionDetailsMismatchLabel = new JLabel(
			"\u24D8 Sorry, we could not connect to this server. Please check");
	JLabel connectionDetailsMismatchLabel2 = new JLabel(
			"your Host Name and connection, then try again.");
	JPanel connectionDetailsMismatchPanel = new JPanel();

	JButtonRounded skypeNameButton = new JButtonRounded("Skype Name", 35);
	JPanel skypeNameButtonPanel = new JPanel();

	/**
	 * This used to read as "Learn more about Microsoft account" on Skype
	 * 
	 * A slight modification and it now says Skype instead of Microsoft
	 */
	JLabel learnMoreAboutSkypeAccountLabel = new JLabel(
			"Learn more about Skype account");
	JPanel learnMoreAboutSkypeAccountLabelPanel = new JPanel();

	JTextField hostNameField = new JTextField("Host Name");
	JPanel hostNamePanel = new JPanel();

	JTextField skypeNameField = new JTextField("Skype Name");
	JPanel skypeNamePanel = new JPanel();

	JPasswordField passwordField = new JPasswordField("Password");
	JPanel passwordPanel = new JPanel();

	JLabel cantAccessAccountLabel = new JLabel("Can't access your account?");
	JPanel cantAccessAccountLabelPanel = new JPanel();

	JLabel cantAccessAccountLabel2 = new JLabel("Can't access your account?");
	JPanel cantAccessAccountLabelPanel2 = new JPanel();

	JButtonRounded signInButton = new JButtonRounded("Sign in", 35);
	JPanel signInButtonPanel = new JPanel();

	JButtonRounded connectButton = new JButtonRounded("Connect", 35);
	JPanel connectButtonPanel = new JPanel();

	JFrame frame = this;

	WindowAdapter windowAdapter = new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			DialogForm form = new DialogForm(
					frame,
					"Quit Skype?",
					"Sure you want to quit Skype?",
					"You won't be able to send or receive instant messages and\ncalls if you do.",
					"Quit", new Runnable() {

						@Override
						public void run() {
							System.exit(-1);
						}

					});
			form.show();
		}
	};

	public LoginForm() {
		super("Skype");

		skypeLogoImageIcon = ImageIO.getResourceAsImageIcon("/561453571.png");

		underLineFontAttributes.put(TextAttribute.UNDERLINE,
				TextAttribute.UNDERLINE_ON);

		getContentPane().setBackground(new Color(0, 175, 240));

		homePagePanel.setBackground(new Color(0, 175, 240));
		homePagePanel.setLayout(new BoxLayout(homePagePanel, BoxLayout.Y_AXIS));

		loginPanel.setBackground(new Color(0, 175, 240));
		loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

		connectionSetupPanel.setBackground(new Color(0, 175, 240));
		connectionSetupPanel.setLayout(new BoxLayout(connectionSetupPanel,
				BoxLayout.Y_AXIS));

		closeMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(-1);
			}

		});

		skypeMenu.add(closeMenuItem);

		menuBar.add(skypeMenu);

		toolsMenu.add(connectionOptionsMenuItem);

		menuBar.add(toolsMenu);

		helpMenu.add(checkForUpdatesMenuItem);
		helpMenu.add(new JSeparator());

		aboutSkype.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					AboutSkypeForm aboutSkypeForm = new AboutSkypeForm(frame);
					aboutSkypeForm.show();
				} catch (FontFormatException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		});
		helpMenu.add(aboutSkype);

		menuBar.add(helpMenu);

		homePagePanel.add(Box.createRigidArea(new Dimension(10, 40)));

		loginPanel.add(Box.createRigidArea(new Dimension(10, 40)));

		connectionSetupPanel.add(Box.createRigidArea(new Dimension(10, 40)));

		{
			JLabel skypeLogoIconLabel = new JLabel(skypeLogoImageIcon);
			JPanel skypeLogoPanel = new JPanel();
			skypeLogoPanel.setLayout(new BoxLayout(skypeLogoPanel,
					BoxLayout.X_AXIS));
			skypeLogoPanel.add(skypeLogoIconLabel);
			homePagePanel.add(skypeLogoPanel);
		}

		homePagePanel.add(Box.createRigidArea(new Dimension(10, 33)));

		{
			JLabel skypeLogoIconLabel = new JLabel(skypeLogoImageIcon);
			JPanel skypeLogoPanel = new JPanel();
			skypeLogoPanel.setLayout(new BoxLayout(skypeLogoPanel,
					BoxLayout.X_AXIS));
			skypeLogoPanel.add(skypeLogoIconLabel);
			loginPanel.add(skypeLogoPanel);
		}

		{
			JLabel skypeLogoIconLabel = new JLabel(skypeLogoImageIcon);
			JPanel skypeLogoPanel = new JPanel();
			skypeLogoPanel.setLayout(new BoxLayout(skypeLogoPanel,
					BoxLayout.X_AXIS));
			skypeLogoPanel.add(skypeLogoIconLabel);
			connectionSetupPanel.add(skypeLogoPanel);
		}

		loginPanel.add(Box.createRigidArea(new Dimension(10, 33)));

		connectionSetupPanel.add(Box.createRigidArea(new Dimension(10, 33)));

		signInLabelPanel.setLayout(new BoxLayout(signInLabelPanel,
				BoxLayout.X_AXIS));
		signInLabelPanel.setBackground(new Color(0, 175, 240));

		signInLabel.setFont(textFieldFont.deriveFont(Font.TRUETYPE_FONT, 18));
		signInLabel.setForeground(Color.white);

		signInLabelPanel.add(signInLabel);

		homePagePanel.add(signInLabelPanel);

		homePagePanel.add(Box.createRigidArea(new Dimension(10, 10)));

		skypeNameButtonPanel.setLayout(new BoxLayout(skypeNameButtonPanel,
				BoxLayout.X_AXIS));
		skypeNameButtonPanel.setBackground(new Color(0, 175, 240));

		skypeNameButton.setFont(textFieldFont);
		skypeNameButton.setBackground(new Color(12, 125, 175));
		skypeNameButton.setForeground(Color.white);
		skypeNameButton.setMinimumSize(new Dimension(312, 35));
		skypeNameButton.setMaximumSize(new Dimension(312, 35));
		skypeNameButton.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));
		skypeNameButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				remove(homePagePanel);
				add(loginPanel);
				validate();
				repaint();
				navigateSignIn();
			}

		});

		skypeNameButtonPanel.add(skypeNameButton);

		homePagePanel.add(skypeNameButtonPanel);

		homePagePanel.add(Box.createRigidArea(new Dimension(10, 85)));

		learnMoreAboutSkypeAccountLabelPanel.setLayout(new BoxLayout(
				learnMoreAboutSkypeAccountLabelPanel, BoxLayout.X_AXIS));
		learnMoreAboutSkypeAccountLabelPanel.setBackground(new Color(0, 175,
				240));

		MouseAdapter learnMoreAboutSkypeAccountLabelMouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				super.mousePressed(evt);
			}
		};

		learnMoreAboutSkypeAccountLabel
				.addMouseListener(learnMoreAboutSkypeAccountLabelMouseAdapter);
		learnMoreAboutSkypeAccountLabelPanel
				.addMouseListener(learnMoreAboutSkypeAccountLabelMouseAdapter);

		learnMoreAboutSkypeAccountLabel.setFont(textFieldFont
				.deriveFont(underLineFontAttributes));
		learnMoreAboutSkypeAccountLabel.setForeground(Color.white);
		learnMoreAboutSkypeAccountLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		learnMoreAboutSkypeAccountLabelPanel
				.add(learnMoreAboutSkypeAccountLabel);

		homePagePanel.add(learnMoreAboutSkypeAccountLabelPanel);

		loginDetailsMismatchLabel.setFont(textFieldFont);
		loginDetailsMismatchLabel.setForeground(Color.white);
		JPanel loginDetailsMismatchLabelPanel = new JPanel();
		loginDetailsMismatchLabelPanel.setLayout(new BoxLayout(
				loginDetailsMismatchLabelPanel, BoxLayout.X_AXIS));
		loginDetailsMismatchLabelPanel.setBackground(new Color(0, 175, 240));
		loginDetailsMismatchLabelPanel.add(loginDetailsMismatchLabel);

		loginDetailsMismatchLabel2.setFont(textFieldFont);
		loginDetailsMismatchLabel2.setForeground(Color.white);
		JPanel loginDetailsMismatchLabelPanel2 = new JPanel();
		loginDetailsMismatchLabelPanel2.setLayout(new BoxLayout(
				loginDetailsMismatchLabelPanel2, BoxLayout.X_AXIS));
		loginDetailsMismatchLabelPanel2.setBackground(new Color(0, 175, 240));
		loginDetailsMismatchLabelPanel2.add(loginDetailsMismatchLabel2);

		loginDetailsMismatchPanel.setLayout(new BoxLayout(
				loginDetailsMismatchPanel, BoxLayout.Y_AXIS));
		loginDetailsMismatchPanel.setBackground(new Color(0, 175, 240));
		loginDetailsMismatchPanel.add(loginDetailsMismatchLabelPanel);
		loginDetailsMismatchPanel.add(loginDetailsMismatchLabelPanel2);
		loginDetailsMismatchPanel.add(Box
				.createRigidArea(new Dimension(10, 20)));

		connectionDetailsMismatchLabel.setFont(textFieldFont);
		connectionDetailsMismatchLabel.setForeground(Color.white);
		JPanel connectionDetailsMismatchLabelPanel = new JPanel();
		connectionDetailsMismatchLabelPanel.setLayout(new BoxLayout(
				connectionDetailsMismatchLabelPanel, BoxLayout.X_AXIS));
		connectionDetailsMismatchLabelPanel
				.setBackground(new Color(0, 175, 240));
		connectionDetailsMismatchLabelPanel.add(connectionDetailsMismatchLabel);

		connectionDetailsMismatchLabel2.setFont(textFieldFont);
		connectionDetailsMismatchLabel2.setForeground(Color.white);
		JPanel connectionDetailsMismatchLabelPanel2 = new JPanel();
		connectionDetailsMismatchLabelPanel2.setLayout(new BoxLayout(
				connectionDetailsMismatchLabelPanel2, BoxLayout.X_AXIS));
		connectionDetailsMismatchLabelPanel2.setBackground(new Color(0, 175,
				240));
		connectionDetailsMismatchLabelPanel2
				.add(connectionDetailsMismatchLabel2);

		connectionDetailsMismatchPanel.setLayout(new BoxLayout(
				connectionDetailsMismatchPanel, BoxLayout.Y_AXIS));
		connectionDetailsMismatchPanel.setBackground(new Color(0, 175, 240));
		connectionDetailsMismatchPanel.add(connectionDetailsMismatchLabelPanel);
		connectionDetailsMismatchPanel
				.add(connectionDetailsMismatchLabelPanel2);
		connectionDetailsMismatchPanel.add(Box.createRigidArea(new Dimension(
				10, 20)));

		skypeNameField.setFont(textFieldFont);

		// Listen for enter key press in text field
		ActionListener textFieldEnterKeyActionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				signInButton.processMouseEvent(new MouseEvent(signInButton,
						MouseEvent.MOUSE_PRESSED, 0, 0, 0, 0, 1, false, 0));
				try {
					Thread.sleep(68);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				signInButton.processMouseEvent(new MouseEvent(signInButton,
						MouseEvent.MOUSE_RELEASED, 0, 0, 0, 0, 1, false, 0));
				signInButton.repaint();
			}

		};

		// Placeholder
		skypeNameField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				if (skypeNameField.getText().equals("Skype Name")) {
					skypeNameField.setText("");
				}
				skypeNameField.selectAll();
				if (whiteBackgroundTextFieldOnFocus) {
					skypeNameField.setForeground(Color.black);
					skypeNameField.setBackground(Color.white);
					skypeNamePanel.setBackground(Color.white);
				} else {
					skypeNameField.setForeground(Color.black);
					skypeNameField.setBackground(new Color(204, 239, 252));
					skypeNamePanel.setBackground(new Color(204, 239, 252));
				}
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (skypeNameField.getText().equals("")) {
					skypeNameField.setText("Skype Name");
				}
				skypeNameField.setForeground(new Color(0, 175, 240));
				skypeNameField.setBackground(new Color(204, 239, 252));
				skypeNamePanel.setBackground(new Color(204, 239, 252));
			}

		});

		// Listen for input
		skypeNameField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				if (skypeNameField.getText().length() > 0
						&& !skypeNameField.getText().equals("Skype Name")) {
					if (passwordField.getText().length() > 0
							&& !passwordField.getText().equals("Password")) {
						signInButton.setForeground(Color.white);
						signInButton.setEnabled(true);
						signInButton.repaint();
						return;
					}
				}
				signInButton.setForeground(new Color(40, 155, 200));
				signInButton.setEnabled(false);
				signInButton.repaint();
			}

		});

		skypeNamePanel
				.setLayout(new BoxLayout(skypeNamePanel, BoxLayout.X_AXIS));
		skypeNamePanel.setBackground(new Color(204, 239, 252));
		skypeNamePanel.add(Box.createRigidArea(new Dimension(8, 10)));
		skypeNamePanel.setPreferredSize(new Dimension(312, 35));

		skypeNameField.setMinimumSize(new Dimension(304, 35));
		skypeNameField.setMaximumSize(new Dimension(304, 35));
		skypeNameField.setForeground(new Color(0, 175, 240));
		skypeNameField.setBackground(new Color(204, 239, 252));
		skypeNameField.setBorder(BorderFactory.createEmptyBorder());
		skypeNameField.getKeymap().removeKeyStrokeBinding(enterKeyStroke);
		skypeNameField.addActionListener(textFieldEnterKeyActionListener);

		skypeNamePanel.add(skypeNameField);

		loginPanel.add(skypeNamePanel);
		loginPanel.add(Box.createRigidArea(new Dimension(10, 20)));

		hostNameField.setFont(textFieldFont);

		// Placeholder
		hostNameField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				if (hostNameField.getText().equals("Host Name")) {
					hostNameField.setText("");
				}
				hostNameField.selectAll();
				if (whiteBackgroundTextFieldOnFocus) {
					hostNameField.setForeground(Color.black);
					hostNameField.setBackground(Color.white);
					hostNamePanel.setBackground(Color.white);
				} else {
					hostNameField.setForeground(Color.black);
					hostNameField.setBackground(new Color(204, 239, 252));
					hostNamePanel.setBackground(new Color(204, 239, 252));
				}
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (hostNameField.getText().equals("")) {
					hostNameField.setText("Host Name");
				}
				hostNameField.setForeground(new Color(0, 175, 240));
				hostNameField.setBackground(new Color(204, 239, 252));
				hostNamePanel.setBackground(new Color(204, 239, 252));
			}

		});

		// Listen for input
		hostNameField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				if (hostNameField.getText().length() > 0
						&& !hostNameField.getText().equals("Host Name")) {
					connectButton.setForeground(Color.white);
					connectButton.setEnabled(true);
					connectButton.repaint();
					return;
				}
				connectButton.setForeground(new Color(40, 155, 200));
				connectButton.setEnabled(false);
				connectButton.repaint();
			}

		});

		hostNamePanel.setLayout(new BoxLayout(hostNamePanel, BoxLayout.X_AXIS));
		hostNamePanel.setBackground(new Color(204, 239, 252));
		hostNamePanel.add(Box.createRigidArea(new Dimension(8, 10)));
		hostNamePanel.setPreferredSize(new Dimension(312, 35));

		hostNameField.setMinimumSize(new Dimension(304, 35));
		hostNameField.setMaximumSize(new Dimension(304, 35));
		hostNameField.setForeground(new Color(0, 175, 240));
		hostNameField.setBackground(new Color(204, 239, 252));
		hostNameField.setBorder(BorderFactory.createEmptyBorder());
		hostNameField.getKeymap().removeKeyStrokeBinding(enterKeyStroke);
		hostNameField.addActionListener(textFieldEnterKeyActionListener);

		hostNamePanel.add(hostNameField);

		connectionSetupPanel.add(hostNamePanel);

		passwordField.setFont(textFieldFont);

		// Placeholder
		passwordField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				if (passwordField.getText().equals("Password")) {
					passwordField.setText("");
					passwordField.setEchoChar('*');
				}
				passwordField.selectAll();
				if (whiteBackgroundTextFieldOnFocus) {
					passwordField.setForeground(Color.black);
					passwordField.setBackground(Color.white);
					passwordPanel.setBackground(Color.white);
				} else {
					passwordField.setForeground(Color.black);
					passwordField.setBackground(new Color(204, 239, 252));
					passwordPanel.setBackground(new Color(204, 239, 252));
				}
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (passwordField.getText().equals("")) {
					passwordField.setText("Password");
					passwordField.setEchoChar((char) 0);
				}
				passwordField.setForeground(new Color(0, 175, 240));
				passwordField.setBackground(new Color(204, 239, 252));
				passwordPanel.setBackground(new Color(204, 239, 252));
			}

		});

		// Listen for input
		passwordField.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent arg0) {
			}

			@Override
			public void keyReleased(KeyEvent arg0) {
			}

			@Override
			public void keyTyped(KeyEvent arg0) {
				if (skypeNameField.getText().length() > 0
						&& !skypeNameField.getText().equals("Skype Name")) {
					if (passwordField.getText().length() > 0
							&& !passwordField.getText().equals("Password")) {
						signInButton.setForeground(Color.white);
						signInButton.setEnabled(true);
						signInButton.repaint();
						return;
					}
				}
				signInButton.setForeground(new Color(40, 155, 200));
				signInButton.setEnabled(false);
				signInButton.repaint();
			}

		});

		passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.X_AXIS));
		passwordPanel.setBackground(new Color(204, 239, 252));
		passwordPanel.add(Box.createRigidArea(new Dimension(8, 10)));
		passwordPanel.setPreferredSize(new Dimension(312, 35));

		passwordField.setEchoChar((char) 0);
		passwordField.setMinimumSize(new Dimension(304, 35));
		passwordField.setMaximumSize(new Dimension(304, 35));
		passwordField.setForeground(new Color(0, 175, 240));
		passwordField.setBackground(new Color(204, 239, 252));
		passwordField.setBorder(BorderFactory.createEmptyBorder());
		passwordField.getKeymap().removeKeyStrokeBinding(enterKeyStroke);
		passwordField.addActionListener(textFieldEnterKeyActionListener);

		passwordPanel.add(passwordField);

		loginPanel.add(passwordPanel);

		loginPanel.add(Box.createRigidArea(new Dimension(10, 20)));

		connectionSetupPanel.add(Box.createRigidArea(new Dimension(10, 20)));

		MouseAdapter signInButtonMouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				if (skypeNameField.getText().length() == 0) {
					return;
				}
				if (skypeNameField.getText().equals("Skype Name")) {
					return;
				}
				if (passwordField.getText().length() == 0) {
					return;
				}
				if (passwordField.getText().equals("Password")) {
					return;
				}
				navigateSignIn(skypeNameField.getText(),
						passwordField.getText());
			}
		};

		signInButtonPanel.setLayout(new BoxLayout(signInButtonPanel,
				BoxLayout.X_AXIS));
		signInButtonPanel.setBackground(new Color(0, 175, 240));
		signInButtonPanel.addMouseListener(signInButtonMouseAdapter);

		signInButton.setFont(textFieldFont);
		signInButton.setBackground(new Color(12, 125, 175));
		signInButton.setForeground(new Color(40, 155, 200));
		signInButton.setMinimumSize(new Dimension(312, 35));
		signInButton.setMaximumSize(new Dimension(312, 35));
		signInButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		signInButton.addMouseListener(signInButtonMouseAdapter);

		signInButtonPanel.add(signInButton);

		loginPanel.add(signInButtonPanel);

		MouseAdapter connectButtonMouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				if (hostNameField.getText().length() == 0) {
					return;
				}
				if (hostNameField.getText().equals("Host Name")) {
					return;
				}
				Skype.getPlugin().setHostname(hostNameField.getText());
				navigateHomePage();
			}
		};

		connectButtonPanel.setLayout(new BoxLayout(connectButtonPanel,
				BoxLayout.X_AXIS));
		connectButtonPanel.setBackground(new Color(0, 175, 240));
		connectButtonPanel.addMouseListener(connectButtonMouseAdapter);

		connectButton.setFont(textFieldFont);
		connectButton.setBackground(new Color(12, 125, 175));
		connectButton.setForeground(new Color(40, 155, 200));
		connectButton.setMinimumSize(new Dimension(312, 35));
		connectButton.setMaximumSize(new Dimension(312, 35));
		connectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		connectButton.addMouseListener(connectButtonMouseAdapter);

		connectButtonPanel.add(connectButton);

		connectionSetupPanel.add(connectButtonPanel);

		loginPanel.add(Box.createRigidArea(new Dimension(10, 29)));

		connectionSetupPanel.add(Box.createRigidArea(new Dimension(10, 29)));

		cantAccessAccountLabelPanel.setLayout(new BoxLayout(
				cantAccessAccountLabelPanel, BoxLayout.X_AXIS));
		cantAccessAccountLabelPanel.setBackground(new Color(0, 175, 240));

		MouseAdapter cantAccessAccountLabelMouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				super.mousePressed(evt);
			}
		};

		cantAccessAccountLabel
				.addMouseListener(cantAccessAccountLabelMouseAdapter);
		cantAccessAccountLabelPanel
				.addMouseListener(cantAccessAccountLabelMouseAdapter);

		cantAccessAccountLabel.setFont(textFieldFont
				.deriveFont(underLineFontAttributes));
		cantAccessAccountLabel.setForeground(Color.white);
		cantAccessAccountLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		cantAccessAccountLabelPanel.add(cantAccessAccountLabel);

		loginPanel.add(cantAccessAccountLabelPanel);

		cantAccessAccountLabelPanel2.setLayout(new BoxLayout(
				cantAccessAccountLabelPanel2, BoxLayout.X_AXIS));
		cantAccessAccountLabelPanel2.setBackground(new Color(0, 175, 240));

		cantAccessAccountLabel2
				.addMouseListener(cantAccessAccountLabelMouseAdapter);
		cantAccessAccountLabelPanel2
				.addMouseListener(cantAccessAccountLabelMouseAdapter);

		cantAccessAccountLabel2.setFont(textFieldFont
				.deriveFont(underLineFontAttributes));
		cantAccessAccountLabel2.setForeground(Color.white);
		cantAccessAccountLabel2.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		cantAccessAccountLabelPanel2.add(cantAccessAccountLabel2);

		connectionSetupPanel.add(cantAccessAccountLabelPanel2);

		createAccountLabel.setForeground(Color.white);
		createAccountLabel.setFont(textFieldFont);
		createAccountLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));
		ImageIcon rightArrowImageIcon = ImageIO
				.getResourceAsImageIcon("/2031316577.png");
		JLabel createAccountLabel2 = new JLabel(rightArrowImageIcon);
		createAccountLabel2.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		signInWithAnotherAccountLabel.setForeground(Color.white);
		signInWithAnotherAccountLabel.setFont(textFieldFont);
		signInWithAnotherAccountLabel.setCursor(Cursor
				.getPredefinedCursor(Cursor.HAND_CURSOR));

		homePageFooterPanel.setLayout(new BoxLayout(homePageFooterPanel,
				BoxLayout.Y_AXIS));
		homePageFooterPanel.setBackground(new Color(0, 150, 205));
		homePageFooterPanel.add(Box.createRigidArea(new Dimension(10, 21)));

		loginFooterPanel.setLayout(new BoxLayout(loginFooterPanel,
				BoxLayout.Y_AXIS));
		if (retainDarkLoginFooterPanel) {
			loginFooterPanel.setBackground(new Color(0, 150, 205));
		} else {
			loginFooterPanel.setBackground(new Color(0, 175, 240));
		}
		loginFooterPanel.add(Box.createRigidArea(new Dimension(10, 21)));

		MouseAdapter createAccountLabelMouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				super.mousePressed(evt);
				remove(homePagePanel);
				remove(loginPanel);
				remove(homePageFooterPanel);
				remove(loginFooterPanel);
				loginPanel.remove(loginDetailsMismatchPanel);
				ImageIcon loadingGifImageIcon = ImageIO
						.getResourceAsImageIcon("/998288425.gif");
				JLabel loadingGifIconLabel = new JLabel(loadingGifImageIcon);
				add(loadingGifIconLabel);
				validate();
				repaint();
				loginPanel.remove(2);
				loginPanel.add(Box.createRigidArea(new Dimension(10, 33)), 2);
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						RegisterForm registerForm = new RegisterForm(frame,
								new RegisterForm.Runnable() {

									@Override
									public void run() {
										SocketHandlerContext ctx = Skype
												.getPlugin().getHandle();
										UUID participantId = Skype.getPlugin()
												.getUniqueId(skypeName);
										UUID authCode = null;
										{
											Optional<PacketPlayInReply> replyPacket = ctx
													.getOutboundHandler()
													.dispatch(
															ctx,
															new PacketPlayOutRegister(
																	fullName,
																	skypeName,
																	password));
											if (replyPacket.isPresent()) {
												if (replyPacket.get()
														.getStatusCode() == 200) {
													authCode = UUID
															.fromString(replyPacket
																	.get()
																	.getText());
												} else if (replyPacket
														.get()
														.getText()
														.equals("User is already registered.")) {
													DialogForm form = new DialogForm(
															frame,
															PacketPlayInReply.class
																	+ "",
															PacketType.REGISTER
																	+ " failed",
															replyPacket
																	.toString(),
															"OK",
															new Runnable() {

																@Override
																public void run() {
																}

															});
													form.show();
													return;
												}
											}
										}
										System.out
												.println(ctx
														.getOutboundHandler()
														.dispatch(
																ctx,
																new PacketPlayOutRefreshToken(
																		authCode)));
										Contact contact = new Contact();
										contact.setUniqueId(participantId);
										contact.setSkypeName(skypeName);
										contact.setDisplayName(fullName);
										System.out
												.println(ctx
														.getOutboundHandler()
														.dispatch(
																ctx,
																new PacketPlayOutUpdateUser(
																		authCode,
																		participantId,
																		contact)));
										contact = new Contact(contact
												.toString());
										DialogForm form = new DialogForm(frame,
												PacketPlayInReply.class + "",
												PacketType.REGISTER
														+ " success", contact
														.getUniqueId()
														.getUUID().toString(),
												"OK", new Runnable() {

													@Override
													public void run() {
													}

												});
										form.show();
										return;
									}

								});
						registerForm.show();
						remove(loadingGifIconLabel);
						add(loginPanel);
						add(loginFooterPanel, BorderLayout.SOUTH);
						validate();
						repaint();
						skypeNameField.grabFocus();
					}
				}, simulatePageLoadingDelay ? pageLoadingDelayInMs : 1L);
			}
		};

		createAccountLabel.addMouseListener(createAccountLabelMouseAdapter);
		createAccountLabel2.addMouseListener(createAccountLabelMouseAdapter);
		createAccountLabelPanel
				.addMouseListener(createAccountLabelMouseAdapter);

		createAccountLabelPanel.setBackground(new Color(0, 150, 205));
		createAccountLabelPanel.setLayout(new BoxLayout(
				createAccountLabelPanel, BoxLayout.X_AXIS));
		createAccountLabelPanel.add(createAccountLabel);
		createAccountLabelPanel.add(createAccountLabel2);

		MouseAdapter signInWithAnotherAccountMouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				navigateHomePage();
				super.mousePressed(evt);
			}
		};

		signInWithAnotherAccountLabel
				.addMouseListener(signInWithAnotherAccountMouseAdapter);
		signInWithAnotherAccountLabelPanel
				.addMouseListener(signInWithAnotherAccountMouseAdapter);

		if (retainDarkLoginFooterPanel) {
			signInWithAnotherAccountLabelPanel.setBackground(new Color(0, 150,
					205));
		} else {
			signInWithAnotherAccountLabelPanel.setBackground(new Color(0, 175,
					240));
		}
		signInWithAnotherAccountLabelPanel.setLayout(new BoxLayout(
				signInWithAnotherAccountLabelPanel, BoxLayout.Y_AXIS));

		signInWithAnotherAccountLabelPanel.add(signInWithAnotherAccountLabel);

		homePageFooterPanel.add(createAccountLabelPanel);
		homePageFooterPanel.add(Box.createRigidArea(new Dimension(10, 21)));

		loginFooterPanel.add(signInWithAnotherAccountLabelPanel);
		loginFooterPanel.add(Box.createRigidArea(new Dimension(10, 21)));

		add(homePageFooterPanel, BorderLayout.SOUTH);

		add(homePagePanel);

		setJMenuBar(menuBar);

		/**
		 * Note to self! setResizable() must come before pack()
		 * 
		 * If you do not do this before pack() then the size is wrong
		 */
		setResizable(false);

		getContentPane().setPreferredSize(new Dimension(720, 470));
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

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

		addWindowListener(windowAdapter);
	}

	private void navigateHomePage() {
		remove(homePagePanel);
		remove(loginPanel);
		remove(connectionSetupPanel);
		remove(homePageFooterPanel);
		remove(loginFooterPanel);
		ImageIcon loadingGifImageIcon = ImageIO
				.getResourceAsImageIcon("/998288425.gif");
		JLabel loadingGifIconLabel = new JLabel(loadingGifImageIcon);
		add(loadingGifIconLabel);
		validate();
		repaint();
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				Optional<SocketHandlerContext> handle = Skype.getPlugin()
						.createHandle();
				remove(loadingGifIconLabel);
				add(homePagePanel);
				add(homePageFooterPanel, BorderLayout.SOUTH);
				validate();
				repaint();
				skypeNameField.grabFocus();
				if (handle.isPresent()) {
					Skype.getPlugin().setHandle(handle.get());
				} else {
					new Timer().schedule(new TimerTask() {
						@Override
						public void run() {
							navigateConnectionSetup();
						}
					}, simulatePageLoadingDelay ? pageLoadingDelayInMs : 1L);
				}
			}
		}, simulatePageLoadingDelay ? pageLoadingDelayInMs : 1L);
	}

	private void navigateConnectionSetup() {
		remove(homePagePanel);
		remove(loginPanel);
		remove(homePageFooterPanel);
		remove(loginFooterPanel);
		connectionSetupPanel.remove(connectionDetailsMismatchPanel);
		ImageIcon loadingGifImageIcon = ImageIO
				.getResourceAsImageIcon("/998288425.gif");
		JLabel loadingGifIconLabel = new JLabel(loadingGifImageIcon);
		add(loadingGifIconLabel);
		validate();
		repaint();
		new Thread(() -> {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					connectionSetupPanel.remove(2);
					connectionSetupPanel.add(
							Box.createRigidArea(new Dimension(10, 15)), 2);
					connectionSetupPanel.add(connectionDetailsMismatchPanel, 3);
					remove(loadingGifIconLabel);
					add(connectionSetupPanel);
					add(loginFooterPanel, BorderLayout.SOUTH);
					validate();
					repaint();
					hostNameField.grabFocus();
				}
			}, simulatePageLoadingDelay ? pageLoadingDelayInMs : 1L);
		}).start();
	}

	private void navigateSignIn() {
		navigateSignIn(null, null);
	}

	public void navigateSignIn(String skypeName, String password) {
		remove(homePagePanel);
		remove(loginPanel);
		remove(connectionSetupPanel);
		remove(homePageFooterPanel);
		remove(loginFooterPanel);
		loginPanel.remove(loginDetailsMismatchPanel);
		ImageIcon loadingGifImageIcon = ImageIO
				.getResourceAsImageIcon("/998288425.gif");
		JLabel loadingGifIconLabel = new JLabel(loadingGifImageIcon);
		add(loadingGifIconLabel);
		validate();
		repaint();
		new Thread(() -> {
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					if (skypeName != null && password != null) {
						try {
							SocketHandlerContext ctx = Skype.getPlugin()
									.getHandle();
							UUID participantId = Skype.getPlugin().getUniqueId(
									skypeName);
							UUID authCode = null;
							Optional<PacketPlayInReply> replyPacket = ctx
									.getOutboundHandler().dispatch(
											ctx,
											new PacketPlayOutLogin(skypeName,
													password));
							if (replyPacket.isPresent()) {
								if (replyPacket.get().getStatusCode() == 200) {
									authCode = UUID.fromString(replyPacket
											.get().getText());
									ctx.getOutboundHandler().dispatch(
											ctx,
											new PacketPlayOutRefreshToken(
													authCode));
									replyPacket = ctx
											.getOutboundHandler()
											.dispatch(
													ctx,
													new PacketPlayOutLookupUser(
															authCode,
															participantId));
									if (replyPacket.isPresent()) {
										if (replyPacket.get().getStatusCode() == 200) {
											Contact loggedInUser = new Contact(
													replyPacket.get().getText());
											PGPUtilities
													.createOrLookupPublicKey(skypeName);
											MainForm form = new MainForm(
													authCode, password, loggedInUser);
											/**
											 * We will now read the data we have in memory on our disk
											 * 
											 * This may be contacts, conversations, messages and personal data
											 * 
											 * If the data is not present then read the last 30 days from the server
											 * 
											 * The data is stored forever on the hard drive, just not on the server
											 */
											form.readFromMemory();
											frame.removeWindowListener(windowAdapter);
											frame.dispose();
											form.show();
											return;
										}
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						loginPanel.remove(2);
						loginPanel.add(
								Box.createRigidArea(new Dimension(10, 15)), 2);
						loginPanel.add(loginDetailsMismatchPanel, 3);
					} else {
						loginPanel.remove(2);
						loginPanel.add(
								Box.createRigidArea(new Dimension(10, 33)), 2);
					}
					remove(loadingGifIconLabel);
					add(loginPanel);
					add(loginFooterPanel, BorderLayout.SOUTH);
					validate();
					repaint();
					skypeNameField.grabFocus();
				}
			}, simulatePageLoadingDelay ? pageLoadingDelayInMs : 1L);
		}).start();
	}

	@Override
	public void show() {
		navigateHomePage();
		super.show();
	}

}
