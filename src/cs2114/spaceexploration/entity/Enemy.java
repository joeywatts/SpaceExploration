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
    private static final int         MAX_SHOOTING_DISTANCE = 500;
    private static final float       DISTANCE_TO_PLAYER    = 20;
    private static final float       FAR_PLANE             = 200f;

    private float                    health;
    private int                      tickSinceLastShot;
    private SpaceExplorationRenderer renderer;

    private float                    fovHoriz;

    private float                    velocity;
    private Number3D                 moveDirection;
    private float                    enemyRoll;
    private BaseObject3D             model;

    private Number3D                 realPosition;
    private float                    realScale;

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
        realPosition = new Number3D();
        health = 100;
        model = defaultModel.clone();
        DirectionalLight light = new DirectionalLight(1, 0.2f, -1);
        light.setColor(1.0f, 1.0f, 1.0f);
        light.setPower(20);
        model.setRotZ(0);
        model.setRotX(0);
        model.addLight(light);
        realScale = 0.15f;
        fovHoriz = 45 * 3.14159f / 180.0f;
        addChild(model);
        velocity = 2;
    }


    /**
     * Sets the real position of the Enemy.
     *
     * @param position
     *            the new position.
     */
    public void setRealPosition(Number3D position)
    {
        realPosition = position;
    }


    /**
     * Get the real position of the enemy.
     *
     * @return the real position of the enemy.
     */
    public Number3D getRealPosition()
    {
        return realPosition;
    }


    /**
     * Sets the real scale of the enemy.
     *
     * @param scale
     *            the real scale of the enemy.
     */
    public void setRealScale(float scale)
    {
        this.realScale = scale;
    }


    /**
     * Gets the real scale of the enemy.
     *
     * @return the real scale of the enemy.
     */
    public float getRealScale()
    {
        return realScale;
    }


    private Bullet shoot()
    {
        Universe universe = renderer.getUniverse();
        Bullet b = universe.shootBullet(this, moveDirection, 0);
        b.setPosition(getPosition().clone());
        b.setOrientation(getOrientation());
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
        Number3D myPos = realPosition.clone();
        float distance = playerPos.distanceTo(myPos);
        float angle = calculateHorizontalAngle(myPos, playerPos);
        /* Player can be seen. */
        Number3D dir = playerPos.subtract(myPos);
        dir.normalize();
        float dot = dir.dot(moveDirection);
        if (distance > DISTANCE_TO_PLAYER)
        {
            /* Turn to face the player and move towards him. */
            moveDirection.lerpSelf(moveDirection, dir, 0.2f);
            setOrientation(Quaternion.getRotationTo(
                new Number3D(),
                moveDirection));
            realPosition.add(moveDirection.clone().multiply(velocity * .32f));
        }
        else if (dot > .9f && tickSinceLastShot > RELOAD_TIME
            && Math.abs(angle) < fovHoriz && distance < MAX_SHOOTING_DISTANCE)
        {
            shoot();
            tickSinceLastShot = 0;
            enemyRoll = (enemyRoll + dot * 25f) / 2.0f;
        }
        if (distance > FAR_PLANE)
        {
            setPosition(player.getPosition().clone()
                .subtract(dir.multiply(FAR_PLANE)));
            setScale(FAR_PLANE / distance);
        }
        else
        {
            setPosition(realPosition);
            setScale(realScale);
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
        IBoundingVolume enemyBB = model.getGeometry().getBoundingBox();
        enemyBB.transform(model.getModelMatrix());
        IBoundingVolume bulletBB = b.getBoundingBox();
        if (enemyBB.intersectsWith(bulletBB))
        {
            b.explode();
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
        if (p.getCenter().distanceTo(getRealPosition()) < p.getRadius())
        {
            health -= 100;
            return true;
        }
        return false;
    }
}
