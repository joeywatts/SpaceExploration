package cs2114.spaceexploration.entity;

import cs2114.spaceexploration.universe.Planet;

// -------------------------------------------------------------------------
/**
 * Actor is an interface that defines a character in the game.
 *
 * @author joeywatts
 * @version Dec 4, 2014
 */
public interface Actor
{
    /**
     * Checks if there is a collision between this Actor and a Planet.
     *
     * @param planet
     *            the Planet.
     * @return true if there is a collision.
     */
    public boolean checkCollision(Planet planet);


    /**
     * Checks if there is a collision between this Actor and a Bullet.
     *
     * @param bullet
     *            the Bullet.
     * @return true if there is a collision.
     */
    public boolean checkCollision(Bullet bullet);
}
