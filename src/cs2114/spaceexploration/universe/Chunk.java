package cs2114.spaceexploration.universe;

// Class depends upon the Rajawali 3D library (stable v0.7).

import cs2114.spaceexploration.tessellation.ChunkTessellator;
import rajawali.BaseObject3D;
import rajawali.materials.DiffuseMaterial;
import rajawali.math.Number3D;

/**
 * A Chunk represents a section of Planet. Planets are split into Chunks in
 * order to improve rendering performance and memory consumption.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 17, 2014
 */
public class Chunk
    extends BaseObject3D
{
    /**
     * The size of every Chunk. (a Chunk is a cube).
     */
    public static final int SIZE = 32;

    private Planet          planet;

    private float[][][]     density;
    private float[][][]     temperature;


    /**
     * Instantiates a new Chunk at a given distance from the planet's center.
     *
     * @param planet
     *            the planet that this chunk belongs to.
     * @param location
     *            the location of this chunk relative the center of the planet.
     */
    public Chunk(Planet planet, Number3D location)
    {
        this.planet = planet;
        setPosition(location);
        density = new float[SIZE + 1][SIZE + 1][SIZE + 1];
        /*
         * Stores the temperature values for all the corners of this chunk to
         * interpolate between them.
         */
        temperature = new float[2][2][2];
    }


    /**
     * Sets the temperature at a corner of the chunk.
     *
     * @param x
     *            identifies the x coordinate (either 0 or 1).
     * @param y
     *            identifies the y coordinate (either 0 or 1).
     * @param z
     *            identifies the z coordinate (either 0 or 1).
     * @param value
     *            the temperature to set.
     */
    public void setTemperature(int x, int y, int z, float value)
    {
        temperature[x][y][z] = value;
    }


    /**
     * Sets the density value at a certain position within the chunk.
     *
     * @param x
     *            the x coordinate within the chunk.
     * @param y
     *            the y coordinate within the chunk.
     * @param z
     *            the z coordinate within the chunk.
     * @param val
     *            the density value to set.
     */
    public void setDensity(int x, int y, int z, float val)
    {
        density[x][y][z] = val;
    }


    /**
     * Gets the density value at a point within the chunk.
     *
     * @param x
     *            the x coordinate within the chunk.
     * @param y
     *            the y coordinate within the chunk.
     * @param z
     *            the z coordiante within the chunk.
     * @return the density value
     */
    public float getDensity(int x, int y, int z)
    {
        return density[x][y][z];
    }


    /**
     * Gets the temperature at a point within the chunk by interpolating between
     * the sampled temperatures at the corners of the chunk.
     *
     * @param x
     *            the x coordinate within the chunk.
     * @param y
     *            the y coordinate within the chunk.
     * @param z
     *            the z coordinate within the chunk.
     * @return a temperature value in the range [0, 1]
     */
    public float getTemperature(int x, int y, int z)
    {
        float xInterp = (float)x / (SIZE + 1);
        float yInterp = (float)y / (SIZE + 1);
        float zInterp = (float)z / (SIZE + 1);
        float temp = 0;
        for (int i = 0; i < 2; i++)
        {
            float widthInterp = xInterp;
            if (i == 0)
            {
                widthInterp = 1 - xInterp;
            }
            for (int j = 0; j < 2; j++)
            {
                float heightInterp = yInterp;
                if (j == 0)
                {
                    heightInterp = 1 - yInterp;
                }
                for (int k = 0; k < 2; k++)
                {
                    float depthInterp = zInterp;
                    if (k == 0)
                    {
                        depthInterp = 1 - zInterp;
                    }
                    temp +=
                        temperature[i][j][k] * widthInterp * heightInterp
                            * depthInterp;
                }
            }
        }
        return temp;
    }


    /**
     * Generates the isosurface for this Chunk.
     *
     * @param lodLevel
     *            the Level of Detail to tessellate the Chunk at (Must be a
     *            constant from ChunkTessellator).
     * @return true if an isosurface was generated.
     */
    public boolean tessellate(int lodLevel)
    {
        ChunkTessellator tessellator = planet.getChunkTessellator();
        tessellator.tessellateChunk(this, lodLevel);
        if (tessellator.getVertices() == null)
        {
            return false;
        }
        setData(
            tessellator.getVertices(),
            tessellator.getNormals(),
            tessellator.getTextureCoords(),
            tessellator.getColors(),
            tessellator.getIndices());
        DiffuseMaterial mat = new DiffuseMaterial();
        mat.setUseColor(true);
        mat.setAmbientColor(1, 1, 1, 1);
        mat.setAmbientIntensity(0.5f);
        setMaterial(mat);
        setDoubleSided(true);
        return true;
    }

}
