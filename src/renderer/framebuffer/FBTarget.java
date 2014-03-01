package renderer.framebuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

/**
 * Defines the supported Texture targets for the Frame Buffer
 * @author Max
 *
 */
public enum FBTarget {
	GL_COLOR_ATTACHMENT(GL30.GL_COLOR_ATTACHMENT0, GL11.GL_RGBA, GL11.GL_RGBA),
	GL_DEPTH_ATTACHMENT(GL30.GL_DEPTH_ATTACHMENT, GL11.GL_DEPTH_COMPONENT, GL14.GL_DEPTH_COMPONENT24),
	GL_NORMAL_ATTACHMENT(GL30.GL_COLOR_ATTACHMENT1, GL11.GL_RGBA, GL30.GL_RGBA32F);
	
	private int target;
	private int format;
	private int internalFormat;
	
	FBTarget(int target, int format, int internalFormat) {
        this.target = target;
        this.format = format;
        this.internalFormat = internalFormat;
    }

    public int getTarget() {
        return target;
    }
    
    public int getFormat() {
    	return format;
    }
    
    public int getInternalFormat() {
    	return internalFormat;
    }
}
