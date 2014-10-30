package renderer.framebuffer;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * Frame buffer class 
 * @author Max
 *
 */
public class FrameBuffer {
	
	public FrameBuffer(int width, int height, List<FBTarget> fbTargets) {
		this.width = width;
		this.height = height;
		this.fbTargets = new HashMap<>();
		
		bufferId = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, bufferId);

		IntBuffer buffer = BufferUtils.createIntBuffer(fbTargets.size());
		
		for(FBTarget target : fbTargets) {
			int texId = generateTexture(target);
			if(texId != -1) {
				// Bind the new frame buffer and attach the texture
				GL30.glFramebufferTexture2D (
						GL30.GL_FRAMEBUFFER, 
						target.getTarget(), 
						GL11.GL_TEXTURE_2D, 
						texId, 
						0
						);
			
				// Do not draw to the depth attachment
				if(target != FBTarget.GL_DEPTH_ATTACHMENT) {
					buffer.put(target.getTarget());
				}
				
				this.fbTargets.put(target, texId);
			}
		}
		
		buffer.flip();
		
		/*
		// Generate and set up the render buffer (For depth attachment, taken out because now rendering depth to texture)
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
				*/

		GL20.glDrawBuffers(buffer);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, DEFAULT_FRAME_BUFFER);

	}
	
	/**
	 * Get the frame buffer handle 
	 * @return bufferId
	 */
	public int getFrameBuffer() {
		return bufferId;
	}
	
	/**
	 * Get the texture handle
	 * @return textureId
	 */
	public int getFrameBufferTexture(FBTarget target) {
		return fbTargets.get(target);
	}
	
	private int generateTexture(FBTarget target) {
		int bufferTextureId = GL11.glGenTextures();

		// Generate and allocate the frame buffer texture 
		GL11.glBindTexture (GL11.GL_TEXTURE_2D, bufferTextureId);
		GL11.glTexImage2D (
				GL11.GL_TEXTURE_2D,
				0,
				target.getInternalFormat(),
				width,
				height,
				0,
				target.getFormat(),
				GL11.GL_UNSIGNED_BYTE,
				(ByteBuffer)null
				);

		// Set texture parameters
		switch(target.getTarget()) {
		case GL30.GL_COLOR_ATTACHMENT0 : {
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			break;
		}
		case GL30.GL_DEPTH_ATTACHMENT : {
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL14.GL_DEPTH_TEXTURE_MODE, GL11.GL_INTENSITY);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL14.GL_COMPARE_R_TO_TEXTURE);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_FUNC, GL11.GL_LEQUAL);
			break;
		}
		case GL30.GL_COLOR_ATTACHMENT1: {
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
			GL11.glTexParameteri(GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
			break;
		}
		}
					
		GL11.glBindTexture (GL11.GL_TEXTURE_2D, 0);

		return bufferTextureId;
	}
	
	private static final int DEFAULT_FRAME_BUFFER = 0;
	
	private int width;
	private int height;
	
	private int bufferId;
	// private int renderBufferId;
	private Map<FBTarget, Integer> fbTargets; 
}
