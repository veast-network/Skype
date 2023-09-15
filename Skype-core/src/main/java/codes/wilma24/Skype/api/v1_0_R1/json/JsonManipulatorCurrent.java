package codes.wilma24.Skype.api.v1_0_R1.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class JsonManipulatorCurrent extends JsonManipulator {

	@Override
	public boolean validateJsonStrict(String input) {
		try {
			new Gson().getAdapter(JsonElement.class).fromJson(input);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
