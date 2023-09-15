package codes.wilma24.Skype.api.v1_0_R1.socket;

public abstract class SocketHandler {

	public abstract void exceptionCaught(SocketHandlerContext ctx, Throwable cause);
	
	public abstract void handlerAdded(SocketHandlerContext ctx, Runnable callback);
	
	public abstract void handlerRemoved(SocketHandlerContext ctx);
	
}
