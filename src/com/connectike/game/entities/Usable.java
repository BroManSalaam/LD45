package com.connectike.game.entities;

import com.connectike.game.creatures.Player;

/**
 * This class represents anything that can be "used" eg. can be interacted using F
 * 
 * @author loucks
 *
 */
public interface Usable {
	
	public Item use(Player player);
}
