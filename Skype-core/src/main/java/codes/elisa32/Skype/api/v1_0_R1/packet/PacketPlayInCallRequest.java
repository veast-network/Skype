package codes.elisa32.Skype.api.v1_0_R1.packet;

import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInCallRequest extends Packet {
	private UUID conversationId;
	private UUID callId;

	public PacketPlayInCallRequest(UUID conversationId, UUID callId) {
		super(PacketType.CALL_REQUEST_IN);
		this.setConversationId(conversationId);
		this.setCallId(callId);
	}

	public UUID getConversationId() {
		return conversationId;
	}

	public void setConversationId(UUID conversationId) {
		this.conversationId = conversationId;
	}

	public UUID getCallId() {
		return callId;
	}

	public void setCallId(UUID callId) {
		this.callId = callId;
	}

}
