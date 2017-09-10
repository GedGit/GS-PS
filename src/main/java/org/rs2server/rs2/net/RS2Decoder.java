package org.rs2server.rs2.net;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.rs2server.rs2.Constants;
import org.rs2server.rs2.net.Packet.Type;

/**
 * Game protocol decoding class.
 * 
 * @author Graham Edgecombe
 *
 */
public class RS2Decoder extends CumulativeProtocolDecoder {

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
		int opcode = in.get() & 0xFF;
		int size = Constants.PACKET_SIZES[opcode];
		if (size == -1) {
			if (in.remaining() >= 1) {
				size = in.get() & 0xFF;
			} else {
				return false;
			}
		} else if (size == -2) {
			if (in.remaining() >= 2) {
				size = in.getShort() & 0xFFFF;
			} else {
				return false;
			}
		} else if (size == -3) {
			if (in.remaining() >= 1) {
				size = in.remaining();
			} else {
				return false;
			}
		}

		if (in.remaining() >= size) {
			byte[] data = new byte[size];
			in.get(data);
			IoBuffer payload = IoBuffer.allocate(data.length);
			payload.put(data);
			payload.flip();
			out.write(new Packet(opcode, Type.FIXED, payload));
			return true;
		} else {
			if (Constants.DEBUG)
				System.out.println("Did not go through with " + opcode + " - " + in.remaining() + " - " + size);
		}
		return false;
	}

}
