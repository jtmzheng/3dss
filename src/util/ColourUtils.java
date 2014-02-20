package util;

import org.lwjgl.util.vector.Vector3f;

/**
 * Utility functions for colours.
 */
public class ColourUtils {
	/**
	 * Encode a number into a colour
	 * @param num Number to encode (int)
	 * @return 
	 */
	public static Vector3f encodeColour(int num) {
		int r = (num >> 16) & 0xFF;
		int g = (num >> 8) & 0xFF;
		int b = num & 0xFF;
		
		return new Vector3f((float)r / 255.0f, (float)g / 255.0f, (float)b / 255.0f);
	}
	
	/**
	 * Decode a colour into a number
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public static int decodeColour(float x, float y, float z) {
		int red = (int)Math.ceil(x * 255);
		int green = (int)Math.ceil(y * 255);
		int blue = (int)Math.ceil(z * 255);
		
		int rgb = ((red & 0x0FF) << 16) | ((green & 0x0FF) << 8) | (blue & 0x0FF);
		return rgb;
	}
}
