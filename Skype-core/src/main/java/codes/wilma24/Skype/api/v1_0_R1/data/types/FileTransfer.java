package codes.wilma24.Skype.api.v1_0_R1.data.types;

import java.util.ArrayList;
import java.util.List;

import codes.wilma24.Skype.api.v1_0_R1.gson.GsonBuilder;
import codes.wilma24.Skype.api.v1_0_R1.uuid.UUID;

import com.google.gson.Gson;

public class FileTransfer {

	public volatile UUID fileTransferId;

	private List<String> participants = new ArrayList<String>();

	public volatile long creationTime = System.currentTimeMillis();

	private long length;

	public FileTransfer(UUID fileTransferId) {
		this.fileTransferId = fileTransferId;
	}

	public FileTransfer(String json) {
		Gson gson = GsonBuilder.create();
		FileTransfer clazz = gson.fromJson(json, FileTransfer.class);
		this.fileTransferId = clazz.fileTransferId;
		this.participants = clazz.participants;
		this.creationTime = clazz.creationTime;
		this.length = clazz.length;
	}

	public String exportAsJson() {
		Gson gson = GsonBuilder.create();
		return gson.toJson(this);
	}

	@Override
	public String toString() {
		return exportAsJson();
	}

	public UUID getFileTransferId() {
		return fileTransferId;
	}

	public void setFileTransferId(UUID fileTransferId) {
		this.fileTransferId = fileTransferId;
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

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

}
