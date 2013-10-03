package characters;

import input.InputListener;
import input.KeyEvent;
import input.MouseClickEvent;
import input.MouseMoveEvent;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import renderer.Camera;
import system.Settings;

/**
 * Player class which contains a camera,
 * weapons, attributes, and other things.
 * 
 * The player implements InputListener, and responds to events from raw inputs.
 * See Main.java and the "input" package to see how the binding works.
 * 
 * @author Adi
 */
public class Player implements InputListener {
	
	// Default player attributes.
	private HashMap<String, Float> attributes = new HashMap<String, Float>();
	
	// Camera object that the player uses.
	private Camera playerCam;
	
	// Movement fields.
	// TODO: once these are finalized, move to defaultPlayerAttributes.
	private float speed_x = 0.0f;
	private float speed_y = 0.0f;

	private float acceleration = 0.01f;
	private float MAX_SPEED = 0.17f;
	private float drag = 0.001f;

	// Key press flags.
	private boolean wPress = false;
	private boolean aPress = false;
	private boolean sPress = false;
	private boolean dPress = false;
	
	/**
	 * Constructs a Player with a Camera.
	 * @param c The Camera object that abstracts out the view matrix logic.
	 */
	public Player(Camera c) {
		this.playerCam = c;
		setup();
	}
	
	/**
	 * Sets up all necessary player attributes.
	 */
	public void setup() {
		this.attributes.putAll(Settings.getDefaultPlayerAttributes());
	}

	/**
	 * Strafes the player (uses playerCam).
	 */
	private void strafe(){
		playerCam.strafe(speed_x);
	}
	
	/**
	 * Moves the player forward and backwards (uses playerCam).
	 */
	private void moveFrontBack(){
		playerCam.moveFrontBack(speed_y);
	}
	
	/**
	 * Moves the player.
	 * This should be called in the game loop.
	 */
	public void move () {
		// Apply movement from key presses.
		if (wPress && speed_y < MAX_SPEED)  speed_y += acceleration;
		if (aPress && speed_x > -MAX_SPEED) speed_x -= acceleration;
		if (sPress && speed_y > -MAX_SPEED) speed_y -= acceleration;
		if (dPress && speed_x < MAX_SPEED)  speed_x += acceleration;
				
		// Apply drag.
		if (speed_x > 0) speed_x -= drag;
		if (speed_x < 0) speed_x += drag;
		if (speed_y > 0) speed_y -= drag;
		if (speed_y < 0) speed_y += drag;
		
		// Move our player.
		strafe();
		moveFrontBack();
	}
	
	/**
	 * This event handler fires whenever a mouse button is clicked.
	 * @param evt A MouseClickEvent object.
	 */
	@Override
	public void onMouseClickedEvent(MouseClickEvent evt) {
		if (evt.isPress())
			System.out.println("BUTTON CLICKED: " + evt.getButtonType());
		else System.out.println("BUTTON RELEASED");
	}

	/**
	 * This event handler fires whenever the mouse is moved.
	 * @param evt A MouseMoveEvent object.
	 */
	@Override
	public void onMouseMoveEvent(MouseMoveEvent evt) {	
		playerCam.rotateCamera(Display.getWidth()/2 - evt.getX(),
							   Display.getHeight()/2 - evt.getY());
	}

	/**
	 * This event handler fires whenever a key event is triggered.
	 * Key events are triggered on press of a key and on release of a key.
	 * @param evt A KeyEvent object.
	 */
	@Override
	public void onKeyEvent(KeyEvent evt) {
		int code = evt.getKeyCode();
		
		// Determine whether this is a press or a release event.
		boolean pressed = evt.isPress() ? true : false;
		
		// Set the appropriate movement flag.
		if (code == Keyboard.KEY_W) wPress = pressed;
		if (code == Keyboard.KEY_A) aPress = pressed;
		if (code == Keyboard.KEY_S) sPress = pressed;
		if (code == Keyboard.KEY_D) dPress = pressed;
	}
}
