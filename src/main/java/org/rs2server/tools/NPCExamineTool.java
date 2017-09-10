package org.rs2server.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.rs2server.rs2.Constants;
import org.rs2server.rs2.model.npc.NPCLootTable;

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author Clank1337
 */
public class NPCExamineTool {

	public static class ItemRarities {

		private final List<Integer> npcIds;
		private final List<ItemRarity> rarities;

		ItemRarities(List<Integer> npcIds, List<ItemRarity> rarities) {
			this.npcIds = npcIds;
			this.rarities = rarities;
		}

		public List<Integer> getNpcIds() {
			return npcIds;
		}

		public List<ItemRarity> getRarities() {
			return rarities;
		}
	}

	public class ItemRarity {
		private final int itemId;
		private final int minAmount;
		private final int maxAmount;
		private final double rarity;

		ItemRarity(int itemId, int minAmount, int maxAmount, double rarity) {
			this.itemId = itemId;
			this.minAmount = minAmount;
			this.maxAmount = maxAmount;
			this.rarity = rarity;
		}

		public String getColor() {
			String colorScheme = "";
			switch (toString()) {
			case "Common":
				colorScheme = "<col=00FF00><shad=000000>";
				break;
			case "Uncommon":
				colorScheme =  "<col=FFFF00><shad=000000>";
				break;
			case "Quite Rare":
				colorScheme =  "<col=FF4C4C><shad=000000>";
				break;
			case "Very Rare":
				colorScheme =  "<col=992D2D><shad=00BFFF>";
				break;
			case "Legendary":
				colorScheme =  "<col=00BFFF><shad=000000>";
				break;
			
			}
			return colorScheme;
		}

		@Override
		public String toString() {
			if (rarity >= 50.0) {
				return "Common";
			}
			if (rarity >= 25.0) {
				return "Uncommon";
			}
			if (rarity >= 10.0) {
				return "Quite Rare";
			}
			if (rarity >= 1.0) {
				return "Very Rare";
			}
			if (rarity < 1.0) {
				return "Legendary";
			}
			return "N / A";
		}

		public int getItemId() {
			return itemId;
		}

		public double getRarity() {
			return rarity;
		}

		public int getMinAmount() {
			return minAmount;
		}

		public int getMaxAmount() {
			return maxAmount;
		}
	}

	@SuppressWarnings("resource")
	public void write() {
		final Gson gson = new Gson();
		final File dir = new File("./data/json/drops/");
		for (final File drop : dir.listFiles()) {
			try (final BufferedReader parse = new BufferedReader(new FileReader(drop))) {
				final NPCLootTable lootTable = gson.fromJson(parse, NPCLootTable.class);
				List<ItemRarities> npcList = new ArrayList<>();
				List<Integer> ids = Arrays.stream(lootTable.getNpcIdentifiers()).boxed().collect(toList());
				List<ItemRarity> rarity = new ArrayList<>();
				
				lootTable.getDynamicDrops().stream().filter(Objects::nonNull).forEach(i -> {
					rarity.add(new ItemRarity(i.getItemID(), i.getMinAmount(), i.getMaxAmount(), i.getHitRollCeil()));
					if (Constants.DEBUG)
						System.out.println("NPC Drop Tables: ["+drop.getName()+"] "+i.getItemID());
				});

				npcList.add(new ItemRarities(ids, rarity));

				Gson swag = new GsonBuilder().setPrettyPrinting().create();

				File file = new File("./data/json/examine/" + drop.getName().replace(".gson", ".json"));

				if (file.exists())
					file.delete(); // delete if already exists
				
				file.createNewFile();

				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				swag.toJson(npcList, writer);
				writer.close();

				Scanner fileScanner = new Scanner(file);
				fileScanner.nextLine();
				FileWriter fileStream = new FileWriter(file);
				BufferedWriter out = new BufferedWriter(fileStream);
				while (fileScanner.hasNextLine()) {
					String next = fileScanner.nextLine();
					if (next.equals("\n")) {
						out.newLine();
					} else if (fileScanner.hasNextLine()) {
						out.write(next);
					}
					out.newLine();
				}
				out.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
