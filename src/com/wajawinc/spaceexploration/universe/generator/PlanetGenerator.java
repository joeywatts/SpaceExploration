package com.wajawinc.spaceexploration.universe.generator;

import com.wajawinc.spaceexploration.universe.Chunk;

/**
 * PlanetGenerator is an interface that facilitates the generation of a Planet.
 * A PlanetGenerator is sent as a parameter in the constructor to the Planet
 * class, and is called by the Planet when new Chunks are generated.
 */
public interface PlanetGenerator {
	/**
	 * Generates the Chunk. Usually involves setting the density values for each
	 * point and the temperature values for each corner.
	 * 
	 * @param c
	 *            the Chunk to generate.
	 */
	public void generateChunk(Chunk c);
}
