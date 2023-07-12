package codes.elisa32.Skype.api.v1_0_R1.packet;

import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInRemoveContact extends Packet {

	private UUID conversationId;

	private UUID participantId;

	public PacketPlayInRemoveContact(UUID conversationId, UUID participantId) {
		super(PacketType.REMOVE_CONTACT_IN);
		this.setConversationId(conversationId);
		this.setParticipantId(participantId);
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

}
