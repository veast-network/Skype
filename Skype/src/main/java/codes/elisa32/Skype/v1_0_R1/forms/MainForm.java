package codes.elisa32.Skype.v1_0_R1.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.lang3.text.WordUtils;
import org.bouncycastle.openpgp.PGPException;
import org.pgpainless.PGPainless;

import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptContactRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutDeclineContactRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutEnteringListeningMode;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLookupContacts;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLookupConversationHistory;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLookupMessageHistory;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLookupOnlineStatus;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLookupUser;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutRefreshToken;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutRemoveMessage;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutSendCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutSendContactRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutSendMessage;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutUpdateUser;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutUserSearch;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.Utils;
import codes.elisa32.Skype.v1_0_R1.audioio.AudioIO;
import codes.elisa32.Skype.v1_0_R1.data.types.Contact;
import codes.elisa32.Skype.v1_0_R1.data.types.Conversation;
import codes.elisa32.Skype.v1_0_R1.data.types.Message;
import codes.elisa32.Skype.v1_0_R1.data.types.MessageType;
import codes.elisa32.Skype.v1_0_R1.data.types.Status;
import codes.elisa32.Skype.v1_0_R1.fontio.FontIO;
import codes.elisa32.Skype.v1_0_R1.imageio.ImageIO;
import codes.elisa32.Skype.v1_0_R1.pgp.PGPUtilities;
import codes.elisa32.Skype.v1_0_R1.plugin.Skype;
import codes.elisa32.Skype.v1_0_R1.uicommon.CopyOfJButtonRounded;
import codes.elisa32.Skype.v1_0_R1.uicommon.JContactsConversationGroup;
import codes.elisa32.Skype.v1_0_R1.uicommon.JRecentConversationGroup;
import codes.elisa32.Skype.v1_0_R1.uicommon.JVerticalLayout;

import com.google.gson.Gson;
import com.ibm.icu.util.Calendar;

public class MainForm extends JFrame {

	private JFrame frame = this;

	private static MainForm instance;

	private Timer timer1, timer2, timer3;

