package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutSendMessage extends Packet {

	private UUID authCode;

	private UUID conversationId;

	private UUID messageId;

	private Object payload;

	private long timestamp;

	public PacketPlayOutSendMessage(UUID authCode, UUID conversationId,
			UUID messageId, Object payload, long timestamp) {
		super(PacketType.MESSAGE_OUT);
		this.setAuthCode(authCode);
		this.setConversationId(conversationId);
		this.setMessageId(messageId);
		this.setPayload(payload.toString());
		this.setTimestamp(timestamp);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

	public UUID getConversationId() {
		return conversationId;
	}

	public void setConversationId(UUID conversationId) {
		this.conversationId = conversationId;
	}

	public UUID getMessageId() {
		return messageId;
	}

	public void setMessageId(UUID messageId) {
		this.messageId = messageId;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

}
