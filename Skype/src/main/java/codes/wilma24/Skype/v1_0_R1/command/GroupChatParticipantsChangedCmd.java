package codes.wilma24.Skype.v1_0_R1.command;

import java.util.ArrayList;
import java.util.List;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInGroupChatParticipantsChanged;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.data.types.Conversation;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;
import codes.wilma24.Skype.v1_0_R1.plugin.Skype;

import com.google.gson.Gson;

public class GroupChatParticipantsChangedCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInGroupChatParticipantsChanged packet = Packet.fromJson(
				msg.toString(), PacketPlayInGroupChatParticipantsChanged.class);
		UUID conversationId = packet.getConversationId();
		String json = packet.getPayload().toString();
		Gson gson = GsonBuilder.create();
		List<String> skypeNames = gson.fromJson(json, List.class);
		for (Conversation conversation : MainForm.get().getConversations()
				.toArray(new Conversation[0]).clone()) {
			if (conversation.getUniqueId().equals(conversationId)) {
				if (skypeNames.contains(MainForm.get().getLoggedInUser()
						.getSkypeName())) {
					List<UUID> participantIds = new ArrayList<>();
					for (String skypeName : skypeNames) {
						participantIds.add(Skype.getPlugin().getUniqueId(
								skypeName));
					}
					conversation.setParticipants(participantIds);
					if (MainForm.get().rightPanelPage.equals("Conversation")) {
						if (MainForm.get().getSelectedConversation()
								.getUniqueId()
								.equals(conversation.getUniqueId())) {
							MainForm.get().refreshWindow(
									MainForm.get().SCROLL_TO_BOTTOM);
							return PacketPlayInReply.empty();
						}
					}
					MainForm.get().refreshWindow();
				} else {
					MainForm.get().getConversations().remove(conversation);
					if (MainForm.get().rightPanelPage.equals("Conversation")) {
						if (MainForm.get().getSelectedConversation()
								.getUniqueId()
								.equals(conversation.getUniqueId())) {
							MainForm.get().setSelectedConversation(null);
							MainForm.get().rightPanelPage = "AccountHome";
						}
					}
					MainForm.get().refreshWindow();
				}
			}
		}
		return PacketPlayInReply.empty();
	}
}
