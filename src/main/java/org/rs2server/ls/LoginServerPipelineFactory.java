package org.rs2server.ls;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.util.Timer;
public class LoginServerPipelineFactory implements ChannelPipelineFactory {
	
	@SuppressWarnings("unused")
	private final Timer timer;

	public LoginServerPipelineFactory(final Timer timer) {
		this.timer = timer;
	}

	@Override
	public ChannelPipeline getPipeline() throws Exception {
		final ChannelPipeline pipeline = Channels.pipeline();
		pipeline.addLast("encoder", new PacketEncoder());
		pipeline.addLast("decoder", new PacketDecoder());
		// pipeline.addLast("timeout", new IdleStateHandler(timer, 60, 0, 0));
		pipeline.addLast("handler", ChannelHandler.getInstance());
		return pipeline;
	}

}
