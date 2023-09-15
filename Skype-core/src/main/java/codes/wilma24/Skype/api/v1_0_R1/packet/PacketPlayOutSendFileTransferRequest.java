package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayOutSendFileTransferRequest extends Packet {

	private UUID authCode;

	private UUID conversationId;

	private UUID fileTransferId;

	private String fileName;

	private long length;

	private String cipher;

	public PacketPlayOutSendFileTransferRequest(UUID authCode,
			UUID conversationId, UUID fileTransferId, String fileName,
			long length, String cipher) {
		super(PacketType.SEND_FILE_TRANSFER_REQUEST);
		this.setAuthCode(authCode);
		this.setConversationId(conversationId);
		this.setFileTransferId(fileTransferId);
		this.setFileName(fileName);
		this.setLength(length);
		this.setCipher(cipher);
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

	public UUID getFileTransferId() {
		return fileTransferId;
	}

	public void setFileTransferId(UUID fileTransferId) {
		this.fileTransferId = fileTransferId;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getCipher() {
		return cipher;
	}

	public void setCipher(String cipher) {
		this.cipher = cipher;
	}

}
