package org.rs2server.rs2.model;

/**
 * Holds information about a single player's look.
 * @author Graham Edgecombe
 *
 */
public class Appearance {
	
	/**
	 * The gender.
	 */
	private int gender;
	
	/**
	 * The chest model.
	 */
	private int chest;
	
	/**
	 * The arms model.
	 */
	private int arms;
	
	/**
	 * The legs model.
	 */
	private int legs;
	
	/**
	 * The head model.
	 */
	private int head;
	
	/**
	 * The hands model.
	 */
	private int hands;
	
	/**
	 * The feet model.
	 */
	private int feet;
	
	/**
	 * The beard model.
	 */
	private int beard;
	
	/**
	 * The hair colour.
	 */
	private int hairColour;
	
	/**
	 * The torso colour.
	 */
	private int torsoColour;
	
	/**
	 * The legs colour.
	 */
	private int legColour;
	
	/**
	 * The feet colour.
	 */
	private int feetColour;
	
	/**
	 * The skin colour.
	 */
	private int skinColour;
	
	/**
	 * Creates the default player appearance.
	 */
	public Appearance() {
		gender = 0;
		head = 0;
		chest = 18;
		arms = 26;
		hands = 33;
		legs = 36;
		feet = 42;
		beard = 10;
		hairColour = 0;
		torsoColour = 0;
		legColour = 0;
		feetColour = 0;
		skinColour = 0;
	}
	
	/**
	 * Gets the look array, which is an array with 13 elements describing the
	 * look of a player.
	 * @return The look array.
	 */
	public int[] getLook() {
		return new int[] {
			gender,
			hairColour,
			torsoColour,
			legColour,
			feetColour,
			skinColour,
			head,
			chest,
			arms,
			hands,
			legs,
			feet,
			beard
		};
	}
	
	/**
	 * Sets the look array.
	 * @param look The look array.
	 * @throws IllegalArgumentException if the array length is not 13.
	 */
	public void setLook(int[] look) {
		if(look.length != 13) {
			throw new IllegalArgumentException("Array length must be 13.");
		}
		gender = look[0];
		head = look[6];
		chest = look[7];
		arms = look[8];
		hands = look[9];
		legs = look[10];
		feet = look[11];
		beard = look[12];
		hairColour = look[1];
		torsoColour = look[2];
		legColour = look[3];
		feetColour = look[4];
		skinColour = look[5];
	}
	
	public void setFemale() {
		head = 48; // Hair
        beard = 1000; // Beard
        chest = 57; // Torso
        arms = 64; // Arms
        hands = 68; // Bracelets
        legs = 77; // Legs
        feet = 80; // Shoes
		hairColour = 0;
		torsoColour = 0;
		legColour = 0;
		feetColour = 0;
		skinColour = 0;
        gender = 1;
	}
	
	public void setMale() {
		gender = 0;
		head = 0;
		chest = 18;
		arms = 26;
		hands = 33;
		legs = 36;
		feet = 42;
		beard = 10;
		hairColour = 0;
		torsoColour = 0;
		legColour = 0;
		feetColour = 0;
		skinColour = 0;
	}
	
	/**
	 * Sets the look array.
	 * @param look The look array.
	 * @throws IllegalArgumentException if the array length is not 13.
	 */
	public void setLook(int index, int look) {
		if(index > 13) {
			throw new IllegalArgumentException("Cannot be above 13.");
		}
		switch(index) {
		case 0:
			gender = look;
			break;
		case 1:
			hairColour = look;
			break;
		case 2:
			torsoColour = look;
			break;
		case 3:
			legColour = look;
			break;
		case 4:
			feetColour = look;
			break;
		case 5:
			skinColour = look;
			break;
		case 6:
			head = look;
			break;
		case 7:
			chest = look;
			break;
		case 8:
			arms = look;
			break;
		case 9:
			hands = look;
			break;
		case 10:
			legs = look;
			break;
		case 11:
			feet = look;
			break;
		case 12:
			beard = look;
			break;
		}
	}
	
	/**
	 * Gets the hair colour.
	 * @return The hair colour.
	 */
	public int getHairColour() {
		return hairColour;
	}
	
	/**
	 * Gets the torso colour.
	 * @return The torso colour.
	 */
	public int getTorsoColour() {
		return torsoColour;
	}
	
	/**
	 * Gets the leg colour.
	 * @return The leg colour.
	 */
	public int getLegColour() {
		return legColour;
	}
	
	/**
	 * Gets the feet colour.
	 * @return The feet colour.
	 */
	public int getFeetColour() {
		return feetColour;
	}
	
	/**
	 * Gets the skin colour.
	 * @return The skin colour.
	 */
	public int getSkinColour() {
		return skinColour;
	}
	
	/**
	 * Gets the gender.
	 * @return The gender.
	 */
	public int getGender() {
		return gender;
	}
	
	/**
	 * Gets the chest model.
	 * @return The chest model.
	 */
	public int getChest() {
		return chest;
	}
	
	/**
	 * Gets the arms model.
	 * @return The arms model.
	 */
	public int getArms() {
		return arms;
	}
	
	/**
	 * Gets the head model.
	 * @return The head model.
	 */
	public int getHead() {
		return head;
	}
	
	/**
	 * Gets the hands model.
	 * @return The hands model.
	 */
	public int getHands() {
		return hands;
	}
	
	/**
	 * Gets the legs model.
	 * @return The legs model.
	 */
	public int getLegs() {
		return legs;
	}
	
	/**
	 * Gets the feet model.
	 * @return The feet model.
	 */
	public int getFeet() {
		return feet;
	}
	
	/**
	 * Gets the beard model.
	 * @return The beard model.
	 */
	public int getBeard() {
		return beard;
	}

	public void setGender(int i) {
		this.gender = i;
	}


}
