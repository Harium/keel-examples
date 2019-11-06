package examples.misc;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.MouseEvent;
import com.harium.etyl.commons.event.PointerEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.layer.BufferedLayer;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.Camera;
import com.harium.keel.camera.Webcam;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.search.TriangularSearch;
import com.harium.keel.filter.selection.RGBColorStrategy;

public class SimpleCam extends Application {

    public SimpleCam(int w, int h) {
        super(w, h);
    }

    private Camera cam;
    private BufferedImageSource source = new BufferedImageSource();

    private TriangularSearch colorFilter;

    private RGBColorStrategy colorStrategy;

    private BufferedLayer mirror;

    private Feature screen;

    private PointFeature point;

    @Override
    public void load() {

        loadingInfo = "Opening Camera";

        cam = new Webcam();

        screen = new Feature(0, 0, cam.getWidth(), cam.getHeight());

        loadingInfo = "Setting Filter";

        colorFilter = new TriangularSearch(w, h);
        colorFilter.setBorder(20);

        colorStrategy = new RGBColorStrategy(Color.BLACK.getRGB());
        colorFilter.setSelectionStrategy(colorStrategy);

        mirror = new BufferedLayer(0, 0);

        loading = 100;
    }

    @Override
    public void update(long now) {
        //Get the Camera image
        mirror.setBuffer(cam.getImage());

        //Normally the camera shows the image flipped, but we want to see something like a mirror
        //So we flip the image
        mirror.flipHorizontal();
        source.setImage(mirror.getBuffer());

        //Now we search for the first pixel with the desired color in the whole screen
        point = colorFilter.filterFirst(source, screen);
    }

    @Override
    public void draw(Graphics g) {

        //Draw the mirror image
        mirror.draw(g);

        //Set a Color to our Point
        g.setColor(Color.CYAN);

        //Draw our tracking point with radius = 10 pixels
        g.fillCircle(point.getX(), point.getY(), 10);

    }

    @Override
    public void updateMouse(PointerEvent event) {

        if (event.isButtonUp(MouseEvent.MOUSE_BUTTON_LEFT)) {
            //When mouse clicks with LeftButton, the color filter tries to find
            //the color we are clicking on
            colorStrategy.setColor(mirror.getBuffer().getRGB((int) event.getX(), (int) event.getY()));
        }

    }

}
