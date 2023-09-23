package codes.wilma24.Skype.server.v1_0_R1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.util.Arrays;

import codes.wilma24.Skype.api.v1_0_R1.command.CommandMap;
import codes.wilma24.Skype.api.v1_0_R1.data.types.Call;
import codes.wilma24.Skype.api.v1_0_R1.data.types.FileTransfer;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketType;
import codes.wilma24.Skype.api.v1_0_R1.pgp.PGPUtilities;
import codes.wilma24.Skype.api.v1_0_R1.plugin.event.EventHandler;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.sqlite.ConfigurationSection;
import codes.wilma24.Skype.api.v1_0_R1.sqlite.FileConfiguration;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.server.v1_0_R1.command.AcceptCallDataStreamRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.AcceptCallRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.AcceptContactRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.AcceptFileDataStreamRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.AcceptFileTransferRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.AcceptVideoCallDataStreamRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.AcceptVideoCallRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.DeclineCallRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.DeclineContactRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.DeclineFileTransferRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.DeclineVideoCallRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.EnteringListeningModeCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.FinishedReadingFileTransferDataCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.LoginCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.LookupContactsCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.LookupConversationHistoryCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.LookupConversationLastAccessedCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.LookupConversationParticipantsCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.LookupGroupChatAdminsCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.LookupMessageHistoryCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.LookupOnlineStatusCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.LookupUserCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.LookupUserRegistryCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.LookupVoipContactsCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.MarkConversationAsReadCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.PubKeyExchangeCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.RefreshTokenCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.RegisterCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.RemoveMessageCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.SendCallRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.SendContactRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.SendFileTransferRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.SendMessageCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.SendVideoCallRequestCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.UpdateGroupChatParticipantsCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.UpdateUserCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.UpdateVoipContactsCmd;
import codes.wilma24.Skype.server.v1_0_R1.command.VideoCallResolutionChangedCmd;
import codes.wilma24.Skype.server.v1_0_R1.data.types.Connection;
import codes.wilma24.Skype.server.v1_0_R1.manager.ConversationManager;
import codes.wilma24.Skype.server.v1_0_R1.manager.UserManager;

public class Skype {

	private static Skype plugin;

	private volatile ConfigurationSection config;

	private ServerSocket serverSocket;

	private ConcurrentHashMap<String, String> tokenMap = new ConcurrentHashMap<>();

	private ConcurrentHashMap<UUID, Connection> connectionMap = new ConcurrentHashMap<>();

	private ConcurrentHashMap<UUID, Call> callMap = new ConcurrentHashMap<>();

	private ConcurrentHashMap<UUID, FileTransfer> fileTransferMap = new ConcurrentHashMap<>();

	private ConversationManager conversationManager;

	private UserManager userManager;

	private ConcurrentHashMap<String, ConcurrentHashMap<String, Integer>> dayToRegistrationRateLimiterMap = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, Long> registrationRateLimiterMap = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, Long> messageRateLimiterMap = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, Long> updateUserRateLimiterMap = new ConcurrentHashMap<>();

	private ConcurrentHashMap<String, Long> lookupUserRegistryRateLimiterMap = new ConcurrentHashMap<>();

	private static ArrayList<String> proxyList = new ArrayList<>();

	private static ArrayList<String> bannedIpList = new ArrayList<>();

