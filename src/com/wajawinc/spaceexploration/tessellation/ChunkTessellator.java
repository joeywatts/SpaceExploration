package com.wajawinc.spaceexploration.tessellation;

import com.wajawinc.spaceexploration.universe.Chunk;

public interface ChunkTessellator
{
	public static final int LOD_LEVEL_HIGHEST = 1;
	public static final int LOD_LEVEL_MEDIUM = 4;
	public static final int LOD_LEVEL_LOWEST = 8;
	
    public void tessellateChunk(Chunk c, int lodLevel);
    public float[] getVertices();
    public float[] getTextureCoords();
    public float[] getColors();
    public float[] getNormals();
    public int[] getIndices();
}
