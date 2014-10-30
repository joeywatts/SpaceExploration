package com.wajawinc.spaceexploration.universe.generator;

import com.wajawinc.spaceexploration.universe.Chunk;
import rajawali.math.Number3D;

public interface PlanetGenerator
{
    public void generateChunk(Chunk c, Number3D loc);
    public float getValue(int x, int y, int z);
}
