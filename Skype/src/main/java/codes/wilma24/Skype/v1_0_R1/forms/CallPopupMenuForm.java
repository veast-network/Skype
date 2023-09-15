package codes.wilma24.Skype.v1_0_R1.forms;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import codes.wilma24.Skype.v1_0_R1.fontio.FontIO;

public class CallPopupMenuForm extends JDialog implements FocusListener,
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

	public CallPopupMenuForm(JFrame parent, Runnable callback) {
		super(parent, "Call Popout Menu", false);
		JDialog dialog = this;
		setResizable(false);
		setUndecorated(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		int panelWidth = 171;
		int panelHeight = 185;
		getContentPane().setPreferredSize(
				new Dimension(panelWidth, panelHeight));

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, panelWidth, panelHeight);
		layeredPane.setPreferredSize(new Dimension(171, 185));
		layeredPane.setOpaque(true);
		layeredPane.setBackground(new Color(0, 0, 0, (int) (255 * 0.8)));
		setBackground(new Color(0, 0, 0, 0));

		JLayeredPane layeredPane2 = new JLayeredPane();
		layeredPane2.setBounds(0, 0, panelWidth, panelHeight);
		layeredPane2.setPreferredSize(new Dimension(171, 185));
		layeredPane2.setOpaque(false);
		layeredPane.add(layeredPane2, new Integer(0), 0);

		layeredPane.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
				Color.white));

		int x = -1;
		int y = -4;

		Font font = FontIO.SEGOE_UI_SYMBOL.deriveFont(13f);

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Send files...");
			label.setFont(font);
			label.setForeground(Color.white);

			MouseAdapter mouseAdapter = new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent evt) {
					if (hoverPanel != null) {
						layeredPane2.removeAll();
					}
					hoverPanel = constructHoverPanel(panelWidth, 19);
					layeredPane2.add(hoverPanel, new Integer(0), 0);
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent evt) {
					if (hoverPanel != null) {
						layeredPane2.removeAll();
						repaint();
						hoverPanel = null;
					}
				}

				@Override
				public void mousePressed(MouseEvent evt) {
					dispatchEvent(new WindowEvent(dialog,
							WindowEvent.WINDOW_CLOSING));
					callback.input = label.getText();
					callback.run();
				}
			};

			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			label.addMouseListener(mouseAdapter);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(16 + x, 19 + y, width, height + 10);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(labelPanel, new Integer(1), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Send Contacts...");
			label.setFont(font);
			label.setForeground(Color.white);

			MouseAdapter mouseAdapter = new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent evt) {
					if (hoverPanel != null) {
						layeredPane2.removeAll();
					}
					hoverPanel = constructHoverPanel(panelWidth, 51);
					layeredPane2.add(hoverPanel, new Integer(0), 0);
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent evt) {
					if (hoverPanel != null) {
						layeredPane2.removeAll();
						repaint();
						hoverPanel = null;
					}
				}

				@Override
				public void mousePressed(MouseEvent evt) {
					dispatchEvent(new WindowEvent(dialog,
							WindowEvent.WINDOW_CLOSING));
					callback.input = label.getText();
					callback.run();
				}
			};

			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			label.addMouseListener(mouseAdapter);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(16 + x, 51 + y, width, height + 10);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(labelPanel, new Integer(1), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Share screens...");
			if (MainForm.get().videoMode != MainForm.get().WEBCAM_CAPTURE_MODE) {
				label.setText("Stop sharing");
			}
			label.setFont(font);
			label.setForeground(Color.white);

			MouseAdapter mouseAdapter = new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent evt) {
					if (hoverPanel != null) {
						layeredPane2.removeAll();
					}
					hoverPanel = constructHoverPanel(panelWidth, 83);
					layeredPane2.add(hoverPanel, new Integer(0), 0);
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent evt) {
					if (hoverPanel != null) {
						layeredPane2.removeAll();
						repaint();
						hoverPanel = null;
					}
				}

				@Override
				public void mousePressed(MouseEvent evt) {
					dispatchEvent(new WindowEvent(dialog,
							WindowEvent.WINDOW_CLOSING));
					callback.input = label.getText();
					callback.run();
				}
			};

			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			label.addMouseListener(mouseAdapter);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(16 + x, 83 + y, width, height + 10);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(labelPanel, new Integer(1), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(true);
			labelPanel.setBackground(new Color(255, 255, 255, 155));

			labelPanel.setBounds(8, 108, 155, 1);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(labelPanel, new Integer(1), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Add people to this call...");
			label.setFont(font);
			label.setForeground(Color.white);

			MouseAdapter mouseAdapter = new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent evt) {
					if (hoverPanel != null) {
						layeredPane2.removeAll();
					}
					hoverPanel = constructHoverPanel(panelWidth, 124);
					layeredPane2.add(hoverPanel, new Integer(0), 0);
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent evt) {
					if (hoverPanel != null) {
						layeredPane2.removeAll();
						repaint();
						hoverPanel = null;
					}
				}

				@Override
				public void mousePressed(MouseEvent evt) {
					dispatchEvent(new WindowEvent(dialog,
							WindowEvent.WINDOW_CLOSING));
					callback.input = label.getText();
					callback.run();
				}
			};

			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			label.addMouseListener(mouseAdapter);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(16 + x, 124 + y, width, height + 10);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(labelPanel, new Integer(1), 0);
		}

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Show dial pad");
			label.setFont(font);
			label.setForeground(Color.white);

			MouseAdapter mouseAdapter = new MouseAdapter() {
				@Override
				public void mouseEntered(MouseEvent evt) {
					if (hoverPanel != null) {
						layeredPane2.removeAll();
					}
					hoverPanel = constructHoverPanel(panelWidth, 156);
					layeredPane2.add(hoverPanel, new Integer(0), 0);
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent evt) {
					if (hoverPanel != null) {
						layeredPane2.removeAll();
						repaint();
						hoverPanel = null;
					}
				}

				@Override
				public void mousePressed(MouseEvent evt) {
					dispatchEvent(new WindowEvent(dialog,
							WindowEvent.WINDOW_CLOSING));
					callback.input = label.getText();
					callback.run();
				}
			};

			label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			label.addMouseListener(mouseAdapter);

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(16 + x, 156 + y, width, height + 10);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(labelPanel, new Integer(1), 0);
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

		private String input;

		public String getInput() {
			return input;
		}

	}

}
