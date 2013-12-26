package characters;

import javax.vecmath.Quat4f;

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

import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.linearmath.Transform;

import event.PubSubListener;
import event.PublishEventType;
import event.Publisher;

/**
 * Player class which contains a camera,
 * weapons, attributes, and other things.
 * 
 * The player implements InputListener, and responds to events from raw inputs.
 * See Main.java and the "input" package to see how the binding works.
 * TODO: Character will eventually extend model
 * 
 * @author Adi
 */
public class Player implements InputListener {
	// Camera object that the player uses.
	private Camera playerCam;
	private Model playerModel;
	
	//Light parameters
	private Vector3f m_Ld;
	private Vector3f m_Ls;
	private Vector3f m_La;
	
	// Light associated with the camera
	private LightHandle cameraLight;
	private LightManager lightManager = null; 
	
	// Player attributes
	private float shields = 100f;
	private float HP = 100F;

	// Movement fields.
	private float speed_x = 0.0f;
	private float speed_y = 0.0f;

	private boolean enableAcceleration;
	private float acceleration = 0.01f;
	private float MAX_SPEED = 0.17f;
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
	public Player(Camera c, 
			Model m) {
		playerCam = c;
		playerModel = m;
		setup();
	}

	/**
	 * Sets up all necessary player attributes and listeners.
	 */
	public void setup() {
		// Setup the player light (spotlight)
		m_Ls = new Vector3f(1.0f, 1.0f, 1.0f);
		m_Ld = new Vector3f(0.7f, 0.7f, 0.7f);
		m_La = new Vector3f(0.2f, 0.2f, 0.2f);
		cameraLight = new LightHandle(this, new Light(new Vector3f(playerCam.getLocation()), 
				new Vector3f(m_Ls), 
				new Vector3f(m_Ld), 
				new Vector3f(m_La), 
				new Vector3f(playerCam.getDirection())));
		
		lightManager = LightManager.getLightManagerHandle();

		enableAcceleration = Settings.getBoolean("playerAcceleration");
		
		// Subscribe the enemy death listener to the "enemy death" event.
		Publisher.getInstance().bindSubscriber(new EnemyDeathListener(), PublishEventType.ENEMY_DEATH);
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
		
		// Move player
		strafe();
		moveFrontBack();

		// Update the camera light fields
		if(cameraLight.isValid()) {
			Light light = cameraLight.getLight();
			light.setPosition(new Vector3f(playerCam.getLocation()));
			light.setDirection(new Vector3f(playerCam.getDirection()));
		}
	
		lightManager.updateAllLights();
		
		// Update the physics model
		RigidBody playerRigidBody = playerModel.getPhysicsModel().getRigidBody();
		Vector3f position = playerCam.getLocation();
		playerRigidBody.setWorldTransform(new Transform(new javax.vecmath.Matrix4f(new Quat4f(0, 0, 0, 1), 
        		new javax.vecmath.Vector3f(position.x, position.y, position.z), 
        		1)));
		
	}

	/**
	 * This event handler fires whenever a mouse button is clicked.
	 * @param evt A MouseClickEvent object.
	 */
	@Override
	public void onMouseClickedEvent(MouseClickEvent evt) {
		// Turn the camera light on and off 
		if (evt.isPress()) {
			if(cameraLight.isValid()) {
				cameraLight.invalidate();
			}
			else {
				cameraLight.reset(new Light(new Vector3f(playerCam.getLocation()), 
						new Vector3f(m_Ls), 
						new Vector3f(m_Ld), 
						new Vector3f(m_La), 
						new Vector3f(playerCam.getDirection())));
			}
			lightManager.updateAllLights();
		}
		else {}
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

		// Set the appropriate movement flag.
		if (code == Keyboard.KEY_W) wPress = pressed;
		if (code == Keyboard.KEY_A) aPress = pressed;
		if (code == Keyboard.KEY_S) sPress = pressed;
		if (code == Keyboard.KEY_D) dPress = pressed;
	}
	
	/**
	 * Get the player model 
	 * @return
	 */
	public Model getModel() {
		return playerModel;
	}

	private class EnemyDeathListener implements PubSubListener {
		@Override
		public void handleEvent() {
			System.out.println("Congrats, " + name + ". You killed an enemy!");
		}
	}
	
}
