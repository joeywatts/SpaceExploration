package cs2114.spaceexploration.entity;

import cs2114.spaceexploration.SpaceExplorationRenderer;
import cs2114.spaceexploration.universe.Planet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import rajawali.BaseObject3D;
import rajawali.bounds.IBoundingVolume;
import rajawali.lights.DirectionalLight;
import rajawali.math.Number3D;

// -------------------------------------------------------------------------
/**
 * Enemy is a class that represents the UFO antagonists in the game.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 28, 2014
 */
public class Enemy
    extends BaseObject3D
{
    /* Time before shots, in ticks (or frames) */
    private static final int         RELOAD_TIME           = 400;

    private static final float       HIT_CHANCE            = .05f;
    /* Max distance from player to shoot. */
    private static final int         MAX_SHOOTING_DISTANCE = 50;

    private float                    health;
    private int                      tickSinceLastShot;
    private SpaceExplorationRenderer renderer;
    private Set<Bullet>              bullets;

    private static BaseObject3D      defaultModel;


    /**
     * Sets the default model to use for the Enemy.
     *
     * @param model
     *            the default model.
     */
    public static void setDefaultModel(BaseObject3D model)
    {
        defaultModel = model;
    }


    /**
     * Instantiates a new Enemy.
     *
     * @param renderer
     *            the SpaceExplorationRenderer object
     */
    public Enemy(SpaceExplorationRenderer renderer)
    {
        this.renderer = renderer;
        bullets = new LinkedHashSet<Bullet>();
        health = 100;
        BaseObject3D model = defaultModel.clone();
        DirectionalLight light = new DirectionalLight(1, 0.2f, -1);
        light.setColor(1.0f, 1.0f, 1.0f);
        light.setPower(20);
        model.setRotZ(0);
        model.setRotX(0);
        model.addLight(light);
        addChild(model);
    }


    private Bullet shoot(Player player)
    {
        Number3D dir = player.getPosition().subtract(getPosition());
        if (Math.random() > HIT_CHANCE)
        {
            final float miss = 20f;
            dir.add(
                (0.5f - (float)Math.random()) * miss,
                (0.5f - (float)Math.random()) * miss,
                (0.5f - (float)Math.random()) * miss);
        }
        Bullet b = new Bullet(dir, -5);
        b.setPosition(getPosition());
        renderer.addChild(b);
        bullets.add(b);
        return b;
    }


    /**
     * Updates the Enemy per frame.
     *
     * @param player
     *            the Player.
     * @return true if the Enemy is dead.
     */
    public boolean update(Player player)
    {
        tickSinceLastShot++;
        if (tickSinceLastShot > RELOAD_TIME
            && player.getPosition().distanceTo(getPosition()) <= MAX_SHOOTING_DISTANCE)
        {
            tickSinceLastShot = 0;
            bullets.add(shoot(player));
        }
        Iterator<Bullet> iter = bullets.iterator();
        while (iter.hasNext())
        {
            Bullet b = iter.next();
            if (b.update())
            {
                renderer.removeChild(b);
                iter.remove();
            }
            else
            {
                player.checkCollision(b);
            }
        }
        return health <= 0;
    }


    /**
     * Checks if this Enemy collides with a Bullet.
     *
     * @param b
     *            the Bullet
     * @return true if the Enemy collides with the Bullet.
     */
    public boolean checkCollision(Bullet b)
    {
        IBoundingVolume myBB = defaultModel.getGeometry().getBoundingBox();
        myBB.transform(this.getModelMatrix());
        IBoundingVolume bulletBB = b.getBoundingBox();
        if (myBB.intersectsWith(bulletBB))
        {
            b.destroy();
            health -= 100;
            return true;
        }
        return false;
    }


    /**
     * Checks if this Enemy collides with a Planet.
     *
     * @param p
     *            the Planet.
     * @return true if the Enemy collides with the Planet.
     */
    public boolean checkCollision(Planet p)
    {
        IBoundingVolume myBB = defaultModel.getGeometry().getBoundingBox();
        myBB.transform(this.getModelMatrix());
        IBoundingVolume planetBV = p.getBoundingVolume();
        if (myBB.intersectsWith(planetBV))
        {
            health -= 100;
            return true;
        }
        return false;
    }
}
