package threed.space.shooter;

import java.awt.DisplayMode;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

/**
 * NeHe Lesson 05 - 3D Shapes
 * 
 * @author Tim Biedert
 * @author kappaOne
 */
public class Test {

	String windowTitle = "NeHe Lesson 05 - 3D Shapes";
	public boolean closeRequested = false;

	long lastFrameTime; // used to calculate delta
	
	float triangleAngle; // Angle of rotation for the triangles
	float quadAngle; // Angle of rotation for the quads

	public void run() {

		createWindow();
		getDelta(); // Initialise delta timer
		initGL();
		
		while (!closeRequested) {
			pollInput();
			updateLogic(getDelta());
			renderGL();

			Display.update();
		}
		
		cleanup();
	}
	
	private void initGL() {

		/* OpenGL */
		int width = Display.getDisplayMode().getWidth();
		int height = Display.getDisplayMode().getHeight();

		GL11.glViewport(0, 0, width, height); // Reset The Current Viewport
		GL11.glMatrixMode(GL11.GL_PROJECTION); // Select The Projection Matrix
		GL11.glLoadIdentity(); // Reset The Projection Matrix
		GLU.gluPerspective(45.0f, ((float) width / (float) height), 0.1f, 100.0f); // Calculate The Aspect Ratio Of The Window
		GL11.glMatrixMode(GL11.GL_MODELVIEW); // Select The Modelview Matrix
		GL11.glLoadIdentity(); // Reset The Modelview Matrix

		GL11.glShadeModel(GL11.GL_SMOOTH); // Enables Smooth Shading
		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
		GL11.glClearDepth(1.0f); // Depth Buffer Setup
		GL11.glEnable(GL11.GL_DEPTH_TEST); // Enables Depth Testing
		GL11.glDepthFunc(GL11.GL_LEQUAL); // The Type Of Depth Test To Do
		GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST); // Really Nice Perspective Calculations

	}
	
	private void updateLogic(int delta) {
		triangleAngle += 0.1f * delta; // Increase The Rotation Variable For The Triangles
		quadAngle -= 0.05f * delta; // Decrease The Rotation Variable For The Quads
	}


	private void renderGL() {

		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer
		GL11.glLoadIdentity(); // Reset The View
		GL11.glTranslatef(-1.5f, 0.0f, -6.0f); // Move Left And Into The Screen

		GL11.glRotatef(triangleAngle, 0.0f, 1.0f, 0.0f); // Rotate The Pyramid On It's Y Axis

		GL11.glBegin(GL11.GL_TRIANGLES); // Start Drawing The Pyramid
		GL11.glColor3f(1.0f, 0.0f, 0.0f); // Red
		GL11.glVertex3f(0.0f, 1.0f, 0.0f); // Top Of Triangle (Front)
		GL11.glColor3f(0.0f, 1.0f, 0.0f); // Green
		GL11.glVertex3f(-1.0f, -1.0f, 1.0f); // Left Of Triangle (Front)
		GL11.glColor3f(0.0f, 0.0f, 1.0f); // Blue
		GL11.glVertex3f(1.0f, -1.0f, 1.0f); // Right Of Triangle (Front)

		GL11.glColor3f(1.0f, 0.0f, 0.0f); // Red
		GL11.glVertex3f(0.0f, 1.0f, 0.0f); // Top Of Triangle (Right)
		GL11.glColor3f(0.0f, 0.0f, 1.0f); // Blue
		GL11.glVertex3f(1.0f, -1.0f, 1.0f); // Left Of Triangle (Right)
		GL11.glColor3f(0.0f, 1.0f, 0.0f); // Green
		GL11.glVertex3f(1.0f, -1.0f, -1.0f); // Right Of Triangle (Right)

		GL11.glColor3f(1.0f, 0.0f, 0.0f); // Red
		GL11.glVertex3f(0.0f, 1.0f, 0.0f); // Top Of Triangle (Back)
		GL11.glColor3f(0.0f, 1.0f, 0.0f); // Green
		GL11.glVertex3f(1.0f, -1.0f, -1.0f); // Left Of Triangle (Back)
		GL11.glColor3f(0.0f, 0.0f, 1.0f); // Blue
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f); // Right Of Triangle (Back)

		GL11.glColor3f(1.0f, 0.0f, 0.0f); // Red
		GL11.glVertex3f(0.0f, 1.0f, 0.0f); // Top Of Triangle (Left)
		GL11.glColor3f(0.0f, 0.0f, 1.0f); // Blue
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f); // Left Of Triangle (Left)
		GL11.glColor3f(0.0f, 1.0f, 0.0f); // Green
		GL11.glVertex3f(-1.0f, -1.0f, 1.0f); // Right Of Triangle (Left)
		GL11.glEnd(); // Done Drawing The Pyramid

		GL11.glLoadIdentity(); // Reset The View
		GL11.glTranslatef(1.5f, 0.0f, -7.0f); // Move Right And Into The Screen

		GL11.glRotatef(quadAngle, 1.0f, 1.0f, 1.0f); // Rotate The Cube On X, Y & Z

		GL11.glBegin(GL11.GL_QUADS); // Start Drawing The Cube
		GL11.glColor3f(0.0f, 1.0f, 0.0f); // Set The Color To Green
		GL11.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Top)
		GL11.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Top)
		GL11.glVertex3f(-1.0f, 1.0f, 1.0f); // Bottom Left Of The Quad (Top)
		GL11.glVertex3f(1.0f, 1.0f, 1.0f); // Bottom Right Of The Quad (Top)

		GL11.glColor3f(1.0f, 0.5f, 0.0f); // Set The Color To Orange
		GL11.glVertex3f(1.0f, -1.0f, 1.0f); // Top Right Of The Quad (Bottom)
		GL11.glVertex3f(-1.0f, -1.0f, 1.0f); // Top Left Of The Quad (Bottom)
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Bottom)
		GL11.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Bottom)

		GL11.glColor3f(1.0f, 0.0f, 0.0f); // Set The Color To Red
		GL11.glVertex3f(1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Front)
		GL11.glVertex3f(-1.0f, 1.0f, 1.0f); // Top Left Of The Quad (Front)
		GL11.glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad (Front)
		GL11.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Right Of The Quad (Front)

		GL11.glColor3f(1.0f, 1.0f, 0.0f); // Set The Color To Yellow
		GL11.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Back)
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Back)
		GL11.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Back)
		GL11.glVertex3f(1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Back)

		GL11.glColor3f(0.0f, 0.0f, 1.0f); // Set The Color To Blue
		GL11.glVertex3f(-1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Left)
		GL11.glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Left)
		GL11.glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Left)
		GL11.glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Right Of The Quad (Left)

		GL11.glColor3f(1.0f, 0.0f, 1.0f); // Set The Color To Violet
		GL11.glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Right)
		GL11.glVertex3f(1.0f, 1.0f, 1.0f); // Top Left Of The Quad (Right)
		GL11.glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad (Right)
		GL11.glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Right)
		GL11.glEnd(); // Done Drawing The Quad

	}

	/**
	 * Poll Input
	 */
	public void pollInput() {

		// scroll through key events
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
					closeRequested = true;
			}
		}

		if (Display.isCloseRequested()) {
			closeRequested = true;
		}
	}
	
	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta() {
	    long time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
	    int delta = (int) (time - lastFrameTime);
	    lastFrameTime = time;
	 
	    return delta;
	}

	private void createWindow() {
		try {
			Display.setDisplayMode(new DisplayMode(640, 480));
			Display.setVSyncEnabled(true);
			Display.setTitle(windowTitle);
			Display.create();
		} catch (LWJGLException e) {
			Sys.alert("Error", "Initialization failed!\n\n" + e.getMessage());
			System.exit(0);
		}
	}
	
	/**
	 * Destroy and clean up resources
	 */
	private void cleanup() {
		Display.destroy();
	}
	
	public static void main(String[] args) {
		Test lesson = new Test();
		lesson.run();
	}
}
