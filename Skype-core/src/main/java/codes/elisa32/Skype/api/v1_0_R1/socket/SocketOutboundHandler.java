package codes.elisa32.Skype.api.v1_0_R1.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Optional;

import codes.elisa32.Skype.api.v1_0_R1.packet.Packet;
import codes.elisa32.Skype.api.v1_0_R1.packet.PacketPlayInReply;

public class SocketOutboundHandler extends SocketHandler {

	@Override
	public void exceptionCaught(SocketHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
	}

	@Override
	public void handlerAdded(SocketHandlerContext ctx) {
	}

	@Override
	public void handlerRemoved(SocketHandlerContext ctx) {
	}

	public void write(SocketHandlerContext ctx, Object msg) {
		Socket socket = ctx.getSocket();
		try {
			socket.getOutputStream().write(msg.toString().getBytes());
			socket.getOutputStream().flush();
		} catch (IOException cause) {
			exceptionCaught(ctx, cause);
		}
	}

	public Optional<PacketPlayInReply> dispatch(SocketHandlerContext ctx,
			Object msg) throws IllegalArgumentException {
		if (ctx.isInboundHandlerAdded()) {
			throw new IllegalArgumentException(SocketInboundHandler.class
					+ " was already added to " + SocketHandlerContext.class);
		}
		if (!ctx.isOutboundHandlerAdded()) {
			throw new IllegalArgumentException(SocketOutboundHandler.class
					+ " was not already added to " + SocketHandlerContext.class);
		}
		try {
			byte[] b = msg.toString().getBytes();
			ctx.getSocket().getOutputStream().write(b, 0, b.length);
			ctx.getSocket().getOutputStream().flush();
		} catch (Exception e) {
			ctx.fireOutboundHandlerInactive();
			return Optional.empty();
		}
		int r;
		byte[] b = new byte[16384];
		StringBuilder sb = new StringBuilder();
		try {
			while ((r = ctx.getSocket().getInputStream().read(b, 0, b.length)) > 0) {
				sb.append(new String(Arrays.copyOf(b, r)));
				if (ctx.getJsonManipulator().validateJsonStrict(sb.toString())) {
					break;
				}
			}
		} catch (SocketTimeoutException e) {
		} catch (IOException e) {
			ctx.fireSocketInactive();
			return Optional.empty();
		}
		if (sb.length() > 0) {
			System.out.println(Optional.of(sb.toString()));
			return Optional.of(Packet.fromJson(sb.toString(),
					PacketPlayInReply.class));
		} else {
			return Optional.empty();
		}
	}

	public void dispatchAsync(SocketHandlerContext ctx, Object msg,
			Runnable callback) throws IllegalArgumentException {
		if (ctx.isInboundHandlerAdded()) {
			throw new IllegalArgumentException(SocketInboundHandler.class
					+ " was already added to " + SocketHandlerContext.class);
		}
		if (!ctx.isOutboundHandlerAdded()) {
			throw new IllegalArgumentException(SocketOutboundHandler.class
					+ " was not already added to " + SocketHandlerContext.class);
		}
		java.lang.Runnable runnable = new java.lang.Runnable() {

			@Override
			public void run() {
				try {
					byte[] b = msg.toString().getBytes();
					ctx.getSocket().getOutputStream().write(b, 0, b.length);
					ctx.getSocket().getOutputStream().flush();
				} catch (Exception e) {
					ctx.fireOutboundHandlerInactive();
					if (callback == null) {
						return;
					}
					callback.replyPacket = Optional.empty();
					callback.run();
					return;
				}
				int r;
				byte[] b = new byte[16384];
				StringBuilder sb = new StringBuilder();
				try {
					while ((r = ctx.getSocket().getInputStream()
							.read(b, 0, b.length)) > 0) {
						sb.append(new String(Arrays.copyOf(b, r)));
						if (ctx.getJsonManipulator().validateJsonStrict(
								sb.toString())) {
							break;
						}
					}
				} catch (SocketTimeoutException e) {
				} catch (IOException e) {
					ctx.fireSocketInactive();
					if (callback == null) {
						return;
					}
					callback.replyPacket = Optional.empty();
					callback.run();
					return;
				}
				if (sb.length() > 0) {
					if (callback == null) {
						return;
					}
					System.out.println(Optional.of(sb.toString()));
					callback.replyPacket = Optional.of(Packet.fromJson(
							sb.toString(), PacketPlayInReply.class));
					callback.run();
					return;
				} else {
					if (callback == null) {
						return;
					}
					callback.replyPacket = Optional.empty();
					callback.run();
					return;
				}
			}

		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	public static abstract class Runnable implements java.lang.Runnable {

		private Optional<PacketPlayInReply> replyPacket;

		public Optional<PacketPlayInReply> getReplyPacket() {
			return replyPacket;
		}

	}

}
