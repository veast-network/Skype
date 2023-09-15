package codes.wilma24.Skype.v1_0_R1.uicommon;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;


public class JTranslucentPanel extends JPanel {

	private float panelAlfa;
	private float childrenAlfa;

	public JTranslucentPanel(float panelAlfa, float childrenAlfa) {
		this.panelAlfa = panelAlfa;
		this.childrenAlfa = childrenAlfa;
	}

	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(getBackground());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				panelAlfa));
		super.paintComponent(g2d);

	}

	@Override
	protected void paintChildren(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g2d.setColor(getBackground());
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
				childrenAlfa));

		super.paintChildren(g);
	}
	// getter and setter
}