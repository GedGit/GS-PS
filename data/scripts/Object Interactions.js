importPackage(org.rs2server.rs2.model)
importPackage(org.rs2server.rs2.model.minigame.impl)
importPackage(org.rs2server.rs2.tickable)

function objectOptionOne9356(player, object) {
	player.getFightCave().start();
}

function objectOptionOne9357(player, object) {
	player.getFightCave().stop();
}

function objectOptionTwo11758(player, object) {
	Bank.open(player);
}

function objectOptionOne1733(player, object) {
	player.setTeleportTarget(Location.create(3058, 9776, 0));
}

function objectOptionOne1734(player, object) {
	player.setTeleportTarget(Location.create(3061, 3376, 0));
}

function objectOptionOne1755(player, object) {
	if(object.getLocation().equals(Location.create(2884, 9797, 0))) {
		player.setTeleportTarget(Location.create(player.getLocation().getX(), player.getLocation().getY() - 6400, 0));
	} else if(player.getLocation().getY() <= 9740 && player.getLocation().getY() >= 9738 && player.getLocation().getX() >= 3018 && player.getLocation().getX() <= 3020) {
		player.setTeleportTarget(Location.create(player.getLocation().getX(), player.getLocation().getY() - 6400, 0));
	} else if(object.getLocation().equals(Location.create(3019, 9850, 0))) {
		player.setTeleportTarget(Location.create(3018, 3450, 0));
	} else if(object.getLocation().equals(Location.create(2547, 9951, 0))) {
		player.setTeleportTarget(Location.create(2548, 3551, 0));
	}
}

function objectOptionOne11867(player, object) {
	player.setTeleportTarget(Location.create(3020, 9850, 0));
}

function objectOptionOne2112(player, object) {
	if(player.getLocation().getY() >= 9757) {
		if(player.getSkills().getLevelForExperience(Skills.MINING) < 60) {
			player.getActionSender().sendMessage("You need a Mining level of 60 to enter the Mining Guild.");
			return;
		}
		player.setTeleportTarget(Location.create(3046, 9756, 0));
	} else {
		player.setTeleportTarget(Location.create(3046, 9757, 0));
	}	
}

function objectOptionOne2113(player, object) {
	if(player.getSkills().getLevelForExperience(Skills.MINING) < 60) {
		player.getActionSender().sendMessage("You need a Mining level of 60 to enter the Mining Guild.");
		return;
	}
	player.setTeleportTarget(Location.create(player.getLocation().getX(), player.getLocation().getY() + 6400, 0));
}

function objectOptionOne11844(player, object) {
	if(player.getLocation().getX() <= 2935) {
		player.setTeleportTarget(Location.create(2936, 3355, 0));
	} else {
		player.setTeleportTarget(Location.create(2934, 3355, 0));
	}
}

function objectOptionOne1759(player, object) {
	if(object.getLocation().equals(Location.create(2547, 3551, 0))) {
		player.setTeleportTarget(Location.create(2548, 9951, 0));
	} else {
		player.setTeleportTarget(Location.create(2884, player.getLocation().getY() + 6400, 0));
	}
}

function objectOptionOne4188(player, object) {
	player.setTeleportTarget(Location.create(2666, 3694, 0));
}

function objectOptionOne492(player, object) {
	player.setTeleportTarget(Location.create(2856, 9570, 0));
}

function objectOptionOne1764(player, object) {
	player.setTeleportTarget(Location.create(2856, 3167, 0));
}

function objectOptionOne9358(player, object) {
	player.setTeleportTarget(Location.create(2480, 5175, 0));
}

function objectOptionOne9359(player, object) {
	player.setTeleportTarget(Location.create(2862, 9572, 0));
}

function objectOptionOne9368(player, object) {
	if(player.getLocation().equals(Location.create(2399, 5167, 0))) {
		if(player.getMinigame() != null) {
			player.getMinigame().quit(player);
		}
	} else if(player.getLocation().equals(Location.create(2399, 5169, 0))) {
		player.getActionSender().sendMessage("The heat of the barrier prevents you from walking through.");
	}	
}

function objectOptionOne9369(player, object) {
	if(player.getLocation().equals(Location.create(2399, 5177, 0))) {
		World.getWorld().getFightPits().addWaitingPlayer(player);
		player.getWalkingQueue().reset();
		player.getWalkingQueue().addStep(2399, 5175);
		player.getWalkingQueue().finish();
	} else if(player.getLocation().equals(Location.create(2399, 5175, 0))) {
		player.getWalkingQueue().reset();
		player.getWalkingQueue().addStep(2399, 5177);
		player.getWalkingQueue().finish();
	}	
}

function objectOptionOne9391(player, object) {
	if(World.getWorld().getFightPits().getParticipants() <= 1) {
		player.getActionSender().sendMessage("There isn't a game currently running that you can view.");
		return;
	}
	World.getWorld().getFightPits().removeWaitingPlayer(player);
	player.setInterfaceAttribute("fightPitOrbs", true);
	player.setTeleportTarget(FightPits.CENTRE_ORB);
	player.getActionSender().sendInterfaceInventory(374);
	player.setPnpc(5135);
	player.getUpdateFlags().flag(UpdateFlags.UpdateFlag.APPEARANCE);
}

function objectOptionOne26305(player, object) {
	if(object.getLocation().equals(Location.create(3008, 3462, 0))) {
		player.setTeleportTarget(Location.create(2899, 3713, 0));
	} else if(object.getLocation().equals(Location.create(2900, 3713, 0))) {
		player.setTeleportTarget(Location.create(3008, 3461, 0));
	}
}

function objectOptionOne26338(player, object) {
	if(player.getLocation().getY() <= 3715) {
		if(player.getSkills().getLevel(Skills.STRENGTH) < 60) {
			player.getActionSender().sendMessage("You need a Strength level of 60 to move this boulder.");
			return;
		}
		player.setTeleportTarget(Location.create(player.getLocation().getX(), player.getLocation().getY() + 4, 0));
	} else {
		player.setTeleportTarget(Location.create(player.getLocation().getX(), player.getLocation().getY() - 4, 0));
	}
}

function objectOptionOne26341(player, object) {
	player.setTeleportTarget(Location.create(2881, 5310, 2));
}

function objectOptionOne26293(player, object) {
	player.setTeleportTarget(Location.create(2916, 3747, 0));
}

function objectOptionTwo26299(player, object) {
	player.setTeleportTarget(Location.create(2914, 5300, 1));
}
