package org.rs2server.rs2.net;

import org.apache.mina.core.buffer.IoBuffer;
import org.rs2server.rs2.net.Packet.Type;


/**
 * A utility class for building packets.
 * @author Graham Edgecombe
 *
 */
public class PacketBuilder {

	/**
	 * Bit mask array.
	 */
	public static final int[] BIT_MASK_OUT = new int[32];

	/**
	 * Creates the bit mask array.
	 */
	static {
		for(int i = 0; i < BIT_MASK_OUT.length; i++) {
			BIT_MASK_OUT[i] = (1 << i) - 1;
		}
	}

	/**
	 * The opcode.
	 */
	private int opcode;

	/**
	 * The type.
	 */
	private Type type;

	/**
	 * The payload.
	 */
	private IoBuffer payload = IoBuffer.allocate(16);

	/**
	 * The current bit position.
	 */
	private int bitPosition;

	/**
	 * Creates a raw packet builder.
	 */
	public PacketBuilder() {
		this(-1);
	}

	/**
	 * Creates a fixed packet builder with the specified opcode.
	 * @param opcode The opcode.
	 */
	public PacketBuilder(int opcode) {
		this(opcode, Type.FIXED);
	}

	/**
	 * Creates a packet builder with the specified opcode and type.
	 * @param opcode The opcode.
	 * @param type The type.
	 */
	public PacketBuilder(int opcode, Type type) {
		this.opcode = opcode;
		this.type = type;
		payload.setAutoExpand(true);
		payload.setAutoShrink(true);
	}

	/**
	 * Writes a byte.
	 * @param b The byte to write.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder put(byte b) {
		payload.put(b);
		return this;
	}

	/**
	 * Writes an array of bytes.
	 * @param b The byte array.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder put(byte[] b) {
		payload.put(b);
		return this;
	}

	/**
	 * Writes a short.
	 * @param s The short.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putShort(int s) {
		payload.putShort((short) s);
		return this;
	}

	/**
	 * Writes an integer.
	 * @param i The integer.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putInt(int i) {
		payload.putInt(i);
		return this;
	}

	/**
	 * Writes a long.
	 * @param l The long.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putLong(long l) {
		payload.putLong(l);
		return this;
	}

	/**
	 * Converts this PacketBuilder to a packet.
	 * @return The Packet object.
	 */
	public Packet toPacket() {
		return new Packet(opcode, type, payload.flip());
	}

	public Packet toPacket1() {
		return new Packet(opcode, type, payload.flip());
	}

	/**
	 * Writes a RuneScape string.
	 * @param string The string to write.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putRS2String(String string) {
		payload.put(string.getBytes());
		payload.put((byte) 0);
		return this;
	}

	public PacketBuilder putNT0String(String string) {
		for (char c : string.toCharArray()) {
			payload.put((byte) c);
		}
		payload.put((byte) '\0');
		return this;
	}

	public PacketBuilder putNewString(String string) {
		for (int i = 0; i < string.length(); i++) {
			final char c = string.charAt(i);
			if (((c <= 0) || (c >= '\u0080')) && ((c < '\u00a0') || (c > '\u00ff'))) {
				if (c == '\u20ac') {
					payload.put((byte) -128);
				} else if (c == '\u201a') {
					payload.put((byte) -126);
				} else if (c == '\u0192') {
					payload.put((byte) -125);
				} else if (c == '\u201e') {
					payload.put((byte) -124);
				} else if (c == '\u2026') {
					payload.put((byte) -123);
				} else if (c == '\u2020') {
					payload.put((byte) -122);
				} else if (c == '\u2021') {
					payload.put((byte) -121);
				} else if (c == '\u02c6') {
					payload.put((byte) -120);
				} else if (c == '\u2030') {
					payload.put((byte) -119);
				} else if (c == '\u0160') {
					payload.put((byte) -118);
				} else if (c == '\u2039') {
					payload.put((byte) -117);
				} else if (c == '\u0152') {
					payload.put((byte) -116);
				} else if (c == '\u017d') {
					payload.put((byte) -114);
				} else if (c == '\u2018') {
					payload.put((byte) -111);
				} else if (c == '\u2019') {
					payload.put((byte) -110);
				} else if (c == '\u201c') {
					payload.put((byte) -109);
				} else if (c == '\u201d') {
					payload.put((byte) -108);
				} else if (c == '\u2022') {
					payload.put((byte) -107);
				} else if (c == '\u2013') {
					payload.put((byte) -106);
				} else if (c == '\u2014') {
					payload.put((byte) -105);
				} else if (c == '\u02dc') {
					payload.put((byte) -104);
				} else if (c == '\u2122') {
					payload.put((byte) -103);
				} else if (c == '\u0161') {
					payload.put((byte) -102);
				} else if (c == '\u203a') {
					payload.put((byte) -101);
				} else if (c == '\u0153') {
					payload.put((byte) -100);
				} else if (c == '\u017e') {
					payload.put((byte) -98);
				} else if (c == '\u0178') {
					payload.put((byte) -97);
				} else {
					payload.put((byte) 63);
				}
			} else {
				payload.put((byte) c);
			}
		}
		payload.put((byte) 0);
		return this;
	}


	/**
	 * Writes a type-A short.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putShortA(int val) {
		payload.put((byte) (val >> 8));
		payload.put((byte) (val + 128));
		return this;
	}

	/**
	 * Writes a type-A byte.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putByteA(int val) {
		payload.put((byte) (val + 128));
		return this;
	}
	

	/**
	 * Writes a little endian type-A short.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putLEShortA(int val) {
		payload.put((byte) (val + 128));
		payload.put((byte) (val >> 8));
		return this;
	}

	/**
	 * Checks if this packet builder is empty.
	 * @return <code>true</code> if so, <code>false</code> if not.
	 */
	public boolean isEmpty() {
		return payload.position() == 0;
	}

