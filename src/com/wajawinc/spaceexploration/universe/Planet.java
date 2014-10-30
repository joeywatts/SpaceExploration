package com.wajawinc.spaceexploration.universe;

import rajawali.materials.DiffuseMaterial;
import com.wajawinc.spaceexploration.tessellation.ChunkTessellator;
import com.wajawinc.spaceexploration.tessellation.MarchingCubesChunkTessellator;
import com.wajawinc.spaceexploration.universe.generator.PlanetGenerator;
import java.util.LinkedHashMap;
import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.AMaterial;
import rajawali.math.Number3D;

// -------------------------------------------------------------------------
/**
 * The Planet class defines a structure that stores all the relevant data
 * involved in generating, storing, and displaying a Planet, both logically and
 * graphically.
 */
public class Planet
    extends BaseObject3D
{
    private PlanetGenerator                generator;

    private LinkedHashMap<Number3D, Chunk> chunks;

    private DirectionalLight               light;
    private Number3D                       lightDir = new Number3D();
    private AMaterial                       planetMaterial;

    /**
     * Instantiates a new Planet object.
     *
     * @param center
     *            the center of the planet, relative to the "center" of the
     *            universe.
     * @param generator
     *            the object that generates the planet's terrain
     */
    public Planet(Number3D center, PlanetGenerator generator)
    {
        this.setPosition(center);
        this.generator = generator;
        chunks = new LinkedHashMap<Number3D, Chunk>();

        lightDir.x = 1;
        lightDir.y = 0.2f;
        lightDir.z = -1f;
        light = new DirectionalLight(lightDir.x, lightDir.y, lightDir.z);
        light.setColor(1.0f, 1.0f, 1.0f);
        light.setPower(2);
    }


    public void generateChunk(Number3D loc)
    {
        Chunk c = new Chunk(this, loc);
        //c.generate();
        generator.generateChunk(c, loc);
        if (!c.tessellate())
            return;
        c.addLight(light);
        addChild(c);
        chunks.put(loc, c);
    }

    public void update(float fps)
    {

        lightDir.rotateY(fps / 240);
        light.setDirection(lightDir.x, lightDir.y, lightDir.z);
    }

    public AMaterial getMaterial() {
        return planetMaterial;
    }

    /**
     * Gets the object that generates this Planet's terrain.
     *
     * @return the PlanetGenerator object.
     */
    public PlanetGenerator getPlanetGenerator()
    {
        return generator;
    }


    /**
     * Gets the object that creates the isosurface from each chunk's voxel data.
     *
     * @return the ChunkTessellator object.
     */
    public ChunkTessellator getChunkTessellator()
    {
        return new MarchingCubesChunkTessellator();
    }
}