	private WindowAdapter windowAdapter = new WindowAdapter() {
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
							Optional<SocketHandlerContext> ctx = Skype
									.getPlugin().createHandle();
							if (ctx.isPresent()) {
								loggedInUser.setOnlineStatus(Status.OFFLINE);
								PacketPlayOutUpdateUser msg = new PacketPlayOutUpdateUser(
										authCode, loggedInUser.getUniqueId(),
										loggedInUser);
								ctx.get().getOutboundHandler()
										.dispatch(ctx.get(), msg);
							}
							setVisible(false);
							AudioIO.LOGOUT.playSoundSync();
							System.exit(-1);
						}

					});
			form.show();
		}
	};

	private SimpleDateFormat twelveHourTime = new SimpleDateFormat("h:mm a");

	private SimpleDateFormat twentyFourHourTime = new SimpleDateFormat("HH:mm");

	private UUID authCode;

	private final Contact loggedInUser;

	private static List<Conversation> conversations = new ArrayList<Conversation>();

	private Conversation selectedConversation;

	/**
	 * UI Defaults
	 */
	private final Dimension defaultWindowSize = new Dimension(1008, 709);

	private final int defaultLeftPanelWidth = 242;

	/*
	 * This controls the speed at which the scroll pane will let us scroll
	 */
	private final int scrollBarUnitIncrement = 16;

	/*
	 * We add one pixel to each of these panels to give room for the border
	 */
	private final int defaultRightTopPanelHeight = 80 + 1;
	private final int defaultRightBottomBottomPanelHeight = 76 + 1;

	/**
	 * UI
	 */
	private Font font = new Font("Seriff", Font.TRUETYPE_FONT, 13);

	private FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(
			font.deriveFont(Font.BOLD, font.getSize()));

	private int scrollBarWidth = ((Integer) UIManager.get("ScrollBar.width"))
			.intValue() + 5;

	private int splitPaneDividerSize = new JSplitPane().getDividerSize();

	private String leftBottomPanelPage = "Contacts";
	public String rightPanelPage = "AccountHome";

	/*
	 * Notification count next to Recent button in top left panel
	 */
	private JPanel recentNotificationCountLabelPanel = new JPanel();
	private JLabel recentNotificationCountLabel = new JLabel(""
			+ this.notificationCount);
	private int notificationCount = 0;

	/**
	 * UI Controls
	 */
	private JSplitPane splitPane = new JSplitPane();
	private JSplitPane leftSplitPane = new JSplitPane();
	private JSplitPane rightSplitPane = new JSplitPane();

	/*
	 * Top left panel
	 */
	private JPanel leftTopPanel = new JPanel();

	private JTextField searchTextField = new JTextField("Search");
	private List<Conversation> searchTextFieldConversations = new ArrayList<Conversation>();

	private JPanel homeButtonBackgroundPanel = new JPanel();
	private JPanel callPhonesButtonBackgroundPanel = new JPanel();

	private JLabel contactsLabel = new JLabel("CONTACTS");
	private JLabel recentLabel = new JLabel("RECENT");

	/*
	 * Bottom left panel
	 */
	private JScrollPane leftBottomScrollPane = new JScrollPane();

	/*
	 * Top right panel
	 */
	private JPanel rightTopPanel = new JPanel();

	/*
	 * Bottom right panel
	 */
	private JSplitPane rightBottomSplitPane = new JSplitPane();

	public JScrollPane rightBottomTopPanel = new JScrollPane();

	private JPanel rightBottomBottomPanel = new JPanel();

	/*
	 * Call variables
	 */
	private boolean microphoneEnabled = true;
	private boolean videoEnabled = false;
	public boolean ongoingCall = false;
	public JLabel ongoingCallTimeLabel = new JLabel();
	public long ongoingCallStartTime = 0L;
	public Conversation ongoingCallConversation = null;
	public Socket callOutgoingAudioSocket = null;
	public Socket callIncomingAudioSocket = null;

	public UUID getAuthCode() {
		return authCode;
	}

	public Optional<Conversation> getConversation(UUID participantId) {
		for (Conversation conversation : conversations) {
			if (conversation.getUniqueId().equals(participantId)) {
				return Optional.of(conversation);
			}
		}
		for (Conversation conversation : searchTextFieldConversations) {
			if (conversation.getUniqueId().equals(participantId)) {
				return Optional.of(conversation);
			}
		}
		if (selectedConversation != null) {
			if (selectedConversation.getUniqueId().equals(participantId)) {
				return Optional.of(selectedConversation);
			}
		}
		return Optional.empty();
	}

	public void setSelectedConversation(Conversation conversation) {
		this.selectedConversation = conversation;
	}

	public Conversation getSelectedConversation() {
		return selectedConversation;
	}

	public List<Conversation> getConversations() {
		return conversations;
	}

	public List<Conversation> getSearchTextFieldConversations() {
		return this.searchTextFieldConversations;
	}

	public Contact getLoggedInUser() {
		return loggedInUser;
	}

	public static MainForm get() {
		return instance;
	}

	public String getDateLabelInReferenceToToday(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		String parsedDate = new SimpleDateFormat("EEEE, MMMM d, yyyy",
				Locale.US).format(date);
		for (int i = 0; i <= 7; i++) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			calendar.add(Calendar.DAY_OF_MONTH, -i);
			int todayYear = calendar.get(Calendar.YEAR);
			int todayMonth = calendar.get(Calendar.MONTH);
			int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
			if (year == todayYear) {
				if (month == todayMonth) {
					if (day == todayDay) {
						if (i == 0) {
							return "Today";
						} else if (i == 1) {
							return "Yesterday";
						} else {
							return new SimpleDateFormat("EEEE").format(date);
						}
					}
				}
			}
		}
		return parsedDate;
	}

	private JLayeredPane createLeftTopLayeredPane(int panelWidth) {
		Color leftTopPaneBackgroundColor = new Color(245, 252, 254);

		/* Start left top panel */
		/**
		 * Construct layered pane for absolute positioning with z index
		 */
		JLayeredPane leftTopLayeredPane = new JLayeredPane();
		leftTopLayeredPane.setBounds(0, 0, panelWidth, 230);
		leftTopLayeredPane.setPreferredSize(new Dimension(panelWidth, 230));
		leftTopLayeredPane.setBackground(leftTopPaneBackgroundColor);
		leftTopLayeredPane.setOpaque(true);

		/**
		 * Add decoration floating in top left panel
		 */
		{
			JPanel iconLabelPanel = new JPanel();
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/52189619.png");
			JLabel iconLabel = new JLabel(imageIcon);
			iconLabelPanel.setBounds(0, -5, 130, 35);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 0
			 */
			leftTopLayeredPane.add(iconLabelPanel, new Integer(0), 0);
		}

		/**
		 * Add decoration floating in top left panel 2
		 */
		{
			BufferedImage bImg = new BufferedImage(panelWidth, 9,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = bImg.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(new Color(211, 230, 234));
			if (leftBottomPanelPage.equals("Contacts")) {
				int mov = -72;
				g2.drawLine(0, 8, 105 + mov, 8);
				g2.drawLine(106 + mov, 8, 117 + mov, 0);
				g2.drawLine(117 + mov, 0, 130 + mov, 8);
				g2.drawLine(131 + mov, 8, panelWidth, 8);
			} else if (leftBottomPanelPage.equals("Recent")
					|| leftBottomPanelPage.equals("RecentOlder")) {
				int mov = 0;
				g2.drawLine(0, 8, 105 + mov, 8);
				g2.drawLine(106 + mov, 8, 117 + mov, 0);
				g2.drawLine(117 + mov, 0, 130 + mov, 8);
				g2.drawLine(131 + mov, 8, panelWidth, 8);
			}
			g2.dispose();
			ImageIcon imageIcon = new ImageIcon(bImg);
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			JLabel iconLabel = new JLabel(imageIcon);
			iconLabelPanel.setBounds(0, 230 - 9, panelWidth, 9);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 0
			 */
			leftTopLayeredPane.add(iconLabelPanel, new Integer(0), 0);
		}

		/**
		 * Add profile icon floating in top left panel
		 */
		{
			JPanel iconLabelPanel = new JPanel();
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/2121871768.png");
			imageIcon = ImageIO.getScaledImageIcon(imageIcon, new Dimension(40,
					40));
			imageIcon = ImageIO.getCircularImageIcon(imageIcon);
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					if (loggedInUser.getPubKey().isPresent()) {
						String pubKey;
						try {
							pubKey = PGPainless.asciiArmor(loggedInUser
									.getPubKey().get());
							JOptionPane.showMessageDialog(frame, pubKey);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(15, 19, 40, 50);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1, above decoration panel
			 */
			leftTopLayeredPane.add(iconLabelPanel, new Integer(1), 0);
		}

		/**
		 * Add status icon floating in top left panel
		 */
		{
			JPanel iconLabelPanel = new JPanel();

			/**
			 * We load the status icon from memory as an icon image
			 */
			ImageIcon imageIcon = ImageIO.getCircularStatusIcon(
					loggedInUser.getOnlineStatus(), Color.white);

			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			iconLabelPanel.setBounds(42, 46, 14, 24);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 2, above profile icon
			 */
			leftTopLayeredPane.add(iconLabelPanel, new Integer(2), 0);
		}

		/**
		 * Add status text floating in top left panel
		 */
		{
			JPanel statusLabelPanel = new JPanel();

			statusLabelPanel.setOpaque(false);

			/**
			 * Capitalize only the first letter of the online status
			 */
			JLabel statusLabel = new JLabel(WordUtils.capitalize(loggedInUser
					.getOnlineStatus().name().toLowerCase()));

			statusLabel.setFont(font);

			int width = statusLabel.getPreferredSize().width;
			int height = statusLabel.getPreferredSize().height;

			statusLabelPanel.add(statusLabel);
			statusLabelPanel.setBounds(70, 41, width, height + 10);

			/**
			 * Panel added to pane with z-index 1, above decoration panel
			 */
			leftTopLayeredPane.add(statusLabelPanel, new Integer(1), 0);
		}

		/**
		 * Add display name floating in top left panel
		 */
		{
			JPanel displayNameLabelPanel = new JPanel();
			JLabel displayNameLabel = new JLabel(loggedInUser.getDisplayName());
			displayNameLabel.setFont(font);

			/**
			 * Reserved for future use
			 */
			displayNameLabel.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					if (loggedInUser.getPubKey().isPresent()) {
						String pubKey;
						try {
							pubKey = PGPainless.asciiArmor(loggedInUser
									.getPubKey().get());
							JOptionPane.showMessageDialog(frame, pubKey);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}

			};

			displayNameLabel.addMouseListener(mouseAdapter);

			displayNameLabelPanel.setOpaque(false);
			displayNameLabelPanel.add(displayNameLabel);
			int width = displayNameLabel.getPreferredSize().width;
			int height = displayNameLabel.getPreferredSize().height;
			displayNameLabelPanel.setBounds(70, 20, width, height + 10);

			/**
			 * Panel added to pane with z-index 1, above decoration panel
			 */
			leftTopLayeredPane.add(displayNameLabelPanel, new Integer(1), 0);
		}

		/* Start search panel */

		/**
		 * Add search icon floating in window
		 */
		{
			JPanel iconLabelPanel = new JPanel();
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/1008012489.png");
			JLabel iconLabel = new JLabel(imageIcon);
			iconLabelPanel.setBounds(0, 65, 120, 51);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1, above decoration panel
			 */
			leftTopLayeredPane.add(iconLabelPanel, new Integer(1), 0);
		}

		/**
		 * Add search border line floating in window
		 */
		{
			JPanel searchBorderLinePanel = new JPanel();
			int width = panelWidth - 30;
			searchBorderLinePanel.setBounds(15, 110, width, 1);
			searchBorderLinePanel.setOpaque(true);
			searchBorderLinePanel.setBackground(new Color(211, 230, 234));

			/**
			 * Panel added to pane with z-index 2, above search icon label
			 */
			leftTopLayeredPane.add(searchBorderLinePanel, new Integer(2), 0);
		}

		/**
		 * Add search text field floating in window
		 */
		{
			JPanel searchTextFieldPanel = new JPanel();
			searchTextFieldPanel.setBounds(38, 75, 192, 25);
			searchTextFieldPanel.setOpaque(false);

			searchTextField.setFont(font);
			searchTextField.setPreferredSize(new Dimension(192, 20));

			/**
			 * We remove all signs of there being a text field here
			 * 
			 * This is because we are decorating the text field ourselves
			 */
			searchTextField.setBackground(new Color(245, 252, 254));
			searchTextField.setBorder(BorderFactory.createEmptyBorder());

			searchTextFieldPanel.add(searchTextField);

			/**
			 * Panel added to pane with z-index 1, above decoration panel
			 */
			leftTopLayeredPane.add(searchTextFieldPanel, new Integer(1), 0);
		}
		/* End search panel */

		/* Start home button panel */

		MouseAdapter homeButtonMouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!rightPanelPage.equals("AccountHome")) {
					/*
					 * Home button is not selected
					 * 
					 * We will now make it selected
					 */
					homeButtonBackgroundPanel.setBackground(new Color(199, 237,
							252));
					callPhonesButtonBackgroundPanel
							.setBackground(leftTopPaneBackgroundColor);
					rightPanelPage = "AccountHome";
					selectedConversation = null;
					refreshWindow();
				} else {
					/*
					 * Home button is selected
					 * 
					 * We will not do anything
					 */
					return;
				}
			}
		};

		/**
		 * Add home button background floating in window
		 */
		{
			homeButtonBackgroundPanel.setBounds(0, 121, panelWidth, 31);
			homeButtonBackgroundPanel.setOpaque(true);
			if (rightPanelPage.equals("AccountHome")) {
				homeButtonBackgroundPanel
						.setBackground(new Color(199, 237, 252));
			} else {
				homeButtonBackgroundPanel
						.setBackground(leftTopPaneBackgroundColor);
			}
			homeButtonBackgroundPanel.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));

			homeButtonBackgroundPanel.addMouseListener(homeButtonMouseAdapter);

			/**
			 * Panel added to pane with z-index 1, above decoration panel
			 */
			leftTopLayeredPane
					.add(homeButtonBackgroundPanel, new Integer(1), 0);
		}

		/**
		 * Add home button icon floating in window
		 */
		{
			JPanel iconLabelPanel = new JPanel();
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/1014844487.png");
			JLabel iconLabel = new JLabel(imageIcon);

			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			iconLabelPanel.setBounds(7, 116, 32, 32);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);
			iconLabelPanel.addMouseListener(homeButtonMouseAdapter);

			/**
			 * Panel added to pane with z-index 2, above home button background
			 */
			leftTopLayeredPane.add(iconLabelPanel, new Integer(2), 0);
		}

		/**
		 * Add home label floating in top left panel
		 */
		{
			JPanel homeLabelPanel = new JPanel();
			JLabel homeLabel = new JLabel("Home");
			homeLabel.setFont(font);
			homeLabelPanel.setOpaque(false);
			homeLabelPanel.add(homeLabel);
			int width = homeLabel.getPreferredSize().width;
			int height = homeLabel.getPreferredSize().height;
			homeLabelPanel.setBounds(40, 122, width, height + 10);

			homeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			homeLabel.addMouseListener(homeButtonMouseAdapter);

			/**
			 * Panel added to pane with z-index 2, above home button background
			 */
			leftTopLayeredPane.add(homeLabelPanel, new Integer(2), 0);
		}

		/* End home button panel */

		/* Start call phones button panel */

		MouseAdapter callPhonesButtonMouseAdapter = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				if (!rightPanelPage.equals("CallPhones")) {
					/*
					 * Call phones button is not selected
					 * 
					 * We will now make it selected
					 */
					callPhonesButtonBackgroundPanel.setBackground(new Color(
							199, 237, 252));
					homeButtonBackgroundPanel
							.setBackground(leftTopPaneBackgroundColor);
					rightPanelPage = "CallPhones";
					selectedConversation = null;
					refreshWindow();
				} else {
					/*
					 * Call phones button is selected
					 * 
					 * We will not do anything
					 */
					return;
				}
			}
		};

		/**
		 * Add call phones button background floating in window
		 */
		{
			callPhonesButtonBackgroundPanel.setBounds(0, 152, panelWidth, 31);
			callPhonesButtonBackgroundPanel.setOpaque(true);
			if (rightPanelPage.equals("CallPhones")) {
				callPhonesButtonBackgroundPanel.setBackground(new Color(199,
						237, 252));
			} else {
				callPhonesButtonBackgroundPanel
						.setBackground(leftTopPaneBackgroundColor);

			}

			callPhonesButtonBackgroundPanel.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));

			callPhonesButtonBackgroundPanel
					.addMouseListener(callPhonesButtonMouseAdapter);

			/**
			 * Panel added to pane with z-index 1, above decoration panel
			 */
			leftTopLayeredPane.add(callPhonesButtonBackgroundPanel,
					new Integer(1), 0);
		}

		/**
		 * Add call phones icon floating in top left panel
		 */
		{
			JPanel iconLabelPanel = new JPanel();
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/1161558118.png");
			JLabel iconLabel = new JLabel(imageIcon);

			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			iconLabelPanel.setBounds(7, 146, 32, 32);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);
			iconLabelPanel.addMouseListener(callPhonesButtonMouseAdapter);

			/**
			 * Panel added to pane with z-index 2, above call phones background
			 */
			leftTopLayeredPane.add(iconLabelPanel, new Integer(2), 0);
		}

		/**
		 * Add call phones label floating in top left panel
		 */
		{
			JPanel callPhonesLabelPanel = new JPanel();
			JLabel callPhonesLabel = new JLabel("Call phones");
			callPhonesLabel.setFont(font);
			callPhonesLabelPanel.setOpaque(false);
			callPhonesLabelPanel.add(callPhonesLabel);
			int width = callPhonesLabel.getPreferredSize().width;
			int height = callPhonesLabel.getPreferredSize().height;
			callPhonesLabelPanel.setBounds(40, 152, width, height + 10);

			callPhonesLabel.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));

			callPhonesLabel.addMouseListener(callPhonesButtonMouseAdapter);

			/**
			 * Panel added to pane with z-index 2, above call phones background
			 */
			leftTopLayeredPane.add(callPhonesLabelPanel, new Integer(2), 0);
		}

		/* End home button panel */

		/* Start contacts label */
		/**
		 * Add contacts label floating in top left panel
		 */
		{
			JPanel contactsLabelPanel = new JPanel();
			contactsLabel.setFont(font.deriveFont(Font.TRUETYPE_FONT, 12));
			if (leftBottomPanelPage.equals("Contacts")) {
				contactsLabel.setForeground(new Color(0, 149, 204));
			} else {
				contactsLabel.setForeground(Color.black);
			}
			contactsLabelPanel.setOpaque(false);
			contactsLabelPanel.add(contactsLabel);
			int width = contactsLabel.getPreferredSize().width;
			int height = contactsLabel.getPreferredSize().height;
			contactsLabelPanel.setBounds(15, 193, width, height + 10);

			contactsLabel.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));

			contactsLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					if (leftBottomPanelPage.equals("Recent")
							|| leftBottomPanelPage.equals("RecentOlder")) {
						leftBottomPanelPage = "Contacts";
						refreshWindow();
					}
				}
			});

			/**
			 * Panel added to pane with z-index 1, above decoration panel
			 */
			leftTopLayeredPane.add(contactsLabelPanel, new Integer(1), 0);
		}

		/* End contacts label */

		/* Start recent label */
		/**
		 * Add recent label floating in top left panel
		 */
		{
			JPanel recentLabelPanel = new JPanel();
			if (leftBottomPanelPage.equals("Recent")
					|| leftBottomPanelPage.equals("RecentOlder")) {
				recentLabel.setForeground(new Color(0, 149, 204));
			} else {
				recentLabel.setForeground(Color.black);
			}
			recentLabel.setFont(font.deriveFont(Font.TRUETYPE_FONT, 12));
			recentLabelPanel.setOpaque(false);
			recentLabelPanel.add(recentLabel);
			int width = recentLabel.getPreferredSize().width;
			int height = recentLabel.getPreferredSize().height;
			recentLabelPanel.setBounds(96, 193, width, height + 10);

			recentLabel.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));

			recentLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent arg0) {
					if (leftBottomPanelPage.equals("Contacts")) {
						leftBottomPanelPage = "Recent";
						refreshWindow();
					}
				}
			});

			/**
			 * Panel added to pane with z-index 1, above decoration panel
			 */
			leftTopLayeredPane.add(recentLabelPanel, new Integer(1), 0);
		}

		/**
		 * Add notification count in top left panel
		 */
		recentNotificationCountLabel.setText("" + this.notificationCount);
		recentNotificationCountLabel.setFont(font);
		recentNotificationCountLabel.setForeground(new Color(255, 125, 0));
		recentNotificationCountLabel.setFont(font);

		recentNotificationCountLabelPanel.setOpaque(false);
		recentNotificationCountLabelPanel.add(recentNotificationCountLabel);
		int width = recentNotificationCountLabel.getPreferredSize().width;
		int height = recentNotificationCountLabel.getPreferredSize().height;
		recentNotificationCountLabelPanel.setBounds(151, 192, width,
				height + 10);

		/**
		 * Panel added to pane with z-index 1
		 */
		leftTopLayeredPane.add(recentNotificationCountLabelPanel,
				new Integer(1), 0);

		if (this.notificationCount == 0) {
			recentNotificationCountLabelPanel.setVisible(false);
		} else {
			recentNotificationCountLabelPanel.setVisible(true);
		}

		/* End recent label */

		/* End left top panel */
		return leftTopLayeredPane;
	}

	private JPanel createLeftBottomPanel(int panelWidth) {

		final JPanel panel = new JPanel();

		panel.setLayout(new JVerticalLayout(0, JVerticalLayout.LEFT,
				JVerticalLayout.TOP));

		final Color scrollPaneColor = new Color(245, 252, 254);

		panel.setBackground(scrollPaneColor);

		final int groupNamePanelHeight = 40;
		final int conversationHeight = 50;

		this.notificationCount = 0;

		List<Conversation> conversations = this.conversations;

		if (searchTextField.getText().length() > 0) {
			if (!searchTextField.getText().equals("Search")) {
				conversations = this.searchTextFieldConversations;
			}
		}

		if (leftBottomPanelPage.equals("Contacts")) {
			Map<String, List<Conversation>> groups = new HashMap<>();

			groups.put("Favorites", new ArrayList<Conversation>());
			groups.put("All", new ArrayList<Conversation>());

			for (Conversation conversation : conversations) {
				if (conversation instanceof Contact) {
					if (((Contact) conversation).isFavorite()) {
						groups.get("Favorites").add(conversation);
					} else {
						groups.get("All").add(conversation);
					}
				}
				this.notificationCount += conversation.getNotificationCount();
			}

			List<JContactsConversationGroup> sortedGroups = new ArrayList<>();

			sortedGroups.add(new JContactsConversationGroup("Favorites", groups
					.get("Favorites")));
			sortedGroups.add(new JContactsConversationGroup("All", groups
					.get("All")));

			for (JContactsConversationGroup conversationGroup : sortedGroups) {
				if (conversationGroup.getConversations().size() > 0) {
					JPanel labelPanel = new JPanel();
					labelPanel.setOpaque(false);

					/**
					 * This allows us to put a layered pane at x 0 y 0
					 * 
					 * Note that the layered pane by default has a null layout
					 * 
					 * That null layout allows us to set the bounds to start at
					 * x 0 y 0
					 * 
					 * If we do not put this code then there will be extra
					 * padding
					 * 
					 * That padding would interfere with the look of the program
					 */
					labelPanel
							.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

					JLayeredPane labelLayeredPane = new JLayeredPane();
					labelLayeredPane.setBounds(0, 0, panelWidth,
							groupNamePanelHeight);
					labelLayeredPane.setPreferredSize(new Dimension(panelWidth,
							groupNamePanelHeight));
					labelLayeredPane.setOpaque(false);

					/**
					 * Add label name in conversation panel
					 */
					{
						JPanel labelNamePanel = new JPanel();
						JLabel labelNameLabel = new JLabel(
								conversationGroup.getGroup());
						labelNameLabel.setFont(font);
						labelNamePanel.setOpaque(false);
						labelNamePanel.add(labelNameLabel);
						int width = labelNameLabel.getPreferredSize().width;
						int height = labelNameLabel.getPreferredSize().height;
						labelNamePanel.setBounds(15, 5, width, height + 10);

						/**
						 * Panel added to pane with z-index 0
						 */
						labelLayeredPane.add(labelNamePanel, new Integer(0), 0);
					}

					labelPanel.add(labelLayeredPane);

					panel.add(labelPanel);
				}
				for (Conversation conversation : conversationGroup
						.getConversations()) {
					JPanel conversationPanel = new JPanel();

					MouseAdapter conversationMouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent evt) {
							if (evt.getButton() != MouseEvent.BUTTON1) {
								return;
							}
							selectConversation(conversation);
						}
					};

					conversationPanel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					conversationPanel
							.addMouseListener(conversationMouseAdapter);

					JPopupMenu popUp = new JPopupMenu();
					{
						JMenuItem menuItem = new JMenuItem(
								"Clear chat for both participants");
						menuItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								DialogForm form = new DialogForm(
										null,
										"Skype™ - Clear chat?",
										"Clear chat?",
										"Are you sure you want to clear this chat?",
										"Remove", new Runnable() {

											@Override
											public void run() {
												Optional<SocketHandlerContext> ctx = Skype
														.getPlugin()
														.createHandle();
												if (ctx.isPresent()) {
													Optional<PacketPlayInReply> reply = ctx
															.get()
															.getOutboundHandler()
															.dispatch(
																	ctx.get(),
																	new PacketPlayOutLogin(
																			authCode));
													if (!reply.isPresent()
															|| reply.get()
																	.getStatusCode() != 200) {
														return;
													}
													for (Message message : conversation
															.getMessages()
															.toArray(
																	new Message[0])
															.clone()) {
														if (message.isDeleted()) {
															continue;
														}
														if (message
																.getMessageType() != null) {
															continue;
														}
														UUID authCode = UUID
																.fromString(reply
																		.get()
																		.getText());
														PacketPlayOutRemoveMessage removeMessage = new PacketPlayOutRemoveMessage(
																authCode,
																conversation
																		.getUniqueId(),
																message.getUniqueId(),
																message.getTimestamp());
														Optional<PacketPlayInReply> replyPacket2 = ctx
																.get()
																.getOutboundHandler()
																.dispatch(
																		ctx.get(),
																		removeMessage);
														if (!replyPacket2
																.isPresent()) {
															return;
														}
														if (replyPacket2
																.get()
																.getStatusCode() != 200) {
															return;
														}
													}
												}
											}
										});

								form.show();

							}
						});
						popUp.add(menuItem);
					}
					popUp.add(new JSeparator());
					{
						JMenuItem menuItem = new JMenuItem(
								"Remove from Contacts");
						menuItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								if (conversation == null) {
									return;
								}
								if (!(conversation instanceof Contact)) {
									return;
								}
								DialogForm form = new DialogForm(null,
										"Skype™ - Remove contact?",
										"Remove contact?", "Remove "
												+ selectedConversation
														.getDisplayName()
												+ " from Contacts?", "Remove",
										new Runnable() {

											@Override
											public void run() {
												Optional<SocketHandlerContext> ctx = Skype
														.getPlugin()
														.createHandle();
												if (ctx.isPresent()) {
													Optional<PacketPlayInReply> reply = ctx
															.get()
															.getOutboundHandler()
															.dispatch(
																	ctx.get(),
																	new PacketPlayOutLogin(
																			authCode));
													if (!reply.isPresent()
															|| reply.get()
																	.getStatusCode() != 200) {
														return;
													}
													for (Message message : conversation
															.getMessages()
															.toArray(
																	new Message[0])
															.clone()) {
														if (message.isDeleted()) {
															continue;
														}
														UUID authCode = UUID
																.fromString(reply
																		.get()
																		.getText());
														PacketPlayOutRemoveMessage removeMessage = new PacketPlayOutRemoveMessage(
																authCode,
																conversation
																		.getUniqueId(),
																message.getUniqueId(),
																message.getTimestamp());
														Optional<PacketPlayInReply> replyPacket2 = ctx
																.get()
																.getOutboundHandler()
																.dispatch(
																		ctx.get(),
																		removeMessage);
														if (!replyPacket2
																.isPresent()) {
															return;
														}
														if (replyPacket2
																.get()
																.getStatusCode() != 200) {
															return;
														}
													}
												}
											}
										});

								form.show();
							}

						});
						popUp.add(menuItem);
					}

					conversationPanel.setComponentPopupMenu(popUp);

					if (selectedConversation != null
							&& selectedConversation.equals(conversation)) {
						conversationPanel
								.setBackground(new Color(199, 237, 252));
						conversationPanel.setOpaque(true);
					} else {
						conversationPanel.setOpaque(false);
					}

					/**
					 * This allows us to put a layered pane at x 0 y 0
					 * 
					 * Note that the layered pane by default has a null layout
					 * 
					 * That null layout allows us to set the bounds to start at
					 * x 0 y 0
					 * 
					 * If we do not put this code then there will be extra
					 * padding
					 * 
					 * That padding would interfere with the look of the program
					 */
					conversationPanel.setLayout(new FlowLayout(
							FlowLayout.CENTER, 0, 0));

					JLayeredPane conversationLayeredPane = new JLayeredPane();
					conversationLayeredPane.setBounds(0, 0, panelWidth,
							conversationHeight);
					conversationLayeredPane.setPreferredSize(new Dimension(
							panelWidth, conversationHeight));
					conversationLayeredPane.setOpaque(false);

					/**
					 * Add profile icon with status icon in conversation panel
					 */
					{
						JPanel iconLabelPanel = conversation
								.getOnlineStatusPanel();

						iconLabelPanel.setBounds(14, 0, 40, 50);

						/**
						 * Panel added to pane with z-index 0
						 */
						conversationLayeredPane.add(iconLabelPanel,
								new Integer(0), 0);
					}

					/**
					 * Add notification count in conversation panel
					 * 
					 * This is only done if the count is less then 3 digits long
					 */
					if (conversation.getNotificationCount() > 0
							&& conversation.getNotificationCount() < 100) {
						JPanel notificationCountLabelPanel = new JPanel();

						JLabel notificationCountLabel = new JLabel(""
								+ conversation.getNotificationCount());
						notificationCountLabel
								.addMouseListener(conversationMouseAdapter);
						notificationCountLabel.setComponentPopupMenu(popUp);
						notificationCountLabel.setFont(font);
						notificationCountLabel.setForeground(new Color(255,
								125, 0));
						notificationCountLabel.setFont(font.deriveFont(
								Font.BOLD, font.getSize()));

						notificationCountLabelPanel.setOpaque(false);
						notificationCountLabelPanel.add(notificationCountLabel);
						int width = notificationCountLabel.getPreferredSize().width;
						int height = notificationCountLabel.getPreferredSize().height;
						if (conversation instanceof Contact) {
							Contact contact = (Contact) conversation;
							if (contact.getMood() != null
									&& contact.getMood().length() > 0) {
								notificationCountLabelPanel.setBounds(
										panelWidth - 16 - width, 4, width,
										height + 10);
							} else {
								notificationCountLabelPanel.setBounds(
										panelWidth - 16 - width, 11, width,
										height + 10);
							}
						} else {
							notificationCountLabelPanel.setBounds(panelWidth
									- 16 - width, 11, width, height + 10);
						}

						/**
						 * Panel added to pane with z-index 0
						 */
						conversationLayeredPane.add(
								notificationCountLabelPanel, new Integer(0), 0);
					}

					/**
					 * Add conversation name in conversation panel
					 */
					{
						JPanel conversationNameLabelPanel = new JPanel();
						int notificationCountWidth = fm.stringWidth(""
								+ conversation.getNotificationCount());
						JLabel conversationNameLabel = new JLabel(
								Utils.concatStringEllipses(
										fm,
										panelWidth
												- 73
												- (conversation
														.getNotificationCount() > 0
														&& conversation
																.getNotificationCount() < 100 ? notificationCountWidth
														+ fm.charWidth('1')
														: 0)
												- this.scrollBarWidth,
										conversation.getDisplayName()));
						conversationNameLabel
								.addMouseListener(conversationMouseAdapter);
						conversationNameLabel.setComponentPopupMenu(popUp);
						conversationNameLabel.setFont(font);
						conversationNameLabel.setToolTipText(conversation
								.getDisplayName());
						if (conversation.getNotificationCount() > 0) {
							conversationNameLabel.setForeground(new Color(255,
									125, 0));
							conversationNameLabel.setFont(font.deriveFont(
									Font.BOLD, font.getSize()));
						}

						conversationNameLabelPanel.setOpaque(false);
						conversationNameLabelPanel.add(conversationNameLabel);
						int width = conversationNameLabel.getPreferredSize().width;
						int height = conversationNameLabel.getPreferredSize().height;
						if (conversation instanceof Contact) {
							Contact contact = (Contact) conversation;
							if (contact.getMood() != null
									&& contact.getMood().length() > 0) {
								conversationNameLabelPanel.setBounds(70, 4,
										width, height + 10);
							} else {
								conversationNameLabelPanel.setBounds(70, 11,
										width, height + 10);
							}
						} else {
							conversationNameLabelPanel.setBounds(70, 11, width,
									height + 10);
						}

						/**
						 * Panel added to pane with z-index 0
						 */
						conversationLayeredPane.add(conversationNameLabelPanel,
								new Integer(0), 0);
					}

					if (conversation instanceof Contact) {
						Contact contact = (Contact) conversation;
						if (contact.getMood() != null
								&& contact.getMood().length() > 0) {
							/**
							 * Add contact mood in conversation panel
							 */
							JPanel contactMoodLabelPanel = new JPanel();
							int notificationCountWidth = fm.stringWidth(""
									+ conversation.getNotificationCount());
							JLabel contactMoodLabel = new JLabel(
									Utils.concatStringEllipses(
											fm,
											panelWidth
													- 73
													- (conversation
															.getNotificationCount() > 0
															&& conversation
																	.getNotificationCount() < 100 ? notificationCountWidth
															+ fm.charWidth('1')
															: 0)
													- this.scrollBarWidth,
											contact.getMood()));
							contactMoodLabel
									.addMouseListener(conversationMouseAdapter);
							contactMoodLabel.setComponentPopupMenu(popUp);
							contactMoodLabel.setFont(font);
							contactMoodLabel.setToolTipText(contact.getMood());
							contactMoodLabel.setForeground(new Color(158, 166,
									169));

							contactMoodLabelPanel.setOpaque(false);
							contactMoodLabelPanel.add(contactMoodLabel);
							int width = contactMoodLabel.getPreferredSize().width;
							int height = contactMoodLabel.getPreferredSize().height;
							contactMoodLabelPanel.setBounds(70, 22, width,
									height + 10);

							/**
							 * Panel added to pane with z-index 0
							 */
							conversationLayeredPane.add(contactMoodLabelPanel,
									new Integer(0), 0);
						}
					}

					conversationPanel.add(conversationLayeredPane);

					panel.add(conversationPanel);
				}
			}
		} else if (leftBottomPanelPage.equals("Recent")
				|| leftBottomPanelPage.equals("RecentOlder")) {
			Map<String, List<Conversation>> groups = new HashMap<>();

			groups.put("Today", new ArrayList<Conversation>());
			groups.put("Yesterday", new ArrayList<Conversation>());

			for (int i = 2; i < 8; i++) {
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				Date lastModified = new Date(year - 1900, month, day - i);
				String dayOfWeek = new SimpleDateFormat("EEEE")
						.format(lastModified);
				groups.put(dayOfWeek, new ArrayList<Conversation>());
			}

			groups.put("Older than a week", new ArrayList<Conversation>());
			groups.put("Older than a month", new ArrayList<Conversation>());

			for (Conversation conversation : conversations) {
				this.notificationCount += conversation.getNotificationCount();
				Date now = new Date();
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(conversation.getLastModified());
				int lastModifiedYear = calendar.get(Calendar.YEAR);
				int lastModifiedMonth = calendar.get(Calendar.MONTH);
				int lastModifiedDay = calendar.get(Calendar.DAY_OF_MONTH);
				String group = new SimpleDateFormat("EEEE").format(conversation
						.getLastModified());
				calendar.setTime(now);
				int todayYear = calendar.get(Calendar.YEAR);
				int todayMonth = calendar.get(Calendar.MONTH);
				int todayDay = calendar.get(Calendar.DAY_OF_MONTH);
				if (lastModifiedYear == todayYear) {
					if (lastModifiedMonth == todayMonth) {
						if (lastModifiedDay == todayDay) {
							group = "Today";
						} else if (lastModifiedDay == todayDay - 1) {
							group = "Yesterday";
						}
					}
				}
				long diffInMillies = now.getTime()
						- conversation.getLastModified().getTime();
				long diffInDays = TimeUnit.DAYS.convert(diffInMillies,
						TimeUnit.MILLISECONDS);
				if (diffInDays > 30) {
					group = "Older than a month";
				} else if (diffInDays >= 7) {
					group = "Older than a week";
				}
				groups.get(group).add(conversation);
			}

			List<JRecentConversationGroup> sortedGroups = new ArrayList<>();

			sortedGroups.add(new JRecentConversationGroup("Today", groups
					.get("Today")));
			sortedGroups.add(new JRecentConversationGroup("Yesterday", groups
					.get("Yesterday")));

			for (int i = 2; i < 8; i++) {
				Calendar calendar = Calendar.getInstance();
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				Date lastModified = new Date(year - 1900, month, day - i);
				String dayOfWeek = new SimpleDateFormat("EEEE")
						.format(lastModified);
				sortedGroups.add(new JRecentConversationGroup(dayOfWeek, groups
						.get(dayOfWeek)));
			}

			sortedGroups.add(new JRecentConversationGroup("Older than a week",
					groups.get("Older than a week")));
			sortedGroups.add(new JRecentConversationGroup("Older than a month",
					groups.get("Older than a month")));

			for (JRecentConversationGroup conversationGroup : sortedGroups) {
				if (conversationGroup.getConversations().size() > 0) {
					if (conversationGroup.getGroup().equals(
							"Older than a month")) {
						if (leftBottomPanelPage.equals("Recent")) {
							JPanel showEarlierMessagesPanel = new JPanel();
							showEarlierMessagesPanel.setOpaque(false);

							/**
							 * This allows us to put a layered pane at x 0 y 0
							 * 
							 * Note that the layered pane by default has a null
							 * layout
							 * 
							 * That null layout allows us to set the bounds to
							 * start at x 0 y 0
							 * 
							 * If we do not put this code then there will be
							 * extra padding
							 * 
							 * That padding would interfere with the look of the
							 * program
							 */
							showEarlierMessagesPanel.setLayout(new FlowLayout(
									FlowLayout.CENTER, 0, 0));

							JLayeredPane showEarlierMessagesLayeredPane = new JLayeredPane();
							showEarlierMessagesLayeredPane.setBounds(0, 0,
									panelWidth, groupNamePanelHeight - 5);
							showEarlierMessagesLayeredPane
									.setPreferredSize(new Dimension(panelWidth,
											groupNamePanelHeight));
							showEarlierMessagesLayeredPane.setOpaque(false);

							/**
							 * Add label name in conversation panel
							 */
							{
								JPanel showEarlierMessagesLabelPanel = new JPanel();
								JLabel showEarlierMessagesLabel = new JLabel(
										"Show earlier messages");
								showEarlierMessagesLabel
										.setForeground(new Color(0, 149, 204));
								showEarlierMessagesLabel
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.HAND_CURSOR));
								showEarlierMessagesLabel
										.addMouseListener(new MouseAdapter() {
											@Override
											public void mousePressed(
													MouseEvent evt) {
												leftBottomPanelPage = "RecentOlder";
												refreshWindow();
											}
										});
								showEarlierMessagesLabel.setFont(font);
								showEarlierMessagesLabelPanel.setOpaque(false);
								showEarlierMessagesLabelPanel
										.add(showEarlierMessagesLabel);
								int width = showEarlierMessagesLabel
										.getPreferredSize().width;
								int height = showEarlierMessagesLabel
										.getPreferredSize().height;
								showEarlierMessagesLabelPanel.setBounds(15, 5,
										width, height + 10);

								/**
								 * Panel added to pane with z-index 0
								 */
								showEarlierMessagesLayeredPane.add(
										showEarlierMessagesLabelPanel,
										new Integer(0), 0);
							}

							showEarlierMessagesPanel
									.add(showEarlierMessagesLayeredPane);

							panel.add(showEarlierMessagesPanel);
							continue;
						}
					}
					JPanel labelPanel = new JPanel();
					labelPanel.setOpaque(false);

					/**
					 * This allows us to put a layered pane at x 0 y 0
					 * 
					 * Note that the layered pane by default has a null layout
					 * 
					 * That null layout allows us to set the bounds to start at
					 * x 0 y 0
					 * 
					 * If we do not put this code then there will be extra
					 * padding
					 * 
					 * That padding would interfere with the look of the program
					 */
					labelPanel
							.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

					JLayeredPane labelLayeredPane = new JLayeredPane();
					labelLayeredPane.setBounds(0, 0, panelWidth,
							groupNamePanelHeight);
					labelLayeredPane.setPreferredSize(new Dimension(panelWidth,
							groupNamePanelHeight));
					labelLayeredPane.setOpaque(false);

					/**
					 * Add label name in conversation panel
					 */
					{
						JPanel labelNamePanel = new JPanel();
						JLabel labelNameLabel = new JLabel(
								conversationGroup.getGroup());
						labelNameLabel.setFont(font);
						labelNamePanel.setOpaque(false);
						labelNamePanel.add(labelNameLabel);
						int width = labelNameLabel.getPreferredSize().width;
						int height = labelNameLabel.getPreferredSize().height;
						labelNamePanel.setBounds(15, 5, width, height + 10);

						/**
						 * Panel added to pane with z-index 0
						 */
						labelLayeredPane.add(labelNamePanel, new Integer(0), 0);
					}

					labelPanel.add(labelLayeredPane);

					panel.add(labelPanel);
				}
				for (Conversation conversation : conversationGroup
						.getConversations()) {
					JPanel conversationPanel = new JPanel();

					MouseAdapter conversationMouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent evt) {
							if (evt.getButton() != MouseEvent.BUTTON1) {
								return;
							}
							selectConversation(conversation);
						}
					};

					conversationPanel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					conversationPanel
							.addMouseListener(conversationMouseAdapter);

					JPopupMenu popUp = new JPopupMenu();
					{
						JMenuItem menuItem = new JMenuItem(
								"Clear chat for both participants");
						menuItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								DialogForm form = new DialogForm(
										null,
										"Skype™ - Clear chat?",
										"Clear chat?",
										"Are you sure you want to clear this chat?",
										"Remove", new Runnable() {

											@Override
											public void run() {
												Optional<SocketHandlerContext> ctx = Skype
														.getPlugin()
														.createHandle();
												if (ctx.isPresent()) {
													Optional<PacketPlayInReply> reply = ctx
															.get()
															.getOutboundHandler()
															.dispatch(
																	ctx.get(),
																	new PacketPlayOutLogin(
																			authCode));
													if (!reply.isPresent()
															|| reply.get()
																	.getStatusCode() != 200) {
														return;
													}
													for (Message message : conversation
															.getMessages()
															.toArray(
																	new Message[0])
															.clone()) {
														if (message.isDeleted()) {
															continue;
														}
														if (message
																.getMessageType() != null) {
															continue;
														}
														UUID authCode = UUID
																.fromString(reply
																		.get()
																		.getText());
														PacketPlayOutRemoveMessage removeMessage = new PacketPlayOutRemoveMessage(
																authCode,
																conversation
																		.getUniqueId(),
																message.getUniqueId(),
																message.getTimestamp());
														Optional<PacketPlayInReply> replyPacket2 = ctx
																.get()
																.getOutboundHandler()
																.dispatch(
																		ctx.get(),
																		removeMessage);
														if (!replyPacket2
																.isPresent()) {
															return;
														}
														if (replyPacket2
																.get()
																.getStatusCode() != 200) {
															return;
														}
													}
												}
											}
										});

								form.show();

							}
						});
						popUp.add(menuItem);
					}
					popUp.add(new JSeparator());
					{
						JMenuItem menuItem = new JMenuItem(
								"Remove from Contacts");
						menuItem.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								if (conversation == null) {
									return;
								}
								if (!(conversation instanceof Contact)) {
									return;
								}
								DialogForm form = new DialogForm(null,
										"Skype™ - Remove contact?",
										"Remove contact?", "Remove "
												+ selectedConversation
														.getDisplayName()
												+ " from Contacts?", "Remove",
										new Runnable() {

											@Override
											public void run() {
												Optional<SocketHandlerContext> ctx = Skype
														.getPlugin()
														.createHandle();
												if (ctx.isPresent()) {
													Optional<PacketPlayInReply> reply = ctx
															.get()
															.getOutboundHandler()
															.dispatch(
																	ctx.get(),
																	new PacketPlayOutLogin(
																			authCode));
													if (!reply.isPresent()
															|| reply.get()
																	.getStatusCode() != 200) {
														return;
													}
													for (Message message : conversation
															.getMessages()
															.toArray(
																	new Message[0])
															.clone()) {
														if (message.isDeleted()) {
															continue;
														}
														UUID authCode = UUID
																.fromString(reply
																		.get()
																		.getText());
														PacketPlayOutRemoveMessage removeMessage = new PacketPlayOutRemoveMessage(
																authCode,
																conversation
																		.getUniqueId(),
																message.getUniqueId(),
																message.getTimestamp());
														Optional<PacketPlayInReply> replyPacket2 = ctx
																.get()
																.getOutboundHandler()
																.dispatch(
																		ctx.get(),
																		removeMessage);
														if (!replyPacket2
																.isPresent()) {
															return;
														}
														if (replyPacket2
																.get()
																.getStatusCode() != 200) {
															return;
														}
													}
												}
											}
										});

								form.show();
							}

						});
						popUp.add(menuItem);
					}

					conversationPanel.setComponentPopupMenu(popUp);

					if (selectedConversation != null
							&& selectedConversation.equals(conversation)) {
						conversationPanel
								.setBackground(new Color(199, 237, 252));
						conversationPanel.setOpaque(true);
					} else {
						conversationPanel.setOpaque(false);
					}

					/**
					 * This allows us to put a layered pane at x 0 y 0
					 * 
					 * Note that the layered pane by default has a null layout
					 * 
					 * That null layout allows us to set the bounds to start at
					 * x 0 y 0
					 * 
					 * If we do not put this code then there will be extra
					 * padding
					 * 
					 * That padding would interfere with the look of the program
					 */
					conversationPanel.setLayout(new FlowLayout(
							FlowLayout.CENTER, 0, 0));

					JLayeredPane conversationLayeredPane = new JLayeredPane();
					conversationLayeredPane.setBounds(0, 0, panelWidth,
							conversationHeight);
					conversationLayeredPane.setPreferredSize(new Dimension(
							panelWidth, conversationHeight));
					conversationLayeredPane.setOpaque(false);

					/**
					 * Add profile icon with status icon in conversation panel
					 */
					{
						JPanel iconLabelPanel = conversation
								.getOnlineStatusPanel();

						iconLabelPanel.setBounds(14, 0, 40, 50);

						/**
						 * Panel added to pane with z-index 0
						 */
						conversationLayeredPane.add(iconLabelPanel,
								new Integer(0), 0);
					}

					/**
					 * Add notification count in conversation panel
					 * 
					 * This is only done if the count is less then 3 digits long
					 */
					if (conversation.getNotificationCount() > 0
							&& conversation.getNotificationCount() < 100) {
						JPanel notificationCountLabelPanel = new JPanel();

						JLabel notificationCountLabel = new JLabel(""
								+ conversation.getNotificationCount());
						notificationCountLabel
								.addMouseListener(conversationMouseAdapter);
						notificationCountLabel.setComponentPopupMenu(popUp);
						notificationCountLabel.setFont(font);
						notificationCountLabel.setForeground(new Color(255,
								125, 0));
						notificationCountLabel.setFont(font.deriveFont(
								Font.BOLD, font.getSize()));

						notificationCountLabelPanel.setOpaque(false);
						notificationCountLabelPanel.add(notificationCountLabel);
						int width = notificationCountLabel.getPreferredSize().width;
						int height = notificationCountLabel.getPreferredSize().height;
						if (conversation instanceof Contact) {
							Contact contact = (Contact) conversation;
							if (contact.getMood() != null
									&& contact.getMood().length() > 0) {
								notificationCountLabelPanel.setBounds(
										panelWidth - 16 - width, 4, width,
										height + 10);
							} else {
								notificationCountLabelPanel.setBounds(
										panelWidth - 16 - width, 11, width,
										height + 10);
							}
						} else {
							notificationCountLabelPanel.setBounds(panelWidth
									- 16 - width, 11, width, height + 10);
						}

						/**
						 * Panel added to pane with z-index 0
						 */
						conversationLayeredPane.add(
								notificationCountLabelPanel, new Integer(0), 0);
					}

					/**
					 * Add conversation name in conversation panel
					 */
					{
						JPanel conversationNameLabelPanel = new JPanel();
						int notificationCountWidth = fm.stringWidth(""
								+ conversation.getNotificationCount());
						JLabel conversationNameLabel = new JLabel(
								Utils.concatStringEllipses(
										fm,
										panelWidth
												- 73
												- (conversation
														.getNotificationCount() > 0
														&& conversation
																.getNotificationCount() < 100 ? notificationCountWidth
														+ fm.charWidth('1')
														: 0)
												- this.scrollBarWidth,
										conversation.getDisplayName()));
						conversationNameLabel
								.addMouseListener(conversationMouseAdapter);
						conversationNameLabel.setComponentPopupMenu(popUp);
						conversationNameLabel.setFont(font);
						conversationNameLabel.setToolTipText(conversation
								.getDisplayName());
						if (conversation.getNotificationCount() > 0) {
							conversationNameLabel.setForeground(new Color(255,
									125, 0));
							conversationNameLabel.setFont(font.deriveFont(
									Font.BOLD, font.getSize()));
						}

						conversationNameLabelPanel.setOpaque(false);
						conversationNameLabelPanel.add(conversationNameLabel);
						int width = conversationNameLabel.getPreferredSize().width;
						int height = conversationNameLabel.getPreferredSize().height;
						if (conversation instanceof Contact) {
							Contact contact = (Contact) conversation;
							if (contact.getMood() != null
									&& contact.getMood().length() > 0) {
								conversationNameLabelPanel.setBounds(70, 4,
										width, height + 10);
							} else {
								conversationNameLabelPanel.setBounds(70, 11,
										width, height + 10);
							}
						} else {
							conversationNameLabelPanel.setBounds(70, 11, width,
									height + 10);
						}

						/**
						 * Panel added to pane with z-index 0
						 */
						conversationLayeredPane.add(conversationNameLabelPanel,
								new Integer(0), 0);
					}

					if (conversation instanceof Contact) {
						Contact contact = (Contact) conversation;
						if (contact.getMood() != null
								&& contact.getMood().length() > 0) {
							/**
							 * Add contact mood in conversation panel
							 */
							JPanel contactMoodLabelPanel = new JPanel();
							int notificationCountWidth = fm.stringWidth(""
									+ conversation.getNotificationCount());
							JLabel contactMoodLabel = new JLabel(
									Utils.concatStringEllipses(
											fm,
											panelWidth
													- 73
													- (conversation
															.getNotificationCount() > 0
															&& conversation
																	.getNotificationCount() < 100 ? notificationCountWidth
															+ fm.charWidth('1')
															: 0)
													- this.scrollBarWidth,
											contact.getMood()));
							contactMoodLabel
									.addMouseListener(conversationMouseAdapter);
							contactMoodLabel.setComponentPopupMenu(popUp);
							contactMoodLabel.setFont(font);
							contactMoodLabel.setToolTipText(contact.getMood());
							contactMoodLabel.setForeground(new Color(158, 166,
									169));

							contactMoodLabelPanel.setOpaque(false);
							contactMoodLabelPanel.add(contactMoodLabel);
							int width = contactMoodLabel.getPreferredSize().width;
							int height = contactMoodLabel.getPreferredSize().height;
							contactMoodLabelPanel.setBounds(70, 22, width,
									height + 10);

							/**
							 * Panel added to pane with z-index 0
							 */
							conversationLayeredPane.add(contactMoodLabelPanel,
									new Integer(0), 0);
						}
					}

					conversationPanel.add(conversationLayeredPane);

					panel.add(conversationPanel);
				}
			}
		}

		panel.add(Box.createRigidArea(new Dimension(10, 5)));

		recentNotificationCountLabel.setText("" + this.notificationCount);

		if (this.notificationCount == 0) {
			recentNotificationCountLabelPanel.setVisible(false);
		} else {
			Rectangle rect = recentNotificationCountLabelPanel.getBounds();
			rect.width = recentNotificationCountLabel.getPreferredSize().width;
			recentNotificationCountLabelPanel.setBounds(rect);
			recentNotificationCountLabelPanel.setVisible(true);
		}

		return panel;
	}

	private JLayeredPane createRightTopLayeredPane(int panelWidth,
			int panelHeight) {
		/* Start right top panel */
		/**
		 * Construct layered pane for absolute positioning with z index
		 */
		JLayeredPane rightTopLayeredPane = new JLayeredPane();
		rightTopLayeredPane.setBounds(0, 0, panelWidth, panelHeight);
		rightTopLayeredPane.setPreferredSize(new Dimension(panelWidth,
				panelHeight));
		rightTopLayeredPane.setBorder(BorderFactory.createMatteBorder(0, 0, 1,
				0, new Color(211, 230, 234)));
		rightTopLayeredPane.setOpaque(false);

		if (selectedConversation == null) {
			/**
			 * The selected right panel page is not Conversation
			 * 
			 * We want the right panel to be just a JScrollPane in this case
			 * 
			 * So we will not put a right top layered pane in this case
			 */
			return rightTopLayeredPane;
		}

		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = selectedConversation.getImageIcon();
			imageIcon = ImageIO.getScaledImageIcon(imageIcon, new Dimension(60,
					60));
			imageIcon = ImageIO.getCircularImageIcon(imageIcon);
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					if (selectedConversation instanceof Contact) {
						Contact contact = (Contact) selectedConversation;
						if (contact.getPubKey().isPresent()) {
							String pubKey;
							try {
								pubKey = PGPainless.asciiArmor(contact
										.getPubKey().get());
								JOptionPane.showMessageDialog(frame, pubKey);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(10, 10, 60, 60);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 0
			 */
			rightTopLayeredPane.add(iconLabelPanel, new Integer(0), 0);
		}

		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/1074949811.png");
			if (selectedConversation instanceof Contact) {
				if (((Contact) selectedConversation).isFavorite()) {
					imageIcon = new ImageIcon(new BufferedImage(30, 30,
							BufferedImage.TYPE_INT_ARGB));
				}
			}
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			iconLabelPanel.setBounds(80, 25, 14, 14);

			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 0
			 */
			rightTopLayeredPane.add(iconLabelPanel, new Integer(0), 0);
		}

		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getCircularStatusIcon(Status.NOT_A_CONTACT);
			if (selectedConversation instanceof Contact) {
				imageIcon = ImageIO
						.getCircularStatusIcon(((Contact) selectedConversation)
								.getOnlineStatus());
			} else if (selectedConversation.isGroupChat()) {
				imageIcon = new ImageIcon(new BufferedImage(30, 30,
						BufferedImage.TYPE_INT_ARGB));
			}
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			if (selectedConversation.isGroupChat()) {
				iconLabelPanel.setBounds(81, 48, 12, 12);
			} else {
				iconLabelPanel.setBounds(80, 47, 14, 14);
			}
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 0
			 */
			rightTopLayeredPane.add(iconLabelPanel, new Integer(0), 0);
		}

		/**
		 * Add status text floating in top left panel
		 */
		{
			JPanel statusLabelPanel = new JPanel();

			statusLabelPanel.setOpaque(false);

			/**
			 * Capitalize only the first letter of the online status
			 */
			JLabel statusLabel = new JLabel(
					" This person isn't in your Contact list.");
			if (selectedConversation instanceof Contact) {
				Status onlineStatus = ((Contact) selectedConversation)
						.getOnlineStatus();
				statusLabel = new JLabel(WordUtils.capitalize(onlineStatus
						.name().toLowerCase()));
			}

			statusLabel.setFont(font);

			int width = statusLabel.getPreferredSize().width;
			int height = statusLabel.getPreferredSize().height;

			statusLabelPanel.add(statusLabel);
			statusLabelPanel.setBounds(104, 40, width, height + 10);

			/**
			 * Panel added to pane with z-index 1, above decoration panel
			 */
			rightTopLayeredPane.add(statusLabelPanel, new Integer(1), 0);
		}

		/**
		 * Add display name floating in top left panel
		 */
		{
			JPanel displayNameLabelPanel = new JPanel();
			JLabel displayNameLabel = new JLabel(
					selectedConversation.getDisplayName());
			displayNameLabel.setFont(FontIO.SEGOE_UI_SEMILIGHT.deriveFont(
					Font.TRUETYPE_FONT, 21));

			/**
			 * Reserved for future use
			 */
			displayNameLabel.setCursor(Cursor
					.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					if (selectedConversation instanceof Contact) {
						Contact contact = (Contact) selectedConversation;
						if (contact.getPubKey().isPresent()) {
							String pubKey;
							try {
								pubKey = PGPainless.asciiArmor(contact
										.getPubKey().get());
								JOptionPane.showMessageDialog(frame, pubKey);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}

			};

			displayNameLabel.addMouseListener(mouseAdapter);

			displayNameLabelPanel.setOpaque(false);
			displayNameLabelPanel.add(displayNameLabel);
			int width = displayNameLabel.getPreferredSize().width;
			int height = displayNameLabel.getPreferredSize().height;
			displayNameLabelPanel.setBounds(104, 11, width, height + 10);

			/**
			 * Panel added to pane with z-index 1, above decoration panel
			 */
			rightTopLayeredPane.add(displayNameLabelPanel, new Integer(1), 0);
		}

		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/1154498825.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			iconLabelPanel.setBounds(panelWidth - 166, 20, 40, 40);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 0
			 */
			rightTopLayeredPane.add(iconLabelPanel, new Integer(0), 0);
		}

		{
			MouseAdapter iconLabelMouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					if (ongoingCall == false) {
						Optional<SocketHandlerContext> ctx = Skype.getPlugin()
								.createHandle();
						if (!ctx.isPresent()) {
							return;
						}
						Optional<PacketPlayInReply> reply = ctx
								.get()
								.getOutboundHandler()
								.dispatch(
										ctx.get(),
										new PacketPlayOutSendCallRequest(
												authCode, selectedConversation
														.getUniqueId()));
						if (!reply.isPresent()) {
							return;
						}
						if (reply.get().getStatusCode() != 200) {
							return;
						}
						AudioIO.CALL_INIT.playSound();
					}
					rightPanelPage = "OngoingCall";
					ongoingCall = true;
					ongoingCallConversation = selectedConversation;
					ongoingCallStartTime = System.currentTimeMillis();
					refreshWindow();
				}
			};
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/1342120668.png");
			JLabel iconLabel = new JLabel(imageIcon);

			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			iconLabel.addMouseListener(iconLabelMouseAdapter);

			iconLabelPanel.setBounds(panelWidth - 111, 20, 40, 40);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);
			iconLabelPanel.addMouseListener(iconLabelMouseAdapter);

			/**
			 * Panel added to pane with z-index 0
			 */
			rightTopLayeredPane.add(iconLabelPanel, new Integer(0), 0);
		}

		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/653174002.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			iconLabelPanel.setBounds(panelWidth - 56, 20, 40, 40);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 0
			 */
			rightTopLayeredPane.add(iconLabelPanel, new Integer(0), 0);
		}

		return rightTopLayeredPane;
	}

	private JPanel createRightBottomTopPanel(int panelWidth) {
		JPanel panel = new JPanel();

		if (rightPanelPage.equals("AccountHome")) {
			int leftSplitPaneWidth = leftSplitPane.getDividerLocation();
			if (leftSplitPaneWidth == 0 || leftSplitPaneWidth == -1) {
				leftSplitPaneWidth = this.defaultLeftPanelWidth;
			}
			panelWidth = getContentPane().getSize().width
					- this.splitPaneDividerSize - leftSplitPaneWidth;
			int panelHeight = this.getContentPane().getHeight();
			JEditorPane website = new JEditorPane();
			website.setEditable(false);
			try {
				website.setPage("https://elisa322008.github.io/skype/");
			} catch (IOException e) {
				e.printStackTrace();
			}
			panel.setBounds(0, 0, panelWidth, panelHeight);
			JScrollPane pane = new JScrollPane(website);
			pane.setBorder(BorderFactory.createEmptyBorder());
			pane.setBounds(0, 0, panelWidth, panelHeight);
			panel.setLayout(null);
			panel.add(pane);
			return panel;
		}

		if (rightPanelPage.equals("OngoingCall")) {
			int leftSplitPaneWidth = leftSplitPane.getDividerLocation();
			if (leftSplitPaneWidth == 0 || leftSplitPaneWidth == -1) {
				leftSplitPaneWidth = this.defaultLeftPanelWidth;
			}
			panelWidth = getContentPane().getSize().width
					- this.splitPaneDividerSize - leftSplitPaneWidth;
			int panelHeight = this.getContentPane().getHeight();
			JLayeredPane layeredPane = new JLayeredPane();
			layeredPane.setBounds(0, 0, panelWidth, panelHeight);
			layeredPane
					.setPreferredSize(new Dimension(panelWidth, panelHeight));
			layeredPane.setOpaque(false);

			{
				JPanel panel2 = new JPanel();
				panel2.setBounds(0, 0, panelWidth, panelHeight);
				panel2.setPreferredSize(new Dimension(panelWidth, panelHeight));
				panel2.setLayout(new BorderLayout());
				panel2.setBackground(new Color(38, 38, 38));

				{
					JPanel iconLabelPanel = new JPanel();
					iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
							0, 0));
					ImageIcon imageIcon;
					if (videoEnabled) {
						imageIcon = ImageIO
								.getResourceAsImageIcon("/628935161.png");
					} else {
						imageIcon = ImageIO
								.getResourceAsImageIcon("/652946707.png");
					}
					JLabel iconLabel = new JLabel(imageIcon);

					iconLabelPanel.setBounds(panelWidth / 2 - 133 + (46 / 2)
							- 3, panelHeight - 24 - 46, 46, 46);
					iconLabelPanel.setOpaque(false);
					iconLabelPanel.add(iconLabel);

					MouseAdapter mouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent evt) {
							videoEnabled = !videoEnabled;
							refreshWindow();
						}
					};

					iconLabel.addMouseListener(mouseAdapter);
					iconLabelPanel.addMouseListener(mouseAdapter);

					iconLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
					iconLabelPanel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					/**
					 * Panel added to pane with z-index 1
					 */
					layeredPane.add(iconLabelPanel, new Integer(1), 0);
				}

				{
					JPanel iconLabelPanel = new JPanel();
					iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
							0, 0));
					ImageIcon imageIcon = ImageIO
							.getResourceAsImageIcon("/567059254.png");
					JLabel iconLabel = new JLabel(imageIcon);

					iconLabelPanel.setBounds(
							panelWidth / 2 - 13 + (46 / 2) - 3,
							panelHeight - 24 - 46, 46, 46);
					iconLabelPanel.setOpaque(false);
					iconLabelPanel.add(iconLabel);

					MouseAdapter mouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent evt) {
							/*
							 * 
							 */
							refreshWindow();
						}
					};

					iconLabel.addMouseListener(mouseAdapter);
					iconLabelPanel.addMouseListener(mouseAdapter);

					iconLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
					iconLabelPanel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					/**
					 * Panel added to pane with z-index 1
					 */
					layeredPane.add(iconLabelPanel, new Integer(1), 0);
				}

				{
					JPanel iconLabelPanel = new JPanel();
					iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
							0, 0));
					ImageIcon imageIcon = ImageIO
							.getResourceAsImageIcon("/58164463.png");
					JLabel iconLabel = new JLabel(imageIcon);

					iconLabelPanel.setBounds(
							panelWidth / 2 + 47 + (46 / 2) - 3,
							panelHeight - 24 - 46, 46, 46);
					iconLabelPanel.setOpaque(false);
					iconLabelPanel.add(iconLabel);

					MouseAdapter mouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent evt) {
							/*
							 * TODO: End call
							 */
							ongoingCall = false;
							ongoingCallConversation = null;
							ongoingCallStartTime = 0L;
							rightPanelPage = "Conversation";
							refreshWindow(true);
							try {
								callIncomingAudioSocket.close();
								callOutgoingAudioSocket.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					};

					iconLabel.addMouseListener(mouseAdapter);
					iconLabelPanel.addMouseListener(mouseAdapter);

					iconLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
					iconLabelPanel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					/**
					 * Panel added to pane with z-index 1
					 */
					layeredPane.add(iconLabelPanel, new Integer(1), 0);
				}

				{
					JPanel iconLabelPanel = new JPanel();
					iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
							0, 0));
					ImageIcon imageIcon;
					if (microphoneEnabled) {
						imageIcon = ImageIO
								.getResourceAsImageIcon("/600306010.png");
					} else {
						imageIcon = ImageIO
								.getResourceAsImageIcon("/624317556.png");
					}
					JLabel iconLabel = new JLabel(imageIcon);

					iconLabelPanel.setBounds(
							panelWidth / 2 - 73 + (46 / 2) - 3,
							panelHeight - 24 - 46, 46, 46);
					iconLabelPanel.setOpaque(false);
					iconLabelPanel.add(iconLabel);

					MouseAdapter mouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent evt) {
							microphoneEnabled = !microphoneEnabled;
							refreshWindow();
						}
					};

					iconLabel.addMouseListener(mouseAdapter);
					iconLabelPanel.addMouseListener(mouseAdapter);

					iconLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
					iconLabelPanel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					/**
					 * Panel added to pane with z-index 1
					 */
					layeredPane.add(iconLabelPanel, new Integer(1), 0);
				}

				{
					JPanel iconLabelPanel = new JPanel();
					iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
							0, 0));
					ImageIcon imageIcon = ImageIO
							.getResourceAsImageIcon("/1595064335.png");

					JLabel iconLabel = new JLabel(imageIcon);

					iconLabelPanel.setBounds(panelWidth / 2 - (256 / 2),
							panelHeight / 2 - (256 / 2), 256, 256);
					iconLabelPanel.setOpaque(false);
					iconLabelPanel.add(iconLabel);

					MouseAdapter mouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent evt) {
							refreshWindow();
						}
					};

					iconLabel.addMouseListener(mouseAdapter);
					iconLabelPanel.addMouseListener(mouseAdapter);

					iconLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
					iconLabelPanel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					/**
					 * Panel added to pane with z-index 1
					 */
					layeredPane.add(iconLabelPanel, new Integer(1), 0);
				}

				{
					JPanel iconLabelPanel = new JPanel();
					iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
							0, 0));
					ImageIcon imageIcon = ImageIO
							.getResourceAsImageIcon("/738834160.png");

					JLabel iconLabel = new JLabel(imageIcon);

					iconLabelPanel.setBounds(panelWidth - 58, 7, 46, 46);
					iconLabelPanel.setOpaque(false);
					iconLabelPanel.add(iconLabel);

					MouseAdapter mouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent evt) {
							/*
							 * Open chat window
							 */
							rightPanelPage = "Conversation";
							refreshWindow(true);
						}
					};

					iconLabel.addMouseListener(mouseAdapter);
					iconLabelPanel.addMouseListener(mouseAdapter);

					iconLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
					iconLabelPanel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					/**
					 * Panel added to pane with z-index 1
					 */
					layeredPane.add(iconLabelPanel, new Integer(1), 0);
				}

				{
					JPanel iconLabelPanel = new JPanel();
					iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
							0, 0));
					ImageIcon imageIcon = ImageIO
							.getResourceAsImageIcon("/822874571.png");

					JLabel iconLabel = new JLabel(imageIcon);

					iconLabelPanel.setBounds(panelWidth - 116, 7, 46, 46);
					iconLabelPanel.setOpaque(false);
					iconLabelPanel.add(iconLabel);

					MouseAdapter mouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent evt) {
							/*
							 * See connection details
							 */
							refreshWindow();
						}
					};

					iconLabel.addMouseListener(mouseAdapter);
					iconLabelPanel.addMouseListener(mouseAdapter);

					iconLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
					iconLabelPanel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					/**
					 * Panel added to pane with z-index 1
					 */
					layeredPane.add(iconLabelPanel, new Integer(1), 0);
				}

				{
					JPanel iconLabelPanel = new JPanel();
					iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
							0, 0));
					ImageIcon imageIcon = ImageIO
							.getResourceAsImageIcon("/773927958.png");

					JLabel iconLabel = new JLabel(imageIcon);

					iconLabelPanel.setBounds(panelWidth - 174, 7, 46, 46);
					iconLabelPanel.setOpaque(false);
					iconLabelPanel.add(iconLabel);

					MouseAdapter mouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent evt) {
							/*
							 * 
							 */
							refreshWindow();
						}
					};

					iconLabel.addMouseListener(mouseAdapter);
					iconLabelPanel.addMouseListener(mouseAdapter);

					iconLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
					iconLabelPanel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					/**
					 * Panel added to pane with z-index 1
					 */
					layeredPane.add(iconLabelPanel, new Integer(1), 0);
				}

				{
					JPanel iconLabelPanel = new JPanel();
					iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER,
							0, 0));
					ImageIcon imageIcon = ImageIO
							.getResourceAsImageIcon("/714822614.png");

					JLabel iconLabel = new JLabel(imageIcon);

					iconLabelPanel.setBounds(12, 7, 46, 46);
					iconLabelPanel.setOpaque(false);
					iconLabelPanel.add(iconLabel);

					MouseAdapter mouseAdapter = new MouseAdapter() {
						@Override
						public void mousePressed(MouseEvent evt) {
							/*
							 * 
							 */
							refreshWindow();
						}
					};

					iconLabel.addMouseListener(mouseAdapter);
					iconLabelPanel.addMouseListener(mouseAdapter);

					iconLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));
					iconLabelPanel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					/**
					 * Panel added to pane with z-index 1
					 */
					layeredPane.add(iconLabelPanel, new Integer(1), 0);
				}

				{
					JPanel labelPanel = new JPanel();
					JLabel label = new JLabel(
							ongoingCallConversation == null ? "Echo / Sound Test Service ."
									: ongoingCallConversation.getDisplayName());
					label.setFont(FontIO.SEGOE_UI.deriveFont(15.0f));
					label.setForeground(Color.white);
					labelPanel.setOpaque(false);
					labelPanel.add(label);
					int width = label.getPreferredSize().width;
					int height = label.getPreferredSize().height;
					labelPanel.setBounds(70, 4, width, height + 10);

					label.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					/**
					 * Panel added to pane with z-index 1, above background
					 */
					layeredPane.add(labelPanel, new Integer(1), 0);
				}

				{
					JPanel labelPanel = new JPanel();
					SimpleDateFormat df = new SimpleDateFormat("mm:ss");
					ongoingCallTimeLabel = new JLabel(df.format(new Date(System
							.currentTimeMillis() - ongoingCallStartTime)));
					ongoingCallTimeLabel.setFont(FontIO.SEGOE_UI);
					ongoingCallTimeLabel.setForeground(Color.white);
					labelPanel.setOpaque(false);
					labelPanel.add(ongoingCallTimeLabel);
					int width = ongoingCallTimeLabel.getPreferredSize().width;
					int height = ongoingCallTimeLabel.getPreferredSize().height;
					labelPanel.setBounds(70, 29, width, height + 10);

					ongoingCallTimeLabel.setCursor(Cursor
							.getPredefinedCursor(Cursor.HAND_CURSOR));

					/**
					 * Panel added to pane with z-index 1, above background
					 */
					layeredPane.add(labelPanel, new Integer(1), 0);
				}

				layeredPane.add(panel2, new Integer(0), 0);
			}

			panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			panel.add(layeredPane);

		}

		if (rightPanelPage.equals("Conversation")) {
			panel.setLayout(new JVerticalLayout(0));
			panel.setOpaque(false);

			final int spacerHeight = 12;

			if (selectedConversation == null) {
				return panel;
			}

			if (!(selectedConversation instanceof Contact)) {
				/**
				 * System level message
				 */
				panel.add(Box.createRigidArea(new Dimension(10,
						spacerHeight + 22)));

				if (selectedConversation.hasOutgoingFriendRequest()) {

					JLabel label = new JLabel("Contact request sent");

					label.setFont(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
							11));

					panel.add(label);

					panel.add(Box.createRigidArea(new Dimension(10, 14)));

					JPanel borderLinePanel = new JPanel();
					borderLinePanel.setLayout(new FlowLayout(FlowLayout.LEFT,
							0, 0));
					borderLinePanel.setOpaque(false);

					JLayeredPane layeredPane = new JLayeredPane();
					layeredPane.setBounds(0, 0, panelWidth, 1);
					layeredPane.setPreferredSize(new Dimension(panelWidth, 1));
					layeredPane.setBackground(new Color(211, 230, 234));
					layeredPane.setOpaque(true);

					borderLinePanel.add(layeredPane);

					panel.add(borderLinePanel);

				} else if (selectedConversation.hasIncomingFriendRequest()) {

					JPanel labelPanel = new JPanel();
					labelPanel.setLayout(new BoxLayout(labelPanel,
							BoxLayout.X_AXIS));
					labelPanel.setOpaque(false);

					{

						JLabel label = new JLabel(
								selectedConversation.getDisplayName()
										+ " is not in your Contacts - ");

						label.setFont(FontIO.TAHOMA.deriveFont(
								Font.TRUETYPE_FONT, 11));

						labelPanel.add(label);

					}

					{

						JLabel label = new JLabel("Add to Contacts");
						label.setCursor(Cursor
								.getPredefinedCursor(Cursor.HAND_CURSOR));

						label.addMouseListener(new MouseAdapter() {
							@Override
							public void mousePressed(MouseEvent evt) {
								super.mousePressed(evt);
								Optional<SocketHandlerContext> ctx = Skype
										.getPlugin().createHandle();
								if (ctx.isPresent()) {
									Message incomingFriendRequest = selectedConversation
											.getIncomingFriendRequestMessage()
											.get();
									Optional<PacketPlayInReply> replyPacket = ctx
											.get()
											.getOutboundHandler()
											.dispatch(
													ctx.get(),
													new PacketPlayOutAcceptContactRequest(
															authCode,
															selectedConversation
																	.getUniqueId(),
															incomingFriendRequest
																	.getUniqueId(),
															incomingFriendRequest
																	.getTimestamp()));
									if (replyPacket.isPresent()) {
										if (replyPacket.get().getStatusCode() == 200) {
											long timestamp = System
													.currentTimeMillis();
											UUID messageId = UUID.randomUUID();
											Message message = new Message(
													messageId,
													loggedInUser.getUniqueId(),
													MessageType.ACCEPT_FRIEND_REQUEST,
													"", timestamp);
											replyPacket = ctx
													.get()
													.getOutboundHandler()
													.dispatch(
															ctx.get(),
															new PacketPlayOutSendMessage(
																	authCode,
																	selectedConversation
																			.getUniqueId(),
																	messageId,
																	message,
																	timestamp));
											if (replyPacket.isPresent()) {
												if (replyPacket.get()
														.getStatusCode() == 200) {
													if (!conversations
															.contains(selectedConversation)) {
														conversations
																.add(selectedConversation);
													}
													selectedConversation
															.getMessages().add(
																	message);
													selectedConversation
															.setLastModified(new Date(
																	timestamp));
													selectedConversation
															.setHasIncomingFriendRequest(
																	false, null);
													refreshWindow();
													AudioIO.IM_SENT.playSound();
												}
											}
										}
									}
								}
							}
						});

						label.setFont(FontIO.TAHOMA.deriveFont(
								Font.TRUETYPE_FONT, 11));
						label.setForeground(new Color(0, 149, 204));

						labelPanel.add(label);

					}

					panel.add(labelPanel);

					panel.add(Box.createRigidArea(new Dimension(10, 14)));

					JPanel borderLinePanel = new JPanel();
					borderLinePanel.setLayout(new FlowLayout(FlowLayout.LEFT,
							0, 0));
					borderLinePanel.setOpaque(false);

					JLayeredPane layeredPane = new JLayeredPane();
					layeredPane.setBounds(0, 0, panelWidth, 1);
					layeredPane.setPreferredSize(new Dimension(panelWidth, 1));
					layeredPane.setBackground(new Color(211, 230, 234));
					layeredPane.setOpaque(true);

					borderLinePanel.add(layeredPane);

					panel.add(borderLinePanel);

				} else {

					JLabel label = new JLabel(
							selectedConversation.getDisplayName()
									+ " is not in your Contacts");

					label.setFont(FontIO.TAHOMA.deriveFont(Font.TRUETYPE_FONT,
							11));

					panel.add(label);

					panel.add(Box.createRigidArea(new Dimension(10,
							spacerHeight - 4)));

					CopyOfJButtonRounded addToContactsButton = new CopyOfJButtonRounded(
							new Dimension(170, 29), "Add to Contacts", 35);

					addToContactsButton.setBackground(new Color(35, 170, 226));

					addToContactsButton.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							AddToContactsForm form = new AddToContactsForm(
									frame, selectedConversation,
									new AddToContactsForm.Runnable() {

										@Override
										public void run() {
											String input = this.getInput();
											Optional<SocketHandlerContext> ctx = Skype
													.getPlugin().createHandle();
											if (ctx.isPresent()) {
												Optional<PacketPlayInReply> replyPacket = ctx
														.get()
														.getOutboundHandler()
														.dispatch(
																ctx.get(),
																new PacketPlayOutSendContactRequest(
																		authCode,
																		selectedConversation
																				.getUniqueId()));
												if (replyPacket.isPresent()) {
													System.out
															.println(replyPacket);
													if (replyPacket.get()
															.getStatusCode() == 200) {
														long timestamp = System
																.currentTimeMillis();
														UUID messageId = UUID
																.randomUUID();
														Message message = new Message(
																messageId,
																loggedInUser
																		.getUniqueId(),
																MessageType.SEND_FRIEND_REQUEST,
																input,
																timestamp);
														replyPacket = ctx
																.get()
																.getOutboundHandler()
																.dispatch(
																		ctx.get(),
																		new PacketPlayOutSendMessage(
																				authCode,
																				selectedConversation
																						.getUniqueId(),
																				messageId,
																				message,
																				timestamp));
														if (replyPacket
																.isPresent()) {
															System.out
																	.println(replyPacket);
															if (replyPacket
																	.get()
																	.getStatusCode() == 200) {
																if (!conversations
																		.contains(selectedConversation)) {
																	conversations
																			.add(selectedConversation);
																}
																selectedConversation
																		.getMessages()
																		.add(message);
																selectedConversation
																		.setLastModified(new Date(
																				timestamp));
																selectedConversation
																		.setHasOutgoingFriendRequest(true);
																refreshWindow();
																AudioIO.IM_SENT
																		.playSound();
															}
														}
													}
												}
											}
										}
									});
							form.setLocationRelativeTo(addToContactsButton);
							int x = form.getLocation().x;
							int y = form.getLocation().y
									+ (form.getContentPane().getHeight() / 2)
									+ (addToContactsButton.getHeight() / 2) + 7;
							form.setLocation(x, y);
							form.show();
						}

					});

					panel.add(addToContactsButton);

					panel.add(Box.createRigidArea(new Dimension(10,
							spacerHeight)));

					JPanel borderLinePanel = new JPanel();
					borderLinePanel.setLayout(new FlowLayout(FlowLayout.LEFT,
							0, 0));
					borderLinePanel.setOpaque(false);

					JLayeredPane layeredPane = new JLayeredPane();
					layeredPane.setBounds(0, 0, panelWidth, 1);
					layeredPane.setPreferredSize(new Dimension(panelWidth, 1));
					layeredPane.setBackground(new Color(211, 230, 234));
					layeredPane.setOpaque(true);

					borderLinePanel.add(layeredPane);

					panel.add(borderLinePanel);

				}

			}

			UUID lastSender = null;
			int lastDayOfYear = -1;

			for (Message message : selectedConversation.getMessages()) {

				Calendar cal = Calendar.getInstance();
				Date timestampDate = new Date(message.getTimestamp());
				cal.setTime(timestampDate);
				int dayOfYear = cal.get(Calendar.DAY_OF_YEAR);
				if (lastDayOfYear != dayOfYear) {
					lastDayOfYear = dayOfYear;
					panel.add(Box.createRigidArea(new Dimension(10,
							spacerHeight)));
					JLabel label = new JLabel(
							this.getDateLabelInReferenceToToday(timestampDate));
					label.setFont(font);
					label.setForeground(new Color(0, 149, 204));
					panel.add(label);
					panel.add(Box.createRigidArea(new Dimension(10,
							spacerHeight)));
					lastSender = message.getSender();
				}

				if (message.getSender() == null) {
					/**
					 * System level message
					 */
					panel.add(Box.createRigidArea(new Dimension(10,
							spacerHeight)));

					JPanel messagePanel = new JPanel();
					messagePanel
							.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
					messagePanel.setOpaque(false);

					JLayeredPane layeredPane = new JLayeredPane();
					layeredPane.setOpaque(false);

					{
						JPanel iconLabelPanel = new JPanel();
						iconLabelPanel.setLayout(new FlowLayout(
								FlowLayout.CENTER, 0, 0));
						ImageIcon imageIcon = message.getImageIcon();
						if (imageIcon == null) {
							/**
							 * No system level message icon
							 * 
							 * We will skip to where we display the message
							 */

						} else {
							JLabel iconLabel = new JLabel(imageIcon);

							iconLabelPanel.setBounds(40, 3, 30, 30);
							iconLabelPanel.setOpaque(false);
							iconLabelPanel.add(iconLabel);

							/**
							 * Panel added to pane with z-index 0
							 */
							layeredPane.add(iconLabelPanel, new Integer(0), 0);

						}
					}

					{
						JPanel labelPanel = new JPanel();
						labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0,
								0));
						labelPanel.setOpaque(false);

						final String html = "<html><body style='width: "
								+ (panelWidth - 351) + "px'>%1s</body></html>";

						JLabel label = new JLabel(String.format(html,
								message.getDecryptedMessage()));

						JPopupMenu popUp = new JPopupMenu();
						{
							JMenuItem menuItem = new JMenuItem("Copy Selection");
							menuItem.setEnabled(false);
							popUp.add(menuItem);
						}
						{
							JMenuItem menuItem = new JMenuItem("Copy Message");
							menuItem.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent arg0) {
									StringSelection selection = new StringSelection(
											message.getDecryptedMessage());
									Clipboard clipboard = Toolkit
											.getDefaultToolkit()
											.getSystemClipboard();
									clipboard.setContents(selection, selection);
								}

							});
							popUp.add(menuItem);
						}
						{
							JMenuItem menuItem = new JMenuItem("Select All");
							menuItem.setEnabled(false);
							popUp.add(menuItem);
						}
						{
							popUp.add(new JSeparator());
						}
						{
							JMenuItem menuItem = new JMenuItem("Edit Message");
							menuItem.setEnabled(false);
							popUp.add(menuItem);
						}
						{
							JMenuItem menuItem = new JMenuItem("Remove Message");
							if (message.isDeleted()) {
								menuItem.setEnabled(false);
							} else {
								menuItem.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent arg0) {
										DialogForm form = new DialogForm(
												null,
												"Skype™ - Remove message?",
												"Remove message?",
												"Are you sure you want to remove this message?",
												"Remove", new Runnable() {

													@Override
													public void run() {
														Optional<SocketHandlerContext> ctx = Skype
																.getPlugin()
																.createHandle();
														if (ctx.isPresent()) {
															Optional<PacketPlayInReply> reply = ctx
																	.get()
																	.getOutboundHandler()
																	.dispatch(
																			ctx.get(),
																			new PacketPlayOutLogin(
																					authCode));
															if (!reply
																	.isPresent()
																	|| reply.get()
																			.getStatusCode() != 200) {
																return;
															}
															UUID authCode = UUID
																	.fromString(reply
																			.get()
																			.getText());
															PacketPlayOutRemoveMessage removeMessage = new PacketPlayOutRemoveMessage(
																	authCode,
																	selectedConversation
																			.getUniqueId(),
																	message.getUniqueId(),
																	message.getTimestamp());
															Optional<PacketPlayInReply> replyPacket2 = ctx
																	.get()
																	.getOutboundHandler()
																	.dispatch(
																			ctx.get(),
																			removeMessage);
															if (!replyPacket2
																	.isPresent()) {
																return;
															}
															if (replyPacket2
																	.get()
																	.getStatusCode() != 200) {
																return;
															}
														}
													}

												});
										form.show();
									}
								});
							}
							popUp.add(menuItem);
						}
						{
							JMenuItem menuItem = new JMenuItem(
									"Move Bookmark Here");
							menuItem.setEnabled(false);
							popUp.add(menuItem);
						}
						label.setComponentPopupMenu(popUp);

						MouseAdapter mouseAdapter = new MouseAdapter() {

							@Override
							public void mousePressed(MouseEvent evt) {
								super.mousePressed(evt);
								if (evt.getButton() != MouseEvent.BUTTON1) {
									return;
								}
								String dialogMessage = message.getMessage();
								if (!message.isSignatureVerified()) {
									dialogMessage = "The data could not be verified"
											+ '\r'
											+ '\n'
											+ "The signature does not match the one that is expected"
											+ '\r'
											+ '\n'
											+ '\r'
											+ '\n'
											+ dialogMessage;
								}
								JOptionPane.showMessageDialog(frame,
										dialogMessage);
							}

						};

						if (!message.getDecryptedMessage().equals(
								message.getMessage())) {
							label.addMouseListener(mouseAdapter);
							label.setCursor(Cursor
									.getPredefinedCursor(Cursor.HAND_CURSOR));
						}

						if (message.isDecryptionSuccessful()
								&& !message.isSignatureVerified()) {
							label.setForeground(Color.red);
						}

						label.setFont(font);

						if (message.isDeleted()) {
							label.setFont(label.getFont().deriveFont(
									Font.ITALIC));
						}

						int width = label.getPreferredSize().width;
						int height = label.getPreferredSize().height;

						labelPanel.add(label);

						if (width > panelWidth - 236) {
							width = panelWidth - 236;
						}

						labelPanel.setBounds(107, 8, width, height + 10);

						layeredPane
								.setBounds(0, 0, panelWidth, 8 + height + 10);
						layeredPane.setPreferredSize(new Dimension(panelWidth,
								8 + height + 10));

						/**
						 * Panel added to pane with z-index 1
						 */
						layeredPane.add(labelPanel, new Integer(1), 0);
					}

					{
						JPanel labelPanel = new JPanel();
						labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0,
								0));
						labelPanel.setOpaque(false);

						Date date = new Date(message.getTimestamp());

						String timestamp = twentyFourHourTime.format(date);

						JLabel label = new JLabel(timestamp);
						label.setFont(font);

						int width = label.getPreferredSize().width;
						int height = label.getPreferredSize().height;

						labelPanel.add(label);

						labelPanel.setBounds(panelWidth - 56, 8, width,
								height + 10);

						/**
						 * Panel added to pane with z-index 1
						 */
						layeredPane.add(labelPanel, new Integer(1), 0);
					}

					messagePanel.add(layeredPane);

					panel.add(messagePanel);

					lastSender = null;
				} else {
					/**
					 * User level message
					 */

					if (message.getMessageType() != null) {

						/**
						 * Friend request
						 */

						if (message.getMessageType() == MessageType.ACCEPT_FRIEND_REQUEST) {
							panel.add(Box
									.createRigidArea(new Dimension(10, 14)));

							{
								String first = loggedInUser.getDisplayName();
								String second = selectedConversation
										.getDisplayName();
								if (message.getSender().equals(
										selectedConversation.getUniqueId())) {
									first = selectedConversation
											.getDisplayName();
									second = loggedInUser.getDisplayName();
								}

								JPanel labelPanel = new JPanel();
								labelPanel.setLayout(new BoxLayout(labelPanel,
										BoxLayout.X_AXIS));
								labelPanel.setOpaque(false);

								{

									JLabel label = new JLabel(first);
									label.setFont(font
											.deriveFont(Font.BOLD, 12));
									labelPanel.add(label);

								}

								{

									JLabel label = new JLabel(
											" has shared contact details with ");
									label.setFont(font.deriveFont(
											Font.TRUETYPE_FONT, 12));
									labelPanel.add(label);

								}

								{

									JLabel label = new JLabel(second);
									label.setFont(font
											.deriveFont(Font.BOLD, 12));
									labelPanel.add(label);

								}

								{

									JLabel label = new JLabel(".");
									label.setFont(font.deriveFont(
											Font.TRUETYPE_FONT, 12));
									labelPanel.add(label);

								}

								panel.add(labelPanel);
							}

							panel.add(Box.createRigidArea(new Dimension(10, 1)));

							{
								JPanel labelPanel = new JPanel();
								labelPanel.setOpaque(false);

								Date date = new Date(message.getTimestamp());

								String timestamp = twentyFourHourTime
										.format(date);

								JLabel label = new JLabel(timestamp);
								label.setFont(font);

								labelPanel.add(label);

								/**
								 * Panel added to pane with z-index 1
								 */
								panel.add(labelPanel);
							}

							panel.add(Box
									.createRigidArea(new Dimension(10, 14)));

							continue;

						}

						if (message.getMessageType() == MessageType.SEND_FRIEND_REQUEST) {

							panel.add(Box
									.createRigidArea(new Dimension(10, 14)));

							{
								JPanel labelPanel = new JPanel();
								labelPanel.setLayout(new BoxLayout(labelPanel,
										BoxLayout.X_AXIS));
								labelPanel.setOpaque(false);

								{
									String name = loggedInUser.getDisplayName();
									if (message.getSender().equals(
											selectedConversation.getUniqueId())) {
										name = selectedConversation
												.getDisplayName();
									}
									JLabel label = new JLabel(name);
									label.setFont(font
											.deriveFont(Font.BOLD, 12));
									labelPanel.add(label);
								}
								{
									JLabel label = new JLabel(
											" would like to add you on Skype");
									label.setFont(font.deriveFont(
											Font.TRUETYPE_FONT, 12));

									labelPanel.add(label);
								}
								panel.add(labelPanel);
							}

							panel.add(Box.createRigidArea(new Dimension(10,
									spacerHeight + 10)));

							{
								JPanel labelPanel = new JPanel();
								labelPanel.setLayout(new BoxLayout(labelPanel,
										BoxLayout.X_AXIS));
								labelPanel.setOpaque(false);

								JLabel label = new JLabel(
										message.getDecryptedMessage());

								JPopupMenu popUp = new JPopupMenu();
								{
									JMenuItem menuItem = new JMenuItem(
											"Copy Selection");
									menuItem.setEnabled(false);
									popUp.add(menuItem);
								}
								{
									JMenuItem menuItem = new JMenuItem(
											"Copy Message");
									menuItem.addActionListener(new ActionListener() {

										@Override
										public void actionPerformed(
												ActionEvent arg0) {
											StringSelection selection = new StringSelection(
													message.getDecryptedMessage());
											Clipboard clipboard = Toolkit
													.getDefaultToolkit()
													.getSystemClipboard();
											clipboard.setContents(selection,
													selection);
										}

									});
									popUp.add(menuItem);
								}
								{
									JMenuItem menuItem = new JMenuItem(
											"Select All");
									menuItem.setEnabled(false);
									popUp.add(menuItem);
								}
								{
									popUp.add(new JSeparator());
								}
								{
									JMenuItem menuItem = new JMenuItem(
											"Edit Message");
									menuItem.setEnabled(false);
									popUp.add(menuItem);
								}
								{
									JMenuItem menuItem = new JMenuItem(
											"Remove Message");
									if (message.isDeleted()) {
										menuItem.setEnabled(false);
									} else {
										menuItem.addActionListener(new ActionListener() {

											@Override
											public void actionPerformed(
													ActionEvent arg0) {
												DialogForm form = new DialogForm(
														null,
														"Skype™ - Remove message?",
														"Remove message?",
														"Are you sure you want to remove this message?",
														"Remove",
														new Runnable() {

															@Override
															public void run() {
																Optional<SocketHandlerContext> ctx = Skype
																		.getPlugin()
																		.createHandle();
																if (ctx.isPresent()) {
																	Optional<PacketPlayInReply> reply = ctx
																			.get()
																			.getOutboundHandler()
																			.dispatch(
																					ctx.get(),
																					new PacketPlayOutLogin(
																							authCode));
																	if (!reply
																			.isPresent()
																			|| reply.get()
																					.getStatusCode() != 200) {
																		return;
																	}
																	UUID authCode = UUID
																			.fromString(reply
																					.get()
																					.getText());
																	PacketPlayOutRemoveMessage removeMessage = new PacketPlayOutRemoveMessage(
																			authCode,
																			selectedConversation
																					.getUniqueId(),
																			message.getUniqueId(),
																			message.getTimestamp());
																	Optional<PacketPlayInReply> replyPacket2 = ctx
																			.get()
																			.getOutboundHandler()
																			.dispatch(
																					ctx.get(),
																					removeMessage);
																	if (!replyPacket2
																			.isPresent()) {
																		return;
																	}
																	if (replyPacket2
																			.get()
																			.getStatusCode() != 200) {
																		return;
																	}
																}
															}

														});
												form.show();
											}
										});
									}
									popUp.add(menuItem);
								}
								{
									JMenuItem menuItem = new JMenuItem(
											"Move Bookmark Here");
									menuItem.setEnabled(false);
									popUp.add(menuItem);
								}
								label.setComponentPopupMenu(popUp);

								MouseAdapter mouseAdapter = new MouseAdapter() {

									@Override
									public void mousePressed(MouseEvent evt) {
										super.mousePressed(evt);
										if (evt.getButton() != MouseEvent.BUTTON1) {
											return;
										}
										String dialogMessage = message
												.getMessage();
										if (!message.isSignatureVerified()) {
											dialogMessage = "The data could not be verified"
													+ '\r'
													+ '\n'
													+ "The signature does not match the one that is expected"
													+ '\r'
													+ '\n'
													+ '\r'
													+ '\n'
													+ dialogMessage;
										}
										JOptionPane.showMessageDialog(frame,
												dialogMessage);
									}

								};

								if (!message.getDecryptedMessage().equals(
										message.getMessage())) {
									label.addMouseListener(mouseAdapter);
									label.setCursor(Cursor
											.getPredefinedCursor(Cursor.HAND_CURSOR));
								}

								if (message.isDecryptionSuccessful()
										&& !message.isSignatureVerified()) {
									label.setForeground(Color.red);
								}

								label.setFont(font.deriveFont(
										Font.TRUETYPE_FONT, 12));
								if (message.isDeleted()) {
									label.setFont(label.getFont().deriveFont(
											Font.ITALIC));
								}
								labelPanel.add(label);

								panel.add(labelPanel);
							}

							panel.add(Box.createRigidArea(new Dimension(10, 1)));

							{
								JPanel labelPanel = new JPanel();
								labelPanel.setOpaque(false);

								Date date = new Date(message.getTimestamp());

								String timestamp = twentyFourHourTime
										.format(date);

								JLabel label = new JLabel(timestamp);
								label.setFont(font);

								labelPanel.add(label);

								/**
								 * Panel added to pane with z-index 1
								 */
								panel.add(labelPanel);
							}

							if (message.getSender().equals(
									loggedInUser.getUniqueId())
									|| message.isDeleted()) {
							} else if (!(selectedConversation instanceof Contact)) {
								panel.add(Box.createRigidArea(new Dimension(0,
										8)));

								JPanel buttonPanel = new JPanel();
								buttonPanel.setOpaque(false);
								buttonPanel.setLayout(new BoxLayout(
										buttonPanel, BoxLayout.X_AXIS));

								{
									CopyOfJButtonRounded button = new CopyOfJButtonRounded(
											new Dimension(113, 29), "Accept",
											35);

									button.addActionListener(new ActionListener() {

										@Override
										public void actionPerformed(
												ActionEvent arg0) {
											Optional<SocketHandlerContext> ctx = Skype
													.getPlugin().createHandle();
											if (ctx.isPresent()) {
												Message incomingFriendRequest = selectedConversation
														.getIncomingFriendRequestMessage()
														.get();
												Optional<PacketPlayInReply> replyPacket = ctx
														.get()
														.getOutboundHandler()
														.dispatch(
																ctx.get(),
																new PacketPlayOutAcceptContactRequest(
																		authCode,
																		selectedConversation
																				.getUniqueId(),
																		incomingFriendRequest
																				.getUniqueId(),
																		incomingFriendRequest
																				.getTimestamp()));
												if (replyPacket.isPresent()) {
													System.out
															.println(replyPacket);
													if (replyPacket.get()
															.getStatusCode() == 200) {
														long timestamp = System
																.currentTimeMillis();
														UUID messageId = UUID
																.randomUUID();
														Message message = new Message(
																messageId,
																loggedInUser
																		.getUniqueId(),
																MessageType.ACCEPT_FRIEND_REQUEST,
																"", timestamp);
														replyPacket = ctx
																.get()
																.getOutboundHandler()
																.dispatch(
																		ctx.get(),
																		new PacketPlayOutSendMessage(
																				authCode,
																				selectedConversation
																						.getUniqueId(),
																				messageId,
																				message,
																				timestamp));
														if (replyPacket
																.isPresent()) {
															System.out
																	.println(replyPacket);
															if (replyPacket
																	.get()
																	.getStatusCode() == 200) {
																if (!conversations
																		.contains(selectedConversation)) {
																	conversations
																			.add(selectedConversation);
																}
																selectedConversation
																		.getMessages()
																		.add(message);
																selectedConversation
																		.setLastModified(new Date(
																				timestamp));
																selectedConversation
																		.setHasIncomingFriendRequest(
																				false,
																				null);
																refreshWindow();
																AudioIO.IM_SENT
																		.playSound();
															}
														}
													}
												}
											}
										}

									});

									button.setBackground(new Color(35, 170, 226));

									buttonPanel.add(button);
								}

								buttonPanel.add(Box
										.createRigidArea(new Dimension(15, 1)));

								{
									Dimension buttonSize = new Dimension(113,
											29);
									CopyOfJButtonRounded button = new CopyOfJButtonRounded(
											buttonSize, "Decline", 35);

									button.addActionListener(new ActionListener() {

										@Override
										public void actionPerformed(
												ActionEvent arg0) {
											Optional<SocketHandlerContext> ctx = Skype
													.getPlugin().createHandle();
											if (ctx.isPresent()) {
												Message incomingFriendRequest = selectedConversation
														.getIncomingFriendRequestMessage()
														.get();
												Optional<PacketPlayInReply> replyPacket = ctx
														.get()
														.getOutboundHandler()
														.dispatch(
																ctx.get(),
																new PacketPlayOutDeclineContactRequest(
																		authCode,
																		selectedConversation
																				.getUniqueId(),
																		incomingFriendRequest
																				.getUniqueId(),
																		incomingFriendRequest
																				.getTimestamp()));
												if (replyPacket.isPresent()) {
													System.out
															.println(replyPacket);
													if (replyPacket.get()
															.getStatusCode() == 200) {
														selectedConversation
																.setLastModified(new Date());
														selectedConversation
																.setHasIncomingFriendRequest(
																		false,
																		null);
														refreshWindow();
														AudioIO.IM_SENT
																.playSound();
													}
												}
											}
										}

									});

									button.setBackground(new Color(102, 207,
											246));

									buttonPanel.add(button);
								}
								panel.add(buttonPanel);
							}

							panel.add(Box
									.createRigidArea(new Dimension(10, 14)));

						}

						continue;

					}

					JPanel messagePanel = new JPanel();
					messagePanel
							.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
					messagePanel.setOpaque(false);

					JLayeredPane layeredPane = new JLayeredPane();
					layeredPane.setOpaque(false);

					if (lastSender == null
							|| !lastSender.equals(message.getSender())) {
						panel.add(Box.createRigidArea(new Dimension(10,
								spacerHeight)));
						lastSender = message.sender;
						if (!message.sender.equals(loggedInUser.getUniqueId())) {
							{
								JPanel iconLabelPanel = new JPanel();
								iconLabelPanel.setLayout(new FlowLayout(
										FlowLayout.CENTER, 0, 0));
								ImageIcon imageIcon = message.getImageIcon();
								imageIcon = ImageIO.getScaledImageIcon(
										imageIcon, new Dimension(36, 36));
								imageIcon = ImageIO
										.getCircularImageIcon(imageIcon);
								JLabel iconLabel = new JLabel(imageIcon);

								/**
								 * Reserved for future use
								 */
								iconLabel
										.setCursor(Cursor
												.getPredefinedCursor(Cursor.HAND_CURSOR));

								iconLabelPanel.setBounds(37, 0, 36, 36);
								iconLabelPanel.setOpaque(false);
								iconLabelPanel.add(iconLabel);

								/**
								 * Panel added to pane with z-index 0
								 */
								layeredPane.add(iconLabelPanel, new Integer(0),
										0);
							}
						}
					}

					if (message.sender.equals(loggedInUser.getUniqueId())) {

						JPanel backgroundPanel = new JPanel();

						{
							backgroundPanel.setLayout(new FlowLayout(
									FlowLayout.CENTER, 0, 0));
							backgroundPanel.setBackground(new Color(229, 247,
									253));
						}

						{
							JPanel labelPanel = new JPanel();
							labelPanel.setLayout(new FlowLayout(
									FlowLayout.LEFT, 0, 0));
							labelPanel.setOpaque(false);

							final String html = "<html><body style='width: "
									+ (panelWidth - 351)
									+ "px'>%1s</body></html>";

							JLabel label = new JLabel(String.format(html,
									message.getDecryptedMessage()));

							JPopupMenu popUp = new JPopupMenu();
							{
								JMenuItem menuItem = new JMenuItem(
										"Copy Selection");
								menuItem.setEnabled(false);
								popUp.add(menuItem);
							}
							{
								JMenuItem menuItem = new JMenuItem(
										"Copy Message");
								menuItem.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent arg0) {
										StringSelection selection = new StringSelection(
												message.getDecryptedMessage());
										Clipboard clipboard = Toolkit
												.getDefaultToolkit()
												.getSystemClipboard();
										clipboard.setContents(selection,
												selection);
									}

								});
								popUp.add(menuItem);
							}
							{
								JMenuItem menuItem = new JMenuItem("Select All");
								menuItem.setEnabled(false);
								popUp.add(menuItem);
							}
							{
								popUp.add(new JSeparator());
							}
							{
								JMenuItem menuItem = new JMenuItem(
										"Edit Message");
								menuItem.setEnabled(false);
								popUp.add(menuItem);
							}
							{
								JMenuItem menuItem = new JMenuItem(
										"Remove Message");
								if (message.isDeleted()) {
									menuItem.setEnabled(false);
								} else {
									menuItem.addActionListener(new ActionListener() {

										@Override
										public void actionPerformed(
												ActionEvent arg0) {
											DialogForm form = new DialogForm(
													null,
													"Skype™ - Remove message?",
													"Remove message?",
													"Are you sure you want to remove this message?",
													"Remove", new Runnable() {

														@Override
														public void run() {
															Optional<SocketHandlerContext> ctx = Skype
																	.getPlugin()
																	.createHandle();
															if (ctx.isPresent()) {
																Optional<PacketPlayInReply> reply = ctx
																		.get()
																		.getOutboundHandler()
																		.dispatch(
																				ctx.get(),
																				new PacketPlayOutLogin(
																						authCode));
																if (!reply
																		.isPresent()
																		|| reply.get()
																				.getStatusCode() != 200) {
																	return;
																}
																UUID authCode = UUID
																		.fromString(reply
																				.get()
																				.getText());
																PacketPlayOutRemoveMessage removeMessage = new PacketPlayOutRemoveMessage(
																		authCode,
																		selectedConversation
																				.getUniqueId(),
																		message.getUniqueId(),
																		message.getTimestamp());
																Optional<PacketPlayInReply> replyPacket2 = ctx
																		.get()
																		.getOutboundHandler()
																		.dispatch(
																				ctx.get(),
																				removeMessage);
																if (!replyPacket2
																		.isPresent()) {
																	return;
																}
																if (replyPacket2
																		.get()
																		.getStatusCode() != 200) {
																	return;
																}
															}
														}

													});
											form.show();
										}
									});
								}
								popUp.add(menuItem);
							}
							{
								JMenuItem menuItem = new JMenuItem(
										"Move Bookmark Here");
								menuItem.setEnabled(false);
								popUp.add(menuItem);
							}
							label.setComponentPopupMenu(popUp);

							MouseAdapter mouseAdapter = new MouseAdapter() {

								@Override
								public void mousePressed(MouseEvent evt) {
									super.mousePressed(evt);
									if (evt.getButton() != MouseEvent.BUTTON1) {
										return;
									}
									String dialogMessage = message.getMessage();
									if (!message.isSignatureVerified()) {
										dialogMessage = "The data could not be verified"
												+ '\r'
												+ '\n'
												+ "The signature does not match the one that is expected"
												+ '\r'
												+ '\n'
												+ '\r'
												+ '\n'
												+ dialogMessage;
									}
									JOptionPane.showMessageDialog(frame,
											dialogMessage);
								}

							};

							if (!message.getDecryptedMessage().equals(
									message.getMessage())) {
								label.addMouseListener(mouseAdapter);
								label.setCursor(Cursor
										.getPredefinedCursor(Cursor.HAND_CURSOR));
							}

							if (message.isDecryptionSuccessful()
									&& !message.isSignatureVerified()) {
								label.setForeground(Color.red);
							}

							label.setBorder(BorderFactory.createEmptyBorder());
							label.setFont(font);

							if (message.isDeleted()) {
								label.setFont(label.getFont().deriveFont(
										Font.ITALIC));
							}

							int width = label.getPreferredSize().width;
							int height = label.getPreferredSize().height;

							labelPanel.add(label);

							if (width > panelWidth - 236) {
								width = panelWidth - 236;
							}

							labelPanel.setBounds(143, 8, width, height + 10);

							backgroundPanel.setBounds(127, 0, panelWidth - 204,
									8 + height + 10);

							layeredPane.setBounds(0, 0, panelWidth,
									8 + height + 10);
							layeredPane.setPreferredSize(new Dimension(
									panelWidth, 8 + height + 10));

							/**
							 * Panel added to pane with z-index 0
							 */
							layeredPane.add(backgroundPanel, new Integer(0), 0);

							/**
							 * Panel added to pane with z-index 1
							 */
							layeredPane.add(labelPanel, new Integer(1), 0);
						}

						{
							JPanel labelPanel = new JPanel();
							labelPanel.setLayout(new FlowLayout(
									FlowLayout.LEFT, 0, 0));
							labelPanel.setOpaque(false);

							Date date = new Date(message.getTimestamp());

							String timestamp = twentyFourHourTime.format(date);

							JLabel label = new JLabel(timestamp);
							label.setFont(font);

							int width = label.getPreferredSize().width;
							int height = label.getPreferredSize().height;

							labelPanel.add(label);

							labelPanel.setBounds(panelWidth - 56, 8, width,
									height + 10);

							/**
							 * Panel added to pane with z-index 1
							 */
							layeredPane.add(labelPanel, new Integer(1), 0);
						}

					} else {
						JPanel backgroundPanel = new JPanel();

						{
							backgroundPanel.setLayout(new FlowLayout(
									FlowLayout.CENTER, 0, 0));
							backgroundPanel.setBackground(new Color(199, 237,
									252));
						}

						{
							JPanel labelPanel = new JPanel();
							labelPanel.setLayout(new FlowLayout(
									FlowLayout.LEFT, 0, 0));
							labelPanel.setOpaque(false);

							final String html = "<html><body style='width: "
									+ (panelWidth - 351)
									+ "px'>%1s</body></html>";

							JLabel label = new JLabel(String.format(html,
									message.getDecryptedMessage()));

							JPopupMenu popUp = new JPopupMenu();
							{
								JMenuItem menuItem = new JMenuItem(
										"Copy Selection");
								menuItem.setEnabled(false);
								popUp.add(menuItem);
							}
							{
								JMenuItem menuItem = new JMenuItem(
										"Copy Message");
								menuItem.addActionListener(new ActionListener() {

									@Override
									public void actionPerformed(ActionEvent arg0) {
										StringSelection selection = new StringSelection(
												message.getDecryptedMessage());
										Clipboard clipboard = Toolkit
												.getDefaultToolkit()
												.getSystemClipboard();
										clipboard.setContents(selection,
												selection);
									}

								});
								popUp.add(menuItem);
							}
							{
								JMenuItem menuItem = new JMenuItem("Select All");
								menuItem.setEnabled(false);
								popUp.add(menuItem);
							}
							{
								popUp.add(new JSeparator());
							}
							{
								JMenuItem menuItem = new JMenuItem(
										"Edit Message");
								menuItem.setEnabled(false);
								popUp.add(menuItem);
							}
							{
								JMenuItem menuItem = new JMenuItem(
										"Remove Message");
								if (message.isDeleted()) {
									menuItem.setEnabled(false);
								} else {
									menuItem.addActionListener(new ActionListener() {

										@Override
										public void actionPerformed(
												ActionEvent arg0) {
											DialogForm form = new DialogForm(
													null,
													"Skype™ - Remove message?",
													"Remove message?",
													"Are you sure you want to remove this message?",
													"Remove", new Runnable() {

														@Override
														public void run() {
															Optional<SocketHandlerContext> ctx = Skype
																	.getPlugin()
																	.createHandle();
															if (ctx.isPresent()) {
																Optional<PacketPlayInReply> reply = ctx
																		.get()
																		.getOutboundHandler()
																		.dispatch(
																				ctx.get(),
																				new PacketPlayOutLogin(
																						authCode));
																if (!reply
																		.isPresent()
																		|| reply.get()
																				.getStatusCode() != 200) {
																	return;
																}
																UUID authCode = UUID
																		.fromString(reply
																				.get()
																				.getText());
																PacketPlayOutRemoveMessage removeMessage = new PacketPlayOutRemoveMessage(
																		authCode,
																		selectedConversation
																				.getUniqueId(),
																		message.getUniqueId(),
																		message.getTimestamp());
																Optional<PacketPlayInReply> replyPacket2 = ctx
																		.get()
																		.getOutboundHandler()
																		.dispatch(
																				ctx.get(),
																				removeMessage);
																if (!replyPacket2
																		.isPresent()) {
																	return;
																}
																if (replyPacket2
																		.get()
																		.getStatusCode() != 200) {
																	return;
																}
															}
														}

													});
											form.show();
										}
									});
								}
								popUp.add(menuItem);
							}
							{
								JMenuItem menuItem = new JMenuItem(
										"Move Bookmark Here");
								menuItem.setEnabled(false);
								popUp.add(menuItem);
							}
							label.setComponentPopupMenu(popUp);

							MouseAdapter mouseAdapter = new MouseAdapter() {

								@Override
								public void mousePressed(MouseEvent evt) {
									super.mousePressed(evt);
									if (evt.getButton() != MouseEvent.BUTTON1) {
										return;
									}
									String dialogMessage = message.getMessage();
									if (!message.isSignatureVerified()) {
										dialogMessage = "The data could not be verified"
												+ '\r'
												+ '\n'
												+ "The signature does not match the one that is expected"
												+ '\r'
												+ '\n'
												+ '\r'
												+ '\n'
												+ dialogMessage;
									}
									JOptionPane.showMessageDialog(frame,
											dialogMessage);
								}

							};

							if (!message.getDecryptedMessage().equals(
									message.getMessage())) {
								label.addMouseListener(mouseAdapter);
								label.setCursor(Cursor
										.getPredefinedCursor(Cursor.HAND_CURSOR));
							}

							if (message.isDecryptionSuccessful()
									&& !message.isSignatureVerified()) {
								label.setForeground(Color.red);
							}

							label.setFont(font);

							if (message.isDeleted()) {
								label.setFont(label.getFont().deriveFont(
										Font.ITALIC));
							}

							int width = label.getPreferredSize().width;
							int height = label.getPreferredSize().height;

							labelPanel.add(label);

							if (width > panelWidth - 236) {
								width = panelWidth - 236;
							}

							labelPanel.setBounds(107, 8, width, height + 10);

							backgroundPanel.setBounds(91, 0, panelWidth - 204,
									8 + height + 10);

							layeredPane.setBounds(0, 0, panelWidth,
									8 + height + 10);
							layeredPane.setPreferredSize(new Dimension(
									panelWidth, 8 + height + 10));

							/**
							 * Panel added to pane with z-index 0
							 */
							layeredPane.add(backgroundPanel, new Integer(0), 0);

							/**
							 * Panel added to pane with z-index 1
							 */
							layeredPane.add(labelPanel, new Integer(1), 0);
						}

						{
							JPanel labelPanel = new JPanel();
							labelPanel.setLayout(new FlowLayout(
									FlowLayout.LEFT, 0, 0));
							labelPanel.setOpaque(false);

							Date date = new Date(message.getTimestamp());

							String timestamp = twentyFourHourTime.format(date);

							JLabel label = new JLabel(timestamp);
							label.setFont(font);

							int width = label.getPreferredSize().width;
							int height = label.getPreferredSize().height;

							labelPanel.add(label);

							labelPanel.setBounds(panelWidth - 56, 8, width,
									height + 10);

							/**
							 * Panel added to pane with z-index 1
							 */
							layeredPane.add(labelPanel, new Integer(1), 0);
						}
					}

					messagePanel.add(layeredPane);

					panel.add(messagePanel);
				}

			}

			panel.add(Box.createRigidArea(new Dimension(10, spacerHeight)));

		}

		return panel;
	}

	private JLayeredPane createRightBottomBottomLayeredPane(int panelWidth,
			int panelHeight) {
		/* Start right bottom bottom panel */
		/**
		 * Construct layered pane for absolute positioning with z index
		 */
		JLayeredPane rightBottomBottomLayeredPane = new JLayeredPane();
		rightBottomBottomLayeredPane.setBounds(0, 0, panelWidth, panelHeight);
		rightBottomBottomLayeredPane.setPreferredSize(new Dimension(panelWidth,
				panelHeight));
		rightBottomBottomLayeredPane.setOpaque(false);

		if (selectedConversation == null) {
			/**
			 * The selected right panel page is not Conversation
			 * 
			 * We want the right panel to be just a JScrollPane in this case
			 * 
			 * So we will not put a right bottom bottom layered pane in this
			 * case
			 */
			return rightBottomBottomLayeredPane;
		}

		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/1173550536.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			iconLabelPanel.setBounds(37, 21, 36, 36);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 0
			 */
			rightBottomBottomLayeredPane.add(iconLabelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();

			labelPanel.setOpaque(false);

			JLabel label = new JLabel("via");

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);
			labelPanel.setBounds(92, -3, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			rightBottomBottomLayeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel labelPanel = new JPanel();

			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Skype");
			label.setForeground(new Color(0, 149, 204));

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);
			labelPanel.setBounds(110, -3, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			rightBottomBottomLayeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/1206468417.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			iconLabelPanel.setBounds(126, 1, 28, 18);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 0
			 */
			rightBottomBottomLayeredPane.add(iconLabelPanel, new Integer(0), 0);
		}

		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new BoxLayout(iconLabelPanel,
					BoxLayout.X_AXIS));
			iconLabelPanel.setBounds(91, 21, panelWidth - 195, 36);
			iconLabelPanel.setOpaque(true);
			iconLabelPanel.setBackground(new Color(229, 247, 253));
			iconLabelPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1,
					1, new Color(102, 207, 246)));
			iconLabelPanel.add(Box.createRigidArea(new Dimension(15, 15)));

			JTextField conversationTextField = new JTextField();
			conversationTextField.setBorder(BorderFactory.createEmptyBorder());
			conversationTextField.setPreferredSize(new Dimension(
					panelWidth - 180, 36));
			conversationTextField.setOpaque(false);
			conversationTextField.setFont(font);
			KeyStroke enterKeyStroke = KeyStroke.getKeyStroke(
					KeyEvent.VK_ENTER, 0);
			conversationTextField.getKeymap().removeKeyStrokeBinding(
					enterKeyStroke);
			conversationTextField.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (conversationTextField.getText().trim().length() == 0) {
						return;
					}
					Optional<SocketHandlerContext> ctx = Skype.getPlugin()
							.createHandle();
					if (!ctx.isPresent()) {
						return;
					}
					UUID messageId = UUID.randomUUID();
					long timestamp = System.currentTimeMillis();
					Message message = new Message(messageId, loggedInUser
							.getUniqueId(), PGPUtilities.encryptAndSign(
							conversationTextField.getText().trim(),
							selectedConversation), timestamp);
					if (!conversations.contains(selectedConversation)) {
						conversations.add(selectedConversation);
					}
					UUID conversationId = selectedConversation.getUniqueId();
					Optional<PacketPlayInReply> replyPacket = ctx
							.get()
							.getOutboundHandler()
							.dispatch(
									ctx.get(),
									new PacketPlayOutSendMessage(authCode,
											conversationId, messageId, message
													.toString(), timestamp));
					if (!replyPacket.isPresent()) {
						return;
					}
					if (replyPacket.get().getStatusCode() == 200) {
						selectedConversation.getMessages().add(message);
						selectedConversation.setLastModified(new Date());
						refreshWindow();
						AudioIO.IM_SENT.playSound();
					}
				}

			});

			iconLabelPanel.add(conversationTextField);

			/**
			 * Panel added to pane with z-index 1
			 */
			rightBottomBottomLayeredPane.add(iconLabelPanel, new Integer(1), 0);
		}

		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/1235097568.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			iconLabelPanel.setBounds(panelWidth - 130, 31, 16, 16);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			rightBottomBottomLayeredPane.add(iconLabelPanel, new Integer(1), 0);
		}

		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/1149538990.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			iconLabelPanel.setBounds(panelWidth - 104, 21, 36, 36);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			rightBottomBottomLayeredPane.add(iconLabelPanel, new Integer(1), 0);
		}

		return rightBottomBottomLayeredPane;
	}

	private void selectConversation(Conversation conversation) {
		if (conversation == null) {
			rightPanelPage = "";
			selectedConversation = null;
		} else {
			if (ongoingCall
					&& conversation.getUniqueId().equals(
							ongoingCallConversation.getUniqueId())) {
				rightPanelPage = "OngoingCall";
			} else {
				rightPanelPage = "Conversation";
			}
			conversation.setNotificationCount(0);
			selectedConversation = conversation;
		}
		refreshWindow(true);
		rightBottomTopPanel.getVerticalScrollBar().setValue(
				rightBottomTopPanel.getVerticalScrollBar().getMaximum());
	}

	public void refreshWindow() {
		refreshWindow(false);
	}

	public void refreshWindow(boolean scrollToBottom) {
		if (selectedConversation != null) {
			selectedConversation.setNotificationCount(0);
		}

		refreshLeftBottomPanel();
		refreshLeftTopPanel();
		refreshRightTopPanel();

		/*
		 * We need to add a 100ms delay to refresh the right bottom panel
		 */
		Timer timer = new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				((Timer) evt.getSource()).stop();
				refreshRightBottomPanel(scrollToBottom);
			}

		});
		timer.restart();
	}

	public void refreshLeftTopPanel() {
		int width = splitPane.getDividerLocation();

		/**
		 * We must remove the top left layered pane from the panel
		 */
		leftTopPanel.removeAll();

		/**
		 * Now that the width of the panel has changed
		 * 
		 * We must resize the components inside to compensate
		 */
		JLayeredPane leftTopLayeredPane = createLeftTopLayeredPane(width);

		/**
		 * Now we can add back the new top left layered pane
		 */
		leftTopPanel.add(leftTopLayeredPane);

		leftTopPanel.validate();
		leftTopPanel.repaint();
	}

	public void refreshLeftBottomPanel() {
		int width = splitPane.getDividerLocation();

		int verticalScrollBarPosition = leftBottomScrollPane
				.getVerticalScrollBar().getValue();

		/**
		 * We construct the left bottom panel using the right width
		 */
		JPanel leftBottomPanel = createLeftBottomPanel(width - scrollBarWidth
				+ 5);

		/**
		 * We now update the view on the left bottom scroll pane
		 */
		leftBottomScrollPane.setViewportView(leftBottomPanel);

		leftBottomScrollPane.validate();
		leftBottomScrollPane.repaint();

		leftBottomScrollPane.getVerticalScrollBar().setValue(
				verticalScrollBarPosition);
	}

	public void refreshRightTopPanel() {
		if (rightPanelPage.equals("Conversation")) {
			rightTopPanel.setVisible(true);
			rightTopPanel.removeAll();

			int leftSplitPaneWidth = leftSplitPane.getDividerLocation();
			if (leftSplitPaneWidth == 0 || leftSplitPaneWidth == -1) {
				leftSplitPaneWidth = this.defaultLeftPanelWidth;
			}

			JLayeredPane rightTopLayeredPane = this.createRightTopLayeredPane(
					getContentPane().getSize().width
							- this.splitPaneDividerSize - leftSplitPaneWidth,
					this.defaultRightTopPanelHeight);

			rightTopPanel.add(rightTopLayeredPane);

			rightTopPanel.validate();
			rightTopPanel.repaint();
		} else {
			rightTopPanel.setVisible(false);
		}
	}

	public void refreshRightBottomPanel(boolean scrollToBottom) {
		this.rightBottomBottomPanel.removeAll();

		int leftSplitPaneWidth = leftSplitPane.getDividerLocation();
		if (leftSplitPaneWidth == 0 || leftSplitPaneWidth == -1) {
			leftSplitPaneWidth = this.defaultLeftPanelWidth;
		}

		int panelWidth = getContentPane().getSize().width
				- this.splitPaneDividerSize - leftSplitPaneWidth;

		int extent = rightBottomTopPanel.getVerticalScrollBar().getModel()
				.getExtent();
		boolean maximum = (rightBottomTopPanel.getVerticalScrollBar()
				.getValue() + extent) == rightBottomTopPanel
				.getVerticalScrollBar().getMaximum();

		JPanel rightBottomTopPanel = this.createRightBottomTopPanel(panelWidth
				- this.scrollBarWidth - 5);

		int verticalScrollBarPosition = this.rightBottomTopPanel
				.getVerticalScrollBar().getValue();

		if (maximum) {
			verticalScrollBarPosition = this.rightBottomTopPanel
					.getVerticalScrollBar().getMaximum();
		}

		this.rightBottomTopPanel.setViewportView(rightBottomTopPanel);

		this.rightBottomTopPanel.getVerticalScrollBar().setValue(
				verticalScrollBarPosition);

		if (rightPanelPage.equals("Conversation")) {

			this.rightBottomBottomPanel.setVisible(true);

			JLayeredPane rightBottomBottomPanel = this
					.createRightBottomBottomLayeredPane(panelWidth,
							this.defaultRightBottomBottomPanelHeight);
			this.rightBottomBottomPanel.add(rightBottomBottomPanel);

		} else {

			this.rightBottomBottomPanel.setVisible(false);
		}

		rightBottomSplitPane.validate();

		if (scrollToBottom) {
			JScrollBar vertical = this.rightBottomTopPanel
					.getVerticalScrollBar();
			vertical.setValue(vertical.getMaximum());
		}

		rightBottomSplitPane.repaint();

		rightBottomSplitPane
				.setDividerLocation(getContentPane().getSize().height
						- defaultRightBottomBottomPanelHeight
						- defaultRightTopPanelHeight);
	}

	public void readFromMemory() {
		conversations.clear();
		SocketHandlerContext ctx = Skype.getPlugin().getHandle();
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DAY_OF_MONTH, 1);
		now = cal.getTime();
		cal.add(Calendar.DAY_OF_MONTH, -31);
		Date thirtyDaysAgo = cal.getTime();
		Gson gson = GsonBuilder.create();
		PacketPlayOutLookupContacts contactsLookup = new PacketPlayOutLookupContacts(
				authCode);
		Optional<PacketPlayInReply> replyPacket = ctx.getOutboundHandler()
				.dispatch(ctx, contactsLookup);
		if (!replyPacket.isPresent()) {
			return;
		}
		List<String> contacts = new ArrayList<>();
		if (replyPacket.get().getStatusCode() == 200) {
			contacts = gson.fromJson(replyPacket.get().getText(), List.class);
		}
		PacketPlayOutLookupConversationHistory conversationHistoryLookup = new PacketPlayOutLookupConversationHistory(
				authCode, thirtyDaysAgo, now);
		replyPacket = ctx.getOutboundHandler().dispatch(ctx,
				conversationHistoryLookup);
		if (!replyPacket.isPresent()) {
			return;
		}
		if (replyPacket.get().getStatusCode() != 200) {
			return;
		}
		List<String> participantIds = gson.fromJson(
				replyPacket.get().getText(), List.class);
		for (String key : participantIds) {
			UUID participantId = UUID.fromString(key);
			if (participantId.equals(loggedInUser.getUniqueId())) {
				continue;
			}
			PacketPlayOutLookupUser userLookup = new PacketPlayOutLookupUser(
					authCode, participantId);
			replyPacket = ctx.getOutboundHandler().dispatch(ctx, userLookup);
			if (!replyPacket.isPresent()) {
				return;
			}
			if (replyPacket.get().getStatusCode() != 200) {
				continue;
			}
			Status onlineStatus = Status.OFFLINE;
			{
				PacketPlayOutLookupOnlineStatus onlineStatusLookup = new PacketPlayOutLookupOnlineStatus(
						authCode, participantId);
				Optional<PacketPlayInReply> replyPacket2 = ctx
						.getOutboundHandler().dispatch(ctx, onlineStatusLookup);
				if (!replyPacket2.isPresent()) {
					return;
				}
				if (replyPacket2.get().getStatusCode() != 200) {
					continue;
				}
				onlineStatus = Status.valueOf(replyPacket2.get().getText());
			}
			boolean hit = false;
			for (String contact : contacts) {
				UUID contactuid = UUID.fromString(contact);
				if (contactuid.equals(participantId)) {
					hit = true;
					break;
				}
			}
			if (hit) {
				Contact contact = new Contact(replyPacket.get().getText());
				contact.setOnlineStatus(onlineStatus);
				PacketPlayOutLookupMessageHistory messageHistoryLookup = new PacketPlayOutLookupMessageHistory(
						authCode, participantId, thirtyDaysAgo, now);
				replyPacket = ctx.getOutboundHandler().dispatch(ctx,
						messageHistoryLookup);
				if (!replyPacket.isPresent()) {
					return;
				}
				if (replyPacket.get().getStatusCode() != 200) {
					continue;
				}
				List<String> messages = gson.fromJson(replyPacket.get()
						.getText(), List.class);
				for (String message : messages) {
					contact.getMessages().add(new Message(message));
				}
				Collections.sort(contact.getMessages());
				contact.setLastModified(new Date(contact.getMessages()
						.get(contact.getMessages().size() - 1).getTimestamp()));
				conversations.add(contact);
			} else {
				Conversation conversation = new Conversation(replyPacket.get()
						.getText());
				PacketPlayOutLookupMessageHistory messageHistoryLookup = new PacketPlayOutLookupMessageHistory(
						authCode, participantId, thirtyDaysAgo, now);
				replyPacket = ctx.getOutboundHandler().dispatch(ctx,
						messageHistoryLookup);
				if (!replyPacket.isPresent()) {
					return;
				}
				if (replyPacket.get().getStatusCode() != 200) {
					continue;
				}
				List<String> messages = gson.fromJson(replyPacket.get()
						.getText(), List.class);
				for (String message : messages) {
					conversation.getMessages().add(new Message(message));
				}
				Collections.sort(conversation.getMessages());
				for (Message message : conversation.getMessages()) {
					if (message.getMessageType() == null) {
						continue;
					}
					if (message.getMessageType() == MessageType.SEND_FRIEND_REQUEST) {
						if (!message.getSender().equals(
								loggedInUser.getUniqueId())) {
							conversation.setHasIncomingFriendRequest(true,
									message);
						} else {
							conversation.setHasOutgoingFriendRequest(true);
						}
					}
					if (message.getMessageType() == MessageType.DECLINE_FRIEND_REQUEST
							|| message.getMessageType() == MessageType.ACCEPT_FRIEND_REQUEST) {
						if (message.getSender().equals(
								loggedInUser.getUniqueId())) {
							conversation.setHasIncomingFriendRequest(false,
									null);
						} else {
							conversation.setHasOutgoingFriendRequest(false);
						}
					}
				}
				conversation.setLastModified(new Date(conversation
						.getMessages()
						.get(conversation.getMessages().size() - 1)
						.getTimestamp()));
				conversations.add(conversation);
			}
		}
	}

	float sampleRate = 16000.0F;
	int sampleSizeBits = 16;
	int channels = 1;
	boolean signed = true;
	boolean bigEndian = false;
	AudioFormat format = new AudioFormat(sampleRate, sampleSizeBits, channels,
			signed, bigEndian);
	DataLine.Info micInfo = new DataLine.Info(TargetDataLine.class, format);
	public TargetDataLine mic = null;

	public MainForm(UUID authCode, Contact loggedInUser) {
		super("Skype\u2122 - " + loggedInUser.getSkypeName());

		try {
			mic = (TargetDataLine) AudioSystem.getLine(micInfo);
			mic.open(format);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		JFrame frame = this;

		instance = this;

		this.authCode = authCode;
		this.loggedInUser = loggedInUser;

		SocketHandlerContext ctx = Skype.getPlugin().getHandle();

		loggedInUser.setOnlineStatus(Status.OFFLINE);

		try {
			loggedInUser.setPubKey(PGPUtilities
					.createOrLookupPublicKey(loggedInUser.getSkypeName()));
		} catch (InvalidAlgorithmParameterException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (PGPException e1) {
			e1.printStackTrace();
		}

		{
			PacketPlayOutUpdateUser msg = new PacketPlayOutUpdateUser(authCode,
					loggedInUser.getUniqueId(), loggedInUser);
			ctx.getOutboundHandler().dispatch(ctx, msg);
		}

		loggedInUser.setOnlineStatus(Status.ONLINE);

		/**
		 * We will now read the data we have in memory on our disk
		 * 
		 * This may be contacts, conversations, messages and personal data
		 * 
		 * If the data is not present then read the last 30 days from the server
		 * 
		 * The data is stored forever on the hard drive, just not on the server
		 */
		this.readFromMemory();

		{
			PacketPlayOutEnteringListeningMode msg = new PacketPlayOutEnteringListeningMode(
					authCode);
			ctx.getOutboundHandler().dispatch(ctx, msg);
		}

		ctx.fireInboundHandlerActive();

		addWindowListener(windowAdapter);

		JMenuBar menuBar = new JMenuBar();

		JMenu skypeMenu = new JMenu("Skype");
		skypeMenu.add(new JMenu("Online Status"));
		skypeMenu.add(new JSeparator());
		skypeMenu.add(new JMenu("Profile"));
		skypeMenu.add(new JMenuItem("Privacy..."));
		skypeMenu.add(new JMenuItem("Account..."));
		skypeMenu.add(new JMenuItem("Buy Skype Credit..."));
		skypeMenu.add(new JSeparator());
		skypeMenu.add(new JMenuItem("Change Password..."));
		JMenuItem signOut = new JMenuItem("Sign Out");
		signOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Skype.getPlugin().onDisable();
				timer1.stop();
				timer2.stop();
				timer3.stop();
				mic.stop();
				mic.close();
				AudioIO.LOGOUT.playSound();
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.removeWindowListener(windowAdapter);
				frame.dispatchEvent(new WindowEvent(frame,
						WindowEvent.WINDOW_CLOSING));
				LoginForm loginForm = new LoginForm();
				loginForm.show();
			}

		});
		skypeMenu.add(signOut);
		skypeMenu.add(new JMenuItem("Close"));
		JMenu contactsMenu = new JMenu("Contacts");
		contactsMenu.add(new JMenuItem("Add Contact"));
		contactsMenu.add(new JMenuItem("Import Contacts..."));
		JMenuItem createNewGroupMenuItem = new JMenuItem("Create New Group...");
		createNewGroupMenuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_N, KeyEvent.CTRL_MASK));
		contactsMenu.add(createNewGroupMenuItem);
		contactsMenu.add(new JSeparator());
		contactsMenu.add(new JMenu("Contact Lists"));
		contactsMenu.add(new JMenuItem("Show Outlook Contacts"));
		contactsMenu.add(new JMenu("Sort Contacts by"));
		contactsMenu.add(new JMenu("Hide Contacts Who"));
		contactsMenu.add(new JMenu("Advanced"));
		contactsMenu.add(new JSeparator());
		{
			JMenuItem menuItem = new JMenuItem("Remove from Contacts");
			menuItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (selectedConversation == null) {
						return;
					}
					if (!(selectedConversation instanceof Contact)) {
						return;
					}
					DialogForm form = new DialogForm(null,
							"Skype™ - Remove contact?", "Remove contact?",
							"Remove " + selectedConversation.getDisplayName()
									+ " from Contacts?", "Remove",
							new Runnable() {

								@Override
								public void run() {
									Optional<SocketHandlerContext> ctx = Skype
											.getPlugin().createHandle();
									if (ctx.isPresent()) {
										Optional<PacketPlayInReply> reply = ctx
												.get()
												.getOutboundHandler()
												.dispatch(
														ctx.get(),
														new PacketPlayOutLogin(
																authCode));
										if (!reply.isPresent()
												|| reply.get().getStatusCode() != 200) {
											return;
										}
										for (Message message : selectedConversation
												.getMessages()
												.toArray(new Message[0])
												.clone()) {
											if (message.isDeleted()) {
												continue;
											}
											UUID authCode = UUID
													.fromString(reply.get()
															.getText());
											PacketPlayOutRemoveMessage removeMessage = new PacketPlayOutRemoveMessage(
													authCode,
													selectedConversation
															.getUniqueId(),
													message.getUniqueId(),
													message.getTimestamp());
											Optional<PacketPlayInReply> replyPacket2 = ctx
													.get()
													.getOutboundHandler()
													.dispatch(ctx.get(),
															removeMessage);
											if (!replyPacket2.isPresent()) {
												return;
											}
											if (replyPacket2.get()
													.getStatusCode() != 200) {
												return;
											}
										}
									}
								}
							});

					form.show();
				}

			});
			contactsMenu.add(menuItem);
		}
		JMenu conversationMenu = new JMenu("Conversation");
		conversationMenu.add(new JMenu("Send"));
		conversationMenu.add(new JSeparator());
		conversationMenu.add(new JMenu("Profile Panel"));
		JMenuItem addPeopleMenuItem = new JMenuItem("Add People...");
		addPeopleMenuItem.setAccelerator(KeyStroke
				.getKeyStroke("control shift A"));
		conversationMenu.add(addPeopleMenuItem);
		conversationMenu.add(new JMenuItem("Rename..."));
		conversationMenu.add(new JMenuItem("Leave Conversation"));
		conversationMenu.add(new JMenuItem("Block..."));
		conversationMenu.add(new JMenuItem("Notification Settings..."));
		conversationMenu.add(new JSeparator());
		JMenuItem findMenuItem = new JMenuItem("Find...");
		findMenuItem.setAccelerator(KeyStroke.getKeyStroke("control F"));
		conversationMenu.add(findMenuItem);
		conversationMenu.add(new JMenu("View Old Messages"));
		conversationMenu.add(new JSeparator());
		conversationMenu.add(new JMenuItem("Add to Favorites"));
		conversationMenu.add(new JMenuItem("Add to List"));
		JMenuItem markAsUnreadMenuItem = new JMenuItem("Mark as Unread");
		markAsUnreadMenuItem.setAccelerator(KeyStroke
				.getKeyStroke("control shift U"));
		conversationMenu.add(markAsUnreadMenuItem);
		JMenuItem hideConversationMenuItem = new JMenuItem("Hide Conversation");
		hideConversationMenuItem.setAccelerator(KeyStroke
				.getKeyStroke("control F4"));
		conversationMenu.add(hideConversationMenuItem);
		JMenuItem closeConversationMenuItem = new JMenuItem("Close");
		closeConversationMenuItem.setAccelerator(KeyStroke
				.getKeyStroke("control W"));
		conversationMenu.add(closeConversationMenuItem);
		JMenu callMenu = new JMenu("Call");
		JMenuItem callMenuItem = new JMenuItem("Call");
		callMenuItem.setAccelerator(KeyStroke.getKeyStroke("control alt R"));
		callMenu.add(callMenuItem);
		JMenuItem videoCallMenuItem = new JMenuItem("Video Call");
		videoCallMenuItem.setAccelerator(KeyStroke
				.getKeyStroke("shift control R"));
		callMenu.add(videoCallMenuItem);
		JMenuItem answerMenuItem = new JMenuItem("Answer");
		answerMenuItem.setAccelerator(KeyStroke.getKeyStroke("alt PAGE_UP"));
		callMenu.add(answerMenuItem);
		callMenu.add(new JSeparator());
		callMenu.add(new JMenuItem("Ignore"));
		JMenuItem muteMicrophoneMenuItem = new JMenuItem("Mute Microphone");
		muteMicrophoneMenuItem.setAccelerator(KeyStroke
				.getKeyStroke("control M"));
		callMenu.add(muteMicrophoneMenuItem);
		callMenu.add(new JMenuItem("Hold"));
		JMenuItem hangUpMenuItem = new JMenuItem("Hang up");
		hangUpMenuItem.setAccelerator(KeyStroke.getKeyStroke("alt PAGE_DOWN"));
		callMenu.add(hangUpMenuItem);
		callMenu.add(new JSeparator());
		{
			JMenuItem callPhonesMenuItem = new JMenuItem("Call Phones");
			callPhonesMenuItem.setAccelerator(KeyStroke
					.getKeyStroke("control D"));
			callMenu.add(callPhonesMenuItem);
		}
		callMenu.add(new JSeparator());
		callMenu.add(new JMenuItem("Audio Settings..."));
		callMenu.add(new JMenu("Video"));
		callMenu.add(new JMenuItem("Share Screens..."));
		callMenu.add(new JMenuItem("Stop Sharing Screen"));
		callMenu.add(new JSeparator());
		callMenu.add(new JMenuItem("Learn about Call Quality"));
		JMenu viewMenu = new JMenu("View");
		JMenuItem contactsMenuItem = new JMenuItem("Contacts");
		contactsMenuItem.setAccelerator(KeyStroke.getKeyStroke("alt 1"));
		viewMenu.add(contactsMenuItem);
		JMenuItem recentMenuItem = new JMenuItem("Recent");
		recentMenuItem.setAccelerator(KeyStroke.getKeyStroke("alt 2"));
		viewMenu.add(recentMenuItem);
		viewMenu.add(new JSeparator());
		viewMenu.add(new JMenuItem("Compact Sidebar View"));
		viewMenu.add(new JSeparator());
		JMenuItem skypeHomeMenuItem = new JMenuItem("Skype Home");
		skypeHomeMenuItem.setAccelerator(KeyStroke.getKeyStroke("control H"));
		viewMenu.add(skypeHomeMenuItem);
		JMenuItem profileMenuItem = new JMenuItem("Profile");
		profileMenuItem.setAccelerator(KeyStroke.getKeyStroke("control I"));
		viewMenu.add(profileMenuItem);
		{
			JMenuItem callPhonesMenuItem = new JMenuItem("Call Phones");
			callPhonesMenuItem.setAccelerator(KeyStroke
					.getKeyStroke("control D"));
			viewMenu.add(callPhonesMenuItem);
		}
		viewMenu.add(new JMenuItem("Snapshots Gallery"));
		viewMenu.add(new JSeparator());
		viewMenu.add(new JMenuItem("Split Window View"));
		JMenuItem fullScreenMenuItem = new JMenuItem("Full Screen");
		fullScreenMenuItem.setAccelerator(KeyStroke.getKeyStroke("alt ENTER"));
		viewMenu.add(fullScreenMenuItem);
		viewMenu.add(new JSeparator());
		viewMenu.add(new JMenuItem("Show Hidden Conversations"));
		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.add(new JMenu("Change Language"));
		toolsMenu.add(new JSeparator());
		toolsMenu.add(new JMenu("Accessibility"));
		toolsMenu.add(new JSeparator());
		toolsMenu.add(new JMenuItem("Skype WiFi..."));
		toolsMenu.add(new JSeparator());
		toolsMenu.add(new JMenuItem("Options..."));
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new JMenuItem("Learn about Skype for Windows"));
		helpMenu.add(new JMenuItem("Go to Support"));
		helpMenu.add(new JMenuItem("Ask the Skype Community"));
		helpMenu.add(new JMenuItem("Heartbeat (Skype Status)"));
		helpMenu.add(new JSeparator());
		helpMenu.add(new JMenuItem("Call Quality Guide"));
		helpMenu.add(new JMenuItem("Check for Updates"));
		helpMenu.add(new JSeparator());
		helpMenu.add(new JMenuItem("Privacy Policy"));

		JMenuItem aboutSkype = new JMenuItem("About Skype");

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

		menuBar.add(skypeMenu);
		menuBar.add(contactsMenu);
		menuBar.add(conversationMenu);
		menuBar.add(callMenu);
		menuBar.add(viewMenu);
		menuBar.add(toolsMenu);
		menuBar.add(helpMenu);

		setJMenuBar(menuBar);

		/* Start left panel */

		/* Start left top panel */

		leftTopPanel.setBackground(new Color(245, 252, 254));

		JLayeredPane leftTopLayeredPane = this
				.createLeftTopLayeredPane(defaultLeftPanelWidth);

		/**
		 * This allows us to put a layered pane at x 0 y 0
		 * 
		 * Note that the layered pane by default has a null layout
		 * 
		 * That null layout allows us to set the bounds to start at x 0 y 0
		 * 
		 * If we do not put this code then there will be extra padding
		 * 
		 * That padding would interfere with the look of the program
		 */
		leftTopPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

		leftTopPanel.add(leftTopLayeredPane);

		/* End left top panel */

		/* Start left bottom panel */

		/**
		 * Construct left bottom panel scroll pane using default width
		 */
		JPanel leftBottomPanel = this
				.createLeftBottomPanel(defaultLeftPanelWidth - scrollBarWidth);
		leftBottomScrollPane = new JScrollPane(new JPanel(),
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		leftBottomScrollPane.setViewportView(leftBottomPanel);

		/**
		 * We speed up the scroll speed of the scroll pane
		 * 
		 * This will be customizable in the future in settings
		 */
		leftBottomScrollPane.getVerticalScrollBar().setUnitIncrement(
				this.scrollBarUnitIncrement);

		/**
		 * Resize the left bottom panel scroll pane to 0 height
		 * 
		 * We shall let the java runtime environment resize it for us
		 */
		leftBottomScrollPane.setPreferredSize(new Dimension(
				defaultLeftPanelWidth, 0));
		leftBottomScrollPane.setBorder(BorderFactory.createEmptyBorder());

		/* End left bottom panel */

		/**
		 * We create the final left panel split pane using the right components
		 * 
		 * The java runtime environment will resize the left split pane
		 */
		leftSplitPane = new JSplitPane(SwingConstants.HORIZONTAL, leftTopPanel,
				leftBottomScrollPane);

		/**
		 * We need to stop the user from resizing the left top panel
		 */
		int leftPanelSplitPaneDividerLocation = leftSplitPane
				.getDividerLocation();
		((BasicSplitPaneUI) leftSplitPane.getUI()).getDivider().setVisible(
				false);
		leftSplitPane.setBorder(BorderFactory.createEmptyBorder());
		leftSplitPane.addPropertyChangeListener("dividerLocation",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						((JSplitPane) e.getSource())
								.setDividerLocation(leftPanelSplitPaneDividerLocation);
					}
				});

		/* End left panel */

		/* Start right panel */

		/* Start right top panel */

		rightTopPanel.setOpaque(false);

		/**
		 * This allows us to put a layered pane at x 0 y 0
		 * 
		 * Note that the layered pane by default has a null layout
		 * 
		 * That null layout allows us to set the bounds to start at x 0 y 0
		 * 
		 * If we do not put this code then there will be extra padding
		 * 
		 * That padding would interfere with the look of the program
		 */
		rightTopPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		JLayeredPane rightTopLayeredPane = this.createRightTopLayeredPane(
				this.defaultWindowSize.width - this.splitPaneDividerSize
						- defaultLeftPanelWidth,
				this.defaultRightTopPanelHeight);

		rightTopPanel.add(rightTopLayeredPane);

		/* End right top panel */

		/* Start right bottom panel */

		rightBottomSplitPane.setOpaque(false);

		int rightBottomSplitPaneWidth = this.defaultWindowSize.width
				- this.splitPaneDividerSize - defaultLeftPanelWidth;

		JPanel rightBottomTopPanel = this
				.createRightBottomTopPanel(rightBottomSplitPaneWidth
						- this.scrollBarWidth - 5);

		/**
		 * Resize the right bottom top panel scroll pane to 0 width 0 height
		 * 
		 * We shall let the java runtime environment resize it for us
		 */
		this.rightBottomTopPanel.setPreferredSize(new Dimension(0, 0));

		this.rightBottomTopPanel.setViewportView(rightBottomTopPanel);

		this.rightBottomTopPanel.setBorder(BorderFactory.createEmptyBorder());

		this.rightBottomTopPanel.getViewport().setOpaque(false);

		this.rightBottomTopPanel.setOpaque(false);

		/**
		 * We speed up the scroll speed of the scroll pane
		 * 
		 * This will be customizable in the future in settings
		 */
		this.rightBottomTopPanel.getVerticalScrollBar().setUnitIncrement(
				this.scrollBarUnitIncrement);

		JLayeredPane rightBottomBottomLayeredPane = this
				.createRightBottomBottomLayeredPane(rightBottomSplitPaneWidth,
						this.defaultRightBottomBottomPanelHeight);

		/**
		 * This allows us to put a layered pane at x 0 y 0
		 * 
		 * Note that the layered pane by default has a null layout
		 * 
		 * That null layout allows us to set the bounds to start at x 0 y 0
		 * 
		 * If we do not put this code then there will be extra padding
		 * 
		 * That padding would interfere with the look of the program
		 */
		rightBottomBottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

		rightBottomBottomPanel.setOpaque(false);

		rightBottomBottomPanel.add(rightBottomBottomLayeredPane);

		rightBottomSplitPane = new JSplitPane(SwingConstants.HORIZONTAL,
				this.rightBottomTopPanel, rightBottomBottomPanel);

		rightBottomSplitPane.setBorder(BorderFactory.createEmptyBorder());

		rightBottomSplitPane.setDividerLocation(this.defaultWindowSize.height
				- this.defaultRightBottomBottomPanelHeight
				- this.defaultRightTopPanelHeight);

		rightBottomSplitPane.setOpaque(false);

		/**
		 * We need to stop the user from resizing the right bottom panel
		 */
		((BasicSplitPaneUI) rightBottomSplitPane.getUI()).getDivider()
				.setVisible(false);

		rightBottomSplitPane.setBorder(BorderFactory.createEmptyBorder());

		/* End right bottom panel */

		/**
		 * Construct right split pane
		 */
		rightSplitPane = new JSplitPane(SwingConstants.HORIZONTAL,
				rightTopPanel, rightBottomSplitPane);

		rightSplitPane.setBorder(BorderFactory.createEmptyBorder());

		/**
		 * Resize the right split pane to be of 0 width and 0 height
		 * 
		 * We shall let the java runtime environment resize it for us
		 */
		rightSplitPane.setPreferredSize(new Dimension(0, 0));

		/* End right panel */

		/* Start split pane */
		/**
		 * Construct split pane to allow the user to resize
		 * 
		 * The java runtime environment will resize the right split pane
		 */
		splitPane = new JSplitPane(SwingConstants.VERTICAL, leftSplitPane,
				rightSplitPane);

		splitPane.setBorder(BorderFactory.createEmptyBorder());

		/* End split pane */

		/**
		 * We need to stop the user from resizing the right top panel
		 */
		int rightPanelSplitPaneDividerLocation = rightSplitPane
				.getDividerLocation();
		((BasicSplitPaneUI) rightSplitPane.getUI()).getDivider().setVisible(
				false);
		rightSplitPane.setBorder(BorderFactory.createEmptyBorder());
		rightSplitPane.addPropertyChangeListener("dividerLocation",
				new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent e) {
						((JSplitPane) e.getSource())
								.setDividerLocation(rightPanelSplitPaneDividerLocation);
					}
				});

		rightSplitPane.setOpaque(false);
		splitPane.setOpaque(false);

		setContentPane(splitPane);

		setBackground(Color.white);

		getContentPane().setPreferredSize(defaultWindowSize);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		/**
		 * If focus is gained, check for placeholder and remove it
		 * 
		 * If focus is lost and text is empty, add back the placeholder
		 * 
		 * If the placeholder is not there it will select text on focus
		 */
		searchTextField.addFocusListener(new FocusListener() {

			@Override
			public void focusGained(FocusEvent arg0) {
				if (searchTextField.getText().equals("Search")) {
					searchTextField.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent arg0) {
				if (searchTextField.getText().equals("")) {
					searchTextField.setText("Search");
				}
			}

		});

		searchTextField.getDocument().addDocumentListener(
				new DocumentListener() {

					String lastText = "";

					@Override
					public void changedUpdate(DocumentEvent e) {
						if (lastText.equals(searchTextField.getText())
								|| (lastText.equals("") && searchTextField
										.getText().equals("Search"))
								|| (lastText.equals("Search") && searchTextField
										.getText().equals(""))) {
							return;
						}
						lastText = searchTextField.getText();
						valueChanged();
						leftBottomPanelPage = "RecentOlder";
						refreshLeftTopPanel();
						refreshLeftBottomPanel();
						searchTextField.grabFocus();
						searchTextField.setSelectionStart(searchTextField
								.getText().length());
					}

					@Override
					public void removeUpdate(DocumentEvent e) {
						if (lastText.equals(searchTextField.getText())
								|| (lastText.equals("") && searchTextField
										.getText().equals("Search"))
								|| (lastText.equals("Search") && searchTextField
										.getText().equals(""))) {
							return;
						}
						lastText = searchTextField.getText();
						valueChanged();
						leftBottomPanelPage = "RecentOlder";
						refreshLeftTopPanel();
						refreshLeftBottomPanel();
						searchTextField.grabFocus();
						searchTextField.setSelectionStart(searchTextField
								.getText().length());
					}

					@Override
					public void insertUpdate(DocumentEvent e) {
						if (lastText.equals(searchTextField.getText())
								|| (lastText.equals("") && searchTextField
										.getText().equals("Search"))
								|| (lastText.equals("Search") && searchTextField
										.getText().equals(""))) {
							return;
						}
						lastText = searchTextField.getText();
						valueChanged();
						leftBottomPanelPage = "RecentOlder";
						refreshLeftTopPanel();
						refreshLeftBottomPanel();
						searchTextField.grabFocus();
						searchTextField.setSelectionStart(searchTextField
								.getText().length());
					}

					public void valueChanged() {
						searchTextFieldConversations.clear();
						if (searchTextField.getText().length() == 0) {
							return;
						}
						if (searchTextField.getText().equals("Search")) {
							return;
						}
						Optional<SocketHandlerContext> ctx = Skype.getPlugin()
								.createHandle();
						if (!ctx.isPresent()) {
							return;
						}
						PacketPlayOutUserSearch packet = new PacketPlayOutUserSearch(
								authCode, searchTextField.getText());
						Optional<PacketPlayInReply> replyPacket = ctx.get()
								.getOutboundHandler()
								.dispatch(ctx.get(), packet);
						if (!replyPacket.isPresent()) {
							return;
						}
						if (replyPacket.get().getStatusCode() != 200) {
							return;
						}
						String json = replyPacket.get().getText();
						Gson gson = GsonBuilder.create();
						List<String> skypeNames = gson.fromJson(json,
								List.class);
						for (String skypeName : skypeNames) {
							if (skypeName.equals(loggedInUser.skypeName)) {
								continue;
							}
							UUID participantId = Skype.getPlugin().getUniqueId(
									skypeName);
							PacketPlayOutLookupUser lookupUserPacket = new PacketPlayOutLookupUser(
									authCode, participantId);
							replyPacket = ctx.get().getOutboundHandler()
									.dispatch(ctx.get(), lookupUserPacket);
							if (!replyPacket.isPresent()) {
								return;
							}
							if (replyPacket.get().getStatusCode() != 200) {
								continue;
							}
							Conversation conversation = new Conversation(
									replyPacket.get().getText());
							boolean hit = false;
							for (Conversation o : conversations) {
								if (o.getUniqueId().equals(
										conversation.getUniqueId())) {
									searchTextFieldConversations.add(o);
									hit = true;
								}
							}
							if (!hit) {
								searchTextFieldConversations.add(conversation);
							}
						}
					}
				});

		/**
		 * Listen for if the user changed the divider location
		 * 
		 * If the divider location changed we need to resize some components
		 */
		splitPane.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent changeEvent) {
				String propertyName = changeEvent.getPropertyName();
				if (propertyName.equals(JSplitPane.DIVIDER_LOCATION_PROPERTY)) {
					if (changeEvent.getSource() instanceof JSplitPane) {
						JSplitPane source = ((JSplitPane) changeEvent
								.getSource());

						/**
						 * If the java runtime environment sets the divider
						 * location for the first time then this value is 0 when
						 * you check getLastDividerLocation() in this method
						 * 
						 * If the user changes the divider location for the
						 * first time then this value is -1 when you check
						 * getLastDividerLocation() in this method
						 * 
						 * So this code here is checking to see if the
						 * getLastDividerLocation() on the JScrollPane is 0 and
						 * if so then we know that the user did not initiate the
						 * divider change and we will not run the code beneath
						 * 
						 * This is for performance reasons when the window first
						 * opens, and also on Linux if we do not add this line
						 * of code then the user interface will be all screwed
						 * up when the program first runs for some reason
						 */
						if (source.getLastDividerLocation() != 0) {
							refreshWindow();
						}
					}
				}
			}
		});

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

		AudioIO.LOGIN.playSound();

		{

			timer1 = new Timer(60 * 1000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent evt) {
					Optional<SocketHandlerContext> ctx = Skype.getPlugin()
							.createHandle();
					if (ctx.isPresent()) {
						Optional<PacketPlayInReply> reply = ctx
								.get()
								.getOutboundHandler()
								.dispatch(ctx.get(),
										new PacketPlayOutRefreshToken(authCode));
						if (!reply.isPresent()
								|| reply.get().getStatusCode() != 200) {
							Skype.getPlugin().onDisable();
							timer1.stop();
							timer2.stop();
							timer3.stop();
							mic.stop();
							mic.close();
							frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							frame.removeWindowListener(windowAdapter);
							frame.dispatchEvent(new WindowEvent(frame,
									WindowEvent.WINDOW_CLOSING));
							LoginForm loginForm = new LoginForm();
							loginForm.show();
							return;
						}
					}
				}

			});

			timer1.start();

		}

		{

			timer2 = new Timer(1000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent evt) {
					SimpleDateFormat df = new SimpleDateFormat("mm:ss");
					ongoingCallTimeLabel.setText(df.format(new Date(System
							.currentTimeMillis() - ongoingCallStartTime)));
				}

			});

			timer2.start();

		}

		{

			timer3 = new Timer(10 * 1000, new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent evt) {
					Optional<SocketHandlerContext> ctx = Skype.getPlugin()
							.createHandle();
					if (ctx.isPresent()) {
						Optional<PacketPlayInReply> reply = ctx
								.get()
								.getOutboundHandler()
								.dispatch(ctx.get(),
										new PacketPlayOutLogin(authCode));
						if (!reply.isPresent()
								|| reply.get().getStatusCode() != 200) {
							Skype.getPlugin().onDisable();
							timer1.stop();
							timer2.stop();
							timer3.stop();
							mic.stop();
							mic.close();
							frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
							frame.removeWindowListener(windowAdapter);
							frame.dispatchEvent(new WindowEvent(frame,
									WindowEvent.WINDOW_CLOSING));
							LoginForm loginForm = new LoginForm();
							loginForm.show();
							return;
						}
						UUID authCode = UUID.fromString(reply.get().getText());
						for (Conversation conversation : conversations) {
							if (conversation instanceof Contact) {
								Contact contact = (Contact) conversation;
								Status onlineStatus = Status.OFFLINE;
								{
									PacketPlayOutLookupOnlineStatus onlineStatusLookup = new PacketPlayOutLookupOnlineStatus(
											authCode, conversation
													.getUniqueId());
									Optional<PacketPlayInReply> replyPacket2 = ctx
											.get()
											.getOutboundHandler()
											.dispatch(ctx.get(),
													onlineStatusLookup);
									if (!replyPacket2.isPresent()) {
										return;
									}
									if (replyPacket2.get().getStatusCode() != 200) {
										continue;
									}
									onlineStatus = Status.valueOf(replyPacket2
											.get().getText());
								}
								contact.setOnlineStatus(onlineStatus);
							}
						}
					}
				}

			});

			timer3.start();

		}
	}

	@Override
	public void show() {
		super.show();

		/**
		 * We need to refresh the top right and bottom right panels on resize
		 *
		 * We create and use a timer set for 100ms to refresh the panels
		 * 
		 * If the component is moved within those 100ms then the timer restarts
		 * 
		 * For best performance turn off "Show window contents while dragging"
		 * 
		 * You can do this by going to visual effects settings on Windows 7
		 * 
		 * This timer is just to improve performance for those who leave that on
		 */
		Timer timer = new Timer(100, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent evt) {
				((Timer) evt.getSource()).stop();
				refreshWindow();
			}

		});

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(final ComponentEvent e) {
				if (e.getComponent() instanceof JFrame) {
					super.componentResized(e);
					timer.restart();
				}
			}
		});
	}
}
