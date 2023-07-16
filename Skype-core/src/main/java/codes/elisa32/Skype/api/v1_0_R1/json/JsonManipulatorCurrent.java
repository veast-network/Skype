package codes.elisa32.Skype.api.v1_0_R1.json;

import java.io.IOException;

import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class JsonManipulatorCurrent extends JsonManipulator {

	@Override
	public boolean validateJsonStrict(String input) {
		try {
			new Gson().getAdapter(JsonElement.class).fromJson(input);
			Packet.fromJson(input, Packet.class).getType();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
