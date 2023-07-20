package codes.elisa32.Skype.server.v1_0_R1.command;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.data.types.FileTransfer;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInDeclineFileTransferRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutDeclineFileTransferRequest;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class DeclineFileTransferRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutDeclineFileTransferRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayOutDeclineFileTransferRequest.class);
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
		PacketPlayInDeclineFileTransferRequest declineCallRequestPacket = new PacketPlayInDeclineFileTransferRequest(
				participantId, fileTransferId);
		int hits = 0;
		for (UUID participant : fileTransfer.getParticipants()) {
			boolean hasParticipantAnsweredFileTransfer = Skype.getPlugin()
					.getUserManager()
					.getConnectionsInFileTransfer(participant, fileTransferId)
					.size() > 0;
			if (hasParticipantAnsweredFileTransfer) {
				for (Connection listeningParticipant : Skype.getPlugin()
						.getUserManager().getListeningConnections(participant)) {
					try {
						listeningParticipant
								.getSocketHandlerContext()
								.getOutboundHandler()
								.dispatchAsync(
										listeningParticipant
												.getSocketHandlerContext(),
										declineCallRequestPacket, null);
						hits++;
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}

		fileTransfer.removeParticipant(participantId);

		if (hits == 0) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}

		PacketPlayInReply replyPacket = new PacketPlayInReply(
				PacketPlayInReply.OK, packet.getType().name() + " success");
		return replyPacket;
	}

}
