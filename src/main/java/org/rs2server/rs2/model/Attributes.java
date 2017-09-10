package org.rs2server.rs2.model;


/**
 * 
 * @author Nine
 * 
 */
public class Attributes {
	
	private Object[] attributes = new Object[10]; // Should match up with how many attributes we have
												 // TODO Auto generate this
	
	public Attributes() {
	}
	
	public Object getAttribute(int key) {
		if (attributes[key] == null)
			attributes[key] = false;
		return attributes[key];
	}
	
	public void setAttribute(int key, Object value) {
		attributes[key] = value;
	}

	public static class A { // Player attributes
		public static final int REGULAR_FOLLOWING = 0; // temp
		public static final int POST_PROCESS_FOR_FOLLOWING = 1;
		public static final int CLAN_WARS_TROLL = 2;
	}
	
	public static class AN { // NPC attributes
		public static final int BOSS_TRANSFORMING = 2;
		
	}
}
