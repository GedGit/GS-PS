package org.rs2server.rs2.domain.model.player;

import lombok.Getter;
import lombok.Setter;
import org.rs2server.rs2.domain.dao.MongoEntity;

import java.util.List;
import java.util.Map;

/**
 * Represents an entity containing important persisting values
 * to do with in-game content.
 * @author twelve
 */
public final
@Setter @Getter
class PlayerSettingsEntity extends MongoEntity {

    /**
     * A config value for a player's attack priority.
     */
    private int playerAttackPriority;

	/**
	 * A config value for a npc's attack priority;
	 */
	private int npcAttackPriority;

    /**
     * A config value for a player's screen brightness.
     */
    private int playerScreenBrightness;

    /**
     * The first bank pin digit.
     */
    private int bankPinDigit1;

    /**
     * The second bank pin digit.
     */
    private int bankPinDigit2;
    /**
     * The third bank pin digit.
     */
    private int bankPinDigit3;
    /**
     * The fourth bank pin digit.
     */
    private int bankPinDigit4;
	/**
	 * The player's music volume
	 */
	private int playerMusicVolume;
	/**
	 * The player's sound effect volume
	 */
	private int playerSoundEffectVolume;
	/**
	 * The player's area sound volume
	 */
	private int playerAreaSoundVolume;
    /**
     * returns {@code true} if a player has a bank pin set.
     */
    private boolean bankSecured;
	/**
	 * The selected skill for XP Drops
	 */
	private int selectedSkill;

	/**
	 * The selected skill for XP Bar
	 */
	private int experienceBarSkill;

	/**
	 * The alignment for the XP Tracker
	 */
	private int alignment;

	/**
	 * The font size for the XP Tracker
	 */
	private int fontSize;

	private boolean placeHolderEnabled;

    private boolean petSpawned;
    private int petId;

    private Map<Integer, Integer> playerVariables;

	private List<Integer> lockedSkills;

	private boolean teleBlocked;
	private int teleBlockTimer;
	
	//public boolean zoomLock;

	public void decreaseTeleBlockTimer(int time) {
		setTeleBlockTimer(getTeleBlockTimer() - time);
	}

	public int getPlayerAttackPriority() {
		return playerAttackPriority;
	}

	public void setPlayerAttackPriority(int playerAttackPriority) {
		this.playerAttackPriority = playerAttackPriority;
	}

	public int getNpcAttackPriority() {
		return npcAttackPriority;
	}

	public void setNpcAttackPriority(int npcAttackPriority) {
		this.npcAttackPriority = npcAttackPriority;
	}

	public int getPlayerScreenBrightness() {
		return playerScreenBrightness;
	}

	public void setPlayerScreenBrightness(int playerScreenBrightness) {
		this.playerScreenBrightness = playerScreenBrightness;
	}

	public int getBankPinDigit1() {
		return bankPinDigit1;
	}

	public void setBankPinDigit1(int bankPinDigit1) {
		this.bankPinDigit1 = bankPinDigit1;
	}

	public int getBankPinDigit2() {
		return bankPinDigit2;
	}

	public void setBankPinDigit2(int bankPinDigit2) {
		this.bankPinDigit2 = bankPinDigit2;
	}

	public int getBankPinDigit3() {
		return bankPinDigit3;
	}

	public void setBankPinDigit3(int bankPinDigit3) {
		this.bankPinDigit3 = bankPinDigit3;
	}

	public int getBankPinDigit4() {
		return bankPinDigit4;
	}

	public void setBankPinDigit4(int bankPinDigit4) {
		this.bankPinDigit4 = bankPinDigit4;
	}

	public int getPlayerMusicVolume() {
		return playerMusicVolume;
	}

	public void setPlayerMusicVolume(int playerMusicVolume) {
		this.playerMusicVolume = playerMusicVolume;
	}

	public int getPlayerSoundEffectVolume() {
		return playerSoundEffectVolume;
	}

	public void setPlayerSoundEffectVolume(int playerSoundEffectVolume) {
		this.playerSoundEffectVolume = playerSoundEffectVolume;
	}

	public int getPlayerAreaSoundVolume() {
		return playerAreaSoundVolume;
	}

	public void setPlayerAreaSoundVolume(int playerAreaSoundVolume) {
		this.playerAreaSoundVolume = playerAreaSoundVolume;
	}

	public boolean isBankSecured() {
		return bankSecured;
	}

	public void setBankSecured(boolean bankSecured) {
		this.bankSecured = bankSecured;
	}

	public int getSelectedSkill() {
		return selectedSkill;
	}

	public void setSelectedSkill(int selectedSkill) {
		this.selectedSkill = selectedSkill;
	}

	public int getExperienceBarSkill() {
		return experienceBarSkill;
	}

	public void setExperienceBarSkill(int experienceBarSkill) {
		this.experienceBarSkill = experienceBarSkill;
	}

	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public boolean isPlaceHolderEnabled() {
		return placeHolderEnabled;
	}

	public void setPlaceHolderEnabled(boolean placeHolderEnabled) {
		this.placeHolderEnabled = placeHolderEnabled;
	}

	public boolean isPetSpawned() {
		return petSpawned;
	}

	public void setPetSpawned(boolean petSpawned) {
		this.petSpawned = petSpawned;
	}

	public int getPetId() {
		return petId;
	}

	public void setPetId(int petId) {
		this.petId = petId;
	}

	public Map<Integer, Integer> getPlayerVariables() {
		return playerVariables;
	}

	public void setPlayerVariables(Map<Integer, Integer> playerVariables) {
		this.playerVariables = playerVariables;
	}

	public List<Integer> getLockedSkills() {
		return lockedSkills;
	}

	public void setLockedSkills(List<Integer> lockedSkills) {
		this.lockedSkills = lockedSkills;
	}

	public boolean isTeleBlocked() {
		return teleBlocked;
	}

	public void setTeleBlocked(boolean teleBlocked) {
		this.teleBlocked = teleBlocked;
	}

	public int getTeleBlockTimer() {
		return teleBlockTimer;
	}

	public void setTeleBlockTimer(int teleBlockTimer) {
		this.teleBlockTimer = teleBlockTimer;
	}

	//public boolean isZoomLocked() {
	//	return zoomLock;
	//}

	//public void setZoomLocked(boolean zoomLocked) {
	//	this.zoomLock = zoomLocked;
	//}
}