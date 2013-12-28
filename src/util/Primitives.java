package util;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import physics.PhysicsModelProperties;
import renderer.Model;
import renderer.Face;
import renderer.VertexData;
import texture.Material;

/**
 * This class offers basic primitive 3D models to be used for testing and debugging. No object loading
 * is done to create these models. Also, the models are not created with proper texture coordinates.
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
        return getRectangularPrism(width, length, height, new PhysicsModelProperties());
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
        Material mat = new Material();
        Vector3f[] v = {
            new Vector3f(0, 0, 0),
            new Vector3f(0, 0, length),
            new Vector3f(0, height, 0),
            new Vector3f(0, height, length),
            new Vector3f(width, 0, 0),
            new Vector3f(width, 0, length),
            new Vector3f(width, height, 0),
            new Vector3f(width, height, length)
        };
        Vector2f tex = new Vector2f(0, 0);
        Vector3f[] vn = {
            new Vector3f(0, 0, 1),
            new Vector3f(0, 0, -1),
            new Vector3f(0, 1, 0),
            new Vector3f(0, -1, 0),
            new Vector3f(1, 0, 0),
            new Vector3f(-1, 0, 0)
        };

        // Create the list of twelve faces.
        List<Face> faceList = new ArrayList<Face>();
        faceList.add(new Face(new VertexData(v[0], tex, vn[1]), new VertexData(v[6], tex, vn[1]), new VertexData(v[4], tex, vn[1]), mat));
        faceList.add(new Face(new VertexData(v[0], tex, vn[1]), new VertexData(v[2], tex, vn[1]), new VertexData(v[6], tex, vn[1]), mat));
        faceList.add(new Face(new VertexData(v[0], tex, vn[5]), new VertexData(v[3], tex, vn[5]), new VertexData(v[2], tex, vn[5]), mat));
        faceList.add(new Face(new VertexData(v[0], tex, vn[5]), new VertexData(v[1], tex, vn[5]), new VertexData(v[3], tex, vn[5]), mat));
        faceList.add(new Face(new VertexData(v[2], tex, vn[2]), new VertexData(v[7], tex, vn[2]), new VertexData(v[6], tex, vn[2]), mat));
        faceList.add(new Face(new VertexData(v[2], tex, vn[2]), new VertexData(v[3], tex, vn[2]), new VertexData(v[7], tex, vn[2]), mat));
        faceList.add(new Face(new VertexData(v[4], tex, vn[4]), new VertexData(v[6], tex, vn[4]), new VertexData(v[7], tex, vn[4]), mat));
        faceList.add(new Face(new VertexData(v[4], tex, vn[4]), new VertexData(v[7], tex, vn[4]), new VertexData(v[5], tex, vn[4]), mat));
        faceList.add(new Face(new VertexData(v[0], tex, vn[3]), new VertexData(v[4], tex, vn[3]), new VertexData(v[5], tex, vn[3]), mat));
        faceList.add(new Face(new VertexData(v[0], tex, vn[3]), new VertexData(v[5], tex, vn[3]), new VertexData(v[1], tex, vn[3]), mat));
        faceList.add(new Face(new VertexData(v[1], tex, vn[0]), new VertexData(v[5], tex, vn[0]), new VertexData(v[7], tex, vn[0]), mat));
        faceList.add(new Face(new VertexData(v[1], tex, vn[0]), new VertexData(v[7], tex, vn[0]), new VertexData(v[3], tex, vn[0]), mat));
        
        return new Model(faceList, props);
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
        return getRectangularPrism(width, length, 0f);
    }

    /**
     * Creates and returns a plane with any custom physics properties.
     * @param width Width of the plane.
     * @param length Length of the plane.
     * @param props Any custom physics properties.
     * @return a plane
     */
    public static Model getPlane(float width, float length, PhysicsModelProperties props) {
    	return getRectangularPrism(width, length, 0f, props);
    }
}