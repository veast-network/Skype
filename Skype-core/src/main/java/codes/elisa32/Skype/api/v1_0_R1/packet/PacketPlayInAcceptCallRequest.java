package codes.elisa32.Skype.api.v1_0_R1.packet;

import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInAcceptCallRequest extends Packet {

	private UUID participantId;
	
	private UUID callId;

	public PacketPlayInAcceptCallRequest(UUID participantId, UUID callId) {
		super(PacketType.ACCEPT_CALL_REQUEST_IN);
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
