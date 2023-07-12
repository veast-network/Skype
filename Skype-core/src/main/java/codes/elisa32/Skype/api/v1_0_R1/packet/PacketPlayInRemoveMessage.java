package codes.elisa32.Skype.api.v1_0_R1.packet;

import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;

public class PacketPlayInRemoveMessage extends Packet  {
	
	private UUID conversationId;
	
	private UUID messageId;

	public PacketPlayInRemoveMessage(UUID conversationId, UUID messageId) {
		super(PacketType.REMOVE_MESSAGE_IN);
		this.setConversationId(conversationId);
		this.setMessageId(messageId);
	}

	public UUID getConversationId() {
		return conversationId;
	}

	public void setConversationId(UUID conversationId) {
		this.conversationId = conversationId;
	}

	public UUID getMessageId() {
		return messageId;
	}

	public void setMessageId(UUID messageId) {
		this.messageId = messageId;
	}

}
