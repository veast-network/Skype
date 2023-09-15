package codes.wilma24.Skype.v1_0_R1.uicommon;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class JGradientPanel extends JPanel {

	private Color c1, c2;

	public JGradientPanel(Color c1, Color c2) {
		this.c1 = c1;
		this.c2 = c2;
		this.setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		int w = getWidth();
		int h = getHeight();
		GradientPaint gp = new GradientPaint(0, 0, c1, 0, h, c2);
		g2d.setPaint(gp);
		g2d.fillRect(0, 0, w, h);
	}
}