package codes.elisa32.Skype.v1_0_R1.command;

import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInFileTransferRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptFileTransferRequest;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.cipher.CipherUtilities;
import codes.elisa32.Skype.v1_0_R1.data.types.Conversation;
import codes.elisa32.Skype.v1_0_R1.forms.MainForm;
import codes.elisa32.Skype.v1_0_R1.pgp.PGPUtilities;
import codes.elisa32.Skype.v1_0_R1.pgp.PGPUtilities.DecryptionResult;
import codes.elisa32.Skype.v1_0_R1.plugin.Skype;

public class FileTransferRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInFileTransferRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayInFileTransferRequest.class);
		UUID fileTransferId = packet.getFileTransferId();
		UUID authCode = MainForm.get().getAuthCode();
		UUID loggedInUser = MainForm.get().getLoggedInUser().getUniqueId();

		Optional<SocketHandlerContext> ctx2 = Skype.getPlugin().createHandle();
		if (!ctx2.isPresent()) {
			return PacketPlayInReply.empty();
		}
		Optional<PacketPlayInReply> reply = ctx2.get().getOutboundHandler()
				.dispatch(ctx2.get(), new PacketPlayOutLogin(authCode));
		if (!reply.isPresent()) {
			return PacketPlayInReply.empty();
		}
		if (reply.get().getStatusCode() != 200) {
			return PacketPlayInReply.empty();
		}
		authCode = UUID.fromString(reply.get().getText());
		UUID conversationId = packet.getConversationId();
		UUID participantId = packet.getParticipantId();
		long timestamp = System.currentTimeMillis();
		String fileName = packet.getFileName();
		long length = packet.getLength();

		final byte[] cipher;

		{
			String pgp = packet.getCipher();
			Optional<Conversation> userLookup = MainForm.get().lookupUser(
					participantId);
			if (!userLookup.isPresent()) {
				return PacketPlayInReply.empty();
			}
			DecryptionResult result = PGPUtilities.decryptAndVerify(pgp,
					userLookup.get());
			if (!result.isSuccessful() || !result.isSignatureVerified()) {
				return PacketPlayInReply.empty();
			}
			cipher = CipherUtilities.decodeBase64(result.getMessage());
		}

		Conversation personWhoIsCalling = null;
		for (Conversation conversation : MainForm.get().getConversations()) {
			if (conversation.getUniqueId().equals(conversationId)) {
				personWhoIsCalling = conversation;
			}
		}
		boolean hit = false;
		if (personWhoIsCalling == null) {
			Optional<Conversation> userLookup = MainForm.get().lookupUser(
					conversationId);
			if (userLookup.isPresent()) {
				personWhoIsCalling = userLookup.get();
				hit = true;
			}
		}
		MainForm.get().ongoingFileTransferConversation = personWhoIsCalling;
		MainForm.get().ongoingFileTransferId = fileTransferId;
		MainForm.get().ongoingFileTransferCipher = cipher;
		MainForm.get().ongoingFileTransferFileName = fileName;
		MainForm.get().ongoingFileTransferLength = length;
		MainForm.get().fileTransferDataTransferFinished = false;
		reply = ctx2
				.get()
				.getOutboundHandler()
				.dispatch(
						ctx2.get(),
						new PacketPlayOutAcceptFileTransferRequest(authCode,
								fileTransferId));
		if (!reply.isPresent()) {
			return PacketPlayInReply.empty();
		}
		if (reply.get().getStatusCode() != 200) {
			return PacketPlayInReply.empty();
		}
		if (participantId.equals(loggedInUser)) {
			MainForm.get().fileTransferOutgoingAudioSockets.add(ctx2.get()
					.getSocket());
		} else if (hit) {
			MainForm.get().getConversations().add(personWhoIsCalling);
		}
		return PacketPlayInReply.empty();
	}
}
