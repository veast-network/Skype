package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutUpdateUser extends Packet {

	private UUID authCode;

	private UUID conversationId;

	private Object payload;
	
	private boolean silent = false;

	public PacketPlayOutUpdateUser(UUID authCode, UUID conversationId,
			Object payload) {
		super(PacketType.UPDATE_USER);
		this.setAuthCode(authCode);
		this.setConversationId(conversationId);
		this.setPayload(payload.toString());
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

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

	public boolean isSilent() {
		return silent;
	}

	public void setSilent(boolean silent) {
		this.silent = silent;
	}

}
