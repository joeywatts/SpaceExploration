package cs2114.spaceexploration;

// Class depends upon the Rajawali 3D library (stable v0.7).

import javax.microedition.khronos.opengles.GL10;

import rajawali.ChaseCamera;
import rajawali.lights.DirectionalLight;
import rajawali.math.Number3D;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import cs2114.spaceexploration.entity.Player;
import cs2114.spaceexploration.universe.ChunkGeneratorThread;
import cs2114.spaceexploration.universe.Planet;
import cs2114.spaceexploration.universe.Universe;
import cs2114.spaceexploration.view.AnalogStick;

// -------------------------------------------------------------------------
/**
 * SpaceExplorationRenderer is a class that extends RajawaliRenderer and manages
 * the rendering and logic loops of the game.
 *
 * @author Joey
 * @version Nov 28, 2014
 */
public class SpaceExplorationRenderer extends RajawaliRenderer {
	/* private ChunkGeneratorExecutor chunkGeneratorExecutor; */

	private Planet planet, planet2;

	private DirectionalLight mLight;

	private Player player;
	private Universe universe;
	
	private AnalogStick directionalAnalogStick;
	private Button accelerate;

	private ChunkGeneratorThread thread;

	private float touchX;
	private float touchY;

	private long lastTime;

	public SpaceExplorationRenderer(Context context) {
		super(context);
		setFrameRate(60);
	}

	@Override
	protected void initScene() {
		mLight = new DirectionalLight(1f, 0.2f, -1.0f);
		mLight.setColor(1.0f, 1.0f, 1.0f);
		mLight.setPower(2);

		/*planet = new Planet(null, new Number3D(), new NoisePlanetGenerator(0));
		planet.addLight(mLight);
		addChild(planet);*/
		universe = new Universe(0, this);
		player = new Player(universe);
		player.loadShipModel(mContext.getResources(), mTextureManager);
		addChild(player);
		player.setPosition(0, 0, -15);

		mCamera = new ChaseCamera(new Number3D(0, 3, 16), .1f, player);
		/*
		 * chunkGeneratorExecutor = new ChunkGeneratorExecutor(); for (int x =
		 * -5; x <= 5; x++) for (int y = -5; y <= 5; y++) for (int z = -5; z <=
		 * 5; z++) chunkGeneratorExecutor.generateChunk(planet, new Number3D(x
		 * Chunk.SIZE, y * Chunk.SIZE, z * Chunk.SIZE),
		 * ChunkTessellator.LOD_LEVEL_HIGHEST);
		 */

		/*
		 * universe = new Universe(0, this); player = new Player(universe);
		 * universe.startUpdater();
		 */

		/*thread = new ChunkGeneratorThread();
		thread.start();

		/*
		 * for (int x = -3; x <= 3; x++) for (int y = -3; y <= 3; y++) for (int
		 * z = -3; z <= 3; z++) thread.generateChunk(planet, new Number3D(x
		 * Chunk.SIZE, y * Chunk.SIZE, z * Chunk.SIZE),
		 * ChunkTessellator.LOD_LEVEL_HIGHEST);
		 */

		/*planet2 = new Planet(null, new Number3D(150, 0, -300),
				new NoisePlanetGenerator(100));
		planet2.addLight(mLight);
		addChild(planet2);
		/*
		 * for (int x = -3; x <= 3; x++) for (int y = -3; y <= 3; y++) for (int
		 * z = -3; z <= 3; z++) thread.generateChunk(planet2, new Number3D( x *
		 * Chunk.SIZE, y * Chunk.SIZE, z * Chunk.SIZE),
		 * ChunkTessellator.LOD_LEVEL_HIGHEST);
		 */
		universe.startUpdater();
		mCamera.setFarPlane(10000f);
		// mCamera.setZ(1024f);
	}

	public Universe getUniverse() {
		return universe;
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * Sets the AnalogStick to use for changing direction.
	 * 
	 * @param analog
	 *            the analog stick
	 */
	public void setAnalogStick(AnalogStick analog) {
		directionalAnalogStick = analog;
	}
	
	public void setAccelerateButton(Button accel) {
		accelerate = accel;
		accelerate.setOnTouchListener(new OnTouchListener() {			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					player.setAccelerating(true);
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					player.setAccelerating(false);
				}
				return true;
			}
		});
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		long time = System.currentTimeMillis();
		if (lastTime != 0) {
			float fps = 1000.0f / (time - lastTime);
			((SpaceExplorationActivity) mContext).setFPS(fps);
		}
		lastTime = time;
		player.setRotX(player.getRotX() + directionalAnalogStick.getNormalizedY());
		player.setRotY(player.getRotY() + directionalAnalogStick.getNormalizedX());
	}

}
