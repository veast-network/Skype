package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInAcceptFileDataStreamRequest extends Packet {

	private UUID authCode;

	private UUID fileTransferId;

	public PacketPlayInAcceptFileDataStreamRequest(UUID authCode,
			UUID fileTransferId) {
		super(PacketType.ACCEPT_FILE_DATA_STREAM_REQUEST_IN);
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
