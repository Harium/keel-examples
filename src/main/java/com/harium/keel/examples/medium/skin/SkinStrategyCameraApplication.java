package examples.medium.skin;

import com.harium.keel.awt.camera.CameraSarxosWebcam;
import com.harium.keel.feature.Component;
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
		//drawComponents(g);
		drawComponent(g, bestCandidate);
		
		for(Component feature: faceComponents) {
			drawComponent(g, feature);
		}
		//drawComponents(g);
	}
	
}
