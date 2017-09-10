package org.rs2server.ls;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

public class PacketEncoder extends OneToOneEncoder {

	@Override
	protected Object encode(final ChannelHandlerContext arg0,
			final Channel session, final Object message) throws Exception {
		final LoginServerPacket packet = (LoginServerPacket) message;
		final ChannelBuffer buffer = ChannelBuffers
				.buffer(packet.getLength() + 2);
		buffer.writeShort(packet.getLength());
		buffer.writeBytes(packet.getOutBuffer());
		return buffer;
	}

}
