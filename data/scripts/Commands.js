load("nashorn:mozilla_compat.js");

importPackage(org.rs2server.rs2.model)
importPackage(org.rs2server.rs2.model.container)
importPackage(org.rs2server.rs2.util)
importPackage(org.rs2server.rs2.model.skills)
importPackage(java.util)

function modernspellbookcmd(player) {
	if (player.getBountyHunter() != null) {
		player.getActionSender().sendMessage(
				"You can't do that while in Bounty Hunter.");
		return;
	}
	player.getActionSender().sendConfig(439, 0);
	player.getCombatState().setSpellBook(MagicCombatAction.SpellBook.MODERN_MAGICS.getSpellBookId());
	player.getActionSender().sendMessage("Your magic book has been changed to the Regular spellbook.");
}

function ancientspellbookcmd(player) {
	if (player.getBountyHunter() != null) {
		player.getActionSender().sendMessage(
				"You can't do that while in Bounty Hunter.");
		return;
	}
	player.getActionSender().sendConfig(439, 1);
	player.getCombatState().setSpellBook(MagicCombatAction.SpellBook.ANCIENT_MAGICKS.getSpellBookId());
	player.getActionSender().sendMessage("Your magic book has been changed to the Ancient spellbook.");
}

function openbankcmd(player) {
	Bank.open(player);	
}

function debugcmd(player) {
	player.setDebugMode(!player.isDebugMode());
	player.sendMessage("Debug: " + player.isDebugMode());
}

function resetbankcmd(player) {
	player.getBank().clear();
}

function slayerlogcmd(player) {
	player.getActionSender().sendSlayerLog();
}