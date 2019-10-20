package com.connectike.game.creatures;

import java.util.Arrays;
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
import com.connectike.util.Assets;
import com.connectike.util.Const;
import com.connectike.util.WorldGenerator;

import pathfinding.AStar;
import pathfinding.Node;

public class BombTurtle extends Enemy {
	
	private boolean isExploding;
	private WorldGenerator worldGen;
	private float attackTimer;
	AStar pathfinder;
	private int worldWidth;
	private int worldHeight;
	
	public enum Priority {
		PLAYER,
		BARRICADE
	}
	
	Vector2 barricade;
	Priority currentPriority;

	public BombTurtle(float x, float y, WorldGenerator worldGen) {
		super(x, y, worldGen);
		this.health = Const.TURRET_HEALTH;
		this.world = worldGen.getWorld();
		this.worldGen = worldGen;
		this.currentPriority = Priority.PLAYER;
		
		this.worldWidth = (Integer) worldGen.getMap().getProperties().get("width");
		this.worldHeight = (Integer) worldGen.getMap().getProperties().get("height");
		
		this.barricade = new Vector2();
		
		this.animFrames = new HashMap<State, HashMap<Direction, Animation<TextureRegion>>>();
		worldGen.enemies.add(this);
		
		initAnimation();
		defineCollision(world);

	}
	
	@Override
	public void update(float dt, CreatureBase player, List<int[]> blocks) {
		elapsedTime = elapsedTime + dt;
		
		this.x = body.getPosition().x;
		this.y = body.getPosition().y;
		
		// start explosion countdown
		if(Vector2.dst(this.x, this.y, player.x, player.y) <= Const.TURTLE_EXPLOSION_OPTIMAL_RANGE && !isExploding) {
			isExploding = true;
		}
		
		if(isExploding) {
			this.attackTimer += dt;
		}
		
		if(this.attackTimer >= Const.TURTLE_EXPLOSION_TIME) {
			this.attack(player);
		}
		

		switch(this.currentPriority) {
		case PLAYER:
			pathfinder = new AStar(this.worldWidth, this.worldHeight,
					new Node(this.getGridX(), this.getGridY()),
					new Node(Math.round(player.x / Const.TILE_SIZE), Math.round(player.y / Const.TILE_SIZE)));
			break;
		case BARRICADE:
			pathfinder = new AStar(this.worldWidth, this.worldHeight,
					new Node(this.getGridX(), this.getGridY()),
					new Node(Math.round(this.barricade.x / Const.TILE_SIZE), Math.round(this.barricade.y / Const.TILE_SIZE)));
		}
		
		pathfinder.setBlocks(blocks);
		

		this.currentPath = pathfinder.findPath();
		Node nextNode;
		
		if(this.currentPath.size() != 0) {
			nextNode = this.currentPath.get(1);
			
			if(nextNode.getX() > this.getGridX()) {
				this.move(Const.TURTLE_SPEED, 0);
			}
			if(nextNode.getX() < this.getGridX()) {
				this.move(-Const.TURTLE_SPEED, 0);
			}
			if(nextNode.getY() > this.getGridY()) {
				this.move(0, Const.TURTLE_SPEED);
			}
			if(nextNode.getY() > this.getGridY()) {
				this.move(0, Const.TURTLE_SPEED);
			}
		} else {
			System.out.println("NO PATH FOR OUR SAD TURTLE FRIEND :(");
		}
	}
	
	
	@Override
	public void attack(CreatureBase victim) {
		float damage = (float) (Const.TURTLE_EXPLOSION_DAMAGE / Math.pow(Vector2.dst(this.x, this.y, victim.x, victim.y), 2));
		victim.health -= damage;
		this.currentState = State.DEAD;
		this.die();
	
	}
	
		
	@Override
	protected void initAnimation() {

		// Prepare TextureRegion variables which will feed into HashMap later
		TextureRegion[] upFrames;
		TextureRegion[] rightFrames;
		TextureRegion[] downFrames;
		TextureRegion[] leftFrames;

		Texture spriteSheet = Assets.manager.get(Assets.bombTurtleSprite);

		// Split the spritesheet into a 2D grid
		TextureRegion[][] spriteGrid = TextureRegion.split(spriteSheet, Const.ANIM_FRAME_SIZE, Const.ANIM_FRAME_SIZE);
		int rowNum = 0;

		for (State state : State.values()) {

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
			
			// Since the explosion state is the same for all directions
			if(state != State.DEAD) {
				// -- DOWN -- //
				for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
					downFrames[i] = spriteGrid[rowNum][i];
				}
	
				// -- RIGHT -- //
				for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
					rightFrames[i] = spriteGrid[rowNum + 1][i];
				}
	
				// -- LEFT -- //
				for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
					leftFrames[i] = spriteGrid[rowNum + 2][i];
				}
	
