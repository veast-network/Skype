package codes.wilma24.SkypeChatViewer.v1_0_R1.data.types;

import java.util.Date;

import codes.wilma24.Skype.api.v1_0_R1.gson.GsonBuilder;

import com.google.gson.Gson;

public class Message implements Comparable<Message> {
	
	private volatile long timestamp;

	private volatile String displayName;
	
	private volatile String username;

	private volatile String msgData;

	public Message(long timestamp, String displayName, String username, String message) {
		this.timestamp = timestamp;
		this.displayName = displayName;
		this.username = username;
		this.msgData = message;
	}

	public Message(String json) {
		Gson gson = GsonBuilder.create();
		Message clazz = gson.fromJson(json, Message.class);
		this.timestamp = clazz.timestamp;
		this.displayName = clazz.displayName;
		this.username = clazz.username;
		this.msgData = clazz.msgData;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return msgData;
	}

	public void setMessage(String message) {
		this.msgData = message;
	}
	
	@Override
	public String toString() {
		return exportAsJson();
	}

	public String exportAsJson() {
		Gson gson = GsonBuilder.create();
		return gson.toJson(this);
	}

	@Override
	public int hashCode() {
		return msgData.hashCode();
	}

	@Override
	public int compareTo(Message msg) {
		return new Date(timestamp).compareTo(new Date(msg.getTimestamp()));
	}
}
