package codes.wilma24.Skype.v1_0_R1.data.types;

import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.Optional;

import javax.swing.ImageIcon;

import codes.wilma24.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;
import codes.wilma24.Skype.v1_0_R1.pgp.PGPUtilities;
import codes.wilma24.Skype.v1_0_R1.pgp.PGPUtilities.DecryptionResult;

import com.google.gson.Gson;

public class Message implements Comparable<Message> {

	private volatile UUID uuid;

	public volatile UUID sender;

	public volatile String message;

	private volatile transient String decryptedMessage = null;

	private volatile transient boolean decryptionSuccessful,
			signatureVerified = false;

	public volatile long timestamp;

	public volatile MessageType messageType;

	public volatile transient boolean deleted = false;

	private volatile transient Conversation conversation;

	public Message(UUID uuid, UUID sender, String message, long timestamp,
			Conversation conversation) {
		this.setUniqueId(uuid);
		this.setSender(sender);
		this.setMessage(message);
		this.setTimestamp(timestamp);
		this.setConversation(conversation);
	}

	public Message(UUID uuid, UUID sender, MessageType messageType,
			String message, long timestamp, Conversation conversation) {
		this.setUniqueId(uuid);
		this.setSender(sender);
		this.setMessageType(messageType);
		this.setMessage(message);
		this.setTimestamp(timestamp);
		this.setConversation(conversation);
	}

	public Message(String json, Conversation conversation) {
		Gson gson = GsonBuilder.create();
		Message clazz = gson.fromJson(json, Message.class);
		this.uuid = clazz.uuid;
		this.sender = clazz.sender;
		this.message = clazz.message;
		this.timestamp = clazz.timestamp;
		this.messageType = clazz.messageType;
		this.conversation = conversation;
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

	public String getDecryptedMessage() {
		if (decryptedMessage != null) {
			return decryptedMessage;
		}
		Conversation sender = MainForm.get().getLoggedInUser();
		if (!MainForm.get().getLoggedInUser().getUniqueId().equals(this.sender)) {
			Optional<Conversation> userLookup = MainForm.get().lookupUser(
					this.sender);
			if (userLookup.isPresent()) {
				sender = userLookup.get();
			}
		}
		if (message.startsWith("-----BEGIN PGP MESSAGE-----")) {
			DecryptionResult result = PGPUtilities.decryptAndVerify(
					message, sender);
			decryptionSuccessful = result.isSuccessful();
			signatureVerified = result.isSignatureVerified();
			decryptedMessage = result.getMessage();
			return decryptedMessage;
		}
		return message;
	}

	public boolean isDecryptionSuccessful() {
		return decryptionSuccessful;
	}

	public void setDecryptionSuccessful(boolean decryptionSuccessful) {
		this.decryptionSuccessful = decryptionSuccessful;
	}

	public boolean isSignatureVerified() {
		return signatureVerified;
	}

	public void setSignatureVerified(boolean signatureVerified) {
		this.signatureVerified = signatureVerified;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setDecryptedMessage(String decryptedMessage) {
		this.decryptedMessage = decryptedMessage;
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
			Optional<Conversation> sender = MainForm.get().lookupUser(
					this.sender);
			if (sender.isPresent()) {
				return sender.get().getImageIcon();
			} else {
				return new ImageIcon(new BufferedImage(30, 30,
						BufferedImage.TYPE_INT_ARGB));
			}
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

	public Conversation getConversation() {
		return conversation;
	}

	public void setConversation(Conversation conversation) {
		this.conversation = conversation;
	}
}
