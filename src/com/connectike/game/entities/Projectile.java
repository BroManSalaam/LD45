package com.connectike.game.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.connectike.game.creatures.CreatureBase;
import com.connectike.util.WorldGenerator;

/**
 * Basic interface which all projectiles (bullets, arrows, etc.)
 * should implement to ensure basic functionality for all.
 * 
 * @author seth
 */
public abstract class Projectile {
	
	protected float lifeLeft = 0.0F;
	protected boolean isDead = false; // Will be true for destroy()
	protected int healthEffect = -5; // Will be subtracted from health, in most cases
	
	protected World world;
	protected Body body;
	
	/**
	 * Called to update the position (and possibly appearance)
	 * of the projectile.
	 * 
	 * @param delta
	 * delta time
	 * 
	 * @author seth
	 */
	public abstract void update(float delta);
	
	/**
	 * When the projectile hits a creature, any action or operations
	 * should be performed now. This might include damaging or healing
	 * the creature. Additionally, a sprite change might occur for
	 * this projectile (i.e. explosions)
	 * 
	 * @param hitCreature
	 * object that was hit by the projectile
	 * 
	 * @author seth
	 */
	public abstract void onHit(CreatureBase hitCreature);
	
	/**
	 * Should be called externally when lifeLeft reaches
	 * 0.0. It should remove the body / fixture from the world and dispose
	 * of the sprite.
	 */
	public void destroy() {
		System.out.println("[Projectile.java][destroy()]: Destroying projectile");
		body.setLinearVelocity(new Vector2(0, 0));
		world.destroyBody(body);
	}
	
	public void scheduleDestroy() {
		isDead = true;
	}
	
	/**
	 * Defines collision aspects, such as body and fixture data.
	 * 
	 * @author seth
	 */
	protected abstract void defineCollision();
	
	/**
	 * Returns the position (center) of the projectile as a vector.
	 * To access X or Y, simply do getLocation().x or y.
	 * 
	 * @return
	 * 2D vector containing positional information.
	 * 
	 * @author seth
	 */
	public Vector2 getPosition() {
		return this.body.getPosition();
	}
	
	/**
	 * Gets the appearance of the projectile, which might be animating.
	 * 
	 * @return
	 * Texture representing current image of projectile.
	 */
	public abstract Texture getAppearance();
	
	/**
	 * Get time left before the bullet expires and is removed from the world.
	 * 
	 * @return
	 * float of time left
	 * 
	 * @author seth
	 */
	public float getLifeLeft() {
		return lifeLeft;
	}
	
	/**
	 * Returns display size of the sprite, used for drawing.
	 * 
	 * @return
	 * integer of size
	 * 
	 * @author seth
	 */
	public abstract int getSpriteSize();
	
	/**
	 * Is the projectile about to be destroyed?
	 * 
	 * @return
	 * boolean representing isDead value
	 * 
	 * @author seth
	 */
	public boolean getIsDead() {
		return isDead;
	}
	
	public int getHealthEffect() {
		return healthEffect;
	}
		
	
}
