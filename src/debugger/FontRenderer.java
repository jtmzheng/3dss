package debugger;

import java.awt.Font;
import java.io.InputStream;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.util.ResourceLoader;
 
public class FontRenderer {
 
	/** The fonts to draw to the screen */
	private static TrueTypeFont font;
	private static TrueTypeFont font2;
	
	/**
	 * Start the test 
	 */
	public static void init() {
		// load a default java font
		Font awtFont = new Font("Times New Roman", Font.BOLD, 24);
		font = new TrueTypeFont(awtFont, true);
		
		// load font from file
		try {
			InputStream inputStream	= ResourceLoader.getResourceAsStream("res/fonts/AppleGaramond-Bold.ttf");
			
			Font awtFont2 = Font.createFont(Font.TRUETYPE_FONT, inputStream);
			awtFont2 = awtFont2.deriveFont(24f); // set font size
			font2 = new TrueTypeFont(awtFont2, true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
	public static void draw() {
		Color.white.bind();
 
		font.drawString(100, 50, "THE LIGHTWEIGHT JAVA GAMES LIBRARY", Color.yellow);
		font2.drawString(100, 100, "NICE LOOKING FONTS!", Color.green);
	}
}