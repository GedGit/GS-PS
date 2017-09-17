package org.rs2server.rs2.model.cm;

import org.rs2server.Server;
import org.rs2server.rs2.domain.service.api.EngineService;
import org.rs2server.rs2.model.player.Player;

public abstract class Content {

	public static final int ZULRAH = 1;
	public static final int CORPOREAL_BEAST = 2;
	public static final int KRAKEN = 3;
	public static final int CERBERUS = 4;

	protected final EngineService engineService;
	protected Player player;

	private long start;
	private long duration;
	private boolean stopping;

	public Content(Player player) {
		this.player = player;
		this.engineService = Server.getInjector().getInstance(EngineService.class);
	}

	public void startRecording() {
		if (start == 0)
			start = System.currentTimeMillis();
	}

	public void stopRecording() {
		if (start != 0) {
			duration = System.currentTimeMillis() - start;
			start = 0;
		}
	}

	public abstract void start();

	public abstract void process();

	public abstract void stop();

	public abstract boolean canStart();

	public abstract void onCannotStart();

	public void onDeath() {
		player.getContentManager().stop(this);
	}

	public void setStopping(boolean stopping) {
		this.stopping = stopping;
	}

	public boolean isStopping() {
		return stopping;
	}

	public long getTotalRecorded() {
		return duration;
	}
	
	public int getRecordedMinutes() {
		return (int) ((duration / (1000 * 60)) % 60);
	}

	public int getRecordedSeconds() {
		return (int) ((duration / 1000) % 60);
	}

}
