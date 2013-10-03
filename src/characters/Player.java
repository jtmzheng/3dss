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
	
	private HashMap<String, Integer> attributes = new HashMap<String, Integer>();
	private Camera playerCam;
	
	private float speed = 0.1f;
	
	// Movement flags.
	private boolean movingForward  = false;
	private boolean movingBackward = false;
	private boolean movingLeft     = false;
	private boolean movingRight    = false;
	
	public Player(Camera c) {
		this.playerCam = c;
		setup();
	}
	
	public void setup() {
		this.attributes.putAll(Settings.getDefaultPlayerAttributes());
	}

	private void strafeLeft(){
		playerCam.strafeLeft(speed);
	}
	
	private void strafeRight(){
		playerCam.strafeRight(speed);
	}
	
	private void moveForwards(){
		playerCam.moveForwards(speed);
	}
	
	private void moveBackwards(){
		playerCam.moveBackwards(speed);
	}
	
	public void move () {
		if (movingForward)  moveForwards();
		if (movingBackward) moveBackwards();
		if (movingLeft)     strafeLeft();
		if (movingRight)    strafeRight();
	}
	
	@Override
	public void onMouseClickedEvent(MouseClickEvent evt) {
		if (evt.isPress())
			System.out.println("BUTTON CLICKED: " + evt.getButtonType());
		else System.out.println("BUTTON RELEASED");
	}

	@Override
	public void onMouseMoveEvent(MouseMoveEvent evt) {	
		playerCam.rotateCamera(Display.getWidth()/2 - evt.getX(),
							   Display.getHeight()/2 - evt.getY());
	}

	@Override
	public void onKeyEvent(KeyEvent evt) {
		int code = evt.getKeyCode();
		boolean pressed = evt.isPress() ? true : false;
		
		if (code == Keyboard.KEY_W) {
			movingForward = pressed;
		} else if (code == Keyboard.KEY_A) {
			movingLeft = pressed;
		} else if (code == Keyboard.KEY_S) {
			movingBackward = pressed;
		} else if (code == Keyboard.KEY_D) {
			movingRight = pressed;
		}
	}
}
