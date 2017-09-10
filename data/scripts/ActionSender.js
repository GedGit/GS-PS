load("nashorn:mozilla_compat.js");

importPackage(org.rs2server.rs2.model)
importPackage(org.rs2server.rs2.util)

function sendLogin(player) {
	player.getActionSender().sendMessage("Welcome to "+Constants.SERVER_NAME+".");
	//Welcome to Deadman mode. On these worlds, you die.
	if (World.isWeekend())
		player.getActionSender().sendMessage("<img=30> Double experience is currently: <col=ff0000>Activated</col>.");
	player.getActionSender().sendMessage("<img=51><col=ff0000> Latest Update</col>: "+Constants.LATEST_UPDATE);
	player.getActionSender().sendSkillLevels();
	player.getActionSender().sendEnergy();
	player.getActionSender().updateRunningConfig();
	player.getActionSender().sendScreenBrightness();
	player.getActionSender().sendString(593, 2, "Combat lvl: " + player.getSkills().getCombatLevel());
	//player.getActionSender().updateQuestText();
	player.getActionSender().sendGlobalCC();
	player.getActionSender().updateSplitPrivateChatConfig();
	player.getActionSender().updateAutoRetaliateConfig();
	player.getActionSender().updateClickPriority();
	player.getActionSender().updateSoundVolume();
}