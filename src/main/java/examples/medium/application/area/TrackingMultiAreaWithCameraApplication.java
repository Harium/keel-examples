package examples.medium.application.area;

import java.awt.image.BufferedImage;

import com.harium.keel.awt.camera.CameraV4L4J;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.layer.BufferedLayer;


public class TrackingMultiAreaWithCameraApplication extends TrackingMultiAreaApplication {

	private BufferedLayer layer;

	public TrackingMultiAreaWithCameraApplication(int w, int h) {
		super(w, h);
	}

	@Override
	protected Feature setupCamera() {
		//cam = new CameraSarxosWebcam(0);
		cam = new CameraV4L4J(0);

		int w = cam.getBufferedImage().getWidth();
		int h = cam.getBufferedImage().getHeight();

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