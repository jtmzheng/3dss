package input;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;

/**
 * Keyboard input class which sets up keyboard and polls for input.
 * 
 * @author Adi
 */
public class KeyInput implements Input{
	/**
	 * Reference to the input listener that is listening to 
	 * changes to this device.
	 */
	private InputListener listener;
	
	public KeyInput () {}
	
	@Override
	public void initialize() {
        try {
            Keyboard.create();
            Keyboard.enableRepeatEvents(true);
        } catch (LWJGLException e) {
        	e.printStackTrace();
        }		
	}

	@Override
	public void poll() {
		while (Keyboard.next()) {
			int code = Keyboard.getEventKey();
			boolean pressed = Keyboard.getEventKeyState();
			
			KeyEvent evt = new KeyEvent(code, pressed);
			evt.setTime(System.currentTimeMillis());
			listener.onKeyEvent(evt);
		}
	}

	@Override
	public void destroy() {
		Keyboard.destroy();		
	}

	@Override
	public void setListener(InputListener input) {
		this.listener = input;
	}
}
