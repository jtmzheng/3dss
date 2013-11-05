package input;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;

/**
 * MouseInput class which sets up mouse and polls for input.
 * 
 * @author Adi
 */
public class MouseInput implements Input{
	/**
	 * Publicly exposed numbers to assign numbers to buttons.
	 */
	public static final int LEFT_BUTTON = 0;
	public static final int RIGHT_BUTTON = 1;
	public static final int MID_BUTTON = 2;
	
	/**
	 * Reference to the input listener that is listening to 
	 * changes to this device.
	 */
	private InputListener listener;
	
	public MouseInput () {}
	
	@Override
	public void initialize() {
		try {
			Mouse.create();
			Mouse.setGrabbed(true);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void poll() {
		while (Mouse.next()) {
			int button = Mouse.getEventButton();
			int x = Mouse.getX();
			int y = Mouse.getY();
			
			int dx = Mouse.getEventDX();
			int dy = Mouse.getEventDY();
			
			if (dx != 0 || dy != 0) {
				MouseMoveEvent evt = new MouseMoveEvent(x, y, dx, dy);
				evt.setTime(System.currentTimeMillis());
				listener.onMouseMoveEvent(evt);
			}
			if (button != -1 || Mouse.isButtonDown(0 | 1 | 2)) {
				MouseClickEvent evt = new MouseClickEvent(button, Mouse.getEventButtonState(), x, y);
				evt.setTime(System.currentTimeMillis());
				listener.onMouseClickedEvent(evt);
			}
		}
		
		Mouse.setCursorPosition(300, 300);
	}

	@Override
	public void destroy() {
		Mouse.destroy();
	}

	@Override
	public void setListener(InputListener input) {
		this.listener = input;
	}
}
