package util;

import renderer.Model;
import physics.PhysicsModelProperties;

/**
 * This class offers basic primitive models to be used for testing and debugging. No
 * object loading is done to create these models and the models do not have proper
 * texture coordinates. The models all have an optional parameter for custom physics
 * model properties.
 *
 * @author Adi
 */
public class Primitives {
    /**
     * Creates and returns a sphere.
     * @param radius The radius of the sphere.
     * @return a sphere
     */
    public static Model getSphere(float radius) {
        return null;
    }

    /**
     * Creates and returns a sphere with any custom physics properties.
     * @param radius The radius of the sphere.
     * @param props Any custom physics properties.
     * @return a sphere
     */
    public static Model getSphere(float radius, PhysicsModelProperties props) {
        return null;
    }

    /**
     * Creates and returns a rectangular prism.
     * @param width Width of the box.
     * @param length Length of the box.
     * @param height Height of the box.
     * @return a rectangular prism
     */
    public static Model getRectangularPrism(float width, float length, float height) {
        return null;
    }

    /**
     * Creates and returns a rectangular prism with any custom physics properties.
     * @param width Width of the box.
     * @param length Length of the box.
     * @param height Height of the box.
     * @param props Any custom physics properties.
     * @return a rectangular prism
     */
    public static Model getRectangularPrism(float width, float length, float height, PhysicsModelProperties props) {
        return null;
    }

    /**
     * Creates and returns a cube.
     * @param edgeLength Length of an edge on the cube.
     * @return a cube
     */
    public static Model getCube(float edgeLength) {
        return getRectangularPrism(edgeLength, edgeLength, edgeLength);
    }

    /**
     * Creates and returns a cube with any custom physics properties.
     * @param edgeLength Length of an edge on the cube.
     * @param props Any custom physics properties.
     * @return a cube
     */
    public static Model getCube(float edgeLength, PhysicsModelProperties props) {
        return getRectangularPrism(edgeLength, edgeLength, edgeLength, props);
    }

    /**
     * Creates and returns a plane.
     * @param width Width of the plane.
     * @param length Length of the plane.
     * @return a plane
     */
    public static Model getPlane(float width, float length) {
        return getRectangularPrism(width, length, 1);
    }

    /**
     * Creates and returns a plane with any custom physics properties.
     * @param width Width of the plane.
     * @param length Length of the plane.
     * @param props Any custom physics properties.
     * @return a plane
     */
    public static Model getPlane(float width, float length, PhysicsModelProperties props) {
        return getRectangularPrism(width, length, 1, props);
    }
}