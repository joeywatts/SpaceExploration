package cs2114.spaceexploration.universe;

//Class depends upon the Rajawali 3D library (stable v0.7).

import cs2114.spaceexploration.tessellation.ChunkTessellator;
import cs2114.spaceexploration.tessellation.MarchingCubesChunkTessellator;
import cs2114.spaceexploration.universe.generator.PlanetGenerator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.math.Number3D;
import android.util.Log;

// -------------------------------------------------------------------------
/**
 * The Planet class defines a structure that stores all the relevant data
 * involved in generating, storing, and displaying a Planet, both logically and
 * graphically.
 */
public class Planet extends BaseObject3D {
	public static final int MAX_PLANET_SIZE = 2000;
	private static final int MAX_LOD_DIST = 3 * Chunk.SIZE;
	private static final int MED_LOD_DIST = 6 * Chunk.SIZE;
	private static final int LOW_LOD_DIST = 8 * Chunk.SIZE;
	private static final int CHUNK_VIEW_DIST = LOW_LOD_DIST;

	private Universe universe;
	private PlanetGenerator generator;

	private Map<Number3D, Chunk> chunks;

	private DirectionalLight light;
	private Number3D lightDir = new Number3D();

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
		this.setPosition(center);
		setScale(4.0f);
		this.generator = generator;
		chunks = new ConcurrentHashMap<Number3D, Chunk>();

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
	 * ChunkGeneratorExecutor class.
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

	public void update(Number3D playerPos) {
		float x = playerPos.x - getPosition().x;
		float y = playerPos.y - getPosition().y;
		float z = playerPos.z - getPosition().z;
		int chunkX = (int) Math.floor(x / Chunk.SIZE) * Chunk.SIZE;
		int chunkY = (int) Math.floor(y / Chunk.SIZE) * Chunk.SIZE;
		int chunkZ = (int) Math.floor(z / Chunk.SIZE) * Chunk.SIZE;
		removeFarChunks(chunkX, chunkY, chunkZ);
		generateSurroundingChunks(chunkX, chunkY, chunkZ);
	}

	private void removeFarChunks(int chunkX, int chunkY, int chunkZ) {
		for (Number3D chunkLoc : chunks.keySet()) {
			if (Math.abs(chunkLoc.x - chunkX) > CHUNK_VIEW_DIST
					|| Math.abs(chunkLoc.y - chunkY) > CHUNK_VIEW_DIST
					|| Math.abs(chunkLoc.z - chunkZ) > CHUNK_VIEW_DIST) {
				removeChild(chunks.get(chunkLoc));
				chunks.remove(chunkLoc);
			}
		}
	}

	private void generateSurroundingChunks(int chunkX, int chunkY, int chunkZ) {
		for (int x = chunkX - CHUNK_VIEW_DIST; x <= chunkX + CHUNK_VIEW_DIST; x += Chunk.SIZE) {
			for (int y = chunkY - CHUNK_VIEW_DIST; y <= chunkY
					+ CHUNK_VIEW_DIST; y += Chunk.SIZE) {
				for (int z = chunkZ - CHUNK_VIEW_DIST; z <= chunkZ
						+ CHUNK_VIEW_DIST; z += Chunk.SIZE) {
					if (Math.abs(chunkX - x) < MAX_LOD_DIST
							&& Math.abs(chunkY - y) < MAX_LOD_DIST
							&& Math.abs(chunkZ - z) < MAX_LOD_DIST) {
						universe.getChunkGenerator().generateChunk(this,
								new Number3D(x, y, z),
								ChunkTessellator.LOD_LEVEL_HIGHEST);
					} else if (Math.abs(chunkX - x) < MED_LOD_DIST
							&& Math.abs(chunkY - y) < MED_LOD_DIST
							&& Math.abs(chunkZ - z) < MED_LOD_DIST) {
						universe.getChunkGenerator().generateChunk(
								this, new Number3D(x, y, z),
								ChunkTessellator.LOD_LEVEL_MEDIUM);
					} else {
						universe.getChunkGenerator().generateChunk(
								this, new Number3D(x, y, z),
								ChunkTessellator.LOD_LEVEL_LOWEST);
					}
				}
			}
		}
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
}
