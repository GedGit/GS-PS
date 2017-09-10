package org.rs2server.ls;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class LoginServerPacket {
	
	private byte[] inBuffer;
	private ChannelBuffer outBuffer;
	private int caret;

	public LoginServerPacket(final int opcode) {
		outBuffer = ChannelBuffers.dynamicBuffer();
		writeByte(opcode);
	}

	public LoginServerPacket(final byte[] inBuffer) {
		this.inBuffer = inBuffer;
	}

	public LoginServerPacket writeByte(final int i) {
		outBuffer.writeByte(i);
		return this;
	}

	public int readByte() {
		return inBuffer[caret++] & 0xff;
	}

	public LoginServerPacket writeShort(final int i) {
		outBuffer.writeShort(i);
		return this;
	}

	public int readShort() {
		return readByte() << 8 | readByte();
	}

	public LoginServerPacket writeInt(final int i) {
		outBuffer.writeInt(i);
		return this;
	}

	public int readInt() {
		return readShort() << 16 | readShort();
	}

	public LoginServerPacket writeLong(final long l) {
		outBuffer.writeLong(l);
		return this;
	}

	public long getLong() {
		long value = 0;
		value |= (long) readByte() << 56L;
		value |= (long) readByte() << 48L;
		value |= (long) readByte() << 40L;
		value |= (long) readByte() << 32L;
		value |= (long) readByte() << 24L;
		value |= (long) readByte() << 16L;
		value |= (long) readByte() << 8L;
		value |= readByte();
		return value;
	}

	public LoginServerPacket writeString(final String s) {
		for (final byte b : s.getBytes()) {
			writeByte(b);
		}
		writeByte(10);
		return this;
	}

	public String readString() {
		int c;
		final StringBuilder builder = new StringBuilder();
		while ((c = readByte()) != 10) {
			builder.append((char) c);
		}
		return builder.toString();
	}

	public int getLength() {
		return outBuffer.writerIndex();
	}

	public ChannelBuffer getOutBuffer() {
		return outBuffer;
	}

}
