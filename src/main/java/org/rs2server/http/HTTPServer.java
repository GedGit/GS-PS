package org.rs2server.http;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.rs2server.rs2.Constants;


public class HTTPServer {
	
	public static void start() {
		Logger log = Logger.getLogger("");
		
		// Configure the server.
		ServerBootstrap bootstrap = new ServerBootstrap(
				new NioServerSocketChannelFactory(
						Executors.newCachedThreadPool(),
						Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HTTPServerFactory());

		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(8080));
		
		WorldList w1 = new WorldList(1, 33, "127.0.0.1", "Economy", Constants.PORT);//43594
		WorldList w2 = new WorldList(2, 33, "127.0.0.1", "Development", 40002);//40002
		/*WorldList w3 = new WorldList(3, 33, "127.0.0.1", "Economy 3", 40003);
		WorldList w4 = new WorldList(4, 33, "127.0.0.1", "Economy 4", 40004);
		WorldList w5 = new WorldList(5, 33, "127.0.0.1", "Economy 5", 40005);
		WorldList w6 = new WorldList(6, 33, "127.0.0.1", "Economy 6", 40006);
		WorldList w7 = new WorldList(7, 33, "127.0.0.1", "Economy 7", 40007);
		WorldList w8 = new WorldList(8, 1029, "127.0.0.1", "PvP", 40008);
		WorldList w9 = new WorldList(9, 536870913, "127.0.0.1", "Deadman Mode", 40009);
		WorldList w10 = new WorldList(10, 33554433, "127.0.0.1", "Tournament", 40010);*/
		//w2.setPlayercount(-1);
		/*w3.setPlayercount(-1);
		w4.setPlayercount(-1);
		w5.setPlayercount(-1);
		w6.setPlayercount(-1);
		w7.setPlayercount(-1);
		w8.setPlayercount(-1);
		w9.setPlayercount(-1);
		w10.setPlayercount(-1);*/
		WorldList.list.put(1, w1);
		WorldList.list.put(2, w2);
		/*WorldList.list.put(3, w3);
		WorldList.list.put(4, w4);
		WorldList.list.put(5, w5);
		WorldList.list.put(6, w6);
		WorldList.list.put(7, w7);
		WorldList.list.put(8, w8);
		WorldList.list.put(9, w9);
		WorldList.list.put(10, w10);*/
		log.info(Constants.SERVER_NAME+" WebServer has loaded sucessfuly.");
	}
}
