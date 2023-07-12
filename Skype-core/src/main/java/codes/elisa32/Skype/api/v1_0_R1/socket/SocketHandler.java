package codes.elisa32.Skype.api.v1_0_R1.socket;

public abstract class SocketHandler {

	public abstract void exceptionCaught(SocketHandlerContext ctx, Throwable cause);
	
	public abstract void handlerAdded(SocketHandlerContext ctx);
	
	public abstract void handlerRemoved(SocketHandlerContext ctx);
	
}
