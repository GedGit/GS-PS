package org.rs2server.ls;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

public class PacketDecoder extends FrameDecoder {

	@Override
	protected Object decode(final ChannelHandlerContext context,
			final Channel session, final ChannelBuffer in) throws Exception {
		if (in.readableBytes() > 2) {
			in.markReaderIndex();
			final int packetLength = in.readShort();
			if (in.readableBytes() >= packetLength) {
				final byte[] data = new byte[packetLength];
				in.readBytes(data, 0, data.length);
				return new LoginServerPacket(data);
			} else {
				in.resetReaderIndex();
				return null;
			}
		}
		return null;
	}


}
