package org.rs2server.rs2.model.player;

/**
 * @author Twelve
 */
public final class ExperienceDropdown {

	public static final int ALIGN_RIGHT = 0;
	public static final int ALIGN_CENTER = 0x1;
	public static final int ALIGN_LEFT = 0x2;

	public static final int SMALLEST_FONT = 0;
	public static final int MEDIUM_FONT = 0x4;
	public static final int LARGE_FONT = 0x8;

	private static final int SELECTED_SKILL_BIT = 22;
	private static final int SELECTED_BAR_BIT = 27;

	private final Player player;

	public ExperienceDropdown(Player player) {
		this.player = player;
	}

	public final int getSelectedValue() {
		return player.getDatabaseEntity().getPlayerSettings().getSelectedSkill() << SELECTED_SKILL_BIT;
	}

	public final int getExperienceBarValue() {
		return player.getDatabaseEntity().getPlayerSettings().getExperienceBarSkill() << SELECTED_BAR_BIT;
	}

	@Override
	public final int hashCode() {// ooo gonna try something dunno if its correct
									// but we will see
		return getSelectedValue() | getExperienceBarValue()
				| player.getDatabaseEntity().getPlayerSettings().getAlignment()
				| player.getDatabaseEntity().getPlayerSettings().getFontSize();
	}

	public final ExperienceDropdown setSelectedSkill(int selectedSkill) {
		player.getDatabaseEntity().getPlayerSettings().setSelectedSkill(selectedSkill);
		return this;
	}

	public final ExperienceDropdown setExperienceBarSkill(int experienceBarSkill) {
		player.getDatabaseEntity().getPlayerSettings().setExperienceBarSkill(experienceBarSkill);
		return this;
	}

	public final ExperienceDropdown setAlignment(int alignment) {
		player.getDatabaseEntity().getPlayerSettings().setAlignment(alignment);
		return this;
	}

	public final ExperienceDropdown setFontSize(int fontSize) {
		player.getDatabaseEntity().getPlayerSettings().setFontSize(fontSize);
		return this;
	}
}
