package codes.elisa32.Skype.api.v1_0_R1.packet;


public class PacketPlayOutRegister extends Packet {

	private volatile String fullName;

	private volatile String skypeName;

	private volatile String password;
	
	private final int protocolVersion = PacketPlayOutLogin.PROTOCOL_VERSION;

	public PacketPlayOutRegister(String fullName, String skypeName, String password) {
		super(PacketType.REGISTER);
		this.setFullName(fullName);
		this.setSkypeName(skypeName);
		this.setPassword(password);
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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

	public int getProtocolVersion() {
		return protocolVersion;
	}

}
