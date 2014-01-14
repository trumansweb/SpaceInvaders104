package org.truman.spaceinvaders;

import java.awt.Rectangle;

import org.newdawn.spaceinvaders.Entity;

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
	/** The rectangle used for this entity during collisions  resolution */
	private Rectangle me = new Rectangle();
	/** The rectangle used for other entities during collision resolution */
	private Rectangle him = new Rectangle();
	private Entity my;

	public CollisionDetection(Entity e){
		my = e;
	}
	
	/**
	 * Check if this entity collided with another.
	 * 
	 * @param other The other entity to check collision against
	 * @return True if the entities collide with each other
	 */
	public boolean collidesWith(Entity other) {
		
		me.setBounds((int) my.getX(),(int) my.getY(),my.getSprite().getWidth(),my.getSprite().getHeight());
		him.setBounds((int) other.getX(),(int) other.getY(),other.getSprite().getWidth(),other.getSprite().getHeight());

		if(me.intersects(him)){
		
			double ax = my.getXd();
			double ay = my.getYd();
			int aw = my.getSprite().getWidth();
			int ah = my.getSprite().getHeight();

			double bx = other.getXd();
			double by = other.getYd();
			int bw = other.getSprite().getWidth();
			int bh = other.getSprite().getHeight();

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

			return true;
		
		}
		
		return false;
	
	}
}
