package codes.wilma24.Skype.v1_0_R1.command;

import java.util.Date;
import java.util.Optional;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInAcceptContactRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLookupOnlineStatus;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLookupUser;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.AppDelegate;
import codes.wilma24.Skype.v1_0_R1.data.types.Contact;
import codes.wilma24.Skype.v1_0_R1.data.types.Conversation;
import codes.wilma24.Skype.v1_0_R1.data.types.Status;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;
import codes.wilma24.Skype.v1_0_R1.plugin.Skype;

public class AcceptContactRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInAcceptContactRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayInAcceptContactRequest.class);
		UUID conversationId = packet.getConversationId(); // contact
		UUID participantId = packet.getParticipantId(); // myself
		UUID authCode = MainForm.get().getAuthCode();

		Conversation[] conversations = MainForm.get().getConversations()
				.toArray(new Conversation[0]).clone();

		for (Conversation conversation : conversations) {
			if (conversation.getUniqueId().equals(conversationId)) {
				/*
				 * We have found the contact that we want to add
				 */
				Optional<SocketHandlerContext> ctx2 = Skype.getPlugin()
						.createHandle();
				if (ctx2.isPresent()) {
					Optional<PacketPlayInReply> replyPacket = ctx2
							.get()
							.getOutboundHandler()
							.dispatch(
									ctx2.get(),
									new PacketPlayOutLookupUser(authCode,
											conversationId));
					if (replyPacket.isPresent()) {
						if (replyPacket.get().getStatusCode() == 200) {
							/*
							 * We will remove the old person that is not a
							 * contact
							 */
							MainForm.get().getConversations()
									.remove(conversation);
							boolean searchingUser = MainForm.get()
									.getSearchTextFieldConversations()
									.remove(conversation);

							/*
							 * We will now add back our person but as a contact
							 * instead
							 */
							Contact contact = new Contact(replyPacket.get()
									.getText());
							contact.getMessages().addAll(
									conversation.getMessages());
							contact.setLastModified(new Date(new Date().getTime() + AppDelegate.TIME_OFFSET));
							Status onlineStatus = Status.OFFLINE;
							{
								PacketPlayOutLookupOnlineStatus onlineStatusLookup = new PacketPlayOutLookupOnlineStatus(
										authCode, conversation.getUniqueId());
								replyPacket = ctx2
										.get()
										.getOutboundHandler()
										.dispatch(ctx2.get(),
												onlineStatusLookup);
								onlineStatus = Status.valueOf(replyPacket.get()
										.getText());
							}
							contact.setOnlineStatus(onlineStatus);
							MainForm.get().getConversations().add(contact);
							if (searchingUser) {
								MainForm.get()
										.getSearchTextFieldConversations()
										.add(contact);
							}
							MainForm.get().setSelectedConversation(contact);
							MainForm.get().refreshWindow();
							break;
						}
					}
				}
			}
		}
		return PacketPlayInReply.empty();
	}

}
