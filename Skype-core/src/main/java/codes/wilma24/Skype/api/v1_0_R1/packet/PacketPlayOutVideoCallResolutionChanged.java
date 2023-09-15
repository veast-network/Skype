package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutVideoCallResolutionChanged extends Packet {

	private UUID authCode;

	private UUID callId;

	private int width;

	private int height;

	public PacketPlayOutVideoCallResolutionChanged(UUID authCode, UUID callId,
			int width, int height) {
		super(PacketType.VIDEO_CALL_RESOLUTION_CHANGED);
		this.setAuthCode(authCode);
		this.setCallId(callId);
		this.setWidth(width);
		this.setHeight(height);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
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
