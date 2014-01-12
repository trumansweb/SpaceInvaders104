package org.newdawn.spaceinvaders.lwjgl;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.spaceinvaders.GameWindow;
import org.newdawn.spaceinvaders.GameWindowCallback;

/**
 * An implementation of GameWindow that will use OPENGL (JOGL) to 
 * render the scene. Its also responsible for monitoring the keyboard
 * using AWT.
 * 
 * @author Kevin Glass
 * @author Brian Matzon
 */
public class LWJGLGameWindow implements GameWindow {

	/** The callback which should be notified of window events */
	private GameWindowCallback callback;

	/** True if the game is currently "running", i.e. the game loop is looping */
	private boolean gameRunning = true;

	/** The width of the game display area */
	private int width;

	/** The height of the game display area */
	private int height;

	/** The loader responsible for converting images into OpenGL textures */
	private TextureLoader textureLoader;

	/** Title of window, we get it before our window is ready, so store it till needed */
	@SuppressWarnings("unused")
	private String title;

	private boolean intro;

	/**
	 * Create a new game window that will use OpenGL to 
	 * render our game.
	 */
	public LWJGLGameWindow() {
	}

	/**
	 * Retrieve access to the texture loader that converts images
	 * into OpenGL textures. Note, this has been made package level
	 * since only other parts of the JOGL implementations need to access
	 * it.
	 * 
	 * @return The texture loader that can be used to load images into
	 * OpenGL textures.
	 */
	TextureLoader getTextureLoader() {
		return textureLoader;
	}

	/**
	 * Set the title of this window.
	 *
	 * @param title The title to set on this window
	 */
	public void setTitle(String title) {
		this.title = title;
		if(Display.isCreated()) {
			Display.setTitle(title);
		}
	}

	/**
	 * Set the resolution of the game display area.
	 *
	 * @param x The width of the game display area
	 * @param y The height of the game display area
	 */
	public void setResolution(int x, int y) {
		width = x;
		height = y;
	}

	/**
	 * Set the display mode to be used
	 *
	 * @param width The width of the display required
	 * @param height The height of the display required
	 * @param fullscreen True if we want fullscreen mode
	 */
	public void setDisplayMode(boolean fullscreen) {

		// return if requested DisplayMode is already set
		if ((Display.getDisplayMode().getWidth() == width) &&
				(Display.getDisplayMode().getHeight() == height) &&
				(Display.isFullscreen() == fullscreen)) {
			return;
		}
		try {
			DisplayMode targetDisplayMode = null;

			if (fullscreen) {
				DisplayMode[] modes = Display.getAvailableDisplayModes();
				int freq = 0;

				for (int i=0;i<modes.length;i++) {
					DisplayMode current = modes[i];

					if ((current.getWidth() == width) && (current.getHeight() == height)) {
						if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
							if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
								targetDisplayMode = current;
								freq = targetDisplayMode.getFrequency();
							}
						}

						// if we've found a match for bpp and frequence against the
						// original display mode then it's probably best to go for this one
						// since it's most likely compatible with the monitor
						if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) &&
								(current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
							targetDisplayMode = current;
							break;
						}
					}
				}
			} else {
				targetDisplayMode = new DisplayMode(width,height);
			}

			if (targetDisplayMode == null) {
				System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
				return;
			}

			Display.setDisplayMode(targetDisplayMode);
			Display.setFullscreen(fullscreen);

		} catch (LWJGLException e) {
			System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
		}
	}

	/*	*//**
	 * Sets the display mode for fullscreen mode
	 *//*
	private boolean setDisplayMode(boolean fullscreen) {
		try {
			// get modes
			DisplayMode[] dm = org.lwjgl.util.Display.getAvailableDisplayModes(
					width, height, -1, -1, -1, -1, 60, 60);

			org.lwjgl.util.Display.setDisplayMode(dm, new String[] {
					"width=" + width,
					"height=" + height,
					"freq=" + 60,
					"bpp="
							+ org.lwjgl.opengl.Display.getDisplayMode()
									.getBitsPerPixel() });
			Display.setFullscreen(fullscreen);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("Unable to enter fullscreen, continuing in windowed mode");
		}

		return false;
	}*/

	/**
	 * Start the rendering process. This method will cause the display to redraw
	 * as fast as possible.
	 */
	public void startRendering() {
		try
		{
			// width and height of window and view port

			// set up window and open it
			Display.setDisplayMode(new DisplayMode(width, height));

			// limit video card to refresh rate of screen (60 Hertz on most LCDs)
			Display.setVSyncEnabled(true);

			// set windows title
			Display.setTitle(title);

			// set up OpenGL to run in forward-compatible mode
			// so that using deprecated functionality will
			// throw an error. This ensures that we are making
			// use of OpenGL 
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAtrributes = new ContextAttribs(4, 0);
			contextAtrributes.withForwardCompatible(true);
			contextAtrributes.withProfileCore(true);
			Display.create(pixelFormat, contextAtrributes);

			// initialize basic OpenGL stuff
			GL11.glViewport(0, 0, Display.getDisplayMode().getWidth(), Display.getDisplayMode().getHeight());
			GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		}
		catch( LWJGLException lwjglException )
		{
			throw new RuntimeException("could not initiate LWJGL", lwjglException);
		}
		// grab the mouse, dont want that hideous cursor when we're playing!
		Mouse.setGrabbed(true);

		// enable textures since we're going to use these for our sprites
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		// disable the OpenGL depth test since we're rendering 2D graphics 
		GL11.glDisable(GL11.GL_DEPTH_TEST);

		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();

		GL11.glOrtho(0, width, height, 0, -1, 1);

		/*			Canvas Canvas = new Canvas();
	        JFrame Frame = new JFrame(title);

	        Frame.setSize(width, height);
	        Frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        Frame.setBackground(Color.BLACK);
	        Frame.setUndecorated(true);
	        Canvas.setBackground(Color.BLACK);
	        Frame.getContentPane().add(Canvas);
	        Frame.setLocationRelativeTo(null);
	        Frame.setVisible(true);

	        Display.setParent(Canvas);*/
		textureLoader = new TextureLoader();

		if(callback != null) {
			callback.initialise();
		}

	gameLoop();
}

