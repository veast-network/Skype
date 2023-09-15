package codes.wilma24.Skype.v1_0_R1.forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import codes.wilma24.Skype.v1_0_R1.data.types.Conversation;
import codes.wilma24.Skype.v1_0_R1.fontio.FontIO;
import codes.wilma24.Skype.v1_0_R1.uicommon.CopyOfJButtonRounded;

public class AddToContactsForm extends JDialog implements FocusListener,
		KeyListener {

	private JTextArea textArea;

	public AddToContactsForm(JFrame parent, Conversation conversation,
			Runnable callback) {
		super(parent, "Add to Contacts", false);
		this.textArea = new JTextArea("Hi " + conversation.getDisplayName()
				+ ", I'd like to add you as a contact.");
		JDialog dialog = this;
		setResizable(false);
		setUndecorated(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setPreferredSize(new Dimension(304, 148));

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, 304, 148);
		layeredPane.setPreferredSize(new Dimension(304, 148));
		layeredPane.setOpaque(true);
		layeredPane.setBackground(Color.white);
		layeredPane.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2,
				new Color(0, 175, 240)));

		{
			JPanel labelPanel = new JPanel();
			labelPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
			labelPanel.setOpaque(false);

			JLabel label = new JLabel("Send " + conversation.getDisplayName()
					+ " a contact request");
			label.setFont(FontIO.SEGOE_UI_BOLD.deriveFont(Font.TRUETYPE_FONT,
					11));

			int width = label.getPreferredSize().width;
			int height = label.getPreferredSize().height;

			labelPanel.add(label);

			labelPanel.setBounds(15, 13, width, height + 10);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(labelPanel, new Integer(0), 0);
		}

		{
			JLayeredPane textAreaPanel = new JLayeredPane();
			textAreaPanel.setOpaque(true);
			textAreaPanel.setBackground(Color.white);
			textAreaPanel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1,
					new Color(228, 236, 239)));
			textAreaPanel.setBounds(14, 41, 276, 55);

			textArea.setFont(FontIO.SEGOE_UI.deriveFont(Font.TRUETYPE_FONT, 11));
			textArea.setBounds(6, 2, 263, 45);
			textArea.setLineWrap(true);
			textArea.selectAll();

			textAreaPanel.add(textArea, new Integer(0), 0);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(textAreaPanel, new Integer(0), 0);
		}

		{

			CopyOfJButtonRounded button = new CopyOfJButtonRounded(
					new Dimension(110, 30), "Send", 35);
			button.setBounds(180, 104, 110, 30);
			button.setBackground(new Color(35, 170, 226));

			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					callback.input = textArea.getText();
					callback.run();
					dialog.dispatchEvent(new WindowEvent(dialog,
							WindowEvent.WINDOW_CLOSING));
				}

			});

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(button, new Integer(0), 0);
		}

		setContentPane(layeredPane);
		pack();
		setLocationRelativeTo(null);
		textArea.addFocusListener(this);
		addKeyListener(this);
		textArea.addKeyListener(this);
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
