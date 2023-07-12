package codes.elisa32.Skype.api.v1_0_R1.packet;

public class PacketPlayInReply extends Packet {

	/**
	 * HTML Status Codes
	 */
	public static final int BAD_REQUEST = 400;
	public static final int NOT_IMPLEMENTED = 501;
	public static final int OK = 200;

	private int statusCode;

	private String text;

	public PacketPlayInReply(int statusCode, String text) {
		super(PacketType.REPLY);
		this.setStatusCode(statusCode);
		this.setText(text);
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public static PacketPlayInReply empty() {
		return null;
	}

}
