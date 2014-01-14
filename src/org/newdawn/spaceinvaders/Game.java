package org.newdawn.spaceinvaders;

import java.awt.Canvas;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.truman.spaceinvaders.CollisionDetection;
import org.truman.spaceinvaders.SoundManager;

/**
 * The main hook of our game. This class with both act as a manager
 * for the display and central mediator for the game logic. 
 * 
 * Display management will consist of a loop that cycles round all
 * entities in the game asking them to move and then drawing them
 * in the appropriate place. With the help of an inner class it
 * will also allow the player to control the main ship.
 * 
 * As a mediator it will be informed when entities within our game
 * detect events (e.g. alient killed, played died) and will take
 * appropriate game actions.
 * 
 * @author Kevin Glass
 */
public class Game extends Canvas implements GameWindowCallback {

	private static final long serialVersionUID = 1L;
	/** The list of all the entities that exist in our game */
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	/** The list of all moving entities like backgrounds that exist in our game */
	private ArrayList<GlobalEntity> backgroundEntities = new ArrayList<GlobalEntity>();
	/** The list of entities that need to be removed from the game this loop */
	private ArrayList<Entity> removeList = new ArrayList<Entity>();
	/** The entity representing the player */
	private Entity ship;
	/** The speed at which the player's ship should move (pixels/sec) */
	private double moveSpeed = 300;
	/** The time at which last fired a shot */
	private long lastFire = 0;
	/** The interval between our player is allowed to shot (ms) */
	private long firingInterval = 100;
	/** The number of aliens left on the screen */
	private int alienCount;

	/** The message to display which waiting for a key press */
	private Sprite message;
	/** True if we're holding up game play until a key has been pressed */
	private boolean waitingForKeyPress = true;
	/** True if game logic needs to be applied this loop, normally as a result of a game event */
	private boolean logicRequiredThisLoop = false;

	/** The time at which the last rendering looped started from the point of view of the game logic */
	private long lastLoopTime = System.currentTimeMillis();
	/** The window that is being used to render the game */
	private GameWindow window;
	/** True if the fire key has been released */
	private boolean fireHasBeenReleased = false;

	/** The sprite containing the "Press Any Key" message */
	private Sprite pressAnyKey;
	/** The sprite containing the "You win!" message */
	private Sprite youWin;
	/** The sprite containing the "You lose!" message */
	private Sprite gotYou;

	/** The time since the last record of fps */
	private long lastFpsTime = 0;
	/** The recorded fps */
	private int fps;

	/** The normal title of the window */
	private String windowTitle = "Space Invaders 104 - Version (0.4)";

	/** The screen size */
	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	private int width = 800;//gd.getDisplayMode().getWidth();
	private int height = 600;//gd.getDisplayMode().getHeight();
	private int level = 1;
	private boolean fire2HasBeenReleased = true;
	private boolean pause = false;
	private boolean vsync = true;
	private boolean pressEnter;
	private Sprite bg;
	private SoundManager sm;
	private int sb1;
	private int sb2;
	private int bgm;
	private String bgmref;
	private String currentBgmref;
	private ArrayList<Integer> blockedKeys = new ArrayList<Integer>();

	/**
	 * Construct our game and set it running.
	 * 
	 * @param renderingType The type of rendering to use (should be one of the contansts from ResourceFactory)
	 */
	public Game(int renderingType) {
		// create a window based on a chosen rendering method
		ResourceFactory.get().setRenderingType(renderingType);
		setWindow(ResourceFactory.get().getGameWindow());
		getWindow().setResolution(width,height);
		getWindow().setGameWindowCallback(this);
		getWindow().setTitle(getWindowTitle());
		
		getWindow().startRendering();
	}

	/**
	 * Intialise the common elements for the game
	 */
	public void initialise() {

		gotYou = ResourceFactory.get().getSprite("sprites/gotyou.gif");
		pressAnyKey = ResourceFactory.get().getSprite("sprites/pressanykey.gif");
		youWin = ResourceFactory.get().getSprite("sprites/youwin.gif");

		message = pressAnyKey;

		sm = new SoundManager();
		sm.initialize(10);
		sb1 = sm.addSound("sounds/sparo.wav");
		sb2 = sm.addSound("sounds/Blaster-Solo.wav");

		startGame();

	}

