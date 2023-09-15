package codes.wilma24.Skype.api.v1_0_R1.packet;

import java.util.Date;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutLookupConversationLastAccessed extends Packet {

	private UUID authCode;

	private UUID conversationId;

	public PacketPlayOutLookupConversationLastAccessed(UUID authCode,
			UUID conversationId) {
		super(PacketType.LOOKUP_CONVERSATION_LAST_ACCESSED);
		this.setAuthCode(authCode);
		this.setConversationId(conversationId);
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

}
