package renderer;

public class Context {

	public Context(String title, int width, int height, int majorVersion, int minorVersion, boolean useCore) {
		this.title = title;
		this.width = width;
		this.height = height;
		this.useFullscreen = false;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.useCore = useCore;
		this.frameRate = DEFAULT_FRAME_RATE;
	}
	
	public Context(String title, int width, int height, int majorVersion, int minorVersion, boolean useCore, int frameRate) {
		this.title = title;
		this.width = width;
		this.height = height;
		this.useFullscreen = false;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.useCore = useCore;
		this.frameRate = frameRate;
	}
	
	public Context(String title, int majorVersion, int minorVersion, boolean useCore) {
		this.title = title;
		this.width = -1;
		this.height = -1;
		this.useFullscreen = true;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.useCore = useCore;
		this.frameRate = DEFAULT_FRAME_RATE;
	}
	
	public Context(String title, int majorVersion, int minorVersion, boolean useCore, int frameRate) {
		this.title = title;
		this.width = -1;
		this.height = -1;
		this.useFullscreen = true;
		this.majorVersion = majorVersion;
		this.minorVersion = minorVersion;
		this.useCore = useCore;
		this.frameRate = frameRate;
	}
	
	// Defaults 
	private static final int DEFAULT_WIDTH = 320;
	private static final int DEFAULT_HEIGHT = 240;
	private static final int DEFAULT_FRAME_RATE = 60;
	
	public final String title;
	public final boolean useFullscreen;
	public final int width;
	public final int height;
	public final int majorVersion;
	public final int minorVersion;
	public final boolean useCore;
	public final int frameRate;
	
}
