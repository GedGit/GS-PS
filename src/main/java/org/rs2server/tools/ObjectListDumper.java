package org.rs2server.tools;

import org.rs2server.cache.format.CacheObjectDefinition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Clank1337
 */
public class ObjectListDumper {

	public static void dump(int start, int end) {
		for (int i = start; i <= end; i++) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./Information/object_list.txt"), true));
				CacheObjectDefinition def = CacheObjectDefinition.forID(i);
				if (def == null)
					continue;
				if (def.getName() == null)
					continue;
				writer.write("id: " + i + " - name: "+def.getName());
				System.out.println("id: " + i + " - name: "+def.getName());
				writer.newLine();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
