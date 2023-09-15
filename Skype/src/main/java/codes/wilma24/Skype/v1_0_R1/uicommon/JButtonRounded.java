package codes.wilma24.Skype.v1_0_R1.uicommon;

import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

public class JButtonRounded extends Component {

	ActionListener actionListener;
	String label;
	protected boolean pressed = false;

	protected int radius;

	/**
	 * Constructs a RoundedButton with no label.
	 */
	public JButtonRounded(int radius) {
		this("", radius);
	}

	/**
	 * Constructs a RoundedButton with the specified label.
	 *
	 * @param label
	 *            the label of the button
	 */
	public JButtonRounded(String label, int radius) {
		this.label = label;
		this.radius = radius;
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	/**
	 * gets the label
	 *
	 * @see setLabel
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * sets the label
	 *
	 * @see getLabel
	 */
	public void setLabel(String label) {
		this.label = label;
		invalidate();
		repaint();
	}

	/**
	 * paints the RoundedButton
	 */
	@Override
	public void paint(Graphics g) {

		// paint the interior of the button
		if (pressed) {
			g.setColor(getBackground().darker().darker());
		} else {
			g.setColor(getBackground());
		}
		g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);

		// draw the label centered in the button
		Font f = getFont();
		if (f != null) {
			FontMetrics fm = getFontMetrics(getFont());
			g.setColor(getForeground());
			g.drawString(label, getWidth() / 2 - fm.stringWidth(label) / 2,
					getHeight() / 2 + fm.getMaxDescent() + 1);
		}
	}

	/**
	 * The preferred size of the button.
	 */
	@Override
	public Dimension getPreferredSize() {
		Font f = getFont();
		if (f != null) {
			FontMetrics fm = getFontMetrics(getFont());
			int max = Math.max(fm.stringWidth(label) + 40, fm.getHeight() + 40);
			return new Dimension(max, max);
		} else {
			return new Dimension(100, 100);
		}
	}

	/**
	 * The minimum size of the button.
	 */
	@Override
	public Dimension getMinimumSize() {
		return new Dimension(100, 100);
	}

	/**
	 * Adds the specified action listener to receive action events from this
	 * button.
	 *
	 * @param listener
	 *            the action listener
	 */
	public void addActionListener(ActionListener listener) {
		actionListener = AWTEventMulticaster.add(actionListener, listener);
		enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	}

	/**
	 * Removes the specified action listener so it no longer receives action
	 * events from this button.
	 *
	 * @param listener
	 *            the action listener
	 */
	public void removeActionListener(ActionListener listener) {
		actionListener = AWTEventMulticaster.remove(actionListener, listener);
	}

	/**
	 * Determine if click was inside round button.
	 */
	@Override
	public boolean contains(int x, int y) {
		int mx = getSize().width / 2;
		int my = getSize().height / 2;
		return (((mx - x) * (mx - x) + (my - y) * (my - y)) <= mx * mx);
	}

	/**
	 * Paints the button and distribute an action event to all listeners.
	 */
	@Override
	public void processMouseEvent(MouseEvent e) {
		Graphics g;
		switch (e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			pressed = true;
			repaint();
			break;
		case MouseEvent.MOUSE_RELEASED:
			if (actionListener != null) {
				actionListener.actionPerformed(new ActionEvent(this,
						ActionEvent.ACTION_PERFORMED, label));
			}
			if (pressed == true) {
				pressed = false;
				repaint();
			}
			break;
		case MouseEvent.MOUSE_ENTERED:

			break;
		case MouseEvent.MOUSE_EXITED:
			if (pressed == true) {
				pressed = false;
				repaint();
			}
			break;
		}
		super.processMouseEvent(e);
	}
}