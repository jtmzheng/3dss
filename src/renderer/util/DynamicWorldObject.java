package renderer.util;

/**
 * A DynamicWorldObject is any object in the world that needs some form of cleanup
 * @author Max
 *
 */
public interface DynamicWorldObject {

	public boolean needsCleanup();
	public boolean runCleanup();
	
}
