package org.newdawn.spaceinvaders;

import java.awt.event.KeyEvent;

import org.truman.spaceinvaders.CollisionDetection;

/**
 * The entity that represents the players ship
 * 
 * @author Kevin Glass
 */
public class ShipEntity extends Entity {
	/** The game in which the ship exists */
	private Game game;
	
	/**
	 * Create a new entity to represent the players ship
	 *  
	 * @param game The game in which the ship is being created
	 * @param ref The reference to the sprite to show for the ship
	 * @param x The initial x location of the player's ship
	 * @param y The initial y location of the player's ship
	 */
	public ShipEntity(Game game,String ref,int x,int y) {
		super(ref,x,y);
		
		this.game = game;
	}
	
	/**
	 * Request that the ship move itself based on an elapsed amount of
	 * time
	 * 
	 * @param delta The time that has elapsed since last move (ms)
	 */
	public void move(long delta) {
		// if we're moving left and have reached the left hand side
		// of the screen, don't move
		if ((dx < 0) && (x < 10)) {
			return;
		}
		// if we're moving right and have reached the right hand side
		// of the screen, don't move
		if ((dx > 0) && (x > game.getWidth()-super.getSprite().getWidth()-10)) {
			return;
		}
		if ((dy < 0) && (y < 10)) {
			return;
		}
		if ((dy > 0) && (y > game.getHeight()-super.getSprite().getHeight()-10)) {
			return;
		}		
		super.move(delta);
	}
	
	/**
	 * Notification that the player's ship has collided with something
	 * 
	 * @param other The entity with which the ship has collided
	 */
	public void collidedWith(Entity other) {
		// if its an alien, notify the game that the player
		// is dead
		if (other instanceof AlienEntity) {
			game.notifyDeath();
		}
		if (other instanceof GlobalEntity) {
			CollisionDetection d = new CollisionDetection(this.game, this, other);
			if(d.collidedTop){
				game.addBlockedKey(KeyEvent.VK_UP);
				super.setVerticalMovement(super.getVerticalMovement()+other.getVerticalMovement());
			}
			if(d.collidedLeft){
				game.addBlockedKey(KeyEvent.VK_LEFT);
			}
			if(d.collidedBottom){
				game.addBlockedKey(KeyEvent.VK_DOWN);
			}
			if(d.collidedRight){
				game.addBlockedKey(KeyEvent.VK_RIGHT);
			}
		}
	}
	
	public void doLogic(long delta) {

	    
	}
}