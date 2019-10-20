package com.connectike.util;


public class Const {
	
	// GRAPHICS
	public static final int V_WIDTH = 800;
	public static final int V_HEIGHT = 800;
	
	public static final float TILE_SIZE = 32;
	
	public static final int ANIM_FRAME_SIZE = 32; // Width & Height of each frame
	public static final int ANIM_FRAME_LENGTH = 4; // Number of frames in animation
	public static final int PLAYER_DISPLAY_SIZE = 75; // Looks good against tiles
	
	// Bits (for collision)
	public static final short BIT_DEFAULT = 2; // Is ignored by player
	public static final short BIT_PLAYER = 4;
	public static final short BIT_ENEMY = 8;
	public static final short BIT_ITEM = 16;
	public static final short BIT_COLLIDE = 32; // For walls and things that are static
	public static final short BIT_AGGRO = 64;
	public static final short BIT_PROJ = 128; // For projectiles / weapons
	
	// PLAYER
	public static final float PLAYER_PICKUP_RANGE = 64;
	public static final int PLAYER_MAX_STEEL_PLATE_COUNT = 500;
	public static final int PLAYER_MAX_COPPER_WIRE_COUNT = 300;
	public static final int PLAYER_MAX_ELECTRONIC_CIRCUIT_COUNT = 100;
	
	public static final int INVENTORY_SIZE = 8;
	public static final int PLAYER_MAX_HEALTH = 100;
	public static final int PLAYER_STARTING_HEALTH = 100;
	public static final float PLAYER_SPEED = 100.0F;
	
	// ENEMY - TURTLE
	public static final int TURTLE_DISPLAY_SIZE = 50;
	public static final float TURTLE_EXPLOSION_SIZE = 256;
	public static final float TURTLE_EXPLOSION_DAMAGE = 1000;
	public static final float TURTLE_EXPLOSION_OPTIMAL_RANGE = 128;
	public static final float TURTLE_SPEED = 800;
	public static final float TURTLE_EXPLOSION_TIME = 2;
	
	// ENEMY - SPITTER
	public static final int SPITTER_DISPLAY_SIZE = 50;
	public static final int SPITTER_RANGE = 250;
	public static final int SPITTER_HEALTH = 10;
	
	// BUILDINGS
	public static final float TURRET_HEALTH = 250;
	public static final float TURRET_SIZE = 96;
	public static final float BARRICADE_HEALTH = 500;
	public static final float BARRICADE_SIZE = 96;
	
	// HUD
	public static final double HUD_HOTBAR_OFFSET_X = 0.50;
	public static final double HUD_HOTBAR_OFFSET_Y = 0.075;
	public static final float HUD_HOTBAR_SCALE = 6;
	public static float HUD_HOTBAR_PADDING = 1.52f;
	public static float HUD_HOTBAR_OFFSET = 0.1f;
	
	
	
	
	
	
}
