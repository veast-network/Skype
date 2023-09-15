package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutDeclineContactRequest extends Packet {

	private UUID authCode;

	private UUID conversationId;

	private UUID contactRequestMessageId;

	private long contactRequestTimestamp;

	public PacketPlayOutDeclineContactRequest(UUID authCode,
			UUID conversationId, UUID contactRequestMessageId,
			long contactRequestTimestamp) {
		super(PacketType.DECLINE_CONTACT_REQUEST);
		this.setAuthCode(authCode);
		this.setConversationId(conversationId);
		this.setContactRequestMessageId(contactRequestMessageId);
		this.setContactRequestTimestamp(contactRequestTimestamp);
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

	public UUID getContactRequestMessageId() {
		return contactRequestMessageId;
	}

	public void setContactRequestMessageId(UUID contactRequestMessageId) {
		this.contactRequestMessageId = contactRequestMessageId;
	}

	public long getContactRequestTimestamp() {
		return contactRequestTimestamp;
	}

	public void setContactRequestTimestamp(long contactRequestTimestamp) {
		this.contactRequestTimestamp = contactRequestTimestamp;
	}

}
