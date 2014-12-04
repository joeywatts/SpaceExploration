package cs2114.spaceexploration.universe.generator;

import cs2114.spaceexploration.universe.Chunk;
import cs2114.spaceexploration.universe.Planet;

/**
 * PlanetGenerator is an interface that facilitates the generation of a Planet.
 * A PlanetGenerator is sent as a parameter in the constructor to the Planet
 * class, and is called by the Planet when new Chunks are generated.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 17, 2014
 */
public interface PlanetGenerator
{
    /**
     * Gets the size of the planet.
     *
     * @return the size of the planet.
     */
    public int getPlanetSize();


    /**
     * Generates the Chunk. Usually involves setting the density values for each
     * point and the temperature values for each corner.
     *
     * @param c
     *            the Chunk to generate.
     */
    public void generateChunk(Chunk c);


    /**
     * Generates the preview chunk for the Planet.
     *
     * @param p
     *            the Planet.
     * @return the preview Chunk.
     */
    public Chunk generatePreview(Planet p);
}
