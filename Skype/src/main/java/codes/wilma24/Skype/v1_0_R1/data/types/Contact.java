package codes.wilma24.Skype.v1_0_R1.data.types;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import codes.wilma24.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.wilma24.Skype.v1_0_R1.imageio.ImageIO;

import com.google.gson.Gson;

public class Contact extends Conversation {

	public volatile boolean favorite = false;

	/*
	 * Show profile
	 */
	public volatile String mood;
	public volatile Status onlineStatus = Status.OFFLINE;
	public volatile String mobilePhone;
	public volatile String homePhone;
	public volatile String officePhone;

	/*
	 * Show full profile
	 */
	public volatile String email;
	public volatile String countryName;
	public volatile String state;
	public volatile String city;
	public volatile String timeZone;
	public volatile String website;
	public volatile String gender;
	public volatile long birthDate;
	public volatile String language;
	public volatile int numberOfContacts;
	public volatile String aboutMe;
	public volatile long lastLogin;

	public Contact() {

	}

	public Contact(String json) {
		readFromJson(json);
	}

	@Override
	public void readFromJson(String json) {
		Gson gson = GsonBuilder.create();
		Contact clazz = gson.fromJson(json, Contact.class);
		/**
		 * Conversation
		 */
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
		/**
		 * Contact
		 */
		this.favorite = clazz.favorite;
		this.mood = clazz.mood;
		/* this.onlineStatus = clazz.onlineStatus; */
		this.mobilePhone = clazz.mobilePhone;
		this.homePhone = clazz.homePhone;
		this.officePhone = clazz.officePhone;
		this.email = clazz.email;
		this.countryName = clazz.countryName;
		this.state = clazz.state;
		this.city = clazz.city;
		this.timeZone = clazz.timeZone;
		this.website = clazz.website;
		this.gender = clazz.gender;
		this.birthDate = clazz.birthDate;
		this.language = clazz.language;
		this.numberOfContacts = clazz.numberOfContacts;
		this.aboutMe = clazz.aboutMe;
		this.lastLogin = clazz.lastLogin;
		Contact contact = (Contact) this;
		Map.Entry<JPanel, JLabel> entry = ImageIO.getConversationIconPanel(
				this.getImageIcon(), contact.getOnlineStatus());
		onlineStatusPanel = entry.getKey();
		onlineStatusLabel = entry.getValue();
	}

	@Override
	public String exportAsJson() {
		Gson gson = GsonBuilder.create();
		return gson.toJson(this);
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
	}

	public String getMood() {
		return mood;
	}

	public void setMood(String mood) {
		this.mood = mood;
	}

	public Status getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(Status onlineStatus) {
		this.onlineStatus = onlineStatus;
		JLabel iconLabel = getOnlineStatusLabel();
		Map.Entry<JPanel, JLabel> entry = ImageIO.getConversationIconPanel(
				this.getImageIcon(), onlineStatus);
		iconLabel.setIcon(entry.getValue().getIcon());
		iconLabel.validate();
		iconLabel.repaint();
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	public String getOfficePhone() {
		return officePhone;
	}

	public void setOfficePhone(String officePhone) {
		this.officePhone = officePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public TimeZone getTimeZone() {
		return TimeZone.getTimeZone(timeZone);
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone.getDisplayName(Locale.ENGLISH);
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Date getBirthDate() {
		return new Date(birthDate);
	}

	public void setBirthDate(Date birthDate) {
		this.birthDate = birthDate.getTime();
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getNumberOfContacts() {
		return numberOfContacts;
	}

	public void setNumberOfContacts(int numberOfContacts) {
		this.numberOfContacts = numberOfContacts;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public void setAboutMe(String aboutMe) {
		this.aboutMe = aboutMe;
	}
	
	public long getLastLogin() {
		return lastLogin;
	}
	
	public void setLastLogin(long lastLogin) {
		this.lastLogin = lastLogin;
	}

}
