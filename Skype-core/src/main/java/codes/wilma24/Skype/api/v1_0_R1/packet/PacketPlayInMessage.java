package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInMessage extends Packet {

	private UUID conversationId;

	private UUID participantId;

	private Object payload;

	public PacketPlayInMessage(UUID conversationId, UUID participantId,
			Object payload) {
		super(PacketType.MESSAGE_IN);
		this.setConversationId(conversationId);
		this.setParticipantId(participantId);
		this.setPayload(payload.toString());
	}

	public UUID getConversationId() {
		return conversationId;
	}

	public void setConversationId(UUID conversationId) {
		this.conversationId = conversationId;
	}

	public UUID getParticipantId() {
		return participantId;
	}

	public void setParticipantId(UUID participantId) {
		this.participantId = participantId;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

}
