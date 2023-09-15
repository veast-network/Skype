package codes.wilma24.Skype.v1_0_R1.forms;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.Scrollable;
import javax.swing.UIManager;

public class RemoveParticipantsFromGroupChatForm extends JFrame implements
		WindowFocusListener {

	public RemoveParticipantsFromGroupChatForm(List<String> skypeNames,
			Runnable callback) {
		super("Remove Participants from Group Chat");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		CheckBoxGroup checkBoxGroup = new CheckBoxGroup(
				skypeNames.toArray(new String[0]));
		panel.add(checkBoxGroup);
		final JFrame frame = this;
		{
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
			JButton button = new JButton("Remove");
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					panel.grabFocus();
					List<String> participantIds = new ArrayList<String>();
					participantIds.addAll(checkBoxGroup.getSelectedItems());
					callback.setParticipants(participantIds);
					callback.run();
					dispatchEvent(new WindowEvent(frame,
							WindowEvent.WINDOW_CLOSING));
				}

			});
			buttonPanel.add(button);
			panel.add(buttonPanel);
		}
		add(panel);
		setUndecorated(true);
		getRootPane().setBorder(
				BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(0, 175,
						240)));
		setPreferredSize(new Dimension(180, 200));
		pack();
		setLocationRelativeTo(null);
		addWindowFocusListener(this);
	}

	public class CheckBoxGroup extends JPanel {

		private JCheckBox all;
		private List<JCheckBox> checkBoxes;

		public CheckBoxGroup(String... options) {
			checkBoxes = new ArrayList<>(25);
			setLayout(new BorderLayout());
			JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
			all = new JCheckBox("Select All...");
			all.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					for (JCheckBox cb : checkBoxes) {
						cb.setSelected(all.isSelected());
					}
				}
			});
			header.add(all);
			add(header, BorderLayout.NORTH);

			JPanel content = new ScrollablePane(new GridBagLayout());
			content.setBackground(UIManager.getColor("List.background"));
			if (options.length > 0) {

				GridBagConstraints gbc = new GridBagConstraints();
				gbc.gridwidth = GridBagConstraints.REMAINDER;
				gbc.anchor = GridBagConstraints.NORTHWEST;
				gbc.weightx = 1;
				for (int index = 0; index < options.length - 1; index++) {
					JCheckBox cb = new JCheckBox(options[index]);
					cb.setOpaque(false);
					checkBoxes.add(cb);
					content.add(cb, gbc);
				}

				JCheckBox cb = new JCheckBox(options[options.length - 1]);
				cb.setOpaque(false);
				checkBoxes.add(cb);
				gbc.weighty = 1;
				content.add(cb, gbc);

			}

			add(new JScrollPane(content));
		}

		public List<String> getSelectedItems() {
			List<String> selected = new ArrayList<>();
			for (JCheckBox box : checkBoxes) {
				if (box.isSelected()) {
					selected.add(box.getText());
				}
			}
			return selected;
		}

		public class ScrollablePane extends JPanel implements Scrollable {

			public ScrollablePane(LayoutManager layout) {
				super(layout);
			}

			public ScrollablePane() {
			}

			@Override
			public Dimension getPreferredScrollableViewportSize() {
				return new Dimension(100, 100);
			}

			@Override
			public int getScrollableUnitIncrement(Rectangle visibleRect,
					int orientation, int direction) {
				return 32;
			}

			@Override
			public int getScrollableBlockIncrement(Rectangle visibleRect,
					int orientation, int direction) {
				return 32;
			}

			@Override
			public boolean getScrollableTracksViewportWidth() {
				boolean track = false;
				Container parent = getParent();
				if (parent instanceof JViewport) {
					JViewport vp = (JViewport) parent;
					track = vp.getWidth() > getPreferredSize().width;
				}
				return track;
			}

			@Override
			public boolean getScrollableTracksViewportHeight() {
				boolean track = false;
				Container parent = getParent();
				if (parent instanceof JViewport) {
					JViewport vp = (JViewport) parent;
					track = vp.getHeight() > getPreferredSize().height;
				}
				return track;
			}

		}

	}

	@Override
	public void windowGainedFocus(WindowEvent arg0) {
	}

	@Override
	public void windowLostFocus(WindowEvent arg0) {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	public abstract static class Runnable implements java.lang.Runnable {

		private List<String> participants = new ArrayList<String>();

		public void setParticipants(List<String> participants) {
			this.participants = participants;
		}

		public List<String> getParticipants() {
			return participants;
		}

	}

}