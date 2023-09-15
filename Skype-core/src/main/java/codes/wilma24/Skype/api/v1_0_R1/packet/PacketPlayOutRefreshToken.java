package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutRefreshToken extends Packet {

	private UUID authCode;

	public PacketPlayOutRefreshToken(UUID authCode) {
		super(PacketType.REFRESH_TOKEN);
		this.setAuthCode(authCode);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

}
