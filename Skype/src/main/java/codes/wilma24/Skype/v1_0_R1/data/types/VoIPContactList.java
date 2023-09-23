package codes.wilma24.Skype.v1_0_R1.data.types;

import java.util.ArrayList;
import java.util.List;

import codes.wilma24.Skype.api.v1_0_R1.gson.GsonBuilder;

import com.google.gson.Gson;

public class VoIPContactList {

	private volatile List<VoIPContact> contacts = new ArrayList<>();
	
	public VoIPContactList() {
		
	}

	public VoIPContactList(String json) {
		readFromJson(json);
	}

	public void readFromJson(String json) {
		Gson gson = GsonBuilder.create();
		VoIPContactList clazz = gson.fromJson(json, VoIPContactList.class);
		this.contacts = clazz.contacts;
	}

	@Override
	public String toString() {
		return exportAsJson();
	}

	public String exportAsJson() {
		Gson gson = GsonBuilder.create();
		return gson.toJson(this);
	}

	public List<VoIPContact> getContacts() {
		return contacts;
	}

}
