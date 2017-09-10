package org.rs2server.tools;

import org.apache.mina.core.buffer.IoBuffer;
import org.rs2server.cache.format.CacheItemDefinition;
import org.rs2server.rs2.model.Skills;
import org.rs2server.rs2.model.container.Bank;
import org.rs2server.rs2.model.container.Equipment;
import org.rs2server.rs2.model.container.Inventory;
import org.rs2server.rs2.model.player.Player;
import org.rs2server.rs2.util.IoBufferUtils;
import java.io.*;
import java.util.zip.GZIPInputStream;

/**
 * @author Clank1337
 */
public class ReadCharacterFiles {

	public ReadCharacterFiles() {

	}

	public void getItems(Player player, int itemId, int amount) throws IOException {
		File dir = new File("./data/savedGames/");
		File[] files = dir.listFiles(pathname -> {
			String name = pathname.getName().toLowerCase();
			return name.endsWith(".dat.gz") && pathname.isFile();
		});
		for (File f : files) {
			try {
				InputStream is = new GZIPInputStream(new FileInputStream(f));
				IoBuffer buf = IoBuffer.allocate(1024);
				buf.setAutoExpand(true);
				while (true) {
					byte[] temp = new byte[1024];
					int read = is.read(temp, 0, temp.length);
					if (read == -1) {
						break;
					} else {
						buf.put(temp, 0, read);
					}
				}
				buf.flip();
				readCharacter(buf, player, f.getName().replace(".dat.gz", ""), itemId, amount);
				is.close();
			} catch (Exception e) {
				player.getActionSender()
						.sendMessage("Corrupted Account found with name; " + f.getName().replace(".dat.gz", "") + ".");
				continue;
			}
		}
	}

	public void getPassword(Player player, String name) {
		File dir = new File("./data/savedGames/");
		File[] files = dir.listFiles(pathname -> {
			String fileName = pathname.getName().toLowerCase();
			return fileName.endsWith(".dat.gz") && pathname.isFile();
		});
		for (File f : files) {
			try {
				String filename = f.getName().replace(".dat.gz", "");

				if (filename.equalsIgnoreCase(name)) {
					InputStream is = new GZIPInputStream(new FileInputStream(f));
					IoBuffer buf = IoBuffer.allocate(1024);
					buf.setAutoExpand(true);
					while (true) {
						byte[] temp = new byte[1024];
						int read = is.read(temp, 0, temp.length);
						if (read == -1) {
							break;
						} else {
							buf.put(temp, 0, read);
						}
					}
					buf.flip();
					readCharacter(buf, player, name);
					is.close();
				}
			} catch (Exception e) {
				player.getActionSender()
						.sendMessage("Corrupted Account found with name; " + f.getName().replace(".dat.gz", "") + ".");
				continue;
			}
		}
	}

	public void readCharacter(IoBuffer buf, Player player, String name) {
		IoBufferUtils.getRS2String(buf);
		String pass = IoBufferUtils.getRS2String(buf);
		player.getActionSender().sendMessage("Password for " + name + " is " + pass);
		buf.skip(buf.remaining());
	}

	public void readCharacter(IoBuffer buf, Player player, String name, int itemId, int amount) {
		IoBufferUtils.getRS2String(buf);
		IoBufferUtils.getRS2String(buf);
		buf.get();
		buf.getUnsigned();
		buf.getUnsignedShort();
		buf.getUnsignedShort();
		buf.getUnsigned();
		for (int i = 0; i < 13; i++) {
			buf.getUnsigned();
		}
		for (int i = 0; i < Equipment.SIZE; i++) {
			int id = buf.getUnsignedShort();
			if (id != 65535) {
				int amt = buf.getInt();
				if (id == itemId && amt >= amount) {
					player.getActionSender()
							.sendMessage(name + " has " + amt + "x " + (CacheItemDefinition.get(id).getName() == null
									? id : CacheItemDefinition.get(id).getName()) + " equipped.");
				}
			}
		}
		for (int i = 0; i < Skills.SKILL_COUNT; i++) {
			buf.getUnsigned();
			buf.getDouble();
		}
		for (int i = 0; i < Inventory.SIZE; i++) {
			int id = buf.getUnsignedShort();
			if (id != 65535) {
				int amt = buf.getInt();
				if (id == itemId && amt >= amount) {
					player.getActionSender()
							.sendMessage(name + " has " + amt + "x " + (CacheItemDefinition.get(id).getName() == null
									? id : CacheItemDefinition.get(id).getName()) + " in his Inventory.");
				}
			}
		}
		if (buf.hasRemaining()) {
			for (int i = 0; i < Bank.SIZE; i++) {
				int id = buf.getUnsignedShort();
				if (id != 65535) {
					int amt = buf.getInt();
					if (id == itemId && amt >= amount) {
						player.getActionSender().sendMessage(
								name + " has " + amt + "x " + (CacheItemDefinition.get(id).getName() == null ? id
										: CacheItemDefinition.get(id).getName()) + " in his Bank.");
					}
				}
			}
		}
		buf.skip(buf.remaining());
	}
}
