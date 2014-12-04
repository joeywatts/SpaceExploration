package cs2114.spaceexploration.entity;

import rajawali.BaseObject3D;
import rajawali.bounds.IBoundingVolume;
import rajawali.lights.DirectionalLight;
import rajawali.math.Number3D;

// -------------------------------------------------------------------------
/**
 * Bullet is a class the represents the projectile that is shot in the game.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 17, 2014
 */
public class Bullet
    extends BaseObject3D
{
    private static final float  BULLET_VELOCITY = 20f;
    private static final float  MAX_DISTANCE    = 1000f;

    private float               velocity;

    private float               distanceTraveled;
    private Number3D            direction;

    private static BaseObject3D defaultModel;


    /**
     * Sets the default model for the Bullet.
     *
     * @param model
     *            the default model
     */
    public static void setDefaultModel(BaseObject3D model)
    {
        defaultModel = model;
    }


    /**
     * Instantiates a new Bullet.
     *
     * @param dir
     *            the direction for the bullet.
     * @param startVelocity
     *            the velocity for the bullet to start at.
     */
    public Bullet(Number3D dir, float startVelocity)
    {
        addChild(defaultModel.clone());
        direction = dir;
        direction.normalize();
        setLookAt(direction);
        DirectionalLight light = new DirectionalLight(1, 0.2f, -1);
        light.setColor(1.0f, 1.0f, 1.0f);
        light.setPower(2);
        addLight(light);
        velocity = startVelocity + BULLET_VELOCITY;
    }


    /**
     * Updates the bullet position.
     *
     * @return true if the bullet should be removed from the scene.
     */
    public boolean update()
    {
        setPosition(getPosition().add(
            direction.clone().multiply(velocity * .32f)));
        distanceTraveled += velocity * .32f;
        return distanceTraveled >= MAX_DISTANCE;
    }


    /**
     * Mark the Bullet to be destroyed.
     */
    public void destroy()
    {
        distanceTraveled = MAX_DISTANCE;
    }


    /**
     * Gets a bounding box for the Bullet.
     *
     * @return the bounding box.
     */
    public IBoundingVolume getBoundingBox()
    {
        IBoundingVolume myBB = defaultModel.getGeometry().getBoundingBox();
        myBB.transform(this.getModelMatrix());
        return myBB;
    }
}
