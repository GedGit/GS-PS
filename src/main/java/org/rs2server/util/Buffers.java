package org.rs2server.util;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

/**
 * Buffer utility class.
 * @author Graham Edgecombe
 *
 */
public class Buffers {

	/**
	 * Reads a null terminated string from a byte buffer.
	 * @param buffer The buffer.
	 * @return The string.
	 */
	public static String readString(ByteBuffer buffer) {
		StringBuilder bldr = new StringBuilder();
		while(buffer.hasRemaining()) {
			byte b = buffer.get();
			if(b == 0) {
				break;
			}
			bldr.append((char) b);
		}
		return bldr.toString();
	}
	
	public static void writeString(RandomAccessFile raf, String s) {
		try {
			byte[] stringBytes = s.getBytes();
			for (int i = 0; i < s.length(); i++) {
				raf.writeByte(stringBytes[i]);
			}
			raf.writeByte(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}
