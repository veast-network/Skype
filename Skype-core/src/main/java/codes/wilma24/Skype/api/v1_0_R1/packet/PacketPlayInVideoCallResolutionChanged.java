package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInVideoCallResolutionChanged extends Packet {

	private UUID participantId;

	private UUID callId;

	private int width;

	private int height;

	public PacketPlayInVideoCallResolutionChanged(UUID participantId,
			UUID callId, int width, int height) {
		super(PacketType.VIDEO_CALL_RESOLUTION_CHANGED_IN);
		this.setParticipantId(participantId);
		this.setCallId(callId);
		this.setWidth(width);
		this.setHeight(height);
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

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
