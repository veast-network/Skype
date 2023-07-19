package codes.elisa32.Skype.v1_0_R1.plugin;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandMap;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketType;
import codes.elisa32.Skype.api.v1_0_R1.plugin.event.EventHandler;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.sqlite.ConfigurationSection;
import codes.elisa32.Skype.api.v1_0_R1.sqlite.FileConfiguration;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.v1_0_R1.command.AcceptCallRequestCmd;
import codes.elisa32.Skype.v1_0_R1.command.AcceptContactRequestCmd;
import codes.elisa32.Skype.v1_0_R1.command.AcceptFileDataStreamRequestCmd;
import codes.elisa32.Skype.v1_0_R1.command.AcceptFileTransferRequestCmd;
import codes.elisa32.Skype.v1_0_R1.command.CallDataStreamRequestCmd;
import codes.elisa32.Skype.v1_0_R1.command.CallParticipantsChangedCmd;
import codes.elisa32.Skype.v1_0_R1.command.CallRequestCmd;
import codes.elisa32.Skype.v1_0_R1.command.DeclineCallRequestCmd;
import codes.elisa32.Skype.v1_0_R1.command.DeclineFileTransferRequestCmd;
import codes.elisa32.Skype.v1_0_R1.command.FileDataStreamRequestCmd;
import codes.elisa32.Skype.v1_0_R1.command.FileTransferParticipantsChangedCmd;
import codes.elisa32.Skype.v1_0_R1.command.FileTransferRequestCmd;
import codes.elisa32.Skype.v1_0_R1.command.GroupChatParticipantsChangedCmd;
import codes.elisa32.Skype.v1_0_R1.command.ReceiveMessageCmd;
import codes.elisa32.Skype.v1_0_R1.command.RemoveMessageCmd;
import codes.elisa32.Skype.v1_0_R1.command.UpdateUserCmd;
import codes.elisa32.Skype.v1_0_R1.command.UserRegistryChangedCmd;

public class Skype {

	private static Skype plugin;

	private ConfigurationSection config;

	private SocketHandlerContext handle;

	private ArrayList<SocketHandlerContext> handles = new ArrayList<>();

	private String hostname = "eu-frankfurt-1.elisa32.codes";

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
		/**
		 * Experimental
		 */
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
	}

	public Skype() {
		try {
			config = new FileConfiguration("config.db")
					.getConfigurationSection();
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

}
