package examples.medium.application.area;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.event.MouseEvent;
import com.harium.etyl.commons.event.PointerEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.keel.awt.PolygonHelper;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.Camera;
import com.harium.keel.camera.FakeCamera;
import com.harium.keel.core.helper.ColorHelper;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.ColorFilter;
import com.harium.keel.filter.validation.point.MaxDimensionValidation;
import com.harium.keel.filter.validation.point.MinDensityValidation;
import com.harium.keel.filter.validation.point.MinDimensionValidation;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class TrackingMultiAreaApplication extends Application {

    protected Camera cam;

    protected BufferedImage buffer;
    private BufferedImageSource source = new BufferedImageSource();

    //Orange Marker
    private ColorFilter orangeFilter;
    //private Color orangeColor = new Color(165, 94, 74);
    private Color orangeColor = new Color(67, 81, 107);

    //Blue Marker
    private ColorFilter blueFilter;
    private Color blueColor = new Color(0, 153, 255);

    private MultiArea area = new MultiArea(5);

    private int tolerance = 10;
    private int minDensity = 12;
    private int minDimension = 37;
    private int maxDimension = 270;

    private MinDensityValidation densityValidation;
    private MinDimensionValidation minDimensionValidation;
    private MaxDimensionValidation maxDimensionValidation;

    private boolean hide = false;
    private boolean markers = true;

    private int xOffset = 0;
    private int yOffset = 0;

    protected Feature screen;

    //Area Stuff
    private boolean foundTwoPointFeatures = false;
    private int areaOver = -1;

    private PointFeature c1 = null, c2 = null;
    private double densityC1 = 0;
    private double densityC2 = 0;
    private static final int minCollisionCount = 80;

    private List<PointFeature> orangePointFeatures;
    private List<PointFeature> bluePointFeatures;

    public TrackingMultiAreaApplication(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {

        loadingInfo = "Loading Images";

        screen = setupCamera();

        densityValidation = new MinDensityValidation(minDensity);
        minDimensionValidation = new MinDimensionValidation(minDimension);
        maxDimensionValidation = new MaxDimensionValidation(maxDimension);

        orangeFilter = setupFilter(orangeColor);
        orangeFilter.addValidation(minDimensionValidation);
        orangeFilter.addValidation(densityValidation);
        orangeFilter.setMinNeighbors(7);

        blueFilter = setupFilter(blueColor);
        blueFilter.addValidation(minDimensionValidation);

        loadingInfo = "Configuring Filter";

        loading = 60;
        reset(cam.getImage());

        loading = 100;
    }

    protected Feature setupCamera() {
        cam = new FakeCamera();

		/*cam.addImage("dumbbells/dumbbells1.png");
        cam.addImage("dumbbells/dumbbells2.png");*/
        ((FakeCamera) cam).addImage("dumbbells/dumbbells5.png");

        int w = cam.getWidth();
        int h = cam.getHeight();

        screen = new Feature(w, h);

        return screen;
    }

    private ColorFilter setupFilter(Color color) {
        ColorFilter filter = new ColorFilter(screen.getWidth(), screen.getHeight(), color, tolerance);
        filter.setBorder(3);
        filter.setStep(2);
        filter.addValidation(maxDimensionValidation);

        return filter;
    }

    protected void reset(BufferedImage b) {
        source.setImage(b);

        orangePointFeatures = orangeFilter.filter(source, screen);
        bluePointFeatures = blueFilter.filter(source, screen);

        if (!orangePointFeatures.isEmpty()) {
            evaluateDensity(b);
        }

        if (foundTwoPointFeatures) {

            area.generateArea(c1.getCenter(), c2.getCenter());
            areaOver = -1;

            //Verify Collisions
            for (PointFeature component : bluePointFeatures) {
                for (int i = 0; i < area.getAreas(); i++) {

                    int count = 0;

                    for (Point2D p : component.getPoints()) {
                        Polygon polygon = area.getPolygons()[i];
                        if (polygon.contains(p.x, p.y)) {
                            count++;
                        }
                    }

                    if (count >= minCollisionCount) {
                        areaOver = i;
                        break;
                    }
                }
            }
        }

        buffer = b;
    }

    private void evaluateDensity(BufferedImage b) {
        densityC1 = 0;
        densityC2 = 0;

        int found = 0;

        for (PointFeature component : orangePointFeatures) {

            if (nonSquared(component) || !hasCenterColor(component, b)) {
                continue;
            }

            double dens = component.getDensity();

            if (dens > densityC1) {
                c2 = c1;
                densityC2 = densityC1;
                c1 = component;
                densityC1 = dens;
                found++;
            } else if (dens > densityC2) {
                c2 = component;
                densityC2 = dens;
                found++;
            }
        }

        foundTwoPointFeatures = found >= 2;
    }

    private boolean nonSquared(PointFeature component) {
        return component.getW() > component.getH() * 1.5;
    }

    private boolean hasCenterColor(PointFeature component, BufferedImage b) {
        Point2D center = component.getCenter();
        int rgb = b.getRGB((int) center.x, (int) center.y);
        return ColorHelper.isColor(orangeFilter.getColor(), rgb, 8);
    }

    @Override
    public void updateMouse(PointerEvent event) {

        if (event.isButtonDown(MouseEvent.MOUSE_BUTTON_LEFT)) {
            orangeColor = pickColor(event.getX(), event.getY());
            orangeFilter.setColor(orangeColor);

            System.out.println(orangeColor.getRed());
            System.out.println(orangeColor.getGreen());
            System.out.println(orangeColor.getBlue());
            System.out.println("---------");
        }

        if (event.isButtonDown(MouseEvent.MOUSE_BUTTON_RIGHT)) {
            blueColor = pickColor(event.getX(), event.getY());
            blueFilter.setColor(blueColor);

            System.out.println(blueColor.getRed());
            System.out.println(blueColor.getGreen());
            System.out.println(blueColor.getBlue());
            System.out.println("---------");
        }
    }

    private Color pickColor(int px, int py) {
        return new Color(buffer.getRGB(px, py));
    }

    @Override
    public void updateKeyboard(KeyEvent event) {

        if (event.isKeyDown(KeyEvent.VK_H)) {
            hide = !hide;
        }

        if (event.isKeyDown(KeyEvent.VK_J)) {
            markers = !markers;
        }

        //Change Tolerance
        if (event.isKeyUp(KeyEvent.VK_EQUALS)) {
            tolerance++;
            orangeFilter.setTolerance(tolerance);
        } else if (event.isKeyUp(KeyEvent.VK_MINUS)) {
            tolerance--;
            orangeFilter.setTolerance(tolerance);
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
            minDimensionValidation.setDimension(minDimension);
        } else if (event.isKeyUp(KeyEvent.VK_K)) {
            minDimension--;
            minDimensionValidation.setDimension(minDimension);
        }

        updateCameraInput(event);
    }

    protected void updateCameraInput(KeyEvent event) {
        if (event.isKeyUp(KeyEvent.VK_RIGHT_ARROW)) {
            ((FakeCamera) cam).nextFrame();
        } else if (event.isKeyUp(KeyEvent.VK_LEFT_ARROW)) {
            ((FakeCamera) cam).previousFrame();
        }
    }

    @Override
    public void draw(Graphics g) {

        reset(cam.getImage());

        if (!hide) {
            g.drawImage(buffer, xOffset, yOffset);
        }

        g.setColor(orangeColor);
        g.fillRect(0, 0, 60, 80);

        g.setColor(blueColor);
        g.fillRect(w - 60, 0, 60, 80);

        g.setColor(Color.WHITE);

        if (markers) {

            g.drawStringShadow("Tol: " + Integer.toString(tolerance), 10, 80);
            g.drawStringShadow("Den: " + Integer.toString(minDensity), 10, 100);
            g.drawStringShadow("Dim: " + Integer.toString(minDimension), 10, 120);

            g.setColor(Color.ORANGE);

            //Draw OrangePointFeatures
            if (orangePointFeatures != null) {
                for (PointFeature component : orangePointFeatures) {
                    g.drawPolygon(PolygonHelper.getBoundingBox(component));
                    g.drawStringShadow(component.getW() + "x" + component.getH(), component.getRectangle());
                    g.drawStringShadow(Double.toString(component.getDensity()), component.getX(), component.getY() + 25, component.getW(), component.getH());
                }
            }

            if (foundTwoPointFeatures) {

                //Draw Areas
                g.setAlpha(50);
                g.setColor(Color.BLUE);

                for (int i = 0; i < area.getAreas(); i++) {
                    Polygon polygon = area.getPolygons()[i];

                    if (areaOver != i) {
                        /*g.setColor(Color.GREEN);
						g.drawRect(area.getLayers()[i]);*/

                        g.setColor(Color.BLUE);
                        g.drawPolygon(polygon);
                    } else {
                        g.fillPolygon(polygon);
                    }

                }

                g.resetOpacity();
            }

            if (areaOver >= 0)
                g.drawStringShadowX(Integer.toString(areaOver + 1), 300);

            if (bluePointFeatures != null) {
                for (PointFeature component : bluePointFeatures) {
                    g.drawPolygon(PolygonHelper.getBoundingBox(component));
                }
            }

        }
    }

}