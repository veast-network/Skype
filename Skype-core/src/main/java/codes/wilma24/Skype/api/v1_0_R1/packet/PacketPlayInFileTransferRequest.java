package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInFileTransferRequest extends Packet {
	private UUID conversationId;
	private UUID participantId;
	private UUID fileTransferId;
	private String fileName;
	private long length;
	private String cipher;

	public PacketPlayInFileTransferRequest(UUID conversationId,
			UUID participantId, UUID fileTransferId, String fileName,
			long length, String cipher) {
		super(PacketType.FILE_TRANSFER_REQUEST_IN);
		this.setConversationId(conversationId);
		this.setParticipantId(participantId);
		this.setFileTransferId(fileTransferId);
		this.setFileName(fileName);
		this.setLength(length);
		this.setCipher(cipher);
	}

	public UUID getConversationId() {
		return conversationId;
	}

	public void setConversationId(UUID conversationId) {
		this.conversationId = conversationId;
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
