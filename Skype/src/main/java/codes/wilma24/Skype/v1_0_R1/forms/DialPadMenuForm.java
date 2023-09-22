package codes.wilma24.Skype.v1_0_R1.forms;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import codes.wilma24.Skype.v1_0_R1.audioio.AudioIO;
import codes.wilma24.Skype.v1_0_R1.imageio.ImageIO;

public class DialPadMenuForm extends JDialog implements FocusListener,
		KeyListener, WindowFocusListener {

	JPanel hoverPanel = null;

	public JPanel constructHoverPanel(int panelWidth, int y) {
		JPanel labelPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				// g.setColor(new Color(0, 150, 205));
				g.setColor(new Color(0, 150, 205));
				g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
			}
		};
		labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		labelPanel.setOpaque(false);

		labelPanel.setBounds(7, y + 7 - 18, panelWidth - 7 - 7, 32);

		return labelPanel;
	}

	public DialPadMenuForm(JFrame parent, Runnable callback) {
		super(parent, "Dial Pad Menu", false);
		JDialog dialog = this;
		setResizable(false);
		setUndecorated(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		int panelWidth = 168;
		int panelHeight = 200;
		getContentPane().setPreferredSize(
				new Dimension(panelWidth, panelHeight));

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, panelWidth, panelHeight);
		layeredPane.setPreferredSize(new Dimension(168, 200));
		layeredPane.setOpaque(true);
		layeredPane.setBackground(Color.black);
		setBackground(new Color(0, 0, 0, 0));
		
		layeredPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
				Color.white));

		// 1
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/68141093.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					callback.input = '1';
					callback.run();
					AudioIO.DTMF_1.playSound();
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(4, 40, 53, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}
		// 2
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/55038668.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					callback.input = '2';
					callback.run();
					AudioIO.DTMF_2.playSound();
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(58, 40, 52, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}
		// 3
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/88976945.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					callback.input = '3';
					callback.run();
					AudioIO.DTMF_3.playSound();
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(111, 40, 53, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}
		// 4
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/74371694.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					callback.input = '4';
					callback.run();
					AudioIO.DTMF_4.playSound();
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(4, 79, 53, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}
		// 5
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/63272712.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					callback.input = '5';
					callback.run();
					AudioIO.DTMF_5.playSound();
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(58, 79, 52, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}
		// 6
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/46694924.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					callback.input = '6';
					callback.run();
					AudioIO.DTMF_6.playSound();
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(111, 79, 53, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}
		// 7
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/76181423.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					callback.input = '7';
					callback.run();
					AudioIO.DTMF_7.playSound();
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(4, 118, 53, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}
		// 8
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/94819694.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					callback.input = '8';
					callback.run();
					AudioIO.DTMF_8.playSound();
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(58, 118, 52, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}
		// 9
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/26526882.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					callback.input = '9';
					callback.run();
					AudioIO.DTMF_9.playSound();
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(111, 118, 53, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}
		// *
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/33630163.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					callback.input = '*';
					callback.run();
					AudioIO.DTMF_STAR.playSound();
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(4, 157, 53, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}
		// 0
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/50890602.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				long before = 0L;

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					before = System.currentTimeMillis();
					AudioIO.DTMF_0.playSound();
				}

				@Override
				public void mouseReleased(MouseEvent evt) {
					super.mouseReleased(evt);
					long elapsed = System.currentTimeMillis() - before;
					if (elapsed < 500) {
						// 0
						callback.input = '0';
						callback.run();
					} else {
						// +
						callback.input = '+';
						callback.run();
					}
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(58, 157, 52, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}
		// #
		{
			JPanel iconLabelPanel = new JPanel();
			iconLabelPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/33529356.png");
			JLabel iconLabel = new JLabel(imageIcon);

			/**
			 * Reserved for future use
			 */
			iconLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			MouseAdapter mouseAdapter = new MouseAdapter() {

				@Override
				public void mousePressed(MouseEvent evt) {
					super.mousePressed(evt);
					callback.input = '#';
					callback.run();
					AudioIO.DTMF_POUND.playSound();
				}

			};

			iconLabel.addMouseListener(mouseAdapter);

			iconLabelPanel.setBounds(111, 157, 53, 39);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}

		setContentPane(layeredPane);
		pack();
		setLocationRelativeTo(null);
		addKeyListener(this);
		addWindowFocusListener(this);
	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
	}

	@Override
	public void windowLostFocus(WindowEvent arg0) {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	@Override
	public void focusGained(FocusEvent arg0) {
	}

	@Override
	public void focusLost(FocusEvent arg0) {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == KeyEvent.VK_ESCAPE) {
			dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	public abstract static class Runnable implements java.lang.Runnable {

		private char input;

		public char getInput() {
			return input;
		}

	}

}
