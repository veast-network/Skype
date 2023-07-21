package codes.elisa32.Skype.server.v1_0_R1.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
	private Connection con2;
	private Socket socket;

	public FileTransferringInboundHandler(FileTransfer fileTransfer,
			UUID participantId, SocketHandlerContext ctx, Connection con) {
		this.fileTransfer = fileTransfer;
		this.loggedInUser = participantId;
		this.ctx = ctx;
		this.con2 = con;
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
			ServerSocket server2 = null;
			boolean err = false;
			int port = 0;
			do {
				try {
					Random random = new Random();
					port = random.nextInt(5000) + 40000;
					server2 = new ServerSocket(port);
					err = false;
				} catch (Exception e) {
					err = true;
					e.printStackTrace();
				}
			} while (err == true);
			final ServerSocket server = server2;
			InputStream dis = socket.getInputStream();
			OutputStream dos = socket.getOutputStream();
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					Socket socket = null;
					try {
						socket = server.accept();
						dos.close();
						InputStream is = socket.getInputStream();
						int bytesRead;
						byte[] b = new byte[8192];
						while ((bytesRead = is.read(b)) != -1) {
							run2(Arrays.copyOf(b, bytesRead));
						}
						is.close();
						socket.close();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							socket.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						try {
							server.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						List<String> skypeNames = new ArrayList<>();
						for (UUID fileTransferParticipant : fileTransfer
								.getParticipants().toArray(new UUID[0]).clone()) {
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
								UUID receivingFileTransferId = con
										.getFileTransferId().get();
								UUID receivingFileDataStreamParticipantId = con
										.getReceivingFileDataStreamParticipantId()
										.get();
								if (receivingFileTransferId.equals(fileTransfer
										.getFileTransferId())) {
									if (!con.isFileDataStreamEnded()) {
										if (!skypeNames.contains(con
												.getSkypeName())) {
											if (!con.getSkypeName().equals(
													con2.getSkypeName())) {
												skypeNames.add(con
														.getSkypeName());
											}
										}
									}
									if (receivingFileDataStreamParticipantId
											.equals(loggedInUser)) {
										con.setFileDataStreamEnded(true);
									}
								}
							}
						}
						List<String> participantIds = new ArrayList<>();
						for (String skypeName : skypeNames) {
							participantIds.add(Skype.getPlugin()
									.getUserManager().getUniqueId(skypeName)
									.toString());
						}
						Object payload = GsonBuilder.create().toJson(
								participantIds);
						for (UUID fileTransferParticipant : fileTransfer
								.getParticipants()) {
							boolean hasParticipantAnsweredFileTransfer = Skype
									.getPlugin()
									.getUserManager()
									.getConnectionsInFileTransfer(
											fileTransferParticipant,
											fileTransfer.getFileTransferId())
									.size() > 0
									|| Skype.getPlugin()
											.getUserManager()
											.getDataStreamConnectionsInFileTransfer(
													fileTransferParticipant,
													fileTransfer
															.getFileTransferId())
											.size() > 0;
							if (!hasParticipantAnsweredFileTransfer) {
								continue;
							}
							PacketPlayInFileTransferParticipantsChanged fileTransferParticipantsChangedPacket = new PacketPlayInFileTransferParticipantsChanged(
									fileTransfer.getFileTransferId(), payload);
							for (Connection listeningParticipant : Skype
									.getPlugin()
									.getUserManager()
									.getListeningConnections(
											fileTransferParticipant)) {
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
							for (UUID fileTransferParticipant : fileTransfer
									.getParticipants().toArray(new UUID[0])
									.clone()) {
								for (Connection con : Skype
										.getPlugin()
										.getUserManager()
										.getDataStreamConnectionsInFileTransfer(
												fileTransferParticipant,
												fileTransfer
														.getFileTransferId())
										.toArray(new Connection[0]).clone()) {
									if (!con.getFileTransferId().isPresent()) {
										continue;
									}
									if (!con.getReceivingFileDataStreamParticipantId()
											.isPresent()) {
										continue;
									}
									UUID receivingFileTransferId = con
											.getFileTransferId().get();
									if (receivingFileTransferId
											.equals(fileTransfer
													.getFileTransferId())) {
										try {
											con.getSocketHandlerContext()
													.getSocket().close();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
							for (UUID fileTransferParticipant : fileTransfer
									.getParticipants().toArray(new UUID[0])
									.clone()) {
								for (Connection con : Skype
										.getPlugin()
										.getUserManager()
										.getConnectionsInFileTransfer(
												fileTransferParticipant,
												fileTransfer
														.getFileTransferId())
										.toArray(new Connection[0]).clone()) {
									if (!con.getFileTransferId().isPresent()) {
										continue;
									}
									if (!con.getReceivingFileDataStreamParticipantId()
											.isPresent()) {
										continue;
									}
									UUID receivingFileTransferId = con
											.getFileTransferId().get();
									if (receivingFileTransferId
											.equals(fileTransfer
													.getFileTransferId())) {
										try {
											con.getSocketHandlerContext()
													.getSocket().close();
										} catch (Exception e) {
											e.printStackTrace();
										}
									}
								}
							}
						}
						fileTransfer.removeParticipant(loggedInUser);
						Skype.getPlugin().getConnectionMap()
								.remove(con2.getAuthCode(), con2);
					}
				}

			});
			thread.start();
			while (true) {
				dis.read(new byte[1024]);
				dos.write((port + "").getBytes());
				dos.flush();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return;
		}
	}

}
