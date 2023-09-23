package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutUpdateVoipContacts extends Packet {

	private UUID authCode;

	private Object payload;

	public PacketPlayOutUpdateVoipContacts(UUID authCode, Object payload) {
		super(PacketType.UPDATE_VOIP_CONTACTS);
		this.setAuthCode(authCode);
		this.setPayload(payload.toString());
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

}
