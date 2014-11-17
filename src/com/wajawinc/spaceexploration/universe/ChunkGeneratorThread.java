package com.wajawinc.spaceexploration.universe;

import java.util.concurrent.ConcurrentLinkedQueue;

import rajawali.math.Number3D;

public class ChunkGeneratorThread extends Thread {

	private static class ChunkUpdate {
		Planet planet;
		Number3D chunkLoc;
		int lodLevel;
	}

	private ConcurrentLinkedQueue<ChunkUpdate> chunksToGenerate;

	public ChunkGeneratorThread() {
		chunksToGenerate = new ConcurrentLinkedQueue<ChunkUpdate>();
	}

	public void generateChunk(Planet planet, Number3D chunk, int lodLevel) {
		ChunkUpdate update = new ChunkUpdate();
		update.chunkLoc = chunk;
		update.lodLevel = lodLevel;
		update.planet = planet;
		chunksToGenerate.offer(update);
	}

	@Override
	public void run() {
		try {
			while (!isInterrupted()) {
				if (!chunksToGenerate.isEmpty()) {
					ChunkUpdate update = chunksToGenerate.poll();
					update.planet.generateChunk(update.chunkLoc, update.lodLevel);
				} else {
					Thread.sleep(300);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
