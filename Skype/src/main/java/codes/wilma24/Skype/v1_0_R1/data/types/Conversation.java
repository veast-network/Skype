package codes.wilma24.Skype.v1_0_R1.data.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.bouncycastle.openpgp.PGPPublicKeyRing;
import org.pgpainless.PGPainless;

import codes.wilma24.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayInReply;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLogin;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLookupConversationParticipants;
import codes.wilma24.Skype.api.v1_0_R1.packet.PacketPlayOutLookupGroupChatAdmins;
import codes.wilma24.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;
import codes.wilma24.Skype.v1_0_R1.forms.MainForm;
import codes.wilma24.Skype.v1_0_R1.imageio.ImageIO;
import codes.wilma24.Skype.v1_0_R1.plugin.Skype;

import com.google.gson.Gson;

public class Conversation {

	/*
	 * Public key
	 */
	public volatile String pubKey;

	public volatile UUID uuid;

	public volatile String skypeName;

	public volatile String name;

	public volatile boolean groupChat = false;

	public volatile String imageIconUrl;

	private volatile transient boolean bot = false;

	public volatile long lastModified;

	public volatile transient int notificationCount;

	private volatile transient boolean hasIncomingFriendRequest = false;

	private volatile transient Message incomingFriendRequestMessage = null;

	private volatile transient boolean hasOutgoingFriendRequest = false;

	public volatile transient ImageIcon imageIcon;

	public volatile transient List<Message> messages = new ArrayList<>();

	protected volatile transient JPanel onlineStatusPanel;

	protected volatile transient JLabel onlineStatusLabel;

	public Conversation() {
		if (this instanceof Bot) {
			this.bot = true;
		}
		if (this instanceof Contact || this instanceof Bot) {
			Contact contact = (Contact) this;
			Map.Entry<JPanel, JLabel> entry = ImageIO.getConversationIconPanel(
					this.getImageIcon(), contact.getOnlineStatus());
			onlineStatusPanel = entry.getKey();
			onlineStatusLabel = entry.getValue();
		} else {
			Map.Entry<JPanel, JLabel> entry = ImageIO.getConversationIconPanel(
					this.getImageIcon(), Status.NOT_A_CONTACT);
			onlineStatusPanel = entry.getKey();
			onlineStatusLabel = entry.getValue();
		}
	}

	public Conversation(String json) {
		readFromJson(json);
	}

