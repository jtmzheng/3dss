package util;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

/**
 * Collection of math related static utility functions.
 *
 * @author Adi
 */
public class MathUtils {
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
			if (dotPlaneWithVector(planes[i], pt) < 0.0f) return false;
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
}