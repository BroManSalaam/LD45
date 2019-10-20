package com.connectike.game.screens;

import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.connectike.game.MartianGame;
import com.connectike.game.creatures.BombTurtle;
import com.connectike.game.creatures.CreatureBase;
import com.connectike.game.creatures.Enemy;
import com.connectike.game.creatures.Player;
import com.connectike.game.creatures.Spitter;
import com.connectike.game.entities.Bullet;
import com.connectike.game.entities.Food;
import com.connectike.game.entities.Gun;
import com.connectike.game.entities.Item;
import com.connectike.game.entities.Projectile;
import com.connectike.game.entities.Resource;
import com.connectike.util.Assets;
import com.connectike.util.Const;
import com.connectike.util.PlayerInputManager;
import com.connectike.util.WorldGenerator;

import buildings.Barricade;
import buildings.Building;
import buildings.Turret;
import pathfinding.AStar;
import pathfinding.Node;
import ui.HUD;

public class PlayScreen implements Screen {

	// Rendering variables
	private MartianGame game;
	private OrthographicCamera cam;
	private Viewport viewport;
	private ShapeRenderer shapeRenderer;
	private float camX = 0; // Coordinate of camera focus point (center)
	private float camY = 0;

	// Tile Map variables
	private OrthogonalTiledMapRenderer mapRender;
	private float mapWidth = 0.0F; // In number of tiles
	private float mapHeight = 0.0F; // total pixels = mapHeight * TILE_SIZE
	private List<int[]> mapBlocks;

	// HUD
	HUD hud;

	// Box2d
	private Box2DDebugRenderer b2dr;
	private WorldGenerator worldGen;

	// Creatures
	private Player player;

	// Misc. Objects
	private PlayerInputManager inputManager;

	private boolean displayDebug = false;

	public PlayScreen(MartianGame game) {
		this.game = game;

		cam = new OrthographicCamera();
		viewport = new ScreenViewport(cam);

		System.out.println("[PlayScreen.java][PlayScreen()]: Creating the world...");
		worldGen = new WorldGenerator("maps/full_map.tmx");
		b2dr = new Box2DDebugRenderer();

		// Map variables
		System.out.println("[PlayScreen.java][PlayScreen()]: Loading Tiled map...");
		Assets.load();
		worldGen.createWorld();
//		WorldGenerator.createWorld("maps/martian_map.tmx", map, world);
		mapRender = new OrthogonalTiledMapRenderer(worldGen.getMap());

		shapeRenderer = new ShapeRenderer();

		// Center the camera on the viewport center
		mapWidth = Float.valueOf((Integer) worldGen.getMap().getProperties().get("width"));
		mapHeight = Float.valueOf((Integer) worldGen.getMap().getProperties().get("height"));
		cam.position.set(mapWidth * Const.TILE_SIZE / 2, mapHeight * Const.TILE_SIZE / 2, 0);

		// Input
		inputManager = new PlayerInputManager();
		Gdx.input.setInputProcessor(inputManager);

		// Player setup
		player = new Player(cam, worldGen.getWorld(), inputManager);
		worldGen.attachCollisionManager(player);

		// HUD
		hud = new HUD(this.game.batch);

		// generate items and resources...

//		this.game.items.add(new Food(50, 50, 10));

//		this.game.resources.add(new Resource(10, 10, ResourceType.COPPER_WIRE, 100));

		player.giveItem(new Turret());
		player.giveItem(new Turret());
		player.giveItem(new Food());
		player.giveItem(new Barricade());
		player.giveItem(new Gun());

		mapBlocks = AStar.findBlocks(worldGen.getMap().getLayers().get("Collisions").getObjects());
				
		BombTurtle turtle = new BombTurtle(cam.position.x - 500, cam.position.y - 500, worldGen);
		Spitter testSpitter = new Spitter(cam.position.x + 100, cam.position.y + 100, 5.0F, worldGen);
		
	}

	@Override
	public void show() {

	}

	/**
	 * Will run every render loop to update game variables, states, etc.
	 * 
	 * @param dt
	 */
	public void update(float dt) {

		cam.update();
		mapRender.setView(cam);

		handleInput(dt);
		updateItems(dt);
		updateBuildings(dt);
		updateProjectiles(dt);
		updateEnemies(dt);
		hud.update(player.getN_steelPlates(), player.getN_copperWire(), player.getN_electronicCircuits());

		worldGen.update(dt);
		cam.position.set(player.getPosition().x, player.getPosition().y, 0);
		camX = cam.position.x; // These are used locally for drawing
		camY = cam.position.y;

		player.update(dt);

	}