	public void readFromJson(String json) {
		Gson gson = GsonBuilder.create();
		Conversation clazz = gson.fromJson(json, Conversation.class);
		this.pubKey = clazz.pubKey;
		this.uuid = clazz.uuid;
		this.skypeName = clazz.skypeName;
		this.name = clazz.name;
		this.groupChat = clazz.groupChat;
		this.imageIconUrl = clazz.imageIconUrl;
		if (imageIconUrl != null) {
			if (imageIconUrl.length() > 0) {
				if (imageIconUrl.startsWith("https://i.imgur.com/")) {
					try {
						URL url = new URL(imageIconUrl);
						URLConnection connection = url.openConnection();
						connection
								.setRequestProperty("User-Agent",
										"Mozilla/5.0 (Windows NT 6.3; Win64; x64; rv:72.0) Gecko/20100101 Firefox/72.0");
						InputStream is = url.openStream();
						ByteArrayOutputStream os = new ByteArrayOutputStream();

						byte[] b = new byte[2048];
						int length;

						while ((length = is.read(b)) != -1) {
							os.write(b, 0, length);
						}

						is.close();
						os.close();

						BufferedImage image = javax.imageio.ImageIO
								.read(new ByteArrayInputStream(os.toByteArray()));

						ImageIcon imgIcon = new ImageIcon(image);

						this.imageIcon = imgIcon;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		if (this instanceof Contact) {
			Contact contact = (Contact) this;
			Map.Entry<JPanel, JLabel> entry = ImageIO.getConversationIconPanel(
					this.getImageIcon(), contact.getOnlineStatus());
			onlineStatusPanel = entry.getKey();
			onlineStatusLabel = entry.getValue();
		} else {
			Map.Entry<JPanel, JLabel> entry = ImageIO.getConversationIconPanel(
					this.getImageIcon(), Status.NOT_A_CONTACT);
			onlineStatusPanel = entry.getKey();
			onlineStatusLabel = entry.getValue();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof Conversation) {
			Conversation conversation = (Conversation) obj;
			if (uuid.toString().equals(conversation.uuid.toString())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return exportAsJson();
	}

	public String exportAsJson() {
		Gson gson = GsonBuilder.create();
		return gson.toJson(this);
	}

	public Optional<PGPPublicKeyRing> getPubKey() {
		try {
			if (pubKey != null) {
				PGPPublicKeyRing pubKey = PGPainless.readKeyRing()
						.publicKeyRing(this.pubKey);
				return Optional.of(pubKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Optional.empty();
	}

	public void setPubKey(String pubKey) {
		this.pubKey = pubKey;
	}

	public UUID getUniqueId() {
		return uuid;
	}

	public void setUniqueId(UUID uuid) {
		this.uuid = uuid;
	}

	public String getSkypeName() {
		return skypeName;
	}

	public void setSkypeName(String skypeName) {
		this.skypeName = skypeName;
	}

	public String getDisplayName() {
		return name;
	}

	public void setDisplayName(String name) {
		this.name = name;
	}

	public Date getLastModified() {
		return new Date(lastModified);
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified.getTime();
	}

	public int getNotificationCount() {
		return notificationCount;
	}

	public void setNotificationCount(int notificationCount) {
		this.notificationCount = notificationCount;
	}

	public boolean isGroupChat() {
		return groupChat;
	}

	public void setGroupChat(boolean groupChat) {
		this.groupChat = groupChat;
		if (this instanceof Contact || this instanceof Bot) {
			Contact contact = (Contact) this;
			Map.Entry<JPanel, JLabel> entry = ImageIO.getConversationIconPanel(
					this.getImageIcon(), contact.getOnlineStatus());
			onlineStatusPanel = entry.getKey();
			onlineStatusLabel = entry.getValue();
		} else {
			Map.Entry<JPanel, JLabel> entry = ImageIO.getConversationIconPanel(
					this.getImageIcon(), Status.NOT_A_CONTACT);
			onlineStatusPanel = entry.getKey();
			onlineStatusLabel = entry.getValue();
		}
	}

	public String getImageIconUrl() {
		return imageIconUrl;
	}

	public void setImageIconUrl(String imageIconUrl) {
		this.imageIconUrl = imageIconUrl;
	}

	public boolean hasIncomingFriendRequest() {
		return hasIncomingFriendRequest;
	}

	public void setHasIncomingFriendRequest(boolean hasIncomingFriendRequest,
			Message message) {
		this.hasIncomingFriendRequest = hasIncomingFriendRequest;
		this.incomingFriendRequestMessage = message;
	}

	public Optional<Message> getIncomingFriendRequestMessage() {
		if (incomingFriendRequestMessage == null) {
			return Optional.empty();
		}
		return Optional.of(incomingFriendRequestMessage);
	}

	public boolean hasOutgoingFriendRequest() {
		return hasOutgoingFriendRequest;
	}

	public void setHasOutgoingFriendRequest(boolean hasOutgoingFriendRequest) {
		this.hasOutgoingFriendRequest = hasOutgoingFriendRequest;
	}

	public List<Message> getMessages() {
		return messages;
	}

	public void setMessages(List<Message> messages) {
		this.messages = messages;
	}

	public ImageIcon getImageIcon() {
		if (imageIcon == null) {
			if (groupChat) {
				return ImageIO.getResourceAsImageIcon("/151908522.png");
			} else if (bot) {
				return ImageIO.getResourceAsImageIcon("/22744.png");
			} else {
				return ImageIO.getResourceAsImageIcon("/1595064335.png");
			}
		} else {
			return imageIcon;
		}
	}

	public void setImageIcon(ImageIcon imageIcon) {
		this.imageIcon = imageIcon;
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}

	public JPanel getOnlineStatusPanel() {
		return onlineStatusPanel;
	}

	public void setOnlineStatusPanel(JPanel onlineStatusPanel) {
		this.onlineStatusPanel = onlineStatusPanel;
	}

	public JLabel getOnlineStatusLabel() {
		return onlineStatusLabel;
	}

	public void setOnlineStatusLabel(JLabel onlineStatusLabel) {
		this.onlineStatusLabel = onlineStatusLabel;
	}

	private List<UUID> participants = null;

	public void setParticipants(List<UUID> participants) {
		this.participants = participants;
	}

	public List<UUID> getParticipants() {
		if (participants != null) {
			return participants;
		}
		if (!groupChat) {
			return new ArrayList<>();
		}
		Optional<SocketHandlerContext> ctx = Skype.getPlugin().createHandle();
		if (!ctx.isPresent()) {
			return new ArrayList<>();
		}
		Optional<PacketPlayInReply> reply = ctx
				.get()
				.getOutboundHandler()
				.dispatch(ctx.get(),
						new PacketPlayOutLogin(MainForm.get().getAuthCode()));
		if (!reply.isPresent() || reply.get().getStatusCode() != 200) {
			return new ArrayList<>();
		}
		UUID authCode = UUID.fromString(reply.get().getText());
		PacketPlayOutLookupConversationParticipants packet = new PacketPlayOutLookupConversationParticipants(
				authCode, this.getUniqueId());
		Optional<PacketPlayInReply> replyPacket = ctx.get()
				.getOutboundHandler().dispatch(ctx.get(), packet);
		if (!replyPacket.isPresent()) {
			return new ArrayList<>();
		}
		if (replyPacket.get().getStatusCode() != 200) {
			return new ArrayList<>();
		}
		String json = replyPacket.get().getText();
		Gson gson = GsonBuilder.create();
		List<String> participants = gson.fromJson(json, List.class);
		List<UUID> participantIds = new ArrayList<>();
		for (String participant : participants) {
			participantIds.add(UUID.fromString(participant));
		}
		this.participants = participantIds;
		return participantIds;
	}

	private List<UUID> groupChatAdmins = null;

	public void setGroupChatAdmins(List<UUID> groupChatAdmins) {
		this.groupChatAdmins = groupChatAdmins;
	}

	public List<UUID> getGroupChatAdmins() {
		if (groupChatAdmins != null) {
			return groupChatAdmins;
		}
		if (!groupChat) {
			return new ArrayList<>();
		}
		Optional<SocketHandlerContext> ctx = Skype.getPlugin().createHandle();
		if (!ctx.isPresent()) {
			return new ArrayList<>();
		}
		Optional<PacketPlayInReply> reply = ctx
				.get()
				.getOutboundHandler()
				.dispatch(ctx.get(),
						new PacketPlayOutLogin(MainForm.get().getAuthCode()));
		if (!reply.isPresent() || reply.get().getStatusCode() != 200) {
			return new ArrayList<>();
		}
		UUID authCode = UUID.fromString(reply.get().getText());
		PacketPlayOutLookupGroupChatAdmins packet = new PacketPlayOutLookupGroupChatAdmins(
				authCode, this.getUniqueId());
		Optional<PacketPlayInReply> replyPacket = ctx.get()
				.getOutboundHandler().dispatch(ctx.get(), packet);
		if (!replyPacket.isPresent()) {
			return new ArrayList<>();
		}
		if (replyPacket.get().getStatusCode() != 200) {
			return new ArrayList<>();
		}
		String json = replyPacket.get().getText();
		Gson gson = GsonBuilder.create();
		List<String> participants = gson.fromJson(json, List.class);
		List<UUID> participantIds = new ArrayList<>();
		for (String participant : participants) {
			participantIds.add(UUID.fromString(participant));
		}
		this.groupChatAdmins = participantIds;
		return participantIds;
	}

}
