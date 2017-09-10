package org.rs2server.http;

import java.util.concurrent.ConcurrentHashMap;

public class WorldList {

	public static final ConcurrentHashMap<Integer, WorldList> list = new ConcurrentHashMap<Integer, WorldList>();

	private String ip;
	private String name;
	private boolean members;
	private int country;
	private int world;
	private int playercount;
	public int mask;
	public int port;

	public WorldList(int world, int mask, String ip, String name, int port) {
		this.setWorld(world);
		this.setMask(mask);
		this.setName(name);
		this.setIp(ip);
		this.setPlayercount(0);
		this.setPort(port);
	}

	public String getIp() {
		return ip;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public boolean isMembers() {
		return members;
	}

	public void setMembers(boolean members) {
		this.members = members;
	}

	public int getPlayercount() {
		return playercount;
	}

	public void setPlayercount(int playercount) {
		this.playercount = playercount;
	}

	public int getWorld() {
		return world;
	}

	public void setWorld(int world) {
		this.world = world;
	}

	public int getCountry() {
		return country;
	}

	public void setCountry(int country) {
		this.country = country;
	}

	public int getMask() {
		return mask;
	}

	private void setMask(int mask) {
		this.mask = mask;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
