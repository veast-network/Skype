package codes.wilma24.Skype.v1_0_R1.uicommon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CombinedAction implements ActionListener {
	private final ActionListener action1;
	private final ActionListener action2;

	public CombinedAction(ActionListener action1, ActionListener action2) {
		super();
		this.action1 = action1;
		this.action2 = action2;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (action1 != null) {
			action1.actionPerformed(e);
		}
		if (action2 != null) {
			action2.actionPerformed(e);
		}
	}

}