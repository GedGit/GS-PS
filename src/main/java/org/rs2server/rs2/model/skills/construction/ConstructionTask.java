package org.rs2server.rs2.model.skills.construction;

import java.util.Arrays;

public class ConstructionTask {

	/**
	 * The slayer task groups.
	 * Each group has a unique id which is assigned by Jagex for the Slayer rewards interface,
	 * therefore these ids SHOULD NOT BE TOUCHED.
	 */
	public enum TaskGroup {
		BLUE_DRAGON(25),
		BANSHEE(38);

		private int id;

		TaskGroup(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		public static TaskGroup forName(String taskGroup) {
			if (taskGroup == null) {
				return null;
			}
			return Arrays.stream(TaskGroup.values()).filter(g -> g.name().equals(taskGroup)).findFirst().get();
		}
	}

	public enum Materials {
		AGENT(5419, new Object[][] { 
				{ "Chair", 1, 1, 25, 8.0 },
				{ "Crude_Wooden_Chair", 8, 1, 25, 16.0 },
				{ "Wooden_dining_table", 10, 1, 25, 18.0 },
				{ "Wooden_dining_bench", 10, 1, 25, 18.0 },
				{ "Wooden_kitchen_table", 12, 1, 25, 20.0 },
				{ "Rocking_chair", 14, 1, 25, 22.0 },
				{ "Oak_Chair", 19, 1, 25, 23.0 },
				{ "Wooden_bed", 20, 1, 25, 25.0 },
				{ "oak_dining_table", 22, 5, 30, 26.0 },
				{ "oak_dining_bench", 1, 1, 25, 8.0 },
		});
		

		private int id;
		private Object[][] data;
		@SuppressWarnings("unused")
		private TaskGroup group;

		Materials(int id, Object[][] data) {
			this.id = id;
			this.data = data;
		}

		public static Materials forId(int id) {
			for (Materials materials : Materials.values()) {
				if (materials.id == id) {
					return materials;
				}
			}
			return null;
		}

		public int getId() {
			return id;
		}

		public Object[][] getData() {
			return data;
		}

	}

	private Materials materials;
	private int itemId;
	private int taskAmount;
	private int initialAmount;

	public ConstructionTask(Materials materiels, int initalAmount, int itemId, int taskAmount, Materials materials) {
		this.materials = materials;
		this.initialAmount =initalAmount;
		this.itemId = itemId;
		this.initialAmount = taskAmount;
		this.taskAmount = taskAmount;
	}

	public String getName() {
		return (String) materials.data[itemId][0];
	}

	public int getItemId() {
		return itemId;
	}

	public int getTaskAmount() {
		return taskAmount;
	}

	public void decreaseAmount() {
		taskAmount--;
	}

	public double getXPAmount() {
		return Double.parseDouble(materials.data[itemId][4].toString()) * 4;
	}

	public int getInitialAmount() {
		return initialAmount;
	}

	public Materials getmaterials() {
		return materials;
	}
}
