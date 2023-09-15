package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInUpdateUser extends Packet {

	private UUID conversationId;

	private Object payload;

	public PacketPlayInUpdateUser(UUID conversationId,
			Object payload) {
		super(PacketType.UPDATE_USER_IN);
		this.setConversationId(conversationId);
		this.setPayload(payload.toString());
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

}
