package examples.medium.skin;

import com.harium.etyl.core.graphics.Graphics;
import com.harium.keel.camera.Webcam;
import com.harium.keel.feature.PointFeature;

public class SkinStrategyCameraApplication extends SimpleFaceFinderApplication {

    public SkinStrategyCameraApplication(int w, int h) {
        super(w, h);
    }

    protected void initCamera() {
        cam = new Webcam();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(cam.getImage(), 0, 0);
        reset();
        //drawPointFeatures(g);
        drawPointFeature(g, bestCandidate);

        for (PointFeature feature : facePointFeatures) {
            drawPointFeature(g, feature);
        }
        //drawPointFeatures(g);
    }

}
