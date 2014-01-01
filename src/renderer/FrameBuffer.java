package renderer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.nio.ByteBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class FrameBuffer {

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
		
		GL30.glBindFramebuffer (GL30.GL_FRAMEBUFFER, bufferId);
		GL30.glFramebufferTexture2D (
				GL30.GL_FRAMEBUFFER, 
				GL30.GL_COLOR_ATTACHMENT0, 
				GL11.GL_TEXTURE_2D, 
				bufferTextureId, 
				0
				);
		
		// Set texture parameters
		GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri (GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		
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
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
	}
	
	public int getFrameBuffer() {
		return bufferId;
	}
	
	public int getFrameBufferTexture() {
		return bufferTextureId;
	}
	
}
