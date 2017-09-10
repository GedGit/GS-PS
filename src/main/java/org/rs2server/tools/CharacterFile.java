package org.rs2server.tools;

import org.apache.mina.core.buffer.IoBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class CharacterFile {

	public static void main(String[] args) throws FileNotFoundException, IOException {
		File f = new File("data/savedGames/salve.dat.gz");
		InputStream is = new GZIPInputStream(new FileInputStream(f));
		IoBuffer buf = IoBuffer.allocate(1024);
		buf.setAutoExpand(true);
		while(true) {
			byte[] temp = new byte[1024];
			int read = is.read(temp, 0, temp.length);
			if(read == -1) {
				break;
			} else {
				buf.put(temp, 0, read);
			}
		}
		buf.flip();
		for (int i = 0; i < buf.remaining(); i++) {
			System.out.println(buf.get());
		}
		is.close();
	}

}
