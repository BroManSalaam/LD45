package com.connectike.game.entities;


public class Resource {
	
	public final float x;
	public final float y;
	
	public enum ResourceType {
		STEEL_PLATE,
		COPPER_WIRE,
		ELECTRONIC_CIRCUIT
	}
	
	public final ResourceType type;
	public int quantity = 0;

	public Resource(float x, float y, ResourceType type, int quantity) {
		this.x = x;
		this.y = y;
		this.quantity = quantity;
		this.type = type;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public ResourceType getType() {
		return type;
	}

	public float getQuantity() {
		return quantity;
	}

	
	
}
