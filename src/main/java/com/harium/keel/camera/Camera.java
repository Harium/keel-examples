package com.harium.keel.camera;

import java.awt.image.BufferedImage;

public interface Camera {

    BufferedImage getImage();

    int getWidth();

    int getHeight();

}
