package codes.wilma24.Skype.v1_0_R1.plugin;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.bouncycastle.openpgp.PGPSecretKeyRing;
import org.pgpainless.PGPainless;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandMap;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketType;
import codes.wilma24.Skype.api.v1_0_R1.plugin.event.EventHandler;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.sqlite.ConfigurationSection;
import codes.wilma24.Skype.api.v1_0_R1.sqlite.FileConfiguration;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.command.AcceptCallRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.AcceptContactRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.AcceptFileDataStreamRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.AcceptFileTransferRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.CallDataStreamRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.CallParticipantsChangedCmd;
import codes.wilma24.Skype.v1_0_R1.command.CallRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.DeclineCallRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.DeclineFileTransferRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.FileDataStreamRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.FileTransferParticipantsChangedCmd;
import codes.wilma24.Skype.v1_0_R1.command.FileTransferRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.GroupChatParticipantsChangedCmd;
import codes.wilma24.Skype.v1_0_R1.command.ReceiveMessageCmd;
import codes.wilma24.Skype.v1_0_R1.command.RemoveMessageCmd;
import codes.wilma24.Skype.v1_0_R1.command.UpdateUserCmd;
import codes.wilma24.Skype.v1_0_R1.command.UserRegistryChangedCmd;
import codes.wilma24.Skype.v1_0_R1.command.VideoCallDataStreamRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.VideoCallRequestCmd;
import codes.wilma24.Skype.v1_0_R1.command.VideoCallResolutionChangedCmd;
import codes.wilma24.Skype.v1_0_R1.pgp.PGPUtilities;

public class Skype {

	private static Skype plugin;

	private ConfigurationSection config;

	private SocketHandlerContext handle;

	private ArrayList<SocketHandlerContext> handles = new ArrayList<>();

	private String hostname = "eu-frankfurt-1.wilma24.codes";

	private PGPSecretKeyRing privKey;

	private PGPPublicKeyRing pubKey;

	static {
		plugin = new Skype();
		CommandMap.register(PacketType.MESSAGE_IN, new ReceiveMessageCmd());
		CommandMap.register(PacketType.ACCEPT_CONTACT_REQUEST_IN,
				new AcceptContactRequestCmd());
		CommandMap.register(PacketType.REMOVE_MESSAGE_IN,
				new RemoveMessageCmd());
		CommandMap.register(PacketType.CALL_REQUEST_IN, new CallRequestCmd());
		CommandMap.register(PacketType.ACCEPT_CALL_REQUEST_IN,
				new AcceptCallRequestCmd());
		CommandMap.register(PacketType.DECLINE_CALL_REQUEST_IN,
				new DeclineCallRequestCmd());
		CommandMap.register(PacketType.CALL_DATA_STREAM_REQUEST_IN,
				new CallDataStreamRequestCmd());
		CommandMap.register(PacketType.USER_REGISTRY_CHANGED_IN,
				new UserRegistryChangedCmd());
		CommandMap.register(PacketType.CALL_PARTICIPANTS_CHANGED_IN,
				new CallParticipantsChangedCmd());
		CommandMap.register(PacketType.GROUP_CHAT_PARTICIPANTS_CHANGED_IN,
				new GroupChatParticipantsChangedCmd());
		CommandMap.register(PacketType.UPDATE_USER_IN, new UpdateUserCmd());
		CommandMap.register(PacketType.FILE_TRANSFER_REQUEST_IN,
				new FileTransferRequestCmd());
		CommandMap.register(PacketType.FILE_TRANSFER_PARTICIPANTS_CHANGED_IN,
				new FileTransferParticipantsChangedCmd());
		CommandMap.register(PacketType.FILE_DATA_STREAM_REQUEST_IN,
				new FileDataStreamRequestCmd());
		CommandMap.register(PacketType.ACCEPT_FILE_TRANSFER_REQUEST_IN,
				new AcceptFileTransferRequestCmd());
		CommandMap.register(PacketType.ACCEPT_FILE_DATA_STREAM_REQUEST_IN,
				new AcceptFileDataStreamRequestCmd());
		CommandMap.register(PacketType.DECLINE_FILE_TRANSFER_REQUEST,
				new DeclineFileTransferRequestCmd());
		CommandMap.register(PacketType.VIDEO_CALL_REQUEST_IN,
				new VideoCallRequestCmd());
		CommandMap.register(PacketType.VIDEO_CALL_DATA_STREAM_REQUEST_IN,
				new VideoCallDataStreamRequestCmd());
		CommandMap.register(PacketType.VIDEO_CALL_RESOLUTION_CHANGED_IN,
				new VideoCallResolutionChangedCmd());
	}

	public Skype() {
		try {
			config = new FileConfiguration("config.db")
					.getConfigurationSection();
			try {
				setPrivKey(PGPUtilities.createOrLookupPrivateKey("private"));
				setPubKey(PGPainless.readKeyRing().publicKeyRing(
						PGPUtilities.createOrLookupPublicKey("private")));
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (PGPException e) {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Skype getPlugin() {
		return plugin;
	}

	public ConfigurationSection getConfig() {
		return config;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public Optional<SocketHandlerContext> createHandle() {
		try {
			Socket socket = new Socket(hostname, 28109);
			SocketHandlerContext handle = new SocketHandlerContext(socket);
			handles.add(handle);
			return Optional.of(handle);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public SocketHandlerContext getHandle() {
		if (handle == null) {
			Optional<SocketHandlerContext> handle = createHandle();
			if (handle.isPresent()) {
				this.handle = handle.get();
			}
		}
		return handle;
	}

	public void setHandle(SocketHandlerContext ctx) {
		this.handle = ctx;
	}

	public UUID getUniqueId(String skypeName) {
		return UUID.nameUUIDFromBytes(("skype:" + skypeName).getBytes());
	}

	@EventHandler
	public void onEnable() {

	}

	@EventHandler
	public void onDisable() {
		for (SocketHandlerContext ctx : handles.toArray(
				new SocketHandlerContext[0]).clone()) {
			try {
				ctx.fireSocketInactive();
				ctx.getSocket().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		handles.clear();
	}

	public PGPPublicKeyRing getPubKey() {
		return pubKey;
	}

	public void setPubKey(PGPPublicKeyRing pubKey) {
		this.pubKey = pubKey;
	}

	public PGPSecretKeyRing getPrivKey() {
		return privKey;
	}

	public void setPrivKey(PGPSecretKeyRing privKey) {
		this.privKey = privKey;
	}

}