	static {
		long before = System.currentTimeMillis();
		plugin = new Skype();
		try {
			InputStream is = Skype.class.getResource("/IP2PROXY-LITE-PX2.CSV")
					.openStream();
			byte[] b = new byte[1024];
			int read;
			File file = File.createTempFile("IP2PROXY-LITE-PX2", ".CSV");
			FileOutputStream fos = new FileOutputStream(file);
			while ((read = is.read(b)) != -1) {
				fos.write(Arrays.copyOf(b, read));
				fos.flush();
			}
			fos.close();
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				String line;
				while ((line = br.readLine()) != null) {
					String ipnum = line.substring(1);
					ipnum = ipnum.substring(0, ipnum.indexOf("\""));
					long ipnumLong = Long.parseLong(ipnum);
					String ip = "";
					ip = ((ipnumLong / 16777216) % 256) + "."
							+ ((ipnumLong / 65536) % 256) + "."
							+ ((ipnumLong / 256) % 256) + "."
							+ (ipnumLong % 256);
					proxyList.add(ip);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			PGPUtilities.createOrLookupPublicKey();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PGPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long elapsed = System.currentTimeMillis() - before;
		System.out.println("took " + elapsed
				+ "ms to start server on port 28109");
	}

	public boolean testRateLimitRegistration(String ip) {
		long timestamp = Long.parseLong(registrationRateLimiterMap
				.getOrDefault(ip, 0L) + "");
		registrationRateLimiterMap.put(ip, System.currentTimeMillis());
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		ConcurrentHashMap<String, Integer> map = dayToRegistrationRateLimiterMap
				.getOrDefault(format.format(date),
						new ConcurrentHashMap<String, Integer>());
		int prev = map.getOrDefault(ip, 0);
		map.put(ip, prev + 1);
		dayToRegistrationRateLimiterMap.put(format.format(date), map);
		if (prev + 1 > 10) {
			bannedIpList.add(ip);
		}
		if (prev + 1 > 5) {
			return true;
		}
		if (System.currentTimeMillis() - timestamp < 10000L) {
			return true;
		}
		return false;
	}

	public boolean testRateLimitMessaging(String skypeName, String ip) {
		{
			long timestamp = Long.parseLong(messageRateLimiterMap.getOrDefault(
					ip, 0L) + "");
			messageRateLimiterMap.put(ip, System.currentTimeMillis());
			if (System.currentTimeMillis() - timestamp < 2000L) {
				return true;
			}
		}
		{
			long timestamp = Long.parseLong(messageRateLimiterMap.getOrDefault(
					skypeName, 0L) + "");
			messageRateLimiterMap.put(skypeName, System.currentTimeMillis());
			if (System.currentTimeMillis() - timestamp < 2000L) {
				return true;
			}
		}
		return false;
	}

	public boolean testRateLimitUpdateUser(String skypeName, String ip) {
		{
			long timestamp = Long.parseLong(updateUserRateLimiterMap
					.getOrDefault(ip, 0L) + "");
			updateUserRateLimiterMap.put(ip, System.currentTimeMillis());
			if (System.currentTimeMillis() - timestamp < 10000L) {
				return true;
			}
		}
		{
			long timestamp = Long.parseLong(updateUserRateLimiterMap
					.getOrDefault(skypeName, 0L) + "");
			updateUserRateLimiterMap.put(skypeName, System.currentTimeMillis());
			if (System.currentTimeMillis() - timestamp < 2000L) {
				return true;
			}
		}
		return false;
	}

	public boolean testRateLimitLookupUserRegistry(String skypeName, String ip) {
		{
			long timestamp = Long.parseLong(lookupUserRegistryRateLimiterMap
					.getOrDefault(ip, 0L) + "");
			lookupUserRegistryRateLimiterMap
					.put(ip, System.currentTimeMillis());
			if (System.currentTimeMillis() - timestamp < 10000L) {
				return true;
			}
		}
		{
			long timestamp = Long.parseLong(lookupUserRegistryRateLimiterMap
					.getOrDefault(skypeName, 0L) + "");
			lookupUserRegistryRateLimiterMap.put(skypeName,
					System.currentTimeMillis());
			if (System.currentTimeMillis() - timestamp < 10000L) {
				return true;
			}
		}
		return false;
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

	public ConcurrentHashMap<UUID, Connection> getConnectionMap() {
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
		CommandMap.register(PacketType.MARK_CONVERSATION_AS_READ,
				new MarkConversationAsReadCmd());
		CommandMap.register(PacketType.LOOKUP_CONVERSATION_LAST_ACCESSED,
				new LookupConversationLastAccessedCmd());
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
		CommandMap.register(PacketType.SEND_VIDEO_CALL_REQUEST,
				new SendVideoCallRequestCmd());
		CommandMap.register(PacketType.ACCEPT_VIDEO_CALL_REQUEST,
				new AcceptVideoCallRequestCmd());
		CommandMap.register(PacketType.DECLINE_VIDEO_CALL_REQUEST,
				new DeclineVideoCallRequestCmd());
		CommandMap.register(PacketType.ACCEPT_VIDEO_CALL_DATA_STREAM_REQUEST,
				new AcceptVideoCallDataStreamRequestCmd());
		CommandMap.register(PacketType.VIDEO_CALL_RESOLUTION_CHANGED,
				new VideoCallResolutionChangedCmd());
		CommandMap.register(PacketType.PUB_KEY_EXCHANGE,
				new PubKeyExchangeCmd());
		CommandMap.register(PacketType.UPDATE_VOIP_CONTACTS,
				new UpdateVoipContactsCmd());
		CommandMap.register(PacketType.LOOKUP_VOIP_CONTACTS,
				new LookupVoipContactsCmd());
		serverSocket = new ServerSocket(28109);
		outerLoop: while (true) {
			try {
				Socket socket = serverSocket.accept();
				String ip = socket.getInetAddress().getHostAddress();
				long before = System.currentTimeMillis();
				if (ip.equals("127.0.0.1")) {
				} else {
					for (String ip2 : proxyList) {
						if (ip.equals(ip2)) {
							long elapsed = System.currentTimeMillis() - before;
							System.out.println("took " + elapsed
									+ "ms to block " + ip);
							socket.close();
							continue outerLoop;
						}
					}
					for (String ip2 : bannedIpList) {
						if (ip.equals(ip2)) {
							long elapsed = System.currentTimeMillis() - before;
							System.out.println("took " + elapsed
									+ "ms to block " + ip);
							socket.close();
							continue outerLoop;
						}
					}
				}
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

	public ConcurrentHashMap<String, String> getTokenMap() {
		return tokenMap;
	}

	public void setTokenMap(ConcurrentHashMap<String, String> tokenMap) {
		this.tokenMap = tokenMap;
	}
}
