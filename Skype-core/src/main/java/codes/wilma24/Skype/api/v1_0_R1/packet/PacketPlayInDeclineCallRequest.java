package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInDeclineCallRequest extends Packet {

	private UUID participantId;

	private UUID callId;

	public PacketPlayInDeclineCallRequest(UUID participantId, UUID callId) {
		super(PacketType.DECLINE_CALL_REQUEST_IN);
		this.setParticipantId(participantId);
		this.setCallId(callId);
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
