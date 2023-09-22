package codes.wilma24.Skype.v1_0_R1.audioio;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class AudioIO {

	public static AudioIO BUSY = new AudioIO("/BUSY.wav");

	public static AudioIO CALL_ERROR1 = new AudioIO("/CALL_ERROR1.wav");

	public static AudioIO CALL_ERROR2 = new AudioIO("/CALL_ERROR2.wav");

	public static AudioIO CALL_IN = new AudioIO("/CALL_IN.wav");

	public static AudioIO CALL_INIT = new AudioIO("/CALL_INIT.wav");

	public static AudioIO CALL_OUT = new AudioIO("/CALL_OUT.wav");

	public static AudioIO CALL_RECONNECT_CONTINUOUS = new AudioIO(
			"/CALL_RECONNECT_CONTINUOUS.wav");

	public static AudioIO CALL_RECONNECT_FRONT = new AudioIO(
			"/CALL_RECONNECT_FRONT.wav");

	public static AudioIO FORWARDED = new AudioIO("/FORWARDED.wav");

	public static AudioIO FT_COMPLETE = new AudioIO("/FT_COMPLETE.wav");

	public static AudioIO FT_FAILED = new AudioIO("/FT_FAILED.wav");

	public static AudioIO HANGUP = new AudioIO("/HANGUP.wav");

	public static AudioIO HOLD = new AudioIO("/HOLD.wav");

	public static AudioIO IM = new AudioIO("/IM.wav");

	public static AudioIO IM_ACC = new AudioIO("/IM_ACC.wav");

	public static AudioIO IM_SENT = new AudioIO("/IM_SENT.wav");

	public static AudioIO INCOMING_AUTH = new AudioIO("/INCOMING_AUTH.wav");

	public static AudioIO INCOMING_CONTACTS = new AudioIO(
			"/INCOMING_CONTACTS.wav");

	public static AudioIO INCOMING_FILE = new AudioIO("/INCOMING_FILE.wav");

	public static AudioIO INCOMING_VOICEMAIL = new AudioIO(
			"/INCOMING_VOICEMAIL.wav");

	public static AudioIO KNOCKING = new AudioIO("/KNOCKING.wav");

	public static AudioIO LOGIN = new AudioIO("/LOGIN.wav");

	public static AudioIO LOGOUT = new AudioIO("/LOGOUT.wav");

	public static AudioIO OFFLINE = new AudioIO("/OFFLINE.wav");

	public static AudioIO OLD_BUSY = new AudioIO("/OLD_BUDY.wav");

	public static AudioIO OLD_CALL_ERROR2 = new AudioIO("/OLD_CALL_ERROR2.wav");

	public static AudioIO OLD_CALL_IN = new AudioIO("/OLD_CALL_IN.wav");

	public static AudioIO OLD_CALL_INIT = new AudioIO("/OLD_CALL_INIT.wav");

	public static AudioIO OLD_CALL_OUT = new AudioIO("/OLD_CALL_OUT.wav");

	public static AudioIO OLD_HANGUP = new AudioIO("/OLD_HANGUP.wav");

	public static AudioIO OLD_HOLD = new AudioIO("/OLD_HOLD.wav");

	public static AudioIO OLD_IM = new AudioIO("/OLD_IM.wav");

	public static AudioIO OLD_ONLINE = new AudioIO("/OLD_ONLINE.wav");

	public static AudioIO OLD_RESUME = new AudioIO("/OLD_RESUME.wav");

	public static AudioIO ONLINE = new AudioIO("/ONLINE.wav");

	public static AudioIO RESUME = new AudioIO("/RESUME.wav");

	public static AudioIO SEND_VM = new AudioIO("/SEND_VM.wav");

	public static AudioIO USER_ADDED = new AudioIO("/USER_ADDED.wav");

	public static AudioIO USER_LEFT = new AudioIO("/USER_LEFT.wav");

	public static AudioIO USER_LEFT_ORG = new AudioIO("/USER_LEFT_ORG.wav");

	public static AudioIO VC_BEEP_1 = new AudioIO("/VC_BEEP_1.wav");

	public static AudioIO DTMF_1 = new AudioIO("/dtmf-1.wav");

	public static AudioIO DTMF_2 = new AudioIO("/dtmf-2.wav");

	public static AudioIO DTMF_3 = new AudioIO("/dtmf-3.wav");

	public static AudioIO DTMF_4 = new AudioIO("/dtmf-4.wav");

	public static AudioIO DTMF_5 = new AudioIO("/dtmf-5.wav");

	public static AudioIO DTMF_6 = new AudioIO("/dtmf-6.wav");

	public static AudioIO DTMF_7 = new AudioIO("/dtmf-7.wav");

	public static AudioIO DTMF_8 = new AudioIO("/dtmf-8.wav");

	public static AudioIO DTMF_9 = new AudioIO("/dtmf-9.wav");

	public static AudioIO DTMF_0 = new AudioIO("/dtmf-9.wav");

	public static AudioIO DTMF_POUND = new AudioIO("/dtmf-pound.wav");

	public static AudioIO DTMF_STAR = new AudioIO("/dtmf-star.wav");

	public static AudioIO ECHO123 = new AudioIO("/ECHO123.wav");

	private String resource;

	public AudioIO(String resource) {
		this.resource = resource;
	}

	private InputStream getResourceAsInputStream(String resource) {
		if (resource.startsWith("/")) {
			resource = resource.substring(1);
		}
		try {
			InputStream is = AudioIO.class.getResource("/audio/" + resource)
					.openStream();
			return is;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private Clip playSound(String resource, boolean async) {
		try {
			AudioInputStream stream;
			AudioFormat format;
			DataLine.Info info;
			Clip clip;

			try (BufferedInputStream bis = new BufferedInputStream(
					getResourceAsInputStream(resource))) {
				stream = AudioSystem.getAudioInputStream(bis);
				format = stream.getFormat();
				info = new DataLine.Info(Clip.class, format);
				clip = (Clip) AudioSystem.getLine(info);
				clip.open(stream);
				clip.start();
				if (!async) {
					while (!clip.isRunning())
						Thread.sleep(10);
					while (clip.isRunning())
						Thread.sleep(10);
					clip.close();
				}
				return clip;
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public Clip playSound() {
		return playSound(resource, true);
	}

	public Clip playSoundSync() {
		return playSound(resource, false);
	}
}
