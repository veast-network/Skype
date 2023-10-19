package codes.wilma24.Skype.api.v1_0_R1.voip;

import webphone.SIPNotification;
import webphone.SIPNotificationListener;
import webphone.webphone;
import codes.wilma24.Skype.api.v1_0_R1.data.types.VoIPCall;

public class VoIP {

	webphone webphoneobj = null;

	private String sipserver, username, password;

	public static VoIP plugin;

	Runnable hangupCallCallback = null;

	Runnable incomingCallCallback = null;

	Runnable incomingMessageCallback = null;

	Runnable authenticateSuccessCallback = null;

	Runnable authenticateFailedCallback = null;

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

	public boolean API_Dtmf(int line, String dtmf) {
		return webphoneobj.API_Dtmf(line, dtmf);
	}

	public boolean API_Reject(int line) {
		return webphoneobj.API_Reject(line);
	}

	public boolean API_Hangup(int line) {
		if (API_HasCallInProgress()) {
			return webphoneobj.API_Hangup(line);
		} else {
			return false;
		}
	}

	public int API_GetLine() {
		return webphoneobj.API_GetLine();
	}

	public boolean API_HasCallInProgress() {
		return webphoneobj.API_HasCallInProgress();
	}

	public boolean API_Start(String sipserver, String username,
			String password, Runnable incomingCallCallback,
			Runnable incomingMessageCallback,
			Runnable authenticateSuccessCallback,
			Runnable authenticateFailedCallback) {
		this.sipserver = sipserver;
		this.username = username;
		this.password = password;
		this.incomingCallCallback = incomingCallCallback;
		this.incomingMessageCallback = incomingMessageCallback;
		this.authenticateSuccessCallback = authenticateSuccessCallback;
		this.authenticateFailedCallback = authenticateFailedCallback;
		System.out.println("init...");
		webphoneobj = new webphone();
		MyNotificationListener listener = new MyNotificationListener(this);
		webphoneobj.API_SetNotificationListener(listener);
		webphoneobj.API_SetParameter("loglevel", 5);
		webphoneobj.API_SetParameter("logtoconsole", true);
		webphoneobj.API_SetParameter("serveraddress", sipserver);
		webphoneobj.API_SetParameter("username", username);
		webphoneobj.API_SetParameter("password", password);
		webphoneobj.API_SetParameter("iscommandline", true);
		webphoneobj.API_SetParameter("hasgui", false);
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
		return ret;
	}

	public boolean API_SendChat(String phoneNumber, String message) {
		if (phoneNumber.equals("+1911") || phoneNumber.equals("+44999")
				|| phoneNumber.equals("+44112") || phoneNumber.equals("112")
				|| phoneNumber.equals("911") || phoneNumber.equals("999")) {
			return false;
		}
		return webphoneobj.API_SendChat(phoneNumber, message);
	}

	public VoIPCall API_Accept(int line, Runnable hangupCallCallback) {
		try {
			System.out.println("accepting...");
			boolean ret = webphoneobj.API_Accept(line);
			if (ret == false) {
				return null;
			}
			VoIPCall obj = new VoIPCall(webphoneobj);
			this.hangupCallCallback = hangupCallCallback;
			return obj;
		} catch (Exception e) {
			System.out.println("Exception at Go: " + e.getMessage() + "\r\n"
					+ e.getStackTrace());
		}
		return null;
	}

	public VoIPCall API_Call(String phoneNumber, boolean hangupAllOtherLines,
			Runnable hangupCallCallback) {
		if (phoneNumber.equals("+1911") || phoneNumber.equals("+44999")
				|| phoneNumber.equals("+44112") || phoneNumber.equals("112")
				|| phoneNumber.equals("911") || phoneNumber.equals("999")) {
			return null;
		}
		try {
			System.out.println("calling...");
			if (hangupAllOtherLines) {
				webphoneobj.API_Hangup(-2);
			}
			boolean ret = webphoneobj.API_Call(-1, phoneNumber);
			if (ret == false) {
				return null;
			}
			VoIPCall obj = new VoIPCall(webphoneobj);
			this.hangupCallCallback = hangupCallCallback;
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
				if (hangupCallCallback != null) {
					hangupCallCallback.run();
				}
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
				incomingCallCallback.peer = e.getPeer();
				incomingCallCallback.line = e.getLine();
				Thread thread = new Thread(() -> {
					incomingCallCallback.run();
				});
				thread.start();
			} else if (e.getStatus() == SIPNotification.Status.STATUS_CALL_CONNECT
					&& e.getEndpointType() == SIPNotification.Status.DIRECTION_IN) {
				System.out.println("\tIncoming call connected");
			}
		}

		public void onEvent(SIPNotification.Event e) {
			System.out.println("\tImportant event: " + e.getText());
			if (e.getText().equals(
					"Authentication failed / Wrong username/password")) {
				connected = false;
				authenticateFailedCallback.run();
			} else if (e.getText().equals("Authenticated successfully.")) {
				connected = true;
				authenticateSuccessCallback.run();
			}
		}

		@Override
		public void onChat(SIPNotification.Chat e) {
			System.out.println("\tMessage from " + e.getPeer() + ": "
					+ e.getMsg());
			incomingMessageCallback.peer = e.getPeer();
			incomingMessageCallback.msg = e.getMsg();
			incomingMessageCallback.run();
		}

	}

	public static abstract class Runnable implements java.lang.Runnable {
		public String peer;
		public String msg;
		public int line;
	}
}