package com.wajawinc.spaceexploration.universe.generator;

import android.util.Log;
import com.wajawinc.spaceexploration.universe.Chunk;
import rajawali.math.Number3D;

public class NoisePlanetGenerator
    implements PlanetGenerator
{
    private static final int SAMPLE_RATE_X = 8;
    private static final int SAMPLE_RATE_Y = 8;
    private static final int SAMPLE_RATE_Z = 8;

    private Noise n;
    private int   radius;


    public NoisePlanetGenerator()
    {
        n = new Noise(256, 256.1353462f);
        radius = 32*5;
    }

    private static float turb(float x, float y, float z, float turb) {
        float currentTurb = turb;
        float noise = 0;
        while (currentTurb >= 1) {
            noise += FastNoise.noise(x/121.21254f, y/121.214543f, z/121.1256f)*currentTurb;
            currentTurb/=2;
        }
        return noise/turb;
    }

    public void generateChunk(Chunk c, Number3D loc)
    {
        int chunkX = (int) loc.x;
        int chunkY = (int) loc.y;
        int chunkZ = (int) loc.z;
        int sizeX = Chunk.SIZE/SAMPLE_RATE_X+1;
        int sizeY = Chunk.SIZE/SAMPLE_RATE_Y+1;
        int sizeZ = Chunk.SIZE/SAMPLE_RATE_Z+1;
        float[][][] noiseVals = new float[sizeX][sizeY][sizeZ];
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                for (int z = 0; z < sizeZ; z++) {
                    noiseVals[x][y][z] = getValue(chunkX + SAMPLE_RATE_X*x, chunkY + SAMPLE_RATE_Y * y, chunkZ + SAMPLE_RATE_Z * z);
                }
            }
        }
        for (int x = 0; x <= Chunk.SIZE; x++) {
            float xf = (float) x / SAMPLE_RATE_X;
            int x1i = (int) xf;
            int x2i = x1i+1;
            if (x == Chunk.SIZE) {
                x2i = x1i;
            }
            float xd = xf - x1i;
            for (int y = 0; y <= Chunk.SIZE; y++) {
                float yf = (float) y / SAMPLE_RATE_Y;
                int y1i = (int) yf;
                int y2i = y1i + 1;
                if (y == Chunk.SIZE) {
                    y2i = y1i;
                }
                float yd = yf - y1i;
                for (int z = 0; z <= Chunk.SIZE; z++) {
                    float zf = (float) z / SAMPLE_RATE_Z;
                    int z1i = (int) zf;
                    int z2i = z1i + 1;
                    if (z == Chunk.SIZE) {
                        z2i = z1i;
                    }
                    float zd = zf - z1i;
                    float val = 0;
                    val += noiseVals[x1i][y1i][z1i]*(1-xd)*(1-yd)*(1-zd) + noiseVals[x2i][y1i][z1i]*xd*(1-yd)*(1-zd);
                    val += noiseVals[x1i][y2i][z1i]*(1-xd)*yd*(1-zd) + noiseVals[x2i][y2i][z1i]*xd*yd*(1-zd);
                    val += noiseVals[x1i][y1i][z2i]*(1-xd)*(1-yd)*zd + noiseVals[x2i][y1i][z2i]*xd*(1-yd)*zd;
                    val += noiseVals[x1i][y2i][z2i]*(1-xd)*yd*zd + noiseVals[x2i][y2i][z2i]*xd*yd*zd;
                    //Log.d("Noise", x + " " + y + " " + z + " " + val);
                    //val -= (float)Math.sqrt(x * x + y * y + z * z) / radius;
                    c.setValue(x, y, z, val);
                }
            }
        }
    }

    public float getValue(int x, int y, int z)
    {
        //float noise = (float)Math.cos(turb(x, y, z, 256))*0.5f+0.5f;
        float noise = n.getNoiseValue(x, y, z);
        //Log.d("Noise", noise +" ");
        float adjustedNoise = (float) Math.cos(noise)*0.5f + 0.5f;
        float dist = (float)Math.sqrt(x * x + y * y + z * z) / radius;
        if (dist > 1) dist = 1;
        float val = (.65f*adjustedNoise + .35f*dist );
        return 0.85f-val;
    }

}
