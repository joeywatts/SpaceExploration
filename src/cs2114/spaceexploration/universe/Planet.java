package cs2114.spaceexploration.universe;

//Class depends upon the Rajawali 3D library (stable v0.7).

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.math.Number3D;
import android.util.Log;
import cs2114.spaceexploration.tessellation.ChunkTessellator;
import cs2114.spaceexploration.tessellation.MarchingCubesChunkTessellator;
import cs2114.spaceexploration.universe.generator.PlanetGenerator;

// -------------------------------------------------------------------------
/**
 * The Planet class defines a structure that stores all the relevant data
 * involved in generating, storing, and displaying a Planet, both logically and
 * graphically.
 */
public class Planet extends BaseObject3D {
	public static final int MAX_PLANET_SIZE = 500;
	public static final int MIN_PLANET_SIZE = 200;

	private Universe universe;
	private PlanetGenerator generator;
	
	private Number3D center;

	private Map<Number3D, Chunk> chunks;

	private DirectionalLight light;
	private Number3D lightDir = new Number3D();

	/* the scale of the planet before the far plane hack (see the update method) */
	private float fullPlanetScale;

	/*
	 * A single chunk to show the planet at a low detail from a distance.
	 */
	private Chunk previewChunk;

	/**
	 * Instantiates a new Planet object.
	 *
	 * @param universe
	 *            the universe that the planet is in.
	 * @param center
	 *            the center of the planet, relative to the "center" of the
	 *            universe.
	 * @param generator
	 *            the object that generates the planet's terrain
	 */
	public Planet(Universe universe, Number3D center, PlanetGenerator generator) {
		this.universe = universe;
		this.setPosition(this.center = center);
		fullPlanetScale = 4.0f;
		setScale(fullPlanetScale);
		this.generator = generator;
		chunks = new ConcurrentHashMap<Number3D, Chunk>();
		previewChunk = generator.generatePreview(this);
		addChild(previewChunk);
		lightDir.x = 1;
		lightDir.y = 0.2f;
		lightDir.z = -1f;
		light = new DirectionalLight(lightDir.x, lightDir.y, lightDir.z);
		light.setColor(1.0f, 1.0f, 1.0f);
		light.setPower(2);
	}

	/**
	 * Generates a Chunk for the Planet. It should be noted that this method is
	 * synchronous, so it should never be called on the render thread. See the
	 * ChunkGeneratorThread class.
	 *
	 * @param loc
	 *            the location of the Chunk being generated.
	 * @param lodLevel
	 *            the Level of Detail to tessellate the chunk at (Must be a
	 *            constant from ChunkTessellator.)
	 */
	public void generateChunk(Number3D loc, int lodLevel) {
		Chunk c = new Chunk(this, loc);
		generator.generateChunk(c);
		if (!c.tessellate(lodLevel)) {
			Log.d("Didn't generate", loc.toString());
			return;
		}
		c.addLight(light);
		addChild(c);
		chunks.put(loc, c);
	}

	public Map<Number3D, Chunk> getChunks() {
		return chunks;
	}

	/**
	 * Gets the object that generates this Planet's terrain.
	 *
	 * @return the PlanetGenerator object.
	 */
	public PlanetGenerator getPlanetGenerator() {
		return generator;
	}

	/**
	 * Gets the object that creates the isosurface from each chunk's voxel data.
	 *
	 * @return the ChunkTessellator object.
	 */
	public ChunkTessellator getChunkTessellator() {
		return new MarchingCubesChunkTessellator();
	}

	/**
	 * Checks if the preview Chunk is showing.
	 * 
	 * @return true if the preview Chunk is showing.
	 */
	public boolean isShowingPreview() {
		return previewChunk != null;
	}

	/**
	 * Generates all of the planet's chunks and removes the preview chunk.
	 */
	public void generatePlanet() {
		int chunkNum = (int) Math.ceil(generator.getPlanetSize()
				/ (float) Chunk.SIZE);
		for (int cx = (int) -Math.floor(chunkNum / 2.0f); cx <= (int) Math
				.ceil(chunkNum / 2.0f); cx++) {
			for (int cy = (int) -Math.floor(chunkNum / 2.0f); cy <= (int) Math
					.ceil(chunkNum / 2.0f); cy++) {
				for (int cz = (int) -Math.floor(chunkNum / 2.0f); cz <= (int) Math
						.ceil(chunkNum / 2.0f); cz++) {
					generateChunk(new Number3D(cx * Chunk.SIZE,
							cy * Chunk.SIZE, cz * Chunk.SIZE),
							ChunkTessellator.LOD_LEVEL_HIGHEST);
				}
			}
		}
		if (previewChunk != null) {
			removeChild(previewChunk);
			previewChunk = null;
		}
	}

	/**
	 * Updates the planet's scale and position so that it can be seen by the
	 * player and it is not clipped by the far plane.
	 * 
	 * @param playerLoc
	 *            the player's location
	 */
	public void update(Number3D playerLoc) {
		final float MAX_DIST = 900;
		float distance = playerLoc.distanceTo(center);
		if (distance > MAX_DIST) {
			Number3D position = new Number3D(center);
			position.subtract(playerLoc);
			position.normalize();
			position.multiply(MAX_DIST);
			setScale(MAX_DIST / distance * fullPlanetScale);
			setPosition(position);
		} else {
			setScale(fullPlanetScale);
			setPosition(center);
		}
	}
}
