package org.rs2server.util;

import org.apache.mina.core.buffer.IoBuffer;

import java.nio.ByteBuffer;

/**
 * This is for streaming or whatever
 *
 * @author 'Mystic Flow
 */
public class BufferUtils {

	private static final char[] CHARACTERS = {
		'\u20ac', '\0', '\u201a', '\u0192', '\u201e', '\u2026', '\u2020', 
		'\u2021', '\u02c6', '\u2030', '\u0160', '\u2039', '\u0152', '\0', 
		'\u017d', '\0', '\0', '\u2018', '\u2019', '\u201c', '\u201d', 
		'\u2022', '\u2013', '\u2014', '\u02dc', '\u2122', '\u0161', 
		'\u203a', '\u0153', '\0', '\u017e', '\u0178'
	};
	
	public static void writeRS2String(ByteBuffer buffer, String string) {
		buffer.put(string.getBytes());
		buffer.put((byte) 0);
	}

	public static String readRS2String(ByteBuffer buffer) {
		StringBuilder sb = new StringBuilder();
		byte b;
		while (buffer.remaining() > 0 && (b = buffer.get()) != 0) {
			sb.append((char) b);
		}
		return sb.toString();
	}

	public static int readSmart(ByteBuffer buf) {
		int peek = buf.get(buf.position()) & 0xFF;
		if (peek < 128) {
			return buf.get();
		} else {
			return (buf.getShort() & 0xFFFF) - 32768;
		}
	}
	
	public static int readSmart(IoBuffer buf) {
		int peek = buf.get(buf.position()) & 0xFF;
		if (peek < 128) {
			return buf.get();
		} else {
			return (buf.getShort() & 0xFFFF) - 32768;
		}
	}



	public static int getMediumInt(ByteBuffer buffer) {
		return ((buffer.get() & 0xFF) << 16) | ((buffer.get() & 0xFF) << 8) | (buffer.get() & 0xFF);
	}

	public static int readSmart2(ByteBuffer buffer) {
		int i_26_ = 0;
		int i_27_;
		for (i_27_ = readSmart(buffer); i_27_ == 32767; i_27_ = readSmart(buffer)) {
			i_26_ += 32767;
		}
		i_26_ += i_27_;
		return i_26_;
	}

	public static void writeInt(int val, int index, byte[] buffer) {
		buffer[index++] = (byte) (val >> 24);
		buffer[index++] = (byte) (val >> 16);
		buffer[index++] = (byte) (val >> 8);
		buffer[index++] = (byte) val;
	}


	public static int readMedium(int index, byte[] buffer) {
		return ((buffer[index++] & 0xff) << 16) | ((buffer[index++] & 0xff) << 8) | (buffer[index++] & 0xff);
	}
	
	public static int readInt(int index, byte[] buffer) {
		return ((buffer[index++] & 0xff) << 24) | ((buffer[index++] & 0xff) << 16) | ((buffer[index++] & 0xff) << 8) | (buffer[index++] & 0xff);
	}

    public static char getCPCharacter(ByteBuffer buffer) {
        int read = buffer.get() & 0xff;
        if (read == 0) {
            throw new IllegalArgumentException("Non cp1252 character 0x" + Integer.toString(read, 16) + " provided");
        }
        if (read >= 128 && read < 160) {
            char cpChar = CHARACTERS[read - 128];
            if (cpChar == '\0') {
                cpChar = '?';
            }
            read = cpChar;
        }
        return (char) read;
    }
/*
 * 
	final String getString(int i) {
		anInt7302++;
		int i_17_ = anInt7298;
		while ((aByteArray7322[anInt7298++] ^ 0xffffffff) != -1) {
		}
		int i_18_ = anInt7298 - i_17_ + -1;
		if (i >= -122) {
			return null;
		}
		if (i_18_ == 0) {
			return "";
		}
		return Class358.method3742(i_17_, aByteArray7322, i_18_, (byte) 75);
	}
		anInt4472++;
		char[] cs = new char[i_0_];
		int i_1_ = 0;
		for (int i_2_ = 0; i_0_ > i_2_; i_2_++) {
			int i_3_ = bs[i + i_2_] & 0xff;
			if (i_3_ != 0) {
				if (i_3_ >= 128 && i_3_ < 160) {
					int i_4_ = r.aCharArray9507[i_3_ + -128];
					if (i_4_ == 0) {
						i_4_ = 63;
					}
					i_3_ = i_4_;
				}
				cs[i_1_++] = (char) i_3_;
			}
		}
		if (b != 75) {
			aByteArray4479 = null;
		}
 */

	public static final String getString(ByteBuffer buf) {
		int i_19_ = buf.position();
		while (buf.get() != 0) {
			/* empty */
		}
		int i_20_ = buf.position() - i_19_ - 1;
		if (i_20_ == 0)
			return "";
		return method3722(i_19_, buf, i_20_);
	}

	static final String method3722(int i, ByteBuffer is, int i_1_) {
		char[] cs = new char[i_1_];
		int i_3_ = 0;
		for (int i_4_ = 0; i_1_ > i_4_; i_4_++) {
			int i_5_ = is.get(i + i_4_) & 0xff;
			if (i_5_ != 0) {
				if (i_5_ >= 128 && i_5_ < 160) {
					int i_6_ = CHARACTERS[i_5_ - 128];
					if (i_6_ == 0)
						i_6_ = 63;
					i_5_ = i_6_;
				}
				cs[i_3_++] = (char) i_5_;
			}
		}
		return new String(cs, 0, i_3_);
	}
/*    public static String getString(ByteBuffer in)  {
        StringBuffer string = new StringBuffer();

        int i;
        while ((i = in.get()) != 0 && i != -1) {
            string.append((char) i);
        }
        return string.toString();
    }*/

	public static String getJagexString(ByteBuffer buffer) {
		StringBuilder sb = new StringBuilder();
		int b;
		buffer.get();
		while (buffer.remaining() > 0 && (b = buffer.get() & 0xFF) != 0) {
			if (b >= 128 && b < 160) {
				int roar = CHARACTERS[b - 128];
				if (roar == 0) {
					roar = 63;
				}
				b = roar;
			}
			sb.append((char) b);
		}
		return sb.toString();
	}

}
