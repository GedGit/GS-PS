package org.rs2server.ls;

import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.rs2server.rs2.model.player.Player;

/**
 * The main executor of the login server.
 *  
 * @author Mack
 *
 */
public class LoginServer {

	private Channel session;

	private final Map<String, DataRequest> loginRequests = new HashMap<String, DataRequest>();
	private final Map<String, DataRequest> friendWorldRequests = new HashMap<String, DataRequest>();

	public int getLoginResponse(final String name, final String password) {
		if (isConnected()) {
			synchronized (loginRequests) {
				if (loginRequests.get(name) == null) {
					loginRequests.put(name, new DataRequest(13));
				}
				final DataRequest response = loginRequests.get(name);
				session.write(new LoginServerPacket(7).writeString(name).writeString(password));
				while (response.awaiting()) {
					try {
						loginRequests.wait();
					} catch (final InterruptedException e) {
						continue;
					}
				}
				loginRequests.remove(name);
				return response.getResponse();
			}
		} else {
			return 8;
		}
	}

	public int getFriendWorld(final String name) {
		if (!session.isConnected()) {
			return 0;
		}
		synchronized (friendWorldRequests) {
			if (friendWorldRequests.get(name) == null) {
				friendWorldRequests.put(name, new DataRequest(0));
			}
			final DataRequest response = friendWorldRequests.get(name);
			final LoginServerPacket statusRequest = new LoginServerPacket(5);
			statusRequest.writeString(name);
			session.write(statusRequest);
			while (response.awaiting()) {
				try {
					friendWorldRequests.wait();
				} catch (final InterruptedException e) {
					continue;
				}
			}
			friendWorldRequests.remove(name);
			return response.getResponse();
		}
	}

	public void register(final Player player) {
		session.write(new LoginServerPacket(2).writeString(player.getName()).writeShort(player.getIndex()));// .writeByte()
		int size = player.getPrivateChat().getFriends().size();
		final LoginServerPacket packet = new LoginServerPacket(4).writeString(player.getName()).writeInt(size);

		player.getActionSender().sendFriends();
		// player.getPrivateChat().getFriends().forEach(friend -> {
		// packet.writeLong(Utils.stringToLong(friend));
		// });
		session.write(packet);
	}

	public void read(final int opcode, final LoginServerPacket packet) {// Packets
		switch (opcode) {
		case 2: {
			final String name = packet.readString();
			final int response = packet.readByte();
			synchronized (loginRequests) {
				loginRequests.get(name).setResponse(response);
				loginRequests.notifyAll();
			}
		}
			break;
		case 3: {
			final String name = packet.readString();
			final int world = packet.readByte();
			synchronized (friendWorldRequests) {
				friendWorldRequests.get(name).setResponse(world);
				friendWorldRequests.notifyAll();
			}
		}
			break;
		/*
		 * case 4: { // final String nameOfSender = packet.readString(); final
		 * String nameOfRecipient = packet.readString(); final int messageSize =
		 * packet.readInt(); final byte[] message = new byte[messageSize - 8];
		 * for (int i = 0; i < message.length; i++) { message[i] = (byte)
		 * packet.readByte(); } final Player recipient =
		 * World.getPlayer(nameOfRecipient); if (recipient != null) { //
		 * recipient.getFriendsIgnores().sendMessage(Utils.stringToLong(
		 * nameOfRecipient), new ChatMessage(Huffman.readEncryptedMessage(150,
		 * stream))); // <<the // problem<<<<<<<<<<<<<<< } } break; case 5: {
		 * final String name = packet.readString(); final String nameOfFriend =
		 * packet.readString(); final int world = packet.readByte(); final
		 * Player player = World.getPlayer(name); Player friend =
		 * World.getPlayer(nameOfFriend); if (player != null) {
		 * player.getFriendsIgnores().changeFriendStatus(friend,
		 * World.containsPlayer(nameOfFriend)); }
		 * System.out.println("Received packet 5"); } break;
		 */
		}
	}

	public void sendPrivateMessage(final String nameOfSender, final String nameOfRecipient, final byte[] message) {
		final LoginServerPacket packet = new LoginServerPacket(6).writeString(nameOfSender).writeString(nameOfRecipient)
				.writeInt(message.length);
		for (final byte element : message) {
			packet.writeByte(element);
		}
		session.write(packet);
	}

	public void unregister(final Player player) {
		session.write(new LoginServerPacket(3).writeString(player.getName()).writeShort(player.getIndex()));
	}

	public void setChannel(final Channel session) {
		this.session = session;
	}

	public boolean isConnected() {
		return (session != null && session.isConnected());
	}

	private class DataRequest {

		private int response;
		private boolean waiting;

		public DataRequest(final int initialResponse) {
			response = initialResponse;
			waiting = true;
		}

		public int getResponse() {
			return response;
		}

		public void setResponse(final int response) {
			this.response = response;
			waiting = false;
		}

		public boolean awaiting() {
			return waiting;
		}

	}

}
