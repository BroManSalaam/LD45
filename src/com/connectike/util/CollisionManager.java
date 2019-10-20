package com.connectike.util;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.connectike.game.creatures.BombTurtle;
import com.connectike.game.creatures.CreatureBase;
import com.connectike.game.creatures.Player;
import com.connectike.game.entities.Projectile;

import buildings.Barricade;
import buildings.Building;

public class CollisionManager implements ContactListener {
	
	private Player player;
	
	/**
	 * Use this to initialize the collision manager.
	 * 
	 * @param p
	 * Player object
	 * 
	 * @author seth
	 */
	public CollisionManager(Player p) {
		this.player = p;
	}
	
	@Override
	public void beginContact(Contact contact) {
				
		Fixture fixA = contact.getFixtureA();
		Fixture fixB = contact.getFixtureB();
		
		// TODO: Remove for release
		//System.out.println("Collision: " + fixA.getUserData() + " " + fixB.getUserData());
		
		// If either fixture is the player, proceed
		if((fixA.getUserData() == "full-body") || (fixB.getUserData() == "full-body")) {
			Fixture playerFix;
			Fixture collidedFix;
			
			// Now that we know one is the player, find out which one
			if(fixA.getUserData() == "full-body") {
				playerFix = fixA;
				collidedFix = fixB;
			} else if(fixB.getUserData() == "full-body") {
				playerFix = fixB;
				collidedFix = fixA;
			} else {
				System.out.println("[CollisionManager.java][beginContact]: " +
									"Fixture was mistakenly identified as player!");
				return;
			}
			
			
		// PROJECTILE
		// If either fixture is a projectile, do this stuff
		} else if((fixA.getUserData() instanceof Projectile) || (fixB.getUserData() instanceof Projectile)) {
			Fixture projectileFix;
			Fixture collidedFix;
			
			// Now that we know one is the player, find out which one
			if(fixA.getUserData() instanceof Projectile) {
				projectileFix = fixA;
				collidedFix = fixB;
			} else if(fixB.getUserData() instanceof Projectile) {
				projectileFix = fixB;
				collidedFix = fixA;
			} else {
				System.out.println("[CollisionManager.java][beginContact()]: " +
									"Fixture was mistakenly identified as projectile!");
				return;
			}
			
			Projectile proj = (Projectile) projectileFix.getUserData();
			
			// Find out what it hit
			// PLAYER
			if(collidedFix.getUserData() == "full-body") {
				
				// Do some damage
				this.player.setHealth(this.player.getHealth() - proj.getHealthEffect());
				
				System.out.println("[CollisionManager.java][beginContact()]: New Player health=" + player.getHealth());
				
			// CREATURE
			} else if(collidedFix.getUserData() instanceof CreatureBase) {
				CreatureBase creature = (CreatureBase) collidedFix.getUserData();
				
				// Do some damage
				creature.setHealth(creature.getHealth() - proj.getHealthEffect());
				
			// BUILDING
			} else if(collidedFix.getUserData() instanceof Building) {
				
				Building hitBuilding = (Building) collidedFix.getUserData();
				hitBuilding.damage(proj.getHealthEffect());
								
			}
			proj.scheduleDestroy(); // Destroy the projectile
						
		} else if((fixA.getUserData() instanceof CreatureBase) || (fixB.getUserData() instanceof CreatureBase)) {
			
			Fixture enemyFix = null;
			Fixture barricadeFix = null;
			
			if(fixA.getUserData() instanceof BombTurtle && fixB.getUserData() instanceof Barricade) {
				enemyFix = fixA;
				barricadeFix = fixB;
			} else if(fixB.getUserData() instanceof BombTurtle && fixA.getUserData() instanceof Barricade) {
				enemyFix = fixB;
				barricadeFix = fixA;
			} else {
				return;
			}
			
			BombTurtle turtle = (BombTurtle) enemyFix.getUserData();
			
			
		}
		
	}

	@Override
	public void endContact(Contact contact) {
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
	
}
