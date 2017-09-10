package org.rs2server.cache.format;

import org.rs2server.cache.Cache;
import org.rs2server.cache.CacheManager;
import org.rs2server.util.BufferUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class CacheNPCDefinition {
	short[] recolorToFind;
	public int anInt2156 = 32;
	public int npcID;
	public String name;
	short[] recolorToReplace;
	public int[] models;
	int[] models_2;
	public int stanceAnimation = -1;
	public int standTurnAnimation = -1;
	public int occupiedTiles = 1;
	public int walkAnimation = -1;
	short[] retextureToReplace;
	public int rotate90RightAnimation = -1;
	public boolean aBool2170 = true;
	int resizeX = 128;
	int contrast = -1;
	public int rotate180Animation = -1;
	public int playerVariable = -1;
	public String[] options = new String[5];
	public boolean renderOnMinimap = true;
	public int combatLevel = -1;
	public int rotate90LeftAnimation = -1;
	int resizeY = 128;
	public boolean hasRenderPriority = false;
	int ambient = -1;
	public int headIcon = -1;
	public int anInt2184 = -1239106770;
	public int[] anIntArray2185;
	short[] retextureToFind;
	public int playerSetting = -1;
	public boolean isClickable = true;
	public int runAnimation = -1;
	public boolean aBool2190 = false;
	public int id;
	static byte aByte2194;

	public static CacheNPCDefinition[] npcs;
//	public static final Map<Integer, NpcDefinition> CACHE = new HashMap<Integer, NpcDefinition>();

	public static final CacheNPCDefinition get(int i) {
		
		if (npcs == null) {
			npcs = new CacheNPCDefinition[Cache.getAmountOfNpcs()];
		}
		
		CacheNPCDefinition definition = npcs[i];

		if (definition != null) {
			return definition;
		}

		definition = new CacheNPCDefinition();

		definition.id = i;
		
		try {

			byte[] npc = CacheManager.getData(2, 9, i);
			if (npc == null) {
				return null;
			}
			ByteBuffer data = ByteBuffer.wrap(npc);
			if (data != null) {
				while(true) {
					try {
						int op = data.get() & 0xFF;

						if (op == 0) {
							break;
						}
						definition.decode(data, op);
					} catch (Exception e) {
						e.printStackTrace();
						return null;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		npcs[i] = definition;
		return definition;
	}



	public static void writeDWordBigEndian(DataOutputStream dat, int i) throws IOException {
		dat.write((byte) (i >> 16));
		dat.write((byte) (i >> 8));
		dat.write((byte) (i >> 8));
	}
	
	private void decode(ByteBuffer buffer, int opcode) {
		if(1 == opcode) {
			int length = buffer.get() & 0xFF;
			this.models = new int[length];

			for(int index = 0; index < length; ++index) {
				this.models[index] = buffer.getShort() & 0xFFFF;
			}

		} else if(2 == opcode) {
			this.name = BufferUtils.getString(buffer);
		} else if(12 == opcode) {
			this.occupiedTiles = buffer.get() & 0xFF;
		} else if(opcode == 13) {
			this.stanceAnimation = buffer.getShort() & 0xFFFF;
		} else if(opcode == 14) {
			this.walkAnimation = buffer.getShort() & 0xFFFF;
		} else if(15 == opcode) {
			this.standTurnAnimation = buffer.getShort() & 0xFFFF;
		} else if(opcode == 16) {
			this.runAnimation = buffer.getShort() & 0xFFFF;
		} else if(17 == opcode) {
			this.walkAnimation = buffer.getShort() & 0xFFFF;
			this.rotate180Animation = buffer.getShort() & 0xFFFF;
			this.rotate90RightAnimation = buffer.getShort() & 0xFFFF;
			this.rotate90LeftAnimation = buffer.getShort() & 0xFFFF;
		} else if(opcode >= 30 && opcode < 35) {
			this.options[opcode - 30] = BufferUtils.getString(buffer);
			if(this.options[opcode - 30].equalsIgnoreCase("hidden")) {
				this.options[opcode - 30] = null;
			}
		} else if(opcode == 40) {
			int length = buffer.get() & 0xFF;
			this.recolorToFind = new short[length];
			this.recolorToReplace = new short[length];

			for(int index = 0; index < length; ++index) {
				this.recolorToFind[index] = (short) (buffer.getShort() & 0xFFFF);
				this.recolorToReplace[index] = (short) (buffer.getShort() & 0xFFFF);
			}

		} else if(opcode == 41) {
			int length = buffer.get() & 0xFF;
			this.retextureToFind = new short[length];
			this.retextureToReplace = new short[length];

			for(int index = 0; index < length; ++index) {
				this.retextureToFind[index] = (short) (buffer.getShort() & 0xFFFF);
				this.retextureToReplace[index] = (short) (buffer.getShort() & 0xFFFF);
			}

		} else if(60 != opcode) {
			if(opcode == 93) {
				this.renderOnMinimap = false;
			} else if(95 == opcode) {
				this.combatLevel = buffer.getShort() & 0xFFFF;
			} else if(97 == opcode) {
				this.resizeX = buffer.getShort() & 0xFFFF;
			} else if(98 == opcode) {
				this.resizeY = buffer.getShort() & 0xFFFF;
			} else if(opcode == 99) {
				this.hasRenderPriority = true;
			} else if(100 == opcode) {
				this.ambient = buffer.get();
			} else if(101 == opcode) {
				this.contrast = buffer.get();
			} else if(opcode == 102) {
				this.headIcon = buffer.getShort() & 0xFFFF;
			} else if(103 == opcode) {
				this.anInt2156 = buffer.getShort() & 0xFFFF;
			} else if(opcode == 106) {
				this.playerVariable = buffer.getShort() & 0xFFFF;
				if(65535 == this.playerVariable) {
					this.playerVariable = -1;
				}

				this.playerSetting = buffer.getShort() & 0xFFFF;
				if(65535 == this.playerSetting) {
					this.playerSetting = -1;
				}

				int length = buffer.get() & 0xFF;
				this.anIntArray2185 = new int[length + 1];

				for(int index = 0; index <= length; ++index) {
					this.anIntArray2185[index] = buffer.getShort() & 0xFFFF;
					if(this.anIntArray2185[index] == 65535) {
						this.anIntArray2185[index] = -1;
					}
				}

			} else if(107 == opcode) {
				this.isClickable = false;
			} else if(opcode == 109) {
				this.aBool2170 = false;
			} else if(opcode == 111) {
				this.aBool2190 = true;
			} else if(opcode == 112) {
				this.anInt2184 = buffer.get() & 0xFF;
			} else {
				System.out.println("Unrecognized opcode: "+opcode);
			}
		} else {
			int length = buffer.get() & 0xFF;
			this.models_2 = new int[length];

			for(int index = 0; index < length; ++index) {
				this.models_2[index] = buffer.getShort() & 0xFFFF;
			}
		}
	}
	
	public String[] getOptions() {
		return options;
	}



	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}

	public int getCombatLevel() {
		return combatLevel;
	}



	public int getSize() {
		return occupiedTiles;
	}



	public int getOccupiedTiles() {
		return occupiedTiles;
	}
}
