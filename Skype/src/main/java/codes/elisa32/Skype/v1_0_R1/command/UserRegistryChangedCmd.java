package codes.elisa32.Skype.v1_0_R1.command;

import java.util.List;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInUserRegistryChanged;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.v1_0_R1.forms.MainForm;

import com.google.gson.Gson;

public class UserRegistryChangedCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInUserRegistryChanged packet = Packet.fromJson(
				msg.toString(), PacketPlayInUserRegistryChanged.class);
		String json = packet.getPayload().toString();
		Gson gson = GsonBuilder.create();
		MainForm.get().registry.clear();
		MainForm.get().registry.addAll(gson.fromJson(json, List.class));
		return PacketPlayInReply.empty();
	}
}
