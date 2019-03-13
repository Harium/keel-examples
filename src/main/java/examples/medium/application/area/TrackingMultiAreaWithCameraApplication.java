package examples.medium.application.area;

import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.layer.BufferedLayer;
import com.harium.keel.camera.Webcam;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;

import java.awt.image.BufferedImage;


public class TrackingMultiAreaWithCameraApplication extends TrackingMultiAreaApplication {

    private BufferedLayer layer;

    public TrackingMultiAreaWithCameraApplication(int w, int h) {
        super(w, h);
    }

    @Override
    protected Feature setupCamera() {
        cam = new Webcam();

        int w = cam.getWidth();
        int h = cam.getHeight();

        screen = new PointFeature(0, 0, w, h);
        layer = new BufferedLayer(w, h);

        return screen;
    }

    @Override
    protected void reset(BufferedImage b) {
        layer.setBuffer(b);
        layer.flipHorizontal();

        super.reset(layer.getBuffer());
    }

    @Override
    protected void updateCameraInput(KeyEvent event) {

    }

}