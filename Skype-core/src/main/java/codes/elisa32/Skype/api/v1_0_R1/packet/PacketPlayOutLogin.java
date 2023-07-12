package codes.elisa32.Skype.api.v1_0_R1.packet;

import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutLogin extends Packet {

	private UUID authCode;

	private String skypeName;

	private String password;

	public PacketPlayOutLogin(UUID authCode) {
		super(PacketType.LOGIN);
		this.setAuthCode(authCode);
	}

	public PacketPlayOutLogin(String skypeName, String password) {
		super(PacketType.LOGIN);
		this.setSkypeName(skypeName);
		this.setPassword(password);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

	public String getSkypeName() {
		return skypeName;
	}

	public void setSkypeName(String skypeName) {
		this.skypeName = skypeName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
