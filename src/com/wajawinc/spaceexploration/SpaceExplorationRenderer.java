package com.wajawinc.spaceexploration;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import com.wajawinc.spaceexploration.universe.Chunk;
import com.wajawinc.spaceexploration.universe.ChunkGeneratorExecutor;
import com.wajawinc.spaceexploration.universe.Planet;
import com.wajawinc.spaceexploration.universe.generator.NoisePlanetGenerator;
import javax.microedition.khronos.opengles.GL10;
import rajawali.lights.DirectionalLight;
import rajawali.math.Number3D;
import rajawali.renderer.RajawaliRenderer;

public class SpaceExplorationRenderer
    extends RajawaliRenderer
{
    private ChunkGeneratorExecutor chunkGeneratorExecutor;

    private Planet planet;

    private DirectionalLight mLight;

    private float touchX;
    private float touchY;

    public SpaceExplorationRenderer(Context context)
    {
        super(context);
        setFrameRate(60);
    }

    @Override
    protected void initScene()
    {
        mLight = new DirectionalLight(1f, 0.2f, -1.0f);
        mLight.setColor(1.0f, 1.0f, 1.0f);
        mLight.setPower(2);

        planet = new Planet(new Number3D(), new NoisePlanetGenerator());
        planet.addLight(mLight);
        addChild(planet);


        chunkGeneratorExecutor = new ChunkGeneratorExecutor(this);
        for (int x = -5; x <= 5; x++)
            for (int y = -5; y <= 5; y++)
                for (int z = -5; z <= 5; z++)
                    chunkGeneratorExecutor.generateChunk(planet, new Number3D(x*Chunk.SIZE, y*Chunk.SIZE, z*Chunk.SIZE));

        mCamera.setFarPlane(10000f);
        mCamera.setZ(512f);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        super.onDrawFrame(glUnused);
        ((SpaceExplorationActivity) mContext).setFPS(this.getFrameRate());
        //planet.update(this.getFrameRate());
        planet.setRotY(planet.getRotY()+1);
    }


    @Override
    public void onTouchEvent(MotionEvent event)
    {
        Log.d("TouchEvent", "got event");
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float dx = event.getX()-touchX;
            float dy = event.getY()-touchY;
            //dx /= 500f;
            //dy /= 500f;
            //mSphere.setRotX(mSphere.getRotX()+dy);
            //mSphere.setRotY(mSphere.getRotY()+dx);
        }
        touchX = event.getX();
        touchY = event.getY();
    }
}
