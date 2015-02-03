package renderer.model;

import renderer.Renderable;

/**
 * In order to be Cullable, an object needs to be both Boundable and Renderable
 * @author maxz
 *
 */
public interface Cullable extends Boundable, Renderable {

}
