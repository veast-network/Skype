package codes.elisa32.Skype.api.v1_0_R1.packet;

import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;


public class PacketPlayInVideoCallParticipantsChanged extends Packet {

	private UUID callId;
	
	private Object payload;

	public PacketPlayInVideoCallParticipantsChanged(UUID callId, Object payload) {
		super(PacketType.VIDEO_CALL_PARTICIPANTS_CHANGED_IN);
		this.setCallId(callId);
		this.setPayload(payload.toString());
	}

	public UUID getCallId() {
		return callId;
	}

	public void setCallId(UUID callId) {
		this.callId = callId;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

}
