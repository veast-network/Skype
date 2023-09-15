package codes.wilma24.Skype.api.v1_0_R1.packet;

import java.util.Date;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutLookupMessageHistory extends Packet {

	private UUID authCode;

	private UUID conversationId;

	private Date from, to;

	public PacketPlayOutLookupMessageHistory(UUID authCode,
			UUID conversationId, Date from, Date to) {
		super(PacketType.LOOKUP_MESSAGE_HISTORY);
		this.setAuthCode(authCode);
		this.setConversationId(conversationId);
		this.setFrom(from);
		this.setTo(to);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

	public UUID getConversationId() {
		return conversationId;
	}

	public void setConversationId(UUID conversationId) {
		this.conversationId = conversationId;
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
