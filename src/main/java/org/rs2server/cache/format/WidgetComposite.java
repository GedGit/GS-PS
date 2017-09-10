package org.rs2server.cache.format;

import org.rs2server.cache.CacheManager;
import org.rs2server.cache.FileInformationTable;
import org.rs2server.util.BufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class WidgetComposite {
	public int[] spritesY;
	public static boolean[] interfacesLoadedArray;
	public int widgetItemPaddingX = 0;
	public static boolean mediaUnavailable = false;
	public int rotationX = 0;
	public int[] conditionTypes;
	public int anInt1772;
	public Object[] mouseExitListener;
	public int anInt1774;
	public int anInt1775;
	public int x = 0;
	public int[] conditionValues;
	public int anInt1778 = 0;
	public int anInt1779 = 0;
	public int anInt1780;
	public Object[] anObjectArray1781;
	public boolean hidden = false;
	public int anInt1784 = 0;
	public int scrollPosition = 0;
	public int scrollMaxH = 0;
	public int scrollMaxW = 0;
	public int color = 0;
	public Object[] anObjectArray1789;
	public int height = 0;
	public int actionType = 0;
	public boolean outline = false;
	public int alpha = 0;
	public Object[] anObjectArray1794;
	public Object[] anObjectArray1795;
	public int enabledTexture = -1;
	public int sprite = 0;
	public boolean aBool1798 = false;
	public int borderThickness = 0;
	public int shadowColor = 0;
	public boolean reflectH;
	public Object[] anObjectArray1802;
	public int modelType;
	public int modelId;
	public int anInt1805;
	int activeMediaID;
	public int animation;
	public int type;
	public int anInt1809 = 0;
	public int anInt1810 = 0;
	public int anInt1811;
	public int rotationZ = 0;
	public int rotationY = 0;
	public Object[] anObjectArray1814;
	public int activeMediaAnimID;
	public boolean aBool1816 = false;
	public int fontID = -343759393;
	public String text = "";
	public int anInt1819;
	public int anInt1820 = 0;
	public String name = "";
	public Object[] anObjectArray1822;
	public boolean textShadowed = false;
	public int y = 0;
	public int widgetItemPaddingY = 0;
	public boolean aBool1826;
	public int[] spritesX;
	public int mouseOverActiveColor = 0;
	public String[] interfaceActions;
	public int settings = 0;
	public boolean reflectV;
	public String[] actions;
	public WidgetComposite aClass108_Sub17_1833 = null;
	public int anInt1834 = 0;
	public int anInt1835 = 0;
	public boolean aBool1836 = false;
	public boolean hasScript = false;
	public int mouseOverColor = 0;
	public Object[] anObjectArray1841;
	public Object[] anObjectArray1842;
	public Object[] anObjectArray1843;
	public String activeComponentString = "";
	public Object[] mouseHoverListener;
	public Object[] anObjectArray1847;
	public Object[] anObjectArray1848;
	public Object[] anObjectArray1849;
	public int width = 0;
	public Object[] configListener;
	public int[] configChangeTriggers;
	public Object[] tableListener;
	public int[] itemUpdateTriggers;
	public boolean interactable = false;
	public int[] anIntArray1856;
	public Object[] renderListener;
	public static WidgetComposite[][] widgets;
	public Object[] mouseWheelTrigger;
	public int anInt1860 = 0;
	public Object[] anObjectArray1861;
	public int anInt1862 = 0;
	public int anInt1863 = 0;
	public Object[] anObjectArray1864;
	public Object[] anObjectArray1865;
	public int componentActiveColor = 0;
	public int[][] opcodes;
	public Object[] skillParameters;
	public int[] sprites;
	public String spellName = "";
	public String tooltip;
	public int[] widgetItems;
	public int[] widgetItemAmounts;
	public int itemID;
	public Object[] mouseEnterListener;
	public int anInt1877;
	public int mediaZoom = 1060870108;
	public WidgetComposite[] aClass108_Sub17Array1879;
	public boolean aBool1880;
	public boolean aBool1881;
	public Object[] anObjectArray1882;
	public int anInt1883;
	public String selectedActionName = "";
	public int anInt1885;
	public int contentType = 0;
	public int cycle;
	int activeMediaType = -1227663423;
	public int uid;
	private int anInt1846;
	public int texture = -1;
	private byte w;
	private byte buttonType;
	@SuppressWarnings("unused")
	private byte t, h;
	@SuppressWarnings("unused")
	private int by;
	public boolean as;
	public int id;
	public int parent = -1;
	public WidgetComposite owner;
	public List<WidgetComposite> components = new ArrayList<>();
	public int anInt798;

	public static WidgetComposite getComponent(int uid) throws IOException {
		int parent = uid >> 16;
		int child = uid & 0xFFFF;
		return get(parent)[child];
	}

	public static WidgetComposite[] get(int id) throws IOException {

		FileInformationTable table = CacheManager.getFIT(3);
		if (widgets == null) {
			widgets = new WidgetComposite[table.getEntryCount()][];
		}

		if (widgets[id] != null) {
			return widgets[id];
		}

		WidgetComposite[] children = widgets[id] = new WidgetComposite[CacheManager.getRealContainerChildCount(3, id)];
		for (int i = 0; i < children.length; i++) {

			WidgetComposite child = children[i];
			if (child != null) {
				continue;
			}

			byte[] component = CacheManager.getData(3, id, i);
			if (component == null) {
				continue;
			}

			ByteBuffer data = ByteBuffer.wrap(component);

			try {
				if (data.hasRemaining()) {
					child = children[i] = new WidgetComposite();
					child.uid = i + (id << 16);
					child.id = i;


					if (component[0] == -1) {
						child.decodeInteractable(data);
					} else {
						child.decodeInterface(data);
					}
					if (child.parent != -1) {
						WidgetComposite parent = get(child.parent >> 16)[child.parent & 0xFF];

						if (parent != null) {
							child.owner = parent;
							parent.components.add(child);
						}
					}

				} else {
					System.out.println("NULL DATA For "+id+", "+i);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return children;
	}

	public void decodeInterface(ByteBuffer buffer) {
		this.interactable = false;
		this.type = buffer.get() & 0xFF;
		this.actionType = buffer.get() & 0xFF;
		this.contentType = ((buffer.getShort() & 0xFFFF));
		this.anInt1778 = (this.x = buffer.getShort());
		this.anInt1779 = (this.y = buffer.getShort());
		this.width = ((buffer.getShort() & 0xFFFF));
		this.height = ((buffer.getShort() & 0xFFFF));
		this.alpha = buffer.get() & 0xFF;
		this.parent = ((buffer.getShort() & 0xFFFF));

		if('\uffff' == this.parent) {
			this.parent = -1;
		} else {
			this.parent = ((this.uid & -65536) + this.parent);
		}
		this.anInt1846 = ((buffer.getShort() & 0xFFFF));
		if(this.anInt1846 == '\uffff') {
			this.anInt1846 = -1;
		}

		int conditionCount = buffer.get() & 0xFF;
		int condition;
		if(conditionCount > 0) {
			this.conditionTypes = new int[conditionCount];
			this.conditionValues = new int[conditionCount];

			for(condition = 0; condition < conditionCount; ++condition) {
				this.conditionTypes[condition] = buffer.get() & 0xFF;
				this.conditionValues[condition] = ((buffer.getShort() & 0xFFFF));
			}
		}

		int opcodeCount = buffer.get() & 0xFF;
		int opcode;
		int subOpCode;
		int subOpCodeCount;
		if(opcodeCount > 0) {
			this.opcodes = new int[opcodeCount][];

			for(opcode = 0; opcode < opcodeCount; ++opcode) {
				subOpCodeCount = ((buffer.getShort() & 0xFFFF));
				this.opcodes[opcode] = new int[subOpCodeCount];

				for(subOpCode = 0; subOpCode < subOpCodeCount; ++subOpCode) {
					this.opcodes[opcode][subOpCode] = ((buffer.getShort() & 0xFFFF));
					if(this.opcodes[opcode][subOpCode] == '\uffff') {
						this.opcodes[opcode][subOpCode] = -1;
					}
				}
			}
		}

		if(0 == this.type) {
			this.scrollMaxW = ((buffer.getShort() & 0xFFFF));
			this.hidden = (buffer.get() & 0xFF) == 1;
		}

		if(1 == this.type) {
			buffer.getShort();
			buffer.get();
		}

		if(this.type == 2) {
			this.widgetItems = new int[this.height * this.width];
			this.widgetItemAmounts = new int[this.height * this.width];
			int var6 = buffer.get() & 0xFF;
			if(var6 == 1) {
				// this.anInt1830 = (this.anInt1830 * 956161607 | 268435456);
			}

			int tempVal = buffer.get() & 0xFF;
			if(1 == tempVal) {
				// this.anInt1830 = (this.anInt1830 * 956161607 | 1073741824) * -1506120841;
			}

			int var7 = buffer.get() & 0xFF;
			if(1 == var7) {
				// this.anInt1830 = (this.anInt1830 * 956161607 | Integer.MIN_VALUE) * -1506120841;
			}

			int var3 = buffer.get() & 0xFF;
			if(var3 == 1) {
				//   this.anInt1830 = (this.anInt1830 * 956161607 | 536870912) * -1506120841;
			}

			this.widgetItemPaddingX = buffer.get() & 0xFF;
			this.widgetItemPaddingY = buffer.get() & 0xFF;
			this.spritesX = new int[20];
			this.spritesY = new int[20];
			this.sprites = new int[20];

			int index;
			for(index = 0; index < 20; ++index) {
				int hasSprite = buffer.get() & 0xFF;
				if(1 == hasSprite) {
					this.spritesX[index] = buffer.getShort();
					this.spritesY[index] = buffer.getShort();
					this.sprites[index] = buffer.getInt();
				} else {
					this.sprites[index] = -1;
				}
			}

			this.interfaceActions = new String[5];

			for(index = 0; index < 5; ++index) {
				String action = BufferUtils.getString(buffer);
				if(action.length() > 0) {
					this.interfaceActions[index] = action;
					this.settings = (this.settings | 1 << 23 + index);
				}
			}
		}

		if(this.type == 3) {
			this.outline = (buffer.get() & 0xFF) == 1;
		}

		if(4 == this.type || 1 == this.type) {
			this.anInt1863 = buffer.get() & 0xFF;
			this.anInt1862 = buffer.get() & 0xFF;
			this.anInt1820 = buffer.get() & 0xFF;
			this.fontID = ((buffer.getShort() & 0xFFFF));
			if(this.fontID == '\uffff') {
				this.fontID = -1;
			}
			this.textShadowed = (buffer.get() & 0xFF) == 1;
		}

		if(4 == this.type) {
			this.text = BufferUtils.getString(buffer);
			this.activeComponentString = BufferUtils.getString(buffer);
		}

		if(1 == this.type || this.type == 3 || 4 == this.type) {
			this.color = buffer.getInt();
		}

		if(this.type == 3 || this.type == 4) {
			this.componentActiveColor = buffer.getInt();
			this.mouseOverColor = buffer.getInt();
			this.mouseOverActiveColor = buffer.getInt();
		}

		if(this.type == 5) { // sprite
			this.texture = buffer.getInt();
			this.enabledTexture = buffer.getInt();
		}

		if(6 == this.type) {
			this.modelType = 1;
			this.modelId = ((buffer.getShort() & 0xFFFF));
			if(this.modelId * 2030124439 == '\uffff') {
				this.modelId = 1;
			}

			this.activeMediaType = -1;
			this.activeMediaID = ((buffer.getShort() & 0xFFFF));
			if(this.activeMediaID == '\uffff') {
				this.activeMediaID = -1;
			}

			this.animation = ((buffer.getShort() & 0xFFFF));
			if(this.animation == '\uffff') {
				this.animation = -1;
			}

			this.activeMediaAnimID = ((buffer.getShort() & 0xFFFF));
			if(this.activeMediaAnimID == '\uffff') {
				this.activeMediaAnimID = -1;
			}

			this.mediaZoom = ((buffer.getShort() & 0xFFFF));
			this.rotationX = ((buffer.getShort() & 0xFFFF));
			this.rotationZ = ((buffer.getShort() & 0xFFFF));
		}

		if(7 == this.type) {
			this.widgetItems = new int[this.height * this.width];
			this.widgetItemAmounts = new int[this.height * this.width];
			this.anInt1863 = buffer.get() & 0xFF;
			this.fontID = ((buffer.getShort() & 0xFFFF));

			if(this.fontID == '\uffff') {
				this.fontID = -1;
			}

			this.textShadowed = (buffer.get() & 0xFF) == 1;
			this.color = buffer.getInt();
			this.widgetItemPaddingX = buffer.getShort();
			this.widgetItemPaddingY = buffer.getShort();
			opcode = buffer.get() & 0xFF;
			if(1 == opcode) {
				//this.anInt1830 = (this.anInt1830 * 956161607 | 1073741824) * -1506120841;
			}

			this.interfaceActions = new String[5];

			for(int actionIndex = 0; actionIndex < 5; ++actionIndex) {
				String action = BufferUtils.getString(buffer);
				if(action.length() > 0) {
					this.interfaceActions[actionIndex] = action;
					this.settings = (this.settings | 1 << 23 + actionIndex);
				}
			}
		}

		if(this.type == 8) {
			this.text = BufferUtils.getString(buffer);
		}

		if(2 == this.actionType || 2 == this.type) {
			this.selectedActionName = BufferUtils.getString(buffer);
			this.spellName = BufferUtils.getString(buffer);
			opcode = ((buffer.getShort() & 0xFFFF)) & 63;
			this.settings = (this.settings | opcode << 11);
		}

		if(this.actionType == 1 || 4 == this.actionType || 5 == this.actionType || 6 == this.actionType) {
			this.tooltip = BufferUtils.getString(buffer);
			if(this.tooltip.length() == 0) {
				if(this.actionType == 1) {
					this.tooltip = "Ok";
				}

				if(4 == this.actionType) {
					this.tooltip = "Select";
				}

				if(this.actionType == 5) {
					this.tooltip = "Select";
				}

				if(this.actionType == 6) {
					this.tooltip = "Continue";
				}
			}
		}

		if(this.actionType == 1 || 4 == this.actionType || 5 == this.actionType) {
			// this.anInt1830 = (this.anInt1830 * 956161607 | 4194304) * -1506120841;
		}

		if(this.actionType == 6) {
			//     this.anInt1830 = (this.anInt1830 * 956161607 | 1) * -1506120841;
		}

	}

	private void decodeInteractable(ByteBuffer buffer) {
		buffer.position(1);

		this.interactable = true;
		this.type = buffer.get() & 0xFF;
		this.contentType = buffer.getShort() & 0xFFFF;
		this.anInt1778 = (this.x = buffer.getShort());
		this.anInt1779 = (this.y = buffer.getShort());

		this.width = buffer.getShort() & 0xFFFF;

		if(9 == this.type ) {
			this.height = buffer.getShort();
		} else {
			this.height = buffer.getShort() & 0xFFFF;
		}
		this.w = buffer.get();
		this.buttonType = buffer.get();
		this.t = buffer.get();
		this.h = buffer.get();

		this.parent = buffer.getShort() & 0xFFFF;

		if(this.parent == '\uffff') {
			this.parent = -1;
		} else {
			this.parent = (this.parent + (this.uid & -65536));
		}

		this.hidden = (buffer.get() & 0xFF) == 1;
		if(0 == this.type) {
			this.scrollMaxH = buffer.getShort() & 0xFFFF;
			this.scrollMaxW = buffer.getShort() & 0xFFFF;
			buffer.get();
		}

		if(this.type == 5) {
			this.texture = buffer.getInt();
			this.sprite = buffer.getShort() & 0xFFFF;
			this.aBool1798 = (buffer.get() & 0xFF) == 1;
			this.alpha = buffer.get() & 0xFF;
			this.borderThickness = buffer.get() & 0xFF;
			this.shadowColor = buffer.getInt();

			this.reflectH = (buffer.get() & 0xFF) == 1;
			this.reflectV = (buffer.get() & 0xFF) == 1;
		}

		if(6 == this.type) {
			this.modelType = 1;
			this.modelId = buffer.getShort() & 0xFFFF;
			if(this.modelId == '\uffff') {
				this.modelId = -1;
			}

			this.anInt1809 = buffer.getShort();
			this.anInt1810 = buffer.getShort() ;
			this.rotationX = buffer.getShort() & 0xFFFF;
			this.rotationZ = buffer.getShort() & 0xFFFF;
			this.rotationY = buffer.getShort() & 0xFFFF;
			this.mediaZoom = buffer.getShort() & 0xFFFF;
			this.animation = buffer.getShort() & 0xFFFF;
			if(this.animation == '\uffff') {
				this.animation = -1;
			}
			this.aBool1816 = (buffer.get() & 0xFF) == 1;

			buffer.position(buffer.position() + 2);
			if (this.w != 0) {
				this.by = buffer.getShort() & 0xFFFF;
			}

			if (this.buttonType != 0) {
				buffer.position(buffer.position() + 2);
			}
		}

		if(4 == this.type) {
			this.fontID = buffer.getShort() & 0xFFFF;
			if('\uffff' == this.fontID) {// gonna have to go back around and check all these with a different client 
				this.fontID = -1;
			}
			this.text = BufferUtils.getString(buffer);
			this.anInt1820 = buffer.get() & 0xFF;// font size (?)
			this.anInt1863 = buffer.get() & 0xFF;
			this.anInt1862 = buffer.get() & 0xFF;

			this.textShadowed = (buffer.get() & 0xFF) == 1;
			this.color = buffer.getInt();
		}

		if(this.type == 3) {
			this.color = buffer.getInt();
			this.outline = (buffer.get() & 0xFF) == 1;
			this.alpha = buffer.get() & 0xFF;
		}

		if(this.type == 9) {
			this.anInt1811 = buffer.get() & 0xFF;
			this.color = buffer.getInt();
			this.as = (buffer.get() & 0xFF) == 1;
		}

		this.settings = BufferUtils.getMediumInt(buffer);
		this.name = BufferUtils.getString(buffer);

		int var4 = buffer.get() & 0xFF;
		if(var4 > 0) {
			this.actions = new String[var4];
			for(int var3 = 0; var3 < var4; ++var3) {
				this.actions[var3] = BufferUtils.getString(buffer);
			}
		}

		this.anInt1834 = buffer.get() & 0xFF;
		this.anInt1835 = buffer.get() & 0xFF;
		this.aBool1836 = (buffer.get() & 0xFF) == 1;
		this.selectedActionName = BufferUtils.getString(buffer);


		this.anObjectArray1861 = this.readScriptParameters(buffer, (byte) 4);// spellbook related

		this.anObjectArray1802 = this.readScriptParameters(buffer, (byte)59);// duel options related
		this.mouseEnterListener = this.readScriptParameters(buffer, (byte)111);
		this.mouseExitListener = this.readScriptParameters(buffer, (byte)83);
		this.anObjectArray1849 = this.readScriptParameters(buffer, (byte)39);

		this.configListener = this.readScriptParameters(buffer, (byte)53);
		this.tableListener = this.readScriptParameters(buffer, (byte)48);
		this.skillParameters = this.readScriptParameters(buffer, (byte)116);
		this.renderListener = this.readScriptParameters(buffer, (byte)75);
		this.anObjectArray1795 = this.readScriptParameters(buffer, (byte)122);
		this.mouseHoverListener = this.readScriptParameters(buffer, (byte)88);
		this.anObjectArray1794 = this.readScriptParameters(buffer, (byte)48);
		this.anObjectArray1841 = this.readScriptParameters(buffer, (byte)124);
		this.anObjectArray1842 = this.readScriptParameters(buffer, (byte)78);
		this.anObjectArray1843 = this.readScriptParameters(buffer, (byte)41);
		this.anObjectArray1847 = this.readScriptParameters(buffer, (byte)105);
		this.anObjectArray1848 = this.readScriptParameters(buffer, (byte)59);
		this.mouseWheelTrigger = this.readScriptParameters(buffer, (byte)3);
		this.configChangeTriggers = this.readTriggers(buffer, 2025977477);
		this.itemUpdateTriggers = this.readTriggers(buffer, 2102476719);
		this.anIntArray1856 = this.readTriggers(buffer, 2079814240);
	}

	int[] readTriggers(ByteBuffer buffer, int var2) {
		int triggerCount = buffer.get() & 0xFF;
		if(0 == triggerCount) {
			return null;
		} else {
			int[] triggers = new int[triggerCount];

			for(int triggerIndex = 0; triggerIndex < triggerCount; ++triggerIndex) {
				triggers[triggerIndex] = buffer.getInt();
			}

			return triggers;
		}
	}

	Object[] readScriptParameters(ByteBuffer buffer, byte var2) {
		int length = buffer.get() & 0xFF;
		if(length == 0) {
			return null;
		} else {
			Object[] params = new Object[length];

			for(int paramIndex = 0; paramIndex < length; ++paramIndex) {
				int var4 = buffer.get() & 0xFF;
				if(0 == var4) {
					params[paramIndex] = new Integer(buffer.getInt());
				} else if(var4 == 1) {
					params[paramIndex] = BufferUtils.getString(buffer);
				}
			}

			this.hasScript = true;
			return params;
		}
	}
}
