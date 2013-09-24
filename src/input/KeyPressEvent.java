package input;

/**
 * Event representing key presses.
 * 
 * @author Adi
 */
public class KeyPressEvent extends InputEvent {

	/**
	 * Keycode representing the key pressed.
	 */
	private int keyCode;
	
	/**
	 * Character pressed on the keyboard.
	 */
	private char character;
	
	/**
	 * Determines whether this was a repeat event (from holding down the key).
	 */
	private boolean repeat;
	
	/**
	 * Determines whether this was a press/release event.
	 */
	private boolean press;
	
	public KeyPressEvent(int code, char charPressed, boolean press, boolean holding) {
		this.keyCode = code;
		this.character = charPressed;
		this.repeat = holding;
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
	 * Gets the character pressed.
	 * @return char
	 */
	public char getCharacter() {
		return character;
	}
	
	/**
	 * Gets if the event is a repeat.
	 * @return boolean
	 */
	public boolean isRepeat() {
		return repeat;
	}
	
	/**
	 * Gets if event is a press or release.
	 */
	public boolean isPress() {
		return press;
	}
}
