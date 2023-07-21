package codes.elisa32.Skype.server.v1_0_R1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandMap;
import codes.elisa32.Skype.api.v1_0_R1.data.types.Call;
import codes.elisa32.Skype.api.v1_0_R1.data.types.FileTransfer;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketType;
import codes.elisa32.Skype.api.v1_0_R1.plugin.event.EventHandler;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.sqlite.ConfigurationSection;
import codes.elisa32.Skype.api.v1_0_R1.sqlite.FileConfiguration;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.command.AcceptCallDataStreamRequestCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.AcceptCallRequestCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.AcceptContactRequestCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.AcceptFileDataStreamRequestCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.AcceptFileTransferRequestCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.DeclineCallRequestCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.DeclineContactRequestCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.DeclineFileTransferRequestCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.EnteringListeningModeCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.FinishedReadingFileTransferDataCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.LoginCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.LookupContactsCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.LookupConversationHistoryCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.LookupConversationParticipantsCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.LookupGroupChatAdminsCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.LookupMessageHistoryCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.LookupOnlineStatusCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.LookupUserCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.LookupUserRegistryCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.RefreshTokenCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.RegisterCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.RemoveMessageCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.SendCallRequestCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.SendContactRequestCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.SendFileTransferRequestCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.SendMessageCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.UpdateGroupChatParticipantsCmd;
import codes.elisa32.Skype.server.v1_0_R1.command.UpdateUserCmd;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;
import codes.elisa32.Skype.server.v1_0_R1.manager.ConversationManager;
import codes.elisa32.Skype.server.v1_0_R1.manager.UserManager;

public class Skype {

	private static Skype plugin;

	private volatile ConfigurationSection config;

	private ServerSocket serverSocket;

	private HashMap<UUID, Connection> connectionMap = new HashMap<>();

	private ConcurrentHashMap<UUID, Call> callMap = new ConcurrentHashMap<>();

	private ConcurrentHashMap<UUID, FileTransfer> fileTransferMap = new ConcurrentHashMap<>();

	private ConversationManager conversationManager;

	private UserManager userManager;

	static {
		plugin = new Skype();
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
		conversationManager = new ConversationManager(config);
		userManager = new UserManager(config);
	}

	public static void main(String[] args) throws IOException {
		Skype skype = Skype.getPlugin();
		skype.onEnable();
	}

	public static Skype getPlugin() {
		return plugin;
	}

	public ConfigurationSection getConfig() {
		return config;
	}

	public HashMap<UUID, Connection> getConnectionMap() {
		return connectionMap;
	}

	public ConcurrentHashMap<UUID, Call> getCallMap() {
		return callMap;
	}

	public void setCallMap(ConcurrentHashMap<UUID, Call> callMap) {
		this.callMap = callMap;
	}

	public ConcurrentHashMap<UUID, FileTransfer> getFileTransferMap() {
		return fileTransferMap;
	}

	public void setFileTransferMap(
			ConcurrentHashMap<UUID, FileTransfer> fileTransferMap) {
		this.fileTransferMap = fileTransferMap;
	}

	public ConversationManager getConversationManager() {
		return conversationManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	@EventHandler
	public void onEnable() throws IOException {
		CommandMap.register(PacketType.REGISTER, new RegisterCmd());
		CommandMap.register(PacketType.LOGIN, new LoginCmd());
		CommandMap.register(PacketType.REFRESH_TOKEN, new RefreshTokenCmd());
		CommandMap.register(PacketType.UPDATE_USER, new UpdateUserCmd());
		CommandMap.register(PacketType.UPDATE_GROUP_CHAT_PARTICIPANTS,
				new UpdateGroupChatParticipantsCmd());
		CommandMap.register(PacketType.ENTERING_LISTEN_MODE,
				new EnteringListeningModeCmd());
		CommandMap.register(PacketType.LOOKUP_USER, new LookupUserCmd());
		CommandMap.register(PacketType.LOOKUP_ONLINE_STATUS,
				new LookupOnlineStatusCmd());
		CommandMap.register(PacketType.LOOKUP_USER_REGISTRY,
				new LookupUserRegistryCmd());
		CommandMap.register(PacketType.MESSAGE_OUT, new SendMessageCmd());
		CommandMap.register(PacketType.REMOVE_MESSAGE_OUT,
				new RemoveMessageCmd());
		CommandMap.register(PacketType.LOOKUP_CONVERSATION_HISTORY,
				new LookupConversationHistoryCmd());
		CommandMap.register(PacketType.LOOKUP_CONVERSATION_PARTICIPANTS,
				new LookupConversationParticipantsCmd());
		CommandMap.register(PacketType.LOOKUP_GROUP_CHAT_ADMINS,
				new LookupGroupChatAdminsCmd());
		CommandMap.register(PacketType.LOOKUP_MESSAGE_HISTORY,
				new LookupMessageHistoryCmd());
		CommandMap
				.register(PacketType.LOOKUP_CONTACTS, new LookupContactsCmd());
		CommandMap.register(PacketType.ACCEPT_CONTACT_REQUEST,
				new AcceptContactRequestCmd());
		CommandMap.register(PacketType.DECLINE_CONTACT_REQUEST,
				new DeclineContactRequestCmd());
		CommandMap.register(PacketType.SEND_CONTACT_REQUEST,
				new SendContactRequestCmd());
		CommandMap.register(PacketType.SEND_CALL_REQUEST,
				new SendCallRequestCmd());
		CommandMap.register(PacketType.ACCEPT_CALL_REQUEST,
				new AcceptCallRequestCmd());
		CommandMap.register(PacketType.ACCEPT_CALL_DATA_STREAM_REQUEST,
				new AcceptCallDataStreamRequestCmd());
		CommandMap.register(PacketType.DECLINE_CALL_REQUEST,
				new DeclineCallRequestCmd());
		/**
		 * Experimental
		 */
		CommandMap.register(PacketType.SEND_FILE_TRANSFER_REQUEST,
				new SendFileTransferRequestCmd());
		CommandMap.register(PacketType.ACCEPT_FILE_TRANSFER_REQUEST,
				new AcceptFileTransferRequestCmd());
		CommandMap.register(PacketType.ACCEPT_FILE_DATA_STREAM_REQUEST,
				new AcceptFileDataStreamRequestCmd());
		CommandMap.register(PacketType.DECLINE_FILE_TRANSFER_REQUEST,
				new DeclineFileTransferRequestCmd());
		CommandMap.register(PacketType.FINISHED_READING_FILE_TRANSFER_DATA,
				new FinishedReadingFileTransferDataCmd());

		serverSocket = new ServerSocket(28109);
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				SocketHandlerContext ctx = new SocketHandlerContext(socket);
				ctx.fireInboundHandlerActive(new Runnable() {

					@Override
					public void run() {
						/*
						 * incoming data stream got closed
						 */
					}

				});
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
		serverSocket.close();
	}
}
