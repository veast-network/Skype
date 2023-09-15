package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutAcceptCallRequest extends Packet {

	private UUID authCode;

	private UUID callId;

	public PacketPlayOutAcceptCallRequest(UUID authCode, UUID callId) {
		super(PacketType.ACCEPT_CALL_REQUEST);
		this.setAuthCode(authCode);
		this.setCallId(callId);
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

}
