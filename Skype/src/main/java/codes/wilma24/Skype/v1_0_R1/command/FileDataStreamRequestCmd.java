package codes.wilma24.Skype.v1_0_R1.command;

import java.awt.Desktop;
import java.awt.Window;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.Optional;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.ProgressMonitorInputStream;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandExecutor;
import codes.wilma24.Skype.api.v1_0_R1.packet.Packet;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInFileDataStreamRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutAcceptFileDataStreamRequest;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutFinishedReadingFileTransferData;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.cipher.CipherUtilities;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;
import codes.wilma24.Skype.v1_0_R1.plugin.Skype;

public class FileDataStreamRequestCmd extends CommandExecutor {

	@Override
	public PacketPlayInReply onCommand(SocketHandlerContext ctx, Object msg) {
		PacketPlayInFileDataStreamRequest packet = Packet.fromJson(
				msg.toString(), PacketPlayInFileDataStreamRequest.class);
		UUID fileTransferId = packet.getFileTransferId();
		UUID participantId = packet.getParticipantId();
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
		ctx2.get()
				.getOutboundHandler()
				.write(ctx2.get(),
						new PacketPlayOutAcceptFileDataStreamRequest(authCode,
								participantId, fileTransferId));
		final Socket socket = ctx2.get().getSocket();
		String fileName = MainForm.get().ongoingFileTransferFileName;
		long length = MainForm.get().ongoingFileTransferLength;
		final UUID authCode2 = authCode;
		if (MainForm.get().ongoingFileTransferData == null) {
			Thread thread = new Thread(
					() -> {
						try {
							socket.setSoTimeout(0);
							MainForm.get().fileTransferIncomingAudioSockets
									.add(ctx2.get().getSocket());
							MainForm.get().ongoingFileTransfer = true;
							JFrame mainForm = MainForm.get();
							byte[] cipher = MainForm.get().ongoingFileTransferCipher;
							long bytesRead = 0;
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							ProgressMonitorInputStream progressMonitorInputStream;
							final Window parent = mainForm;
							try (BufferedInputStream bufferedInputStream = new BufferedInputStream(
									progressMonitorInputStream = new ProgressMonitorInputStream(
											parent, "Retrieving " + fileName,
											socket.getInputStream()))) {
								ProgressMonitor progressMonitor = progressMonitorInputStream
										.getProgressMonitor();
								progressMonitor.setMinimum(0);
								progressMonitor.setMaximum((int) length);
								progressMonitor.setMillisToDecideToPopup(100);
								progressMonitor.setMillisToPopup(100);
								byte[] b = new byte[8192];
								int len;
								while ((len = bufferedInputStream.read(b)) != -1) {
									if (parent instanceof JFrame) {
										if (parent.isEnabled()) {
											parent.setEnabled(false);
										}
									}
									bytesRead += len;
									baos.write(Arrays.copyOf(b, len));
									progressMonitor
											.setNote((length - bytesRead)
													/ 1000 + " more kb to read");
									if (bytesRead == length) {
										break;
									}
								}
							} catch (Exception e) {
								e.printStackTrace();
								throw e;
							} finally {
								parent.setEnabled(true);
								if (parent instanceof JFrame) {
									java.awt.EventQueue
											.invokeLater(new Runnable() {
												@Override
												public void run() {
													((JFrame) parent).toFront();
													parent.repaint();
												}
											});
								}
							}
							if (bytesRead > 0) {
								Cipher cipher2 = CipherUtilities
										.createDecryptionCipher(cipher);
								ByteArrayInputStream in = new ByteArrayInputStream(
										baos.toByteArray());
								File file = new File(System
										.getProperty("java.io.tmpdir"), "Skype");
								file.mkdirs();
								file = new File(file, fileName);
								FileOutputStream out = new FileOutputStream(
										file);
								CipherOutputStream cipherOutputStream = new CipherOutputStream(
										out, cipher2);
								int numRead;
								byte[] buf = new byte[4096];
								while ((numRead = in.read(buf)) >= 0) {
									cipherOutputStream.write(buf, 0, numRead);
								}
								in.close();
								cipherOutputStream.flush();
								cipherOutputStream.close();
								Desktop desktop = Desktop.getDesktop();
								desktop.open(new File(file.getParent()));
							}
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						Optional<SocketHandlerContext> ctx3 = Skype.getPlugin()
								.createHandle();
						if (!ctx3.isPresent()) {
							return;
						}
						ctx3.get()
								.getOutboundHandler()
								.dispatch(
										ctx3.get(),
										new PacketPlayOutFinishedReadingFileTransferData(
												authCode2, fileTransferId));
						if (MainForm.get().ongoingFileTransferId != null)
							if (fileTransferId.equals(MainForm.get().ongoingFileTransferId)) {
								MainForm.get().ongoingFileTransfer = false;
								MainForm.get().ongoingFileTransferConversation = null;
								MainForm.get().ongoingFileTransferParticipants
										.clear();
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
								try {
									for (Socket socket2 : MainForm.get().fileTransferOutgoingAudioSockets) {
										socket2.close();
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								MainForm.get().fileTransferIncomingAudioSockets
										.clear();
								MainForm.get().fileTransferOutgoingAudioSockets
										.clear();
							}
					});
			thread.start();
		}

		return PacketPlayInReply.empty();
	}

}
