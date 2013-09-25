package input;

/**
 * Mouse motion event.
 * 
 * @author Adi
 */
public class MouseMoveEvent extends InputEvent {
	
	private int x, y, dx, dy;
	
	public MouseMoveEvent(int x, int y, int dx, int dy) {
		this.x = x;
		this.y = y;
		this.dx = dx;
		this.dy = dy;
	}
	
	/**
	 * Get change in x
	 * @return dx
	 */
	public int getdx() {
		return dx;
	}
	
	/**
	 * Get change in y
	 * @return dy
	 */
	public int getdy() {
		return dy;
	}
	
	/**
	 * Returns current x position
	 * @return x
	 */
	public int getX() {
		return x;
	}
	
	/**
	 * Returns current y position
	 * @return y
	 */
	public int getY() {
		return y;
	}
}
