package com.wajawinc.spaceexploration.universe;

import rajawali.BaseObject3D;
import com.wajawinc.spaceexploration.universe.generator.NoisePlanetGenerator;
import java.util.LinkedList;
import java.util.List;
import rajawali.math.Number3D;

// -------------------------------------------------------------------------
/**
 * The Universe class defines a structure that stores all the relevant data
 * involved in generating, storing, and displaying all of the Planets in the
 * game's universe, both logically and graphically.
 *
 */
public class Universe
{
    private LinkedList<Planet> planets;

    public Universe()
    {
        planets = new LinkedList<Planet>();
        planets.add(new Planet(new Number3D(), new NoisePlanetGenerator()));
    }

    public List<Planet> getPlanets() {
        return planets;
    }

}
