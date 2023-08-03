package codes.elisa32.Skype.v1_0_R1.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.net.Socket;
import java.util.Optional;

import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutDeclineCallRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.audioio.AudioIO;
import codes.elisa32.Skype.v1_0_R1.awt.AWTUtilities;
import codes.elisa32.Skype.v1_0_R1.cipher.CipherOutputStream;
import codes.elisa32.Skype.v1_0_R1.data.types.Conversation;
import codes.elisa32.Skype.v1_0_R1.fontio.FontIO;
import codes.elisa32.Skype.v1_0_R1.imageio.ImageIO;
import codes.elisa32.Skype.v1_0_R1.plugin.Skype;

public class IncomingCallForm extends JDialog {

	private Conversation conversation;

	private Thread thread = null;

	private Optional<SocketHandlerContext> ctx2;

	private PacketPlayInCallRequest packet;

	private final JDialog dialog;

	private UUID authCode = MainForm.get().getAuthCode();

	private byte[] cipher;

	public void answerCall() {
		UUID callId = packet.getCallId();
		MainForm.get().ongoingCallCipher = cipher;
		Optional<PacketPlayInReply> reply = ctx2
				.get()
				.getOutboundHandler()
				.dispatch(ctx2.get(),
						new PacketPlayOutAcceptCallRequest(authCode, callId));
		if (!reply.isPresent()) {
			MainForm.get().ongoingCallCipher = null;
			return;
		}
		if (reply.get().getStatusCode() != 200) {
			MainForm.get().ongoingCallCipher = null;
			return;
		}
		MainForm.get().mic.stop();
		MainForm.get().mic.drain();
		Thread thread = new Thread(
				() -> {
					try {
						byte tmpBuff[] = new byte[MainForm.get().mic
								.getBufferSize() / 5];
						MainForm.get().mic.start();
						MainForm.get().callOutgoingAudioSockets.add(ctx2.get()
								.getSocket());
						CipherOutputStream cos = new CipherOutputStream(ctx2
								.get().getSocket().getOutputStream(), cipher);
						while (MainForm.get().isVisible()) {
							try {
								int count = MainForm.get().mic.read(tmpBuff, 0,
										tmpBuff.length);
								if (count > 0) {
									cos.write(tmpBuff, 0, count);
								} else {
									Thread.sleep(100);
								}
							} catch (Exception e) {
								e.printStackTrace();
								break;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (MainForm.get().ongoingCallId != null)
						if (callId.equals(MainForm.get().ongoingCallId)) {
							try {
								MainForm.get().mic.stop();
								MainForm.get().mic.drain();
							} catch (Exception e2) {
								e2.printStackTrace();
							}
							try {
								for (Socket socket : MainForm.get().callIncomingAudioSockets) {
									socket.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								for (Socket socket : MainForm.get().callOutgoingAudioSockets) {
									socket.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							MainForm.get().ongoingCall = false;
							MainForm.get().rightPanelPage = "Conversation";
							MainForm.get().ongoingVideoCall = false;
							MainForm.get().ongoingVideoCallParticipants.clear();
							MainForm.get().ongoingVideoCallId = null;
							MainForm.get().ongoingVideoCallCipher = null;
							try {
								for (Socket socket : MainForm.get().videoCallIncomingAudioSockets) {
									socket.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							try {
								for (Socket socket : MainForm.get().videoCallOutgoingAudioSockets) {
									socket.close();
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
							MainForm.get().videoEnabled = false;
							MainForm.get().microphoneEnabled = true;
							MainForm.get().refreshWindow();
						}
				});
		thread.start();
		MainForm.get().rightPanelPage = "OngoingCall";
		MainForm.get().ongoingCall = true;
		MainForm.get().ongoingCallStartTime = System.currentTimeMillis();
		for (Conversation conversation : MainForm.get().getConversations()) {
			if (conversation.getUniqueId().equals(
					this.conversation.getUniqueId())) {
				MainForm.get().setSelectedConversation(conversation);
				MainForm.get().ongoingCallConversation = conversation;
				MainForm.get().ongoingCallId = packet.getCallId();
			}
		}
		MainForm.get().refreshWindow();
		dialog.dispatchEvent(new WindowEvent(dialog, WindowEvent.WINDOW_CLOSING));
	}

	public IncomingCallForm(PacketPlayInCallRequest packet,
			Conversation conversation, boolean playSound, byte[] cipher) {
		this.conversation = conversation;
		this.ctx2 = Skype.getPlugin().createHandle();
		this.packet = packet;
		this.cipher = cipher;

		setTitle("Incoming Call");

		setUndecorated(true);

		setAlwaysOnTop(true);

		setBackground(new Color(0, 0, 0, 0));

		setPreferredSize(new Dimension(445, 113));

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

		setLocation(scrSize.width - getWidth() - 21, 21);

		int panelWidth = getWidth();
		int panelHeight = getHeight();

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, panelWidth, panelHeight);
		layeredPane.setPreferredSize(new Dimension(panelWidth, panelHeight));
		layeredPane.setOpaque(false);

		setContentPane(layeredPane);

		dialog = this;

		Optional<PacketPlayInReply> reply = ctx2.get().getOutboundHandler()
				.dispatch(ctx2.get(), new PacketPlayOutLogin(authCode));
		authCode = UUID.fromString(reply.get().getText());

		try {
			AWTUtilities.setWindowOpacity(this, 0.0f);
		} catch (Exception e) {
		}

		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				if (playSound) {
					new Thread(() -> {
						do {
							try {
								Clip clip = AudioIO.CALL_IN.playSound();
								while (!clip.isRunning()) {
									Thread.sleep(10);
									if (!dialog.isVisible()) {
										clip.stop();
										clip.close();
									}
								}
								while (clip.isRunning()) {
									Thread.sleep(10);
									if (!dialog.isVisible()) {
										clip.stop();
										clip.close();
									}
								}
								clip.close();
							} catch (Exception e) {
								e.printStackTrace();
							}
						} while (dialog.isVisible());
					}).start();
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
					}
				}
			}

		});

		{
			JPanel panel2 = new JPanel();
			panel2.setBounds(0, 0, panelWidth, panelHeight);
			panel2.setPreferredSize(new Dimension(panelWidth, panelHeight));
			panel2.setLayout(new BorderLayout());
			panel2.setOpaque(false);
			panel2.setBackground(new Color(0, 0, 0, 0));

			{
				JPanel iconLabelPanel = new JPanel();
				iconLabelPanel
						.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
				ImageIcon imageIcon = ImageIO
						.getResourceAsImageIcon("/1487572183.png");
				JLabel iconLabel = new JLabel(imageIcon);

				iconLabelPanel.setBounds(0, 0, panelWidth, panelHeight);
				iconLabelPanel.setOpaque(false);
				iconLabelPanel.add(iconLabel);

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
						.getScaledImageIcon(
								conversation == null ? ImageIO
										.getResourceAsImageIcon("/2121871768.png")
										: (conversation.getImageIcon() == null ? ImageIO
												.getResourceAsImageIcon("/2121871768.png")
												: conversation.getImageIcon()),
								new Dimension(66, 66));
				JLabel iconLabel = new JLabel(imageIcon);

				iconLabelPanel.setBounds(10, 36, 66, 66);
				iconLabelPanel.setOpaque(false);
				iconLabelPanel.add(iconLabel);

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
						.getResourceAsImageIcon("/1802188396.png");
				JLabel iconLabel = new JLabel(imageIcon);

				iconLabelPanel.setBounds(422, 5, 16, 16);
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
								.getResourceAsImageIcon("/1768200603.png"));
						iconLabelPanel.validate();
					}

					@Override
					public void mouseExited(MouseEvent e) {
						iconLabel.setIcon(ImageIO
								.getResourceAsImageIcon("/1802188396.png"));
						iconLabelPanel.validate();
					}

					@Override
					public void mousePressed(MouseEvent evt) {
						UUID authCode = MainForm.get().getAuthCode();
						UUID callId = packet.getCallId();
						ctx2.get()
								.getOutboundHandler()
								.dispatch(
										ctx2.get(),
										new PacketPlayOutDeclineCallRequest(
												authCode, callId));
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

			{
				JPanel iconLabelPanel = new JPanel();
				iconLabelPanel
						.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
				ImageIcon imageIcon = ImageIO
						.getResourceAsImageIcon("/1047540480.png");
				JLabel iconLabel = new JLabel(imageIcon);

				iconLabelPanel.setBounds(329, 77, 82, 26);
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
								.getResourceAsImageIcon("/1105733532.png"));
						iconLabelPanel.validate();
					}

					@Override
					public void mouseExited(MouseEvent e) {
						iconLabel.setIcon(ImageIO
								.getResourceAsImageIcon("/1047540480.png"));
						iconLabelPanel.validate();
					}

					@Override
					public void mousePressed(MouseEvent evt) {
						UUID authCode = MainForm.get().getAuthCode();
						UUID callId = packet.getCallId();
						ctx2.get()
								.getOutboundHandler()
								.dispatch(
										ctx2.get(),
										new PacketPlayOutDeclineCallRequest(
												authCode, callId));
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

			{
				JPanel iconLabelPanel = new JPanel();
				iconLabelPanel
						.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
				ImageIcon imageIcon = ImageIO
						.getResourceAsImageIcon("/533554849.png");
				JLabel iconLabel = new JLabel(imageIcon);

				iconLabelPanel.setBounds(173, 77, 152, 26);
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
								.getResourceAsImageIcon("/744760571.png"));
						iconLabelPanel.validate();
					}

					@Override
					public void mouseExited(MouseEvent e) {
						iconLabel.setIcon(ImageIO
								.getResourceAsImageIcon("/533554849.png"));
						iconLabelPanel.validate();
					}

					@Override
					public void mousePressed(MouseEvent evt) {
						answerCall();
					}
				};

				iconLabel.addMouseListener(mouseAdapter);

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
						.getResourceAsImageIcon("/1036542902.png");
				JLabel iconLabel = new JLabel(imageIcon);

				iconLabelPanel.setBounds(87, 77, 82, 26);
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
								.getResourceAsImageIcon("/585560858.png"));
						iconLabelPanel.validate();
					}

					@Override
					public void mouseExited(MouseEvent e) {
						iconLabel.setIcon(ImageIO
								.getResourceAsImageIcon("/1036542902.png"));
						iconLabelPanel.validate();
					}

					@Override
					public void mousePressed(MouseEvent evt) {
						answerCall();
					}
				};

				iconLabel.addMouseListener(mouseAdapter);

				/**
				 * Panel added to pane with z-index 1
				 */
				layeredPane.add(iconLabelPanel, new Integer(1), 0);
			}

			{
				JPanel labelPanel = new JPanel();
				JLabel label = new JLabel(
						conversation == null ? "Anna Davies calling"
								: conversation.getDisplayName() + " calling");
				label.setFont(FontIO.TAHOMA_BOLD.deriveFont(19.0f));
				label.setForeground(new Color(255, 255, 255, 235));
				labelPanel.setOpaque(false);
				labelPanel.add(label);
				int width = label.getPreferredSize().width;
				int height = label.getPreferredSize().height;
				labelPanel.setBounds(87, 31, width, height + 10);

				/**
				 * Panel added to pane with z-index 1, above background
				 */
				layeredPane.add(labelPanel, new Integer(1), 0);
			}
		}
	}

	@Override
	public void show() {
		super.show();
		thread.start();
	}

}
