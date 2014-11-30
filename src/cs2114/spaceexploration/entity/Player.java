package cs2114.spaceexploration.entity;

// Class depends upon the Rajawali 3D library (stable v0.7).

import rajawali.BaseObject3D;
import rajawali.materials.SimpleMaterial;
import rajawali.materials.TextureInfo;
import rajawali.materials.TextureManager;
import rajawali.math.Number3D;
import rajawali.parser.AParser.ParsingException;
import rajawali.parser.ObjParser;
import android.content.res.Resources;
import cs2114.spaceexploration.R;
import cs2114.spaceexploration.universe.Universe;

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
    private Number3D velocity;
    private Universe universe;
    
    private boolean accelerating;
    
    private BaseObject3D ship;
    
    public Player(Universe universe) {
        this.universe = universe;
        velocity = new Number3D();
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
    public void loadShipModel(Resources res, TextureManager textureManager) {
    	ObjParser objParser = new ObjParser(res, textureManager, R.raw.player_ship_obj);
    	try {
			objParser.parse();
		} catch (ParsingException e) {
			e.printStackTrace();
		}
    	ship = objParser.getParsedObject();
    	ship.setRotY(180);
    	/*DiffuseMaterial mat = new DiffuseMaterial();
		mat.setAmbientColor(1, 1, 1, 1);
		mat.setAmbientIntensity(0.5f);*/
    	//PhongMaterial phong = new PhongMaterial();
    	//phong.setSpecularColor(0xffffffff); // white
    	//phong.setAmbientColor(0xffffff00); // yellow
    	//phong.setShininess(0.5f);
    	//phong.setUseColor(false);
    	//ship.setMaterial(phong);
    	ship.setMaterial(new SimpleMaterial());
    	ship.addTexture(new TextureInfo(R.drawable.player_ship));
    	
    	/*DirectionalLight light = new DirectionalLight(1, 0.2f, -1);
		light.setColor(1.0f, 1.0f, 1.0f);
		light.setPower(2);
		ship.addLight(light);*/
    	addChild(ship);
    }
    
    public void setAccelerating(boolean accel) {
    	accelerating = accel;
    }
    
    public void update() {
    	if (accelerating) {
    		this.getLookAt();
    	}
    }

}
