package codes.wilma24.Skype.v1_0_R1.command;

import java.util.List;
import java.util.Optional;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInUserRegistryChanged;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLookupUserRegistry;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;
import codes.wilma24.Skype.v1_0_R1.plugin.Skype;

import com.google.gson.Gson;

public class UserRegistryChangedCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInUserRegistryChanged packet = Packet.fromJson(
				msg.toString(), PacketPlayInUserRegistryChanged.class);
		Optional<SocketHandlerContext> ctx2 = Skype.getPlugin().createHandle();
		if (!ctx2.isPresent()) {
			return PacketPlayInReply.empty();
		}
		UUID authCode = UUID.fromString(ctx2
				.get()
				.getOutboundHandler()
				.dispatch(ctx2.get(),
						new PacketPlayOutLogin(MainForm.get().getAuthCode()))
				.get().getText());
		PacketPlayOutLookupUserRegistry packet2 = new PacketPlayOutLookupUserRegistry(
				authCode);
		Optional<PacketPlayInReply> replyPacket = ctx2.get()
				.getOutboundHandler().dispatch(ctx2.get(), packet2);
		if (!replyPacket.isPresent()) {
			return PacketPlayInReply.empty();
		}
		if (replyPacket.get().getStatusCode() != 200) {
			return PacketPlayInReply.empty();
		}
		String json = replyPacket.get().getText();
		Gson gson = GsonBuilder.create();
		MainForm.get().registry.clear();
		MainForm.get().registry.addAll(gson.fromJson(json, List.class));
		return PacketPlayInReply.empty();
	}
}
