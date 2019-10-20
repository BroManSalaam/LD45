package com.connectike.game.creatures;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.connectike.game.creatures.CreatureBase.Direction;
import com.connectike.game.creatures.CreatureBase.State;
import com.connectike.game.entities.Bullet;
import com.connectike.util.Assets;
import com.connectike.util.Const;
import com.connectike.util.WorldGenerator;

public class Spitter extends Enemy {
	
	private float attackTimer = 0.0f; // Used to "schedule" attacks
	private final float ATTACK_FREQ;
	private WorldGenerator worldGen;
	
	/**
	 * A plant that spit projectiles at the player.
	 * 
	 * To add, do
	 * worldGen.enemies.add(new Spitter(...));
	 * 
	 * @param x
	 * @param y
	 * @param health
	 * @param world
	 */
	public Spitter(float x, float y, float attackFreq, WorldGenerator worldGen) {
		super(x, y, worldGen);
		this.x = x;
		this.y = y;
		this.health = Const.SPITTER_HEALTH;
		this.ATTACK_FREQ = attackFreq; // How often (seconds) this attacks
		this.world = worldGen.getWorld();
		this.worldGen = worldGen;
		
		this.animFrames = new HashMap<State, HashMap<Direction, Animation<TextureRegion>>>();
		worldGen.enemies.add(this);
		
		initAnimation();
		defineCollision(world);
	}
	
	@Override
	public void update(float dt, CreatureBase player, List<int[]> map) {
		elapsedTime = elapsedTime + dt;
		attackTimer = attackTimer + dt;
		
		// Update the direction the creature is facing
		double up = calcDistance(player.getX(), player.getY(), this.x, this.y + Const.SPITTER_DISPLAY_SIZE);
		double right = calcDistance(player.getX(), player.getY(), this.x + Const.SPITTER_DISPLAY_SIZE, this.y);
		double down = calcDistance(player.getX(), player.getY(), this.x, this.y - Const.SPITTER_DISPLAY_SIZE);
		double left = calcDistance(player.getX(), player.getY(), this.x - Const.SPITTER_DISPLAY_SIZE, this.y);
		
		// Guess that up is the closest and correct if needed
		double leastDir = up;
		currentDirection = Enemy.Direction.UP;
		if(right < leastDir) {
			currentDirection = Enemy.Direction.RIGHT;
			leastDir = right;
		}
		if(down < leastDir) {
			currentDirection = Enemy.Direction.DOWN;
			leastDir = down;
		}
		if(left < leastDir) {
			currentDirection = Enemy.Direction.LEFT;
			leastDir = left;
		}
		
		if(attackTimer >= ATTACK_FREQ) {
			attack(player);
		}
		
	}
	
	@Override
	public void attack(CreatureBase player) {
		// Shoot a projectile toward the player if close enough
		
		double distance = calcDistance(player.getX(), this.x, player.getY(), this.y);
		
		if(distance <= Const.SPITTER_RANGE) {			
			// Find the direction so its own spit doesn't kill itself
			Vector2 spawnLocation = new Vector2(getX(), getY());
			if (this.getCurrentDirection() == Enemy.Direction.UP) { // UP
				spawnLocation.y = getY() + Const.PLAYER_DISPLAY_SIZE / 2;
			} else if (this.getCurrentDirection() == Enemy.Direction.RIGHT) { // RIGHT
				spawnLocation.x = getX() + Const.PLAYER_DISPLAY_SIZE / 2;
			} else if (this.getCurrentDirection() == Enemy.Direction.DOWN) { // DOWN
				spawnLocation.y = getY() - Const.PLAYER_DISPLAY_SIZE / 2;
			} else if (this.getCurrentDirection() == Enemy.Direction.LEFT) { // LEFT
				spawnLocation.x = getX() - Const.PLAYER_DISPLAY_SIZE / 2;
			}
			new Bullet("img/projectiles/spit.png", 2.0F, spawnLocation, worldGen).setTrajectory(player.getX(), player.getY());
			
			attackTimer = 0.0F;
		}
	}
	
	@Override
	public void update(float dt) {
		System.out.println("[Spitter.java][update()]: This enemy class should be using other update method!");
	}
	
