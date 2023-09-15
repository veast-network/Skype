package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutAcceptFileDataStreamRequest extends Packet {

	private UUID authCode;

	private UUID participantId;

	private UUID fileTransferId;

	public PacketPlayOutAcceptFileDataStreamRequest(UUID authCode,
			UUID participantId, UUID fileTransferId) {
		super(PacketType.ACCEPT_FILE_DATA_STREAM_REQUEST);
		this.setAuthCode(authCode);
		this.setParticipantId(participantId);
		this.setFileTransferId(fileTransferId);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

	public UUID getParticipantId() {
		return participantId;
	}

	public void setParticipantId(UUID participantId) {
		this.participantId = participantId;
	}

	public UUID getFileTransferId() {
		return fileTransferId;
	}

	public void setFileTransferId(UUID fileTransferId) {
		this.fileTransferId = fileTransferId;
	}

}
