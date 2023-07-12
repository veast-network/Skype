package codes.elisa32.Skype.api.v1_0_R1.command;

import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;

public abstract class CommandExecutor {

	public abstract PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg);

}