/**
 * Register a callback that will be notified of game window
 * events.
 *
 * @param callback The callback that should be notified of game
 * window events. 
 */
public void setGameWindowCallback(GameWindowCallback callback) {
	this.callback = callback;
}

/**
 * Check if a particular key is current pressed.
 *
 * @param keyCode The code associated with the key to check 
 * @return True if the specified key is pressed
 */
public boolean isKeyPressed(int keyCode) {
	// apparently, someone at decided not to use standard 
	// keycode, so we have to map them over:
	switch(keyCode) {
	case KeyEvent.VK_SPACE:
		keyCode = Keyboard.KEY_SPACE;
		break;
	case KeyEvent.VK_UP:
		keyCode = Keyboard.KEY_UP;
		break;
	case KeyEvent.VK_DOWN:
		keyCode = Keyboard.KEY_DOWN;
		break;
	case KeyEvent.VK_LEFT:
		keyCode = Keyboard.KEY_LEFT;
		break;
	case KeyEvent.VK_RIGHT:
		keyCode = Keyboard.KEY_RIGHT;
		break;
	case KeyEvent.VK_SHIFT:
		keyCode = Keyboard.KEY_LSHIFT;
		break;
	case KeyEvent.VK_P:
		keyCode = Keyboard.KEY_P;
		break;
	}    

	return org.lwjgl.input.Keyboard.isKeyDown(keyCode);
}	
/**
 * Check if a particular key is current pressed.
 *
 * @param keyCode The code associated with the key to check 
 * @return True if the specified key is pressed
 */
public boolean isLMousePressed(int mouseCode) {
	switch(mouseCode) {
	case MouseEvent.BUTTON1:
		mouseCode = 0;
		break;
	}
	return org.lwjgl.input.Mouse.isButtonDown(mouseCode);
}

public boolean isRMousePressed(int mouseCode) {
	switch(mouseCode) {
	case MouseEvent.BUTTON2:
		mouseCode = 1;
		break;
	}
	return org.lwjgl.input.Mouse.isButtonDown(mouseCode);
}

public void renderText(String s, int x, int y) {
	GL11.glDisable(GL11.GL_TEXTURE_2D);
	SimpleText.drawString(s, x, y);
	GL11.glEnable(GL11.GL_TEXTURE_2D);
}

/**
 * Run the main game loop. This method keeps rendering the scene
 * and requesting that the callback update its screen.
 */
private void gameLoop() {
	while (gameRunning) {
		// clear screen
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		/*			
			if (!intro) {
				Intro();
				//SystemTimer.sleep(3000);
				//intro = true;
			}
			else */if (callback != null) {
				callback.frameRendering();
			}

			Display.update();

			if(Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				gameRunning = false;
				Display.destroy();
				callback.windowClosed();
			}
			
	}
}

@SuppressWarnings("unused")
private void Intro() {
	SimpleText.drawString("Space Invaders 1.04.1", width/2, height/2);		
}

public boolean isIntro() {
	return intro;
}

public void setIntro(boolean intro) {
	this.intro = intro;
}
}