package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutSendVideoCallRequest extends Packet {

	private UUID authCode;

	private UUID conversationId;
	
	private String cipher;

	public PacketPlayOutSendVideoCallRequest(UUID authCode, UUID conversationId, String cipher) {
		super(PacketType.SEND_VIDEO_CALL_REQUEST);
		this.setAuthCode(authCode);
		this.setConversationId(conversationId);
		this.setCipher(cipher);
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

	public String getCipher() {
		return cipher;
	}

	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

}
