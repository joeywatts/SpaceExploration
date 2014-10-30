package com.wajawinc.spaceexploration.universe;

import com.wajawinc.spaceexploration.tessellation.ChunkTessellator;
import com.wajawinc.spaceexploration.universe.generator.PlanetGenerator;
import rajawali.BaseObject3D;
import rajawali.materials.DiffuseMaterial;
import rajawali.math.Number3D;

public class Chunk
    extends BaseObject3D
{
    public static final int SIZE = 32;

    private Planet           planet;

    private float[][][]        data;


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
        data = new float[SIZE+1][SIZE+1][SIZE+1];
    }


    /**
     * Generates the voxel data structure for this chunk of the planet.
     */
    /*public void generate()
    {
        PlanetGenerator generator = planet.getPlanetGenerator();
        int planetX = (int)getPosition().x;
        int planetY = (int)getPosition().y;
        int planetZ = (int)getPosition().z;
        for (int x = 0; x < SIZE+1; x++)
        {
            for (int y = 0; y < SIZE+1; y++)
            {
                for (int z = 0; z < SIZE+1; z++)
                {
                    data[x][y][z] =
                        generator.getValue(x + planetX, y + planetY, z
                            + planetZ);
                }
            }
        }
    }
*/

    public void setValue(int x, int y, int z, float val) {
        data[x][y][z] = val;
    }

    public float getValue(int x, int y, int z) {
        return data[x][y][z];
    }

    /**
     * Generates the isosurface for this Chunk.
     * @return true if an isosurface was generated.
     */
    public boolean tessellate()
    {
        ChunkTessellator tessellator = planet.getChunkTessellator();
        tessellator.tessellateChunk(this);
        if (tessellator.getVertices() == null) {
            return false;
        }
        setData(
            tessellator.getVertices(),
            tessellator.getNormals(),
            tessellator.getTextureCoords(),
            tessellator.getColors(),
            tessellator.getIndices());
        //SimpleMaterial mat = new SimpleMaterial();
        DiffuseMaterial mat = new DiffuseMaterial();
        mat.setUseColor(true);
        mat.setAmbientColor(1, 1, 1, 1);
        mat.setAmbientIntensity(0.5f);
        setMaterial(mat);
        setBackSided(true);
        return true;
    }

}
