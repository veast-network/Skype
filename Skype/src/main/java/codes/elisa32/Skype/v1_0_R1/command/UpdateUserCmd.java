package codes.elisa32.Skype.v1_0_R1.command;

import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInUpdateUser;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLookupOnlineStatus;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.data.types.Contact;
import codes.elisa32.Skype.v1_0_R1.data.types.Conversation;
import codes.elisa32.Skype.v1_0_R1.data.types.Status;
import codes.elisa32.Skype.v1_0_R1.forms.MainForm;
import codes.elisa32.Skype.v1_0_R1.plugin.Skype;

public class UpdateUserCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInUpdateUser packet = Packet.fromJson(msg.toString(),
				PacketPlayInUpdateUser.class);
		UUID conversationId = packet.getConversationId();
		String json = packet.getPayload().toString();
		for (Conversation conversation : MainForm.get().getConversations()
				.toArray(new Conversation[0]).clone()) {
			if (conversation.getUniqueId().equals(conversationId)) {
				if (conversation instanceof Contact) {
					Contact contact = (Contact) conversation;
					contact.readFromJson(json);
					Optional<SocketHandlerContext> ctx2 = Skype.getPlugin()
							.createHandle();
					UUID authCode = UUID.fromString(ctx2
							.get()
							.getOutboundHandler()
							.dispatch(
									ctx2.get(),
									new PacketPlayOutLogin(MainForm.get()
											.getAuthCode())).get().getText());
					Status onlineStatus = Status.OFFLINE;
					{
						PacketPlayOutLookupOnlineStatus onlineStatusLookup = new PacketPlayOutLookupOnlineStatus(
								authCode, conversation.getUniqueId());
						Optional<PacketPlayInReply> replyPacket = ctx2.get()
								.getOutboundHandler()
								.dispatch(ctx2.get(), onlineStatusLookup);
						onlineStatus = Status.valueOf(replyPacket.get()
								.getText());
					}
					contact.setOnlineStatus(onlineStatus);
				} else {
					conversation.readFromJson(json);
				}
			}
		}
		if (MainForm.get().rightPanelPage.equals("Conversation")) {
			MainForm.get().refreshWindow(MainForm.get().SCROLL_TO_BOTTOM);
		} else {
			MainForm.get().refreshWindow();
		}
		return PacketPlayInReply.empty();
	}
}
