package cs2114.spaceexploration;

// Class depends upon the Rajawali 3D library (stable v0.9).

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ProgressBar;
import cs2114.spaceexploration.entity.Bullet;
import cs2114.spaceexploration.entity.Enemy;
import cs2114.spaceexploration.entity.Player;
import cs2114.spaceexploration.universe.Universe;
import cs2114.spaceexploration.view.AnalogStick;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import rajawali.BaseObject3D;
import rajawali.ChaseCamera;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.materials.TextureInfo;
import rajawali.math.Number3D;
import rajawali.parser.AParser.ParsingException;
import rajawali.parser.ObjParser;
import rajawali.renderer.RajawaliRenderer;

// -------------------------------------------------------------------------
/**
 * SpaceExplorationRenderer is a class that extends RajawaliRenderer and manages
 * the rendering and logic loops of the game.
 *
 * @author jwatts96
 * @author garnesen
 * @author jam0704
 * @version Nov 28, 2014
 */
public class SpaceExplorationRenderer
    extends RajawaliRenderer
{
    private DirectionalLight mLight;

    private Player           player;
    private Universe         universe;

    private AnalogStick      directionalAnalogStick;
    private Button           accelerate;
    private Button           brake;
    private ProgressBar      healthBar;

    private long             lastTime;
    private Handler          handler;


    /**
     * Instantiates a new SpaceExplorationRenderer.
     *
     * @param context
     *            the Android context.
     */
    public SpaceExplorationRenderer(Context context)
    {
        super(context);
        setFrameRate(60);
        handler = new Handler();
    }


    @Override
    protected void initScene()
    {
        mLight = new DirectionalLight(1f, 0.2f, -1.0f);
        mLight.setColor(1.0f, 1.0f, 1.0f);
        mLight.setPower(2);

        player = new Player(this);
        player.loadShipModel(mContext.getResources(), mTextureManager);
        loadBullet();
        addChild(player);
        player.setPosition(0, 0, -15);
        universe = new Universe(0, this);

        loadEnemy();
        universe.addEnemy(new Number3D(0, 0, -50));

        mCamera = new ChaseCamera(new Number3D(0, 0.75f, 4.0f), .1f, player);

        universe.startUpdater();
        mCamera.setFarPlane(10000f);
        // mCamera.setZ(1024f);
        setSkybox(
            R.drawable.pos_z,
            R.drawable.pos_x,
            R.drawable.neg_z,
            R.drawable.neg_x,
            R.drawable.pos_y,
            R.drawable.neg_y);
// setSkybox(R.drawable.skybox);
    }


    /**
     * Gets the Universe.
     *
     * @return the universe.
     */
    public Universe getUniverse()
    {
        return universe;
    }


    /**
     * Gets the Player.
     *
     * @return the Player.
     */
    public Player getPlayer()
    {
        return player;
    }


    /**
     * Sets the AnalogStick to use for changing direction.
     *
     * @param analog
     *            the analog stick
     */
    public void setAnalogStick(AnalogStick analog)
    {
        directionalAnalogStick = analog;
    }


    /**
     * Sets the ProgressBar to use for the Health bar.
     *
     * @param bar
     *            the ProgressBar
     */
    public void setHealthBar(ProgressBar bar)
    {
        healthBar = bar;
    }


    /**
     * Sets the Button to use for acceleration.
     *
     * @param accel
     *            the acceleration Button
     */
    public void setAccelerateButton(Button accel)
    {
        accelerate = accel;
        accelerate.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    player.accelerate();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    player.decelerate();
                }
                return true;
            }
        });
    }


    /**
     * Sets the brake button.
     *
     * @param brake
     *            the brake button.
     */
    public void setBrakeButton(Button brake)
    {
        this.brake = brake;
        this.brake.setOnTouchListener(new OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    player.shoot();
                    player.decelerate();
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    player.accelerate();
                }
                return true;
            }
        });
    }


    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        super.onDrawFrame(glUnused);
        long time = System.currentTimeMillis();
        if (lastTime != 0)
        {
            float fps = 1000.0f / (time - lastTime);
            ((SpaceExplorationActivity)mContext).setFPS(fps);
        }
        lastTime = time;
        float ratio = directionalAnalogStick.getRatio();
        player.update(
            directionalAnalogStick.getNormalizedX() * ratio,
            directionalAnalogStick.getNormalizedY() * ratio);
        universe.updateBullets();
        universe.updateEnemies();
        universe.updatePlanets();
        healthBar.setProgress((int)player.getHealth());
    }


    private void loadBullet()
    {
        ObjParser objParser =
            new ObjParser(
                getContext().getResources(),
                mTextureManager,
                R.raw.missile2_obj);
        try
        {
            objParser.parse();
        }
        catch (ParsingException e)
        {
            e.printStackTrace();
        }
        BaseObject3D bulletModel = objParser.getParsedObject();
        DiffuseMaterial mat = new DiffuseMaterial();
        mat.setUseColor(true);
        mat.setAmbientColor(0xff0000);
        mat.setAmbientIntensity(1);
        bulletModel.setMaterial(mat);
        bulletModel.setColor(0xff0000);
// bulletModel.addTexture(new TextureInfo(R.drawable.misslered));
        bulletModel.setScale(0.15f);
        bulletModel.setRotX(90);
        Bullet.setDefaultModel(bulletModel);
    }


    private void loadEnemy()
    {
        ObjParser objParser =
            new ObjParser(
                getContext().getResources(),
                mTextureManager,
                R.raw.ufo_obj);
        try
        {
            objParser.parse();
        }
        catch (ParsingException e)
        {
            e.printStackTrace();
        }
        BaseObject3D enemyModel = objParser.getParsedObject();
        enemyModel.setRotZ(0);
        DiffuseMaterial mat = new DiffuseMaterial();
        enemyModel.setMaterial(mat);
        enemyModel.addTexture(new TextureInfo(R.drawable.ufo_texture));
        Enemy.setDefaultModel(enemyModel);
    }


    /**
     * Ends the game.
     */
    public void finishActivity()
    {
        ((SpaceExplorationActivity)getContext()).finish();
    }


    /**
     * Posts a Runnable to the UI thread.
     *
     * @param r
     *            the Runnable.
     */
    public void post(Runnable r)
    {
        handler.post(r);
    }

}
