package codes.elisa32.Skype.server.v1_0_R1.command;

import java.util.List;
import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.data.types.FileTransfer;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInFileTransferRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutSendFileTransferRequest;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class SendFileTransferRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutSendFileTransferRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayOutSendFileTransferRequest.class);
		UUID authCode = packet.getAuthCode();
		UUID conversationId = packet.getConversationId();
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
		String fileName = packet.getFileName();
		long length = packet.getLength();
		String cipher = packet.getCipher();
		FileTransfer fileTransfer = new FileTransfer(fileTransferId);
		int hits = 0;
		if (Skype.getPlugin().getUserManager().isGroupChat(conversationId)) {
			Optional<List<UUID>> participants = Skype.getPlugin()
					.getConversationManager().getParticipants(conversationId);
			PacketPlayInFileTransferRequest fileTransferRequestPacket = new PacketPlayInFileTransferRequest(
					conversationId, participantId, fileTransferId, fileName,
					length, cipher);
			if (participants.isPresent()) {
				for (UUID participant : participants.get()) {
					if (participant.equals(participantId)) {
						continue;
					}
					fileTransfer.addParticipant(participant);
					for (Connection listeningParticipant : Skype.getPlugin()
							.getUserManager()
							.getListeningConnections(participant)) {
						try {
							listeningParticipant
									.getSocketHandlerContext()
									.getOutboundHandler()
									.dispatchAsync(
											listeningParticipant
													.getSocketHandlerContext(),
											fileTransferRequestPacket, null);
							hits++;
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						}
					}
				}
			}
			for (Connection listeningParticipant : Skype.getPlugin()
					.getUserManager().getListeningConnections(participantId)) {
				try {
					listeningParticipant
							.getSocketHandlerContext()
							.getOutboundHandler()
							.dispatchAsync(
									listeningParticipant
											.getSocketHandlerContext(),
									fileTransferRequestPacket, null);
					hits++;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		} else {
			PacketPlayInFileTransferRequest fileTransferRequestPacket = new PacketPlayInFileTransferRequest(
					participantId, participantId, fileTransferId, fileName,
					length, cipher);
			fileTransfer.addParticipant(conversationId);
			for (Connection listeningParticipant : Skype.getPlugin()
					.getUserManager().getListeningConnections(conversationId)) {
				try {
					listeningParticipant
							.getSocketHandlerContext()
							.getOutboundHandler()
							.dispatchAsync(
									listeningParticipant
											.getSocketHandlerContext(),
									fileTransferRequestPacket, null);
					hits++;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
			for (Connection listeningParticipant : Skype.getPlugin()
					.getUserManager().getListeningConnections(participantId)) {
				try {
					listeningParticipant
							.getSocketHandlerContext()
							.getOutboundHandler()
							.dispatchAsync(
									listeningParticipant
											.getSocketHandlerContext(),
									fileTransferRequestPacket, null);
					hits++;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		fileTransfer.setLength(length);
		fileTransfer.addParticipant(participantId);

		if (hits == 0) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}

		Skype.getPlugin().getFileTransferMap()
				.put(fileTransferId, fileTransfer);

		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}
}
