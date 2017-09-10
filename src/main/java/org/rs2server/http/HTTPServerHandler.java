package org.rs2server.http;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.setContentLength;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.USER_AGENT;
import static org.jboss.netty.handler.codec.http.HttpMethod.GET;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.OK;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.util.CharsetUtil;
import org.rs2server.rs2.net.OutputStream;

import java.util.Iterator;
import java.util.Map.Entry;

import org.jboss.netty.buffer.ChannelBuffer;

public class HTTPServerHandler extends SimpleChannelUpstreamHandler {

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		Object msg = e.getMessage();
		// if (msg instanceof HttpRequest) {
		handleHttpRequest(ctx, (HttpRequest) msg);
		// }
	}

	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req) throws Exception {
		// Allow only GET methods.
		System.out.println(" " + ctx.getChannel().getLocalAddress().toString());

		if (req.getMethod() == GET) {
			if (req.getHeader(USER_AGENT).startsWith("Java")) {
				HttpResponse res = new DefaultHttpResponse(HTTP_1_1, OK);
				OutputStream out = new OutputStream();
				out.writeShort(WorldList.list.size());
				for (Iterator<Entry<Integer, WorldList>> it$ = WorldList.list.entrySet().iterator(); it$.hasNext();) {
					WorldList w = it$.next().getValue();
					out.writeShort(w.getWorld());
					out.writeInt(w.getMask());
					out.writeString(w.getIp());
					out.writeString(w.getName());
					out.writeByte((byte) 0);
					out.writeShort(w.getPlayercount());
				}
				ChannelBuffer content = ChannelBuffers.buffer(500);
				content.writeInt(out.getBuffer().length);
				content.writeBytes(out.getBuffer());
				res.setHeader(CONTENT_TYPE, "text/html; charset=UTF-8");
				res.setContent(content);
				sendHttpResponse(ctx, req, res);
			}
		}
	}

	private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
		// Generate an error page if response status code is not OK (200).
		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
			setContentLength(res, res.getContent().readableBytes());
		}

		// Send the response and close the connection if necessary.
		ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		e.getChannel().close();
	}

}
