package codes.elisa32.Skype.api.v1_0_R1.socket;

import java.net.Socket;
import java.net.SocketException;

import codes.elisa32.Skype.api.v1_0_R1.cryptography.CryptographicContext;
import codes.elisa32.Skype.api.v1_0_R1.cryptography.SimpleCryptographicContext;
import codes.elisa32.Skype.api.v1_0_R1.json.JsonManipulator;
import codes.elisa32.Skype.api.v1_0_R1.json.JsonManipulatorCurrent;

public class SocketHandlerContext {

	private Socket socket;

	private SocketInboundHandler inboundHandler;

	private boolean inboundHandlerAdded = false;

	private SocketOutboundHandler outboundHandler;

	private boolean outboundHandlerAdded = false;

	private CryptographicContext cryptographicContext;

	private JsonManipulator jsonManipulator;

	public SocketHandlerContext(Socket socket) {
		this.setSocket(socket);

		try {
			/**
			 * If a read operation blocks for more then 2sec, throw err
			 */
			socket.setSoTimeout(2000);
		} catch (SocketException e) {
			e.printStackTrace();
		}

		this.inboundHandler = new SocketInboundHandler();
		this.outboundHandler = new SocketOutboundHandler();
		this.cryptographicContext = new SimpleCryptographicContext();
		this.jsonManipulator = new JsonManipulatorCurrent();

		this.fireOutboundHandlerActive();
	}

	public Socket getSocket() {
		return socket;
	}

	private void setSocket(Socket socket) {
		this.socket = socket;
	}

	public SocketInboundHandler getInboundHandler() {
		return inboundHandler;
	}

	public boolean isInboundHandlerAdded() {
		return inboundHandlerAdded;
	}

	public SocketOutboundHandler getOutboundHandler() {
		return outboundHandler;
	}

	public boolean isOutboundHandlerAdded() {
		return outboundHandlerAdded;
	}

	public CryptographicContext getCryptographicContext() {
		return cryptographicContext;
	}

	public JsonManipulator getJsonManipulator() {
		return jsonManipulator;
	}

	public SocketHandlerContext fireInboundHandlerActive() {
		SocketHandlerContext ctx = this;
		if (!inboundHandlerAdded) {
			inboundHandler.handlerAdded(ctx);
			inboundHandlerAdded = true;
		}
		return ctx;
	}

	@Deprecated
	public SocketHandlerContext fireOutboundHandlerActive() {
		SocketHandlerContext ctx = this;
		if (!outboundHandlerAdded) {
			outboundHandler.handlerAdded(ctx);
			outboundHandlerAdded = true;
		}
		return ctx;
	}

	@Deprecated
	public SocketHandlerContext fireSocketActive() {
		SocketHandlerContext ctx = this;
		if (!outboundHandlerAdded) {
			outboundHandler.handlerAdded(ctx);
			outboundHandlerAdded = true;
		}
		if (!inboundHandlerAdded) {
			inboundHandler.handlerAdded(ctx);
			inboundHandlerAdded = true;
		}
		return ctx;
	}

	public SocketHandlerContext fireInboundHandlerInactive() {
		SocketHandlerContext ctx = this;
		if (inboundHandlerAdded) {
			inboundHandler.handlerRemoved(ctx);
			inboundHandlerAdded = false;
		}
		return ctx;
	}

	@Deprecated
	public SocketHandlerContext fireOutboundHandlerInactive() {
		SocketHandlerContext ctx = this;
		if (outboundHandlerAdded) {
			outboundHandler.handlerRemoved(ctx);
			outboundHandlerAdded = false;
		}
		return ctx;
	}

	@Deprecated
	public SocketHandlerContext fireSocketInactive() {
		SocketHandlerContext ctx = this;
		if (outboundHandlerAdded) {
			outboundHandler.handlerRemoved(ctx);
			outboundHandlerAdded = false;
		}
		if (inboundHandlerAdded) {
			inboundHandler.handlerRemoved(ctx);
			inboundHandlerAdded = false;
		}
		return ctx;
	}

	@Deprecated
	public void start() {
		fireSocketActive();
	}

	@Deprecated
	public void stop() {
		fireSocketInactive();
	}

}
