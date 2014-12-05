package cs2114.spaceexploration.entity;

import rajawali.Camera;
import rajawali.util.ObjectColorPicker.ColorPickerInfo;
import rajawali.BaseObject3D;
import rajawali.bounds.BoundingBox;
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

    private Actor               shooter;

    private DestroyAnimation    animation;
    private boolean             exploding;

    private static BaseObject3D defaultModel;
    private BaseObject3D        model;


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
     * @param actor
     *            the Actor that shot the bullet.
     * @param dir
     *            the direction for the bullet.
     * @param startVelocity
     *            the velocity for the bullet to start at.
     */
    public Bullet(Actor actor, Number3D dir, float startVelocity)
    {
        shooter = actor;
        addChild(model = defaultModel.clone());
        direction = dir;
        direction.normalize();
        setLookAt(direction);
        DirectionalLight light = new DirectionalLight(1, 0.2f, -1);
        light.setColor(1.0f, 1.0f, 1.0f);
        light.setPower(2);
        addLight(light);
        velocity = startVelocity + BULLET_VELOCITY;
    }


    @Override
    public void render(
        Camera camera,
        float[] projMatrix,
        float[] vMatrix,
        float[] parentMatrix,
        ColorPickerInfo pickerInfo)
    {
        super.render(camera, projMatrix, vMatrix, parentMatrix, pickerInfo);
        getBoundingBox().drawBoundingVolume(camera, projMatrix, vMatrix, model.getModelMatrix());
    }

    /**
     * Updates the bullet position.
     *
     * @return true if the bullet should be removed from the scene.
     */
    public boolean update()
    {
        if (exploding)
        {
            return !animation.update();
        }
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


    private BoundingBox myBB;

    /**
     * Gets a bounding box for the Bullet.
     *
     * @return the bounding box.
     */
    public IBoundingVolume getBoundingBox()
    {
        IBoundingVolume bulletBB = model.getGeometry().getBoundingBox();
        bulletBB.transform(model.getModelMatrix());
        return bulletBB;
    }


    /**
     * Gets the shooter of the Bullet.
     *
     * @return the shooter of the Bullet.
     */
    public Actor getShooter()
    {
        return shooter;
    }


    /**
     * Explodes the bullet.
     */
    public void explode()
    {
        if (!exploding)
        {
            removeChild(model);
            exploding = true;
            animation = new DestroyAnimation();
            animation.setScale(0.25f);
            addChild(animation);
        }
    }
}
