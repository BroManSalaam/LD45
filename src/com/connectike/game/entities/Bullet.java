package com.connectike.game.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.connectike.game.creatures.CreatureBase;
import com.connectike.util.Const;
import com.connectike.util.WorldGenerator;

public class Bullet extends Projectile {
	
	private final int BULLET_SIZE = 12;
	private final float VELOCITY = 12.0f; // Increase in direction every update()
	private final String PATH;
	
	private final Vector2 spawn;
	private Vector2 movementChange; // Will be added to position every loop
	
	/**
	 * A simple projectile which has a single texture (no animation) and
	 * dies quickly and non-dramatically.
	 * 
	 * @param lifeTime
	 * amount of time (in seconds) that the bullet should persist for
	 * @param spawnLocation
	 * Vector2 of where the bullet should spawn
	 * @param worldGen
	 * world generator object
	 * @param cam
	 * OrthographicCamera object to factor in zoom
	 * 
	 * @author seth
	 */
	public Bullet(String path, float lifeTime, Vector2 spawnLocation, WorldGenerator worldGen) {
		
		this.PATH = path;
		this.lifeLeft = lifeTime;
		this.healthEffect = 500;
		this.spawn = spawnLocation;
		this.movementChange = new Vector2(0.0F, 0.0F); // Dummy value
		
		this.world = worldGen.getWorld();
		
		worldGen.projectiles.add(this);
		
		defineCollision();
	}
	
	@Override
	public void update(float delta) {
		lifeLeft = lifeLeft - delta;
				
		if(lifeLeft <= 0.0F) {
			isDead = true;
		}
		
		if(!isDead) {
			body.setTransform(body.getPosition().x + movementChange.x, body.getPosition().y + movementChange.y, 0.0f);
		}
		
	}
	
	@Override
	public void onHit(CreatureBase hitCreature) {
		System.out.println("[Bullet.java][onHit()]: Hit a creature");
	}
	
	/**
	 * Sets the bullet on a new path that it will follow
	 * based on a "target" (x,y) pair. However, the bullet will not stop
	 * at the point; it will stop when it hits something or its life
	 * expires.
	 * NOTE: Should be called AFTER Bullet is initialized
	 * 
	 * @usage
	 * setTrajectory(playerX + 16, playerY + 2);
	 * 
	 * @param xPos
	 * x point along the new path of the bullet
	 * @param yPos
	 * y point in the path of the bullet
	 * 
	 * @author seth
	 */
	public void setTrajectory(float xPos, float yPos) {
		
		// Use doubles since Math functions uses them
		double dx = xPos - getPosition().x; // Change in x
		double dy = yPos - getPosition().y;
		
		double currentX = getPosition().x;
		double currentY = getPosition().y;
				
		System.out.println("Change: " + dx + ", " + dy);
		
		// First, find the angle of the position to the bullet's position
		double angle = Math.atan(dy / dx);
		
		// We know how fast the bullet should be moving (VELOCITY)
		// Take  degree and multiply by VELOCITY to get x & y components
		float xComp = (float) Math.cos(angle) * VELOCITY;
		float yComp = (float) Math.sin(angle) * VELOCITY;
		
		// Verify the signs (+ or -) of components with original dx dy
		if((dx / Math.abs(dx)) != (xComp / Math.abs(xComp))) {
			xComp = xComp * -1;
		}
		if((dy / Math.abs(dy)) != (yComp / Math.abs(yComp))) {
			yComp = yComp * -1;
		}
		
		System.out.println("New Trajectory: " + xComp + ", " + yComp + " @ " + Math.toDegrees(angle));
		
		setMovementChange(new Vector2(xComp, yComp));
	}
	
	@Override
	protected void defineCollision() {
		
		BodyDef bodyDef = new BodyDef();
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		
		// NOTE: Fixture coordinates are on the MAP, not screen

		// BODY
		bodyDef.position.set(spawn);
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		body = world.createBody(bodyDef);
		
		// FIXTURE - COLLISION
		shape.setAsBox((BULLET_SIZE / 2), (BULLET_SIZE / 2));
		fixtureDef.shape = shape;
		fixtureDef.filter.categoryBits = Const.BIT_PROJ;
		fixtureDef.filter.maskBits = Const.BIT_PLAYER | Const.BIT_ENEMY | Const.BIT_ITEM | Const.BIT_AGGRO | Const.BIT_COLLIDE;
		// Attach this object to user data for use in parsing later
		body.createFixture(fixtureDef).setUserData(this);
		
	}
	
	@Override
	public Texture getAppearance() {
		return new Texture(PATH);
	}
	
	@Override
	public int getSpriteSize() {
		return BULLET_SIZE;
	}
	
	public Vector2 getMovementChange() {
		return movementChange;
	}
	public void setMovementChange(Vector2 newMovement) {
		this.movementChange = newMovement;
	}
	
	
	
}
