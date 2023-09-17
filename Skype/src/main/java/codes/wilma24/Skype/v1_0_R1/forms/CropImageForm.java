package codes.wilma24.Skype.v1_0_R1.forms;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.BorderFactory;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import codes.wilma24.Skype.v1_0_R1.imageio.ImageIO;

public class CropImageForm extends JDialog implements MouseMotionListener,
		MouseListener, AdjustmentListener {

	private ImageIcon img;

	private int width, height;

	private JPanel topGrabber, bottomGrabber, leftGrabber, rightGrabber;

	private JPanel topLeftGrabber, topRightGrabber, bottomLeftGrabber,
			bottomRightGrabber;

	private JPanel sub;

	private JLabel label;

	private JScrollBar scrollBar;

	private JMenuItem reset = new JMenuItem("Reset");

	private Runnable callback;

	private boolean forceSquareAspectRatio;

	private Rectangle def;

	public CropImageForm(JFrame parent, ImageIcon img, Runnable callback,
			boolean forceSquareAspectRatio) {
		super(parent, "Crop Image", true);
		this.img = img;
		this.callback = callback;
		this.forceSquareAspectRatio = forceSquareAspectRatio;
		setResizable(false);
		setContentPane(new JLayeredPane());
		if (img.getIconWidth() > 640) {
			if (img.getIconWidth() > img.getIconHeight()) {
				img = ImageIO
						.getScaledImageIcon(
								img,
								new Dimension(640, (int) (((double) img
										.getIconHeight() / (double) img
										.getIconWidth()) * 640.0)));
			} else {
				img = ImageIO
						.getScaledImageIcon(
								img,
								new Dimension((int) (((double) img
										.getIconWidth() / (double) img
										.getIconHeight()) * 640.0), 640));
			}
		}
		getContentPane().setPreferredSize(
				new Dimension(img.getIconWidth(), img.getIconHeight()));
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenuItem save = new JMenuItem("Save");
		BufferedImage bi = new BufferedImage(img.getIconWidth(),
				img.getIconHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		img.paintIcon(null, g, 0, 0);
		g.dispose();
		final CropImageForm frame = this;
		save.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Rectangle bounds = sub.getBounds();
				Rectangle bounds2 = new Rectangle(bounds.x - 10, bounds.y - 10,
						bounds.width + 20, bounds.height + 20);
				if (bounds2.y < 0) {
					bounds2.height += bounds2.y;
					bounds2.y = 0;
				}
				if (bounds2.x < 0) {
					bounds2.width += bounds2.x;
					bounds2.x = 0;
				}
				if (bounds2.x + bounds2.width > bi.getWidth()) {
					bounds2.width = bi.getWidth() - bounds2.x;
				}
				if (bounds2.y + bounds2.height > bi.getHeight()) {
					bounds2.height = bi.getHeight() - bounds2.y;
				}
				BufferedImage bi2 = bi.getSubimage(bounds2.x, bounds2.y,
						bounds2.width, bounds2.height);
				ImageIcon img2 = new ImageIcon(bi2);
				callback.setSubImage(img2);
				callback.run();
				frame.dispatchEvent(new WindowEvent(frame,
						WindowEvent.WINDOW_CLOSING));
			}

		});
		reset.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrollBar.removeAdjustmentListener(frame);
				scrollBar.setValue(100);
				scrollBar.addAdjustmentListener(frame);
				int fwidth = width;
				int fheight = height;
				if (forceSquareAspectRatio) {
					fheight = fwidth;
				}
				{
					// label
					sub.setBounds(10, 10, fwidth - 20, fheight - 20);
					sub.validate();
					sub.repaint();
				}
				{
					// top grabber
					topGrabber.setBounds(10, 0, fwidth - 20, 10);
					topGrabber.validate();
					topGrabber.repaint();
				}
				{
					// bottom grabber
					bottomGrabber.setBounds(10, fheight - 10, fwidth - 20, 10);
					bottomGrabber.validate();
					bottomGrabber.repaint();
				}
				{
					// left grabber
					leftGrabber.setBounds(0, 10, 10, fheight - 20);
					leftGrabber.validate();
					leftGrabber.repaint();
				}
				{
					// right grabber
					rightGrabber.setBounds(fwidth - 10, 10, 10, fheight - 20);
					rightGrabber.validate();
					rightGrabber.repaint();
				}
				{
					// top left corner grabber
					topLeftGrabber.setBounds(0, 0, 10, 10);
					topLeftGrabber.validate();
					topLeftGrabber.repaint();
				}
				{
					// top right corner grabber
					topRightGrabber.setBounds(fwidth - 10, 0, 10, 10);
					topRightGrabber.validate();
					topRightGrabber.repaint();
				}
				{
					// bottom left corner grabber
					bottomLeftGrabber.setBounds(0, fheight - 10, 10, 10);
					bottomLeftGrabber.validate();
					bottomLeftGrabber.repaint();
				}
				{
					// bottom right corner grabber
					bottomRightGrabber.setBounds(fwidth - 10, fheight - 10, 10,
							10);
					bottomRightGrabber.validate();
					bottomRightGrabber.repaint();
				}
				frame.validate();
				frame.repaint();
				def = new Rectangle(sub.getBounds().x - 10,
						sub.getBounds().y - 10, sub.getBounds().width + 20, sub
								.getBounds().height + 20);
			}

		});
		fileMenu.add(save);
		fileMenu.add(reset);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		pack();
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLayout(null);
		width = getContentPane().getPreferredSize().width;
		height = getContentPane().getPreferredSize().height;
		getContentPane().setPreferredSize(new Dimension(width, height + 20));
		pack();
		{
			// scroll bar
			scrollBar = new JScrollBar();
			scrollBar.setModel(new DefaultBoundedRangeModel() {
				@Override
				public void setValue(int i) {
					super.setValue(i);
				}

				@Override
				public void setMaximum(int i) {
					super.setMaximum(100);
				}

				@Override
				public void setMinimum(int i) {
					super.setMinimum(1);
				}

				@Override
				public void setExtent(int i) {
					super.setExtent(i);
				}

				@Override
				public void setRangeProperties(int newValue, int newExtent,
						int newMin, int newMax, boolean adjusting) {
					super.setRangeProperties(newValue, newExtent, newMin,
							100, adjusting);
				}

			});
			scrollBar.setOrientation(JScrollBar.HORIZONTAL);
			scrollBar.setMinimum(1);
			scrollBar.setMaximum(100);
			scrollBar.setValue(100);
			scrollBar.setBounds(0, height, width, 20);
			scrollBar.addAdjustmentListener(this);
			add(scrollBar, new Integer(2), 0);
		}
		{
			// label
			label = new JLabel(img);
			label.setBounds(0, 0, img.getIconWidth(), img.getIconHeight());
			add(label, new Integer(0), 0);
		}
		{
			// label
			sub = new JPanel();
			sub.setBounds(10, 10, img.getIconWidth() - 20,
					img.getIconHeight() - 20);
			sub.setOpaque(false);
			sub.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			sub.addMouseListener(this);
			sub.addMouseMotionListener(this);
			add(sub, new Integer(1), 0);
		}
		{
			// top grabber
			topGrabber = new JPanel();
			topGrabber.setBounds(10, 0, img.getIconWidth() - 20, 10);
			topGrabber.setBackground(new Color(199, 237, 252, 125));
			topGrabber.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0,
					Color.white));
			topGrabber.setCursor(Cursor
					.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
			topGrabber.addMouseListener(this);
			topGrabber.addMouseMotionListener(this);
			add(topGrabber, new Integer(1), 0);
		}
		{
			// bottom grabber
			bottomGrabber = new JPanel();
			bottomGrabber.setBounds(10, height - 10, img.getIconWidth() - 20,
					10);
			bottomGrabber.setBackground(new Color(199, 237, 252, 125));
			bottomGrabber.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0,
					Color.white));
			bottomGrabber.setCursor(Cursor
					.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
			bottomGrabber.addMouseListener(this);
			bottomGrabber.addMouseMotionListener(this);
			add(bottomGrabber, new Integer(1), 0);
		}
		{
			// left grabber
			leftGrabber = new JPanel();
			leftGrabber.setBounds(0, 10, 10, height - 20);
			leftGrabber.setBackground(new Color(199, 237, 252, 125));
			leftGrabber.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1,
					Color.white));
			leftGrabber.setCursor(Cursor
					.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
			leftGrabber.addMouseListener(this);
			leftGrabber.addMouseMotionListener(this);
			add(leftGrabber, new Integer(1), 0);
		}
		{
			// right grabber
			rightGrabber = new JPanel();
			rightGrabber.setBounds(width - 10, 10, 10, height - 20);
			rightGrabber.setBackground(new Color(199, 237, 252, 125));
			rightGrabber.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 1,
					Color.white));
			rightGrabber.setCursor(Cursor
					.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
			rightGrabber.addMouseListener(this);
			rightGrabber.addMouseMotionListener(this);
			add(rightGrabber, new Integer(1), 0);
		}
		{
			// top left corner grabber
			topLeftGrabber = new JPanel();
			topLeftGrabber.setBounds(0, 0, 10, 10);
			topLeftGrabber.setBackground(new Color(199, 237, 252, 125));
			topLeftGrabber.setBorder(BorderFactory.createMatteBorder(1, 1, 0,
					0, Color.white));
			topLeftGrabber.setCursor(Cursor
					.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
			topLeftGrabber.addMouseListener(this);
			topLeftGrabber.addMouseMotionListener(this);
			add(topLeftGrabber, new Integer(1), 0);
		}
		{
			// top right corner grabber
			topRightGrabber = new JPanel();
			topRightGrabber.setBounds(width - 10, 0, 10, 10);
			topRightGrabber.setBackground(new Color(199, 237, 252, 125));
			topRightGrabber.setBorder(BorderFactory.createMatteBorder(1, 0, 0,
					1, Color.white));
			topRightGrabber.setCursor(Cursor
					.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
			topRightGrabber.addMouseListener(this);
			topRightGrabber.addMouseMotionListener(this);
			add(topRightGrabber, new Integer(1), 0);
		}
		{
			// bottom left corner grabber
			bottomLeftGrabber = new JPanel();
			bottomLeftGrabber.setBounds(0, height - 10, 10, 10);
			bottomLeftGrabber.setBackground(new Color(199, 237, 252, 125));
			bottomLeftGrabber.setBorder(BorderFactory.createMatteBorder(0, 1,
					1, 0, Color.white));
			bottomLeftGrabber.setCursor(Cursor
					.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
			bottomLeftGrabber.addMouseListener(this);
			bottomLeftGrabber.addMouseMotionListener(this);
			add(bottomLeftGrabber, new Integer(1), 0);
		}
		{
			// bottom right corner grabber
			bottomRightGrabber = new JPanel();
			bottomRightGrabber.setBounds(width - 10, height - 10, 10, 10);
			bottomRightGrabber.setBackground(new Color(199, 237, 252, 125));
			bottomRightGrabber.setBorder(BorderFactory.createMatteBorder(0, 0,
					1, 1, Color.white));
			bottomRightGrabber.setCursor(Cursor
					.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
			bottomRightGrabber.addMouseListener(this);
			bottomRightGrabber.addMouseMotionListener(this);
			add(bottomRightGrabber, new Integer(1), 0);
		}
		if (forceSquareAspectRatio) {
			reset.doClick();
		} else {
			def = new Rectangle(sub.getBounds().x - 10, sub.getBounds().y - 10,
					sub.getBounds().width + 20, sub.getBounds().height + 20);
		}
	}

	private Point startPos = null;
	private Grabber grabber = null;

	@Override
	public void mouseDragged(MouseEvent arg0) {
		// TODO Auto-generated method stub
		int x = arg0.getPoint().x;
		int y = arg0.getPoint().y;
		x = x - startPos.x;
		y = y - startPos.y;
		Rectangle bounds;
		if (forceSquareAspectRatio) {
			if (grabber == Grabber.LEFT) {
				grabber = Grabber.TOP_LEFT;
			} else if (grabber == Grabber.RIGHT) {
				grabber = Grabber.BOTTOM_RIGHT;
			} else if (grabber == Grabber.TOP) {
				grabber = Grabber.TOP_RIGHT;
			} else if (grabber == Grabber.BOTTOM) {
				grabber = Grabber.BOTTOM_LEFT;
			}
		}
		switch (grabber) {
		case BOTTOM:
			bounds = topGrabber.getBounds();
			topGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			topGrabber.validate();
			topGrabber.repaint();
			bounds = bottomGrabber.getBounds();
			bottomGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height);
			bottomGrabber.validate();
			bottomGrabber.repaint();
			bounds = leftGrabber.getBounds();
			leftGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height + y);
			leftGrabber.validate();
			leftGrabber.repaint();
			bounds = rightGrabber.getBounds();
			rightGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height + y);
			rightGrabber.validate();
			rightGrabber.repaint();
			bounds = topLeftGrabber.getBounds();
			topLeftGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			topLeftGrabber.validate();
			topLeftGrabber.repaint();
			bounds = topRightGrabber.getBounds();
			topRightGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			topRightGrabber.validate();
			topRightGrabber.repaint();
			bounds = bottomLeftGrabber.getBounds();
			bottomLeftGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height);
			bottomLeftGrabber.validate();
			bottomLeftGrabber.repaint();
			bounds = bottomRightGrabber.getBounds();
			bottomRightGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height);
			bottomRightGrabber.validate();
			bottomRightGrabber.repaint();
			bounds = sub.getBounds();
			sub.setBounds(bounds.x, bounds.y, bounds.width, bounds.height + y);
			sub.validate();
			sub.repaint();
			break;
		case BOTTOM_LEFT:
			if (forceSquareAspectRatio) {
				if (x == 0) {
					x = y;
				}
				x = -y;
			}
			bounds = topGrabber.getBounds();
			topGrabber.setBounds(bounds.x + x, bounds.y, bounds.width - x,
					bounds.height);
			topGrabber.validate();
			topGrabber.repaint();
			bounds = bottomGrabber.getBounds();
			bottomGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width
					- x, bounds.height);
			bottomGrabber.validate();
			bottomGrabber.repaint();
			bounds = leftGrabber.getBounds();
			leftGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height + y);
			leftGrabber.validate();
			leftGrabber.repaint();
			bounds = rightGrabber.getBounds();
			rightGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height + y);
			rightGrabber.validate();
			rightGrabber.repaint();
			bounds = topLeftGrabber.getBounds();
			topLeftGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height);
			topLeftGrabber.validate();
			topLeftGrabber.repaint();
			bounds = topRightGrabber.getBounds();
			topRightGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			topRightGrabber.validate();
			topRightGrabber.repaint();
			bounds = bottomLeftGrabber.getBounds();
			bottomLeftGrabber.setBounds(bounds.x + x, bounds.y + y,
					bounds.width, bounds.height);
			bottomLeftGrabber.validate();
			bottomLeftGrabber.repaint();
			bounds = bottomRightGrabber.getBounds();
			bottomRightGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height);
			bottomRightGrabber.validate();
			bottomRightGrabber.repaint();
			bounds = sub.getBounds();
			sub.setBounds(bounds.x + x, bounds.y, bounds.width - x,
					bounds.height + y);
			sub.validate();
			sub.repaint();
			break;
		case BOTTOM_RIGHT:
			if (forceSquareAspectRatio) {
				if (y == 0) {
					y = x;
				}
				y = x;
			}
			bounds = topGrabber.getBounds();
			topGrabber.setBounds(bounds.x, bounds.y, bounds.width + x,
					bounds.height);
			topGrabber.validate();
			topGrabber.repaint();
			bounds = bottomGrabber.getBounds();
			bottomGrabber.setBounds(bounds.x, bounds.y + y, bounds.width + x,
					bounds.height);
			bottomGrabber.validate();
			bottomGrabber.repaint();
			bounds = leftGrabber.getBounds();
			leftGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height + y);
			leftGrabber.validate();
			leftGrabber.repaint();
			bounds = rightGrabber.getBounds();
			rightGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height + y);
			rightGrabber.validate();
			rightGrabber.repaint();
			bounds = topLeftGrabber.getBounds();
			topLeftGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			topLeftGrabber.validate();
			topLeftGrabber.repaint();
			bounds = topRightGrabber.getBounds();
			topRightGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height);
			topRightGrabber.validate();
			topRightGrabber.repaint();
			bounds = bottomLeftGrabber.getBounds();
			bottomLeftGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height);
			bottomLeftGrabber.validate();
			bottomLeftGrabber.repaint();
			bounds = bottomRightGrabber.getBounds();
			bottomRightGrabber.setBounds(bounds.x + x, bounds.y + y,
					bounds.width, bounds.height);
			bottomRightGrabber.validate();
			bottomRightGrabber.repaint();
			bounds = sub.getBounds();
			sub.setBounds(bounds.x, bounds.y, bounds.width + x, bounds.height
					+ y);
			sub.validate();
			sub.repaint();
			break;
		case LEFT:
			bounds = topGrabber.getBounds();
			topGrabber.setBounds(bounds.x + x, bounds.y, bounds.width - x,
					bounds.height);
			topGrabber.validate();
			topGrabber.repaint();
			bounds = bottomGrabber.getBounds();
			bottomGrabber.setBounds(bounds.x + x, bounds.y, bounds.width - x,
					bounds.height);
			bottomGrabber.validate();
			bottomGrabber.repaint();
			bounds = leftGrabber.getBounds();
			leftGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height);
			leftGrabber.validate();
			leftGrabber.repaint();
			bounds = rightGrabber.getBounds();
			rightGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			rightGrabber.validate();
			rightGrabber.repaint();
			bounds = topLeftGrabber.getBounds();
			topLeftGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height);
			topLeftGrabber.validate();
			topLeftGrabber.repaint();
			bounds = topRightGrabber.getBounds();
			topRightGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			topRightGrabber.validate();
			topRightGrabber.repaint();
			bounds = bottomLeftGrabber.getBounds();
			bottomLeftGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height);
			bottomLeftGrabber.validate();
			bottomLeftGrabber.repaint();
			bounds = bottomRightGrabber.getBounds();
			bottomRightGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			bottomRightGrabber.validate();
			bottomRightGrabber.repaint();
			bounds = sub.getBounds();
			sub.setBounds(bounds.x + x, bounds.y, bounds.width - x,
					bounds.height);
			sub.validate();
			sub.repaint();
			break;
		case RIGHT:
			bounds = topGrabber.getBounds();
			topGrabber.setBounds(bounds.x, bounds.y, bounds.width + x,
					bounds.height);
			topGrabber.validate();
			topGrabber.repaint();
			bounds = bottomGrabber.getBounds();
			bottomGrabber.setBounds(bounds.x, bounds.y, bounds.width + x,
					bounds.height);
			bottomGrabber.validate();
			bottomGrabber.repaint();
			bounds = leftGrabber.getBounds();
			leftGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			leftGrabber.validate();
			leftGrabber.repaint();
			bounds = rightGrabber.getBounds();
			rightGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height);
			rightGrabber.validate();
			rightGrabber.repaint();
			bounds = topLeftGrabber.getBounds();
			topLeftGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			topLeftGrabber.validate();
			topLeftGrabber.repaint();
			bounds = topRightGrabber.getBounds();
			topRightGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height);
			topRightGrabber.validate();
			topRightGrabber.repaint();
			bounds = bottomLeftGrabber.getBounds();
			bottomLeftGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			bottomLeftGrabber.validate();
			bottomLeftGrabber.repaint();
			bounds = bottomRightGrabber.getBounds();
			bottomRightGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height);
			bottomRightGrabber.validate();
			bottomRightGrabber.repaint();
			bounds = sub.getBounds();
			sub.setBounds(bounds.x, bounds.y, bounds.width + x, bounds.height);
			sub.validate();
			sub.repaint();
			break;
		case TOP:
			bounds = topGrabber.getBounds();
			topGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height);
			topGrabber.validate();
			topGrabber.repaint();
			bounds = bottomGrabber.getBounds();
			bottomGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			bottomGrabber.validate();
			bottomGrabber.repaint();
			bounds = leftGrabber.getBounds();
			leftGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height - y);
			leftGrabber.validate();
			leftGrabber.repaint();
			bounds = rightGrabber.getBounds();
			rightGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height - y);
			rightGrabber.validate();
			rightGrabber.repaint();
			bounds = topLeftGrabber.getBounds();
			topLeftGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height);
			topLeftGrabber.validate();
			topLeftGrabber.repaint();
			bounds = topRightGrabber.getBounds();
			topRightGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height);
			topRightGrabber.validate();
			topRightGrabber.repaint();
			bounds = bottomLeftGrabber.getBounds();
			bottomLeftGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			bottomLeftGrabber.validate();
			bottomLeftGrabber.repaint();
			bounds = bottomRightGrabber.getBounds();
			bottomRightGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			bottomRightGrabber.validate();
			bottomRightGrabber.repaint();
			bounds = sub.getBounds();
			sub.setBounds(bounds.x, bounds.y + y, bounds.width, bounds.height
					- y);
			sub.validate();
			sub.repaint();
			break;
		case TOP_LEFT:
			if (forceSquareAspectRatio) {
				if (y == 0) {
					y = x;
				}
				y = x;
			}
			bounds = topGrabber.getBounds();
			topGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width - x,
					bounds.height);
			topGrabber.validate();
			topGrabber.repaint();
			bounds = bottomGrabber.getBounds();
			bottomGrabber.setBounds(bounds.x + x, bounds.y, bounds.width - x,
					bounds.height);
			bottomGrabber.validate();
			bottomGrabber.repaint();
			bounds = leftGrabber.getBounds();
			leftGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width,
					bounds.height - y);
			leftGrabber.validate();
			leftGrabber.repaint();
			bounds = rightGrabber.getBounds();
			rightGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height - y);
			rightGrabber.validate();
			rightGrabber.repaint();
			bounds = topLeftGrabber.getBounds();
			topLeftGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width,
					bounds.height);
			topLeftGrabber.validate();
			topLeftGrabber.repaint();
			bounds = topRightGrabber.getBounds();
			topRightGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height);
			topRightGrabber.validate();
			topRightGrabber.repaint();
			bounds = bottomLeftGrabber.getBounds();
			bottomLeftGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height);
			bottomLeftGrabber.validate();
			bottomLeftGrabber.repaint();
			bounds = bottomRightGrabber.getBounds();
			bottomRightGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			bottomRightGrabber.validate();
			bottomRightGrabber.repaint();
			bounds = sub.getBounds();
			sub.setBounds(bounds.x + x, bounds.y + y, bounds.width - x,
					bounds.height - y);
			sub.validate();
			sub.repaint();
			break;
		case TOP_RIGHT:
			if (forceSquareAspectRatio) {
				if (x == 0) {
					x = y;
				}
				x = -y;
			}
			bounds = topGrabber.getBounds();
			topGrabber.setBounds(bounds.x, bounds.y + y, bounds.width + x,
					bounds.height);
			topGrabber.validate();
			topGrabber.repaint();
			bounds = bottomGrabber.getBounds();
			bottomGrabber.setBounds(bounds.x, bounds.y, bounds.width + x,
					bounds.height);
			bottomGrabber.validate();
			bottomGrabber.repaint();
			bounds = leftGrabber.getBounds();
			leftGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height - y);
			leftGrabber.validate();
			leftGrabber.repaint();
			bounds = rightGrabber.getBounds();
			rightGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width,
					bounds.height - y);
			rightGrabber.validate();
			rightGrabber.repaint();
			bounds = topLeftGrabber.getBounds();
			topLeftGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
					bounds.height);
			topLeftGrabber.validate();
			topLeftGrabber.repaint();
			bounds = topRightGrabber.getBounds();
			topRightGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width,
					bounds.height);
			topRightGrabber.validate();
			topRightGrabber.repaint();
			bounds = bottomLeftGrabber.getBounds();
			bottomLeftGrabber.setBounds(bounds.x, bounds.y, bounds.width,
					bounds.height);
			bottomLeftGrabber.validate();
			bottomLeftGrabber.repaint();
			bounds = bottomRightGrabber.getBounds();
			bottomRightGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
					bounds.height);
			bottomRightGrabber.validate();
			bottomRightGrabber.repaint();
			bounds = sub.getBounds();
			sub.setBounds(bounds.x, bounds.y + y, bounds.width + x,
					bounds.height - y);
			sub.validate();
			sub.repaint();
			break;
		case MOVE:
			bounds = topGrabber.getBounds();
			topGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width,
					bounds.height);
			topGrabber.validate();
			topGrabber.repaint();
			bounds = bottomGrabber.getBounds();
			bottomGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width,
					bounds.height);
			bottomGrabber.validate();
			bottomGrabber.repaint();
			bounds = leftGrabber.getBounds();
			leftGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width,
					bounds.height);
			leftGrabber.validate();
			leftGrabber.repaint();
			bounds = rightGrabber.getBounds();
			rightGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width,
					bounds.height);
			rightGrabber.validate();
			rightGrabber.repaint();
			bounds = topLeftGrabber.getBounds();
			topLeftGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width,
					bounds.height);
			topLeftGrabber.validate();
			topLeftGrabber.repaint();
			bounds = topRightGrabber.getBounds();
			topRightGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width,
					bounds.height);
			topRightGrabber.validate();
			topRightGrabber.repaint();
			bounds = bottomLeftGrabber.getBounds();
			bottomLeftGrabber.setBounds(bounds.x + x, bounds.y + y,
					bounds.width, bounds.height);
			bottomLeftGrabber.validate();
			bottomLeftGrabber.repaint();
			bounds = bottomRightGrabber.getBounds();
			bottomRightGrabber.setBounds(bounds.x + x, bounds.y + y,
					bounds.width, bounds.height);
			bottomRightGrabber.validate();
			bottomRightGrabber.repaint();
			bounds = sub.getBounds();
			sub.setBounds(bounds.x + x, bounds.y + y, bounds.width,
					bounds.height);
			sub.validate();
			sub.repaint();
			break;
		default:
			break;
		}
		validate();
		repaint();
		int pos = (int) Math.floor(((double) sub.getBounds().width / (double) def.width) * 100.0);
		scrollBar.removeAdjustmentListener(this);
		scrollBar
				.setValue(pos);
		scrollBar.addAdjustmentListener(this);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		startPos = arg0.getPoint();
		if (arg0.getSource() == topGrabber) {
			grabber = Grabber.TOP;
		} else if (arg0.getSource() == bottomGrabber) {
			grabber = Grabber.BOTTOM;
		} else if (arg0.getSource() == leftGrabber) {
			grabber = Grabber.LEFT;
		} else if (arg0.getSource() == rightGrabber) {
			grabber = Grabber.RIGHT;
		} else if (arg0.getSource() == topLeftGrabber) {
			grabber = Grabber.TOP_LEFT;
		} else if (arg0.getSource() == topRightGrabber) {
			grabber = Grabber.TOP_RIGHT;
		} else if (arg0.getSource() == bottomLeftGrabber) {
			grabber = Grabber.BOTTOM_LEFT;
		} else if (arg0.getSource() == bottomRightGrabber) {
			grabber = Grabber.BOTTOM_RIGHT;
		} else if (arg0.getSource() == sub) {
			grabber = Grabber.MOVE;
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	private enum Grabber {

		TOP, BOTTOM, LEFT, RIGHT,

		TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,

		MOVE

	}

	public static abstract class Runnable implements java.lang.Runnable {

		private ImageIcon subImage;

		public void setSubImage(ImageIcon subImage) {
			this.subImage = subImage;
		}

		public ImageIcon getSubImage() {
			return subImage;
		}

	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent arg0) {
		// TODO Auto-generated method stub
		if (sub == null) {
			return;
		}
		int newValue = arg0.getValue();
		int targetWidth = (int) ((double) def.getWidth() * ((double) newValue / 100.0));
		int diffWidth = sub.getBounds().width + 20 - targetWidth;
		int targetHeight = (int) ((double) def.getHeight() * ((double) newValue / 100.0));
		int diffHeight = sub.getBounds().height + 20 - targetHeight;
		int x = -diffWidth;
		int y = -diffHeight;
		if (forceSquareAspectRatio) {
			if (y == 0) {
				y = x;
			}
			y = x;
		}
		Rectangle bounds;
		bounds = topGrabber.getBounds();
		topGrabber.setBounds(bounds.x, bounds.y, bounds.width + x,
				bounds.height);
		topGrabber.validate();
		topGrabber.repaint();
		bounds = bottomGrabber.getBounds();
		bottomGrabber.setBounds(bounds.x, bounds.y + y, bounds.width + x,
				bounds.height);
		bottomGrabber.validate();
		bottomGrabber.repaint();
		bounds = leftGrabber.getBounds();
		leftGrabber.setBounds(bounds.x, bounds.y, bounds.width, bounds.height
				+ y);
		leftGrabber.validate();
		leftGrabber.repaint();
		bounds = rightGrabber.getBounds();
		rightGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
				bounds.height + y);
		rightGrabber.validate();
		rightGrabber.repaint();
		bounds = topLeftGrabber.getBounds();
		topLeftGrabber.setBounds(bounds.x, bounds.y, bounds.width,
				bounds.height);
		topLeftGrabber.validate();
		topLeftGrabber.repaint();
		bounds = topRightGrabber.getBounds();
		topRightGrabber.setBounds(bounds.x + x, bounds.y, bounds.width,
				bounds.height);
		topRightGrabber.validate();
		topRightGrabber.repaint();
		bounds = bottomLeftGrabber.getBounds();
		bottomLeftGrabber.setBounds(bounds.x, bounds.y + y, bounds.width,
				bounds.height);
		bottomLeftGrabber.validate();
		bottomLeftGrabber.repaint();
		bounds = bottomRightGrabber.getBounds();
		bottomRightGrabber.setBounds(bounds.x + x, bounds.y + y, bounds.width,
				bounds.height);
		bottomRightGrabber.validate();
		bottomRightGrabber.repaint();
		bounds = sub.getBounds();
		sub.setBounds(bounds.x, bounds.y, bounds.width + x, bounds.height + y);
		sub.validate();
		sub.repaint();
	}

}
