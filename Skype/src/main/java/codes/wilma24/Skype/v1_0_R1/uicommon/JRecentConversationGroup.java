package codes.wilma24.Skype.v1_0_R1.uicommon;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import codes.wilma24.Skype.v1_0_R1.data.types.Conversation;

public class JRecentConversationGroup {

	private String group;

	private List<Conversation> conversations;

	public JRecentConversationGroup(String group,
			List<Conversation> conversations) {
		this.setGroup(group);

		/**
		 * We will sort the conversations based on the last modified date
		 */
		Collections.sort(conversations, new Comparator<Conversation>() {
			@Override
			public int compare(Conversation a, Conversation b) {
				return b.getLastModified().compareTo(a.getLastModified());
			}
		});

		this.setConversations(conversations);
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public List<Conversation> getConversations() {
		return conversations;
	}

	public void setConversations(List<Conversation> conversations) {
		this.conversations = conversations;
	}

}
