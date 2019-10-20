package com.connectike.game.entities;

import com.connectike.game.creatures.Player;
import com.connectike.util.Const;

/**
 * An item that restores hearts
 * 
 * @author loucks
 *
 */
public class Food extends Item {
	
	int restorationValue = 20;
	
	public Food() {
		
	}

	public Food(float x, float y) {
		super(x, y);
	}

	@Override
	public Item use(Player player) {
		boolean canHeal = player.heal(this.restorationValue);
		
		return canHeal ? this : null;
	}
	
}
