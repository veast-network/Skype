package codes.elisa32.Skype.api.v1_0_R1.packet;

import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutUserSearch extends Packet {
	
	private UUID authCode;
	
	private String input;

	public PacketPlayOutUserSearch(UUID authCode, String input) {
		super(PacketType.USER_SEARCH);
		this.setAuthCode(authCode);
		this.setInput(input);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

}
