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
// Source File Name:   InputStream.java

package org.rs2server.io;

import org.rs2server.rs2.model.player.Player;


// Referenced classes of package com.rs.io:
//            Stream

public final class InputStream extends Stream
{

    public void initBitAccess()
    {
        bitPosition = offset * 8;
    }
    //

    public void finishBitAccess()
    {
        offset = (7 + bitPosition) / 8;
    }

    public int readBits(int bitOffset)
    {
        int bytePos = bitPosition >> 0x6a15e203;
        int i_8_ = -(7 & bitPosition) + 8;
        bitPosition += bitOffset;
        int value = 0;
        for(; ~bitOffset < ~i_8_; i_8_ = 8)
        {
            value += (BIT_MASK[i_8_] & buffer[bytePos++]) << -i_8_ + bitOffset;
            bitOffset -= i_8_;
        }

        if(~i_8_ == ~bitOffset)
            value += buffer[bytePos] & BIT_MASK[i_8_];
        else
            value += buffer[bytePos] >> -bitOffset + i_8_ & BIT_MASK[bitOffset];
        return value;
    }

    public InputStream(int capacity)
    {
        buffer = new byte[capacity];
    }

    public InputStream(byte buffer[])
    {
        this.buffer = buffer;
        length = buffer.length;
    }

    public void checkCapacity(int length)
    {
        if(offset + length >= buffer.length)
        {
            byte newBuffer[] = new byte[(offset + length) * 2];
            System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
            buffer = newBuffer;
        }
    }

    public int read24BitInt()
    {
        return (readUnsignedByte() << 16) + (readUnsignedByte() << 8) + readUnsignedByte();
    }

    public void skip(int length)
    {
        offset += length;
    }

    public void setLength(int length)
    {
        this.length = length;
    }

    public void setOffset(int offset)
    {
        this.offset = offset;
    }

    public int getRemaining()
    {
        return offset >= length ? 0 : length - offset;
    }

    public void addBytes(byte b[], int offset, int length)
    {
        checkCapacity(length - offset);
        System.arraycopy(b, offset, buffer, this.offset, length);
        this.length += length - offset;
    }

    public int readPacket(Player p)
    {
        return readUnsignedByte();
    }

    public int readByte()
    {
        return getRemaining() <= 0 ? 0 : buffer[offset++];
    }

    public void readBytes(byte buffer[], int off, int len)
    {
        for(int k = off; k < len + off; k++)
            buffer[k] = (byte)readByte();

    }

    public void readBytes(byte buffer[])
    {
        readBytes(buffer, 0, buffer.length);
    }

    public int readSmart2()
    {
        int i = 0;
        int i_33_;
        for(i_33_ = readUnsignedSmart(); i_33_ == 32767; i_33_ = readUnsignedSmart())
        {
            i += 32767;
        }

        i += i_33_;
        return i;
    }

    public int readUnsignedByte()
    {
        return readByte() & 0xff;
    }

    public int readByte128()
    {
        return (byte)(readByte() - 128);
    }

    public int readByteC()
    {
        return (byte)(-readByte());
    }

    public int read128Byte()
    {
        return (byte)(128 - readByte());
    }

    public int readUnsignedByte128()
    {
        return readUnsignedByte() - 128 & 0xff;
    }

    public int readUnsignedByteC()
    {
        return -readUnsignedByte() & 0xff;
    }

    public int readUnsigned128Byte()
    {
        return 128 - readUnsignedByte() & 0xff;
    }

    public int readShortLE()
    {
        int i = readUnsignedByte() + (readUnsignedByte() << 8);
        if(i > 32767)
            i -= 0x10000;
        return i;
    }
    
    public int readShortLE(byte b)
    {
        int i = readUnsignedByte() + (readUnsignedByte() << 8);
        if(i > 32767)
            i -= 0x10000;
        return i;
    }

    public int readShort128()
    {
        int i = (readUnsignedByte() << 8) + (readByte() - 128 & 0xff);
        if(i > 32767)
            i -= 0x10000;
        return i;
    }

    public int readShortLE128()
    {
        int i = (readByte() - 128 & 0xff) + (readUnsignedByte() << 8);
        if(i > 32767)
            i -= 0x10000;
        return i;
    }

    public int read128ShortLE()
    {
        int i = (128 - readByte() & 0xff) + (readUnsignedByte() << 8);
        if(i > 32767)
            i -= 0x10000;
        return i;
    }

    public int readShort()
    {
        int i = (readUnsignedByte() << 8) + readUnsignedByte();
        if(i > 32767)
            i -= 0x10000;
        return i;
    }
    