	/**
	 * Start a fresh game, this should clear out any old data and
	 * create a new set.
	 */
	private void startGame() {
		// clear out any existing entities and initialize a new set
		entities.clear();
		initEntities();
		if(!waitingForKeyPress){
			if (bgmref != currentBgmref) {
				currentBgmref = bgmref;
				bgm = sm.addSound(bgmref);
				sm.playSound(bgm);
			}
		}
	}

	/**
	 * Initialize the starting state of the entities (ship and aliens). Each
	 * entity will be added to the overall list of entities in the game.
	 */
	private void initEntities() {

		// create the player ship and place it roughly in the center of the screen
		ship = new ShipEntity(this,"sprites/ship.gif",0,0);
		ship.x = width/2-ship.getSprite().getWidth()/2;
		ship.y = height-ship.getSprite().getHeight()-5;
		entities.add(ship);

		// setup level data
		switch (level) {
		case 1:
			bg = ResourceFactory.get().getSprite("sprites/152.gif");
			bgmref = "sounds/The Terminator (1984) Theme.wav";
			break;
		case 2:
			bg = ResourceFactory.get().getSprite("sprites/152.gif");
			bgmref = "sounds/The Terminator (1984) Theme.wav";
			break;
		case 3:
			bg = ResourceFactory.get().getSprite("sprites/152.gif");
			bgmref = "sounds/The Terminator (1984) Theme.wav";
			break;
		case 4:
			bg = ResourceFactory.get().getSprite("sprites/152.gif");
			bgmref = "sounds/The Terminator (1984) Theme.wav";
			break;
		case 5:
			bg = ResourceFactory.get().getSprite("sprites/bg2.gif");
			bgmref = "sounds/Filter & The Crystal Method - (Can't You) Trip Like I Do [Official Video] - DASH.WAV";
			break;
		default:
			bg = ResourceFactory.get().getSprite("sprites/bg2.gif");
			bgmref = "sounds/Filter & The Crystal Method - (Can't You) Trip Like I Do [Official Video] - DASH.WAV";
			break;
		}
		// fill the screen
		for (int i = 1; i <= (int)(height/bg.getHeight()+2); i++)
			for (int j = 0; j < (int)(width/bg.getWidth()+1); j++)
				backgroundEntities.add(new GlobalEntity(this, bg, bg.getWidth()*j, height - bg.getHeight()*i));
		// arrange enemies
		alienCount = 8;
		/*for (int x=0;x<10;x++){
			entities.add(new AlienEntity(this,100+(x*50),30));
			alienCount++;
			for (int y=0;y<level;y++){			
				Entity alieny = new AlienEntity(this,100+(x*50),(50*y)+30);
				entities.add(alieny);
				alienCount++;
			}
		}*/
		entities.add(new GlobalEntity(this, "sprites/brick.gif", 340, 50));
	}

	/**
	 * Notification from a game entity that the logic of the game
	 * should be run at the next opportunity (normally as a result of some
	 * game event)
	 */
	public void updateLogic() {
		logicRequiredThisLoop = true;
	}

	/**
	 * Remove an entity from the game. The entity removed will
	 * no longer move or be drawn.
	 * 
	 * @param entity The entity that should be removed
	 */
	public void removeEntity(Entity entity) {
		removeList.add(entity);
	}

	public void updateHealth(Entity first, Entity second) {
		/**
		 * TODO: add strength and calculate against
		 */
		first.setHealth(first.getHealth()-20);
	}

	/**
	 * Notification that the player has died. 
	 */
	public void notifyDeath() {
		message = gotYou;
		waitingForKeyPress = true;
	}

	/**
	 * Notification that the player has won since all the aliens
	 * are dead.
	 */
	public void notifyWin() {
		message = youWin;
		level++;
		pressEnter = true;
	}

	/**
	 * Notification that an alien has been killed
	 */
	public void notifyAlienKilled() {
		// reduce the alien count, if there are none left, the player wins!
		alienCount--;

		if (alienCount == 0) {
			notifyWin();
		}

		// if there are still some aliens left then they all need to get faster, so
		// speed up all the existing aliens
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);

