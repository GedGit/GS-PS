/*******************************************************************************
 * Azureify Revision 498 RuneScape Emulator
 * Copyright (C) 2012 Ryan ryry-@sympatico.ca
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   OutputStream.java

package org.rs2server.rs2.net;

import java.math.BigInteger;

import org.rs2server.io.Stream;
import org.rs2server.rs2.model.player.Player;

// Referenced classes of package com.rs.io:
//            Stream

public class OutputStream extends Stream {

	public OutputStream(int capacity) {
		opcodeStart = 0;
		setBuffer(new byte[capacity]);
	}

	public OutputStream() {
		opcodeStart = 0;
		setBuffer(new byte[16]);
	}

	public OutputStream(byte buffer[]) {
		opcodeStart = 0;
		setBuffer(buffer);
		offset = buffer.length;
		length = buffer.length;
	}

	public OutputStream(int buffer[]) {
		opcodeStart = 0;
		setBuffer(new byte[buffer.length]);
		int ai[];
		int j = (ai = buffer).length;
		for (int i = 0; i < j; i++) {
			int value = ai[i];
			writeByte(value);
		}

	}

	public void checkCapacityPosition(int position) {
		if (position >= getBuffer().length) {
			byte newBuffer[] = new byte[position + 16];
			System.arraycopy(getBuffer(), 0, newBuffer, 0, getBuffer().length);
			setBuffer(newBuffer);
		}
	}

	public void skip(int length) {
		setOffset(getOffset() + length);
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public void writeBytes(byte b[], int offset, int length) {
		checkCapacityPosition((getOffset() + length) - offset);
		System.arraycopy(b, offset, getBuffer(), getOffset(), length);
		setOffset(getOffset() + (length - offset));
	}

	public void writeBytes(byte b[]) {
		int offset = 0;
		int length = b.length;
		checkCapacityPosition((getOffset() + length) - offset);
		System.arraycopy(b, offset, getBuffer(), getOffset(), length);
		setOffset(getOffset() + (length - offset));
	}

	public void addBytes128(byte data[], int offset, int len) {
		for (int k = offset; k < len; k++)
			writeByte((byte) (data[k] + 128));

	}

	public void addBytesS(byte data[], int offset, int len) {
		for (int k = offset; k < len; k++)
			writeByte((byte) (-128 + data[k]));

	}

	public void addBytes_Reverse(byte data[], int offset, int len) {
		for (int i = len - 1; i >= 0; i--)
			writeByte(data[i]);

	}

	public void addBytes_Reverse128(byte data[], int offset, int len) {
		for (int i = len - 1; i >= 0; i--)
			writeByte((byte) (data[i] + 128));

	}

	public void writeByte(int i) {
		writeByte(i, offset++);
	}

	public void writeNegativeByte(int i) {
		writeByte(-i, offset++);
	}

	public void writeByte(int i, int position) {
		checkCapacityPosition(position);
		getBuffer()[position] = (byte) i;
	}

	public void writeByte128(int i) {
		writeByte(i + 128);
	}

	public void writeByteC(int i) {
		writeByte(-i);
	}

	public void write128Byte(int i) {
		writeByte(128 - i);
	}

	public void writeByteCA(int i) {
		writeByte(-128 + i);
	}

	public void writeByteAC(int i) {
		writeByte(-i - 128);
	}

	public void writeShortLE128(int i) {
		writeByte(i + 128);
		writeByte(i >> 8);
	}

	public void writeShort128(int i) {
		writeByte(i >> 8);
		writeByte(i + 128);
	}

	public void writeSmart(int i) {
		if (i >= 128)
			writeShort(i + 32768);
		else
			writeByte(i);
	}

	public void writeNewSmart(int i) {
		if (i > 32767)
			writeInt(i - 0x7fffffff - 1);
		else
			writeShort(i);
	}

	public void writeShort(int i) {
		writeByte(i >> 8);
		writeByte(i);
	}

	public void writeShortLE(int i) {
		writeByte(i);
		writeByte(i >> 8);
	}

	public void write24BitInteger(int i) {
		writeByte(i >> 16);
		writeByte(i >> 8);
		writeByte(i);
	}

	public void write24BitIntegerBackwards(int i) {
		writeByte(i);
		writeByte(i >> 8);
		writeByte(i >> 16);

	}

	public void writeInt(int i) {
		writeByte(i >> 24);
		writeByte(i >> 16);
		writeByte(i >> 8);
		writeByte(i);
	}

	public void writeUnsignedInt(int i) {
		writeByte((i & 0xFF) >> 24);
		writeByte((i & 0xFF) >> 16);
		writeByte((i & 0xFF) >> 8);
		writeByte(i & 0xFF);
	}

	public void writeIntV1(int i) {
		writeByte(i >> 8);
		writeByte(i);
		writeByte(i >> 24);
		writeByte(i >> 16);
	}

	public void writeIntV2(int i) {
		writeByte(i >> 16);
		writeByte(i >> 24);
		writeByte(i);
		writeByte(i >> 8);
	}

	public void writeIntV3(int i) {
		// TODO Auto-generated method stub
		writeByte(i);
		writeByte(i >> 16);
		writeByte(i >> 24);
		writeByte(i >> 8);
	}

	public void writeIntLE(int i) {
		writeByte(i);
		writeByte(i >> 8);
		writeByte(i >> 16);
		writeByte(i >> 24);
	}

	public void writeLong(long l) {
		writeByte((int) (l >> 56));
		writeByte((int) (l >> 48));
		writeByte((int) (l >> 40));
		writeByte((int) (l >> 32));
		writeByte((int) (l >> 24));
		writeByte((int) (l >> 16));
		writeByte((int) (l >> 8));
		writeByte((int) l);
	}

	public void writePSmarts(int i) {
		if (i < 128) {
			writeByte(i);
			return;
		}
		if (i < 32768) {
			writeShort(32768 + i);
			return;
		} else {
			System.out.println("Error psmarts out of range:");
			return;
		}
	}

	public void writeString(String s) {
		checkCapacityPosition(getOffset() + s.length() + 1);
		System.arraycopy(s.getBytes(), 0, getBuffer(), getOffset(), s.length());
		setOffset(getOffset() + s.length());
		writeByte(0);
	}

	public void writeGJString(String s) {
		writeByte(0);
		writeString(s);
	}

	public void putGJString3(String s) {
		writeByte(0);
		writeString(s);
		writeByte(0);
	}

	public void writePacket(Player p, int id) {

		// getBuffer()[offset++] = (byte) (id +
		// p.getSession().outCipher.getNextValue());
	}

	public void writePacketVarByte(Player p, int id) {
		this.writePacket(p, id);
		this.writeByte(0);
		this.opcodeStart = this.getOffset() - 1;
	}

	public void writePacketVarShort(Player p, int id) {
		this.writePacket(p, id);
		this.writeShort(0);
		this.opcodeStart = this.getOffset() - 2;
	}

	public void endPacketVarByte() {
		writeByte((getOffset() - (opcodeStart + 2)) + 1, opcodeStart);
	}

	public void endPacketVarShort() {
		int size = getOffset() - (opcodeStart + 2);
		writeByte(size >> 8, opcodeStart++);
		writeByte(size, opcodeStart);
	}

	public void putRS2String(String string) {
		for (char c : string.toCharArray()) {
			writeByte((byte) c);
		}
		writeByte((byte) 0);

	}

	public void initBitAccess() {
		bitPosition = getOffset() * 8;
	}

	public void finishBitAccess() {
		setOffset((bitPosition + 7) / 8);
	}

	public int getBitPos(int i) {
		return 8 * i - bitPosition;
	}

	public void writeBits(int numBits, int value) {
		// initBitAccess();

		int bytePos = bitPosition >> 3;
		int bitOffset = 8 - (bitPosition & 7);
		bitPosition += numBits;
		for (; numBits > bitOffset; bitOffset = 8) {
			checkCapacityPosition(bytePos);
			getBuffer()[bytePos] &= ~BIT_MASK[bitOffset];
			getBuffer()[bytePos++] |= value >> numBits - bitOffset & BIT_MASK[bitOffset];
			numBits -= bitOffset;
		}

		checkCapacityPosition(bytePos);
		if (numBits == bitOffset) {
			getBuffer()[bytePos] &= ~BIT_MASK[bitOffset];
			getBuffer()[bytePos] |= value & BIT_MASK[bitOffset];
		} else {
			getBuffer()[bytePos] &= ~(BIT_MASK[numBits] << bitOffset - numBits);
			getBuffer()[bytePos] |= (value & BIT_MASK[numBits]) << bitOffset - numBits;
		}
		// finishBitAccess();
	}

	public void setBuffer(byte buffer[]) {
		this.buffer = buffer;
	}

	public final void rsaEncode(BigInteger key, BigInteger modulus) {
		int length = offset;
		offset = 0;
		byte data[] = new byte[length];
		getBytes(data, 0, length);
		BigInteger biginteger2 = new BigInteger(data);
		BigInteger biginteger3 = biginteger2.modPow(key, modulus);
		byte out[] = biginteger3.toByteArray();
		offset = 0;
		writeBytes(out, 0, out.length);
	}

	private static final int BIT_MASK[];
	private int opcodeStart;

	static {
		BIT_MASK = new int[32];
		for (int i = 0; i < 32; i++)
			BIT_MASK[i] = (1 << i) - 1;

	}

	public void writeBytesA(byte[] data, int len, int offset) {
		for (int i = offset; i < len - offset; i++) {
			writeByte128(data[i]);
		}

	}

	public void putBytes(byte[] data, int len, int offset) {
		for (int i = offset; i < len - offset; i++) {
			writeByte(data[i]);
		}

	}
}
