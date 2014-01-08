package org.newdawn.spaceinvaders;

public class GlobalEntity extends Entity{
	/** The game in which the ship exists */
	private Game game;

	/**
	 * Create a new global entity like backgrounds, goodies...
	 *  
	 * @param game The game in which the entity is being created
	 * @param ref The reference to the sprite to show for the entity
	 * @param x The initial x location of the entity
	 * @param y The initial y location of the entity
	 */
	public GlobalEntity(Game game, String ref, double x, double y) {
		super(ref,x,y);

		this.setGame(game);
	}

	public GlobalEntity(Game game, Sprite sprite, double x, double y) {
		super(sprite,x,y);

		this.setGame(game);
	}

	/**
	 * Request that the entity move itself based on an elapsed amount of
	 * time
	 * 
	 * @param delta The time that has elapsed since last move (ms)
	 */
	public void move(long delta) {
		super.move(delta);
	}

	/**
	 * Notification that the entity has collided with something
	 * 
	 * @param other The entity with which this entity has collided
	 */
	public void collidedWith(Entity other) {
	}

	public Game getGame() {
		return game;
	}

	public void setGame(Game game) {
		this.game = game;
	}
}
