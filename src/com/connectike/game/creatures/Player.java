package com.connectike.game.creatures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.connectike.game.entities.Item;
import com.connectike.game.entities.Resource.ResourceType;
import com.connectike.util.Assets;
import com.connectike.util.Const;
import com.connectike.util.PlayerInputManager;

public class Player extends CreatureBase {
	
	private int n_steelPlates = 0;
	private int n_copperWire = 0;
	private int n_electronicCircuits = 0;

	private int itemHeld;
	private int speedMultiplier = 1;

	private boolean isColliding = false;

	private List<Item> inventory;
	
	private PlayerInputManager inputManager;
	private OrthographicCamera cam;
	
	/**
	 * Player object for the game, which may include an inventory system and special
	 * events / attributes.
	 * 
	 * @param camera       camera object used to create box2d fixtures at character
	 *                     location
	 * @param w            the world object for Box2D
	 * @param inputManager linked input source of game
	 * 
	 * @author seth
	 */
	public Player(OrthographicCamera camera, World w, PlayerInputManager inputManager) {
		
		this.health = Const.PLAYER_STARTING_HEALTH;
		this.animFrames = new HashMap<State, HashMap<Direction, Animation<TextureRegion>>>();
		this.inputManager = inputManager;
		this.cam = camera;

		this.inventory = new ArrayList<Item>();

		initAnimation();
		defineCollision(w);
	}

	@Override
	public void update(float dt) {
		elapsedTime = elapsedTime + dt;
		handleInput(dt);
		
		this.x = body.getPosition().x;
		this.y = body.getPosition().y;
		
		if (health <= 0.0F) {
			currentState = State.DEAD;
			System.out.println("YOU'RE dead, BUSTER!!!!!");
		}
		
		
	}
	
	public void handleInput(float dt) {

		// If the player is pressing a key, loop the animation
		if ((inputManager.isW) || (inputManager.isA) || (inputManager.isS) || (inputManager.isD)) {
			if (!isLooping) {
				elapsedTime = elapsedTime + 0.4F;
			}
			isLooping = true;
		}
		
		if(inputManager.isShift) {
			currentState = State.RUN;
			speedMultiplier = 99999; // This doesn't do much because libGDX sucks! :<
		} else {
			currentState = State.WALK;
			speedMultiplier = 1;
		}
		
		// Set direction and give Body object motion
		Vector2 newVelocity = new Vector2();
		if(inputManager.isW) {
			currentDirection = Direction.UP;
			newVelocity.y = Const.PLAYER_SPEED * speedMultiplier;
		}
		if(inputManager.isA) {
			currentDirection = Direction.LEFT;
			newVelocity.x = -1 * Const.PLAYER_SPEED * speedMultiplier;
		}
		if(inputManager.isS) {
			currentDirection = Direction.DOWN;
			newVelocity.y = -1 * Const.PLAYER_SPEED * speedMultiplier;
		}
		if(inputManager.isD) {
			currentDirection = Direction.RIGHT;
			newVelocity.x = Const.PLAYER_SPEED * speedMultiplier;
		}
		if((newVelocity.x == 0.0F) && (newVelocity.y == 0.0F)) {
			// If no keys are pressed, stop the animation and reset elapsed time
			isLooping = false;
			elapsedTime = 0.0F;
		}
		body.setLinearVelocity(newVelocity);
		
	}
	
	@Override
	protected void initAnimation() {

		// Prepare TextureRegion variables which will feed into HashMap later
		TextureRegion[] upFrames;
		TextureRegion[] rightFrames;
		TextureRegion[] downFrames;
		TextureRegion[] leftFrames;

		Texture spriteSheet = Assets.manager.get(Assets.playerSprite);
		// splits sprite sheet up into a 32x32 grid and returns a
		// multidimensional array to reference the tiles
		// example: upTemp[1][2] would get the second column third row
		// formats in [row, column]
		TextureRegion[][] spriteGrid = TextureRegion.split(spriteSheet, Const.ANIM_FRAME_SIZE, Const.ANIM_FRAME_SIZE);
		int rowNum = 0;

		// For each state / animation type, loop and add directional animations
		for (State state : State.values()) {

			// Create new TextureRegion objects for each state
			// These are mutable objects and will change unless new objects
			// are made. (Kind of act as pointers)
			upFrames = new TextureRegion[Const.ANIM_FRAME_LENGTH];
			rightFrames = new TextureRegion[Const.ANIM_FRAME_LENGTH];
			downFrames = new TextureRegion[Const.ANIM_FRAME_LENGTH];
			leftFrames = new TextureRegion[Const.ANIM_FRAME_LENGTH];

			switch (state) {
			case WALK:
				rowNum = 0;
				break;
			case RUN:
				rowNum = 0;
				break;
			case DEAD:
				rowNum = 4;
				break;
			default:
				rowNum = 0;
				break;
			}

			// -- UP -- //
			for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
				upFrames[i] = spriteGrid[rowNum][i];
			}

			// -- RIGHT -- //
			for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
				rightFrames[i] = spriteGrid[rowNum + 1][i];
			}

