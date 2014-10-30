package com.wajawinc.spaceexploration.universe.generator;


public class Noise
{
    private float size;
    private int planetSeed;

    public Noise(int seed, float s)
    {
        size = s;
        planetSeed = seed;
    }

    public float getNoiseValue (float x, float y, float z)
    {
        return turb(x, y, z, size);
    }

    private float noise(int x, int y, int z) {
        int n = x*331 + y*337 + z*347 + planetSeed;
        n = (n << 13) ^ n;
        int nn=(n*(n*n*41333 +53307781)+1376312589)&0x7fffffff;
        return ((1.0f-(nn/1073741824.0f))+1)/2.0f;
    }

    /**
     * Adds zoomed in smoothed noise values.
     * @param x
     * @param y
     * @param z
     * @param size
     * @return the noise value for given coordinate
     */
    private float turb (float x, float y, float z, float size)
    {
        float originalSize = size * 2;
        float noiseVal = 0;
        while (size >= 1)
        {
            noiseVal += createSmoothValue(x/size, y/size, z/size) * size;
            size /= 2;
        }

        return noiseVal / originalSize;
    }

    /**
     * Creates a smoothed noise value based off the surrounding random values on the integer lattice.
     * @param x
     * @param y
     * @param z
     * @return the smoothed value
     */
    private float createSmoothValue (float x, float y, float z)
    {
        //bottom corner integer lattice coordinate
        int xLat1 = (int)Math.floor(x);//((int)x + width) % width;
        int yLat1 = (int)Math.floor(y);//((int)y + height) % height;
        int zLat1 = (int)Math.floor(z);//((int)z + depth) % depth;
        //top corner integer lattice coordinate
        int xLat2 = xLat1 - 1;//((int)x - 1 + width) % width;
        int yLat2 = yLat1 - 1;//((int)y - 1 + height) % height;
        int zLat2 = zLat1 - 1;//((int)z - 1 + depth) % depth;
        //Distances of the original coordinate from the walls of the surrounding lattice cube
        float xDis1 = x - xLat1;
        float xDis2 = 1 - xDis1;
        float yDis1 = y - yLat1;
        float yDis2 = 1 - yDis1;
        float zDis1 = z - zLat1;
        float zDis2 = 1 - zDis1;
        //Calculates the interpolation
        float smoothValue = 0;
        smoothValue += noise(xLat1, yLat1, zLat1) * xDis1 * yDis1 * zDis1;
        smoothValue += noise(xLat2, yLat1, zLat1) * xDis2 * yDis1 * zDis1;
        smoothValue += noise(xLat1, yLat2, zLat1) * xDis1 * yDis2 * zDis1;
        smoothValue += noise(xLat1, yLat1, zLat2) * xDis1 * yDis1 * zDis2;
        smoothValue += noise(xLat2, yLat2, zLat1) * xDis2 * yDis2 * zDis1;
        smoothValue += noise(xLat1, yLat2, zLat2) * xDis1 * yDis2 * zDis2;
        smoothValue += noise(xLat2, yLat1, zLat2) * xDis2 * yDis1 * zDis2;
        smoothValue += noise(xLat2, yLat2, zLat2) * xDis2 * yDis2 * zDis2;
        return smoothValue;
    }

}
