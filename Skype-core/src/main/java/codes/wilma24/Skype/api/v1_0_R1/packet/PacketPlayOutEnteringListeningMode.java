package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutEnteringListeningMode extends Packet {
	
	private UUID authCode;

	public PacketPlayOutEnteringListeningMode(UUID authCode) {
		super(PacketType.ENTERING_LISTEN_MODE);
		this.setAuthCode(authCode);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

}