			if (entity instanceof AlienEntity) {
				// speed up by 2%
				entity.setHorizontalMovement(entity.getHorizontalMovement() * 1.02);
			}
		}
	}

	/**
	 * Attempt to fire a shot from the player. Its called "try"
	 * since we must first check that the player can fire at this 
	 * point, i.e. has he/she waited long enough between shots
	 */
	public void tryToFire() {
		// check that we have waiting long enough to fire
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}

		// if we waited long enough, create the shot entity, and record the time.
		lastFire = System.currentTimeMillis();
		ShotEntity shot = new ShotEntity(this,"sprites/shot.gif",ship.getX()+10,ship.getY()-30);
		entities.add(shot);
		sm.playEffect(sb1);
	}

	public void tryToFire2() {
		// check that we have waiting long enough to fire
		if (System.currentTimeMillis() - lastFire < firingInterval) {
			return;
		}

		// if we waited long enough, create the shot entity, and record the time.
		lastFire = System.currentTimeMillis();
		ShotEntity shotL = new ShotEntity(this,"sprites/shot.gif",ship.getX()+2,ship.getY()-22);
		entities.add(shotL);
		ShotEntity shotR = new ShotEntity(this,"sprites/shot.gif",ship.getX()+18,ship.getY()-22);
		entities.add(shotR);
		sm.playEffect(sb2);

	}

	/**
	 * Notification that a frame is being rendered. Responsible for
	 * running game logic and rendering the scene.
	 */
	public void frameRendering() {	
/*		SystemTimer.sleep(lastLoopTime+10-SystemTimer.getTime());

		// work out how long its been since the last update, this
		// will be used to calculate how far the entities should
		// move this loop
		long delta = SystemTimer.getTime() - lastLoopTime;
		lastLoopTime = SystemTimer.getTime();*/
		long delta = System.currentTimeMillis() - lastLoopTime;
		lastLoopTime = System.currentTimeMillis();
		lastFpsTime += delta;
		fps++;
		try { getClass().wait(1000); } catch (Exception e) {}
		// update our FPS counter if a second has passed
		if (lastFpsTime >= 1000) {
			getWindow().setTitle(getWindowTitle()+" (FPS: "+fps+")");
			lastFpsTime = 0;
			fps = 0;
		}

		// resolve the movement of the ship. First assume the ship 
		// isn't moving. If either cursor key is pressed then
		// update the movement appropriately
		ship.setHorizontalMovement(0);
		ship.setVerticalMovement(0);
		
		// make sure we don't have blocked keys 
		blockedKeys.clear();
		
		// brute force collisions, compare every entity against
		// every other entity. If any of them collide notify 
		// both entities that the collision has occurred
		for (int p=0;p<entities.size();p++) {
			for (int s=p+1;s<entities.size();s++) {
				Entity me = (Entity) entities.get(p);
				Entity him = (Entity) entities.get(s);
				if (me.collidesWith(him)) {
					me.collidedWith(him);
					him.collidedWith(me);
				}
			}
		}

		// remove any entity that has been marked for clear up
		entities.removeAll(removeList);
		removeList.clear();
		keyhandling();

		// if a game event has indicated that game logic should
		// be resolved, cycle round every entity requesting that
		// their personal logic should be considered.
		if (logicRequiredThisLoop) {
			for (int i=0;i<entities.size();i++) {
				Entity entity = (Entity) entities.get(i);
				entity.doLogic();
			}

			logicRequiredThisLoop = false;
		}

		if (!waitingForKeyPress && !pause && !pressEnter) {
			// Entity moving
			for (int i=0;i<entities.size();i++) {
				Entity entity = (Entity) entities.get(i);	
				entity.move(delta);
			}
			// Background moving/replace
			for (int i=0;i<backgroundEntities.size();i++) {
				GlobalEntity entity = (GlobalEntity) backgroundEntities.get(i);
				if(entity.y >= height)
					entity.y -= (int)(height/bg.getHeight()+2)*bg.getHeight();
				entity.setVerticalMovement(120);
				entity.move(delta);
			}
		}
		// cycle round drawing all the entities we have in the game
		for (int i=0;i<backgroundEntities.size();i++) {
			Entity entity = (Entity) backgroundEntities.get(i);
			entity.draw();
		}
		for (int i=0;i<entities.size();i++) {
			Entity entity = (Entity) entities.get(i);
			entity.draw();
		}

		// if we're waiting for an "any key" press then draw the 
		// current message 
		if (waitingForKeyPress) {
			message.draw(325,250);
		}


	}

	private void keyhandling() {

		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_F) {
					//getWindow().setDisplayMode(!Display.isFullscreen());
				}
				else if (Keyboard.getEventKey() == Keyboard.KEY_V) {
					vsync = !vsync;
					Display.setVSyncEnabled(vsync);
				}
				else if (Keyboard.getEventKey() == Keyboard.KEY_P && !pressEnter && !waitingForKeyPress) {
					pause = !pause;
				}
				else if (Keyboard.getEventKey() == Keyboard.KEY_RETURN) {
					if (pressEnter) {
						startGame();
						pressEnter = false;
					}
				}
			}
		}
		getWindow().renderText("Level: "+level, 370, 23);
		if(!pressEnter){
			if(pause) getWindow().renderText("Pause\nPress p to continue", 77, 33);
			else{ 
				boolean upPressed = getWindow().isKeyPressed(KeyEvent.VK_UP) && !isBlockedKey(KeyEvent.VK_UP);
				boolean downPressed = getWindow().isKeyPressed(KeyEvent.VK_DOWN) && !isBlockedKey(KeyEvent.VK_DOWN);
				boolean leftPressed = getWindow().isKeyPressed(KeyEvent.VK_LEFT) && !isBlockedKey(KeyEvent.VK_LEFT);
				boolean rightPressed = getWindow().isKeyPressed(KeyEvent.VK_RIGHT) && !isBlockedKey(KeyEvent.VK_RIGHT);
				boolean firePressed = getWindow().isKeyPressed(KeyEvent.VK_SPACE) || getWindow().isLMousePressed(MouseEvent.BUTTON1);
				boolean fire2Pressed = getWindow().isKeyPressed(KeyEvent.VK_SHIFT) || getWindow().isRMousePressed(MouseEvent.BUTTON1);

				if (!waitingForKeyPress) {
					if ((upPressed) && (!downPressed)) {
						ship.setVerticalMovement(-moveSpeed);
					} else if ((downPressed) && (!upPressed)) {
						ship.setVerticalMovement(moveSpeed);
					}
					if ((leftPressed) && (!rightPressed)) {
						ship.setHorizontalMovement(-moveSpeed);
					} else if ((rightPressed) && (!leftPressed)) {
						ship.setHorizontalMovement(moveSpeed);
					}

					if (!firePressed) {
						fireHasBeenReleased = true;
					}
					if (!fire2Pressed) {
						fire2HasBeenReleased = true;
					}

					// if we're pressing fire, attempt to fire
					if (firePressed && fireHasBeenReleased) {
						tryToFire();
						fireHasBeenReleased = false;
					}
					if (fire2Pressed && fire2HasBeenReleased) {
						tryToFire2();
						fire2HasBeenReleased = false;
					}
				}
				else if (!firePressed) {
					fireHasBeenReleased = true;
				}
				if ((firePressed) && (fireHasBeenReleased)) {
					waitingForKeyPress = false;
					fireHasBeenReleased = false;
					startGame();
				}
			}
		}
		// if escape has been pressed, stop the game
		if (getWindow().isKeyPressed(KeyEvent.VK_ESCAPE)) {
			System.exit(0);
		}

	}

	/**
	 * Notifcation that the game window has been closed
	 */
	public void windowClosed() {
		sm.destroy();
		System.exit(0);
	}

	/**
	 * The entry point into the game. We'll simply create an
	 * instance of class which will start the display and game
	 * loop.
	 * 
	 * @param argv The arguments that are passed into our game
	 */
	public static void main(String argv[]) {

		new Game(ResourceFactory.OPENGL_LWJGL);
		/*		int result = JOptionPane.showOptionDialog(null,"Java2D or OpenGL?","Java2D or OpenGL?",JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE,null,new String[] {"Java2D","JOGL","LWJGL"},null);

		if (result == 0) {
			new Game(ResourceFactory.JAVA2D);
		} else if (result == 1) {
			new Game(ResourceFactory.OPENGL_JOGL);
		} else if (result == 2) {
			new Game(ResourceFactory.OPENGL_LWJGL);
		}*/
	}

	public GameWindow getWindow() {
		return window;
	}

	public void setWindow(GameWindow window) {
		this.window = window;
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void addBlockedKey(int key) {
		blockedKeys.add(key);
	}

	public boolean isBlockedKey(int key) {
		return blockedKeys.contains(key);
	}
}