				// -- UP -- //
				for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
					upFrames[i] = spriteGrid[rowNum + 3][i];
				}
			} else {
				
				for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
					upFrames[i] = spriteGrid[rowNum][i];
				}
			}
			
			// This is where the fun begins... (creating animations)
			float frameDelay = 1.0F; // Delay between frames for animations
			switch (state) {
			case WALK:
				animFrames.put(State.WALK, new HashMap<Direction, Animation<TextureRegion>>());

				frameDelay = 0.15F; // CHANGE FOR DIFFERENT EFFECTS
				animFrames.get(State.WALK).put(Direction.UP, new Animation<TextureRegion>(frameDelay, upFrames));
				animFrames.get(State.WALK).put(Direction.RIGHT, new Animation<TextureRegion>(frameDelay, rightFrames));
				animFrames.get(State.WALK).put(Direction.DOWN, new Animation<TextureRegion>(frameDelay, downFrames));
				animFrames.get(State.WALK).put(Direction.LEFT, new Animation<TextureRegion>(frameDelay, leftFrames));
				break;
			case DEAD:
				animFrames.put(State.DEAD, new HashMap<Direction, Animation<TextureRegion>>());

				frameDelay = 0.1F;
				// Use all upFrames since its the same for all directions
				animFrames.get(State.DEAD).put(Direction.UP, new Animation<TextureRegion>(frameDelay, upFrames));
				animFrames.get(State.DEAD).put(Direction.RIGHT, new Animation<TextureRegion>(frameDelay, upFrames));
				animFrames.get(State.DEAD).put(Direction.DOWN, new Animation<TextureRegion>(frameDelay, upFrames));
				animFrames.get(State.DEAD).put(Direction.LEFT, new Animation<TextureRegion>(frameDelay, upFrames));
				break;
			default:
				System.out.println("[Player.java][initAnimation()]: WARNING: No valid animation for State = " + state);
				break;
			}

		} // End of for loop

	} // End of initAnimation()

	@Override
	protected void defineCollision(World world) {
		
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();

		// NOTE: Fixture coordinates are on the MAP, not screen

		// SCALING VARIABLES
		// This converts the pixels on the image to screen pixels
		// i.e. character is 22 pixels wide. 22 * ratio = screen pixel width
		float pixelRatio = Const.TURTLE_DISPLAY_SIZE / Const.ANIM_FRAME_SIZE;

		// BODY
		bodyDef.position.set(x, y);
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		body = world.createBody(bodyDef);
		
		// FIXTURE - COLLISION
		// Subtract away the transparent areas of the sprite
		shape.setAsBox((Const.TURTLE_DISPLAY_SIZE / 4), (Const.TURTLE_DISPLAY_SIZE / 4));
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = Const.BIT_ENEMY;
		fixtureDef.filter.maskBits = Const.BIT_PLAYER | Const.BIT_ITEM | Const.BIT_COLLIDE | Const.BIT_AGGRO | Const.BIT_PROJ;
		body.createFixture(fixtureDef).setUserData(this);
		
		shape.dispose();
	}

	
	@Override
	public int getDisplaySize() {
		return Const.TURTLE_DISPLAY_SIZE;
	}

	@Override
	public void update(float dt) {
		// TODO Auto-generated method stub
		
	}
	
}
