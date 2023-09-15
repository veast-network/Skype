package codes.wilma24.Skype.api.v1_0_R1.packet;

import java.util.Date;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutLookupConversationHistory extends Packet {

	private UUID authCode;

	private Date from, to;

	public PacketPlayOutLookupConversationHistory(UUID authCode, Date from,
			Date to) {
		super(PacketType.LOOKUP_CONVERSATION_HISTORY);
		this.setAuthCode(authCode);
		this.setFrom(from);
		this.setTo(to);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

}
