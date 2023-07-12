package codes.elisa32.Skype.v1_0_R1.command;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInRemoveMessage;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.data.types.Conversation;
import codes.elisa32.Skype.v1_0_R1.data.types.Message;
import codes.elisa32.Skype.v1_0_R1.data.types.MessageType;
import codes.elisa32.Skype.v1_0_R1.forms.MainForm;

public class RemoveMessageCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInRemoveMessage packet = Packet.fromJson(msg.toString(),
				PacketPlayInRemoveMessage.class);
		UUID conversationId = packet.getConversationId();
		UUID messageId = packet.getMessageId();
		for (Conversation conversation : MainForm.get().getConversations()) {
			if (conversation.getUniqueId().equals(conversationId)) {
				for (Message message : conversation.getMessages()) {
					if (message.getUniqueId().equals(messageId)) {
						if (message.getMessageType() == MessageType.SEND_FRIEND_REQUEST) {
							message.setDeleted(true);
						} else {
							message.setMessage("This message has been removed.");
							message.setMessageType(null);
							message.setDeleted(true);
						}
						MainForm.get().refreshWindow();
						break;
					}
				}
			}
		}
		return PacketPlayInReply.empty();
	}

}
