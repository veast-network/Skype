package codes.elisa32.Skype.v1_0_R1.data.types;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Optional;

import javax.swing.ImageIcon;

import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.forms.MainForm;
import codes.elisa32.Skype.v1_0_R1.imageio.ImageIO;

import com.google.gson.Gson;

public class Message implements Comparable<Message> {

	private volatile UUID uuid;

	public volatile UUID sender;

	public volatile String message;

	public volatile long timestamp;

	public volatile MessageType messageType;

	public volatile transient boolean deleted = false;

	public Message(UUID uuid, UUID sender, String message, long timestamp) {
		this.setUniqueId(uuid);
		this.setSender(sender);
		this.setMessage(message);
		this.setTimestamp(timestamp);
	}

	public Message(UUID uuid, UUID sender, MessageType messageType,
			String message, long timestamp) {
		this.setUniqueId(uuid);
		this.setSender(sender);
		this.setMessageType(messageType);
		this.setMessage(message);
		this.setTimestamp(timestamp);
	}

	public Message(String json) {
		Gson gson = GsonBuilder.create();
		Message clazz = gson.fromJson(json, Message.class);
		this.uuid = clazz.uuid;
		this.sender = clazz.sender;
		this.message = clazz.message;
		this.timestamp = clazz.timestamp;
		this.messageType = clazz.messageType;
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

	public UUID getSender() {
		return sender;
	}

	public void setSender(UUID sender) {
		this.sender = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public ImageIcon getImageIcon() {
		if (sender == null) {
			switch (messageType) {
			case CALL_IN:
				return new ImageIcon(new BufferedImage(30, 30,
						BufferedImage.TYPE_INT_ARGB));
			case CALL_END:
				return new ImageIcon(new BufferedImage(30, 30,
						BufferedImage.TYPE_INT_ARGB));
			default:
				return new ImageIcon(new BufferedImage(30, 30,
						BufferedImage.TYPE_INT_ARGB));
			}
		} else {
			Optional<Conversation> sender = MainForm.get().getConversation(
					this.sender);
			if (sender.isPresent()) {
				return sender.get().getImageIcon();
			}
			return new ImageIcon(new BufferedImage(30, 30,
					BufferedImage.TYPE_INT_ARGB));
		}
	}

	public MessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MessageType messageType) {
		this.messageType = messageType;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	@Override
	public int compareTo(Message msg) {
		return new Date(timestamp).compareTo(new Date(msg.getTimestamp()));
	}
}
