package codes.wilma24.Skype.server.v1_0_R1.command;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.data.types.FileTransfer;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInAcceptFileTransferRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInFileDataStreamRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptFileTransferRequest;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.server.v1_0_R1.Skype;
import codes.wilma24.Skype.server.v1_0_R1.data.types.Connection;
import codes.wilma24.Skype.server.v1_0_R1.socket.FileTransferringInboundHandler;

public class AcceptFileTransferRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutAcceptFileTransferRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayOutAcceptFileTransferRequest.class);
		UUID authCode = packet.getAuthCode();
		Connection con = Skype.getPlugin().getUserManager()
				.getConnection(authCode);
		if (con == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		UUID fileTransferId = packet.getFileTransferId();
		UUID participantId = con.getUniqueId();
		FileTransfer fileTransfer = Skype.getPlugin().getFileTransferMap()
				.getOrDefault(fileTransferId, null);
		if (fileTransfer == null) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		if (!fileTransfer.isParticipant(participantId)) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		PacketPlayInAcceptFileTransferRequest acceptFileTransferRequestPacket = new PacketPlayInAcceptFileTransferRequest(
				participantId, fileTransferId);
		{
			PacketPlayInFileDataStreamRequest fileDataStreamRequestPacket = new PacketPlayInFileDataStreamRequest(
					participantId, fileTransferId);

			for (UUID fileTransferParticipant : fileTransfer.getParticipants()) {
				boolean hasParticipantAnsweredFileTransfer = Skype
						.getPlugin()
						.getUserManager()
						.getConnectionsInFileTransfer(fileTransferParticipant,
								fileTransferId).size() > 0;
				if (!hasParticipantAnsweredFileTransfer) {
					continue;
				}
				for (Connection listeningParticipant : Skype.getPlugin()
						.getUserManager()
						.getListeningConnections(fileTransferParticipant)) {
					Thread thread = new Thread(
							() -> {
								listeningParticipant
										.getSocketHandlerContext()
										.getOutboundHandler()
										.write(listeningParticipant
												.getSocketHandlerContext(),
												acceptFileTransferRequestPacket);
								listeningParticipant
										.getSocketHandlerContext()
										.getOutboundHandler()
										.write(listeningParticipant
												.getSocketHandlerContext(),
												fileDataStreamRequestPacket);
							});
					thread.start();
				}
			}
		}

		{
			for (UUID fileTransferParticipant : fileTransfer.getParticipants()) {
				boolean hasParticipantAnsweredFileTransfer = Skype
						.getPlugin()
						.getUserManager()
						.getConnectionsInFileTransfer(fileTransferParticipant,
								fileTransferId).size() > 0;
				if (!hasParticipantAnsweredFileTransfer) {
					continue;
				}
				PacketPlayInFileDataStreamRequest fileDataStreamRequestPacket = new PacketPlayInFileDataStreamRequest(
						fileTransferParticipant, fileTransferId);
				for (Connection listeningParticipant : Skype.getPlugin()
						.getUserManager()
						.getListeningConnections(participantId)) {
					Thread thread = new Thread(
							() -> {
								listeningParticipant
										.getSocketHandlerContext()
										.getOutboundHandler()
										.write(listeningParticipant
												.getSocketHandlerContext(),
												acceptFileTransferRequestPacket);
								listeningParticipant
										.getSocketHandlerContext()
										.getOutboundHandler()
										.write(listeningParticipant
												.getSocketHandlerContext(),
												fileDataStreamRequestPacket);
							});
					thread.start();
				}
			}
		}

		con.setFileTransferId(fileTransferId);
		ctx.fireInboundHandlerInactive();

		FileTransferringInboundHandler inboundHandler = new FileTransferringInboundHandler(
				fileTransfer, participantId, ctx, con);
		Thread thread = new Thread(inboundHandler);
		thread.start();

		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}

}
