package org.rs2server.ls;

import java.util.logging.Logger;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.handler.timeout.IdleStateAwareChannelUpstreamHandler;
import org.rs2server.Server;
import org.rs2server.rs2.Constants;


public class ChannelHandler extends IdleStateAwareChannelUpstreamHandler {

	private static Logger logger = Logger.getLogger(ChannelHandler.class.getName());

	private static final ChannelHandler INSTANCE = new ChannelHandler();

	public static ChannelHandler getInstance() {
		return INSTANCE;
	}

	@Override
	public void exceptionCaught(final ChannelHandlerContext context, final ExceptionEvent event) {
		context.getChannel().close();
	}

	@Override
	public void channelConnected(final ChannelHandlerContext context, final ChannelStateEvent event) throws Exception {
		final Channel channel = context.getChannel();
		System.out.println("Login Server Channel Connected");
		System.out.println("World: "+ Constants.WORLD_ID);
		channel.write(new LoginServerPacket(1).writeByte(Constants.WORLD_ID).writeString("password"));
	}

	@Override
	public void channelClosed(final ChannelHandlerContext context, final ChannelStateEvent event) throws Exception {
		@SuppressWarnings("unused")
		final Channel channel = context.getChannel();
	}

	@Override
	public void messageReceived(final ChannelHandlerContext context, final MessageEvent event) throws Exception {
		final Channel channel = context.getChannel();
		if (event.getMessage() instanceof LoginServerPacket) {
			final LoginServerPacket packet = (LoginServerPacket) event.getMessage();

			System.err.println("Received opcode: "+ packet.toString()); 
			final int opcode = packet.readByte();
			if (opcode == 1) {
				final int code = packet.readByte();
				if (code == 2) {
					Server.getLoginServer().setChannel(channel);
				} else {
					logger.severe("Error connecting to login server [code=" + code + "].");
				}
			} else {
				Server.getLoginServer().read(opcode, packet);
			}
		}
	}

}
