package codes.elisa32.Skype.api.v1_0_R1.packet;

import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutRemoveMessage extends Packet {

	private UUID authCode;

	private UUID conversationId;

	private UUID messageId;

	public PacketPlayOutRemoveMessage(UUID authCode, UUID conversationId,
			UUID messageId) {
		super(PacketType.REMOVE_MESSAGE_OUT);
		this.setAuthCode(authCode);
		this.setConversationId(conversationId);
		this.setMessageId(messageId);
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

}
