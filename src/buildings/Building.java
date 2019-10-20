package buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.connectike.game.entities.Item;
import com.connectike.util.Const;
import com.connectike.util.WorldGenerator;

/**
 * 
 * This class represents anything that has a body and can take damage.
 * 
 * @author loucks
 *
 */
public abstract class Building extends Item {
	
	protected float width;
	protected float height;
	protected Body body;
	
	protected float health;

	public Building() {
		super();
	}

	/**
	 * Gives the building a place in box2d and places it into the world
	 * initializing the building creates it as an item.
	 * It must be initialized in order to become an entity
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param health
	 * @param defaultBody
	 * should the building use default body / collision definition?
	 * @param worldGen
	 */
	public void place(float x, float y, float width, float height, float health, boolean defaultBody, WorldGenerator worldGen) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.health = health;
		
		if(defaultBody) {
			PolygonShape shape = new PolygonShape();
	
			BodyDef bdef = new BodyDef();
			FixtureDef fdef = new FixtureDef();
	
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set(x + (width / 2), y + (height / 2));
			body = worldGen.getWorld().createBody(bdef);
	
			shape.setAsBox(width / 2, height / 2);
			fdef.shape = shape;
			fdef.filter.categoryBits = Const.BIT_COLLIDE;
			fdef.filter.maskBits = Const.BIT_PLAYER | Const.BIT_ENEMY | Const.BIT_PROJ;
			body.createFixture(fdef).setUserData(this);
			
			worldGen.buildings.add(this);
		}
		
	}

	public abstract boolean heal(float amount);
	
	public abstract Texture getTexture();
	
	/**
	 * deal damage to this building, if it dies return true
	 * 
	 * @param amount
	 */
	public boolean damage(float amount) {
		this.health -= amount;
		
		if(this.health <= 0) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Will remove body / fixture from physics world.
	 * 
	 * @param world
	 * world object to remove this body from
	 */
	public void destroy(World world) {
		world.destroyBody(body);
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getHealth() {
		return health;
	}

}
