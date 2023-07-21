package codes.elisa32.Skype.server.v1_0_R1.command;

import java.util.ArrayList;
import java.util.List;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.data.types.FileTransfer;
import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInFileTransferParticipantsChanged;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptFileDataStreamRequest;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class AcceptFileDataStreamRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayOutAcceptFileDataStreamRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayOutAcceptFileDataStreamRequest.class);
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
		UUID participantId = packet.getParticipantId();
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
		if (!fileTransfer.isParticipant(con.getUniqueId())) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		if (con.isListening()) {
			PacketPlayInReply replyPacket = new PacketPlayInReply(
					PacketPlayInReply.BAD_REQUEST, packet.getType().name()
							+ " failed");
			return replyPacket;
		}
		ctx.fireInboundHandlerInactive();
		con.setFileDataStream(fileTransferId, participantId);
		List<String> skypeNames = new ArrayList<>();
		for (UUID fileTransferParticipant : fileTransfer.getParticipants()
				.toArray(new UUID[0]).clone()) {
			for (Connection con2 : Skype
					.getPlugin()
					.getUserManager()
					.getDataStreamConnectionsInFileTransfer(
							fileTransferParticipant,
							fileTransfer.getFileTransferId())
					.toArray(new Connection[0]).clone()) {
				if (!con2.getFileTransferId().isPresent()) {
					continue;
				}
				if (!con2.getReceivingFileDataStreamParticipantId().isPresent()) {
					continue;
				}
				UUID receivingFileTransferId = con2.getFileTransferId().get();
				if (receivingFileTransferId.equals(fileTransfer
						.getFileTransferId())) {
					if (!con2.isFileDataStreamEnded()) {
						if (!skypeNames.contains(con2.getSkypeName())) {
							skypeNames.add(con2.getSkypeName());
						}
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
		if (participantIds.size() == 1) {
			return PacketPlayInReply.empty();
		}
		for (UUID fileTransferParticipant : fileTransfer.getParticipants()) {
			boolean hasParticipantAnsweredFileTransfer = Skype
					.getPlugin()
					.getUserManager()
					.getConnectionsInFileTransfer(fileTransferParticipant,
							fileTransferId).size() > 0;
			if (!hasParticipantAnsweredFileTransfer) {
				continue;
			}
			PacketPlayInFileTransferParticipantsChanged fileTransferParticipantsChangedPacket = new PacketPlayInFileTransferParticipantsChanged(
					fileTransferId, payload);
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
											fileTransferParticipantsChangedPacket);
						});
				thread.start();
			}
		}
		return PacketPlayInReply.empty();
	}

}
