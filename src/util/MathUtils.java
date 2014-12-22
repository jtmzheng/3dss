package util;

import java.util.Random;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Collection of math related static utility functions.
 *
 * @author Adi
 */
public class MathUtils {
	public static final Matrix4f IDENTITY4x4 = new Matrix4f();
	
	// @TODO: Probably bad usage of a static initializer
	static {
		Matrix4f.setIdentity(IDENTITY4x4);
    }
	
	/**
	 * Returns true if a point is inside a set of planes, and false otherwise.
	 */
	public static boolean isPointInPlanes (Vector3f pt, Plane... planes) {
		for (int i = 0; i < planes.length; i++) {
			if (dotPlaneWithVector(planes[i], pt) < 0f) return false;
		}
		return true;
	}
	
	/**
	 * Computes the dot product of a plane and a vector.
	 */
	public static float dotPlaneWithVector (Plane p, Vector3f v) {
		return p.a*v.x + p.b*v.y + p.c*v.z + p.d;
	}

	/**
	 * Returns true if a point is inside a set of planes, and false otherwise.
	 */
	public static boolean isPointInPlanes (Vector4f pt, Plane... planes) {
		for (int i = 0; i < planes.length; i++) {
			if (dotPlaneWithVector(planes[i], pt) < 0f) return false;
		}

		return true;
	}
	
	/**
	 * Computes the dot product of a plane and a vector.
	 */
	public static float dotPlaneWithVector (Plane p, Vector4f v) {
		return p.a*v.x + p.b*v.y + p.c*v.z + p.d;
	}

	/**
	 * Normalizes the plane coefficients such that the plane normal (a,b,c) has a unit length of 1.
	 */
	public static void normalizePlane (Plane p) {
		float mag = (float) Math.sqrt(p.a*p.a + p.b*p.b + p.c*p.c);
		p.a /= mag;
		p.b /= mag;
		p.c /= mag;
	}
	
	/**
	 * Returns a pseudo-random number between min and max, inclusive.
	 * The difference between min and max can be at most
	 * <code>Integer.MAX_VALUE - 1</code>.
	 *
	 * @param min Minimum value
	 * @param max Maximum value.  Must be greater than min.
	 * @return Integer between min and max, inclusive.
	 * @see java.util.Random#nextInt(int)
	 */
	public static int randInt(int min, int max) {

	    // Usually this can be a field rather than a method variable
	    Random rand = new Random();

	    // nextInt is normally exclusive of the top value,
	    // so add 1 to make it inclusive
	    int randomNum = rand.nextInt((max - min) + 1) + min;

	    return randomNum;
	}
}