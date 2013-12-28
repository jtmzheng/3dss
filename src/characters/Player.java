package characters;

import input.InputListener;
import input.KeyEvent;
import input.MouseClickEvent;
import input.MouseMoveEvent;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import renderer.Camera;
import renderer.Light;
import renderer.LightHandle;
import renderer.LightManager;
import renderer.Model;
import system.Settings;
import event.PubSubListener;
import event.PublishEventType;
import event.Publisher;

/**
 * Player class which contains a camera, model, light
 * weapons, attributes, and other things.
 * 
 * The player implements InputListener, and responds to events from raw inputs.
 * See Main.java and the "input" package to see how the binding works.
 * 
 * @author Adi
 * @author Max
 */
public class Player implements InputListener {
	// Camera object that the player uses.
	private Camera playerCam;
	private Model playerModel;
	
	//Light parameters
	private Vector3f mLd;
	private Vector3f mLs;
	private Vector3f mLa;
	
	// Light associated with the camera
	private LightHandle cameraLight;
	private LightManager lightManager = null; 

	// Movement fields.
	private float speed_x = 0.0f;
	private float speed_y = 0.0f;

	private boolean enableAcceleration;
	private float acceleration = 100f;
	private float MAX_SPEED = 1700f;
	private float drag = 0.001f;

	// Key press flags.
	private boolean wPress = false;
	private boolean aPress = false;
	private boolean sPress = false;
	private boolean dPress = false;
	
	// Player name.
	private String name = "Jun Tao";

	/**
	 * Constructs a Player with a Camera.
	 * @param c The Camera object that abstracts out the view matrix logic.
	 */
	public Player(Camera c, Model m) {
		playerCam = c;
		playerModel = m;
		setup();
	}

	/**
	 * Sets up all necessary player attributes and listeners.
	 */
	private void setup() {
		// Setup the player light (spotlight)
		mLs = new Vector3f(1.0f, 1.0f, 1.0f);
		mLd = new Vector3f(0.7f, 0.7f, 0.7f);
		mLa = new Vector3f(0.2f, 0.2f, 0.2f);
		cameraLight = new LightHandle(this, new Light(new Vector3f(playerCam.getLocation()), 
				new Vector3f(mLs), 
				new Vector3f(mLd), 
				new Vector3f(mLa), 
				new Vector3f(playerCam.getDirection())));
		
		lightManager = LightManager.getLightManagerHandle();

		enableAcceleration = Settings.getBoolean("playerAcceleration");
		
		// The player model should not be rendererd
		playerModel.setRenderFlag(false);
		
		// Subscribe the enemy death listener to the "enemy death" event.
		Publisher.getInstance().bindSubscriber(new EnemyDeathListener(), PublishEventType.ENEMY_DEATH);
	}

	/**
	 * Moves the player.
	 * This should be called in the game loop.
	 */
	public void move () {
		if (enableAcceleration) {
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
		} else {		
			speed_x = 0;
			speed_y = 0;
			
			if (wPress) speed_y = MAX_SPEED;
			if (aPress) speed_x = -MAX_SPEED;
			if (sPress) speed_y = -MAX_SPEED;
			if (dPress) speed_x = MAX_SPEED;
		}
		
		// Set the camera location to the current model origin
		javax.vecmath.Vector3f oldPosition = playerModel.getModelOrigin();
		playerCam.setLocation(new Vector3f(oldPosition.x, oldPosition.y, oldPosition.z));

		// Update the camera light fields
		if(cameraLight.isValid()) {
			Light light = cameraLight.getLight();
			light.setPosition(new Vector3f(playerCam.getLocation()));
			light.setDirection(new Vector3f(playerCam.getDirection()));
		}
	
		lightManager.updateAllLights();
		
		// Update the player related physics (apply forces)	
		updatePhysics();
	}

	/**
	 * This event handler fires whenever a mouse button is clicked.
	 * @param evt A MouseClickEvent object.
	 */
	@Override
	public void onMouseClickedEvent(MouseClickEvent evt) {
		// Stub
	}

	/**
	 * This event handler fires whenever the mouse is moved.
	 * @param evt A MouseMoveEvent object.
	 */
	@Override
	public void onMouseMoveEvent(MouseMoveEvent evt) {	
		playerCam.rotateCamera(evt.getdx(), evt.getdy());
		
		// Update the camera light fields
		if(cameraLight.isValid()) {
			Light light = cameraLight.getLight();
			light.setPosition(new Vector3f(playerCam.getLocation()));
			light.setDirection(new Vector3f(playerCam.getDirection()));
		}
		
		lightManager.updateAllLights();
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

		// Process the key event
		switch (code) {
		case Keyboard.KEY_W: {
			wPress = pressed;
			break;
		}
		case Keyboard.KEY_A: {
			aPress = pressed;
			break;
		}
		case Keyboard.KEY_S: {
			sPress = pressed;
			break;
		}
		case Keyboard.KEY_D: {
			dPress = pressed;
			break;
		}
		case Keyboard.KEY_F: {
			if (pressed) {
				triggerLight();
			}
		}
		}
	}

	/**
	 * Get the player model 
	 * @return playerModel model used to represent the player
	 */
	public Model getModel() {
		return playerModel;
	}
	
	/**
	 * Strafes the player (uses playerCam).
	 * @deprecated
	 */
	private void strafe(){
		playerCam.strafe(speed_x);
	}

	/**
	 * Moves the player forward and backwards (uses playerCam).
	 * @deprecated
	 */
	private void moveFrontBack(){
		playerCam.moveFrontBack(speed_y);
	}
	
	/**
	 * Update the player's model physics using JBullet
	 */
	private void updatePhysics() {
		// Reset the forces and the velocity
		playerModel.resetModelForces();
		playerModel.resetModelKinematics();
		
		javax.vecmath.Vector3f forceRight = new javax.vecmath.Vector3f(speed_x * playerCam.getRight().x,
				speed_x * playerCam.getRight().y,
				speed_x * playerCam.getRight().z);
		javax.vecmath.Vector3f forceDirection = new javax.vecmath.Vector3f(speed_y * playerCam.getDirection().x,
				speed_y * playerCam.getDirection().y,
				speed_y * playerCam.getDirection().z);

		forceDirection.add(forceRight);
		playerModel.getPhysicsModel().applyForce(forceDirection);
	}
	
	/**
	 * Light is triggered to opposite of current state
	 */
	private void triggerLight() {
		if(cameraLight.isValid()) {
			cameraLight.invalidate();
		} else {
			cameraLight.reset(new Light(new Vector3f(playerCam.getLocation()), 
					new Vector3f(mLs), 
					new Vector3f(mLd), 
					new Vector3f(mLa), 
					new Vector3f(playerCam.getDirection())));
		}
		lightManager.updateAllLights();
	}

	private class EnemyDeathListener implements PubSubListener {
		@Override
		public void handleEvent() {
			System.out.println("Congrats, " + name + ". You killed an enemy!");
		}
	}
}