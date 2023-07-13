package codes.elisa32.Skype.server.v1_0_R1.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

import codes.elisa32.Skype.api.v1_0_R1.data.types.Call;
import codes.elisa32.Skype.api.v1_0_R1.socket.SocketHandlerContext;
import codes.elisa32.Skype.api.v1_0_R1.uuid.UUID;
import codes.elisa32.Skype.server.v1_0_R1.Skype;
import codes.elisa32.Skype.server.v1_0_R1.data.types.Connection;

public class CallingInboundHandler implements Runnable {

	private UUID loggedInUser;
	private Call call;
	private SocketHandlerContext ctx;
	private Connection con;
	private Socket socket;

	public CallingInboundHandler(Call call, UUID participantId,
			SocketHandlerContext ctx, Connection con) {
		this.call = call;
		this.loggedInUser = participantId;
		this.ctx = ctx;
		this.con = con;
		this.socket = ctx.getSocket();
	}

	@Override
	public void run() {
		try {
			socket.setSoTimeout(0);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {
				byte[] b = new byte[1024];
				int len = socket.getInputStream().read(b, 0, b.length);
				if (len == -1) {
					break;
				}
				try {
					socket.setSoTimeout(8000);
				} catch (SocketException e1) {
					e1.printStackTrace();
				}
				for (UUID callParticipant : call.getParticipants()
						.toArray(new UUID[0]).clone()) {
					if (callParticipant.equals(loggedInUser)) {
						continue;
					}
					for (Connection con : Skype
							.getPlugin()
							.getUserManager()
							.getDataStreamConnectionsInCall(callParticipant,
									call.getCallId())
							.toArray(new Connection[0]).clone()) {
						if (!con.getCallId().isPresent()) {
							continue;
						}
						if (!con.getParticipantId().isPresent()) {
							continue;
						}
						UUID receivingCallId = con.getCallId().get();
						UUID receivingCallDataStreamParticipantId = con
								.getParticipantId().get();
						if (receivingCallId.equals(call.getCallId())) {
							if (receivingCallDataStreamParticipantId
									.equals(this.loggedInUser)) {
								try {
									b = Arrays.copyOf(b, len);
									con.getSocketHandlerContext().getSocket()
											.getOutputStream().write(b, 0, len);
									con.getSocketHandlerContext().getSocket()
											.getOutputStream().flush();
								} catch (Exception e) {
									e.printStackTrace();
									call.removeParticipant(callParticipant);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (UUID callParticipant : call.getParticipants().toArray(new UUID[0])
				.clone()) {
			if (callParticipant.equals(loggedInUser)) {
				continue;
			}
			for (Connection con : Skype
					.getPlugin()
					.getUserManager()
					.getDataStreamConnectionsInCall(callParticipant,
							call.getCallId()).toArray(new Connection[0])
					.clone()) {
				if (!con.getCallId().isPresent()) {
					continue;
				}
				if (!con.getParticipantId().isPresent()) {
					continue;
				}
				UUID receivingCallId = con.getCallId().get();
				UUID receivingCallDataStreamParticipantId = con
						.getParticipantId().get();
				if (receivingCallId.equals(call.getCallId())) {
					if (receivingCallDataStreamParticipantId
							.equals(this.loggedInUser)) {
						try {
							con.getSocketHandlerContext().getSocket().close();
							call.removeParticipant(callParticipant);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		call.removeParticipant(loggedInUser);
		Skype.getPlugin().getConnectionMap().remove(con.getAuthCode(), con);
	}

}
