package org.rs2server.tools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.rs2server.rs2.domain.service.api.loot.Loot;
import org.rs2server.rs2.domain.service.api.loot.LootGenerationService;
import org.rs2server.rs2.model.npc.NPCLootTable;

import java.io.*;
import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * @author Clank1337
 */
public class DropConverter {

	@SuppressWarnings("resource")
	public static void handleSwag() {
		final Gson gson = new Gson();
		final File dir = new File("./data/json/drops/");
		for (final File drop : dir.listFiles()) {
			try (final BufferedReader parse = new BufferedReader(new FileReader(drop))) {
				final NPCLootTable lootTable = gson.fromJson(parse, NPCLootTable.class);
				List<LootGenerationService.NpcLootTable> npcList = new ArrayList<>();

				List<Integer> ids = Arrays.stream(lootTable.getNpcIdentifiers()).boxed().collect(toList());
				List<Loot> guaranteedList = new ArrayList<>();
				lootTable.getStaticDrops().stream().filter(Objects::nonNull).forEach(
						i -> guaranteedList.add(Loot.of(i.getItemID(), i.getMinAmount(), i.getMaxAmount(), 100)));
				int rolls = lootTable.getRolls();

				List<Loot> low = lootTable.getDynamicDrops().stream().filter(Objects::nonNull)
						.filter(l -> l.getHitRollCeil() >= 20)
						.map(i -> Loot.of(i.getItemID(), i.getMinAmount(), i.getMaxAmount(), i.getHitRollCeil()))
						.collect(toList());
				List<Loot> med = lootTable.getDynamicDrops().stream().filter(Objects::nonNull)
						.filter(l -> l.getHitRollCeil() > 10 && l.getHitRollCeil() < 20)
						.map(i -> Loot.of(i.getItemID(), i.getMinAmount(), i.getMaxAmount(), i.getHitRollCeil()))
						.collect(toList());
				List<Loot> high = lootTable.getDynamicDrops().stream().filter(Objects::nonNull)
						.filter(l -> l.getHitRollCeil() <= 10)
						.map(i -> Loot.of(i.getItemID(), i.getMinAmount(), i.getMaxAmount(), i.getHitRollCeil()))
						.collect(toList());
				npcList.add(new LootGenerationService.NpcLootTable(rolls, ids, guaranteedList, low, med, high));

				Gson swag = new GsonBuilder().setPrettyPrinting().create();

				try {
					File file = new File("./data/json/drops2/" + drop.getName().replace(".gson", ".json"));

					if (!file.exists()) {
						file.createNewFile();
					}

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
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
