package examples.medium.skin;

import com.harium.keel.awt.camera.CameraSarxosWebcam;
import com.harium.keel.feature.PointFeature;
import com.harium.etyl.core.graphics.Graphics;

public class SkinStrategyCameraApplication extends SimpleFaceFinderApplication {
	
	public SkinStrategyCameraApplication(int w, int h) {
		super(w, h);
	}
	
	protected void initCamera() {
		cam = new CameraSarxosWebcam();
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(cam.getBufferedImage(), 0, 0);
		reset();
		//drawPointFeatures(g);
		drawPointFeature(g, bestCandidate);
		
		for(PointFeature feature: facePointFeatures) {
			drawPointFeature(g, feature);
		}
		//drawPointFeatures(g);
	}
	
}
