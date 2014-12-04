package cs2114.spaceexploration.tessellation;

import cs2114.spaceexploration.universe.Chunk;

// -------------------------------------------------------------------------
/**
 * ChunkTessellator is an interface that abstracts the implementation of the
 * conversion of the scalar voxel field to polygons. Due to the arbitrary nature
 * of the functionality of the implementing classes, the implementing classes
 * cannot be tested.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 17, 2014
 */
public interface ChunkTessellator
{

    /**
     * Convert the Chunk data into the polygons.
     *
     * @param c
     *            the Chunk to tessellate.
     * @param lodLevel
     *            the level of detail of the polygon mesh.
     */
    public void tessellateChunk(Chunk c, int lodLevel);


    /**
     * Get the calculated vertices.
     *
     * @return the vertices
     */
    public float[] getVertices();


    /**
     * Get the texture coordinates.
     *
     * @return the texture coordinates
     */
    public float[] getTextureCoords();


    /**
     * Get the colors.
     *
     * @return the colors.
     */
    public float[] getColors();


    /**
     * Get the normals.
     *
     * @return the normals.
     */
    public float[] getNormals();


    /**
     * Get the indices
     *
     * @return the indices
     */
    public int[] getIndices();
}
