package codes.wilma24.Skype.v1_0_R1.forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import codes.wilma24.Skype.v1_0_R1.imageio.ImageIO;

public class AboutSkypeForm extends JDialog implements MouseListener,
		FocusListener, KeyListener {

	public AboutSkypeForm(JFrame parent) throws FontFormatException,
			IOException {
		super(parent, "About Skype", false);
		JPanel panel = new JPanel();
		getContentPane().setBackground(Color.white);
		panel.setBackground(Color.white);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		Font font = new Font("Seriff", Font.TRUETYPE_FONT, 11).deriveFont(
				Font.TRUETYPE_FONT, 11F);
		{
			JPanel groupPanel = new JPanel();
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/2033640590.png");
			JLabel iconLabel = new JLabel(imageIcon);
			groupPanel.add(iconLabel);
			panel.add(groupPanel);
		}
		panel.add(Box.createRigidArea(new Dimension(10, 34)));
		{
			JPanel groupPanel = new JPanel();
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
			ImageIcon imageIcon = ImageIO
					.getResourceAsImageIcon("/156472449.png");
			JLabel iconLabel = new JLabel(imageIcon);
			groupPanel.add(iconLabel);
			panel.add(groupPanel);
		}
		panel.add(Box.createRigidArea(new Dimension(10, 20)));
		{
			JPanel groupPanel = new JPanel();
			groupPanel.setBackground(Color.white);
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
			JLabel label = new JLabel("Skype\u2122   Version 7.11.32.102");
			label.setFont(font.deriveFont(Font.BOLD, 11));
			groupPanel.add(label);
			panel.add(groupPanel);
		}
		panel.add(Box.createRigidArea(new Dimension(10, 20)));
		{
			JPanel groupPanel = new JPanel();
			groupPanel.setBackground(Color.white);
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
			JLabel label = new JLabel("\u00a9 2015 Skype and/or Microsoft");
			label.setFont(font);
			groupPanel.add(label);
			panel.add(groupPanel);
		}
		{
			JPanel groupPanel = new JPanel();
			groupPanel.setBackground(Color.white);
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
			JLabel label = new JLabel("Patents Pending");
			label.setFont(font);
			groupPanel.add(label);
			panel.add(groupPanel);
		}
		{
			JPanel groupPanel = new JPanel();
			groupPanel.setBackground(Color.white);
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
			JLabel label = new JLabel(
					"The Skype Name, associated trademarks and logos and the \"S\" logo are");
			label.setFont(font);
			groupPanel.add(label);
			panel.add(groupPanel);
		}
		{
			JPanel groupPanel = new JPanel();
			groupPanel.setBackground(Color.white);
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
			JLabel label = new JLabel(
					"trademarks of Skype or related entities.");
			label.setFont(font);
			groupPanel.add(label);
			panel.add(groupPanel);
		}
		panel.add(Box.createRigidArea(new Dimension(10, 15)));
		{
			JPanel groupPanel = new JPanel();
			groupPanel.setBackground(Color.white);
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
			JLabel label = new JLabel(
					"Warning: This program is protected by copyright law and international");
			label.setFont(font);
			groupPanel.add(label);
			panel.add(groupPanel);
		}
		{
			JPanel groupPanel = new JPanel();
			groupPanel.setBackground(Color.white);
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
			JLabel label = new JLabel(
					"treaties. Unauthorized reproduction or distribution of this program, or any");
			label.setFont(font);
			groupPanel.add(label);
			panel.add(groupPanel);
		}
		{
			JPanel groupPanel = new JPanel();
			groupPanel.setBackground(Color.white);
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
			JLabel label = new JLabel(
					"portions of it, may result in severe civil and criminal penalties, and will be");
			label.setFont(font);
			groupPanel.add(label);
			panel.add(groupPanel);
		}
		{
			JPanel groupPanel = new JPanel();
			groupPanel.setBackground(Color.white);
			groupPanel.setLayout(new BoxLayout(groupPanel, BoxLayout.X_AXIS));
			JLabel label = new JLabel(
					"prosecuted to the maximum extent possible under the law.");
			label.setFont(font);
			groupPanel.add(label);
			panel.add(groupPanel);
		}
		add(panel);
		setUndecorated(true);
		getContentPane().setPreferredSize(new Dimension(481, 424));
		pack();
		setLocationRelativeTo(parent);
		addMouseListener(this);
		addFocusListener(this);
		addKeyListener(this);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

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

}
