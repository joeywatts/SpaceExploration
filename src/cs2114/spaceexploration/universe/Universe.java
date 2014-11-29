package cs2114.spaceexploration.universe;

//Class depends upon the Rajawali 3D library (stable v0.7).

import cs2114.spaceexploration.SpaceExplorationRenderer;
import cs2114.spaceexploration.entity.Player;
import cs2114.spaceexploration.universe.generator.Noise;
import cs2114.spaceexploration.universe.generator.NoisePlanetGenerator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import rajawali.math.Number3D;
import android.util.Log;

// -------------------------------------------------------------------------
/**
 * The Universe class defines a structure that stores all the relevant data
 * involved in generating, storing, and displaying all of the Planets in the
 * game's universe, both logically and graphically.
 *
 */
public class Universe {
	private static final int UNIVERSE_DIVISION_SIZE = 5000;
	/* Noise samples to generate per division. */
	private static final int SAMPLES_PER_DIVISION = 25;
	/* Closest distance that one planet is allowed to be from another */
	private static final int CLOSEST_DISTANCE_ALLOWED = Planet.MAX_PLANET_SIZE * 3 / 2;

	private SpaceExplorationRenderer renderer;
	private Map<Number3D, Planet> planets;
	private int universeSeed;
	private Noise universeNoise;
	private Number3D universeGridLocation;

	private Number3D generationList[];

	private ChunkGeneratorThread chunkGenerator;
	private UniverseUpdater updater;

	public Universe(int universeSeed, SpaceExplorationRenderer renderer) {
		this.universeSeed = universeSeed;
		this.renderer = renderer;
		planets = new ConcurrentHashMap<Number3D, Planet>();
		universeNoise = new Noise(universeSeed, 128.1245f);
		generationList = new Number3D[SAMPLES_PER_DIVISION];
	}

	public void update(Player p) {
		int lastGridX = -1;
		int lastGridY = -1;
		int lastGridZ = -1;
		if (universeGridLocation != null) {
			lastGridX = (int) universeGridLocation.x;
			lastGridY = (int) universeGridLocation.y;
			lastGridZ = (int) universeGridLocation.z;
		} else {
			universeGridLocation = new Number3D();
		}
		Number3D playerPos = p.getPosition();
		universeGridLocation.x = Math.round(playerPos.x / UNIVERSE_DIVISION_SIZE);
		universeGridLocation.y = Math.round(playerPos.y / UNIVERSE_DIVISION_SIZE);
		universeGridLocation.z = Math.round(playerPos.z / UNIVERSE_DIVISION_SIZE);
		int currentGridX = (int) universeGridLocation.x;
		int currentGridY = (int) universeGridLocation.y;
		int currentGridZ = (int) universeGridLocation.z;
		if (currentGridX != lastGridX || currentGridY != lastGridY || currentGridZ != lastGridZ) {
			/* Populate new list of planets. */
			generateNewPlanetsList(currentGridX, currentGridY, currentGridZ);
		}
		/* Destroy planets that are too far away from the player. */
		/* Load planet chunks. */
		updatePlanets(playerPos);
	}

	private void generateNewPlanetsList(int divisionX, int divisionY, int divisionZ) {
		int x = divisionX * UNIVERSE_DIVISION_SIZE;
		int y = divisionY * UNIVERSE_DIVISION_SIZE;
		int z = divisionZ * UNIVERSE_DIVISION_SIZE;
		int generationListIndex = 0;
		noiseSample:
		for (int i = 0; i < SAMPLES_PER_DIVISION; i++) {
			generationList[i] = null;
			float planetX = x + universeNoise.getNoiseValue(x + i + 1, y + i, z + i) * UNIVERSE_DIVISION_SIZE;
			float planetY = y + universeNoise.getNoiseValue(x + i, y + i + 1, z + i) * UNIVERSE_DIVISION_SIZE;
			float planetZ = z + universeNoise.getNoiseValue(x + i, y + i, z + i + 1) * UNIVERSE_DIVISION_SIZE;
			for (int j = generationListIndex - 1; j >= 0; j--) {
				if (generationList[j] != null &&
						Math.abs(planetX - generationList[j].x) < CLOSEST_DISTANCE_ALLOWED &&
						Math.abs(planetY - generationList[j].y) < CLOSEST_DISTANCE_ALLOWED &&
						Math.abs(planetZ - generationList[j].z) < CLOSEST_DISTANCE_ALLOWED) {
					/* This planet is too close to another one. Skip it. */
					continue noiseSample;
				}
			}
			generationList[generationListIndex++] = new Number3D(planetX, planetY, planetZ);
			generatePlanet(generationList[generationListIndex-1]);
		}
	}

	private void updatePlanets(Number3D playerPos) {
		for (Number3D planetPos : planets.keySet()) {
			if (Math.abs(planetPos.x - playerPos.x) > UNIVERSE_DIVISION_SIZE ||
					Math.abs(planetPos.y - playerPos.y) > UNIVERSE_DIVISION_SIZE ||
					Math.abs(planetPos.z - playerPos.z) > UNIVERSE_DIVISION_SIZE) {
				Log.d("updatePlanets", "Destroying planet: " + playerPos);
				destroyPlanet(planetPos);
			} else {
				Log.d("updatePlanets", "Calling planet.update: " + playerPos);
				planets.get(planetPos).update(playerPos);
			}
		}
	}

	public void startUpdater() {
		updater = new UniverseUpdater(this, renderer.getPlayer());
		updater.start();
		chunkGenerator = new ChunkGeneratorThread();
		chunkGenerator.start();
	}

	public void stopUpdater() {
		updater.interrupt();
		updater = null;
		chunkGenerator.interrupt();
		chunkGenerator = null;
	}

	public void destroyPlanet(Number3D center) {
		destroyPlanet(planets.get(center));
	}

	public void destroyPlanet(Planet planet) {
		planets.remove(planet.getPosition());
		renderer.removeChild(planet);
	}

	public void generatePlanet(Number3D center) {
		Planet planet = new Planet(this, center, new NoisePlanetGenerator(universeSeed * 31 + center.hashCode()));
		planets.put(center, planet);
		renderer.addChild(planet);
	}

	public Map<Number3D, Planet> getPlanets() {
		return planets;
	}

	public ChunkGeneratorThread getChunkGenerator() {
		return chunkGenerator;
	}
}
