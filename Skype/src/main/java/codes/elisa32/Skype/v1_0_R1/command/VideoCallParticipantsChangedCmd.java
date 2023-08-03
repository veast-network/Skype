package codes.elisa32.Skype.v1_0_R1.command;

import java.util.ArrayList;
import java.util.List;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInVideoCallParticipantsChanged;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.forms.MainForm;

import com.google.gson.Gson;

public class VideoCallParticipantsChangedCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInVideoCallParticipantsChanged packet = Packet.fromJson(
				msg.toString(), PacketPlayInVideoCallParticipantsChanged.class);
		String json = packet.getPayload().toString();
		Gson gson = GsonBuilder.create();
		List<String> participants = gson.fromJson(json, List.class);
		List<UUID> participantIds = new ArrayList<>();
		for (String participant : participants) {
			participantIds.add(UUID.fromString(participant));
		}
		UUID callId = packet.getCallId();
		if (MainForm.get().ongoingVideoCallId == null) {
			return PacketPlayInReply.empty();
		}
		if (!MainForm.get().ongoingVideoCallId.equals(callId)) {
			return PacketPlayInReply.empty();
		}
		MainForm.get().ongoingVideoCallParticipants.clear();
		MainForm.get().ongoingVideoCallParticipants.addAll(participantIds);
		if (MainForm.get().rightPanelPage.equals("Covnersation")) {
			if (MainForm
					.get()
					.getSelectedConversation()
					.getUniqueId()
					.equals(MainForm.get().ongoingCallConversation
							.getUniqueId())) {
				MainForm.get().refreshWindow(MainForm.get().SCROLL_TO_BOTTOM);
				return PacketPlayInReply.empty();
			}
		}
		MainForm.get().refreshWindow();
		return PacketPlayInReply.empty();
	}
}
