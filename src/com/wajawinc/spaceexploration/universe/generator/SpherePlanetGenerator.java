package com.wajawinc.spaceexploration.universe.generator;

import com.wajawinc.spaceexploration.universe.Chunk;
import rajawali.math.Number3D;

public class SpherePlanetGenerator implements PlanetGenerator
{

    public SpherePlanetGenerator()
    {
    }

    public void generateChunk(Chunk c)
    {
    	Number3D loc = c.getPosition();
        int chunkX = (int) loc.x;
        int chunkY = (int) loc.y;
        int chunkZ = (int) loc.z;
        for (int x = 0; x <= Chunk.SIZE; x++) {
            for (int y = 0; y <= Chunk.SIZE; y++) {
                for (int z = 0; z <= Chunk.SIZE; z++) {
                    c.setDensity(x, y, z, calculateDensity(x+chunkX, y+chunkY, z+chunkZ));
                }
            }
        }
    }

    public float calculateDensity(int x, int y, int z)
    {
        if (x >= 60 && x < 64 && y >= 60 && z < 64 && z >= 60 && z < 64) {
            return 1;
        }
        float val = (float)Math.sqrt(x*x+y*y+z*z);
        return 64.0f - val;
    }

}
