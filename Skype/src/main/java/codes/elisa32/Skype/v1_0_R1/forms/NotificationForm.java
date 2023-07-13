package codes.elisa32.Skype.v1_0_R1.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import codes.elisa32.Skype.v1_0_R1.Utils;
import codes.elisa32.Skype.v1_0_R1.audioio.AudioIO;
import codes.elisa32.Skype.v1_0_R1.awt.AWTUtilities;
import codes.elisa32.Skype.v1_0_R1.data.types.Contact;
import codes.elisa32.Skype.v1_0_R1.data.types.Conversation;
import codes.elisa32.Skype.v1_0_R1.data.types.Message;
import codes.elisa32.Skype.v1_0_R1.fontio.FontIO;
import codes.elisa32.Skype.v1_0_R1.imageio.ImageIO;

public class NotificationForm extends JDialog {

	private Conversation conversation;

	public NotificationForm(Conversation conversation, Message message,
			boolean playSound) {
		this.conversation = conversation;

		setTitle("Notification");

		setUndecorated(true);

		setAlwaysOnTop(true);

		setBackground(new Color(0, 0, 0, 0));

		setPreferredSize(new Dimension(222, 78));

		/*
		 * Note to self! setResizable() must come before pack()
		 * 
		 * If you do not do this before pack() then the size is wrong
		 */
		setResizable(false);

		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(
				getGraphicsConfiguration());

		setLocation(scrSize.width - getWidth(), scrSize.height
				- toolHeight.bottom - getHeight());

		int panelWidth = getWidth();
		int panelHeight = getHeight();

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, panelWidth, panelHeight);
		layeredPane.setPreferredSize(new Dimension(panelWidth, panelHeight));
		layeredPane.setOpaque(false);

		setContentPane(layeredPane);

		final JDialog dialog = this;

