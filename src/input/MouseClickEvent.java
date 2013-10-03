package input;

/**
 * Mouse click event.
 * 
 * @author Adi
 */
public class MouseClickEvent extends InputEvent{
	
	/**
	 * Determines which button is clicked.
	 */
	private int button;
	
	/**
	 * Determine whether the button was pressed or released.
	 */
	private boolean pressed;
	
	private int x, y;
	
	/**
	 * Creates a MouseClickEvent.
	 * 
	 * @param button Which button was pressed/released
	 * @param pressed Was it pressed or released
	 * @param x x location of event
	 * @param y y location of event
	 */
	public MouseClickEvent (int button, boolean pressed, int x, int y) {
		this.button = button;
		this.x = x;
		this.y = y;
		this.pressed = pressed;
	}
	
	/**
	 * Gets the button type.
	 * @return button's type
	 */
	public int getButtonType() {
		return this.button;
	}
	
	/**
	 * Determines whether its a press or release event.
	 * @return a boolean to determine whether it's a press or release.
	 */
	public boolean isPress () {
		return pressed;
	}
	/**
	 * Gets the X location of the event.
	 * @return x location of mouse click.
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Gets the y location of the event.
	 * @return y location of mouse click.
	 */
	public int getY() {
		return y;
	}
}
