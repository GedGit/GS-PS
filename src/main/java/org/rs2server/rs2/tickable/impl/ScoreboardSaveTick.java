package org.rs2server.rs2.tickable.impl;

import org.rs2server.rs2.model.minigame.bh.BountyHunter;
import org.rs2server.rs2.tickable.Tickable;

public class ScoreboardSaveTick extends Tickable {

	public static final int UPDATE_TIME = 200; //2 mins
	
	public ScoreboardSaveTick() {
		super(UPDATE_TIME);
	}

	@Override
	public void execute() {
		BountyHunter.INSTANCE.save();
	}
}