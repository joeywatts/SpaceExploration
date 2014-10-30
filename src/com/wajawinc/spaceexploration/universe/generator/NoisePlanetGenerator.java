package com.wajawinc.spaceexploration.universe.generator;

import java.util.Random;

import rajawali.math.Number3D;

import com.wajawinc.spaceexploration.universe.Chunk;
import com.wajawinc.spaceexploration.universe.Planet;

public class NoisePlanetGenerator implements PlanetGenerator {
	private static final Random random = new Random();
	
	private static final int SAMPLE_RATE_X = 8;
	private static final int SAMPLE_RATE_Y = 8;
	private static final int SAMPLE_RATE_Z = 8;
	
	private static final int MIN_PLANET_RADIUS = 300;
	private static final int MAX_PLANET_RADIUS = Planet.MAX_PLANET_SIZE;

	private Noise densityNoise;
	private Noise tempNoise;
	private int radius;

	public NoisePlanetGenerator(int planetSeed) {
		random.setSeed(planetSeed);
		radius = random.nextInt(MAX_PLANET_RADIUS
				- MIN_PLANET_RADIUS)
				+ MIN_PLANET_RADIUS;
		densityNoise = new Noise(planetSeed, 256.1353462f);
		tempNoise = new Noise(planetSeed + 1, 128.1254f);
	}

	public void generateChunk(Chunk c) {
		Number3D loc = c.getPosition();
		int chunkX = (int) loc.x;
		int chunkY = (int) loc.y;
		int chunkZ = (int) loc.z;
		int sizeX = Chunk.SIZE / SAMPLE_RATE_X + 1;
		int sizeY = Chunk.SIZE / SAMPLE_RATE_Y + 1;
		int sizeZ = Chunk.SIZE / SAMPLE_RATE_Z + 1;
		float[][][] noiseVals = new float[sizeX][sizeY][sizeZ];
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				for (int z = 0; z < sizeZ; z++) {
					noiseVals[x][y][z] = calculateDensity(chunkX
							+ SAMPLE_RATE_X * x, chunkY + SAMPLE_RATE_Y * y,
							chunkZ + SAMPLE_RATE_Z * z);
				}
			}
		}
		for (int x = 0; x <= Chunk.SIZE; x++) {
			float xf = (float) x / SAMPLE_RATE_X;
			int x1i = (int) xf;
			int x2i = x1i + 1;
			if (x == Chunk.SIZE) {
				x2i = x1i;
			}
			float xd = xf - x1i;
			for (int y = 0; y <= Chunk.SIZE; y++) {
				float yf = (float) y / SAMPLE_RATE_Y;
				int y1i = (int) yf;
				int y2i = y1i + 1;
				if (y == Chunk.SIZE) {
					y2i = y1i;
				}
				float yd = yf - y1i;
				for (int z = 0; z <= Chunk.SIZE; z++) {
					float zf = (float) z / SAMPLE_RATE_Z;
					int z1i = (int) zf;
					int z2i = z1i + 1;
					if (z == Chunk.SIZE) {
						z2i = z1i;
					}
					float zd = zf - z1i;
					float val = 0;
					val += noiseVals[x1i][y1i][z1i] * (1 - xd) * (1 - yd)
							* (1 - zd) + noiseVals[x2i][y1i][z1i] * xd
							* (1 - yd) * (1 - zd);
					val += noiseVals[x1i][y2i][z1i] * (1 - xd) * yd * (1 - zd)
							+ noiseVals[x2i][y2i][z1i] * xd * yd * (1 - zd);
					val += noiseVals[x1i][y1i][z2i] * (1 - xd) * (1 - yd) * zd
							+ noiseVals[x2i][y1i][z2i] * xd * (1 - yd) * zd;
					val += noiseVals[x1i][y2i][z2i] * (1 - xd) * yd * zd
							+ noiseVals[x2i][y2i][z2i] * xd * yd * zd;
					c.setDensity(x, y, z, val);
				}
			}
		}
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					c.setTemperature(
							x,
							y,
							z,
							calculateTemperature(chunkX + x * Chunk.SIZE,
									chunkY + y * Chunk.SIZE, chunkZ + z
											* Chunk.SIZE));
				}
			}
		}

	}

	public float calculateDensity(int x, int y, int z) {
		float noise = densityNoise.getNoiseValue(x, y, z);
		float adjustedNoise = (float) Math.cos(noise) * 0.5f + 0.5f;
		float dist = (float) Math.sqrt(x * x + y * y + z * z) / radius;
		if (dist > 1)
			dist = 1;
		float val = (.65f * adjustedNoise + .35f * dist);
		return 0.85f - val;
	}

	public float calculateTemperature(int x, int y, int z) {
		return tempNoise.getNoiseValue(x, y, z);
	}
}
