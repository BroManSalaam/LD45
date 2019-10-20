package buildings;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.connectike.game.creatures.Player;
import com.connectike.game.entities.Item;
import com.connectike.util.Const;
import com.connectike.util.WorldGenerator;

/**
 * 
 * A construct that can be made using some steel to ward off aliens
 * 
 * @author loucks
 *
 */
public class Barricade extends Building {

	

	public Barricade() {
	}
	
	
	public void place(float x, float y, WorldGenerator worldGen) {
		super.place(x, y, Const.BARRICADE_SIZE, Const.BARRICADE_SIZE, Const.BARRICADE_HEALTH, false, worldGen);		
		
		// Create a custom body so that projectiles can pass over them
		PolygonShape shape = new PolygonShape();
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();

		bdef.type = BodyDef.BodyType.StaticBody;
		bdef.position.set(x + (width / 2), y + (height / 2));
		body = worldGen.getWorld().createBody(bdef);

		shape.setAsBox(width / 2, height / 2);
		fdef.shape = shape;
		fdef.filter.categoryBits = Const.BIT_COLLIDE;
		fdef.filter.maskBits = Const.BIT_PLAYER | Const.BIT_ENEMY;
		body.createFixture(fdef).setUserData(this);
		
		worldGen.buildings.add(this);
	}

	@Override
	public boolean heal(float amount) {
		
		if(this.health >= Const.BARRICADE_HEALTH) {
			return false;
		} else if(this.health + amount >= Const.BARRICADE_HEALTH) {
			this.health = Const.BARRICADE_HEALTH;
			return true;
		} else {
			this.health += amount;
			return true;
		}
	}

	@Override
	public Item use(Player player) {
		return this;
	}
	
	@Override
	public Texture getTexture() {
		return new Texture("img/buildings/barricade.png");
	}
	
	
}
