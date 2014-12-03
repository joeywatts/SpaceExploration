package cs2114.spaceexploration.universe;

//Class depends upon the Rajawali 3D library (stable v0.7).

import cs2114.spaceexploration.entity.Player;

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
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
