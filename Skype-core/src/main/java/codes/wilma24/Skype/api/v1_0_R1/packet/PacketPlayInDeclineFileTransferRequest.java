package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInDeclineFileTransferRequest extends Packet {

	private UUID participantId;

	private UUID fileTransferId;

	public PacketPlayInDeclineFileTransferRequest(UUID participantId,
			UUID fileTransferId) {
		super(PacketType.DECLINE_FILE_TRANSFER_REQUEST_IN);
		this.setParticipantId(participantId);
		this.setFileTransferId(fileTransferId);
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
