load("nashorn:mozilla_compat.js");//allows importPackage() usage

importPackage(org.rs2server.rs2.model)
importPackage(org.rs2server.rs2.util)
importPackage(org.rs2server.rs2.model.map)
importPackage(org.rs2server.rs2.model.skills)
importPackage(java.util)

function wildernessDitch(player, obstacle, object) {
	var y = 3;
	var dir = 0;
	if(player.getLocation().getY() > 3520) {
		y = -3;
		dir = 2;
	}
	var forceMovementVars =  [ 0, 0, 0, y, 33, 60, dir, 2 ];
	Agility.forceMovement(player, Animation.create(6132), forceMovementVars, 1, true);
}

function shamanRockClimb1(player, obstacle, object) {
    if(player.getLocation().getX() != 1454) {
        player.removeAttribute("busy");
        return;
    }
    Agility.setRunningToggled(player, false, 6);
    Agility.forceWalkingQueue(player, Animation.create(737), player.getX() + 6, player.getY(), 0, 6, true);
}

function shamanRockClimb2(player, obstacle, object) {
    if(player.getLocation().getX() != 1460) {
        player.removeAttribute("busy");
        return;
    }
    Agility.setRunningToggled(player, false, 6);
    player.face(Location.create(1480, 3690, 0));
    Agility.forceWalkingQueue(player, Animation.create(737), player.getX() - 6, player.getY(), 0, 6, true);
}
function shamanRockClimb3(player, obstacle, object) {
    if(player.getLocation().getX() != 1470) {
        player.removeAttribute("busy");
        return;
    }
    Agility.setRunningToggled(player, false, 6);
    player.face(Location.create(player.getX() - 6, player.getY(), 0));
    Agility.forceWalkingQueue(player, Animation.create(737), player.getX() + 6, player.getY(), 0, 6, true);
}
function shamanRockClimb4(player, obstacle, object) {
    if(player.getLocation().getX() != 1476) {
        player.removeAttribute("busy");
        return;
    }
    Agility.setRunningToggled(player, false, 6);
    player.face(Location.create(1480, 3690, 0));
    Agility.forceWalkingQueue(player, Animation.create(737), player.getX() - 6, player.getY(), 0, 6, true);
}

