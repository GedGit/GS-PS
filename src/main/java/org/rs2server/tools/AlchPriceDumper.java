package org.rs2server.tools;

import org.rs2server.cache.format.CacheItemDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tim on 11/24/2015.
 */
public class AlchPriceDumper {

	private static final int ITEM_AMOUNT = 13064;
	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(AlchPriceDumper.class);

	@SuppressWarnings("unused")
	public static void dumpExamines() {
		for (int i = 0; i < ITEM_AMOUNT; i++) {
			try {
				int[] values = new int[2];
				CacheItemDefinition def = CacheItemDefinition.get(i);
				if (def == null || def.getName() == null) {
					continue;
				}
				// URL url = new URL("http://2007.runescape.wikia.com/wiki/" +
				// def.getName().replaceAll(" ", "_"));
				// HttpURLConnection con = (HttpURLConnection)
				// url.openConnection();
				// BufferedReader in = new BufferedReader(new
				// InputStreamReader(con.getInputStream()));
				// BufferedWriter writer = new BufferedWriter(new
				// FileWriter("./alchprices.txt"));
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./examines.txt"), true));
				URL url = new URL("http://2007.runescape.wikia.com/wiki/" + def.getName().replaceAll(" ", "_"));
				HttpURLConnection con = (HttpURLConnection) url.openConnection();

				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line;
				String examine = "";
				while ((line = reader.readLine()) != null) {
					if (line.startsWith(
							"<td colspan=\"2\" style=\"padding:3px 7px 3px 7px; line-height:140%; text-align:center;\"> ")) {
						examine = line.replaceFirst(
								"<td colspan=\"2\" style=\"padding:3px 7px 3px 7px; line-height:140%; text-align:center;\"> ",
								"");
						System.out.println("Examine: " + examine);
					}
				}
				writer.write(i + ":" + examine);
				writer.newLine();
				con.disconnect();
				reader.close();
				writer.close();
			} catch (IOException e) {
				System.out.println("Unable to find " + CacheItemDefinition.get(i).getName() + " with item ID: " + i);
			}
		}
	}

	@SuppressWarnings("unused")
	public static void dumpPrices() {

		for (int i = 0; i < ITEM_AMOUNT; i++) {
			try {
				int[] values = new int[2];
				CacheItemDefinition def = CacheItemDefinition.get(i);
				if (def == null || def.getName() == null) {
					continue;
				}
				// URL url = new URL("http://2007.runescape.wikia.com/wiki/" +
				// def.getName().replaceAll(" ", "_"));
				// HttpURLConnection con = (HttpURLConnection)
				// url.openConnection();
				// BufferedReader in = new BufferedReader(new
				// InputStreamReader(con.getInputStream()));
				// BufferedWriter writer = new BufferedWriter(new
				// FileWriter("./alchprices.txt"));
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File("./alchprices.txt"), true));
				URL url = new URL("http://2007.runescape.wikia.com/wiki/" + def.getName().replaceAll(" ", "_"));
				HttpURLConnection con = (HttpURLConnection) url.openConnection();

				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line;

				int prices[] = new int[2];
				int amt = 0;
				while ((line = reader.readLine()) != null) {
					if (line.contains("</th><td>") && line.contains(";coins")) {
						String[] value = line.split(" ");
						if (value[1] != null) {
							value[0] = value[0].replace(";coins", "").replace(",", "").replace("#160", "");
							value[1] = value[1].replace(";coins", "").replace(",", "").replace("#160", "")
									.replace("&", "").replace("<br", "");
							if (!isInteger(value[1])) {
								break;
							}
							if (amt == 1) {
								prices[0] = Integer.parseInt(value[1]);
							} else if (amt == 0) {
								prices[1] = Integer.parseInt(value[1]);
							}
						}
						amt++;
					}
				}
				System.out.println("Id: " + i + ", Low: " + prices[0] + ", High: " + (prices[1]));
				writer.write(i + ":" + prices[0] + ":" + prices[1]);
				writer.newLine();
				con.disconnect();
				reader.close();
				writer.close();
			} catch (IOException e) {
				System.out.println("Unable to find " + CacheItemDefinition.get(i).getName() + " with item ID: " + i);
			}
		}
	}

	public static boolean isInteger(String str) {
		if (str == null) {
			return false;
		}
		int length = str.length();
		if (length == 0) {
			return false;
		}
		int i = 0;
		if (str.charAt(0) == '-') {
			if (length == 1) {
				return false;
			}
			i = 1;
		}
		for (; i < length; i++) {
			char c = str.charAt(i);
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}
}
