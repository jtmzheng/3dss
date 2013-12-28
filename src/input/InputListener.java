package input;

/**
 * This is an interface for receiving input from devices.
 * Whatever implements this class can respond to clicks
 * and key presses, and respond accordingly.
 * 
 * @author Adi
 */
public interface InputListener {
	/**
	 * Listens to mouse click events.
	 * @param evt
	 */
	public void onMouseClickedEvent(MouseClickEvent evt);
	
	/**
	 * Listens to mouse move events.
	 * @param evt
	 */
	public void onMouseMoveEvent(MouseMoveEvent evt);
	
	/**
	 * Listens to key press/release events.
	 * @param evt
	 */
	public void onKeyEvent(KeyEvent evt);
}