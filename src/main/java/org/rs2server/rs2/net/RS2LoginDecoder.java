package org.rs2server.rs2.net;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.rs2server.Server;
import org.rs2server.cache.CacheManager;
import org.rs2server.rs2.model.World;
import org.rs2server.rs2.model.player.PlayerDetails;
import org.rs2server.rs2.util.IoBufferUtils;
import org.rs2server.rs2.util.TextUtils;
import org.rs2server.rs2.util.XTEA;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * Login protocol decoding class.
 * 
 * @author Graham Edgecombe
 *
 */
public class RS2LoginDecoder extends CumulativeProtocolDecoder {

	/**
	 * Logger instance.
	 */
	private static final Logger logger = Logger.getLogger(RS2LoginDecoder.class.getName());

	/**
	 * Opcode stage.
	 */
	public static final int STATE_OPCODE = 0;

	/**
	 * Login stage.
	 */
	public static final int STATE_LOGIN = 1;

	/**
	 * Precrypted stage.
	 */
	public static final int STATE_PRECRYPTED = 2;

	/**
	 * Crypted stage.
	 */
	public static final int STATE_CRYPTED = 3;

	/**
	 * Update stage.
	 */
	public static final int STATE_UPDATE = -1;

	/**
	 * Game opcode.
	 */
	public static final int OPCODE_GAME = 14;

	/**
	 * Update opcode.
	 */
	public static final int OPCODE_UPDATE = 15;

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		int state = (Integer) session.getAttribute("state", STATE_OPCODE);
		switch (state) {
		case STATE_UPDATE:
			while (in.remaining() >= 4) {
				final int opcode = in.get() & 0xFF;
				final int container = in.get() & 0xFF;
				final int file = in.getShort() & 0xFFFF;

				switch (opcode) {
				case 0:
				case 1:
					Packet response = CacheManager.generate(opcode, container, file);
					// Opening client connection
					if (response != null)
						session.write(response);
					break;
				}
			}
			return in.remaining() == 0;
		case STATE_OPCODE:
			if (in.remaining() > 0) {
				int opcode = in.get() & 0xFF;
				switch (opcode) {
				case OPCODE_UPDATE:
					session.write(new PacketBuilder().put((byte) 0).toPacket());
					if (in.remaining() >= 4) {
						int version = in.getInt();
						if (version != 83 && version != Server.VERSION) {
							System.out.println("Invalid version: " + version);
							session.write(new PacketBuilder().put((byte) 6).toPacket());
							session.close(false);
							in.rewind();
							break;
						}
						session.setAttribute("state", STATE_UPDATE);
						return true;
					}
					in.rewind();
					break;
				case OPCODE_GAME:
					if (in.remaining() == 0)
						session.write(new PacketBuilder().put((byte) 0).toPacket());
					if (in.remaining() >= 3) {
						int type = in.get();
						int size = in.getShort() & 0xFFFF;
						if (type != 16 && type != 18 || size > in.remaining()) {
							session.close(false);
							return false;
						}

						int version = in.getInt();
						if (version != 83 && version != Server.VERSION) {
							System.out.println("Invalid version: " + version);
							session.close(false);
							return false;
						}

						in.skip(4);

						int[] keys = new int[4];
						for (int i = 0; i < keys.length; i++) {
							keys[i] = in.getInt();
						}
						if (in.remaining() >= 1) {
							in.skip(8);
							String password = IoBufferUtils.getRS2String(in);
							String UUID = TextUtils.rot47(IoBufferUtils.getRS2String(in))
									.replace("To be filled by O.E.M.", "");
							byte[] block = new byte[in.remaining()];
							in.get(block);
							ByteBuffer xtea = ByteBuffer.wrap(XTEA.decrypt(keys, block, 0, block.length));

							String username = IoBufferUtils.getRS2String(xtea);

							int returnCode = 2;
							if (returnCode == 2) {
								returnCode = Server.getLoginServer().getLoginResponse(username, password);
							}

							for (int i = 0; i < xtea.remaining(); i++) {
								xtea.get();
							}
							int loginType = xtea.get() & 0xFF;

							switch (loginType) {
							case 0:
								session.getFilterChain().remove("protocol");
								session.getFilterChain().addFirst("protocol",
										new ProtocolCodecFilter(RS2CodecFactory.GAME));

								PlayerDetails pd = new PlayerDetails(session, username, password, version, null, null,
										UUID);
								World.getWorld().load(pd);
								break;
							case 3:
								logger.warning("Login type 3 by " + username + "... Skipping");
								in.skip(8);
								break;
							default:
								logger.warning("Login type unknown: " + loginType);
								break;
							}
						}
					}
					break;
				}
				in.rewind();
			}
			break;
		}
		return false;
	}

}
