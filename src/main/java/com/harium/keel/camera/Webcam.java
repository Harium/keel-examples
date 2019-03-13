package com.harium.keel.camera;

import java.awt.image.BufferedImage;

/**
 * Move to keel-awt-camera (ideally use BufferedSource as return)
 */
public class Webcam implements Camera {

    com.github.sarxos.webcam.Webcam webcam;

    public Webcam() {
        webcam = com.github.sarxos.webcam.Webcam.getDefault();
    }

    @Override
    public BufferedImage getImage() {
        return webcam.getImage();
    }

    @Override
    public int getWidth() {
        return webcam.getViewSize().width;
    }

    @Override
    public int getHeight() {
        return webcam.getViewSize().height;
    }
}