	/**
	 * Starts bit access.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder startBitAccess() {
		bitPosition = payload.position() * 8;
		return this;
	}

	/**
	 * Finishes bit access.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder finishBitAccess() {
		payload.position((bitPosition + 7) / 8);
		return this;
	}

	/**
	 * Writes some bits.
	 * @param numBits The number of bits to write.
	 * @param value The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putBits(int numBits, int value) {
		if(!payload.hasArray()) {
			throw new UnsupportedOperationException("The IoBuffer implementation must support array() for bit usage.");
		}

		int bytes = (int) Math.ceil((double) numBits / 8D) + 1;
		payload.expand((bitPosition + 7) / 8 + bytes);

		byte[] buffer = payload.array();

		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;

		for(; numBits > bitOffset; bitOffset = 8) {
			buffer[bytePos] &= ~BIT_MASK_OUT[bitOffset];
			buffer[bytePos++] |= (value >> (numBits-bitOffset)) & BIT_MASK_OUT[bitOffset];
			numBits -= bitOffset;
		}
		if(numBits == bitOffset) {
			buffer[bytePos] &= ~BIT_MASK_OUT[bitOffset];
			buffer[bytePos] |= value & BIT_MASK_OUT[bitOffset];
		} else {
			buffer[bytePos] &= ~(BIT_MASK_OUT[numBits] << (bitOffset - numBits));
			buffer[bytePos] |= (value & BIT_MASK_OUT[numBits]) << (bitOffset - numBits);
		}
		return this;
	}

	/**
	 * Puts an <code>IoBuffer</code>.
	 * @param buf The buffer.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder put(IoBuffer buf) {
		payload.put(buf);
		return this;
	}


	public PacketBuilder nigga(int val) {
		payload.put((byte) (val >> 8));
		payload.put((byte) (val));//probably put short lol
		return this;
	}

	/**
	 * Writes a type-C byte.
	 * @param val The value to write.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putByteC(int val) {
		put((byte) (-val));
		return this;
	}

	/**
	 * Writes a little-endian short.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putLEShort(int val) {
		payload.put((byte) (val));
		payload.put((byte) (val >> 8));
		return this;
	}

	/**
	 * Writes a type-1 integer.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder writeIntV1(int val) {
		payload.put((byte) (val >> 8));
		payload.put((byte) val);
		payload.put((byte) (val >> 24));
		payload.put((byte) (val >> 16));
		return this;
	}

	/**
	 * Writes a type-2 integer.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putInt2(int val) {
		payload.put((byte) (val >> 16));
		payload.put((byte) (val >> 24));
		payload.put((byte) val);
		payload.put((byte) (val >> 8));
		return this;
	}

	/**
	 * Writes a little-endian integer.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putLEInt(int val) {
		payload.put((byte) (val));
		payload.put((byte) (val >> 8));
		payload.put((byte) (val >> 16));
		payload.put((byte) (val >> 24));
		return this;
	}

	/**
	 * Puts a sequence of bytes in the buffer.
	 * @param data The bytes.
	 * @param offset The offset.
	 * @param length The length.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder put(byte[] data, int offset, int length) {
		payload.put(data, offset, length);
		return this;
	}

	public PacketBuilder putBackwards(byte[] src, int offset, int length) {
		for (int i = length - 1; i >= offset; i--) {
			payload.put(src[i]);
		}
		return this;
	}

	/**
	 * Puts a type-A byte in the buffer.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putByteA(byte val) {
		payload.put((byte) (val + 128));
		return this;
	}

	/**
	 * Puts a type-C byte in the buffer.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putByteC(byte val) {
		payload.put((byte) (-val));
		return this;
	}

	/**
	 * Puts a type-S byte in the buffer.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putByteS(int val) {// try putByteS if it doesnt work we use A
		payload.put((byte) (128 - val));
		return this;
	}

	/**
	 * Puts a series of reversed bytes in the buffer.
	 * @param is The source byte array.
	 * @param offset The offset.
	 * @param length The length.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putReverse(byte[] is, int offset, int length) {
		for(int i = (offset + length - 1); i >= offset; i--) {
			payload.put(is[i]);
		}
		return this;
	}

	/**
	 * Puts a series of reversed type-A bytes in the buffer.
	 * @param is The source byte array.
	 * @param offset The offset.
	 * @param length The length.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putReverseA(byte[] is, int offset, int length) {
		for(int i = (offset + length - 1); i >= offset; i--) {
			putByteA(is[i]);
		}
		return this;
	}

	/**
	 * Puts a 3-byte integer.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putTriByte(int val) {
		payload.put((byte) (val >> 16));
		payload.put((byte) (val >> 8));
		payload.put((byte) val);
		return this;
	}

	/**
	 * Puts a byte or short.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putSmart(int val) {
		if(val >= 128) {
			putShort((val + 32768));
		} else {
			put((byte) val);
		}
		return this;
	}

	/**
	 * Puts a byte or short for signed use.
	 * @param val The value.
	 * @return The PacketBuilder instance, for chaining.
	 */
	public PacketBuilder putSignedSmart(int val) {
		if(val >= 128) {
			putShort((val + 49152));
		} else {
			put((byte) (val + 64));
		}
		return this;
	}

	public int position() {
		return payload.position();
	}

	public void putReverseA(IoBuffer payload2) {
		// TODO Auto-generated method stub

	}

	public void putBytesA(byte data[], int offset, int len) {
		for(int k = offset; k < len; k++)
			put((byte)(data[k] + 128));

	}



}