		try {
			AWTUtilities.setWindowOpacity(this, 0.0f);
		} catch (Exception e) {
		}

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (playSound) {
					AudioIO.IM.playSound();
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				for (int i = 0; i < 10; i++) {
					try {
						AWTUtilities.setWindowOpacity(dialog, 0.1f * i);
						Thread.sleep(10);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		});
		thread.start();

		MouseAdapter openConversationActionListener = new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent evt) {
				if (MainForm.get() != null) {
					MainForm.get().rightPanelPage = "Conversation";
					for (Conversation obj : MainForm.get().getConversations()) {
						if (obj.getUniqueId()
								.equals(conversation.getUniqueId())) {
							MainForm.get().setSelectedConversation(obj);
						}
					}
					MainForm.get().refreshWindow(true);
					MainForm.get().show();
					MainForm.get().setExtendedState(JFrame.NORMAL);
				}
				dialog.dispatchEvent(new WindowEvent(dialog,
						WindowEvent.WINDOW_CLOSING));
			}
		};

		{
			JPanel panel2 = new JPanel();
			panel2.setBounds(0, 0, panelWidth, panelHeight);
			panel2.setPreferredSize(new Dimension(panelWidth, panelHeight));
			panel2.setLayout(new BorderLayout());
			panel2.setBackground(new Color(38, 38, 38));

			{
				JPanel iconLabelPanel = new JPanel();
				iconLabelPanel
						.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
				ImageIcon imageIcon = ImageIO
						.getResourceAsImageIcon("/675692956.png");
				JLabel iconLabel = new JLabel(imageIcon);

				iconLabelPanel.setBounds(0, 0, panelWidth, panelHeight);
				iconLabelPanel.setOpaque(false);
				iconLabelPanel.add(iconLabel);

				iconLabel.addMouseListener(openConversationActionListener);
				iconLabelPanel.addMouseListener(openConversationActionListener);

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
				iconLabelPanel
						.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
				ImageIcon imageIcon = ImageIO
						.getResourceAsImageIcon("/1465928249.png");
				JLabel iconLabel = new JLabel(imageIcon);

				iconLabelPanel.setBounds(201, 8, 14, 14);
				iconLabelPanel.setOpaque(false);
				iconLabelPanel.add(iconLabel);

				iconLabel.setCursor(Cursor
						.getPredefinedCursor(Cursor.HAND_CURSOR));
				iconLabelPanel.setCursor(Cursor
						.getPredefinedCursor(Cursor.HAND_CURSOR));

				MouseAdapter mouseAdapter = new MouseAdapter() {
					@Override
					public void mouseEntered(MouseEvent e) {
						iconLabel.setIcon(ImageIO
								.getResourceAsImageIcon("/1335031438.png"));
						iconLabelPanel.validate();
					}

					@Override
					public void mouseExited(MouseEvent e) {
						iconLabel.setIcon(ImageIO
								.getResourceAsImageIcon("/1465928249.png"));
						iconLabelPanel.validate();
					}

					@Override
					public void mousePressed(MouseEvent evt) {
						dialog.dispatchEvent(new WindowEvent(dialog,
								WindowEvent.WINDOW_CLOSING));
					}
				};

				iconLabel.addMouseListener(mouseAdapter);

				/**
				 * Panel added to pane with z-index 1
				 */
				layeredPane.add(iconLabelPanel, new Integer(1), 0);
			}

			if (conversation == null || !(conversation instanceof Contact)) {
				JPanel iconLabelPanel = new JPanel();
				iconLabelPanel
						.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
				ImageIcon imageIcon = ImageIO
						.getResourceAsImageIcon("/448998300.png");
				JLabel iconLabel = new JLabel(imageIcon);

				iconLabelPanel.setBounds(9, 34, 16, 16);
				iconLabelPanel.setOpaque(false);
				iconLabelPanel.add(iconLabel);

				iconLabel.addMouseListener(openConversationActionListener);
				iconLabelPanel.addMouseListener(openConversationActionListener);

				iconLabel.setCursor(Cursor
						.getPredefinedCursor(Cursor.HAND_CURSOR));
				iconLabelPanel.setCursor(Cursor
						.getPredefinedCursor(Cursor.HAND_CURSOR));

				/**
				 * Panel added to pane with z-index 1
				 */
				layeredPane.add(iconLabelPanel, new Integer(1), 0);
			} else {
				JPanel iconLabelPanel = new JPanel();
				iconLabelPanel
						.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
				ImageIcon imageIcon = ImageIO
						.getCircularStatusIcon(((Contact) conversation)
								.getOnlineStatus());
				JLabel iconLabel = new JLabel(imageIcon);

				iconLabelPanel.setBounds(10, 35, 14, 14);
				iconLabelPanel.setOpaque(false);
				iconLabelPanel.add(iconLabel);

				iconLabel.addMouseListener(openConversationActionListener);
				iconLabelPanel.addMouseListener(openConversationActionListener);

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
						conversation == null ? "comsignca.predarid"
								: conversation.getDisplayName());
				label.setFont(FontIO.SEGOE_UI.deriveFont(11.0f));
				label.setForeground(Color.black);
				labelPanel.setOpaque(false);
				labelPanel.add(label);
				int width = label.getPreferredSize().width;
				int height = label.getPreferredSize().height;
				labelPanel.setBounds(29, 30, width, height + 10);

				label.addMouseListener(openConversationActionListener);
				labelPanel.addMouseListener(openConversationActionListener);

				label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				labelPanel.setCursor(Cursor
						.getPredefinedCursor(Cursor.HAND_CURSOR));

				/**
				 * Panel added to pane with z-index 1, above background
				 */
				layeredPane.add(labelPanel, new Integer(1), 0);
			}

			{
				JPanel labelPanel = new JPanel();
				FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(
						FontIO.SEGOE_UI.deriveFont(11.0f));
				String msg = message == null ? "Любишь мокреньких мамаш? И..."
						: message.getDecryptedMessage();
				JLabel label = new JLabel(Utils.concatStringEllipses(fm, 174,
						"\"" + msg + "\""));
				if (label.getText().endsWith("...")) {
					label.setText(label.getText() + "\"");
				}
				label.setFont(FontIO.SEGOE_UI.deriveFont(11.0f));
				label.setForeground(Color.gray);
				int shiftColour = 25;
				label.setForeground(new Color(label.getForeground().getRed()
						- shiftColour, label.getForeground().getGreen()
						- shiftColour, label.getForeground().getRed()
						- shiftColour));
				labelPanel.setOpaque(false);
				labelPanel.add(label);
				int width = label.getPreferredSize().width;
				int height = label.getPreferredSize().height;
				labelPanel.setBounds(29, 46, width, height + 10);

				label.addMouseListener(openConversationActionListener);
				labelPanel.addMouseListener(openConversationActionListener);

				label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				labelPanel.setCursor(Cursor
						.getPredefinedCursor(Cursor.HAND_CURSOR));

				/**
				 * Panel added to pane with z-index 1, above background
				 */
				layeredPane.add(labelPanel, new Integer(1), 0);
			}
		}
	}

	@Override
	public void show() {
		if (MainForm.get().getSelectedConversation() != null
				&& MainForm.get().getSelectedConversation().getUniqueId()
						.equals(conversation.getUniqueId())) {
			if (MainForm.get().rightPanelPage.equals("Conversation")) {
				if (MainForm.get().getExtendedState() != JFrame.ICONIFIED) {
					return;
				}
			}
		}
		super.show();
	}

}
