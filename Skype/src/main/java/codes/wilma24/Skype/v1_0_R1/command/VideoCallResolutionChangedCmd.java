package codes.wilma24.Skype.v1_0_R1.command;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInVideoCallResolutionChanged;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;

public class VideoCallResolutionChangedCmd extends CommandExecutor {

	public void run3(PacketPlayInVideoCallResolutionChanged packet, int hits) {
		if (hits > 10) {
			return;
		}
		Timer timer = new Timer(200, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				((Timer) arg0.getSource()).stop();
				run2(packet, hits + 1);
			}
		});
		timer.restart();
	}

	public void run2(PacketPlayInVideoCallResolutionChanged packet, int hits) {
		if (MainForm.get().ongoingCall != true) {
			run3(packet, hits);
			return;
		}
		if (MainForm.get().ongoingVideoCall != true) {
			run3(packet, hits);
			return;
		}
		if (!MainForm.get().ongoingVideoCallId.equals(packet.getCallId())) {
			run3(packet, hits);
			return;
		}
		if (packet.getParticipantId().equals(
				MainForm.get().getLoggedInUser().getUniqueId())) {
			run3(packet, hits);
			return;
		}
		MainForm.ongoingVideoCallWidth = packet.getWidth();
		MainForm.ongoingVideoCallHeight = packet.getHeight();
		MainForm.get().refreshWindow();
	}

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInVideoCallResolutionChanged packet = Packet.fromJson(
				msg.toString(), PacketPlayInVideoCallResolutionChanged.class);
		run2(packet, 0);
		return PacketPlayInReply.empty();
	}
}
