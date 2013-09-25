package characters;

import input.InputListener;
import input.KeyPressEvent;
import input.MouseClickEvent;
import input.MouseMoveEvent;

import java.util.HashMap;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

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
	
	public Player(Camera c) {
		this.playerCam = c;
		setup();
	}
	
	public void setup() {
		this.attributes.putAll(Settings.getDefaultPlayerAttributes());
	}

	public void strafeLeft(){
		playerCam.strafeLeft(speed);
	}
	
	public void strafeRight(){
		playerCam.strafeRight(speed);
	}
	
	public void moveForwards(){
		playerCam.moveForwards(speed);
	}
	
	public void moveBackwards(){
		playerCam.moveBackwards(speed);
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
	public void onKeyPressEvent(KeyPressEvent evt) {
		if (evt.isPress()) {
			char c = evt.getCharacter();
			if (c == 'w') {
				moveForwards();
			} else if (c == 'a') {
				strafeLeft();
			} else if (c == 'd') {
				strafeRight();
			} else if (c == 's') {
				moveBackwards();
			}
		}
	}
}
