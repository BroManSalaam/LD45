package com.connectike.game.creatures;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.connectike.game.entities.PhysicalEntity;
import com.connectike.util.WorldGenerator;

public abstract class CreatureBase extends PhysicalEntity {
	
	// General variables
	protected float x;
	protected float y;
	protected float health = 100;
	protected boolean isDead = false;
	
	// Collision variables
	protected Body body;
	protected World world;
	protected WorldGenerator worldGen;
	
	// Texture and state variables
	protected Texture spriteSheet;
	// 2D Hash map. 1st dimension is state (walking, running, etc.)
	// 2nd dimension is direction of the character
	protected HashMap<State, HashMap<Direction, Animation<TextureRegion>>> animFrames;
	protected State currentState = State.WALK;
	protected Direction currentDirection = Direction.DOWN;
	protected float elapsedTime = 0.0F;
	protected boolean isLooping = false; // i.e. false = player not moving
	
	/**
	 * State of the character. Can include walking, running, attacking,
	 * or being dead
	 * 
	 * @author seth
	 */
	public enum State {
		WALK, RUN, ATTACK, DEAD
	}
	/**
	 * States the direction of the character, as in UP, RIGHT, DOWN, or LEFT.
	 * 
	 * @author seth
	 */
	public enum Direction {
		UP, RIGHT, DOWN, LEFT
	}
	
	
	/**
	 * Base abstract class for all creatures / movable objects. Anything
	 * that can "control itself" should be considered a creature.
	 * 
	 * 
	 * @author seth
	 */
	public CreatureBase() {
		
	}
	
	/**
	 * Should be called in a loop to update this creature's variables
	 * and state.
	 * 
	 * @param dt
	 * delta time
	 * 
	 * @author seth
	 */
	public abstract void update(float dt);
	
	
	/**
	 * Called when health <= 0. May do a variety of things, such
	 * as remove the creature from the world, modify game statistics, etc.
	 * By default, the velocity will be set to zero and removed from
	 * the world.
	 * 
	 * @author seth
	 */
	public void die() {
		System.out.println("[CreatureBase.java][die()]: Creature is dying!");
		body.setLinearVelocity(new Vector2(0, 0));
		world.destroyBody(body);
	}
	
	/**
	 * Will populate the HashMap object with Texture objects for use
	 * in displaying this creature's appearance / image. Recommended to
	 * call in creature's constructor
	 */
	protected abstract void initAnimation();
	
	/**
	 * Will define the collision and fixture object for this creature. Also
	 * recommended to be called in the creature's constructor
	 * 
	 * @param world
	 * collision world object
	 * 
	 * @author seth
	 */
	protected abstract void defineCollision(World world);
	
	/**
	 * Uses the STATE and DIRECTION variables to retrieve a sprite
	 * indicative of the appearance of this creature.
	 * 
	 * @return
	 * Animation object containing frames for the animation
	 * 
	 * @author seth
	 */
	public Animation<TextureRegion> getCurrentAppearance() {
		return animFrames.get(currentState).get(currentDirection);
	}
	
	/**
	 * Returns the size of the sprite on-screen
	 * 
	 * @return
	 * Integer representing width and height of sprite.
	 * 
	 * @author seth
	 */
	public abstract int getDisplaySize();
	
	// Get / Set methods
	
	public float getHealth() {
		return health;
	}
	public void setHealth(float health) {
		this.health = health;
	}
	
	public float getElapsedTime() {
		return elapsedTime;
	}
	public boolean getIsLooping() {
		return isLooping;
	}
	
	public Body getBody() {
		return body;
	}
	
	public State getCurrentState() {
		return currentState;
	}
	
	public Direction getCurrentDirection() {
		return currentDirection;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	
	
	
}
