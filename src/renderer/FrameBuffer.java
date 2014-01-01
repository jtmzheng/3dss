package renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Frame buffer class 
 * @author Max
 *
 */
public class FrameBuffer {
	private static final int DEFAULT_FRAME_BUFFER = 0;
	
	private final int bufferId;
	private final int bufferTextureId;
	private final int renderBufferId;
	
	/**
	 * Create a new frame buffer
	 * @param width width of the viewport
	 * @param height height of the viewport
	 */
	public FrameBuffer(int width, int height) {
		bufferId = GL30.glGenFramebuffers();
		bufferTextureId = GL11.glGenTextures();

		// Generate and allocate the frame buffer texture 
		GL11.glBindTexture (GL11.GL_TEXTURE_2D, bufferTextureId);
		GL11.glTexImage2D (
				GL11.GL_TEXTURE_2D,
				0,
				GL11.GL_RGBA,
				width,
				height,
				0,
				GL11.GL_RGBA,
				GL11.GL_UNSIGNED_BYTE,
				(ByteBuffer)null
				);
		GL11.glBindTexture (GL11.GL_TEXTURE_2D, 0);
		
		// Bind the new frame buffer and attach the texture
		GL30.glBindFramebuffer (GL30.GL_FRAMEBUFFER, bufferId);
		GL30.glFramebufferTexture2D (
				GL30.GL_FRAMEBUFFER, 
				GL30.GL_COLOR_ATTACHMENT0, 
				GL11.GL_TEXTURE_2D, 
				bufferTextureId, 
				0
				);
		
		// Generate and set up the render buffer
		renderBufferId = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer (GL30.GL_RENDERBUFFER, renderBufferId);
		GL30.glRenderbufferStorage (
				GL30.GL_RENDERBUFFER, 
				GL11.GL_DEPTH_COMPONENT, 
				width, 
				height
				);
		
		GL30.glFramebufferRenderbuffer (
				GL30.GL_FRAMEBUFFER, 
				GL30.GL_DEPTH_ATTACHMENT, 
				GL30.GL_RENDERBUFFER, 
				renderBufferId
				);
		
		GL20.glDrawBuffers(GL30.GL_COLOR_ATTACHMENT0);		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, DEFAULT_FRAME_BUFFER);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getFrameBuffer() {
		return bufferId;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getFrameBufferTexture() {
		return bufferTextureId;
	}
	
}
