package codes.elisa32.Skype.api.v1_0_R1.data.types;

import java.util.ArrayList;
import java.util.List;

import codes.elisa32.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;

import com.google.gson.Gson;

public class Call {

	public volatile UUID callId;

	private List<String> participants = new ArrayList<String>();

	public volatile long creationTime = System.currentTimeMillis();

	public Call(UUID callId) {
		this.callId = callId;
	}

	public Call(String json) {
		Gson gson = GsonBuilder.create();
		Call clazz = gson.fromJson(json, Call.class);
		this.callId = clazz.callId;
		this.participants = clazz.participants;
		this.creationTime = clazz.creationTime;
	}

	public String exportAsJson() {
		Gson gson = GsonBuilder.create();
		return gson.toJson(this);
	}

	@Override
	public String toString() {
		return exportAsJson();
	}

	public UUID getCallId() {
		return callId;
	}

	public void setCallId(UUID callId) {
		this.callId = callId;
	}

	public boolean isParticipant(UUID participantId) {
		for (String participant : this.participants.toArray(new String[0])
				.clone()) {
			if (participant == null) {
				continue;
			}
			if (participant.equals(participantId.toString())) {
				return true;
			}
		}
		return false;
	}

	public void addParticipant(UUID participantId) {
		participants.add(participantId.toString());
	}

	public void removeParticipant(UUID participantId) {
		participants.remove(participantId.toString());
	}

	public List<UUID> getParticipants() {
		List<UUID> participants = new ArrayList<>();
		for (String participant : this.participants.toArray(new String[0])
				.clone()) {
			if (participant == null) {
				continue;
			}
			participants.add(UUID.fromString(participant));
		}
		return participants;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

}
