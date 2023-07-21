package codes.elisa32.Skype.api.v1_0_R1.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.SwingUtilities;

import codes.elisa32.Skype.api.v1_0_R1.command.CommandMap;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;

public class SocketInboundHandler extends SocketHandler {

	protected Thread thread;
	
	protected Runnable callback;

	@Override
	public void exceptionCaught(SocketHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}

	@Override
	public void handlerAdded(SocketHandlerContext ctx, Runnable callback) {
		if (thread != null) {
			/**
			 * Handler was added previously, we do not need to add it again
			 */
			return;
		}
		this.callback = callback;
		thread = new Thread(
				() -> {
					Socket socket = ctx.getSocket();
					while (true) {
						int r;
						byte[] b = new byte[32768];
						StringBuilder sb = new StringBuilder();
						List<String> validJsonStrings = new ArrayList<String>();
						try {
							while ((r = socket.getInputStream().read(b, 0,
									b.length)) > 0) {
								sb.append(new String(Arrays.copyOf(b, r), StandardCharsets.UTF_8));
								if (ctx.getJsonManipulator()
										.validateJsonStrict(sb.toString())) {
									/**
									 * String buffer is validated as being a
									 * json string
									 * 
									 * Break read loop so we do not have to wait
									 * for timeout
									 */
									validJsonStrings.add(sb.toString());
									break;
								} else {
									while (sb.length() > 0) {
										boolean hit = false;
										for (int i = 0; i < sb.length(); i++) {
											String split = sb.substring(0,
													i + 1);
											if (ctx.getJsonManipulator()
													.validateJsonStrict(
															split.toString())) {
												validJsonStrings.add(split);
												sb = new StringBuilder(sb
														.substring(i + 1));
												hit = true;
												break;
											}
										}
										if (hit == false) {
											break;
										}
									}
								}
							}
						} catch (SocketTimeoutException e) {
							continue;
						} catch (IOException e) {
							/**
							 * Socket is no longer active, remove inbound
							 * handler
							 */
							break;
						}
						if (sb.length() == 0) {
							/**
							 * Socket is no longer active, remove inbound
							 * handler
							 */
							break;
						}
						for (String json : validJsonStrings) {
							SwingUtilities.invokeLater(() -> {
								if (ctx.getJsonManipulator()
										.validateJsonStrict(json)) {
									/**
									 * String buffer is validated as being a
									 * json string
									 * 
									 * We will now call our socketRead(ctx, msg)
									 * method
									 */
									this.socketRead(ctx, json);
								} else {
									/**
									 * Socket did not provide a valid json
									 * string before timeout
									 */
								}
							});
						}
					}
					this.handlerRemoved(ctx);
				});
		thread.start();
	}

	@Override
	public void handlerRemoved(SocketHandlerContext ctx) {
		thread.stop();
		if (callback != null) {
			callback.run();
		}
	}

	private void socketRead(SocketHandlerContext ctx, Object msg) {
		System.out.println(Optional.of(msg.toString()));
		PacketPlayInReply replyPacket = CommandMap.dispatch(ctx, msg);
		if (replyPacket == null) {
			return;
		}
		ctx.getOutboundHandler().write(ctx, replyPacket);
	}
}
