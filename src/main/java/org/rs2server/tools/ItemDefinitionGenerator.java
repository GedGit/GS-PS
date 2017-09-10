package org.rs2server.tools;

import org.rs2server.cache.format.CacheItemDefinition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Clank1337
 */
public class ItemDefinitionGenerator {

	public ItemDefinitionGenerator(int start, int end) {
		for (int i = start; i <= end; i++) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./data/itemdef.txt"), true));
				CacheItemDefinition def = CacheItemDefinition.get(i);
				writer.newLine();
				writer.write("{");
				writer.newLine();
				writer.write("\"id\": " + i + ",");
				writer.newLine();
				writer.write("\"examine\": \"null\",");
				writer.newLine();
				writer.write("\"bonus\": [");
				writer.newLine();
				writer.write("0,\n0,\n0,\n0,\n0,\n" +
						"0,\n" +
						"0,\n" +
						"0,\n" +
						"0,\n" +
						"0,\n" +
						"0,\n" +
						"0,\n" +
						"0,\n" +
						"0,\n" +
						"0");
				writer.newLine();
				writer.write("],");
				writer.newLine();
				writer.write("\"stackable\": " + def.stackable + ",");
				writer.newLine();
				writer.write("\"noted\": " + (def.isNoted()) + ",");
				writer.newLine();
				writer.write("\"weight\": 0.0,");
				writer.newLine();
				writer.write("\"members\": false,");
				writer.newLine();
				writer.write("\"attackSpeed\": 4,");
				writer.newLine();
				writer.write("\"equipmentSlot\": -1,");
				writer.newLine();
				writer.write("\"extraDefinitions\": false,");
				writer.newLine();
				writer.write("\"fullHat\": true,");
				writer.newLine();
				writer.write("\"fullMask\": false,");
				writer.newLine();
				writer.write("\"fullBody\": false,");
				writer.newLine();
				writer.write("\"tradable\": false,");
				writer.newLine();
				writer.write("\"twoHanded\": false,");
				writer.newLine();
				writer.write("\"dropable\": false,");
				writer.newLine();
				writer.write("\"storePrice\": 0,");
				writer.newLine();
				writer.write("\"lowAlch\": 0,");
				writer.newLine();
				writer.write("\"highAlch\": 0");

				writer.newLine();
				writer.write("},");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
