package cs2114.spaceexploration.universe;

// Class depends upon the Rajawali 3D library (stable v0.7).

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
 */
public class Universe
{
    /* Size of each cubic divsion of the universe */
    private static final int         UNIVERSE_DIVISION_SIZE = 4000;
    /* Padding on each sides of each cubic division of the universe */
    private static final int         DIVISION_PADDING       = 800;

    private static final int         PLANET_VIEW_DISTANCE   = 1;

    private SpaceExplorationRenderer renderer;
    private Map<Number3D, Planet>    planets;
    private int                      universeSeed;
    private Noise                    universeNoise;

    private ChunkGeneratorThread     chunkGenerator;
    private UniverseUpdater          updater;


    /**
     * Instantiate a new Universe.
     *
     * @param universeSeed
     *            the random seed of the Universe.
     * @param renderer
     *            the Renderer object
     */
    public Universe(int universeSeed, SpaceExplorationRenderer renderer)
    {
        this.universeSeed = universeSeed;
        this.renderer = renderer;
        planets = new ConcurrentHashMap<Number3D, Planet>();
        universeNoise = new Noise(universeSeed, 128.1245f);
    }


    /**
     * A synchronous update method for the Universe. Generates and removes
     * planets based on the Player's location. Called on a background thread.
     *
     * @param player
     *            the Player
     */
    public void update(Player player)
    {
        int divX = Math.round(player.getPosition().x / UNIVERSE_DIVISION_SIZE);
        int divY = Math.round(player.getPosition().y / UNIVERSE_DIVISION_SIZE);
        int divZ = Math.round(player.getPosition().z / UNIVERSE_DIVISION_SIZE);
        int minPlanetDivX =
            (divX - PLANET_VIEW_DISTANCE) * UNIVERSE_DIVISION_SIZE;
        int maxPlanetDivX =
            (divX + PLANET_VIEW_DISTANCE) * UNIVERSE_DIVISION_SIZE;
        int maxPlanetDivY =
            (divY + PLANET_VIEW_DISTANCE) * UNIVERSE_DIVISION_SIZE;
        int minPlanetDivY =
            (divY - PLANET_VIEW_DISTANCE) * UNIVERSE_DIVISION_SIZE;
        int minPlanetDivZ =
            (divZ - PLANET_VIEW_DISTANCE) * UNIVERSE_DIVISION_SIZE;
        int maxPlanetDivZ =
            (divZ + PLANET_VIEW_DISTANCE) * UNIVERSE_DIVISION_SIZE;
        for (Number3D key : planets.keySet())
        {
            if (key.x < minPlanetDivX
                || key.x > maxPlanetDivX
                || key.y < minPlanetDivY
                || key.y > maxPlanetDivY
                || key.z < minPlanetDivZ
                || key.z > maxPlanetDivZ)
            {
                renderer.removeChild(planets.remove(key));
            }
        }
        for (int x = minPlanetDivX; x <= maxPlanetDivX; x +=
            UNIVERSE_DIVISION_SIZE)
        {
            for (int y = minPlanetDivY; y <= maxPlanetDivY; y +=
                UNIVERSE_DIVISION_SIZE)
            {
                for (int z = minPlanetDivZ; z <= maxPlanetDivZ; z +=
                    UNIVERSE_DIVISION_SIZE)
                {
                    Number3D planetKey = new Number3D(x, y, z);
                    if (!planets.containsKey(planetKey))
                    {
                        generatePlanet(planetKey);
                    }
                }
            }
        }
    }


    /**
     * A synchronous method that should be called every frame to update all the
     * planet's positions and scales so that they can be seen by the player even
     * if their real center is farther than the far plane.
     *
     * @param playerLoc
     *            the player's location.
     */
    public void updatePlanets(Number3D playerLoc)
    {
        for (Planet planet : planets.values())
        {
            planet.update(playerLoc);
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
    private Number3D getPlanetLocation(int x, int y, int z)
    {
        float xOffset =
            (UNIVERSE_DIVISION_SIZE - DIVISION_PADDING * 2)
                * (universeNoise.getNoiseValue(x, y, z) - 0.5f);
        float yOffset =
            (UNIVERSE_DIVISION_SIZE - DIVISION_PADDING * 2)
                * (universeNoise.getNoiseValue(y, z, x) - 0.5f);
        float zOffset =
            (UNIVERSE_DIVISION_SIZE - DIVISION_PADDING * 2)
                * (universeNoise.getNoiseValue(z, x, y) - 0.5f);
        return new Number3D(x + xOffset, y + yOffset, z + zOffset);
    }


    /**
     * Starts the Universe updater and Chunk generation background threads.
     */
    public void startUpdater()
    {
        updater = new UniverseUpdater(this, renderer.getPlayer());
        updater.start();
        chunkGenerator = new ChunkGeneratorThread();
        chunkGenerator.start();
    }


    /**
     * Stops the Universe updater and Chunk generation background threads.
     */
    public void stopUpdater()
    {
        updater.interrupt();
        updater = null;
        chunkGenerator.interrupt();
        chunkGenerator = null;
    }


    /**
     * Generates a planet at a certain Universe grid location.
     *
     * @param key
     *            the grid location
     */
    public void generatePlanet(Number3D key)
    {
        Number3D center = getPlanetLocation((int)key.x, (int)key.y, (int)key.z);
        Log.d("center", center.toString());
        Planet planet =
            new Planet(this, center, new NoisePlanetGenerator(universeSeed * 31
                + center.hashCode()));
        /* Asynchronously generate a Chunk so the player can see the planet. */
        chunkGenerator.generatePlanetPreview(planet);
        planets.put(key, planet);
        renderer.addChild(planet);
    }


    /**
     * Gets a Map with the Universe grid location as the key and the Planet as
     * the values.
     *
     * @return the Map of the loaded Planets in the universe.
     */
    public Map<Number3D, Planet> getPlanets()
    {
        return planets;
    }


    /**
     * Gets the Chunk generator thread.
     *
     * @return the Chunk generator thread.
     */
    public ChunkGeneratorThread getChunkGenerator()
    {
        return chunkGenerator;
    }
}
