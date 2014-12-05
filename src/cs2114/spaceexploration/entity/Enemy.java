package cs2114.spaceexploration.entity;

import cs2114.spaceexploration.SpaceExplorationRenderer;
import cs2114.spaceexploration.universe.Planet;
import cs2114.spaceexploration.universe.Universe;
import rajawali.BaseObject3D;
import rajawali.bounds.IBoundingVolume;
import rajawali.lights.DirectionalLight;
import rajawali.math.Number3D;
import rajawali.math.Quaternion;

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
    implements Actor
{
    /* Time before shots, in ticks (or frames) */
    private static final int         RELOAD_TIME           = 400;

    private static final float       HIT_CHANCE            = .05f;
    /* Max distance from player to shoot. */
    private static final int         MAX_SHOOTING_DISTANCE = 50;

    private float                    health;
    private int                      tickSinceLastShot;
    private SpaceExplorationRenderer renderer;

    private float                    fovHoriz;

    private float                    velocity;
    private Number3D                 moveDirection;
    private float                    enemyRoll;
    private BaseObject3D             model;

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
        moveDirection = new Number3D(0, 0, -1);
        this.renderer = renderer;
        health = 100;
        model = defaultModel.clone();
        DirectionalLight light = new DirectionalLight(1, 0.2f, -1);
        light.setColor(1.0f, 1.0f, 1.0f);
        light.setPower(20);
        model.setRotZ(0);
        model.setRotX(0);
        model.addLight(light);
        fovHoriz = 45 * 3.14159f / 180.0f;
        addChild(model);
    }


    private Bullet shoot()
    {
        Universe universe = renderer.getUniverse();
        Bullet b = universe.shootBullet(this, moveDirection, -5);
        b.setPosition(getPosition().clone());
        return b;
    }


    /**
     * Updates the Enemy per frame.
     *
     * @return true if the Enemy is dead.
     */
    public boolean update()
    {
        tickSinceLastShot++;
        Player player = renderer.getPlayer();
        Number3D playerPos = player.getPosition().clone();
        Number3D myPos = getPosition().clone();
        if (playerPos.distanceTo(myPos) <= MAX_SHOOTING_DISTANCE)
        {
            float angle = calculateHorizontalAngle(myPos, playerPos);
            if (Math.abs(angle) < fovHoriz)
            {
                /* Player can be seen. */
                Number3D dir = playerPos.subtract(myPos);
                dir.normalize();
                /* Turn to face the player and move towards him. */
                float dot = dir.dot(moveDirection);
                if (dot > .9f)
                {
                    /* Shoot */
                    if (tickSinceLastShot > RELOAD_TIME) {
                        shoot();
                        tickSinceLastShot = 0;
                    }
                }
                enemyRoll = (enemyRoll + dot * 25f) / 2.0f;
                moveDirection.lerpSelf(moveDirection, dir, 0.2f);
                setOrientation(Quaternion.getRotationTo(
                    new Number3D(),
                    moveDirection));
                setPosition(getPosition().add(moveDirection.clone().multiply(velocity)));
            }
            else
            {
                enemyRoll = enemyRoll / 2.0f;
            }
        }
        return health <= 0;
    }


    private float calculateHorizontalAngle(Number3D v1, Number3D v2)
    {
        return (float)Math.tan((v2.x - v1.x) / (v2.z - v1.z));
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
