package com.wajawinc.spaceexploration;

import javax.microedition.khronos.opengles.GL10;

import rajawali.lights.DirectionalLight;
import rajawali.math.Number3D;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

import com.wajawinc.spaceexploration.entity.Player;
import com.wajawinc.spaceexploration.tessellation.ChunkTessellator;
import com.wajawinc.spaceexploration.universe.Chunk;
import com.wajawinc.spaceexploration.universe.ChunkGeneratorThread;
import com.wajawinc.spaceexploration.universe.Planet;
import com.wajawinc.spaceexploration.universe.Universe;
import com.wajawinc.spaceexploration.universe.generator.NoisePlanetGenerator;

public class SpaceExplorationRenderer extends RajawaliRenderer {
	/*private ChunkGeneratorExecutor chunkGeneratorExecutor;*/

	private Planet planet, planet2;

	private DirectionalLight mLight;

	private Player player;
	private Universe universe;
	
	private ChunkGeneratorThread thread;

	// private float touchX;
	// private float touchY;
	
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

		
		planet = new Planet(null, new Number3D(), new NoisePlanetGenerator(0));
		planet.addLight(mLight);
		addChild(planet);
		

		/*chunkGeneratorExecutor = new ChunkGeneratorExecutor();
		for (int x = -5; x <= 5; x++)
			for (int y = -5; y <= 5; y++)
				for (int z = -5; z <= 5; z++)
					chunkGeneratorExecutor.generateChunk(planet, new Number3D(x
							* Chunk.SIZE, y * Chunk.SIZE, z * Chunk.SIZE), ChunkTessellator.LOD_LEVEL_HIGHEST);*/
		
		
		/*universe = new Universe(0, this);
		player = new Player(universe);
		universe.startUpdater();*/
		
		thread = new ChunkGeneratorThread();
		thread.start();
		
		/*for (int x = -3; x <= 3; x++)
			for (int y = -3; y <= 3; y++)
				for (int z = -3; z <= 3; z++)
					thread.generateChunk(planet, new Number3D(x
							* Chunk.SIZE, y * Chunk.SIZE, z * Chunk.SIZE), ChunkTessellator.LOD_LEVEL_HIGHEST);*/
		
		planet2 = new Planet(null, new Number3D(150, 0, -300), new NoisePlanetGenerator(100));
		planet2.addLight(mLight);
		addChild(planet2);
		for (int x = -3; x <= 3; x++)
			for (int y = -3; y <= 3; y++)
				for (int z = -3; z <= 3; z++)
					thread.generateChunk(planet2, new Number3D(x
							* Chunk.SIZE, y * Chunk.SIZE, z * Chunk.SIZE), ChunkTessellator.LOD_LEVEL_HIGHEST);

		mCamera.setFarPlane(10000f);
		mCamera.setZ(1024f);
	}

	public Universe getUniverse() {
		return universe;
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		super.onDrawFrame(glUnused);
		long time = System.currentTimeMillis();
		if (lastTime != 0) {
			float fps = 1000.0f/(time-lastTime);
			((SpaceExplorationActivity) mContext).setFPS(fps);
		}
		lastTime = time;
		// planet.update(this.getFrameRate());
		planet2.setRotY(planet2.getRotY() + 1);
	}

	@Override
	public void onTouchEvent(MotionEvent event) {
		Log.d("TouchEvent", "got event");
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// float dx = event.getX()-touchX;
			// float dy = event.getY()-touchY;
			// dx /= 500f;
			// dy /= 500f;
			// mSphere.setRotX(mSphere.getRotX()+dy);
			// mSphere.setRotY(mSphere.getRotY()+dx);
		}
		// touchX = event.getX();
		// touchY = event.getY();
	}
}
