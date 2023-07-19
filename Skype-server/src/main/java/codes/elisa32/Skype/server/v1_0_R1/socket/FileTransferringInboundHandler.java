package codes.elisa32.Skype.server.v1_0_R1.socket;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import codes.elisa32.Skype.api.v1_0_R1.data.types.FileTransfer;
import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInFileTransferParticipantsChanged;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class FileTransferringInboundHandler implements Runnable {

	private UUID loggedInUser;
	private FileTransfer fileTransfer;
	private SocketHandlerContext ctx;
	private Connection con;
	private Socket socket;

	public FileTransferringInboundHandler(FileTransfer fileTransfer,
			UUID participantId, SocketHandlerContext ctx, Connection con) {
		this.fileTransfer = fileTransfer;
		this.loggedInUser = participantId;
		this.ctx = ctx;
		this.con = con;
		this.socket = ctx.getSocket();
	}

	public void run2(byte[] b) {
		for (UUID fileTransferParticipant : fileTransfer.getParticipants()
				.toArray(new UUID[0]).clone()) {
			if (fileTransferParticipant.equals(loggedInUser)) {
				continue;
			}
			for (Connection con : Skype
					.getPlugin()
					.getUserManager()
					.getDataStreamConnectionsInFileTransfer(
							fileTransferParticipant,
							fileTransfer.getFileTransferId())
					.toArray(new Connection[0]).clone()) {
				if (!con.getFileTransferId().isPresent()) {
					continue;
				}
				if (!con.getReceivingFileDataStreamParticipantId().isPresent()) {
					continue;
				}
				UUID receivingFileTransferId = con.getFileTransferId().get();
				UUID receivingFileDataStreamParticipantId = con
						.getReceivingFileDataStreamParticipantId().get();
				if (receivingFileTransferId.equals(fileTransfer
						.getFileTransferId())) {
					if (receivingFileDataStreamParticipantId
							.equals(this.loggedInUser)) {
						try {
							con.getSocketHandlerContext().getSocket()
									.getOutputStream().write(b);
							con.getSocketHandlerContext().getSocket()
									.getOutputStream().flush();
						} catch (Exception e) {
							e.printStackTrace();
							fileTransfer
									.removeParticipant(fileTransferParticipant);
							con.setFileDataStream(null, null);
						}
					}
				}
			}
		}
	}

	@Override
	public void run() {
		try {
			socket.setSoTimeout(0);
			byte[] b = new byte[1024];
			int bytesRead;
			int length = 0;
			BufferedInputStream dis = new BufferedInputStream(
					socket.getInputStream());
			while ((bytesRead = dis.read(b)) != -1) {
				length += bytesRead;
				run2(Arrays.copyOf(b, bytesRead));
				socket.getOutputStream().write("200 OK".getBytes());
				socket.getOutputStream().flush();
				if (length == fileTransfer.getLength()) {
					break;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> skypeNames = new ArrayList<>();
		for (UUID fileTransferParticipant : fileTransfer.getParticipants()
				.toArray(new UUID[0]).clone()) {
			for (Connection con : Skype
					.getPlugin()
					.getUserManager()
					.getDataStreamConnectionsInFileTransfer(
							fileTransferParticipant,
							fileTransfer.getFileTransferId())
					.toArray(new Connection[0]).clone()) {
				if (!con.getFileTransferId().isPresent()) {
					continue;
				}
				if (!con.getReceivingFileDataStreamParticipantId().isPresent()) {
					continue;
				}
				UUID receivingFileTransferId = con.getFileTransferId().get();
				UUID receivingFileDataStreamParticipantId = con
						.getReceivingFileDataStreamParticipantId().get();
				if (receivingFileTransferId.equals(fileTransfer
						.getFileTransferId())) {
					if (!con.isFileDataStreamEnded()) {
						if (!skypeNames.contains(con.getSkypeName())) {
							if (!con.getSkypeName().equals(
									this.con.getSkypeName())) {
								skypeNames.add(con.getSkypeName());
							}
						}
					}
					if (receivingFileDataStreamParticipantId
							.equals(this.loggedInUser)) {
						con.setFileDataStreamEnded(true);
					}
				}
			}
		}
		List<String> participantIds = new ArrayList<>();
		for (String skypeName : skypeNames) {
			participantIds.add(Skype.getPlugin().getUserManager()
					.getUniqueId(skypeName).toString());
		}
		Object payload = GsonBuilder.create().toJson(participantIds);
		for (UUID fileTransferParticipant : fileTransfer.getParticipants()) {
			boolean hasParticipantAnsweredFileTransfer = Skype
					.getPlugin()
					.getUserManager()
					.getConnectionsInFileTransfer(fileTransferParticipant,
							fileTransfer.getFileTransferId()).size() > 0
					|| Skype.getPlugin()
							.getUserManager()
							.getDataStreamConnectionsInFileTransfer(
									fileTransferParticipant,
									fileTransfer.getFileTransferId()).size() > 0;
			if (!hasParticipantAnsweredFileTransfer) {
				continue;
			}
			PacketPlayInFileTransferParticipantsChanged fileTransferParticipantsChangedPacket = new PacketPlayInFileTransferParticipantsChanged(
					fileTransfer.getFileTransferId(), payload);
			for (Connection listeningParticipant : Skype.getPlugin()
					.getUserManager()
					.getListeningConnections(fileTransferParticipant)) {
				Thread thread = new Thread(
						() -> {
							listeningParticipant
									.getSocketHandlerContext()
									.getOutboundHandler()
									.dispatch(
											listeningParticipant
													.getSocketHandlerContext(),
											fileTransferParticipantsChangedPacket);
						});
				thread.start();
			}
		}
		if (skypeNames.size() < 2) {
			for (UUID fileTransferParticipant : fileTransfer.getParticipants()
					.toArray(new UUID[0]).clone()) {
				for (Connection con : Skype
						.getPlugin()
						.getUserManager()
						.getDataStreamConnectionsInFileTransfer(
								fileTransferParticipant,
								fileTransfer.getFileTransferId())
						.toArray(new Connection[0]).clone()) {
					if (!con.getFileTransferId().isPresent()) {
						continue;
					}
					if (!con.getReceivingFileDataStreamParticipantId()
							.isPresent()) {
						continue;
					}
					UUID receivingFileTransferId = con.getFileTransferId()
							.get();
					if (receivingFileTransferId.equals(fileTransfer
							.getFileTransferId())) {
						try {
							con.getSocketHandlerContext().getSocket().close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
			for (UUID fileTransferParticipant : fileTransfer.getParticipants()
					.toArray(new UUID[0]).clone()) {
				for (Connection con : Skype
						.getPlugin()
						.getUserManager()
						.getConnectionsInFileTransfer(fileTransferParticipant,
								fileTransfer.getFileTransferId())
						.toArray(new Connection[0]).clone()) {
					if (!con.getFileTransferId().isPresent()) {
						continue;
					}
					if (!con.getReceivingFileDataStreamParticipantId()
							.isPresent()) {
						continue;
					}
					UUID receivingFileTransferId = con.getFileTransferId()
							.get();
					if (receivingFileTransferId.equals(fileTransfer
							.getFileTransferId())) {
						try {
							con.getSocketHandlerContext().getSocket().close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		fileTransfer.removeParticipant(loggedInUser);
		Skype.getPlugin().getConnectionMap().remove(con.getAuthCode(), con);
	}

}
