package org.truman.spaceinvaders;

import org.newdawn.spaceinvaders.Entity;
import org.newdawn.spaceinvaders.Game;


/**
 * Collision detection for rectangles
 * bounds should depend on the delta and move speed
 * if you move to fast in the object from left or
 * right, the objects will not collide
 */
public class CollisionDetection {

	public boolean collidedTop;
	public boolean collidedLeft;
	public boolean collidedBottom;
	public boolean collidedRight;
	private double bounds = 3;

	/**
	 * Collision detection for b (e.g. the ship which is b
	 * collides with a which is an Global or Alien Entity etc.)
	 */
	public CollisionDetection(Game game, Entity b, Entity a){

		double ax = a.getXd();
		double ay = a.getYd();
		int aw = a.getSprite().getWidth();
		int ah = a.getSprite().getHeight();

		double bx = b.getXd();
		double by = b.getYd();
		int bw = b.getSprite().getWidth();
		int bh = b.getSprite().getHeight();

		double aTop = ay;
		double aBottom = ay + ah;
		double aLeft = ax;
		double aRight = ax + aw;

		double bTop = by;
		double bBottom = by + bh;
		double bLeft = bx;
		double bRight = bx + bw;

		if(	aBottom > bTop && // collided top
			aTop < bTop && // but not over b
			aLeft < bRight - bounds && // and
			aRight > bLeft + bounds){ // in collision y
			collidedTop = true;
		}
		else if(aTop < bBottom && // collided bottom
				aBottom > bTop && // but not under b
				aLeft < bRight - bounds && // and 
				aRight > bLeft + bounds){// in collision y
			collidedBottom = true;
		}
		else if(aLeft < bRight && // collided left
				bLeft > aLeft) // and not more left then b
			collidedLeft = true;
		else if(aRight > bLeft) collidedRight = true; // collided right

	}

}