	@Override
	protected void initAnimation() {
		
		// Prepare TextureRegion variables which will feed into HashMap later
		TextureRegion[] upFrames;
		TextureRegion[] rightFrames;
		TextureRegion[] downFrames;
		TextureRegion[] leftFrames;
		
		Texture spriteSheet = Assets.manager.get(Assets.spitterSprite);
		
		// Split the spritesheet into a 2D grid
		TextureRegion[][] spriteGrid = TextureRegion.split(spriteSheet, Const.ANIM_FRAME_SIZE, Const.ANIM_FRAME_SIZE);
		int rowNum = 0;
		
		for(State state : State.values()) {
			
			// Create new TextureRegion objects for each state
			// They are passed by reference otherwise
			upFrames = new TextureRegion[Const.ANIM_FRAME_LENGTH];
			rightFrames = new TextureRegion[Const.ANIM_FRAME_LENGTH];
			downFrames = new TextureRegion[Const.ANIM_FRAME_LENGTH];
			leftFrames = new TextureRegion[Const.ANIM_FRAME_LENGTH];
			
			switch (state) {
				case WALK:
					rowNum = 0;
					break;
				case DEAD:
					rowNum = 4;
					break;
				default:
					rowNum = 0;
					break;
			}
			
			// -- RIGHT -- //
			for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
				rightFrames[i] = spriteGrid[rowNum][i];
			}
			
			// -- LEFT -- //
			for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
				leftFrames[i] = spriteGrid[rowNum + 1][i];
			}
			
			// -- UP -- //
			for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
				upFrames[i] = spriteGrid[rowNum + 2][i];
			}
			
			// -- DOWN -- //
			for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
				downFrames[i] = spriteGrid[rowNum + 3][i];
			}
			
			
			// Create the animations
			float frameDelay = 1.0F; // Delay between frames for animations
			switch (state) {
			case WALK:
				animFrames.put(State.WALK, new HashMap<Direction, Animation<TextureRegion>>());

				frameDelay = 0.45F;
				animFrames.get(State.WALK).put(Direction.UP, new Animation<TextureRegion>(frameDelay, upFrames));
				animFrames.get(State.WALK).put(Direction.RIGHT, new Animation<TextureRegion>(frameDelay, rightFrames));
				animFrames.get(State.WALK).put(Direction.DOWN, new Animation<TextureRegion>(frameDelay, downFrames));
				animFrames.get(State.WALK).put(Direction.LEFT, new Animation<TextureRegion>(frameDelay, leftFrames));
				break;
			default:
				System.out.println("[Player.java][initAnimation()]: WARNING: No valid animation for State = " + state);
				break;
			}
			
		} // End of for loop
		
		
	}

	@Override
	protected void defineCollision(World world) {
		
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();

		// NOTE: Fixture coordinates are on the MAP, not screen

		// SCALING VARIABLES
		// This converts the pixels on the image to screen pixels
		// i.e. character is 22 pixels wide. 22 * ratio = screen pixel width
		float pixelRatio = Const.SPITTER_DISPLAY_SIZE / Const.ANIM_FRAME_SIZE;

		// BODY
		bodyDef.position.set(x, y);
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		body = world.createBody(bodyDef);

		// FIXTURE - COLLISION
		// Subtract away the transparent areas of the sprite
		shape.setAsBox((Const.SPITTER_DISPLAY_SIZE / 2), (Const.SPITTER_DISPLAY_SIZE / 2));
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = Const.BIT_ENEMY;
		fixtureDef.filter.maskBits = Const.BIT_PLAYER | Const.BIT_ITEM | Const.BIT_COLLIDE | Const.BIT_AGGRO | Const.BIT_PROJ;
		body.createFixture(fixtureDef).setUserData(this);
		
		shape.dispose();
	}
	
	@Override
	public int getDisplaySize() {
		return Const.SPITTER_DISPLAY_SIZE;
	}
	
	/**
	 * Pretty simple, this returns the distance between two points
	 * 
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 * 
	 * @author seth
	 */
	private double calcDistance(float x1, float y1, float x2, float y2) {
		return Math.sqrt(Math.pow(x2 - x1, 2) + (Math.pow(y2 - y1, 2)));
	}
	
	// Get / Set methods
	
	public float getAttackTimer() {
		return attackTimer;
	}
	public void setAttackTimer(float attackTimer) {
		this.attackTimer = attackTimer;
	}
	
	public float getAttackFreq() {
		return ATTACK_FREQ;
	}
	
}
