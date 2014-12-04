package cs2114.spaceexploration.tests;

import cs2114.spaceexploration.universe.generator.Noise;

// -------------------------------------------------------------------------
/**
 * Tests for the Noise class.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 17, 2014
 */
public class NoiseTest
    extends student.TestCase
{
    private Noise noise;
    private Noise noise2;
    private Noise noise3;


    protected void setUp()
    {
        noise = new Noise(0, 4.1f);
        noise2 = new Noise(0, 4.1f);
        noise3 = new Noise(1, 4.1f);
    }


    /**
     * Tests that noise is the same every time with the same parameters, and
     * that it is pseudorandom.
     */
    public void testPseudorandomness()
    {
        assertEquals(
            noise.getNoiseValue(0, 0, 0),
            noise2.getNoiseValue(0, 0, 0),
            0.01f);
        assertEquals(
            noise.getNoiseValue(1, 1, 1),
            noise2.getNoiseValue(1, 1, 1),
            0.01f);
        assertFalse(Math.abs(noise.getNoiseValue(0, 0, 0)
            - noise3.getNoiseValue(0, 0, 0)) < 0.01f);
        assertFalse(Math.abs(noise.getNoiseValue(0, 0, 0)
            - noise.getNoiseValue(1, 1, 1)) < 0.01f);
    }
}
