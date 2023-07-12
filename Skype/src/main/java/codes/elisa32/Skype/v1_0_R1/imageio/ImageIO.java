package codes.elisa32.Skype.v1_0_R1.imageio;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import codes.elisa32.Skype.v1_0_R1.data.types.Status;

public class ImageIO {

	private static volatile Map<String, ImageIcon> images = new HashMap<>();

	private static volatile Map<String, ImageIcon> circularImages = new HashMap<>();

	private static volatile Map<String, ImageIcon> scaledImages = new HashMap<>();

	public static InputStream getResourceAsStream(String resource) {
		if (resource.startsWith("/")) {
			resource = resource.substring(1);
		}
		try {
			InputStream is = ImageIO.class.getResource("/images/" + resource)
					.openStream();
			return is;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ImageIcon getResourceAsImageIcon(String resource) {
		if (resource.startsWith("/")) {
			resource = resource.substring(1);
		}
		if (images.containsKey(resource)) {
			return images.get(resource);
		} else {
			try {
				InputStream resourceUrl = ImageIO.class.getResource(
						"/images/" + resource).openStream();
				ImageIcon imageIcon = new ImageIcon(
						javax.imageio.ImageIO.read(resourceUrl));
				images.put(resource, imageIcon);
				return imageIcon;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}

	public static JPanel getConversationIconPanel(ImageIcon imageIcon) {
		return getConversationIconPanel(imageIcon, null);
	}

	public static JPanel getConversationIconPanel(ImageIcon imageIcon,
			Status status) {
		JPanel panel = new JPanel();
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
		panel.setOpaque(false);

		JLayeredPane layeredPane = new JLayeredPane();
		layeredPane.setBounds(0, 0, 40, 50);
		layeredPane.setPreferredSize(new Dimension(40, 50));
		layeredPane.setOpaque(false);

		/**
		 * Add profile icon floating in top left panel
		 */
		{
			JPanel iconLabelPanel = new JPanel();

			imageIcon = ImageIO.getScaledImageIcon(imageIcon, new Dimension(40,
					40));
			imageIcon = ImageIO.getCircularImageIcon(imageIcon);
			JLabel iconLabel = new JLabel(imageIcon);

			iconLabelPanel.setBounds(0, 0, 40, 50);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 0
			 */
			layeredPane.add(iconLabelPanel, new Integer(0), 0);
		}

		/**
		 * Add status icon floating in top left panel
		 */
		if (status != null) {
			JPanel iconLabelPanel = new JPanel();

			JLabel iconLabel = new JLabel(ImageIO.getCircularStatusIcon(status,
					Color.white));

			iconLabelPanel.setBounds(27, 27, 14, 24);
			iconLabelPanel.setOpaque(false);
			iconLabelPanel.add(iconLabel);

			/**
			 * Panel added to pane with z-index 1
			 */
			layeredPane.add(iconLabelPanel, new Integer(1), 0);
		}

		panel.add(layeredPane);

		return panel;
	}

	public static ImageIcon getCircularStatusIcon(Status status,
			Color borderColor) {
		ImageIcon imageIcon;
		switch (status) {
		case ONLINE:
			imageIcon = getResourceAsImageIcon("/2051703251.png");
			break;
		case AWAY:
			imageIcon = getResourceAsImageIcon("/2036617426.png");
			break;
		case DO_NOT_DISTURB:
			imageIcon = getResourceAsImageIcon("/1229195039.png");
			break;
		case OFFLINE:
			imageIcon = getResourceAsImageIcon("/1552924175.png");
			break;
		case INVISIBLE:
			imageIcon = getResourceAsImageIcon("/1552924175.png");
			break;
		default:
			imageIcon = getResourceAsImageIcon("/1738805368.png");
			break;
		}

		if (borderColor != null) {
			/**
			 * We add a circle around the status icon image
			 */
			Image img = imageIcon.getImage();
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			BufferedImage bImg = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = bImg.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.drawImage(img, 0, 0, null);
			g2.setColor(borderColor);
			g2.drawOval(0, 0, width - 1, height - 1);
			g2.dispose();
			return new ImageIcon(bImg);
		}

		return imageIcon;
	}

	public static ImageIcon getCircularStatusIcon(Status status) {
		return getCircularStatusIcon(status, null);
	}

	public static ImageIcon getCircularImageIcon(ImageIcon imgIcon) {
		if (circularImages.containsKey(imgIcon.toString())) {
			return circularImages.get(imgIcon.toString());
		} else {
			Image img = imgIcon.getImage();
			int width = img.getWidth(null);
			int height = img.getHeight(null);
			BufferedImage bImg = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = bImg.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			int circleDiameter = Math.min(width, height);
			Ellipse2D.Double circle = new Ellipse2D.Double(0, 0,
					circleDiameter, circleDiameter);
			g2.setClip(circle);
			g2.drawImage(img, 0, 0, null);
			g2.dispose();
			ImageIcon newImgIcon = new ImageIcon(bImg);
			circularImages.put(imgIcon.toString(), newImgIcon);
			return newImgIcon;
		}
	}

	public static ImageIcon getScaledImageIcon(ImageIcon imgIcon,
			Dimension dimension) {
		if (scaledImages.containsKey(imgIcon.toString() + dimension.toString())) {
			return scaledImages.get(imgIcon.toString() + dimension.toString());
		} else {
			Image newImg = imgIcon.getImage().getScaledInstance(
					dimension.width, dimension.height, Image.SCALE_SMOOTH);
			ImageIcon newImgIcon = new ImageIcon(newImg);
			scaledImages.put(imgIcon.toString() + dimension.toString(),
					newImgIcon);
			return newImgIcon;
		}
	}

}
