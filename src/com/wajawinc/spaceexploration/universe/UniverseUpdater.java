package com.wajawinc.spaceexploration.universe;

import com.wajawinc.spaceexploration.entity.Player;

public class UniverseUpdater extends Thread {

	private Universe universe;
	private Player player;

	public UniverseUpdater(Universe universe, Player player) {
		this.universe = universe;
		this.player = player;
	}

	public void run() {
		while (!this.isInterrupted()) {
			universe.update(player);
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
