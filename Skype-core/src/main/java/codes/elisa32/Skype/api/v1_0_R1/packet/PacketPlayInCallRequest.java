package codes.elisa32.Skype.api.v1_0_R1.packet;

import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInCallRequest extends Packet {
	private UUID conversationId;
	private UUID participantId;
	private UUID callId;

	public PacketPlayInCallRequest(UUID conversationId, UUID participantId, UUID callId) {
		super(PacketType.CALL_REQUEST_IN);
		this.setConversationId(conversationId);
		this.setParticipantId(participantId);
		this.setCallId(callId);
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

	public UUID getCallId() {
		return callId;
	}

	public void setCallId(UUID callId) {
		this.callId = callId;
	}

}
