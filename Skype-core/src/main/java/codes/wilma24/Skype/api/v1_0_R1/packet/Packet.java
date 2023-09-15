package codes.wilma24.Skype.api.v1_0_R1.packet;

import codes.wilma24.Skype.api.v1_0_R1.gson.GsonBuilder;

import com.google.gson.Gson;

public class Packet {

	public volatile PacketType type;

	public Packet(PacketType type) {
		this.type = type;
	}

	public static <T> T fromJson(String json, Class<T> clazz) {
		Gson gson = GsonBuilder.create();
		return gson.fromJson(json, clazz);
	}

	public String toJson() {
		Gson gson = GsonBuilder.create();
		return gson.toJson(this);
	}

	@Override
	public String toString() {
		return toJson();
	}

	public PacketType getType() {
		return type;
	}

}
