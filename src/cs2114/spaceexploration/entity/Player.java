package cs2114.spaceexploration.entity;

// Class depends upon the Rajawali 3D library (stable v0.7).

import cs2114.spaceexploration.universe.Planet;
import cs2114.spaceexploration.universe.Universe;
import rajawali.BaseObject3D;
import rajawali.math.Number3D;

// -------------------------------------------------------------------------
/**
 *  Player is an object that represents the game's Player.
 *
 *  @author jwatts96
 *  @author garnesen
 *  @author jam0704
 *  @version Nov 17, 2014
 */
public class Player extends BaseObject3D
{
    private Number3D lastPosition;
    private Number3D temp;
    private Number3D velocity;
    private Universe universe;
    public Player(Universe universe) {
        this.universe = universe;
        temp = new Number3D();
        velocity = new Number3D();
        lastPosition = new Number3D();
    }
    public Number3D getVelocity() {
        return velocity;
    }
    public void setVelocity(float x, float y, float z) {
        velocity.setAll(x, y, z);
    }
    public void setVelocity(Number3D other) {
        velocity.setAllFrom(other);
    }
//    public void update(float tpf) {
//        Number3D position = getPosition();
//        lastPosition.setAllFrom(position);
//        position.add(velocity.multiply(tpf));
//        Planet lastPlanet = universe.getClosestPlanet(lastPosition);
//        Planet currentPlanet = universe.getClosestPlanet(position);
//        if (lastPlanet != currentPlanet) {
//            /* unload all of the last planet */
//            universe.unloadPlanet(lastPlanet);
//        }
//        /* Re-use temp variable to conserve memory */
//        Number3D chunkPosition = temp;
//        currentPlanet.getChunkLocation(position, chunkPosition);
//        int currentX = (int) chunkPosition.x;
//        int currentY = (int) chunkPosition.y;
//        int currentZ = (int) chunkPosition.z;
//        currentPlanet.getChunkLocation(lastPosition, chunkPosition);
//        int lastX = (int) chunkPosition.x;
//        int lastY = (int) chunkPosition.y;
//        int lastZ = (int) chunkPosition.z;
//
//    }

}
