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
	/* Size of each cubic divsion of the universe */
	private static final int UNIVERSE_DIVISION_SIZE = 5000;
	/* Padding on each sides of each cubic division of the universe */
	private static final int DIVISION_PADDING = 1000;

	private static final int PLANET_VIEW_DISTANCE = 1;

	private SpaceExplorationRenderer renderer;
	private Map<Number3D, Planet> planets;
	private int universeSeed;
	private Noise universeNoise;

	private ChunkGeneratorThread chunkGenerator;
	private UniverseUpdater updater;

	public Universe(int universeSeed, SpaceExplorationRenderer renderer) {
		this.universeSeed = universeSeed;
		this.renderer = renderer;
		planets = new ConcurrentHashMap<Number3D, Planet>();
		universeNoise = new Noise(universeSeed, 128.1245f);
	}

	public void update(Player p) {
		int divX = (int) Math.round(p.getPosition().x / UNIVERSE_DIVISION_SIZE);
		int divY = (int) Math.round(p.getPosition().y / UNIVERSE_DIVISION_SIZE);
		int divZ = (int) Math.round(p.getPosition().z / UNIVERSE_DIVISION_SIZE);
		int minPlanetDivX = (divX - PLANET_VIEW_DISTANCE)
				* UNIVERSE_DIVISION_SIZE;
		int maxPlanetDivX = (divX + PLANET_VIEW_DISTANCE)
				* UNIVERSE_DIVISION_SIZE;
		int maxPlanetDivY = (divY + PLANET_VIEW_DISTANCE)
				* UNIVERSE_DIVISION_SIZE;
		int minPlanetDivY = (divY - PLANET_VIEW_DISTANCE)
				* UNIVERSE_DIVISION_SIZE;
		int minPlanetDivZ = (divZ - PLANET_VIEW_DISTANCE)
				* UNIVERSE_DIVISION_SIZE;
		int maxPlanetDivZ = (divZ + PLANET_VIEW_DISTANCE)
				* UNIVERSE_DIVISION_SIZE;
		for (Number3D key : planets.keySet()) {
			if (key.x < minPlanetDivX || key.x > maxPlanetDivX || key.y < minPlanetDivY || key.y > maxPlanetDivY || 
					key.z < minPlanetDivZ || key.z > maxPlanetDivZ) {
				renderer.removeChild(planets.remove(key));
			}
		}
		for (int x = minPlanetDivX; x <= maxPlanetDivX; x += UNIVERSE_DIVISION_SIZE) {
			for (int y = minPlanetDivY; y <= maxPlanetDivY; y += UNIVERSE_DIVISION_SIZE) {
				for (int z = minPlanetDivZ; z <= maxPlanetDivZ; z += UNIVERSE_DIVISION_SIZE) {
					Number3D planetKey = new Number3D(x, y, z);
					if (!planets.containsKey(planetKey)) {
						generatePlanet(planetKey);
					}
				}
			}
		}
		for (Planet planet : planets.values()) {
			planet.update(p.getPosition());
		}
	}

	/**
	 * Gets the Planet location at a certain division location. (relative to the
	 * center of the universe).
	 * 
	 * @param x
	 *            the x-component of the division location.
	 * @param y
	 *            the y-component of the division location.
	 * @param z
	 *            the z-component of the division location.
	 * @return the location of the Planet's center as a Number3D.
	 */
	private Number3D getPlanetLocation(int x, int y, int z) {
		float xOffset = (UNIVERSE_DIVISION_SIZE - DIVISION_PADDING * 2)
				* (universeNoise.getNoiseValue(x, y, z) - 0.5f);
		float yOffset = (UNIVERSE_DIVISION_SIZE - DIVISION_PADDING * 2)
				* (universeNoise.getNoiseValue(y, z, x) - 0.5f);
		float zOffset = (UNIVERSE_DIVISION_SIZE - DIVISION_PADDING * 2)
				* (universeNoise.getNoiseValue(z, x, y) - 0.5f);
		return new Number3D(x + xOffset, y + yOffset, z + zOffset);
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

	public void generatePlanet(Number3D key) {
		Number3D center = getPlanetLocation((int) key.x, (int) key.y,
				(int) key.z);
		Planet planet = new Planet(this, center, new NoisePlanetGenerator(
				universeSeed * 31 + center.hashCode()));
		planets.put(key, planet);
		renderer.addChild(planet);
	}

	public Map<Number3D, Planet> getPlanets() {
		return planets;
	}

	public ChunkGeneratorThread getChunkGenerator() {
		return chunkGenerator;
	}
}
