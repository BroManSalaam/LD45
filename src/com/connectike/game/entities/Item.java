package com.connectike.game.entities;

import com.badlogic.gdx.math.Vector2;

public abstract class Item implements Usable {
	
	public enum Type {
		WEAPON,
		BUILDING,
		HEALTH
	}
	
	Type type;
	
	protected float x;
	protected float y;
		
	public String name;
	public String description;
	
	public int quantity;
	
	
	public Item() {
		
	}
	
	public Item(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setPosition(Vector2 position) {
		this.x = position.x;
		this.y = position.y;
	}
	

	public float getX() {
		return x;
	}


	public float getY() {
		return y;
	}


	public String getName() {
		return name;
	}


	public String getDescription() {
		return description;
	}


	public int getQuantity() {
		return quantity;
	}
}
