package com.connectike.util;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
	
	public static AssetManager manager = new AssetManager();
	
	// Usage: someTexture = assets.manager.get(Assets.raiderSprite);
	public static void load() {
		// Load assets here, like so...
//		manager.load(raiderSprite);
		manager.load(playerSprite);
		manager.load(spitterSprite);
		manager.load(bombTurtleSprite);
		
		manager.load(hotbar);
		manager.load(hotbarHighlight);
		manager.load(food);
		manager.load(barricade);
		manager.load(turret);
		
		manager.finishLoading();
	}
	
	public static void dispose() {
		manager.dispose();
	}
	
	// Spritesheets
	public static final AssetDescriptor<Texture> playerSprite = 
			new AssetDescriptor<Texture>("img/spritesheets/player.png", Texture.class);
	
	public static final AssetDescriptor<Texture> spitterSprite = 
			new AssetDescriptor<Texture>("img/spritesheets/spittingPlant.png", Texture.class);
	
	public static final AssetDescriptor<Texture> bombTurtleSprite = 
			new AssetDescriptor<Texture>("img/spritesheets/bombTurtle.png", Texture.class);
	
	// Hotbar / items
	public static final AssetDescriptor<Texture> hotbar = 
			new AssetDescriptor<Texture>("img/hotbar.png", Texture.class);
	
	public static final AssetDescriptor<Texture> hotbarHighlight = 
			new AssetDescriptor<Texture>("img/hotbarHighlight.png", Texture.class);
	
	public static final AssetDescriptor<Texture> food = 
			new AssetDescriptor<Texture>("img/items/healthPack.png", Texture.class);
	
	public static final AssetDescriptor<Texture> barricade = 
			new AssetDescriptor<Texture>("img/buildings/barricade.png", Texture.class);
	
	public static final AssetDescriptor<Texture> turret = 
			new AssetDescriptor<Texture>("img/items/turret.png", Texture.class);
	
	
	
    // EXAMPLES:
//    public static final AssetDescriptor<Texture> playerSprite = 
//            new AssetDescriptor<Texture>("spritesheets/player.png", Texture.class);
//    
//    public static final AssetDescriptor<Music> mainmusic = 
//            new AssetDescriptor<Music>("audio/mainmusic.mp3", Music.class);
//    
//    public static final AssetDescriptor<Sound> generatorfuel = 
//            new AssetDescriptor<Sound>("audio/generatorfuel.mp3", Sound.class);
}
