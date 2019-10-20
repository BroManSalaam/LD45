package buildings;

import com.badlogic.gdx.graphics.Texture;
import com.connectike.game.creatures.Player;
import com.connectike.game.entities.Item;
import com.connectike.util.Const;
import com.connectike.util.WorldGenerator;

/**
 * basically a building that can do damage
 * 
 * @author louck
 *
 */
public class Turret extends Building {

	public Turret() {
		
	}
	
	public void place(float x, float y, WorldGenerator worldGen) {
		super.place(x, y, Const.TURRET_SIZE, Const.TURRET_SIZE, Const.TURRET_HEALTH, true, worldGen);
	}

	@Override
	public boolean heal(float amount) {
		if (this.health >= Const.TURRET_HEALTH) {
			return false;
		} else if (this.health + amount >= Const.TURRET_HEALTH) {
			this.health = Const.TURRET_HEALTH;
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
		//System.out.println("[Turret.java][getTexture()]: Texture not set up yet!");
		return new Texture("img/items/turret.png");
	}
	
	
}
