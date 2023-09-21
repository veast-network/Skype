package codes.wilma24.Skype.api.v1_0_R1.voip;

import webphone.SIPNotification;
import webphone.SIPNotificationListener;
import webphone.webphone;
import codes.wilma24.Skype.api.v1_0_R1.data.types.VoIPCall;

public class VoIP {

	webphone webphoneobj = null;

	private String sipserver, username, password;

	public static VoIP plugin;

	Runnable callback = null;

	boolean connected = false;

	static {
		plugin = new VoIP();
	}

	public static VoIP getPlugin() {
		return plugin;
	}

	public boolean isConnected() {
		return connected;
	}

	public boolean API_Start(String sipserver, String username, String password) {
		this.sipserver = sipserver;
		this.username = username;
		this.password = password;
		System.out.println("init...");
		webphoneobj = new webphone();
		MyNotificationListener listener = new MyNotificationListener(this);
		webphoneobj.API_SetNotificationListener(listener);
		webphoneobj.API_SetParameter("loglevel", 1);
		webphoneobj.API_SetParameter("logtoconsole", true);
		webphoneobj.API_SetParameter("serveraddress", sipserver);
		webphoneobj.API_SetParameter("username", username);
		webphoneobj.API_SetParameter("password", password);
		System.out.println("start...");
		boolean ret = webphoneobj.API_Start();
		if (ret) {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			webphoneobj.API_Hangup(-2);
		}
		connected = ret;
		return ret;
	}

	public boolean API_SendSMS(String phoneNumber, String message) {
		if (phoneNumber.equals("+1911") || phoneNumber.equals("+44999")
				|| phoneNumber.equals("+44112") || phoneNumber.equals("112")
				|| phoneNumber.equals("911") || phoneNumber.equals("999")) {
			return false;
		}
		return webphoneobj.API_SendSMS(phoneNumber, message);
	}

	public VoIPCall API_Call(String phoneNumber, Runnable callback) {
		if (phoneNumber.equals("+1911") || phoneNumber.equals("+44999")
				|| phoneNumber.equals("+44112") || phoneNumber.equals("112")
				|| phoneNumber.equals("911") || phoneNumber.equals("999")) {
			return null;
		}
		try {
			System.out.println("calling...");
			webphoneobj.API_Hangup(-2);
			boolean ret = webphoneobj.API_Call(-1, phoneNumber);
			if (ret == false) {
				return null;
			}
			VoIPCall obj = new VoIPCall(webphoneobj);
			this.callback = callback;
			return obj;
		} catch (Exception e) {
			System.out.println("Exception at Go: " + e.getMessage() + "\r\n"
					+ e.getStackTrace());
		}
		return null;
	}

	class MyNotificationListener extends SIPNotificationListener {
		VoIP app = null;

		public MyNotificationListener(VoIP app_in) {
			app = app_in;
		}

		public void onAll(SIPNotification e) {
			if (e.toString().contains("CallDisconnect")
					|| e.toString().contains("Call Finished")) {
				callback.run();
			}
			System.out.println("\t\t\t" + e.getNotificationTypeText()
					+ " notification received: " + e.toString());
		}

		public void onRegister(SIPNotification.Register e) {
			if (!e.getIsMain())
				return;
			switch (e.getStatus()) {
			case SIPNotification.Register.STATUS_INPROGRESS:
				System.out.println("\tRegistering...");
				break;
			case SIPNotification.Register.STATUS_SUCCESS:
				System.out.println("\tRegistered successfully.");
				break;
			case SIPNotification.Register.STATUS_FAILED:
				System.out
						.println("\tRegister failed because " + e.getReason());
				break;
			case SIPNotification.Register.STATUS_UNREGISTERED:
				System.out.println("\tUnregistered.");
				break;
			}
		}

		public void onStatus(SIPNotification.Status e) {
			if (e.getLine() == -1)
				return;
			if (e.getStatus() >= SIPNotification.Status.STATUS_CALL_SETUP
					&& e.getStatus() <= SIPNotification.Status.STATUS_CALL_FINISHED) {
				System.out.println("\tCall state is: " + e.getStatusText());
			}
			if (e.getStatus() == SIPNotification.Status.STATUS_CALL_CONNECT
					&& e.getEndpointType() == SIPNotification.Status.DIRECTION_OUT) {
				System.out.println("\tOutgoing call connected to "
						+ e.getPeer());
			} else if (e.getStatus() == SIPNotification.Status.STATUS_CALL_RINGING
					&& e.getEndpointType() == SIPNotification.Status.DIRECTION_IN) {
				System.out.println("\tIncoming call from "
						+ e.getPeerDisplayname());
				app.webphoneobj.API_Accept(e.getLine());
			} else if (e.getStatus() == SIPNotification.Status.STATUS_CALL_CONNECT
					&& e.getEndpointType() == SIPNotification.Status.DIRECTION_IN) {
				System.out.println("\tIncoming call connected");
			}
		}

		public void onEvent(SIPNotification.Event e) {
			System.out.println("\tImportant event: " + e.getText());
		}

		public void onChat(SIPNotification.Chat e) {
			System.out.println("\tMessage from " + e.getPeer() + ": "
					+ e.getMsg());
			app.webphoneobj.API_SendChat(e.getPeer(), "Received");
		}

	}
}