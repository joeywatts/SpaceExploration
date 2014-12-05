package cs2114.spaceexploration.entity;

import android.opengl.GLES20;
import android.graphics.Color;
import java.util.ArrayList;
import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.materials.DiffuseMaterial;
import rajawali.math.Number3D;
import rajawali.math.Quaternion;
import rajawali.primitives.Cube;

// -------------------------------------------------------------------------
/**
 * An animation for desruction of things.
 *
 *  @author Gunnar
 *  @author Joey
 *  @author Jason
 *  @version Nov 28, 2014
 */
public class DestroyAnimation extends BaseObject3D
{
    private Cube rootCube;
    private ArrayList<CubeData> cubes;

    private final int numCubes = 5;
    private final float cubeSize = 2;
    private final int constant = 25;

    private boolean animating;

    // ----------------------------------------------------------
    /**
     * Create a new DestroyAnimation object.
     * @param center the location of the animation start
     */
    public DestroyAnimation()
    {
        cubes = new ArrayList<CubeData>(numCubes);
        createRootCube();
        animating = true;
        for (int i = 0; i < numCubes; i++)
        {
            cubes.add(createCube());
        }
    }

    private void createRootCube()
    {
        rootCube = new Cube(cubeSize, false);
        DiffuseMaterial dm = new DiffuseMaterial();
        dm.setAmbientColor(Color.RED);
        rootCube.setMaterial(dm);
        rootCube.getMaterial().setUseColor(true);
        rootCube.setColor(Color.rgb(100, 100, 100));
        rootCube.setBlendingEnabled(true);
        rootCube.setBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE);

        DirectionalLight light = new DirectionalLight(1, 0.2f, -1);
        light.setColor(1.0f, 1.0f, 1.0f);
        light.setPower(2);
        rootCube.addLight(light);
    }

    private CubeData createCube()
    {
        BaseObject3D cube = rootCube.clone();
        CubeData cd = new CubeData(cube);
        addChild(cube);
        return cd;
    }

    /**
     * Updates the animation.
     * @return true if the animation has not ended, false otherwise
     */
    public boolean update()
    {
        for (CubeData cube : cubes)
        {
            if (!cube.update())
            {
                animating = false;
            }
        }
        return animating;
    }

    private class CubeData
    {
        Number3D direction;
        float quatVal1;
        float quatVal2;
        BaseObject3D cube;
        float Velocity;
        float distanceTraveled;
        private float EFFECT_DISTANCE;
        private int alpha;

        public CubeData(BaseObject3D cube)
        {
            this.cube = cube;
            updateCubeData();
        }

        public void updateCubeData()
        {
            direction = new Number3D ((float)(Math.random()*2 + -1),(float)(Math.random()*2 + -1), (float)(Math.random()*2 + -1));
            quatVal1 = (float)Math.random();
            quatVal2 = (float)Math.random();
            Velocity = (float)((Math.random()*0.4)+0.3);
            EFFECT_DISTANCE = Velocity * constant;
            distanceTraveled = 0f;
            alpha = 255;
        }

        public boolean update()
        {
            boolean stillOnScreen = true;
            cube.setPosition(cube.getPosition().add(direction.clone().multiply(Velocity * .32f)));
            Quaternion temp = new Quaternion();
            temp.fromEuler(quatVal1, 0f, quatVal2);
            temp.multiply(cube.getOrientation());
            cube.setOrientation(temp);
            distanceTraveled += Velocity * .32f;
            if (alpha <= 0)
            {
                stillOnScreen = false;
            }
            else if (distanceTraveled > EFFECT_DISTANCE )
            {
                alpha-=5;
                cube.setColor(Color.argb(alpha, 100, 100, 100));
            }
            return stillOnScreen;
        }
    }
}
