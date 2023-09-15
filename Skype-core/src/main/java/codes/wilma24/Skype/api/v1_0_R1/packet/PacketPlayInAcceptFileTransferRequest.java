package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInAcceptFileTransferRequest extends Packet {

	private UUID authCode;

	private UUID fileTransferId;

	public PacketPlayInAcceptFileTransferRequest(UUID authCode,
			UUID fileTransferId) {
		super(PacketType.ACCEPT_FILE_TRANSFER_REQUEST_IN);
		this.setAuthCode(authCode);
		this.setFileTransferId(fileTransferId);
	}

	public UUID getAuthCode() {
		return authCode;
	}

	public void setAuthCode(UUID authCode) {
		this.authCode = authCode;
	}

	public UUID getFileTransferId() {
		return fileTransferId;
	}

	public void setFileTransferId(UUID fileTransferId) {
		this.fileTransferId = fileTransferId;
	}

}
