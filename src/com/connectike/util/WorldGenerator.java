package com.connectike.util;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.connectike.game.creatures.CreatureBase;
import com.connectike.game.creatures.Enemy;
import com.connectike.game.creatures.Player;
import com.connectike.game.entities.Projectile;

import buildings.Building;

public class WorldGenerator {
	
	private String mapPath;
	private TiledMap map;
	
	private World world;
	public List<Enemy> enemies;
	public List<Projectile> projectiles;
	public List<Building> buildings;
	
	/**
	 * Should handle all core mechanics of map and world generation.
	 * It will take in a map path and populate class
	 * variables for use later with getters.
	 * 
	 * @param path
	 * relative path (working directory) to map TMX file
	 * 
	 * @author seth
	 */
	public WorldGenerator(String path) {
		
		this.mapPath = path;
		
		
		// Initialize the map with the map loader
		TmxMapLoader mapLoader = new TmxMapLoader();
		map = mapLoader.load(mapPath);
		
		// Create the world
		world = new World(new Vector2(0, 0), true);
		
		enemies = new ArrayList<Enemy>();
		projectiles = new ArrayList<Projectile>();
		buildings = new ArrayList<Building>();
		
	}
	
	/**
	 * Called in update loop of active screen to enable world stepping.
	 * 
	 * @param delta
	 * delta time
	 * 
	 * @author seth
	 */
	public void update(float delta) {
		
		world.step(1/120f, 6, 2);
	}
	
	
	public void createWorld() {
		
		/*
		 * all of the indices for the objects in the TMX file
		 * 
		 * example:
		 * 
		 * Tiled Layer: background:  3
		 * Object Layer: Walls:      2
		 * Tiled Layer: NPC's:       1
		 * Object Layer: Houses:     0
		 * 
		 * we would loop through those two object layers
		 * 
		 */
		
		
		// Use the following variables for Box2D configuration
		BodyDef bdef = new BodyDef();
		FixtureDef fdef = new FixtureDef();
		
		PolygonShape shape = new PolygonShape();
		Body body;
		
		
		// WALLS / STATIC COLLISIONS
		// Messy, but it gets the job done *shrugs*
		for(MapObject object : map.getLayers().get(getLayerIndex("Collisions", mapPath)).getObjects().getByType(RectangleMapObject.class)) {
			
			Rectangle rect = ((RectangleMapObject) object).getRectangle();
			
			bdef.type = BodyDef.BodyType.StaticBody;
			bdef.position.set(rect.getX() + rect.getWidth() / 2, rect.getY() + rect.getHeight() / 2);
			body = world.createBody(bdef);
			
			shape.setAsBox(rect.getWidth() / 2, rect.getHeight() / 2);
			fdef.shape = shape;
			fdef.filter.categoryBits = Const.BIT_COLLIDE;
			fdef.filter.maskBits = Const.BIT_PLAYER | Const.BIT_ENEMY | Const.BIT_PROJ;
			body.createFixture(fdef).setUserData("solid");
		}
		
		
		
	}
	
	/**
	 * Call to begin listening for collision with the CollisionManager.
	 * Since many of the collisions affect Player variables, the Player
	 * object is needed. However, it is circularly dependent with
	 * map and world, so Player is attached later.
	 * 
	 * @param p
	 * Player object
	 * 
	 * @author seth
	 */
	public void attachCollisionManager(Player p) {
		world.setContactListener(new CollisionManager(p));
	}
	
	/**
	 * Should be called at end of program to properly close
	 * active variables.
	 * 
	 * @author seth
	 */
	public void dispose() {
		map.dispose();
		world.dispose();
	}
	
	/**
	 * Gets the index of the desired layer based on the
	 * given name. Format all names as they appear
	 * in Tiled editor. This prevents confusion between
	 * map makers and programmers because the index may
	 * change based on creator.
	 * 
	 * @param name
	 * the name of the desired layer index
	 * @param mapPath
	 * path to the loaded map
	 * 
	 * @return
	 * The index of the layer with the matching name.
	 * Will return -1 if no match is found
	 * 
	 * @author seth
	 */
	private int getLayerIndex(String name, String mapPath) {
		
		FileHandle handler = Gdx.files.internal(mapPath);
		String content = handler.readString();
		int nameIndex = 0; // Index of "name"
		String layerName = ""; // Current parsed layer name
		int layerCount = 0; // Incrememnet this for every layer
		
		while(nameIndex != -1) {
			
			// +6 to remove name="   and +1 to get the next name
			nameIndex = content.indexOf("name=\"", nameIndex + 1) + 6;
			if(nameIndex != -1) {
				layerName = content.substring(nameIndex, content.indexOf("\"", nameIndex));
				
				if(layerName.equalsIgnoreCase(name)) {
					return layerCount;
				} else {
					layerCount++;
				}
			}
			
		}
		System.out.println("[WorldGenerator.java][getLayerIndex()]: Layer couldn't be found " + 
							"with name " + name);
		return -1;
	}
	
	
	// Get / Set methods
	
	public String getMapPath() {
		return mapPath;
	}
	
	public TiledMap getMap() {
		return map;
	}
	
	public World getWorld() {
		return world;
	}
	
}
