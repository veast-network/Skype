package codes.wilma24.Skype.api.v1_0_R1.packet;

public class PacketPlayInLogin extends Packet {

	private String authCode, token;

	public PacketPlayInLogin(String authCode, String token) {
		super(PacketType.LOGIN_IN);
		this.setAuthCode(authCode);
		this.setToken(token);
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
