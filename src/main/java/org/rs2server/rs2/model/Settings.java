package org.rs2server.rs2.model;

public class Settings {

	/**
	 * Withdraw as notes flag.
	 */
	private boolean withdrawAsNotes = false;

	/**
	 * Swapping flag.
	 */
	private boolean swapping = true;

	/**
	 * The player's brightness setting.
	 */
	private int brightnessSetting = 2;

	/**
	 * The two mouse buttons flag.
	 */
	private boolean twoMouseButtons = true;

	/**
	 * The chat effects flag.
	 */
	private boolean chatEffects = true;

	/**
	 * The split private chat flag.
	 */
	private boolean splitPrivateChat = false;

	/**
	 * The accept aid flag.
	 */
	private boolean acceptAid = false;

	/**
	 * The auto retaliate flag.
	 */
	private boolean autoRetaliate = true;

	private boolean lootSharing = false;

	private int hunterbhKills;

	private int roguebhKills;

	private boolean[] strongholdChests = new boolean[4];

	private int pestPoints;

	private int chances;

	private int position;

	private int playerClickPriority = 3;

	private int npcClickPriority = 3;

	private int lastWithdrawnValue;

	private boolean quicksActivated;

	private long lastTeleport = -3000;

	private int rfdState = 1;

	private int fcState = 1;

	private int maState = 1;

	private long magicDelay;

	private boolean completedMageArena;

	private boolean muted;

	private int bestRFDState;
	private long lastAltarPrayer = -3000;
	private long lastChatMessage;
	private String lastMessage;
	
	private boolean expCounter;

	/**
	 * Sets the withdraw as notes flag.
	 * 
	 * @param withdrawAsNotes
	 *            The flag.
	 */
	public void setWithdrawAsNotes(boolean withdrawAsNotes) {
		this.withdrawAsNotes = withdrawAsNotes;
	}

	/**
	 * Sets the swapping flag.
	 * 
	 * @param swapping
	 *            The swapping flag.
	 */
	public void setSwapping(boolean swapping) {
		this.swapping = swapping;
	}

	/**
	 * Checks if the player is withdrawing as notes.
	 * 
	 * @return The withdrawing as notes flag.
	 */
	public boolean isWithdrawingAsNotes() {
		return withdrawAsNotes;
	}

	/**
	 * Checks if the player is swapping.
	 * 
	 * @return The swapping flag.
	 */
	public boolean isSwapping() {
		return swapping;
	}

	/**
	 * @return the brightnessSetting
	 */
	public int getBrightnessSetting() {
		return brightnessSetting;
	}

	/**
	 * @param brightnessSetting
	 *            the brightnessSetting to set
	 */
	public void setBrightnessSetting(int brightnessSetting) {
		this.brightnessSetting = brightnessSetting;
	}

	/**
	 * @return the twoMouseButtons
	 */
	public boolean twoMouseButtons() {
		return twoMouseButtons;
	}

	/**
	 * @param twoMouseButtons
	 *            the twoMouseButtons to set
	 */
	public void setTwoMouseButtons(boolean twoMouseButtons) {
		this.twoMouseButtons = twoMouseButtons;
	}

	/**
	 * @return the chatEffects
	 */
	public boolean chatEffects() {
		return chatEffects;
	}

	/**
	 * @param chatEffects
	 *            the chatEffects to set
	 */
	public void setChatEffects(boolean chatEffects) {
		this.chatEffects = chatEffects;
	}

	/**
	 * @return the splitPrivateChat
	 */
	public boolean splitPrivateChat() {
		return splitPrivateChat;
	}

	/**
	 * @param splitPrivateChat
	 *            the splitPrivateChat to set
	 */
	public void setSplitPrivateChat(boolean splitPrivateChat) {
		this.splitPrivateChat = splitPrivateChat;
	}

	/**
	 * @return the acceptAid
	 */
	public boolean isAcceptingAid() {
		return acceptAid;
	}

	/**
	 * @param acceptAid
	 *            the acceptAid to set
	 */
	public void setAcceptAid(boolean acceptAid) {
		this.acceptAid = acceptAid;
	}

	/**
	 * @return the autoRetaliate
	 */
	public boolean isAutoRetaliating() {
		return autoRetaliate;
	}

