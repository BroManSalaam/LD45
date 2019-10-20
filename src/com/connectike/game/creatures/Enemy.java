package com.connectike.game.creatures;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.connectike.util.Const;
import com.connectike.util.WorldGenerator;

import pathfinding.Node;

public abstract class Enemy extends CreatureBase {
	
	protected List<Node> currentPath;
	
	public Enemy(float x, float y, WorldGenerator worldGen) {
		this.x = x;
		this.y = y;
		this.worldGen = worldGen;
	}

	
	/**
	 * For enemies to use when updating.
	 * @param dt
	 * @param player
	 * @param map
	 */
	public void update(float dt, CreatureBase player, List<int[]> map) {
		System.out.println("[CreatureBase.java][update()]: Enemy update should be overwritten!");
	}
	
	/**
	 * Moves the enemy the desired change in X and Y. It is recommended
	 * to multiply the change by the enemy's speed.
	 * 
	 * @param dx
	 * change in x movement
	 * @param dy
	 * change in y movement
	 * 
	 * @author seth
	 */
	public void move(float dx, float dy) {
		if(currentState != State.DEAD) {
			body.setLinearVelocity(dx, dy);
		} else {
			body.setLinearVelocity(0.0F, 0.0F);
		}
	}
	
	/**
	 * Mainly used in enemies, this method should be called anytime
	 * an enemy action takes place.
	 * 
	 * @param victim
	 * the creature being attacked
	 */
	public abstract void attack(CreatureBase victim);
	
	/**
	 * Called when health <= 0. May do a variety of things, such
	 * as remove the creature from the world, modify game statistics, etc.
	 * By default, the velocity will be set to zero and removed from
	 * the world.
	 * 
	 * @author seth
	 */
	@Override
	public void die() {
		System.out.println("[Enemy.java][die()]: Creature is dying!");
		this.health = 0.0F;
		body.setLinearVelocity(new Vector2(0, 0));
		world.destroyBody(this.body);
	}
	
	public List<Node> getCurrentPath() {
		return this.currentPath;
	}
	
	public int getGridX() {
		return Math.round(this.getX() / Const.TILE_SIZE);
	}
	
	public int getGridY() {
		return Math.round(this.getY() / Const.TILE_SIZE);
	}
	
}
