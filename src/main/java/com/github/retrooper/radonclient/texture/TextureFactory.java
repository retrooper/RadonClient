package com.github.retrooper.radonclient.texture;

import com.github.retrooper.radonclient.model.ModelFactory;
import de.matthiasmann.twl.utils.PNGDecoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.ARBFramebufferObject.glGenerateMipmap;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;

public class TextureFactory {
    public static ByteBuffer loadTextureAsBuffer(String fileName) {
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
        return buffer;
    }

    public static TextureArray loadTextures(int width, int height, String... fileNames) {
        if (fileNames.length == 0) {
            throw new IllegalStateException("No texture files provided");
        }
        //Generate texture ID
        int id = glGenTextures();
        ModelFactory.TEXTURES.add(id);
        glBindTexture(GL_TEXTURE_2D_ARRAY, id);
        glTexImage3D(GL_TEXTURE_2D_ARRAY, 0, GL_RGBA, width, height, fileNames.length, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
        for (int i = 0; i < fileNames.length; i++) {
            ByteBuffer buffer = loadTextureAsBuffer("textures/" + fileNames[i]);
            glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, i, width, height, 1, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
            System.out.println("Added " + fileNames[i] + " to the texture array...");
        }

        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_R, GL_CLAMP_TO_EDGE);
        glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
        return new TextureArray(id);
    }
}
