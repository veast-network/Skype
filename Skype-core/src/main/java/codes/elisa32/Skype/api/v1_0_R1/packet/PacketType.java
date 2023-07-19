package codes.elisa32.Skype.api.v1_0_R1.packet;

public enum PacketType {

	/**
	 * PacketPlayOut
	 */
	REFRESH_TOKEN,

	REGISTER,

	LOGIN,

	UPDATE_USER,

	UPDATE_GROUP_CHAT_PARTICIPANTS,

	ENTERING_LISTEN_MODE,

	LOOKUP_USER,

	LOOKUP_ONLINE_STATUS,

	LOOKUP_USER_REGISTRY,

	MESSAGE_OUT,

	REMOVE_MESSAGE_OUT,

	LOOKUP_CONVERSATION_HISTORY,

	LOOKUP_CONVERSATION_PARTICIPANTS,

	LOOKUP_GROUP_CHAT_ADMINS,

	LOOKUP_MESSAGE_HISTORY,

	LOOKUP_CONTACTS,

	SEND_CONTACT_REQUEST,

	ACCEPT_CONTACT_REQUEST,

	DECLINE_CONTACT_REQUEST,

	SEND_FILE_TRANSFER_REQUEST,

	SEND_CALL_REQUEST,

	ACCEPT_CALL_REQUEST,

	ACCEPT_FILE_TRANSFER_REQUEST,

	DECLINE_CALL_REQUEST,

	DECLINE_FILE_TRANSFER_REQUEST,

	ACCEPT_CALL_DATA_STREAM_REQUEST,

	ACCEPT_FILE_DATA_STREAM_REQUEST,
	
	FINISHED_READING_FILE_TRANSFER_DATA,

	/**
	 * PacketPlayIn
	 */
	REPLY,

	MESSAGE_IN,

	REMOVE_MESSAGE_IN,

	ACCEPT_CONTACT_REQUEST_IN,

	CALL_REQUEST_IN,

	FILE_TRANSFER_REQUEST_IN,

	ACCEPT_CALL_REQUEST_IN,

	ACCEPT_FILE_TRANSFER_REQUEST_IN,

	DECLINE_CALL_REQUEST_IN,

	DECLINE_FILE_TRANSFER_REQUEST_IN,

	CALL_DATA_STREAM_REQUEST_IN,

	FILE_DATA_STREAM_REQUEST_IN,

	ACCEPT_FILE_DATA_STREAM_REQUEST_IN,

	USER_REGISTRY_CHANGED_IN,

	GROUP_CHAT_PARTICIPANTS_CHANGED_IN,

	CALL_PARTICIPANTS_CHANGED_IN,

	FILE_TRANSFER_PARTICIPANTS_CHANGED_IN,

	UPDATE_USER_IN
}