	/**
	 * @param autoRetaliate
	 *            the autoRetaliate to set
	 */
	public void setAutoRetaliate(boolean autoRetaliate) {
		this.autoRetaliate = autoRetaliate;
	}

	public void incrementBHKills(boolean wasTarget) {
		if (wasTarget)
			hunterbhKills++;
		else
			roguebhKills++;
	}

	public int getRogueBHKills() {
		return roguebhKills;
	}

	public int getHunterBHKills() {
		return hunterbhKills;
	}

	public void setRogueBHKills(int kills) {
		this.roguebhKills = kills;
	}

	public void setHunterBHKills(int kills) {
		this.hunterbhKills = kills;
	}

	public boolean[] getStrongholdChest() {
		return strongholdChests;
	}

	public void increasePestPoints(int points) {
		this.pestPoints += points;
	}

	public void setPestPoints(int points) {
		this.pestPoints = points;
	}

	public int getPestPoints() {
		return pestPoints;
	}

	public int getChances() {
		return chances;
	}

	public void incChances() {
		chances++;

	}

	public void setChances(int chances) {
		this.chances = chances;

	}

	public void decChances() {
		chances--;

	}

	public boolean isLootsharing() {
		return lootSharing;
	}

	public void setLootSharing(boolean sharing) {
		this.lootSharing = sharing;
	}

	public int getPlayerClickPriority() {
		return playerClickPriority;
	}

	public void setPlayerClickPriority(int i) {
		this.playerClickPriority = i;
	}

	public int getLastWithdrawnValue() {
		return lastWithdrawnValue;
	}

	public void setLastWithdrawnValue(int i) {
		this.lastWithdrawnValue = i;
	}

	public void setXPPosition(int position) {
		this.position = position;
	}

	public int getXPPosition() {
		return position;
	}

	public void setQuicksActivated(boolean b) {
		this.quicksActivated = b;
	}

	public boolean getQuicksActive() {
		return quicksActivated;
	}

	public long getLastTeleport() {
		return System.currentTimeMillis() - lastTeleport;
	}

	public void setLastTeleport(long currentTimeMillis) {
		this.lastTeleport = currentTimeMillis;
	}

	public int getRFDState() {
		return rfdState;
	}

	public void setRFDState(int state) {
		this.rfdState = state;
	}

	public void setMagicDelay(long currentTimeMillis) {
		this.magicDelay = currentTimeMillis;
	}

	public long getMagicDelay() {
		return magicDelay;
	}

	public int getFightCaveState() {
		return fcState;
	}

	public void setFightCaveState(int i) {
		this.fcState = i;
	}

	public int getMageArenaState() {
		return maState;
	}

	public void setMageArenaState(int i) {
		this.maState = i;
	}

	public boolean completedMageArena() {
		return completedMageArena;
	}

	public void setCompletedMageArena(boolean b) {
		this.completedMageArena = b;
	}

	public boolean isMuted() {
		return muted;
	}

	public void setMuted(boolean b) {
		this.muted = b;
	}

	public int getNpcClickPriority() {
		return npcClickPriority;
	}

	public void setNpcClickPriority(int npcClickPriority) {
		this.npcClickPriority = npcClickPriority;
	}

	public void setBestRFDState(int i) {
		this.bestRFDState = i;
	}

	public int getBestRFDState() {
		return bestRFDState;
	}

	public long getLastAltarPrayer() {
		return System.currentTimeMillis() - lastAltarPrayer;
	}

	public void setLastAltarPrayer(long lastAltarPrayer) {
		this.lastAltarPrayer = lastAltarPrayer;
	}

	public void setLastChatMessage(long lastChatMessage) {
		this.lastChatMessage = lastChatMessage;
	}

	public long getLastChatMessage() {
		return lastChatMessage;
	}

	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}

	public String getLastMessage() {
		return lastMessage;
	}

	public boolean wornItems = true;

	public boolean isEnablingWornItems() {
		return wornItems;
	}

	public void setEnablingWornItems(boolean wornItems) {
		this.wornItems = wornItems;
	}

	public boolean isExpCounterOpen() {
		return this.expCounter;
	}

	public void setExpCounterOpen(boolean open) {
		this.expCounter = open;
	}
}
