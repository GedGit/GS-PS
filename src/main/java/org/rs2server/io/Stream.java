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
// Source File Name:   Stream.java

package org.rs2server.io;


public abstract class Stream
{

    public Stream()
    {
    }

    public int getLength()
    {
        return length;
    }

    public byte[] getBuffer()
    {
        return buffer;
    }

    public int getOffset()
    {
        return offset;
    }

    public final void decodeXTEA(int keys[], int start, int end)
    {
        int l = offset;
        offset = start;
        int i1 = (end - start) / 8;
        for(int j1 = 0; j1 < i1; j1++)
        {
            int k1 = readInt();
            int l1 = readInt();
            int sum = 0xc6ef3720;
            int delta = 0x9e3779b9;
            for(int k2 = 32; k2-- > 0;)
            {
                l1 -= keys[(sum & 0x1c84) >>> 11] + sum ^ (k1 >>> 5 ^ k1 << 4) + k1;
                sum -= delta;
                k1 -= (l1 >>> 5 ^ l1 << 4) + l1 ^ keys[sum & 3] + sum;
            }

            offset -= 8;
            writeInt(k1);
            writeInt(l1);
        }

        offset = l;
    }

    private final int readInt()
    {
        offset += 4;
        return ((0xff & buffer[-3 + offset]) << 16) + (((0xff & buffer[-4 + offset]) << 24) + ((buffer[-2 + offset] & 0xff) << 8) + (buffer[-1 + offset] & 0xff));
    }

    private final void writeInt(int value)
    {
        buffer[offset++] = (byte)(value >> 24);
        buffer[offset++] = (byte)(value >> 16);
        buffer[offset++] = (byte)(value >> 8);
        buffer[offset++] = (byte)value;
    }

    public final void getBytes(byte data[], int off, int len)
    {
        for(int k = off; k < len + off; k++)
            data[k] = buffer[offset++];

    }

    protected int offset;
    public int length;
    protected byte buffer[];
    protected int bitPosition;
}
