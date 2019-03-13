package examples.medium.application;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.event.MouseEvent;
import com.harium.etyl.commons.event.PointerEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.layer.BufferedLayer;
import com.harium.keel.awt.PolygonHelper;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.Camera;
import com.harium.keel.camera.Webcam;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.ColorFilter;
import com.harium.keel.filter.validation.point.MaxDimensionValidation;
import com.harium.keel.filter.validation.point.MinDensityValidation;
import com.harium.keel.filter.validation.point.MinDimensionValidation;
import examples.medium.application.area.AreaDrawer;

import java.awt.image.BufferedImage;
import java.util.List;

public class TrackingCameraColorApplication extends Application {

    private Camera cam;
    private BufferedImageSource source = new BufferedImageSource();

    private ColorFilter blueFilter;

    //Blue Marker
    private Color color = new Color(171, 112, 100);

    private int tolerance = 10;
    private int minDensity = 12;
    private int minDimension = 10;
    private int maxDimension = 100;

    private MinDensityValidation densityValidation;
    private MinDimensionValidation dimensionValidation;

    private boolean hide = false;
    private boolean pixels = true;

    private int xOffset = 0;
    private int yOffset = 0;

    private Feature screen;

    private List<PointFeature> bluePointFeatures;

    private BufferedLayer layer;

    public TrackingCameraColorApplication(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {

        loadingInfo = "Loading Images";

        screen = setupCamera();

        densityValidation = new MinDensityValidation(minDensity);
        dimensionValidation = new MinDimensionValidation(minDimension);

        blueFilter = new ColorFilter(screen.getWidth(), screen.getHeight(), color, tolerance);
        blueFilter.addValidation(dimensionValidation);
        blueFilter.addValidation(new MaxDimensionValidation(maxDimension));

        blueFilter.addValidation(densityValidation);

        final int MAGIC_NUMBER = 3;//Higher = Faster and less precise

        blueFilter.setBorder(MAGIC_NUMBER);
        blueFilter.setStep(2);

        loadingInfo = "Configuring Filter";

        loading = 60;
        reset(cam.getImage());

        loading = 100;
    }

    protected Feature setupCamera() {
        cam = new Webcam();

        int w = cam.getWidth();
        int h = cam.getHeight();

        screen = new Feature(w, h);
        layer = new BufferedLayer(w, h);

        return screen;
    }

    int bx = 0;
    int by = 0;
    int bRadius = 0;

    private void reset(BufferedImage b) {
        layer.setBuffer(b);
        layer.flipHorizontal();

        source.setImage(layer.getBuffer());

        bluePointFeatures = blueFilter.filter(source, screen);

        if (!bluePointFeatures.isEmpty()) {

            bx = 0;
            by = 0;

            bRadius = 0;

            for (PointFeature component : bluePointFeatures) {
                Point2D p = component.getCenter();
                bx += p.x;
                by += p.y;

                bRadius += (component.getW() + component.getH()) / 4;
            }

            bx /= bluePointFeatures.size();
            by /= bluePointFeatures.size();

            bRadius /= bluePointFeatures.size();

            return;
        }
    }

    @Override
    public void updateMouse(PointerEvent event) {

        if (event.isButtonDown(MouseEvent.MOUSE_BUTTON_LEFT)) {
            color = pickColor(event.getX(), event.getY());
            blueFilter.setColor(color);

            System.out.println(color.getRed());
            System.out.println(color.getGreen());
            System.out.println(color.getBlue());
            System.out.println("---------");
        }
    }

    private Color pickColor(int px, int py) {
        return new Color(layer.getBuffer().getRGB(px, py));
    }

    @Override
    public void updateKeyboard(KeyEvent event) {

        if (event.isKeyDown(KeyEvent.VK_H)) {
            hide = !hide;
        }

        if (event.isKeyDown(KeyEvent.VK_J)) {
            pixels = !pixels;
        }

        //Change Tolerance
        if (event.isKeyUp(KeyEvent.VK_EQUALS)) {
            tolerance++;
            blueFilter.setTolerance(tolerance);
        } else if (event.isKeyUp(KeyEvent.VK_MINUS)) {
            tolerance--;
            blueFilter.setTolerance(tolerance);
        }

        //Change Density
        if (event.isKeyUp(KeyEvent.VK_P)) {
            minDensity++;
            densityValidation.setDensity(minDensity);
        } else if (event.isKeyUp(KeyEvent.VK_O)) {
            minDensity--;
            densityValidation.setDensity(minDensity);
        }

        //Change Dimension
        if (event.isKeyUp(KeyEvent.VK_L)) {
            minDimension++;
            dimensionValidation.setDimension(minDimension);
        } else if (event.isKeyUp(KeyEvent.VK_K)) {
            minDimension--;
            dimensionValidation.setDimension(minDimension);
        }
    }

    @Override
    public void draw(Graphics g) {

        if (!hide) {
            g.drawImage(layer.getBuffer(), xOffset, yOffset);
        }

        reset(cam.getImage());

        if (pixels) {

            g.setColor(color);
            g.fillRect(0, 0, 60, 80);

            g.setColor(Color.BLACK);

            g.drawString("Tol: " + Integer.toString(tolerance), 10, 80);
            g.drawString("Den: " + Integer.toString(minDensity), 10, 100);
            g.drawString("Dim: " + Integer.toString(minDimension), 10, 120);

            g.setAlpha(60);
            //drawFeaturedPoints(g, sampledFeature, Color.GREEN);
            g.setAlpha(100);

            g.setColor(Color.GREEN);

            PointFeature c1 = null, c2 = null;
            if (bluePointFeatures != null) {
                for (PointFeature component : bluePointFeatures) {
                    g.drawPolygon(PolygonHelper.getBoundingBox(component));
                    g.drawString(Double.toString(component.getDensity()), component.getRectangle());

                    if (c1 == null) {
                        c1 = component;
                    } else if (c2 == null) {
                        c2 = component;
                    }
                }
            }

            if (c1 != null && c2 != null) {
                AreaDrawer.drawMultiArea(g, c1.getCenter(), c2.getCenter());
            }

            g.setAlpha(50);
            g.fillCircle(bx, by, bRadius);
            g.resetOpacity();
        }
    }

}