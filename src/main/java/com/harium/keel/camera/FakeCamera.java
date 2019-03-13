package com.harium.keel.camera;

import com.harium.etyl.loader.image.ImageLoader;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FakeCamera implements Camera {

    int cursor = 0;
    List<BufferedImage> frames = new ArrayList<>();

    public BufferedImage getImage() {
        return frames.get(cursor);
    }

    public void addImage(String path) {
        BufferedImage image = ImageLoader.getInstance().getImage(path);
        frames.add(image);
    }

    public int getWidth() {
        return getImage().getWidth();
    }

    public int getHeight() {
        return getImage().getHeight();
    }

    public BufferedImage nextFrame() {
        cursor++;
        cursor %= frames.size();
        return getImage();
    }

    public BufferedImage previousFrame() {
        cursor += frames.size() - 1;
        cursor %= frames.size();
        return getImage();
    }

    public int getCursor() {
        return cursor;
    }
}
