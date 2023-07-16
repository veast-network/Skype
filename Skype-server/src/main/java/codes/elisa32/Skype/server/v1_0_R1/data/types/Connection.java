package codes.elisa32.Skype.server.v1_0_R1.data.types;

import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;

public class Connection {

	private UUID authCode;

	private long expiryTime;

	private String skypeName;

	private SocketHandlerContext ctx;

	private boolean isListening = false;

	private boolean isReceivingCallDataStream = false;

	private UUID receivingCallId;

	private UUID receivingCallDataStreamParticipantId;
	
	private boolean isCallDataStreamEnded = false;

	public Connection(UUID authCode, long expiryTime, String skypeName,
			SocketHandlerContext ctx) {
		this.setAuthCode(authCode);
		this.setExpiryTime(expiryTime);
		this.setSkypeName(skypeName);
		this.setSocketHandlerContext(ctx);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

	public long getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(long expiryTime) {
		this.expiryTime = expiryTime;
	}

	public String getSkypeName() {
		return skypeName;
	}

	public void setSkypeName(String skypeName) {
		this.skypeName = skypeName;
	}

	public UUID getUniqueId() {
		return Skype.getPlugin().getUserManager().getUniqueId(skypeName);
	}

	public SocketHandlerContext getSocketHandlerContext() {
		return ctx;
	}

	public void setSocketHandlerContext(SocketHandlerContext ctx) {
		this.ctx = ctx;
	}

	public boolean isListening() {
		return isListening;
	}

	public void setListening(boolean isListening) {
		this.isListening = isListening;
	}

	public void setCallDataStream(UUID callId, UUID participantId) {
		if (callId == null) {
			this.isReceivingCallDataStream = false;
			this.receivingCallId = null;
			this.receivingCallDataStreamParticipantId = null;
		} else {
			this.isReceivingCallDataStream = true;
			this.receivingCallId = callId;
			this.receivingCallDataStreamParticipantId = participantId;
		}
	}

	public boolean isInCall() {
		return this.receivingCallId != null;
	}

	public boolean isCallDataStream() {
		return isReceivingCallDataStream;
	}

	public boolean isCallDataStreamEnded() {
		return isCallDataStreamEnded;
	}

	public void setCallDataStreamEnded(boolean isCallDataStreamEnded) {
		this.isCallDataStreamEnded = isCallDataStreamEnded;
	}

	public void setCallId(UUID callId) {
		this.receivingCallId = callId;
	}

	public Optional<UUID> getCallId() {
		if (this.receivingCallId == null) {
			return Optional.empty();
		}
		return Optional.of(this.receivingCallId);
	}

	/*
	 * This is used to get the unique id of the person we are receiving an
	 * active audio stream from. This is used in other parts of the program to
	 * be able to replicate the audio stream over other network connections.
	 */
	public Optional<UUID> getParticipantId() {
		if (this.receivingCallDataStreamParticipantId == null) {
			return Optional.empty();
		}
		return Optional.of(this.receivingCallDataStreamParticipantId);
	}

}