	public void handleInput(float dt) {
		// Move camera or characters here
		if (Gdx.input.isKeyPressed(Input.Keys.R)) {
			cam.zoom += 0.02;
		} else if (Gdx.input.isKeyPressed(Input.Keys.T)) {
			cam.zoom -= 0.02;
		}

		// NOTE: Movement is handled in Player's handleInput() method

		// Show debug visuals (i.e. collision boxes)
		if (inputManager.keysDown.contains(Input.Keys.C)) {
			displayDebug = !displayDebug;
			if (displayDebug) {
				System.out.println("[PlayScreen.java][handleInput()]: Debug display is ON");
			} else {
				System.out.println("[PlayScreen.java][handleInput()]: Debug display is OFF");
			}
			inputManager.removeInput(Input.Keys.C);
		}

		// TOOD: Remove after development
		if (inputManager.keysDown.contains(Input.Keys.ESCAPE)) {
			System.out.println("[PlayScreen.java][handleInput()]: Exiting game");
			Gdx.app.exit();
		}
		
		// ITEMS

		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
			player.setCurrentItem(0);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
			player.setCurrentItem(1);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
			player.setCurrentItem(2);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_4)) {
			player.setCurrentItem(3);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_5)) {
			player.setCurrentItem(4);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
			player.setCurrentItem(5);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
			player.setCurrentItem(6);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_8)) {
			player.setCurrentItem(7);
		}

		Item currentItem = player.getCurrentItem();

		if (currentItem != null && currentItem instanceof Building && Gdx.input.justTouched()) {
			Vector3 position = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
			Building building = (Building) player.useItem();

			if (building != null) {

				if (building instanceof Turret) {
					((Turret) building).place(position.x, position.y, this.worldGen);
				} else if (building instanceof Barricade) {
					((Barricade) building).place(position.x, position.y, this.worldGen);
				} else {
					Gdx.app.log("Invalid building type", "type " + building.getClass());
				}

				System.out.println("place building! " + building.getClass());
			} else {
				// TODO: make a sound indicating it's not a valid operation
			}
		} else if (currentItem != null && currentItem instanceof Gun && Gdx.input.justTouched()) {
			// SHOOT
			if (inputManager.lastClickX > 0) {
				// Set default spawn location and modify based on player direction
				Vector2 spawnLocation = new Vector2(cam.position.x, cam.position.y);
				if (player.getCurrentDirection() == Player.Direction.UP) { // UP
					spawnLocation.y = cam.position.y + Const.PLAYER_DISPLAY_SIZE / 2;
				} else if (player.getCurrentDirection() == Player.Direction.RIGHT) { // RIGHT
					spawnLocation.x = cam.position.x + Const.PLAYER_DISPLAY_SIZE / 2;
				} else if (player.getCurrentDirection() == Player.Direction.DOWN) { // DOWN
					spawnLocation.y = cam.position.y - Const.PLAYER_DISPLAY_SIZE / 2;
				} else if (player.getCurrentDirection() == Player.Direction.LEFT) { // LEFT
					spawnLocation.x = cam.position.x - Const.PLAYER_DISPLAY_SIZE / 2;
				}
				
				float targetX = inputManager.lastClickX + cam.position.x - viewport.getScreenWidth() / 2;
				float targetY = inputManager.lastClickY + cam.position.y - viewport.getScreenHeight() / 2;
				new Bullet("img/projectiles/projectile.png", 2.0F, spawnLocation, worldGen).setTrajectory(targetX, targetY);

				inputManager.lastClickX = 0;
			}
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
			Item item = player.useItem();

			if (item == null) {
				System.out.println("cant use item in slot " + player.getCurrentItemIndex());
			} else {
				System.out.println("player used item, items left " + player.getInventory().size());
			}
		}

	}

	@Override
	public void render(float delta) {
		update(delta);

		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		mapRender.render();

		game.batch.setProjectionMatrix(cam.combined);
		game.batch.begin(); // Draw begin

		// PLAYER
		// Factor in the camera zoom. Use pow() to prevent scaling issues
		float spriteSize = (float) (Math.pow(1 / cam.zoom, 0.05) * Const.PLAYER_DISPLAY_SIZE);
		game.batch.draw(player.getCurrentAppearance().getKeyFrame(player.getElapsedTime(), player.getIsLooping()),
				camX - (spriteSize / 2), camY - (spriteSize / 2) + 20, spriteSize, spriteSize);
		// + 20 to move hitbox by the player's feet only... and not a constant because I'm lazy
		
		shapeRenderer.begin(ShapeType.Filled);

		// Toggle display options with 'C'
		if (displayDebug) {
			shapeRenderer.setProjectionMatrix(cam.combined);

			for (Item item : this.game.items) {
				shapeRenderer.setColor(Color.RED);
				shapeRenderer.rect(item.getX(), item.getY(), Const.TILE_SIZE, Const.TILE_SIZE);
			}

			for (Resource resource : this.game.resources) {
				shapeRenderer.setColor(Color.PINK);
				shapeRenderer.rect(resource.getX(), resource.getY(), Const.TILE_SIZE, Const.TILE_SIZE);
			}
			
//			Array<Fixture> fixtures = new Array<Fixture>();
//			worldGen.getWorld().getFixtures(fixtures);
			b2dr.render(worldGen.getWorld(), cam.combined);
			
			renderPathfinding(delta);

			// TODO: Keep the following for release
			// render building when holding one on mouse


		} // End debug Draw

		
		// draw building from player's hand
		Item currentItem = player.getCurrentItem();
		
		if (currentItem != null && currentItem instanceof Building) {
			Vector3 position = cam.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
			
			Building building = (Building) currentItem;
			
			game.batch.draw(building.getTexture(), position.x, position.y, Const.BARRICADE_SIZE, Const.BARRICADE_SIZE);
		}
		
		// BUILDINGS
		for (Building building : worldGen.buildings) {
			game.batch.draw(building.getTexture(), building.getX(), building.getY(), building.getWidth(), building.getHeight());
		}
		
		// Enemies
		for(Enemy enemy : worldGen.enemies) {
			game.batch.draw(enemy.getCurrentAppearance().getKeyFrame(enemy.getElapsedTime(), true), enemy.getX(), enemy.getY(), enemy.getDisplaySize(), enemy.getDisplaySize());
		}
		
		drawProjectiles(game.batch);
		renderHUD();
		
		shapeRenderer.end();
		game.batch.end(); // Draw end
		
		game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		hud.stage.draw();
	}

	public void updateItems(float dt) {

		Iterator<Item> itemIterator = this.game.items.iterator();
		Iterator<Resource> resourceIterator = this.game.resources.iterator();

		while (itemIterator.hasNext()) {
			Item item = itemIterator.next();

			if (Math.abs(Vector2.dst(cam.position.x, cam.position.y, item.getX(),
					item.getY())) < Const.PLAYER_PICKUP_RANGE) {

				boolean canPickup = player.giveItem(item);

				if (canPickup) {
					itemIterator.remove();
					System.out.println("player picked up " + item.getName());
				}
			}
		}

		while (resourceIterator.hasNext()) {
			Resource resource = resourceIterator.next();

			if (Math.abs(Vector2.dst(cam.position.x, cam.position.y, resource.getX(),
					resource.getY())) < Const.PLAYER_PICKUP_RANGE) {

				boolean canPickup = player.giveResource(resource.quantity, resource.type);
				System.out.println(canPickup);

				if (canPickup) {
					System.out.println("player picked up " + resource.quantity + resource.type);
					resourceIterator.remove();
				}
			}
		}
	}

	public void renderPathfinding(float dt) {
		
		Iterator<Enemy> creatureIterator = worldGen.enemies.iterator();
		
		while(creatureIterator.hasNext()) {
			Enemy creature = creatureIterator.next();
			
			// draw their path
			
			if(creature.getCurrentPath() != null) {
//				System.out.println(creature.getCurrentPath());
				for(Node node : creature.getCurrentPath()) {
					shapeRenderer.setColor(Color.CYAN);
					shapeRenderer.rect(node.getX() * Const.TILE_SIZE, node.getY() * Const.TILE_SIZE, Const.TILE_SIZE, Const.TILE_SIZE);
				}				
			}
		}
	}
	
	public void renderHUD() {

		// draw hotbar

		float width = cam.viewportWidth;
		float height = cam.viewportHeight;

		Vector3 position = cam.unproject(new Vector3(width - (float) (Const.HUD_HOTBAR_OFFSET_X * width),
				height - (float) (Const.HUD_HOTBAR_OFFSET_Y * height), 0));

		Texture hotbarTexture = Assets.manager.get(Assets.hotbar);

		float hotbarWidth = hotbarTexture.getWidth();
		float hotbarHeight = hotbarTexture.getHeight();

		this.game.batch.draw(hotbarTexture, position.x - (hotbarWidth / 2 * Const.HUD_HOTBAR_SCALE),
				position.y - (hotbarHeight / 2 * Const.HUD_HOTBAR_SCALE), hotbarWidth * Const.HUD_HOTBAR_SCALE,
				hotbarHeight * Const.HUD_HOTBAR_SCALE);

		// draw inventory

		Texture itemTexture;
		Texture highlightTexture = Assets.manager.get(Assets.hotbarHighlight);
		int itemIndex = 0;

		for (Item item : player.getInventory()) {

			if (item instanceof Food) {
				itemTexture = Assets.manager.get(Assets.food);
			} else if (item instanceof Barricade) {
				itemTexture = Assets.manager.get(Assets.barricade);
			} else if (item instanceof Turret) {
				itemTexture = Assets.manager.get(Assets.turret);
			} else {
				// TODO: check for incorrect item
				itemTexture = Assets.manager.get(Assets.food);
			}

			float itemWidth = itemTexture.getWidth();
			float itemHeight = itemTexture.getHeight();

			position = cam.unproject(new Vector3(
					width - (float) (Const.HUD_HOTBAR_OFFSET_X * width) - ((hotbarWidth / 2 * Const.HUD_HOTBAR_SCALE)),
					height - (float) (Const.HUD_HOTBAR_OFFSET_Y * height) + (itemHeight / 2), 0));

			game.batch.draw(itemTexture, position.x + (itemWidth * itemIndex * Const.HUD_HOTBAR_PADDING)
					+ (hotbarWidth * Const.HUD_HOTBAR_OFFSET), position.y, itemWidth, itemHeight);

			
			game.batch.draw(highlightTexture, position.x + (32 * player.getCurrentItemIndex() * Const.HUD_HOTBAR_PADDING)
					+ (hotbarWidth * Const.HUD_HOTBAR_OFFSET), position.y, 32, 32);
			
			itemIndex++;
		}


		// indicate what item is currently selected
	}

	private void updateBuildings(float dt) {

		Iterator<Building> buildingIterator = worldGen.buildings.iterator();
		
		while (buildingIterator.hasNext()) {

			Building building = buildingIterator.next();
			
			if (building.getHealth() <= 0) {
				buildingIterator.remove();
				building.destroy(worldGen.getWorld());
			}
			
			if (building instanceof Turret) {
				// TODO: implement turret firing
			}
		}

	}

	/**
	 * Will update all projectiles that are in the Array List found in the World
	 * Generator.
	 * 
	 * @param delta delta time
	 * 
	 * @author seth
	 */
	private void updateProjectiles(float delta) {

		Iterator<Projectile> projIterator = worldGen.projectiles.iterator();

		Projectile currentProj;
		while (projIterator.hasNext()) {
			currentProj = projIterator.next();

			// Is projectile scheduled for destroy?
			if (currentProj.getIsDead()) {
				currentProj.destroy();
				projIterator.remove();
				// p = p - 1; // Since all elements will shift down by one
				continue;
			} else {
				currentProj.update(delta);
			}
		}
	}

	private void drawProjectiles(SpriteBatch graphicObj) {

		Iterator<Projectile> projIterator = worldGen.projectiles.iterator();

		Projectile currentProj;
		while (projIterator.hasNext()) {
			currentProj = projIterator.next();
			// Draw the sprite with its size
			graphicObj.draw(currentProj.getAppearance(), currentProj.getPosition().x, currentProj.getPosition().y,
					currentProj.getSpriteSize(), currentProj.getSpriteSize());
		}
	}
	
	private void updateEnemies(float delta) {
		
		Iterator<Enemy> enemyIterator = worldGen.enemies.iterator();
		
		System.out.println(worldGen.enemies.size());
		
		while (enemyIterator.hasNext()) {

			Enemy enemy = enemyIterator.next();
			
			if(enemy.getHealth() <= 0) {
				enemy.die();
				enemyIterator.remove();
			} else {
				enemy.update(delta, this.player, this.mapBlocks);
			}
		}
		
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		worldGen.dispose(); // Disposes map and world
		Assets.dispose();
		shapeRenderer.dispose();
	}

}
