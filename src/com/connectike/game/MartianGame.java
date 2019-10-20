package com.connectike.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.connectike.game.entities.Item;
import com.connectike.game.entities.Resource;
import com.connectike.game.screens.PlayScreen;

import buildings.Building;

public class MartianGame extends Game {
	
	public SpriteBatch batch;
	PlayScreen playScreen;
	
	public List<Item> items;
	public List<Resource> resources;
	
//	public List<Building> buildings;
	
	@Override
	public void create() {
		batch = new SpriteBatch();
		
		System.out.println("[MartianGame.java][create()]: Initializing...");
		
		items = new ArrayList<Item>();
		resources = new ArrayList<Resource>();
//		buildings = new ArrayList<Building>();
		
		playScreen = new PlayScreen(this);
		setScreen(playScreen);
	}
	
	@Override
	public void render() {
		super.render();
	}
	
	@Override
	public void dispose() {
		batch.dispose();
	}
	
	public void addResource(Resource e) {
		this.resources.add(e);
	}
	
	public void addItem(Item e) {
		this.items.add(e);
	}
}
