package cs2114.spaceexploration.entity;

import android.util.Log;
import rajawali.BaseObject3D;
import rajawali.lights.DirectionalLight;
import rajawali.math.Number3D;
import rajawali.math.Quaternion;

public class Bullet extends BaseObject3D
{
    private static final float VELOCITY = 20f;
    private static final float MAX_DISTANCE = 350f;

    private float distanceTraveled;
    private Number3D direction;

    public Bullet(BaseObject3D bulletObj, Quaternion quat) {
        addChild(bulletObj);
        setOrientation(quat);
        direction = quat.multiply(new Number3D(0, 0, -1));
        DirectionalLight light = new DirectionalLight(1, 0.2f, -1);
        light.setColor(1.0f, 1.0f, 1.0f);
        light.setPower(2);
        addLight(light);
    }

    public boolean update() {
        setPosition(getPosition().add(direction.clone().multiply(VELOCITY * .32f)));
        distanceTraveled += VELOCITY * .32f;
        return distanceTraveled >= MAX_DISTANCE;
    }
}
