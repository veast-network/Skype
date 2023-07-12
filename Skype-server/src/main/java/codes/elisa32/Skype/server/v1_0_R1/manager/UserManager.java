package codes.elisa32.Skype.server.v1_0_R1.manager;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.sqlite.ConfigurationSection;
import codes.elisa32.Skype.api.v1_0_R1.sqlite.FileConfiguration;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

import com.google.gson.Gson;

public class UserManager {

	private ConfigurationSection config;

	public UserManager(ConfigurationSection config) {
		this.config = config;
	}

	public Optional<String> getSkypeName(UUID participantId) {
		if (config.contains("registry." + participantId.toString()
				+ ".skypeName")) {
			try {
				return Optional.of(config.getString("registry."
						+ participantId.toString() + ".skypeName"));
			} catch (SQLException e) {
				e.printStackTrace();
				return Optional.empty();
			}
		} else {
			return Optional.empty();
		}
	}

	public UUID getUniqueId(String skypeName) {
		return UUID.nameUUIDFromBytes(("skype:" + skypeName).getBytes());
	}

	public boolean validatePassword(SocketHandlerContext ctx, String skypeName,
			String password) {
		password = ctx.getCryptographicContext().encodeToString(
				password.getBytes());
		UUID participantId = getUniqueId(skypeName);
		if (userExists(skypeName)) {
			try {
				if (password.equals(config.getString("registry."
						+ participantId.toString() + ".password"))) {
					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public boolean userExists(String skypeName) {
		UUID participantId = getUniqueId(skypeName);
		if (!config.contains("registry." + participantId.toString()
				+ ".fullName")) {
			return false;
		}
		if (!config.contains("registry." + participantId.toString()
				+ ".password")) {
			return false;
		}
		if (!config.contains("registry." + participantId.toString()
				+ ".skypeName")) {
			return false;
		}
		return true;
	}

	public boolean createUser(SocketHandlerContext ctx, String fullName,
			String skypeName, String password) {
		password = ctx.getCryptographicContext().encodeToString(
				password.getBytes(StandardCharsets.UTF_8));
		UUID participantId = getUniqueId(skypeName);
		try {
			config.set("registry." + participantId.toString() + ".fullName",
					fullName);
			config.set("registry." + participantId.toString() + ".password",
					password);
			config.set("registry." + participantId.toString() + ".skypeName",
					skypeName);
			Skype.getPlugin().getConversationManager()
					.addAuthorizedPersonnel(participantId, participantId);
			Skype.getPlugin().getConversationManager()
					.addParticipants(participantId, participantId);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean removeUser(String skypeName) {
		UUID participantId = getUniqueId(skypeName);
		try {
			config.set("registry." + participantId.toString() + ".fullName",
					null);
			config.set("registry." + participantId.toString() + ".password",
					null);
			config.set("registry." + participantId.toString() + ".skypeName",
					null);
			Skype.getPlugin().getConversationManager()
					.removeAuthorizedPersonnel(participantId);
			Skype.getPlugin().getConversationManager()
					.removeParticipants(participantId);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public List<Connection> getConnectionsInCall(UUID participantId, UUID callId) {
		List<Connection> cons = new ArrayList<>();
		for (Connection con : Skype.getPlugin().getConnectionMap().values()) {
			if (con.getExpiryTime() < System.currentTimeMillis()) {
				continue;
			}
			if (!con.getUniqueId().equals(participantId)) {
				continue;
			}
			if (!con.isInCall()) {
				continue;
			}
			if (!con.getCallId().get().equals(callId)) {
				continue;
			}
			if (!con.getSocketHandlerContext().isOutboundHandlerAdded()) {
				continue;
			}
			cons.add(con);
		}
		return cons;
	}

	public List<Connection> getDataStreamConnectionsInCall(UUID participantId,
			UUID callId) {
		List<Connection> cons = new ArrayList<>();
		for (Connection con : Skype.getPlugin().getConnectionMap().values()) {
			if (con.getExpiryTime() < System.currentTimeMillis()) {
				continue;
			}
			if (!con.getUniqueId().equals(participantId)) {
				continue;
			}
			if (!con.isCallDataStream()) {
				continue;
			}
			if (!con.getCallId().get().equals(callId)) {
				continue;
			}
			if (!con.getSocketHandlerContext().isOutboundHandlerAdded()) {
				continue;
			}
			cons.add(con);
		}
		return cons;
	}

	public List<Connection> getListeningConnections(UUID participantId) {
		List<Connection> cons = new ArrayList<>();
		for (Connection con : Skype.getPlugin().getConnectionMap().values()) {
			if (con.getExpiryTime() < System.currentTimeMillis()) {
				continue;
			}
			if (!con.getUniqueId().equals(participantId)) {
				continue;
			}
			if (!con.isListening()) {
				continue;
			}
			if (!con.getSocketHandlerContext().isOutboundHandlerAdded()) {
				continue;
			}
			cons.add(con);
		}
		return cons;
	}

	public Connection getConnection(UUID authCode) {
		if (Skype.getPlugin().getConnectionMap().containsKey(authCode)) {
			Connection con = Skype.getPlugin().getConnectionMap().get(authCode);
			long expiryTime = con.getExpiryTime();
			if (System.currentTimeMillis() > expiryTime) {
				Skype.getPlugin().getConnectionMap().remove(authCode);
				return null;
			} else {
				return con;
			}
		} else {
			return null;
		}
	}

	/**
	 * Contact
	 */

	private Map<String, ConfigurationSection> configByConversationId = new HashMap<>();

	public ConfigurationSection getConfig(UUID conversationId) {
		String key = "config_" + conversationId.toString() + ".db";
		if (configByConversationId.containsKey(key)) {
			return configByConversationId.get(key);
		} else {
			ConfigurationSection section = null;
			try {
				section = new FileConfiguration(key).getConfigurationSection();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			configByConversationId.put(key, section);
			return section;
		}
	}

	public boolean addContact(UUID conversationId, UUID... participantIds) {
		try {
			Gson gson = GsonBuilder.create();
			List<String> list = new ArrayList<String>();
			ConfigurationSection config = this.getConfig(conversationId);
			if (config.contains("conversation." + conversationId.toString()
					+ ".contacts")) {
				String json = config.getString("conversation."
						+ conversationId.toString() + ".contacts");
				list = gson.fromJson(json, List.class);
			}
			for (UUID participant : participantIds) {
				list.add(participant.toString());
			}
			String json = gson.toJson(list);
			config.replace("conversation." + conversationId.toString()
					+ ".contacts", json);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean removeContact(UUID conversationId, UUID... participantIds) {
		try {
			Gson gson = GsonBuilder.create();
			List<String> list = new ArrayList<String>();
			ConfigurationSection config = this.getConfig(conversationId);
			if (config.contains("conversation." + conversationId.toString()
					+ ".contacts")) {
				String json = config.getString("conversation."
						+ conversationId.toString() + ".contacts");
				list = gson.fromJson(json, List.class);
			}
			for (UUID participant : participantIds) {
				list.remove(participant.toString());
			}
			String json = gson.toJson(list);
			config.replace("conversation." + conversationId.toString()
					+ ".contacts", json);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Optional<List<UUID>> getContacts(UUID conversationId) {
		try {
			List<String> list = new ArrayList<String>();
			Gson gson = GsonBuilder.create();
			ConfigurationSection config = this.getConfig(conversationId);
			if (config.contains("conversation." + conversationId.toString()
					+ ".contacts")) {
				String json = config.getString("conversation."
						+ conversationId.toString() + ".contacts");
				list = gson.fromJson(json, List.class);
			}
			List<UUID> participants = new ArrayList<>();
			for (String participant : list) {
				participants.add(UUID.fromString(participant));
			}
			return Optional.of(participants);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public boolean hasContact(UUID conversationId, UUID participantId) {
		Optional<List<UUID>> contacts = getContacts(conversationId);
		if (contacts.isPresent()) {
			for (UUID uuid : contacts.get()) {
				if (uuid.equals(participantId)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean addContactRequest(UUID conversationId,
			UUID... participantIds) {
		try {
			Gson gson = GsonBuilder.create();
			List<String> list = new ArrayList<String>();
			ConfigurationSection config = this.getConfig(conversationId);
			if (config.contains("conversation." + conversationId.toString()
					+ ".contactRequests")) {
				String json = config.getString("conversation."
						+ conversationId.toString() + ".contactRequests");
				list = gson.fromJson(json, List.class);
			}
			for (UUID participant : participantIds) {
				list.add(participant.toString());
			}
			String json = gson.toJson(list);
			config.replace("conversation." + conversationId.toString()
					+ ".contactRequests", json);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean removeContactRequest(UUID conversationId,
			UUID... participantIds) {
		try {
			Gson gson = GsonBuilder.create();
			List<String> list = new ArrayList<String>();
			ConfigurationSection config = this.getConfig(conversationId);
			if (config.contains("conversation." + conversationId.toString()
					+ ".contactRequests")) {
				String json = config.getString("conversation."
						+ conversationId.toString() + ".contactRequests");
				list = gson.fromJson(json, List.class);
			}
			for (UUID participant : participantIds) {
				list.remove(participant.toString());
			}
			String json = gson.toJson(list);
			config.replace("conversation." + conversationId.toString()
					+ ".contactRequests", json);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public Optional<List<UUID>> getContactRequests(UUID conversationId) {
		try {
			List<String> list = new ArrayList<String>();
			Gson gson = GsonBuilder.create();
			ConfigurationSection config = this.getConfig(conversationId);
			if (config.contains("conversation." + conversationId.toString()
					+ ".contactRequests")) {
				String json = config.getString("conversation."
						+ conversationId.toString() + ".contactRequests");
				list = gson.fromJson(json, List.class);
			}
			List<UUID> participants = new ArrayList<>();
			for (String participant : list) {
				participants.add(UUID.fromString(participant));
			}
			return Optional.of(participants);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public boolean hasContactRequest(UUID conversationId, UUID participantId) {
		Optional<List<UUID>> contactRequests = getContactRequests(conversationId);
		if (contactRequests.isPresent()) {
			for (UUID uuid : contactRequests.get()) {
				if (uuid.equals(participantId)) {
					return true;
				}
			}
		}
		return false;
	}
}
