package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInFileTransferParticipantsChanged extends Packet {

	private UUID fileTransferId;

	private Object payload;

	public PacketPlayInFileTransferParticipantsChanged(UUID fileTransferId,
			Object payload) {
		super(PacketType.FILE_TRANSFER_PARTICIPANTS_CHANGED_IN);
		this.setFileTransferId(fileTransferId);
		this.setPayload(payload.toString());
	}

	public UUID getFileTransferId() {
		return fileTransferId;
	}

	public void setFileTransferId(UUID fileTransferId) {
		this.fileTransferId = fileTransferId;
	}

	public Object getPayload() {
		return payload;
	}

	public void setPayload(Object payload) {
		this.payload = payload;
	}

}
