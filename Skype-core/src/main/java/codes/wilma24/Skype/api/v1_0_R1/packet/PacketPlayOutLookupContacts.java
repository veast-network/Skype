package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutLookupContacts extends Packet {

	private UUID authCode;

	public PacketPlayOutLookupContacts(UUID authCode) {
		super(PacketType.LOOKUP_CONTACTS);
		this.setAuthCode(authCode);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

}
