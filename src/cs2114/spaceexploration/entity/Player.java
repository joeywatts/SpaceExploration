package cs2114.spaceexploration.entity;

// Class depends upon the Rajawali 3D library (stable v0.7).

import android.content.res.Resources;
import android.util.Log;
import cs2114.spaceexploration.R;
import cs2114.spaceexploration.SpaceExplorationRenderer;
import cs2114.spaceexploration.universe.Universe;
import java.util.Iterator;
import java.util.LinkedHashSet;
import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.lights.PointLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.TextureInfo;
import rajawali.materials.TextureManager;
import rajawali.math.Number3D;
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
{
    private float              velocity;
    private static final float MAX_VELOCITY = 20f;
    private Universe           universe;

    private int                acceleration;
    private boolean shoot;

    private BaseObject3D       ship;
    private PointLight pointLight;

    private BaseObject3D bulletModel;

    private SpaceExplorationRenderer renderer;

    private LinkedHashSet<Bullet> bullets;

    public Player(SpaceExplorationRenderer renderer)
    {
        this.renderer = renderer;
        this.universe = renderer.getUniverse();
        bullets = new LinkedHashSet<Bullet>();
    }


    public float getVelocity()
    {
        return velocity;
    }

    public void setBulletModel(BaseObject3D model) {
        bulletModel = model;
    }

    public void shoot() {
        Log.d("Shoot", "shot a bullet");
        Bullet b = new Bullet(bulletModel.clone(), this.getOrientation());
        b.setPosition(getPosition());
        renderer.addChild(b);
        bullets.add(b);
    }

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
        //mat.setAmbientColor(1, 1, 1, 1);
        //mat.setAmbientIntensity(1f);

        ship.setMaterial(mat);
        ship.addTexture(new TextureInfo(R.drawable.player_ship));

        DirectionalLight light = new DirectionalLight(1, 0.2f, -1);
        light.setColor(1.0f, 1.0f, 1.0f);
        light.setPower(2);
        ship.addLight(light);
        pointLight = new PointLight();
        //
        pointLight.setPower(20);
        pointLight.setColor(0xffffff);
        //ship.addLight(pointLight);
        //this.addLight(pointLight);

        addChild(ship);
    }


    public void accelerate()
    {
        acceleration++;
    }


    public void decelerate()
    {
        acceleration--;
    }


    public void update()
    {
        velocity =
            Math.min(MAX_VELOCITY, Math.max(0, velocity + 0.4f * acceleration));
        Log.d("velocity", "" + velocity);
        Number3D dir = this.getOrientation().multiply(new Number3D(0, 0, -1));
        setPosition(getPosition().add(dir.multiply(velocity)));
        universe.updatePlanets(getPosition());
        //pointLight.setPosition(getPosition());
        Iterator<Bullet> iter = bullets.iterator();
        Bullet temp;
        while (iter.hasNext()) {
            if ((temp = iter.next()).update()) {
                renderer.removeChild(temp);
                iter.remove();
            }
        }
    }

}