function faladorCrumblingWall(player, obstacle, object) {
	var x = 2;
	var dir = 1;
	if(player.getLocation().getX() >= 2936) {
		x = -2;
		dir = 3;
	}
	var forceMovementVars =  [ 0, 0, x, 0, 20, 60, dir, 2 ];
	Agility.forceMovement(player, Animation.create(839), forceMovementVars, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function ardougneLogBalance(player, obstacle, object) {
    if(player.getLocation().getY() != 3336) {
        player.removeAttribute("busy");
        return;
    }
    var x = -4;
    if (player.getLocation().getX() == 2598) {
        x = 4;
    }
    Agility.setRunningToggled(player, false, 4);
    Agility.forceWalkingQueue(player, Animation.create(762), player.getX() + x, player.getY(), 0, 4, true);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeLogBalance(player, obstacle, object) {
	if(player.getLocation().getX() != 2474) {
		player.removeAttribute("busy");
		return;
	}
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == null) {
		player.setAttribute("gnomeAgilityCourse", 1);
	}
	Agility.setRunningToggled(player, false, 7);
	Agility.forceWalkingQueue(player, Animation.create(762), 2474, 3429, 0, 7, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeObstacleNet(player, obstacle, object) {
	player.face(Location.create(player.getLocation().getX(), 0, 0));
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == 1) {
		player.setAttribute("gnomeAgilityCourse", 2);
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX(), 3424, 1), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeTreeBranch(player, obstacle, object) {
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == 2) {
		player.setAttribute("gnomeAgilityCourse", 3);
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(2473, 3420, 2), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeBalanceRope(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2477, 3420, 2))) {
		player.removeAttribute("busy");
		return;
	}
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == 3) {
		player.setAttribute("gnomeAgilityCourse", 4);
	}
	Agility.setRunningToggled(player, false, 7);
	Agility.forceWalkingQueue(player, Animation.create(762), 2483, 3420, 0, 7, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeTreeBranch2(player, obstacle, object) {
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == 4) {
		player.setAttribute("gnomeAgilityCourse", 5);
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(2485, 3419, 0), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeObstacleNet2(player, obstacle, object) {
	if(player.getLocation().getY() != 3425) {
		player.removeAttribute("busy");
		player.getActionSender().sendMessage("You can't go over the net from here.");
		return;
	}
	player.face(Location.create(player.getLocation().getX(), 9999, 0));
	var gnomeAgilityCourseLvl = player.getAttribute("gnomeAgilityCourse");
	if(gnomeAgilityCourseLvl == 5) {
		player.setAttribute("gnomeAgilityCourse", 6);
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX(), 3427, 0), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function gnomeObstaclePipe(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2484, 3430, 0)) && !player.getLocation().equals(Location.create(2487, 3430, 0))) {
		player.removeAttribute("busy");
		return;
	}
	if(player.getAttribute("gnomeAgilityCourse") != null) {
		var courseLevel = player.getAttribute("gnomeAgilityCourse");
		if(courseLevel == 6) {
			player.getActionSender().sendMessage("You completed the course!");
			player.getSkills().addExperience(Skills.AGILITY, 40);
		}
        player.removeAttribute("gnomeAgilityCourse");
	}
	var forceMovementVars =  [ 0, 2, 0, 5, 45, 100, 0, 3 ];
	var forceMovementVars2 =  [ 0, 0, 0, 2, 0, 15, 0, 1 ];
	Agility.forceMovement(player, Animation.create(746), forceMovementVars, 1, false);
	Agility.forceMovement(player, Animation.create(748), forceMovementVars2, 5, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function taverlyObstaclePipe(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2886, 9799, 0)) && !player.getLocation().equals(Location.create(2892, 9799, 0))) {
		player.removeAttribute("busy");
		return;
	}
	var yValue = 0;
	var yValue2 = 0;
	var xValue = 0;
	var face = 1;
	if (player.getLocation().getX() == 2886) {
		yValue = 4;
		yValue2 = 2;
		xValue = 1;
		face = 1;
	} else {
		yValue = -4;
		yValue2 = -2;
		xValue = -1;
		face = 3;
	}
	var forceMovementVars =  [ xValue, 0, yValue2, 0, 45, 100, face, 3 ];
	var forceMovementVars2 =  [ 0, 0, yValue, 0, 0, 15, face, 1 ];
	Agility.forceMovement(player, Animation.create(746), forceMovementVars, 1, false);
	Agility.forceMovement(player, Animation.create(748), forceMovementVars2, 5, true);
}

function edgeDungeonPipe(player, obstacle, object) {
    if(!player.getLocation().equals(Location.create(3149, 9906, 0)) && !player.getLocation().equals(Location.create(3155, 9906, 0))) {
        player.removeAttribute("busy");
        return;
    }
    var yValue = 0;
    var yValue2 = 0;
    var xValue = 0;
    var face = 1;
    if (player.getLocation().getX() == 3149) {
        yValue = 4;
        yValue2 = 2;
        xValue = 1;
        face = 1;
    } else {
        yValue = -4;
        yValue2 = -2;
        xValue = -1;
        face = 3;
    }
    var forceMovementVars =  [ xValue, 0, yValue2, 0, 45, 100, face, 3 ];
    var forceMovementVars2 =  [ 0, 0, yValue, 0, 0, 15, face, 1 ];
    Agility.forceMovement(player, Animation.create(746), forceMovementVars, 1, false);
    Agility.forceMovement(player, Animation.create(748), forceMovementVars2, 5, true);
}

function taverlySpikeJump(player, obstacle, object) {
    if(!player.getLocation().equals(Location.create(2880, 9813, 0)) && !player.getLocation().equals(Location.create(2878, 9813))) {
        player.removeAttribute("busy");
        return;
    }
    var x = -2;
    if (player.getLocation().equals(Location.create(2878, 9813, 0))) {
        x = 2;
    }
    Agility.forceTeleport(player, Animation.create(2586), Location.create(player.getX() + x, 9813, 0), 0, 2);
}

function fremmySpikeJump(player, obstacle, object) {
    if(!player.getLocation().equals(Location.create(2775, 10003, 0)) && !player.getLocation().equals(Location.create(2773, 10003))) {
        player.removeAttribute("busy");
        return;
    }
    var x = -2;
    if (player.getLocation().equals(Location.create(2773, 10003, 0))) {
        x = 2;
    }
    Agility.forceTeleport(player, Animation.create(2586), Location.create(player.getX() + x, 10003, 0), 0, 2);
}

function fremmySpikeJump2(player, obstacle, object) {
    if(!player.getLocation().equals(Location.create(2770, 10002, 0)) && !player.getLocation().equals(Location.create(2768, 10002))) {
        player.removeAttribute("busy");
        return;
    }
    var x = -2;
    if (player.getLocation().equals(Location.create(2768, 10002, 0))) {
        x = 2;
    }
    Agility.forceTeleport(player, Animation.create(2586), Location.create(player.getX() + x, 10002, 0), 0, 2);
}

function motherlodeDarkTunnel(player, obstacle, object) {
    if(!player.getLocation().equals(Location.create(3759, 5670, 0))) {
        player.removeAttribute("busy");
        return;
    }
    Agility.forceTeleport(player, Animation.create(746), Location.create(3765, 5671, 0), 0, 2);
}

function motherlodeDarkTunnel2(player, obstacle, object) {
    if(!player.getLocation().equals(Location.create(3765, 5671, 0))) {
        player.removeAttribute("busy");
        return;
    }
    Agility.forceTeleport(player, Animation.create(746), Location.create(3759, 5670, 0), 0, 2);
}

function varrockRockClimb(player, obstacle, object) {
	var varrockAgilityCourseLvl = player.getAttribute("varrockAgilityCourse");
	if(varrockAgilityCourseLvl == null) {
		player.setAttribute("varrockAgilityCourse", 1);
	}
	player.face(Location.create(player.getX() - 1, player.getY()));
	Agility.forceTeleport(player, Animation.create(2585), Location.create(3219, 3414, 3), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function varrockTightRope(player, obstacle, object) {
	var varrockAgilityCourseLvl = player.getAttribute("varrockAgilityCourse");
	if(varrockAgilityCourseLvl != null) {
		player.setAttribute("varrockAgilityCourse", varrockAgilityCourseLvl + 1);
	}
	Agility.setRunningToggled(player, false, 6);
	Agility.forceWalkingQueue(player, Animation.create(762), 3206, 3414, 0, 7, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function varrockLeapGap1(player, obstacle, object) {
	var varrockAgilityCourseLvl = player.getAttribute("varrockAgilityCourse");
	if(varrockAgilityCourseLvl != null) {
		player.setAttribute("varrockAgilityCourse", varrockAgilityCourseLvl + 1);
	}
	player.face(Location.create(3200, 3416));
	Agility.delayedAnimation(player, Animation.create(2586), 0);
	Agility.forceTeleport(player, Animation.create(2588), Location.create(3197, 3416, 1), 1, 1);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function varrockBalanceWall(player, obstacle, object) {
	var varrockAgilityCourseLvl = player.getAttribute("varrockAgilityCourse");
	if(varrockAgilityCourseLvl != null) {
		player.setAttribute("varrockAgilityCourse", varrockAgilityCourseLvl + 1);
	}
	player.face(Location.create(3189, player.getY()));
	Agility.forceTeleport(player, Animation.create(1122), Location.create(3190, 3414, 1), 2, 2);
	Agility.forceTeleport(player, Animation.create(1122), Location.create(3190, 3413, 1), 4, 4);
	Agility.forceTeleport(player, Animation.create(1122), Location.create(3190, 3412, 1), 6, 6);
	Agility.forceTeleport(player, Animation.create(1122), Location.create(3190, 3411, 1), 8, 8);
	Agility.forceTeleport(player, Animation.create(1122), Location.create(3190, 3410, 1), 10, 10);
	Agility.forceTeleport(player, Animation.create(1122), Location.create(3190, 3410, 1), 12, 12);
	Agility.forceTeleport(player, Animation.create(753), Location.create(3190, 3409, 1), 13, 13);
	Agility.forceTeleport(player, Animation.create(-1), Location.create(3192, 3406, 3), 14, 14);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function varrockLeapGap2(player, obstacle, object) {
	var varrockAgilityCourseLvl = player.getAttribute("varrockAgilityCourse");
	if(varrockAgilityCourseLvl != null) {
		player.setAttribute("varrockAgilityCourse", varrockAgilityCourseLvl + 1);
	}
	player.face(Location.create(3195, 3398));
	Agility.forceTeleport(player, Animation.create(2585), Location.create(3195, 3399, 3), 0, 0);
	Agility.forceTeleport(player, Animation.create(-1), Location.create(3195, 3398, 3), 2, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function varrockLeapGap3(player, obstacle, object) {
	var varrockAgilityCourseLvl = player.getAttribute("varrockAgilityCourse");
	if(varrockAgilityCourseLvl != null) {
		player.setAttribute("varrockAgilityCourse", varrockAgilityCourseLvl + 1);
	}
	player.face(Location.create(3218, 3399));
	Agility.delayedAnimation(player, Animation.create(4789), 0);
	Agility.forceTeleport(player, Animation.create(2585), Location.create(3215, 3399, 3), 2, 2);
	Agility.forceTeleport(player, Animation.create(-1), Location.create(3218, 3399, 3), 4, 4);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function varrockLeapGap4(player, obstacle, object) {
	var varrockAgilityCourseLvl = player.getAttribute("varrockAgilityCourse");
	if(varrockAgilityCourseLvl != null) {
		player.setAttribute("varrockAgilityCourse", varrockAgilityCourseLvl + 1);
	}
	Agility.delayedAnimation(player, Animation.create(2586), 0);
	Agility.forceTeleport(player, Animation.create(2588), Location.create(3236, 3403, 3), 1, 1);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function varrockHurdleLedge(player, obstacle, object) {
    if (player.getLocation().getY() != 3408) {
        player.removeAttribute("busy");
        return;
    }
	var varrockAgilityCourseLvl = player.getAttribute("varrockAgilityCourse");
	if(varrockAgilityCourseLvl != null) {
		player.setAttribute("varrockAgilityCourse", varrockAgilityCourseLvl + 1);
	}
	player.face(Location.create(3236, 3410));
	Agility.forceWalkingQueue(player, Animation.create(1603), 3236, 3410, 0, 2, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function varrockJumpOffEdge(player, obstacle, object) {
	if(player.getAttribute("varrockAgilityCourse") != null) {
		var courseLevel = player.getAttribute("varrockAgilityCourse");
		if(courseLevel == 8) {
			player.getActionSender().sendMessage("You completed the course!");
            var rand = Misc.random(2);
            if (rand == 0) {
                var amount = Misc.random(3, 8);
                player.getInventory().add(new Item(11849, amount));
                player.getActionSender().sendMessage("You received " + amount + " x Marks of grace for completing the lap.");
            }
		}
		player.removeAttribute("varrockAgilityCourse");
	}
	player.face(Location.create(3236, 3418));
	Agility.delayedAnimation(player, Animation.create(2586), 0);
	Agility.forceTeleport(player, Animation.create(2588), Location.create(3236, 3418, 0), 1, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function draynorRockClimb(player, obstacle, object) {
	var draynorAgilityCourseLvl = player.getAttribute("draynorAgilityCourse");
	if(draynorAgilityCourseLvl == null) {
		player.setAttribute("draynorAgilityCourse", 1);
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(3102, 3279, 3), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function draynorTightRope1(player, obstacle, object) {
	if(player.getLocation().getY() != 3277) {
		player.removeAttribute("busy");
		return;
	}
	var draynorAgilityCourseLvl = player.getAttribute("draynorAgilityCourse");
	if(draynorAgilityCourseLvl != null) {
		player.setAttribute("draynorAgilityCourse", draynorAgilityCourseLvl + 1);
	}
	Agility.setRunningToggled(player, false, 12);
	Agility.forceWalkingQueue(player, Animation.create(762), 3090, 3277, 0, 10, false);
	Agility.forceWalkingQueue(player, Animation.create(762), 3090, 3276, 10, 2, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function draynorTightRope2(player, obstacle, object) {
	if(player.getLocation().getY() != 3276) {
		player.removeAttribute("busy");
		return;
	}
	var draynorAgilityCourseLvl = player.getAttribute("draynorAgilityCourse");
	if(draynorAgilityCourseLvl != null) {
		player.setAttribute("draynorAgilityCourse", draynorAgilityCourseLvl + 1);
	}
	Agility.setRunningToggled(player, false, 12);
	Agility.forceWalkingQueue(player, Animation.create(762), 3092, 3276, 0, 2, false);
	Agility.forceWalkingQueue(player, Animation.create(762), 3092, 3267, 2, 10, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function draynorNarrowWall(player, obstacle, object) {
	if(player.getLocation().getX() != 3089) {
		player.removeAttribute("busy");
		return;
	}//756
	var draynorAgilityCourseLvl = player.getAttribute("draynorAgilityCourse");
	if(draynorAgilityCourseLvl != null) {
		player.setAttribute("draynorAgilityCourse", draynorAgilityCourseLvl + 1);
	}
	Agility.setRunningToggled(player, false, 4);
	Agility.forceWalkingQueue(player, Animation.create(756), 3089, 3262, 0, 3, false);
	Agility.forceWalkingQueue(player, Animation.create(756), 3088, 3261, 3, 1, true);
	Agility.delayedAnimation(player, Animation.create(759), 4);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}
//2585

function draynorJumpWall(player, obstacle, object) {
	if(player.getLocation().getY() != 3257) {
		player.removeAttribute("busy");
		return;
	}
	var draynorAgilityCourseLvl = player.getAttribute("draynorAgilityCourse");
	if(draynorAgilityCourseLvl != null) {
		player.setAttribute("draynorAgilityCourse", draynorAgilityCourseLvl + 1);
	}
	Agility.setRunningToggled(player, false, 4);
	Agility.forceWalkingQueue(player, Animation.create(2585), 3088, 3256, 0, 2, false);
	Agility.forceWalkingQueue(player, Animation.create(2585), 3088, 3255, 2, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function draynorJumpGap(player, obstacle, object) {
	if(player.getLocation().getX() != 3094) {
		player.removeAttribute("busy");
		return;
	}
	var draynorAgilityCourseLvl = player.getAttribute("draynorAgilityCourse");
	if(draynorAgilityCourseLvl != null) {
		player.setAttribute("draynorAgilityCourse", draynorAgilityCourseLvl + 1);
	}
	Agility.delayedAnimation(player, Animation.create(2588), 0);
	Agility.forceTeleport(player, Animation.create(-1), Location.create(3096, 3256, 3), 0, 0);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function draynorCrateJump(player, obstacle, object) {
	if(player.getLocation().getY() != 3261) {
		player.removeAttribute("busy");
		return;
	}//2586, 2588
	if(player.getAttribute("draynorAgilityCourse") != null) {
		var courseLevel = player.getAttribute("draynorAgilityCourse");
		if(courseLevel == 6) {
			player.getActionSender().sendMessage("You completed the course!");
			player.getSkills().addExperience(Skills.AGILITY, 40);
            var rand = Misc.random(2);
            if (rand == 0) {
                var amount = Misc.random(3, 8);
                player.getInventory().add(new Item(11849, amount));
                player.getActionSender().sendMessage("You received " + amount + " x Marks of grace for completing the lap.");
            }
		}
		player.removeAttribute("draynorAgilityCourse");
	}
	Agility.forceTeleport(player, Animation.create(2586), Location.create(3102, 3261, 1), 0, 2);
	Agility.delayedAnimation(player, Animation.create(2588), 2);
	Agility.forceTeleport(player, Animation.create(2586), Location.create(3103, 3261, 0), 4, 5);
	Agility.delayedAnimation(player, Animation.create(2588), 5);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function seersWallClimb(player, obstacle, object) {
    if(player.getLocation().getX() != 2729) {
        player.removeAttribute("busy");
        return;
    }
    var seersAgilityCourseLvl = player.getAttribute("seersAgilityCourse");
    if(seersAgilityCourseLvl == null) {
        player.setAttribute("seersAgilityCourse", 1);
    }
    Agility.forceTeleport(player, Animation.create(737), Location.create(2729, 3488, 1), 0, 2);
    Agility.forceTeleport(player, Animation.create(1118), Location.create(2729, 3490, 3), 2, 4);
    Agility.delayedAnimation(player, Animation.create(-1), 4);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function seersGapJump(player, obstacle, object) {
    if (player.getLocation().getX() != 2721) {
        player.removeAttribute("busy");
        return;
    }
    var seersAgilityCourseLvl = player.getAttribute("seersAgilityCourse");
    if (seersAgilityCourseLvl != null) {
        player.setAttribute("seersAgilityCourse", seersAgilityCourseLvl + 1);
    }
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2719, 3495, 2), 0, 2);
    Agility.delayedAnimation(player, Animation.create(2588), 2);
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2713, 3494, 2), 4, 5);
    Agility.delayedAnimation(player, Animation.create(2588), 5);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function seersTightRope(player, obstacle, object) {
    if (player.getLocation().getX() != 2710) {
        player.removeAttribute("busy");
        return;
    }
    var seersAgilityCourseLvl = player.getAttribute("seersAgilityCourse");
    if (seersAgilityCourseLvl != null) {
        player.setAttribute("seersAgilityCourse", seersAgilityCourseLvl + 1);
    }
    Agility.setRunningToggled(player, false, 9);
    Agility.forceWalkingQueue(player, Animation.create(762), 2710, 3481, 0, 9, true);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function seersGapJump2(player, obstacle, object) {
    if(player.getLocation().getY() != 3477 && player.getLocation().getY() != 3476) {
        player.removeAttribute("busy");
        return;
    }
    var seersAgilityCourseLvl = player.getAttribute("seersAgilityCourse");
    if (seersAgilityCourseLvl != null) {
        player.setAttribute("seersAgilityCourse", seersAgilityCourseLvl + 1);
    }
    Agility.setRunningToggled(player, false, 4);
    Agility.forceTeleport(player, Animation.create(-1), Location.create(2710, 3474, 3), 0, 0);
    Agility.forceTeleport(player, Animation.create(2585), Location.create(2710, 3472, 3), 1, 3);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function seersGapJump3(player, obstacle, object) {
    if(player.getLocation().getY() != 3470) {
        player.removeAttribute("busy");
        return;
    }
    var seersAgilityCourseLvl = player.getAttribute("seersAgilityCourse");
    if (seersAgilityCourseLvl != null) {
        player.setAttribute("seersAgilityCourse", seersAgilityCourseLvl + 1);
    }
    Agility.setRunningToggled(player, false, 2);
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2702, 3465, 2), 0, 2);
    Agility.delayedAnimation(player, Animation.create(2588), 2);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function seersGapJump4(player, obstacle, object) {
    if(player.getLocation().getX() != 2702) {
        player.removeAttribute("busy");
        return;
    }
    if(player.getAttribute("seersAgilityCourse") != null) {
        var courseLevel = player.getAttribute("seersAgilityCourse");
        if(courseLevel == 5) {
            player.getActionSender().sendMessage("You completed the course!");
            player.getSkills().addExperience(Skills.AGILITY, 50);
            var rand = Misc.random(2);
            if (rand == 0) {
                var amount = Misc.random(3, 8);
				if (player.getEquipment().contains(13137)) {
					amount += 1;
				}
				if (player.getEquipment().contains(13138)) {
					amount += 2;
				}
				if (player.getEquipment().contains(13139)) {
					amount += 3;
				}
				if (player.getEquipment().contains(13140)) {
					amount += 4;
				}
                player.getInventory().add(new Item(11849, amount));
                player.getActionSender().sendMessage("You received " + amount + " x Marks of grace for completing the lap.");
            }
        }
        player.removeAttribute("seersAgilityCourse");
    }
    Agility.setRunningToggled(player, false, 2);
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2704, 3464, 0), 0, 2);
    Agility.delayedAnimation(player, Animation.create(2588), 2);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function ardyWoodClimb(player, obstacle, object) {
    if(player.getLocation().getX() != 2673 && player.getLocation().getX() != 2672) {
        player.removeAttribute("busy");
        return;
    }
    var ardyAgilityCourseLvl = player.getAttribute("ardyAgilityCourse");
    if(ardyAgilityCourseLvl == null) {
        player.setAttribute("ardyAgilityCourse", 1);
    }
    Agility.forceTeleport(player, Animation.create(740), Location.create(2673, 3298, 1), 0, 1);
    Agility.forceTeleport(player, Animation.create(740), Location.create(2673, 3298, 2), 1, 2);
    Agility.forceTeleport(player, Animation.create(740), Location.create(2673, 3298, 3), 2, 3);
    Agility.forceTeleport(player, Animation.create(2588), Location.create(2671, 3299, 3), 4, 4);
    Agility.delayedAnimation(player, Animation.create(-1), 5);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function ardyGapJump(player, obstacle, object) {
    if (player.getLocation().getX() != 2671) {
        player.removeAttribute("busy");
        return;
    }
    var ardyAgilityCourseLvl = player.getAttribute("ardyAgilityCourse");
    if (ardyAgilityCourseLvl != null) {
        player.setAttribute("ardyAgilityCourse", ardyAgilityCourseLvl + 1);
    }
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2667, 3311, 1), 0, 2);
    Agility.delayedAnimation(player, Animation.create(2588), 2);
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2665, 3315, 1), 4, 5);
    Agility.delayedAnimation(player, Animation.create(2588), 5);
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2665, 3318, 3), 6, 8);
    Agility.delayedAnimation(player, Animation.create(2588), 8);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function ardyPlankWalk(player, obstacle, object) {
    if (player.getLocation().getX() != 2662) {
        player.removeAttribute("busy");
        return;
    }
    var ardyAgilityCourseLvl = player.getAttribute("ardyAgilityCourse");
    if (ardyAgilityCourseLvl != null) {
        player.setAttribute("ardyAgilityCourse", ardyAgilityCourseLvl + 1);
    }
    Agility.setRunningToggled(player, false, 6);
    Agility.forceWalkingQueue(player, Animation.create(762), 2656, 3318, 0, 6, true);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function ardyGapJump2(player, obstacle, object) {
    if(player.getLocation().getX() != 2654) {
        player.removeAttribute("busy");
        return;
    }
    var ardyAgilityCourseLvl = player.getAttribute("ardyAgilityCourse");
    if (ardyAgilityCourseLvl != null) {
        player.setAttribute("ardyAgilityCourse", ardyAgilityCourseLvl + 1);
    }
    player.face(Location.create(2653, 3314, 3));
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2653, 3314, 3), 0, 2);
    Agility.delayedAnimation(player, Animation.create(2588), 2);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function ardyGapJump3(player, obstacle, object) {
    if(player.getLocation().getY() != 3310) {
        player.removeAttribute("busy");
        return;
    }
    var ardyAgilityCourseLvl = player.getAttribute("ardyAgilityCourse");
    if (ardyAgilityCourseLvl != null) {
        player.setAttribute("ardyAgilityCourse", ardyAgilityCourseLvl + 1);
    }
    player.face(Location.create(2651, 3309, 3));
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2651, 3309, 3), 0, 2);
    Agility.delayedAnimation(player, Animation.create(2588), 2);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function ardyStepRoof(player, obstacle, object) {
    if(player.getLocation().getY() != 3300) {
        player.removeAttribute("busy");
        return;
    }//756
    var ardyAgilityCourseLvl = player.getAttribute("ardyAgilityCourse");
    if(ardyAgilityCourseLvl != null) {
        player.setAttribute("ardyAgilityCourse", ardyAgilityCourseLvl + 1);
    }
    Agility.setRunningToggled(player, false, 4);
    Agility.forceWalkingQueue(player, Animation.create(756), 2655, 3297, 0, 3, false);
    Agility.forceWalkingQueue(player, Animation.create(756), 2656, 3297, 3, 1, true);
    Agility.delayedAnimation(player, Animation.create(759), 4);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function ardyGapJump4(player, obstacle, object) {
    if(player.getLocation().getX() != 2656) {
        player.removeAttribute("busy");
        return;
    }
    if(player.getAttribute("ardyAgilityCourse") != null) {
        var courseLevel = player.getAttribute("ardyAgilityCourse");
        if(courseLevel == 6) {
            player.getActionSender().sendMessage("You completed the course!");
            var rand = Misc.random(2);
            if (rand == 0) {
                var amount = Misc.random(3, 8);
				if (player.getEquipment().contains(13124))
					amount += 4;
                player.getInventory().add(new Item(11849, amount));
                player.getActionSender().sendMessage("You received " + amount + " x Marks of grace for completing the lap.");
            }
        }
        player.removeAttribute("ardyAgilityCourse");
    }
    Agility.setRunningToggled(player, false, 16);
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2658, 3298, 1), 0, 2);
    Agility.delayedAnimation(player, Animation.create(2588), 2);
    Agility.forceWalkingQueue(player, Animation.create(819), 2661, 3298, 3, 3, false);
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2663, 3297, 1), 6, 8);
    Agility.delayedAnimation(player, Animation.create(2588), 8);
    Agility.forceWalkingQueue(player, Animation.create(819), 2666, 3297, 9, 4, false);
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2667, 3297, 1), 12, 13);
    Agility.delayedAnimation(player, Animation.create(2588), 13);
    Agility.forceTeleport(player, Animation.create(2586), Location.create(2668, 3297, 0), 15, 16);
    Agility.delayedAnimation(player, Animation.create(2588), 16);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

//2588

function kharidWall(player, obstacle, object) {
	Agility.forceTeleport(player, Animation.create(828), Location.create(3273, 3192, 3), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function kharidRope(player, obstacle, object) {
	if(player.getLocation().getX() != 3272) {
		player.removeAttribute("busy");
		return;
	}
	var kharidAgilityCourseLvl = player.getAttribute("kharidAgilityCourse");
	if(kharidAgilityCourseLvl == null) {
		player.setAttribute("kharidAgilityCourse", 1);
	}
	Agility.setRunningToggled(player, false, 10);
	Agility.forceWalkingQueue(player, Animation.create(762), 3272, 3172, 0, 10, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function kharidCable(player, obstacle, object) {
    if(player.getLocation().getY() != 3166) {
        player.removeAttribute("busy");
        return;
    }
    var kharidAgilityCourseLvl = player.getAttribute("kharidAgilityCourse");
    if(kharidAgilityCourseLvl == null) {
        player.setAttribute("kharidAgilityCourse", 2);
    }
    //walk 1995, teleport 771
    var forceMovementVars =  [ 0, 0, 14, 0, 30, 50, 1, 2 ];
    var forceMovementVars2 =  [ 0, 0, -2, 0, 30, 50, 1, 1 ];
    Agility.forceMovement(player, player.getWalkAnimation(), forceMovementVars2, 2, false);
    Agility.forceWalkingQueue(player, Animation.create(1995), player.getX() + 2, player.getY(), 4, 5, true);
    Agility.forceMovement(player, Animation.create(751), forceMovementVars, 7, false);
    player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function kharidZipline(player, obstacle, object) {
    if(player.getLocation().getY() != 3163) {
        player.removeAttribute("busy");
        return;
    }
    var kharidAgilityCourseLvl = player.getAttribute("kharidAgilityCourse");
    if(kharidAgilityCourseLvl == null) {
        player.setAttribute("kharidAgilityCourse", 3);
    }
    player.setTeleportTarget(Location.create(3304, 3163, 1));
    Agility.forceWalkingQueue(player, Animation.create(1602), player.getX() + 14, player.getY(), 2, 12, true);
}

function kharidTree(player, obstacle, object) {//1122, 1124, 2458
    if(player.getLocation().getY() != 3164) {
        player.removeAttribute("busy");
        return;
    }
    var kharidAgilityCourseLvl = player.getAttribute("kharidAgilityCourse");
    if(kharidAgilityCourseLvl == null) {
        player.setAttribute("kharidAgilityCourse", 4);
    }
    player.setTeleportTarget(Location.create(3318, 3166, 1));
    Agility.forceWalkingQueue(player, Animation.create(1603), player.getX() - 1, player.getY() + 8, player.getZ() + 1, true);
}
function kharidRope2(player, obstacle, object) {
	if(player.getLocation().getX() != 3313) {
		player.removeAttribute("busy");
		return;
	}
	var kharidAgilityCourseLvl = player.getAttribute("kharidAgilityCourse");
	if(kharidAgilityCourseLvl == null) {
		player.setAttribute("kharidAgilityCourse", 6);
	}
	Agility.setRunningToggled(player, false, 10);
	Agility.forceWalkingQueue(player, Animation.create(762), 3303, 3186, 0, 10, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}
function kharidBeam(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(3316, 3179, 5))) {
		player.removeAttribute("busy");
		return;
	}
	var kharidAgilityCourseLvl = player.getAttribute("kharidAgilityCourse");
    if(kharidAgilityCourseLvl == null) {
        player.setAttribute("kharidAgilityCourse", 4);
    }
    player.setTeleportTarget(Location.create(3316, 3080, 3));
    Agility.forceWalkingQueue(player, Animation.create(1603), player.getX() - 0, player.getY() + 1, player.getZ() + 1, true);
}



function barbarianObstaclePipe(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2552, 3561, 0)) && !player.getLocation().equals(Location.create(2552, 3558, 0))) {
		player.removeAttribute("busy");
		return;
	}
	var y = -3;
	var dir = 2;
	if(player.getLocation().getY() <= 3558) {
		y = 3;
		dir = 0;
	}
	var forceMovementVars =  [ 0, 0, 0, y, 0, 60, dir, 2 ];
	Agility.forceMovement(player, Animation.create(749), forceMovementVars, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function barbarianRopeSwing(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2551, 3554, 0))) {
		player.removeAttribute("busy");
		return;
	}
	
	var random = new Random();
	var randomVar = random.nextInt(player.getSkills().getLevel(Skills.AGILITY));
	var success = true;
	
	if(randomVar < 20) {
		success = false;
	}
	
	Agility.animateObject(object, Animation.create(54), 0);
	Agility.animateObject(object, Animation.create(55), 2);
	if(success) {
		var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
		if(barbAgilityCourseLvl == null) {
			player.setAttribute("barbarianAgilityCourse", 1);
		}
		var forceMovementVars =  [ 0, 0, 0, -5, 30, 50, 2, 2 ];
		Agility.forceMovement(player, Animation.create(751), forceMovementVars, 1, true);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	} else {
		var forceMovementVars =  [ 0, 0, 0, -3, 30, 50, 2, 2 ];
		Agility.forceMovement(player, Animation.create(751), forceMovementVars, 1, true);
		Agility.forceTeleport(player, Animation.create(766), Location.create(2551, 9951, 0), 3, 6);
		Agility.forceWalkingQueue(player, null, 2549, 9951, 7, 2, true);
		Agility.setRunningToggled(player, false, 9);
		Agility.damage(player, 5, 7);
	}
}

function barbarianLogBalance(player, obstacle, object) {
	if(player.getLocation().getY() != 3546) {
		player.removeAttribute("busy");
		return;
	}
	
	var random = new Random();
	var randomVar = random.nextInt(player.getSkills().getLevel(Skills.AGILITY));
	var success = true;
	
	if(randomVar < 20) {
		success = false;
	}
	
	if(success) {
		var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
		if(barbAgilityCourseLvl == 1) {
			player.setAttribute("barbarianAgilityCourse", 2);
		}
		Agility.setRunningToggled(player, false, 12);
		Agility.forceWalkingQueue(player, Animation.create(762), 2541, 3546, 0, 11, true);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	} else {
		player.face(Location.create(0, player.getLocation().getY(),0));
			
		Agility.forceTeleport(player, Animation.create(773), Location.create(2545, 3547, 0), 10, 12);
		Agility.setRunningToggled(player, false, 16);
		Agility.forceWalkingQueue(player, Animation.create(772), 2545, 3549, 12, 4, false);
		Agility.forceWalkingQueue(player, Animation.create(772), 2546, 3550, 13, 3, false);
		
		Agility.forceWalkingQueue(player, Animation.create(762), 2545, 3546, 0, 7, false);		
		
		var forceMovementVars =  [ 0, 0, 0, 1, 25, 30, 3, 2 ];
		Agility.forceMovement(player, Animation.create(771), forceMovementVars, 8, false);
		
		Agility.forceTeleport(player, null, Location.create(2546, 3550, 0), 16, 16);		
	}
}

function barbarianObstacleNet(player, obstacle, object) {
	if(player.getLocation().getX() != 2539) {
		player.removeAttribute("busy");
		return;
	}
	if(player.getLocation().getY() >= 3547) {
		player.removeAttribute("busy");
		return;
	}
	player.face(Location.create(0, player.getLocation().getY(), 0));
	var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
	if(barbAgilityCourseLvl == 2) {
		player.setAttribute("barbarianAgilityCourse", 3);
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX() - 2, 3546, 1), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}

function barbarianLedge(player, obstacle, object) {
	if(player.getLocation().getY() != 3547) {
		player.removeAttribute("busy");
		return;
	}
	
	var random = new Random();
	var randomVar = random.nextInt(player.getSkills().getLevel(Skills.AGILITY));
	var success = true;
	
	if(randomVar < 20) {
		success = false;
	}
		
	player.face(Location.create(0, player.getLocation().getY(), 0));
	
	if(success) {
		var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
		if(barbAgilityCourseLvl == 2) {
			player.setAttribute("barbarianAgilityCourse", 3);
		}
		player.playAnimation(Animation.create(753));
		Agility.setRunningToggled(player, false, 8);
		Agility.forceWalkingQueue(player, null, 2532, 3546, 4, 2, false);
		Agility.forceWalkingQueue(player, Animation.create(756), 2532, 3547, 0, 4, false);	
		Agility.forceTeleport(player, Animation.create(828), Location.create(2532, 3546, 0), 7, 8);
		player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	} else {
		Agility.setRunningToggled(player, false, 8);
		Agility.forceTeleport(player, null, Location.create(2534, 3546, 1), 3, 3);
		Agility.forceWalkingQueue(player, null, 2536, 3547, 6, 3, true);
		Agility.forceTeleport(player, Animation.create(766), Location.create(2534, 3546, 0), 3, 5);
		Agility.forceWalkingQueue(player, Animation.create(756), 2534, 3547, 0, 2, false);
		Agility.damage(player, 5, 6);		
	}
}

function barbarianCrumblingWall1(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2535, 3553, 0))) {
		player.removeAttribute("busy");
		return;
	}
	var forceMovementVars =  [ 0, 0, 2, 0, 0, 60, 1, 2 ];
	Agility.forceMovement(player, Animation.create(839), forceMovementVars, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
	if(barbAgilityCourseLvl == 3) {
		player.setAttribute("barbarianAgilityCourse", 4);
	}
}

function barbarianCrumblingWall2(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2538, 3553, 0))) {
		player.removeAttribute("busy");
		return;
	}
	var forceMovementVars =  [ 0, 0, 2, 0, 0, 60, 1, 2 ];
	Agility.forceMovement(player, Animation.create(839), forceMovementVars, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
	if(barbAgilityCourseLvl == 4) {
		player.setAttribute("barbarianAgilityCourse", 5);
	}
}

function barbarianCrumblingWall3(player, obstacle, object) {
	if(!player.getLocation().equals(Location.create(2541, 3553, 0))) {
		player.removeAttribute("busy");
		return;
	}
	var forceMovementVars =  [ 0, 0, 2, 0, 0, 60, 1, 2 ];
	Agility.forceMovement(player, Animation.create(839), forceMovementVars, 1, true);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
	var barbAgilityCourseLvl = player.getAttribute("barbarianAgilityCourse");
	if(barbAgilityCourseLvl == 5) {
		player.getActionSender().sendMessage("You completed the course!");
		player.getSkills().addExperience(Skills.AGILITY, 46.2);
		player.removeAttribute("barbarianAgilityCourse");
	}
}

function slayerRockClimb(player, obstacle, object) {
	if(player.getLocation().getX() != 2427) {
		player.removeAttribute("busy");
		return;
	}
	var movement = 0;
	if ((player.getLocation().getX() == 2427 && player.getLocation().getY() == 9762 && object.getLocation().getX() == 2427 && object.getLocation().getY() == 9763)) {
		movement = 2;
	} else if (player.getLocation().getX() == 2427 && player.getLocation().getY() == 9764 && object.getLocation().getX() == 2427 && object.getLocation().getY() == 9763) {
		movement = -2;
	}
	if (player.getLocation().getX() == 2427 && player.getLocation().getY() == 9767  && object.getLocation().getX() == 2427 && object.getLocation().getY() == 9766) {
		movement = -2;
	} else if (player.getLocation().getX() == 2427 && player.getLocation().getY() == 9765  && object.getLocation().getX() == 2427 && object.getLocation().getY() == 9766) {
		movement = 2;
	}
	Agility.forceTeleport(player, Animation.create(828), Location.create(player.getLocation().getX(), player.getLocation().getY() + movement, 0), 0, 2);
	player.getSkills().addExperience(Skills.AGILITY, obstacle.getExperience());
}
