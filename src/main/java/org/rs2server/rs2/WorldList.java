package org.rs2server.rs2;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.rs2server.rs2.model.World;
import org.rs2server.rs2.net.Packet;
import org.rs2server.rs2.net.PacketBuilder;

public class WorldList {

	static ServerSocket listener;
	static boolean running = true;

	// KEKEKEKEKEK
	public static void listen() {
		try {
			listener = new ServerSocket(80, 100);// 80
		} catch (IOException e) {
			e.printStackTrace();
		}
		new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("Setting up World List");
				while (running) {
					try {
						Socket s = listener.accept();
						System.out.println("from " + s.getRemoteSocketAddress());
						s.setSoTimeout(1);
						s.setTcpNoDelay(true);
						s.getOutputStream().write(block());
						s.getOutputStream().flush();
						s.getOutputStream().close();
						s.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}

		}).start();
	}

	// this is the list of worlds held in memory on the server
	// to be served to the client
	static WorldDef[] info;

	/*
	 * static { info = new WorldDef[12];//change this to noob info[0] = new
	 * WorldDef(1, 1, "localhost");//2 info[1] = new WorldDef(2, 1024,
	 * "localhost"); // 1024 is high risk info[2] = new WorldDef(3, 4,
	 * "localhost"); info[3] = new WorldDef(4, 8, "localhost");//8 info[4] = new
	 * WorldDef(5, 16, "localhost"); info[5] = new WorldDef(6, 32, "localhost");
	 * info[6] = new WorldDef(7, 64, "localhost"); info[7] = new WorldDef(8,
	 * 128, "localhost"); info[8] = new WorldDef(9, 256, "localhost"); info[9] =
	 * new WorldDef(10, 512, "localhost"); info[10] = new WorldDef(11, 2048,
	 * "localhost"); info[11] = new WorldDef(12, 4096, "localhost"); // doubt it
	 * goes bigger than this but idk }
	 */

	static {
		info = new WorldDef[2];// change this to noob
		info[0] = new WorldDef(1, 1, "47.196.87.183");
		info[1] = new WorldDef(2, 1, "104.196.182.248");
		// info[1] = new WorldDef(2, 0, "47.196.87.183");//1 536870913 33
		// info[2] = new WorldDef(3, 1029, "47.196.87.183");
		/*
		 * info[1] = new WorldDef(2, 1024, "localhost"); // 1024 is high risk
		 * info[2] = new WorldDef(3, 4, "localhost"); info[3] = new WorldDef(4,
		 * 8, "localhost");//8 info[4] = new WorldDef(5, 16, "localhost");
		 * info[5] = new WorldDef(6, 32, "localhost"); info[6] = new WorldDef(7,
		 * 64, "localhost"); info[7] = new WorldDef(8, 128, "localhost");
		 * info[8] = new WorldDef(9, 256, "localhost"); info[9] = new
		 * WorldDef(10, 512, "localhost"); info[10] = new WorldDef(11, 2048,
		 * "localhost"); info[11] = new WorldDef(12, 4096, "localhost");
		 */ // doubt it goes bigger than this but idk
	}

	// 26173 = High Risk PvP

	public static void main(String[] s) throws InterruptedException {
		WorldList.listen();// noob restarted the server lol its laready running
							// lol
		// we dont need the server rn
		while (true) {
			Thread.sleep(1000);
		}
	}

	protected static byte[] block() {
		PacketBuilder pb = new PacketBuilder();
		// BUILD INFO - so this is the info we send to the client
		pb.putShort(info.length); // world count
		// loop world definitions, send description of each one
		for (WorldDef w : info) {
			if (w.id == 1) {
				pb.putShort(w.id);// w.id
				pb.putInt(w.mask);
				pb.putRS2String(w.ip);
				pb.putRS2String(Constants.SERVER_NAME);
				pb.put((byte) 2); // country - idx of the countries array client
									// sided
				pb.putShort(World.getWorld().getPlayers().size());
			}
			if (w.id == 2) {
				pb.putShort(2);// w.id
				pb.putInt(w.mask);
				pb.putRS2String(w.ip);
				pb.putRS2String(Constants.SERVER_NAME);
				pb.put((byte) 2); // country - idx of the countries array client
									// sided
				pb.putShort(World.getWorld().getPlayers().size());
			}
			if (w.id == 3) {
				pb.putShort(3);// w.id
				pb.putInt(w.mask);
				pb.putRS2String(w.ip);
				pb.putRS2String(Constants.SERVER_NAME);// Beta
				pb.put((byte) 2); // country - idx of the countries array client
									// sided
				pb.putShort(0);
			}
		}
		Packet flipped = pb.toPacket();
		// kk run this shit lets c

		// FORMAT - flipping to packet form auto formats the backing array
		return flipped.getPayload().array();
	}
}