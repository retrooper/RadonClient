package com.github.retrooper.radonclient.texture;

import com.github.retrooper.radonclient.model.ModelFactory;
import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.ARBFramebufferObject.glGenerateMipmap;
import static org.lwjgl.opengl.GL11.*;

public class TextureFactory {
    public static Texture loadTexture(String fileName){
        PNGDecoder decoder;
        try {
            InputStream in = TextureFactory.class.getClassLoader().getResourceAsStream(fileName);
            decoder = new PNGDecoder(in);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to find texture " + fileName);
        }
        //Allocate a buffer. Each pixel stores an RGBA value. So thats (4 * width * height) bytes
        ByteBuffer buffer = ByteBuffer.allocateDirect(4 * decoder.getWidth() * decoder.getHeight());
        try {
            decoder.decode(buffer, decoder.getWidth() * 4, PNGDecoder.Format.RGBA);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Flip the buffer, so we can read it.
        buffer.flip();
        //Generate texture ID
        int id = glGenTextures();
        ModelFactory.TEXTURES.add(id);
        //Bind it
        glBindTexture(GL_TEXTURE_2D, id);
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        //Set the texture parameters(linear or nearest)
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        //Upload
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        glGenerateMipmap(GL_TEXTURE_2D);

        return new Texture(id);
    }
}
