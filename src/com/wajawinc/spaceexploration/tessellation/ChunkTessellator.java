package com.wajawinc.spaceexploration.tessellation;

import com.wajawinc.spaceexploration.universe.Chunk;

public interface ChunkTessellator
{
    public void tessellateChunk(Chunk c);
    public float[] getVertices();
    public float[] getTextureCoords();
    public float[] getColors();
    public float[] getNormals();
    public int[] getIndices();
}
