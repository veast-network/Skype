package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInCallRequest extends Packet {
	private UUID conversationId;
	private UUID participantId;
	private UUID callId;
	private String cipher;

	public PacketPlayInCallRequest(UUID conversationId, UUID participantId, UUID callId, String cipher) {
		super(PacketType.CALL_REQUEST_IN);
		this.setConversationId(conversationId);
		this.setParticipantId(participantId);
		this.setCallId(callId);
		this.setCipher(cipher);
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

	public String getCipher() {
		return cipher;
	}

	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

}
