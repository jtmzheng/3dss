package input;

/**
 * Mouse motion event.
 * 
 * @author Adi
 */
public class MouseMoveEvent extends InputEvent {
	private int x, y, dx, dy;
	
	/**
	 * Constructs a MouseMove event.
	 * @param x x location of mouse.
	 * @param y y location of mouse.
	 * @param dx Change in x.
	 * @param dy Change in y.
	 */
	public MouseMoveEvent(int x, int y, int dx, int dy) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
	/**
	 * Get change in x
	 * @return change in x
	 */
	public int getdx() {
		return dx;
	}
	
	/**
	 * Get change in y
	 * @return change in y
	 */
	public int getdy() {
		return dy;
	}
	
	/**
	 * Returns current x position
	 * @return x position
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns current y position
	 * @return y position
	 */
	public int getY() {
		return y;
	}
}