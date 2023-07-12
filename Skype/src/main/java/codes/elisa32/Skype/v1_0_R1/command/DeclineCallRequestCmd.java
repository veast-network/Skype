package codes.elisa32.Skype.v1_0_R1.command;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.v1_0_R1.audioio.AudioIO;

public class DeclineCallRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		// TODO Display on screen that this person has left the phone call
		AudioIO.USER_LEFT.playSound();
		return PacketPlayInReply.empty();
	}

}
