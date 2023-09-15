package codes.wilma24.Skype.v1_0_R1.command;

import java.awt.Window;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import org.bouncycastle.util.Arrays;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInFileTransferParticipantsChanged;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;
import codes.wilma24.Skype.v1_0_R1.plugin.Skype;

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
			Socket socket2 = MainForm.get().fileTransferOutgoingAudioSockets
					.get(0);
			socket2.setSoTimeout(2000);
			InputStream dis = socket2.getInputStream();
			OutputStream dos = socket2.getOutputStream();
			boolean err = false;
			int port = 0;
			do {
				try {
					dos.write("200 OK".getBytes());
					dos.flush();
					byte[] b2 = new byte[1024];
					int bytesRead2 = dis.read(b2);
					port = Integer.parseInt(new String(Arrays.copyOf(b2,
							bytesRead2)));
					err = false;
				} catch (Exception e) {
					e.printStackTrace();
					err = true;
				}
			} while (err == true);
			String hostname = Skype.getPlugin().getHostname();
			Socket socket = new Socket(hostname, port);
			JFrame mainForm = MainForm.get();
			String fileName = MainForm.get().ongoingFileTransferFileName;
			int length = (int) MainForm.get().ongoingFileTransferLength;
			final Window parent = mainForm;
			ProgressMonitorInputStream progressMonitorInputStream = new ProgressMonitorInputStream(
					parent, "Storing " + fileName, bais);
			try (OutputStream outputStream = socket.getOutputStream()) {
				parent.setEnabled(false);
				ProgressMonitor progressMonitor = progressMonitorInputStream
						.getProgressMonitor();
				progressMonitor.setMinimum(0);
				progressMonitor.setMaximum((int) length);
				progressMonitor.setMillisToDecideToPopup(100);
				progressMonitor.setMillisToPopup(100);
				int bytesRead;
				byte[] b = new byte[8192];
				while ((bytesRead = progressMonitorInputStream.read(b)) != -1) {
					progressMonitor.setNote(bais.available() / 1000
							+ " more kb to write");
					outputStream.write(b, 0, bytesRead);
					outputStream.flush();
				}
				progressMonitor.setNote("0 more kb to write");
				progressMonitorInputStream.close();
				outputStream.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			} finally {
				parent.setEnabled(true);
				if (parent instanceof JFrame) {
					java.awt.EventQueue.invokeLater(new Runnable() {
						@Override
						public void run() {
							((JFrame) parent).toFront();
							parent.repaint();
						}
					});
				}
			}
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
				Thread thread = new Thread(() -> {
					run2();
				});
				thread.start();
			}
		} else if (participantIds.size() == 2) {
			Thread thread = new Thread(() -> {
				run2();
			});
			thread.start();
		}
		return PacketPlayInReply.empty();
	}
}
