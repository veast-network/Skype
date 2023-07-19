package codes.elisa32.Skype.v1_0_R1.command;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInFileTransferParticipantsChanged;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.forms.MainForm;

import com.google.gson.Gson;

public class FileTransferParticipantsChangedCmd extends CommandExecutor {

	public void run2() {
		if (MainForm.get().ongoingFileTransferData == null) {
			return;
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(
				MainForm.get().ongoingFileTransferData);
		if (MainForm.get().fileTransferOutgoingAudioSockets.size() > 1) {
			return;
		}
		try {
			Socket socket = MainForm.get().fileTransferOutgoingAudioSockets
					.get(0);
			int bytesRead;
			byte[] b = new byte[1024];
			socket.setSoTimeout(2000);
			BufferedOutputStream dos = new BufferedOutputStream(
					socket.getOutputStream());
			while ((bytesRead = bais.read(b)) != -1) {
				dos.write(b, 0, bytesRead);
				dos.flush();
				try {
					socket.getInputStream().read(new byte[1024]);
				} catch (Exception e) {
					e.printStackTrace();
					dos.write(b, 0, bytesRead);
				}
			}
			socket.getOutputStream().close();
			socket.close();
			MainForm.get().fileTransferDataTransferFinished = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInFileTransferParticipantsChanged packet = Packet.fromJson(
				msg.toString(),
				PacketPlayInFileTransferParticipantsChanged.class);
		String json = packet.getPayload().toString();
		Gson gson = GsonBuilder.create();
		List<String> participants = gson.fromJson(json, List.class);
		List<UUID> participantIds = new ArrayList<>();
		for (String participant : participants) {
			participantIds.add(UUID.fromString(participant));
		}
		UUID fileTransferId = packet.getFileTransferId();
		if (MainForm.get().ongoingFileTransferId == null) {
			return PacketPlayInReply.empty();
		}
		if (!MainForm.get().ongoingFileTransferId.equals(fileTransferId)) {
			return PacketPlayInReply.empty();
		}
		MainForm.get().ongoingFileTransferParticipants.clear();
		MainForm.get().ongoingFileTransferParticipants.addAll(participantIds);
		if (participantIds.size() == 1) {
			if (fileTransferId.equals(MainForm.get().ongoingFileTransferId)) {
				if (MainForm.get().fileTransferDataTransferFinished) {
					MainForm.get().ongoingFileTransfer = false;
					MainForm.get().ongoingFileTransferConversation = null;
					MainForm.get().ongoingFileTransferParticipants.clear();
					MainForm.get().ongoingFileTransferId = null;
					MainForm.get().ongoingFileTransferCipher = null;
					MainForm.get().ongoingFileTransferData = null;
					try {
						for (Socket socket2 : MainForm.get().fileTransferIncomingAudioSockets) {
							socket2.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					MainForm.get().fileTransferIncomingAudioSockets.clear();
					try {
						for (Socket socket2 : MainForm.get().fileTransferOutgoingAudioSockets) {
							socket2.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					MainForm.get().fileTransferOutgoingAudioSockets.clear();
				}
			}
		} else if (MainForm.get().ongoingFileTransferConversation.isGroupChat()) {
			if (MainForm.get().ongoingFileTransferConversation
					.getParticipants().size() == participantIds.size()) {
				run2();
			}
		} else if (participantIds.size() == 2) {
			run2();
		}
		return PacketPlayInReply.empty();
	}
}
