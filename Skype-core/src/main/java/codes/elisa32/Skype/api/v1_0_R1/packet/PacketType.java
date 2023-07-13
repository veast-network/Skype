package codes.elisa32.Skype.api.v1_0_R1.packet;

public enum PacketType {

	/**
	 * PacketPlayOut
	 */
	REFRESH_TOKEN,

	REGISTER,

	/*
	 * You can either login with your skype name and password or you can login
	 * with a non expired auth code, either way you will be given a new auth
	 * code to communicate to the server with.
	 * 
	 * You will need to use a new auth code for every call and every call data
	 * stream you take in. If you use the same auth code for everything, then
	 * you will end up breaking the program to a point that it will not work.
	 */
	LOGIN,

	UPDATE_USER,

	ENTERING_LISTEN_MODE,

	LOOKUP_USER,
	
	LOOKUP_ONLINE_STATUS,

	USER_SEARCH,

	MESSAGE_OUT,

	REMOVE_MESSAGE_OUT,

	LOOKUP_CONVERSATION_HISTORY,

	LOOKUP_CONVERSATION_PARTICIPANTS,

	LOOKUP_MESSAGE_HISTORY,

	LOOKUP_CONTACTS,

	SEND_CONTACT_REQUEST,

	ACCEPT_CONTACT_REQUEST_OUT,

	DECLINE_CONTACT_REQUEST,

	SEND_CALL_REQUEST,

	/*
	 * Accepting the incoming phone call request using this packet will delegate
	 * that SocketHandlerContext as a call data stream and should not be used
	 * for anything other then streaming audio from the client to the server.
	 * 
	 * It is very important that you use a different auth code for every call
	 * you accept using this packet, otherwise the calling will not work
	 * properly and disrupt the rest of the application.
	 * 
	 * As the client is streaming audio to the server, the server will then
	 * route that data stream to all the other call participants that accepted
	 * the call request previously.
	 * 
	 * We will keep track of who is in the call by having a Call object store
	 * all the participants in the call, and as each participant declines or
	 * leaves the call they will be removed from the list of participants.
	 */
	ACCEPT_CALL_REQUEST_OUT,

	DECLINE_CALL_REQUEST_OUT,

	/*
	 * Accepting the incoming phone call data stream request using this packet
	 * will delegate that SocketHandlerContext as a call data stream for
	 * receiving an audio stream from another participant in an ongoing phone
	 * call and should not be used for anything else.
	 * 
	 * It is very important that you use a different auth code for every call
	 * data stream you accept using this packet, otherwise the data stream will
	 * not work properly and disrupt the rest of the application.
	 */
	ACCEPT_CALL_DATA_STREAM_REQUEST_OUT,

	/**
	 * PacketPlayIn
	 */
	REPLY,

	MESSAGE_IN,

	REMOVE_MESSAGE_IN,

	ACCEPT_CONTACT_REQUEST_IN,

	CALL_REQUEST_IN,

	ACCEPT_CALL_REQUEST_IN,

	DECLINE_CALL_REQUEST_IN,

	/*
	 * This is a packet indicating that there is an incoming call data stream
	 * request from another participant in an ongoing phone call.
	 * 
	 * The client should respond to this packet with the equivalent
	 * ACCEPT_CALL_DATA_STREAM_REQUEST_OUT packet indicating that they are ready
	 * to receive the audio stream from said participant in the phone call. The
	 * client should use a new auth code when doing this.
	 */
	CALL_DATA_STREAM_REQUEST_IN,
}
