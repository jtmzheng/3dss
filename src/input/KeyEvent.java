package input;

/**
 * Event representing key presses.
 * 
 * @author Adi
 */
public class KeyEvent extends InputEvent {

	/**
	 * Keycode representing the key pressed.
	 */
	private int keyCode;

	/**
	 * Determines whether this was a press or a release.
	 */
	private boolean press;
	
	/**
	 * 
	 * @param code The keycode.
	 * @param charPressed The character which was pressed.
	 * @param press If the key event is a key press or release.
	 */
	public KeyEvent(int code, boolean press) {
		this.keyCode = code;
		this.press = press;
	}
	
	/**
	 * Gets the keycode.
	 * @return code
	 */
	public int getKeyCode() {
		return keyCode;
	}
	
	/**
	 * Gets if this is a press or a release event.
	 * @return boolean
	 */
	public boolean isPress () {
		return press;
	}
}
