package codes.wilma24.Skype.v1_0_R1.command;

import java.util.Optional;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInRemoveMessage;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.data.types.Conversation;
import codes.wilma24.Skype.v1_0_R1.data.types.Message;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;

public class RemoveMessageCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInRemoveMessage packet = Packet.fromJson(msg.toString(),
				PacketPlayInRemoveMessage.class);
		UUID conversationId = packet.getConversationId();
		UUID participantId = packet.getParticipantId();
		UUID messageId = packet.getMessageId();
		Conversation matchingConversation = null;
		for (Conversation conversation : MainForm.get().getConversations()) {
			if (conversation.getUniqueId().equals(conversationId)) {
				matchingConversation = conversation;
				for (Message message : conversation.getMessages()
						.toArray(new Message[0]).clone()) {
					if (message.getUniqueId().equals(messageId)) {
						if (participantId.equals(conversationId)) {
							message.setMessage(conversation.getDisplayName()
									+ " deleted this message");
							message.setDecryptedMessage(conversation
									.getDisplayName() + " deleted this message");
						} else if (participantId.equals(MainForm.get()
								.getLoggedInUser().getUniqueId())) {
							message.setMessage("You deleted this message");
							message.setDecryptedMessage("You deleted this message");
						} else {
							Optional<Conversation> userLookup = MainForm.get()
									.lookupUser(participantId);
							if (userLookup.isPresent()) {
								message.setMessage(userLookup.get()
										.getDisplayName()
										+ " deleted this message");
								message.setDecryptedMessage(userLookup.get()
										.getDisplayName()
										+ " deleted this message");
							}
						}
						message.setDeleted(true);
						if (MainForm.get().rightPanelPage
								.equals("Conversation")) {
							if (MainForm.get().getSelectedConversation()
									.getUniqueId()
									.equals(conversation.getUniqueId())) {
								MainForm.get().refreshWindow(
										MainForm.get().SCROLL_TO_BOTTOM);
							}
						}
						break;
					}
				}
				break;
			}
		}
		if (matchingConversation != null) {
			boolean isChatGone = true;
			for (Message message : matchingConversation.getMessages()
					.toArray(new Message[0]).clone()) {
				if (message.isDeleted() == false) {
					isChatGone = false;
					break;
				}
			}
			if (isChatGone) {
				MainForm.get().getConversations().remove(matchingConversation);
				if (MainForm.get().rightPanelPage.equals("Conversation")) {
					if (MainForm.get().getSelectedConversation().getUniqueId()
							.equals(matchingConversation.getUniqueId())) {
						MainForm.get().setSelectedConversation(null);
						MainForm.get().rightPanelPage = "AccountHome";
					} else {
						MainForm.get().refreshWindow(
								MainForm.get().SCROLL_TO_BOTTOM);
						return PacketPlayInReply.empty();
					}
				}
				MainForm.get().refreshWindow();
			}
		}
		return PacketPlayInReply.empty();
	}

}