    public int read128Short()
    {
        int i = (readUnsignedByte() << 8) + 128-readUnsignedByte();
        if(i > 32767)
            i -= 0x10000;
        return i;
    }

    public int readUnsignedShortLE()
    {
        return readUnsignedByte() + (readUnsignedByte() << 8);
    }

    public int readUnsignedShort()
    {
        return (readUnsignedByte() << 8) + readUnsignedByte();
    }

    public int readBigSmart()
    {
        if(~buffer[offset] <= -1)
            return readUnsignedShort();
        else
            return readInt() & 0x7fffffff;
    }

    public int readUnsignedShort128()
    {
        return (readUnsignedByte() << 8) + (readByte() - 128 & 0xff);
    }

    public int readUnsignedShortLE128()
    {
        return (readByte() - 128 & 0xff) + (readUnsignedByte() << 8);
    }

    public int readInt()
    {
        return (readUnsignedByte() << 24) + (readUnsignedByte() << 16) + (readUnsignedByte() << 8) + readUnsignedByte();
    }
    public int readMedInt()
    {
        return (readUnsignedByte() << 16) + (readUnsignedByte() << 8) + readUnsignedByte();
    }


    public int readIntV1()
    {
        return (readUnsignedByte() << 8) + readUnsignedByte() + (readUnsignedByte() << 24) + (readUnsignedByte() << 16);
    }

    public int readIntV2()
    {
        return (readUnsignedByte() << 16) + (readUnsignedByte() << 24) + readUnsignedByte() + (readUnsignedByte() << 8);
    }

    public int readIntLE()
    {
        return readUnsignedByte() + (readUnsignedByte() << 8) + (readUnsignedByte() << 16) + (readUnsignedByte() << 24);
    }

    public long readLong()
    {
        long l = (long)readInt() & 0xffffffffL;
        long l1 = (long)readInt() & 0xffffffffL;
        return (l << 32) + l1;
    }

    public String readString()
    {
        String s;
        int b;
        for(s = ""; (b = readByte()) != 0; s = (new StringBuilder(String.valueOf(s))).append((char)b).toString());
        return s;
    }


    public String getString() {
        final int start  = offset;
        while (readByte() != 0) {
            // empty
        }
        final int i_101_ = offset - start - 1;
        return  (i_101_ == 0 ? "" : method_e_String(buffer,
                start, i_101_, -100790773));
    }

    static String method_e_String(final byte[] is, final int i, final int i_16_, final int i_17_) {
        String string;
            final char[] cs = new char[i_16_];
            int i_18_ = 0;
            for (int i_19_ = 0; i_19_ < i_16_; i_19_++) {
                int i_20_ = is[i + i_19_] & 0xff;
                if (i_20_ != 0) {
                    if ((i_20_ >= 128) && (i_20_ < 160)) {
                        int i_21_ = field_n_2237[i_20_ - 128];
                        if (i_21_ == 0) {
                            i_21_ = 63;
                        }
                        i_20_ = i_21_;
                    }
                    cs[i_18_++] = (char) i_20_;
                }
            }
            string = new String(cs, 0, i_18_);
        return string;
    }

    public static final char[] field_n_2237 = { '\u20ac', '\0', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', '\u2021', '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\0', '\u017d', '\0', '\0', '\u2018', '\u2019', '\u201c', '\u201d', '\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', '\u203a', '\u0153', '\0', '\u017e', '\u0178' };

    public String readJagString()
    {
        readByte();
        String s;
        int b;
        for(s = ""; (b = readByte()) != 0; s = (new StringBuilder(String.valueOf(s))).append((char)b).toString());
        return s;
    }

    public int readUnsignedSmart()
    {
        int i = 0xff & buffer[offset];
        if(i >= 128)
            return -32768 + readUnsignedShort();
        else
            return readUnsignedByte();
    }
    
    public byte[] readStringBytes() {
    	int start = offset;
    	while (buffer[offset++] != 10) {
    		
    	}
    	byte[] bytes = new byte[offset - start - 1];
    	for (int i = start; i < offset - 1; i++) {
    		bytes[i - start] = buffer[i];
    	}
    	
    	return bytes;
    }

    private static final int BIT_MASK[] = {
        0, 1, 3, 7, 15, 31, 63, 127, 255, 511, 
        1023, 2047, 4095, 8191, 16383, 32767, 65535, 0x1ffff, 0x3ffff, 0x7ffff, 
        0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 
        0x3fffffff, 0x7fffffff, -1
    };

}
