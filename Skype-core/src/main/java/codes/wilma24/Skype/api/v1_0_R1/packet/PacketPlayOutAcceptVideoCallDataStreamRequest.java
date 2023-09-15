package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutAcceptVideoCallDataStreamRequest extends Packet {

	private UUID authCode;

	private UUID participantId;

	private UUID callId;

	public PacketPlayOutAcceptVideoCallDataStreamRequest(UUID authCode,
			UUID participantId, UUID callId) {
		super(PacketType.ACCEPT_VIDEO_CALL_DATA_STREAM_REQUEST);
		this.setAuthCode(authCode);
		this.setParticipantId(participantId);
		this.setCallId(callId);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
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
