package cs2114.spaceexploration.universe.generator;

//Class depends upon the Rajawali 3D library (stable v0.7).

import android.util.Log;
import cs2114.spaceexploration.tessellation.ChunkTessellator;
import cs2114.spaceexploration.universe.Chunk;
import cs2114.spaceexploration.universe.Planet;
import java.util.Random;
import rajawali.math.Number3D;

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
		radius = 3 * Chunk.SIZE;
		densityNoise = new Noise(planetSeed, 64.1353462f);
		tempNoise = new Noise(planetSeed + 1, 4.1254f);
	}

	public int getPlanetSize() {
		return radius * 2;
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

	@Override
	public Chunk generatePreview(Planet planet) {
		Chunk chunk = new Chunk(planet, new Number3D());
		float interval = 2 * radius / Chunk.SIZE;
		for (int x = 0; x < Chunk.SIZE; x++) {
			for (int y = 0; y < Chunk.SIZE; y++) {
				for (int z = 0; z < Chunk.SIZE; z++) {
					chunk.setDensity(x, y, z, calculateDensity((int) (x
							* interval - radius),
							(int) (y * interval - radius), (int) (z
									* interval - radius)));
				}
			}
		}
		for (int x = 0; x < 2; x++) {
			for (int y = 0; y < 2; y++) {
				for (int z = 0; z < 2; z++) {
					chunk.setTemperature(
							x,
							y,
							z,
							calculateTemperature(-radius + x * radius * 2,
									-radius + y * radius * 2, -radius + z
											* radius * 2));
				}
			}
		}
		chunk.tessellate(ChunkTessellator.LOD_LEVEL_HIGHEST);
		chunk.setScale(interval);
		return chunk;
	}

	public float calculateDensity(int x, int y, int z) {
		if (x <= -3 * Chunk.SIZE || x >= Chunk.SIZE * 4 || y <= -3 * Chunk.SIZE
				|| y >= 4 * Chunk.SIZE || z <= -3 * Chunk.SIZE
				|| z >= 4 * Chunk.SIZE)
			return 0;
		float noise = densityNoise.getNoiseValue(x, y, z);
		float adjustedNoise = (float) Math.cos(noise) * 0.5f + 0.5f;
		float dist = (float) Math.sqrt(x * x + y * y + z * z) / radius;
		if (dist > 1)
			dist = 1;
		float val = (0.7f * adjustedNoise + dist * 0.3f);
		return .85f - val;
	}

	public float calculateTemperature(int x, int y, int z) {
		return tempNoise.getNoiseValue(x*1.2345667f, y*0.456543f, z*0.46731f);
	}
}
