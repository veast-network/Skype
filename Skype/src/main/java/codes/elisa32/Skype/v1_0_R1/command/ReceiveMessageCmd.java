package codes.elisa32.Skype.v1_0_R1.command;

import java.util.Date;
import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInMessage;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLookupUser;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.data.types.Contact;
import codes.elisa32.Skype.v1_0_R1.data.types.Conversation;
import codes.elisa32.Skype.v1_0_R1.data.types.Message;
import codes.elisa32.Skype.v1_0_R1.data.types.MessageType;
import codes.elisa32.Skype.v1_0_R1.forms.MainForm;
import codes.elisa32.Skype.v1_0_R1.forms.NotificationForm;
import codes.elisa32.Skype.v1_0_R1.plugin.Skype;

public class ReceiveMessageCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInMessage packet = Packet.fromJson(msg.toString(),
				PacketPlayInMessage.class);
		UUID authCode = MainForm.get().getAuthCode();
		Contact loggedInUser = MainForm.get().getLoggedInUser();
		UUID conversationId = packet.getConversationId();
		UUID participantId = packet.getParticipantId();
		String json = packet.getPayload().toString();
		Message message = new Message(json);
		boolean hit = false;
		Conversation _conversation = null;
		for (Conversation conversation : MainForm.get().getConversations()) {
			if (conversation.getUniqueId().equals(conversationId)) {
				if (conversation.isGroupChat()) {
					message.setParticipantId(participantId);
				}
				conversation.getMessages().add(message);
				conversation.setNotificationCount(conversation
						.getNotificationCount() + 1);
				conversation.setLastModified(new Date());
				if (message.getMessageType() != null) {
					if (message.getMessageType() == MessageType.SEND_FRIEND_REQUEST) {
						if (!message.getSender().equals(
								loggedInUser.getUniqueId())) {
							conversation.setHasIncomingFriendRequest(true,
									message);
						} else {
							conversation.setHasOutgoingFriendRequest(true);
						}
					}
					if (message.getMessageType() == MessageType.DECLINE_FRIEND_REQUEST
							|| message.getMessageType() == MessageType.ACCEPT_FRIEND_REQUEST) {
						if (message.getSender().equals(
								loggedInUser.getUniqueId())) {
							conversation.setHasIncomingFriendRequest(false,
									null);
						} else {
							conversation.setHasOutgoingFriendRequest(false);
						}
					}
				}
				_conversation = conversation;
				hit = true;
			}
		}
		if (hit == false) {
			Conversation conversation = MainForm.get()
					.getSelectedConversation();
			if (conversation != null) {
				if (conversation.getUniqueId().equals(conversationId)) {
					if (conversation.isGroupChat()) {
						message.setParticipantId(participantId);
					}
					conversation.getMessages().add(message);
					conversation.setNotificationCount(conversation
							.getNotificationCount() + 1);
					conversation.setLastModified(new Date());
					if (message.getMessageType() != null) {
						if (message.getMessageType() == MessageType.SEND_FRIEND_REQUEST) {
							if (!message.getSender().equals(
									loggedInUser.getUniqueId())) {
								conversation.setHasIncomingFriendRequest(true,
										message);
							} else {
								conversation.setHasOutgoingFriendRequest(true);
							}
						}
						if (message.getMessageType() == MessageType.DECLINE_FRIEND_REQUEST
								|| message.getMessageType() == MessageType.ACCEPT_FRIEND_REQUEST) {
							if (message.getSender().equals(
									loggedInUser.getUniqueId())) {
								conversation.setHasIncomingFriendRequest(false,
										null);
							} else {
								conversation.setHasOutgoingFriendRequest(false);
							}
						}
					}
					_conversation = conversation;
					MainForm.get().getConversations().add(conversation);
					hit = true;
				}
			}
		}
		if (hit == false) {
			Optional<SocketHandlerContext> socketHandlerContext = Skype
					.getPlugin().createHandle();
			if (!socketHandlerContext.isPresent()) {
				return PacketPlayInReply.empty();
			}
			Optional<PacketPlayInReply> replyPacket = socketHandlerContext
					.get()
					.getOutboundHandler()
					.dispatch(
							socketHandlerContext.get(),
							new PacketPlayOutLookupUser(authCode,
									conversationId));
			if (!replyPacket.isPresent()) {
				return PacketPlayInReply.empty();
			}
			Conversation conversation = new Conversation(replyPacket.get()
					.getText());
			if (conversation.isGroupChat()) {
				message.setParticipantId(participantId);
			} else {
				conversation.setDisplayName(conversation.getSkypeName());
			}
			conversation.getMessages().add(message);
			conversation.setNotificationCount(1);
			conversation.setLastModified(new Date());
			if (message.getMessageType() != null) {
				if (message.getMessageType() == MessageType.SEND_FRIEND_REQUEST) {
					if (!message.getSender().equals(loggedInUser.getUniqueId())) {
						conversation.setHasIncomingFriendRequest(true, message);
					} else {
						conversation.setHasOutgoingFriendRequest(true);
					}
				}
				if (message.getMessageType() == MessageType.DECLINE_FRIEND_REQUEST
						|| message.getMessageType() == MessageType.ACCEPT_FRIEND_REQUEST) {
					if (message.getSender().equals(loggedInUser.getUniqueId())) {
						conversation.setHasIncomingFriendRequest(false, null);
					} else {
						conversation.setHasOutgoingFriendRequest(false);
					}
				}
			}
			_conversation = conversation;
			MainForm.get().getConversations().add(conversation);
		}
		NotificationForm notif = new NotificationForm(_conversation, message,
				true);
		notif.show();
		MainForm.get().refreshWindow();
		return PacketPlayInReply.empty();
	}
}
