package org.rs2server.cache.format;

import org.rs2server.cache.CacheManager;
import org.rs2server.util.BufferUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class CacheItemDefinition {

	private byte aByte327;
	public int cost;
	private int[] recol_s;
	public int id = -1;
	private int[] recol_d;
	public boolean members;
	public int femalesymbol;
	public int certtemplate;
	private int femalewear2;
	private int manwear;
	public int manhead2;
	private int resizex;
	public String[] op;
	private int xof2d;
	public String name;
	public int womanhead2;
	public int mesh;
	private int manhead;
	public boolean stackable;
	public byte[] examine;
	public int noted;
	public int zoom2d;
	public static boolean members_world = true;
	private int contrast;
	public int malesymbol;
	private boolean aBoolean359 = false;
	private static boolean aBoolean360;
	private int manwear2;
	public String[] iop;
	public int xan2d;
	private int resizez;
	private int resizey;
	public int[] countobj;
	private int yof2d;
	private int ambient;
	public int womanhead;
	public int yan2d;
	private int femalewear;
	public int[] countco;
	public int team;
	public static int anInt376;
	private int zan2d;
	private byte aByte378;
	private boolean aBoolean379 = false;
	private int[] retex_s;
	private int[] retex_d;
	private int aByte205;
	private int aByte154;
	public static final Map<Integer, CacheItemDefinition> CACHE = new HashMap<Integer, CacheItemDefinition>();
	public int field_bo_155 = -1;
	public int field_bs_154;
	private int value;
	private char[] notedName;

	public static final CacheItemDefinition get(int i) {
		CacheItemDefinition definition = CACHE.get(i);

		if (definition != null)
			return definition;

		definition = new CacheItemDefinition();

		definition.id = i;
		definition.method264();
		try {
			ByteBuffer data = ByteBuffer.wrap(CacheManager.getData(2, 10, i));
			while (true) {
				try {
					int op = data.get() & 0xFF;
					if (op == 0)
						break;
					definition.decode(data, op);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		CACHE.put(i, definition);
		return definition;
	}

	public static void writeDWordBigEndian(DataOutputStream dat, int i) throws IOException {
		dat.write((byte) (i >> 16));
		dat.write((byte) (i >> 8));
		dat.write((byte) (i >> 8));
	}

	private void decode(ByteBuffer data, int opcode) {
		if (opcode == 1) {
			mesh = data.getShort() & 0xFFFF;
		} else if (opcode == 2) {
			name = BufferUtils.getString(data);
		} else if (opcode == 4) {
			zoom2d = data.getShort() & 0xFFFF;
		} else if (opcode == 5) {
			xan2d = data.getShort() & 0xFFFF;
		} else if (opcode == 6) {
			yan2d = data.getShort() & 0xFFFF;
		} else if (opcode == 7) {
			xof2d = data.getShort() & 0xFFFF;
			if (xof2d > 32767) {
				xof2d -= 65536;
			}
		} else if (opcode == 8) {
			yof2d = data.getShort() & 0xFFFF;
			if (yof2d > 32767) {
				yof2d -= 65536;
			}
		} else if (opcode == 10) {
			data.getShort();
			// data.getShort();
		} else if (opcode == 11) {
			stackable = true;
		} else if (opcode == 12) {
			cost = data.getInt();
		} else if (opcode == 16) {
			members = true;
		} else if (opcode == 23) {
			manwear = data.getShort() & 0xFFFF;
			aByte205 = data.get() & 0xFF;
		} else if (opcode == 24) {
			manwear2 = data.getShort() & 0xFFFF;
		} else if (opcode == 25) {
			femalewear = data.getShort() & 0xFFFF;
			this.aByte154 = data.get() & 0xFF;
		} else if (opcode == 26) {
			femalewear2 = data.getShort() & 0xFFFF;
		} else if (opcode >= 30 && opcode < 35) {
			if (op == null) {
				op = new String[5];
			}
			op[opcode - 30] = BufferUtils.getString(data);
			if (op[opcode - 30].equalsIgnoreCase("hidden")) {
				op[opcode - 30] = null;
			}
		} else if (opcode >= 35 && opcode < 40) {
			if (iop == null) {
				iop = new String[5];
			}
			iop[opcode - 35] = BufferUtils.getString(data);
		} else if (opcode == 40) {
			int i_54_ = data.get() & 0xFF;
			recol_s = new int[i_54_];
			recol_d = new int[i_54_];
			for (int i_55_ = 0; i_55_ < i_54_; i_55_++) {
				recol_s[i_55_] = data.getShort() & 0xFFFF;
				recol_d[i_55_] = data.getShort() & 0xFFFF;
			}
		} else if (opcode == 41) {
			int length = data.get() & 0xFF;
			retex_s = new int[length];
			retex_d = new int[length];
			for (int i_43_ = 0; length > i_43_; i_43_++) {
				retex_s[i_43_] = data.getShort() & 0xFFFF;
				retex_d[i_43_] = data.getShort() & 0xFFFF;
			}
		} else if (opcode == 65) {
			// stockmarket = true;
		} else if (opcode == 78) {
			malesymbol = data.getShort() & 0xFFFF;
		} else if (opcode == 79) {
			femalesymbol = data.getShort() & 0xFFFF;
		} else if (opcode == 90) {
			manhead = data.getShort() & 0xFFFF;
		} else if (opcode == 91) {
			womanhead = data.getShort() & 0xFFFF;
		} else if (opcode == 92) {
			manhead2 = data.getShort() & 0xFFFF;
		} else if (opcode == 93) {
			womanhead2 = data.getShort() & 0xFFFF;
		} else if (opcode == 95) {
			zan2d = data.getShort() & 0xFFFF;
		} else if (opcode == 96) {
			data.get();// "dummyitem"
		} else if (opcode == 97) {
			noted = data.getShort() & 0xFFFF;
		} else if (opcode == 98) {
			certtemplate = data.getShort() & 0xFFFF;
		} else if (opcode >= 100 && opcode < 110) {
			if (countobj == null) {
				countobj = new int[10];
				countco = new int[10];
			}
			countobj[opcode - 100] = data.getShort() & 0xFFFF;
			countco[opcode - 100] = data.getShort() & 0xFFFF;
		} else if (opcode == 110) {
			resizex = data.getShort() & 0xFFFF;
		} else if (opcode == 111) {
			resizey = data.getShort() & 0xFFFF;
		} else if (opcode == 112) {
			resizez = data.getShort() & 0xFFFF;
		} else if (opcode == 113) {
			ambient = data.get();
		} else if (opcode == 114) {
			contrast = data.get() * 5;
		} else if (opcode == 115) {
			team = data.get() & 0xFF;
		} else if (opcode == 249) {
			int len = data.get() & 0xFF;
			for (int i = 0; i < len; i++) {
				boolean isString = data.get() == 1;
				int key = BufferUtils.getMediumInt(data);
				Object value = isString ? BufferUtils.getJagexString(data) : data.getInt();
			}
		} else if (opcode == 134) {
			data.get();
		} else if (opcode == 139) {
			this.field_bs_154 = data.getShort() & 0xFFFF;
		} else if (opcode == 140) {
			this.field_bo_155 = data.getShort() & 0xFFFF;
		} else if (opcode == 148) {
			data.getShort();
		} else if (opcode == 149) {
			data.getShort();
		} else {
			System.out.println("Unrecognized opcode: " + opcode);
		}
	}

	public int cursor1op;
	public int cursor1;
	public int cursor2op;
	public int cursor2;
	public int cursor1iop;
	public int icursor1;
	public int cursor2iop;
	public int icursor2;

	public final void method264() {
		mesh = 0;
		name = null;
		examine = null;
		recol_s = null;
		recol_d = null;
		zoom2d = 2000;
		xan2d = 0;
		yan2d = 0;
		zan2d = 0;
		xof2d = 0;
		yof2d = 0;
		stackable = false;
		cost = 1;
		members = false;
		op = null;
		iop = null;
		manwear = -1;
		manwear2 = -1;
		aByte378 = (byte) 0;
		femalewear = -1;
		femalewear2 = -1;
		aByte327 = (byte) 0;
		malesymbol = -1;
		femalesymbol = -1;
		manhead = -1;
		manhead2 = -1;
		womanhead = -1;
		womanhead2 = -1;
		countobj = null;
		countco = null;
		noted = -1;
		certtemplate = -1;
		resizex = 128;
		resizey = 128;
		resizez = 128;
		ambient = 0;
		contrast = 0;
		team = 0;
	}

	public int getId() {
		return id;
	}

	public boolean isNoted() {
		return certtemplate != -1;
	}

	public String getName() {
		if (isNoted())
			return getNotedName();
		return name;
	}

	public int getLowAlch() {
		return (int) (0.4 * cost);
	}

	public int getHighAlch() {
		return (int) (0.6 * cost);
	}

	public int getStorePrice() {
		return cost;
	}

	public int getCost() {
		return cost;
	}

	public int getNoted() {
		return noted;
	}

	public String getNotedName() {
		CacheItemDefinition def = CacheItemDefinition.get(noted);
		if (def == null || def.getName() == null) {
			return "" + id;
		}
		return def.getName();
	}
}
