package org.rs2server.rs2.domain.model.player;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Player skills Slayer entity.
 *
 * @author tommo
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final @Setter @Getter class PlayerSkillSlayerEntity {

	/**
	 * The name of the slayer task which is blocked in slot 1.
	 */
	private String blockedTask1;

	/**
	 * The name of the slayer task which is blocked in slot 2.
	 */
	private String blockedTask2;

	/**
	 * The name of the slayer task which is blocked in slot 3.
	 */
	private String blockedTask3;

	/**
	 * The name of the slayer task which is blocked in slot 4.
	 */
	private String blockedTask4;

	/**
	 * The name of the slayer task which is blocked in slot 5.
	 */
	private String blockedTask5;

	/**
	 * If the player has a task extension for Dark Beasts.
	 */
	private boolean extendTaskDarkBeast;

	/**
	 * If the player has a task extension for Ankous.
	 */
	private boolean extendTaskAnkous;

	/**
	 * If the player has a task extension for Suqahs.
	 */
	private boolean extendTaskSuqah;

	/**
	 * If the player has a task extension for Black Dragons.
	 */
	private boolean extendTaskBlackDragon;

	/**
	 * If the player has a task extension for Metal Dragons (only
	 * bronze/iron/steel).
	 */
	private boolean extendTaskMetalDragons;

	/**
	 * If the player has a task extension for Mithril Dragons.
	 */
	private boolean extendTaskMithrilDragon;

	/**
	 * If the player has a task extension for Spiritual Creatures.
	 */
	private boolean extendTaskSpiritualCreatures;

	/**
	 * If the player has a task extension for Aviansies.
	 */
	private boolean extendTaskAviansie;

	/**
	 * If the player has a task extension for Greater Demons.
	 */
	private boolean extendTaskGreaterDemon;

	/**
	 * If the player has a task extension for Black Demons.
	 */
	private boolean extendTaskBlackDemon;

	/**
	 * If the player has a task extension for Bloodvelds.
	 */
	private boolean extendTaskBloodveld;

	/**
	 * If the player has a task extension for Aberrant Spectres.
	 */
	private boolean extendTaskAberrantSpectre;

	/**
	 * If the player has a task extension for Cave Horrors.
	 */
	private boolean extendTaskCaveHorror;

	/**
	 * If the player has a task extension for Dust Devils.
	 */
	private boolean extendTaskDustDevil;

	/**
	 * If the player has a task extension for Skeletal Wyverns.
	 */
	private boolean extendTaskSkeletalWyvern;

	/**
	 * If the player has a task extension for Gargoyles.
	 */
	private boolean extendTaskGargoyle;

	/**
	 * If the player has a task extension for Nechryaels.
	 */
	private boolean extendTaskNechryael;

	/**
	 * If the player has a task extension for Abyssal Demons.
	 */
	private boolean extendTaskAbyssalDemon;

	/**
	 * If the player has a task extension for Cave Krakens.
	 */
	private boolean extendTaskCaveKraken;

	/**
	 * If the player has bought the Slayer Helm unlock.
	 */
	private boolean unlockedSlayerHelm;

	/**
	 * if the player has bought the red slayer helm unlock.
	 */
	private boolean unlockedRedSlayerHelm;

	/**
	 * If the player has bought the Mithril Dragons task unlock.
	 */
	private boolean unlockedMithrilDragonsTask;

	/**
	 * If the player has bought the Aviansies task unlock.
	 */
	private boolean unlockedAviansiesTask;

	/**
	 * If the player has bought the boss tasks unlock.
	 */
	private boolean unlockedBossTasks;

	public String getBlockedTask1() {
		return blockedTask1;
	}

	public void setBlockedTask1(String blockedTask1) {
		this.blockedTask1 = blockedTask1;
	}

	public String getBlockedTask2() {
		return blockedTask2;
	}

	public void setBlockedTask2(String blockedTask2) {
		this.blockedTask2 = blockedTask2;
	}

	public String getBlockedTask3() {
		return blockedTask3;
	}

	public void setBlockedTask3(String blockedTask3) {
		this.blockedTask3 = blockedTask3;
	}

	public String getBlockedTask4() {
		return blockedTask4;
	}

	public void setBlockedTask4(String blockedTask4) {
		this.blockedTask4 = blockedTask4;
	}

	public String getBlockedTask5() {
		return blockedTask5;
	}

	public void setBlockedTask5(String blockedTask5) {
		this.blockedTask5 = blockedTask5;
	}

	public boolean isExtendTaskDarkBeast() {
		return extendTaskDarkBeast;
	}

	public void setExtendTaskDarkBeast(boolean extendTaskDarkBeast) {
		this.extendTaskDarkBeast = extendTaskDarkBeast;
	}

	public boolean isExtendTaskAnkous() {
		return extendTaskAnkous;
	}

	public void setExtendTaskAnkous(boolean extendTaskAnkous) {
		this.extendTaskAnkous = extendTaskAnkous;
	}

	public boolean isExtendTaskSuqah() {
		return extendTaskSuqah;
	}

	public void setExtendTaskSuqah(boolean extendTaskSuqah) {
		this.extendTaskSuqah = extendTaskSuqah;
	}

	public boolean isExtendTaskBlackDragon() {
		return extendTaskBlackDragon;
	}

	public void setExtendTaskBlackDragon(boolean extendTaskBlackDragon) {
		this.extendTaskBlackDragon = extendTaskBlackDragon;
	}

	public boolean isExtendTaskMetalDragons() {
		return extendTaskMetalDragons;
	}

	public void setExtendTaskMetalDragons(boolean extendTaskMetalDragons) {
		this.extendTaskMetalDragons = extendTaskMetalDragons;
	}

	public boolean isExtendTaskMithrilDragon() {
		return extendTaskMithrilDragon;
	}

	public void setExtendTaskMithrilDragon(boolean extendTaskMithrilDragon) {
		this.extendTaskMithrilDragon = extendTaskMithrilDragon;
	}

	public boolean isExtendTaskSpiritualCreatures() {
		return extendTaskSpiritualCreatures;
	}

	public void setExtendTaskSpiritualCreatures(boolean extendTaskSpiritualCreatures) {
		this.extendTaskSpiritualCreatures = extendTaskSpiritualCreatures;
	}

	public boolean isExtendTaskAviansie() {
		return extendTaskAviansie;
	}

	public void setExtendTaskAviansie(boolean extendTaskAviansie) {
		this.extendTaskAviansie = extendTaskAviansie;
	}

	public boolean isExtendTaskGreaterDemon() {
		return extendTaskGreaterDemon;
	}

	public void setExtendTaskGreaterDemon(boolean extendTaskGreaterDemon) {
		this.extendTaskGreaterDemon = extendTaskGreaterDemon;
	}

	public boolean isExtendTaskBlackDemon() {
		return extendTaskBlackDemon;
	}

	public void setExtendTaskBlackDemon(boolean extendTaskBlackDemon) {
		this.extendTaskBlackDemon = extendTaskBlackDemon;
	}

	public boolean isExtendTaskBloodveld() {
		return extendTaskBloodveld;
	}

	public void setExtendTaskBloodveld(boolean extendTaskBloodveld) {
		this.extendTaskBloodveld = extendTaskBloodveld;
	}

	public boolean isExtendTaskAberrantSpectre() {
		return extendTaskAberrantSpectre;
	}

	public void setExtendTaskAberrantSpectre(boolean extendTaskAberrantSpectre) {
		this.extendTaskAberrantSpectre = extendTaskAberrantSpectre;
	}

	public boolean isExtendTaskCaveHorror() {
		return extendTaskCaveHorror;
	}

	public void setExtendTaskCaveHorror(boolean extendTaskCaveHorror) {
		this.extendTaskCaveHorror = extendTaskCaveHorror;
	}

	public boolean isExtendTaskDustDevil() {
		return extendTaskDustDevil;
	}

	public void setExtendTaskDustDevil(boolean extendTaskDustDevil) {
		this.extendTaskDustDevil = extendTaskDustDevil;
	}

	public boolean isExtendTaskSkeletalWyvern() {
		return extendTaskSkeletalWyvern;
	}

	public void setExtendTaskSkeletalWyvern(boolean extendTaskSkeletalWyvern) {
		this.extendTaskSkeletalWyvern = extendTaskSkeletalWyvern;
	}

	public boolean isExtendTaskGargoyle() {
		return extendTaskGargoyle;
	}

	public void setExtendTaskGargoyle(boolean extendTaskGargoyle) {
		this.extendTaskGargoyle = extendTaskGargoyle;
	}

	public boolean isExtendTaskNechryael() {
		return extendTaskNechryael;
	}

	public void setExtendTaskNechryael(boolean extendTaskNechryael) {
		this.extendTaskNechryael = extendTaskNechryael;
	}

	public boolean isExtendTaskAbyssalDemon() {
		return extendTaskAbyssalDemon;
	}

	public void setExtendTaskAbyssalDemon(boolean extendTaskAbyssalDemon) {
		this.extendTaskAbyssalDemon = extendTaskAbyssalDemon;
	}

	public boolean isExtendTaskCaveKraken() {
		return extendTaskCaveKraken;
	}

	public void setExtendTaskCaveKraken(boolean extendTaskCaveKraken) {
		this.extendTaskCaveKraken = extendTaskCaveKraken;
	}

	public boolean isUnlockedRedSlayerHelm() {
		return unlockedRedSlayerHelm;
	}

	public void setUnlockedRedSlayerHelm(boolean unlockedRedSlayerHelm) {
		this.unlockedRedSlayerHelm = unlockedRedSlayerHelm;
	}

	public boolean isUnlockedSlayerHelm() {
		return unlockedSlayerHelm;
	}

	public void setUnlockedSlayerHelm(boolean unlockedSlayerHelm) {
		this.unlockedSlayerHelm = unlockedSlayerHelm;
	}

	public boolean isUnlockedMithrilDragonsTask() {
		return unlockedMithrilDragonsTask;
	}

	public void setUnlockedMithrilDragonsTask(boolean unlockedMithrilDragonsTask) {
		this.unlockedMithrilDragonsTask = unlockedMithrilDragonsTask;
	}

	public boolean isUnlockedAviansiesTask() {
		return unlockedAviansiesTask;
	}

	public void setUnlockedAviansiesTask(boolean unlockedAviansiesTask) {
		this.unlockedAviansiesTask = unlockedAviansiesTask;
	}

	public boolean isUnlockedBossTasks() {
		return unlockedBossTasks;
	}

	public void setUnlockedBossTasks(boolean unlockedBossTasks) {
		this.unlockedBossTasks = unlockedBossTasks;
	}
}