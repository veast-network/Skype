package codes.wilma24.Skype.api.v1_0_R1.data.types;

import webphone.webphone;

public class VoIPCall {

	private webphone webphoneobj;

	public VoIPCall(webphone webphoneobj) {
		this.webphoneobj = webphoneobj;
	}

	public boolean mute(boolean val) {
		return webphoneobj.API_Mute(-1, val);
	}

}
