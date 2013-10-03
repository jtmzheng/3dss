package input;

/**
 * Abstract API for input devices.
 * This is applicable to mouse and keyboard.
 * 
 * @author Adi
 */
public interface Input {
	
	/**
	 * Any initialization code required for this device.
	 */
	public void initialize();
	
	/**
	 * Polls input and fires events.
	 */
	public void poll();
	
	/**
	 * Stops listening to updates from this device.
	 */
	public void destroy();
	
	/**
	 * Sets the object (which implements InputListener) that is
	 * listening to this input. In most cases, this will be 'Player'
	 * @param input The InputListener listening to updates from this Input.
	 */
	public void setListener(InputListener input);
}
