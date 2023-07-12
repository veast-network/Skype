package codes.elisa32.Skype.v1_0_R1.data.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.swing.ImageIcon;

import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.imageio.ImageIO;

import com.google.gson.Gson;

public class Conversation {

	public volatile UUID uuid;

	public volatile String name;

	public volatile boolean groupChat = false;

	public volatile transient long lastModified;

	public volatile transient int notificationCount;

	private volatile transient boolean hasIncomingFriendRequest = false;

	private volatile transient Message incomingFriendRequestMessage = null;

	private volatile transient boolean hasOutgoingFriendRequest = false;

	public volatile transient ImageIcon imageIcon;

	public volatile transient List<Message> messages = new ArrayList<>();

	public Conversation() {

	}

	public Conversation(String json) {
		Gson gson = GsonBuilder.create();
		Conversation clazz = gson.fromJson(json, Conversation.class);
		this.uuid = clazz.uuid;
		this.name = clazz.name;
		this.groupChat = clazz.groupChat;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Conversation) {
			Conversation conversation = (Conversation) obj;
			if (uuid.toString().equals(conversation.uuid.toString())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return exportAsJson();
	}

	public String exportAsJson() {
		Gson gson = GsonBuilder.create();
		return gson.toJson(this);
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public void setUniqueId(UUID uuid) {
		this.uuid = uuid;
	}

	public String getDisplayName() {
		return name;
	}

	public void setDisplayName(String name) {
		this.name = name;
	}

	public Date getLastModified() {
		return new Date(lastModified);
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified.getTime();
	}

	public int getNotificationCount() {
		return notificationCount;
	}

	public void setNotificationCount(int notificationCount) {
		this.notificationCount = notificationCount;
	}

	public boolean isGroupChat() {
		return groupChat;
	}

	public void setGroupChat(boolean groupChat) {
		this.groupChat = groupChat;
	}

	public boolean hasIncomingFriendRequest() {
		return hasIncomingFriendRequest;
	}

	public void setHasIncomingFriendRequest(boolean hasIncomingFriendRequest,
			Message message) {
		this.hasIncomingFriendRequest = hasIncomingFriendRequest;
		this.incomingFriendRequestMessage = message;
	}

	public Optional<Message> getIncomingFriendRequestMessage() {
		if (incomingFriendRequestMessage == null) {
			return Optional.empty();
		}
		return Optional.of(incomingFriendRequestMessage);
	}

	public boolean hasOutgoingFriendRequest() {
		return hasOutgoingFriendRequest;
	}

	public void setHasOutgoingFriendRequest(boolean hasOutgoingFriendRequest) {
		this.hasOutgoingFriendRequest = hasOutgoingFriendRequest;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public ImageIcon getImageIcon() {
		if (imageIcon == null) {
			if (groupChat) {
				return imageIcon;
			} else {
				return ImageIO
						.getResourceAsImageIcon("/1595064335.png");
			}
		} else {
			return imageIcon;
		}
	}

	public void setImageIcon(ImageIcon imageIcon) {
		this.imageIcon = imageIcon;
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

}