			// -- DOWN -- //
			for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
				downFrames[i] = spriteGrid[rowNum + 2][i];
			}

			// -- LEFT -- //
			for (int i = 0; i < Const.ANIM_FRAME_LENGTH; i++) {
				leftFrames[i] = spriteGrid[rowNum + 3][i];
			}

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
			case RUN:
				animFrames.put(State.RUN, new HashMap<Direction, Animation<TextureRegion>>());

				frameDelay = 0.1F;
				animFrames.get(State.RUN).put(Direction.UP, new Animation<TextureRegion>(frameDelay, upFrames));
				animFrames.get(State.RUN).put(Direction.RIGHT, new Animation<TextureRegion>(frameDelay, rightFrames));
				animFrames.get(State.RUN).put(Direction.DOWN, new Animation<TextureRegion>(frameDelay, downFrames));
				animFrames.get(State.RUN).put(Direction.LEFT, new Animation<TextureRegion>(frameDelay, leftFrames));
				break;
			case DEAD:
				animFrames.put(State.DEAD, new HashMap<Direction, Animation<TextureRegion>>());

				frameDelay = 0.15F;
				animFrames.get(State.DEAD).put(Direction.UP, new Animation<TextureRegion>(frameDelay, upFrames));
				animFrames.get(State.DEAD).put(Direction.RIGHT, new Animation<TextureRegion>(frameDelay, rightFrames));
				animFrames.get(State.DEAD).put(Direction.DOWN, new Animation<TextureRegion>(frameDelay, downFrames));
				animFrames.get(State.DEAD).put(Direction.LEFT, new Animation<TextureRegion>(frameDelay, leftFrames));
				break;
			default:
				System.out.println("[Player.java][initAnimation()]: WARNING: No valid animation for State = " + state);
				break;
			}

		} // End of for loop
	} // End of function

	@Override
	protected void defineCollision(World world) {

		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();

		// NOTE: Fixture coordinates are on the MAP, not screen

		// SCALING VARIABLES
		// This converts the pixels on the image to screen pixels
		// i.e. character is 22 pixels wide. 22 * ratio = screen pixel width
		float pixelRatio = Const.PLAYER_DISPLAY_SIZE / Const.ANIM_FRAME_SIZE;

		// BODY
		// A body has properties such as type, location, etc.
		// Create one, and use the world object to add it to the game
		bodyDef.position.set(cam.position.x, cam.position.y);
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		body = world.createBody(bodyDef);

		// FIXTURE - COLLISION
		// A fixture has basic physics properties, such as friction and collision
		// Create a shape for it (rectangle), masking (what it hits), and add it
		// Subtract away the transparent areas of the sprite
		//shape.setAsBox((size / 2) - (pixelRatio * 6), (size / 2) - (pixelRatio * 3));
		shape.setAsBox((Const.PLAYER_DISPLAY_SIZE / 2) - (pixelRatio * 6), (Const.PLAYER_DISPLAY_SIZE / 2) - (pixelRatio * 9));
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = Const.BIT_PLAYER;
		fixtureDef.filter.maskBits = Const.BIT_ENEMY | Const.BIT_ITEM | Const.BIT_COLLIDE | Const.BIT_AGGRO
				| Const.BIT_PROJ;
		body.createFixture(fixtureDef).setUserData("full-body");
		
		shape.dispose();
		
	}
	
	public boolean heal(int amount) {
		
		if(this.health >= Const.PLAYER_MAX_HEALTH) {
			System.out.println("too much health, not healing");
			return false;
		} else if(this.health + amount >= Const.PLAYER_MAX_HEALTH) {
			this.health = Const.PLAYER_MAX_HEALTH;
			return true;
		} else {
			System.out.println("healed");
			this.health += amount;
			return true;
		}
	}
	
	/**
	 * attempts to update the number of plates provided they fall within a suitable
	 * domain
	 * 
	 * returns false if it's unable to perform the operation, otherwise true
	 * 
	 * @param quantity
	 * @param ResourceType
	 * @return
	 */
	public boolean giveResource(int quantity, ResourceType type) {
		
		if(quantity < 0) {
			Gdx.app.log("Out of bounds", "Can't give a negative quantity to player resources");
			return false;
		}
		
		switch (type) {

		case STEEL_PLATE:

			if(this.n_steelPlates + quantity <= Const.PLAYER_MAX_STEEL_PLATE_COUNT) {
				this.n_steelPlates += quantity;
				return true;
			} else {
				return false;
			}

		case COPPER_WIRE:
			
			if(this.n_steelPlates + quantity <= Const.PLAYER_MAX_COPPER_WIRE_COUNT) {
				this.n_copperWire += quantity;
				return true;
			} else {
				return false;
			}

		case ELECTRONIC_CIRCUIT:
			
			if(this.n_steelPlates + quantity <= Const.PLAYER_MAX_ELECTRONIC_CIRCUIT_COUNT) {
				this.n_electronicCircuits += quantity;
				return true;
			} else {
				return false;
			}

		default:
			return false;
		}
	}
	
	public boolean takeResource(int quantity, ResourceType type) {
		if(quantity < 0) {
			Gdx.app.log("Out of bounds", "Can't take a negative quantity away from player resources");
			return false;
		}
		
		switch (type) {

		case STEEL_PLATE:

			if(this.n_steelPlates + quantity > Const.PLAYER_MAX_STEEL_PLATE_COUNT) {
				this.n_steelPlates -= quantity;
				return true;
			} else {
				return false;
			}

		case COPPER_WIRE:
			
			if(this.n_steelPlates + quantity > Const.PLAYER_MAX_COPPER_WIRE_COUNT) {
				this.n_copperWire -= quantity;
				return true;
			} else {
				return false;
			}

		case ELECTRONIC_CIRCUIT:
			
			if(this.n_steelPlates + quantity > Const.PLAYER_MAX_ELECTRONIC_CIRCUIT_COUNT) {
				this.n_electronicCircuits -= quantity;
				return true;
			} else {
				return false;
			}

		default:
			return false;
		}
		
	}

	public boolean hasItem(Item e) {
		return this.inventory.contains(e);
	}
	
	public int getItemIndex(Item e) { 
		return this.inventory.indexOf(e);
	}

	/**
	 * uses the item that the player is currently holding
	 * 
	 * @return
	 */
	public Item useItem() {

		if(this.inventory.size() == 0 || this.itemHeld > this.inventory.size()) {
			return null;
		}
		
		Item item = this.inventory.get(this.itemHeld).use(this);

		if (item != null) {
			this.inventory.remove(this.itemHeld);
			
			this.setNextItemIndex();
			
			return item;
		} else {
			return null;
		}
	}
	
	public Item getCurrentItem() {
		if(this.itemHeld < this.inventory.size()) {
			return this.inventory.get(this.itemHeld);
		} else {
			return null;
		}
	}
	
	private void setNextItemIndex() {
		for (int i = 0; i < this.inventory.size(); i++) {
			if(i < this.inventory.size()) {
				this.itemHeld = i;
				break;
			} else {
				this.itemHeld = 0;
			}
		}
	}
	
	public int getCurrentItemIndex() {
		return this.itemHeld;
	}
	
	public void setCurrentItem(int index) {
		if(index < this.inventory.size()) {
			this.itemHeld = index;
		} else if(this.itemHeld < this.inventory.size()) {
			this.setNextItemIndex();
		}
	}

	public boolean giveItem(Item e) {

		if (this.inventory.size() >= Const.INVENTORY_SIZE) {
			return false;
		} else {
			this.inventory.add(e);
			return true;
		}
	}

	public List<Item> getInventory() {
		return this.inventory;
	}
	
	@Override
	public int getDisplaySize() {
		return Const.PLAYER_DISPLAY_SIZE;
	}

	// Get / Set methods

	public Vector2 getPosition() {
		return body.getPosition();
	}
		
	public int getSpeedMultiplier() {
		return speedMultiplier;
	}
	public void setSpeedMultiplier(int speedMultiplier) {
		this.speedMultiplier = speedMultiplier;
	}
	
	public int getN_steelPlates() {
		return n_steelPlates;
	}

	public int getN_copperWire() {
		return n_copperWire;
	}

	public int getN_electronicCircuits() {
		return n_electronicCircuits;
	}

	public boolean getIsColliding() {
		return isColliding;
	}

	public void setIsColliding(boolean newVal) {
		isColliding = newVal;
	}

}
