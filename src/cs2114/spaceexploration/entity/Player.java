package cs2114.spaceexploration.entity;

// Class depends upon the Rajawali 3D library (stable v0.9).

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.Resources;
import cs2114.spaceexploration.R;
import cs2114.spaceexploration.SpaceExplorationRenderer;
import cs2114.spaceexploration.universe.Planet;
import cs2114.spaceexploration.universe.Universe;
import rajawali.BaseObject3D;
import rajawali.bounds.IBoundingVolume;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.TextureInfo;
import rajawali.materials.TextureManager;
import rajawali.math.Number3D;
import rajawali.math.Quaternion;
import rajawali.parser.AParser.ParsingException;
import rajawali.parser.ObjParser;

// -------------------------------------------------------------------------
/**
 * Player is an object that represents the game's Player.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 17, 2014
 */
public class Player
    extends BaseObject3D
    implements Actor
{
    private SpaceExplorationRenderer renderer;

    private float                    velocity;
    /**
     * The maximum velocity of the player.
     */
    public static final float        MAX_VELOCITY = 6f;

    private int                      acceleration;
    private boolean                  shoot;

    private BaseObject3D             ship;

    private float                    health;
    private float                    shipRoll;

    private Quaternion               temp;

    private int                      enemiesKilled;
    private boolean                  dead;


    /**
     * Instantiates a new Player.
     *
     * @param renderer
     *            the SpaceExplorationRenderer object.
     */
    public Player(SpaceExplorationRenderer renderer)
    {
        this.renderer = renderer;
        temp = new Quaternion();
        health = 100;
    }


    /**
     * Gets the magnitude of the Player's current Velocity.
     *
     * @return the magnitude of the velocity.
     */
    public float getSpeed()
    {
        return velocity;
    }


    /**
     * Shoots a bullet.
     */
    public void shoot()
    {
        shoot = true;
        /*
         * The actual shooting is done on the render thread so that we don't get
         * a ConcurrentModificationException by adding to the Set while we have
         * an iterator.
         */
    }


    /**
     * Loads the ship model for the player.
     *
     * @param res
     *            Resources from the Context.
     * @param textureManager
     *            the TextureManager from the Renderer
     */
    public void loadShipModel(Resources res, TextureManager textureManager)
    {
        ObjParser objParser =
            new ObjParser(res, textureManager, R.raw.player_ship_obj);
        try
        {
            objParser.parse();
        }
        catch (ParsingException e)
        {
            e.printStackTrace();
        }
        ship = objParser.getParsedObject();
        ship.setRotY(180);
        ship.setScale(0.25f);
        DiffuseMaterial mat = new DiffuseMaterial();
        ship.setMaterial(mat);
        ship.addTexture(new TextureInfo(R.drawable.player_ship));
        DirectionalLight light = new DirectionalLight(1, 0.2f, -1);
        light.setColor(1.0f, 1.0f, 1.0f);
        light.setPower(2);
        ship.addLight(light);
        addChild(ship);
    }


    /**
     * Accelerates the player.
     */
    public void accelerate()
    {
        acceleration++;
    }


    /**
     * Decelerates the player.
     */
    public void decelerate()
    {
        acceleration--;
    }


    /**
     * An update method for the Player that is called once per frame.
     *
     * @param dx
     *            the change in the pitch rotation.
     * @param dy
     *            the change in the yaw rotation.
     */
    public void update(float dx, float dy)
    {
        if (dead)
        {
            return;
        }
        if (health <= 0)
        {
            dead = true;
            renderer.post(new Runnable() {
                @Override
                public void run()
                {
                    AlertDialog dialog =
                        new AlertDialog.Builder(renderer.getContext())
                            .setTitle("Beyond the Horizon")
                            .setMessage("You died!").create();
                    dialog.setOnDismissListener(new OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface di)
                        {
                            renderer.finishActivity();
                        }
                    });
                    dialog.show();
                }
            });
        }
        Quaternion q = this.getOrientation();
        temp.fromEuler(dx, 0, dy);
        temp.multiply(q);
        this.setOrientation(temp);
        shipRoll = (dx * 25.0f + shipRoll) / 2.0f;
        ship.setRotZ(shipRoll);

        velocity =
            Math.min(MAX_VELOCITY, Math.max(0, velocity + 0.1f * acceleration));
        Number3D dir = this.getOrientation().multiply(new Number3D(0, 0, -1));
        setPosition(getPosition().clone().add(dir.multiply(velocity)));

        if (shoot)
        {
            shoot = false;
            Number3D bulletDir =
                this.getOrientation().multiply(new Number3D(0, 0, -1));
            Bullet bullet =
                renderer.getUniverse().shootBullet(
                    this,
                    bulletDir,
                    MAX_VELOCITY);
            bullet.setPosition(getPosition().clone());
            bullet.setOrientation(Quaternion.getRotationTo(new Number3D(
                0,
                0,
                -1), bulletDir));
        }
    }


    /**
     * Gets the number of kills.
     *
     * @return the number of kills.
     */
    public int getKills()
    {
        return enemiesKilled;
    }


    /**
     * Increments the number of enemies killed.
     */
    public void addKill()
    {
        enemiesKilled++;
    }


    /**
     * Checks if the Player collides with a Bullet.
     *
     * @param bullet
     *            the Bullet.
     * @return true if the Player collides with a Bullet.
     */
    public boolean checkCollision(Bullet bullet)
    {
        IBoundingVolume myBB = ship.getGeometry().getBoundingBox();
        myBB.transform(ship.getModelMatrix());
        if (myBB.intersectsWith(bullet.getBoundingBox()))
        {
            health -= 5;
            return true;
        }
        return false;
    }


    /**
     * Checks if the Player collides with a Planet.
     *
     * @param p
     *            the Planet.
     * @return true if the Player collides with the Planet.
     */
    public boolean checkCollision(Planet p)
    {
        IBoundingVolume myBB = ship.getGeometry().getBoundingBox();
        myBB.transform(ship.getModelMatrix());
        IBoundingVolume planetBV = p.getBoundingVolume();
        if (myBB.intersectsWith(planetBV))
        {
            health -= 100;
            return true;
        }
        return false;
    }


    /**
     * Gets the Player's health.
     *
     * @return the Player's health.
     */
    public float getHealth()
    {
        return health;
    }
}
