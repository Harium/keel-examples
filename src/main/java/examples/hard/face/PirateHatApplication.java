package examples.hard.face;

import com.harium.etyl.geometry.Point2D;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.FakeCamera;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.track.TrackingByMultipleColorFilter;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.event.MouseEvent;
import com.harium.etyl.commons.event.PointerEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.layer.ImageLayer;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PirateHatApplication extends Application {

    private FakeCamera cam = new FakeCamera();
    private BufferedImageSource source = new BufferedImageSource();

    private TrackingByMultipleColorFilter skinFilter;

    private boolean hide = true;
    private boolean pixels = true;
    private boolean drawCleanedOnly = false;
    private boolean drawBox = true;

    private int xOffset = 0;
    private int yOffset = 0;

    private final int minDensity = 22;
    private final int maxDensity = 50;


    private final int IMAGES_TO_LOAD = 90;

    private List<PointFeature> skinFeatures;

    private PointFeature screen;

    private ImageLayer pirateHat;

    private PointFeature overMouse = null;

    private PointFeature biggestPointFeature = null;

    private BufferedImage image;

    private Map<PointFeature, Double> map = new HashMap<PointFeature, Double>();

    public PirateHatApplication(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {

        loadingInfo = "Loading Images";

        for (int i = 1; i <= IMAGES_TO_LOAD; i++) {
            loading = i;

            cam.addImage("skin/skin" + Integer.toString(i) + ".jpg");
        }

        loadingInfo = "Configuring Filter";

        configureSkinFilter();
        pirateHat = new ImageLayer("effects/piratehat.png");

        loading = 60;
        reset();

        loading = 100;
    }

    private void configureSkinFilter() {

        int width = cam.getWidth();
        int height = cam.getHeight();

        skinFilter = new TrackingByMultipleColorFilter(width, height);

        //skinFilter.addColor(new Color(0xAF, 0x80, 0x66), 0x26);

        //F5 EA E0 D4 C6 B6 A3 90 7B 65 4E
        //C4 C2 B0 A0 8D 7C 6A 5C 4B 3D 2F
        //CD BA A6 93 82 70 5F 4F 41 35 2A

        int tolerance = 0x14;

        int highTolerance = 0x19;

        int lowTolerance = 0x0A;

        skinFilter.addColor(new Color(0xF5, 0xC4, 0xCD), tolerance, tolerance, highTolerance);

        skinFilter.addColor(new Color(0xEA, 0x90, 0x90), lowTolerance, lowTolerance, lowTolerance);

        skinFilter.addColor(new Color(0xE4, 0xC2, 0xBA), tolerance, lowTolerance, highTolerance);

        skinFilter.addColor(new Color(0xE1, 0x79, 0x78), lowTolerance);

        skinFilter.addColor(new Color(0xE0, 0xB0, 0xB0), tolerance, tolerance, lowTolerance);
        //skinFilter.addColor(new Color(0xE0, 0xB0, 0xA0), tolerance, tolerance, lowTolerance);

        skinFilter.addColor(new Color(0xd0, 0x9b, 0x8b), lowTolerance);
        //Secundary Hue Scale
        skinFilter.addColor(new Color(0xd0, 0x7e, 0x5f), lowTolerance);

        skinFilter.addColor(new Color(0xC6, 0x8D, 0x82), lowTolerance);

        skinFilter.addColor(new Color(0xB6, 0x7C, 0x70), tolerance);

        skinFilter.addColor(new Color(0xA3, 0x6A, 0x5F), tolerance);

        //skinFilter.addColor(new Color(0x90, 0x5C, 0x4F), tolerance);

        //skinFilter.addColor(new Color(0xa4, 0x91, 0x95), tolerance),

        skinFilter.addColor(new Color(0xA0, 0x88, 0x88), tolerance, lowTolerance, lowTolerance);


        skinFilter.addColor(new Color(0x7B, 0x4B, 0x41), tolerance);

        skinFilter.addColor(new Color(0x65, 0x3D, 0x35), tolerance, highTolerance, highTolerance);

        skinFilter.addColor(new Color(0x4E, 0x2F, 0x2A), tolerance, lowTolerance, lowTolerance);

        //Shadow and Mouth
        skinFilter.addColor(new Color(0x6B, 0x57, 0x60), tolerance);

        //skinFilter.addPointFeatureStrategy(new MinDensityValidation(minDensity));
        //skinFilter.addPointFeatureStrategy(new MaxDensityValidation(maxDensity));
        //skinFilter.addPointFeatureStrategy(new MinPointFeatureDimension(40));//Avoid small noises
        //skinFilter.addPointFeatureStrategy(new CountPointFeaturePoints(180));//Avoid small noises
        //skinFilter.addPointFeatureStrategy(new MaxPointFeatureDimension(w/2));

    }

    private void reset() {

        BufferedImage b = cam.getImage();

        // TODO Change to effect
        //image = new ContrastQuickFilter(20).process(b);
        image = b;

        int w = image.getWidth();
        int h = image.getHeight();

        source.setImage(image);

        screen = new PointFeature(0, 0, w, h);

        //Sampled
        skinFeatures = skinFilter.filter(source, screen);


        //TODO Merge components

        biggestPointFeature = findBiggestPointFeature(skinFeatures);

        //biggestPointFeature = mergePointFeatures(biggestPointFeature, skinFeatures);

    }

    @Override
    public void updateMouse(PointerEvent event) {

        int mx = event.getX();

        int my = event.getY();

        if (!skinFeatures.isEmpty()) {

            for (PointFeature component : skinFeatures) {

                if (component.colidePoint(mx, my)) {

                    overMouse = component;

                }
            }

        }

        if (event.isButtonUp(MouseEvent.MOUSE_BUTTON_LEFT)) {

            if (mx < image.getWidth() && my < image.getHeight()) {

                final int toleranceByClick = 0x10;

                int rgb = image.getRGB(mx, my);

                Color color = new Color(rgb);

                skinFilter.addColor(color, toleranceByClick);

                String redString = Integer.toString(color.getRed(), 16).toUpperCase();
                String greenString = Integer.toString(color.getGreen(), 16).toUpperCase();
                String blueString = Integer.toString(color.getBlue(), 16).toUpperCase();

                System.out.println("skinFilter.addColor(new Color(0x" + redString + ", 0x" + greenString + ", 0x" + blueString + "), 0x10);");

                reset();
            }

        } else if (event.isButtonUp(MouseEvent.MOUSE_BUTTON_RIGHT)) {

            configureSkinFilter();

            reset();
        }
    }

    @Override
    public void updateKeyboard(KeyEvent event) {

        if (event.isKeyDown(KeyEvent.VK_RIGHT)) {
            cam.nextFrame();
            reset();
        } else if (event.isKeyDown(KeyEvent.VK_LEFT)) {
            cam.previousFrame();
            reset();
        }

        if (event.isKeyDown(KeyEvent.VK_H)) {
            hide = !hide;
        }

        if (event.isKeyDown(KeyEvent.VK_P)) {
            pixels = !pixels;
        }

        if (event.isKeyDown(KeyEvent.VK_C)) {
            drawCleanedOnly = !drawCleanedOnly;
        }

        if (event.isKeyDown(KeyEvent.VK_B)) {
            drawBox = !drawBox;
        }
    }

    @Override
    public void draw(Graphics g) {

        g.setColor(Color.BLACK);

        if (!hide) {

            g.drawImage(image, xOffset, yOffset);
            //g.fillRect(0, 0, 640, 480);

        }

        g.setColor(Color.BLUE);
        g.setAlpha(80);


        //g.setLineWidth(2);

        drawFeatures(skinFeatures, g);

        drawFeatureInfo(g);

    }

    private void drawFeatureInfo(Graphics g) {

        g.setColor(Color.BLACK);

        g.setFontSize(20);

        if (overMouse != null) {

            int offsetX = 650;

            g.drawStringShadow("x: " + overMouse.getX(), offsetX, 40);

            g.drawStringShadow("y: " + overMouse.getY(), offsetX, 60);

            g.drawStringShadow("w: " + overMouse.getW(), offsetX, 80);

            g.drawStringShadow("h: " + overMouse.getH(), offsetX, 100);

            g.drawStringShadow("area: " + overMouse.getArea(), offsetX, 120);

            g.drawStringShadow("dens: " + (int) overMouse.getDensity() + "%", offsetX, 140);

            if (map.containsKey(overMouse)) {
                g.drawStringShadow("factor: " + (double) map.get(overMouse), offsetX, 160);
            }

        }

    }

    private void drawFeatures(List<PointFeature> features, Graphics g) {

        //List<PointFeature> features = mergePointFeatures(skinFeatures);

        for (PointFeature component : features) {

            if (component != overMouse) {

                if (component.getDensity() < minDensity) {

                    //g.setColor(Color.HONEYDEW);
                    g.setColor(Color.LEMON_CHIFFON);

                } else if (component.getDensity() > maxDensity) {

                    g.setColor(Color.DARK_GOLDENROD);
                    //g.setColor(SVGColor.LEMON_CHIFFON);

                } else {
                    g.setColor(Color.BLUE_VIOLET);
                }

            } else {

                g.setColor(Color.RED);

            }

            g.drawRect(component.getRectangle());

            //g.setLineWidth(1);

            for (Point2D point : component.getPoints()) {
                g.fillRect((int) point.x, (int) point.y, 1, 1);
            }

        }

        if (biggestPointFeature != null) {

            //g.drawRect(biggestPointFeature.getRectangle());

            drawPirateHat(biggestPointFeature, g);

        }

    }

    private void drawPirateHat(PointFeature face, Graphics g) {


        double angle = drawAndCalculateAngle(face, g);

        double hatScale = 2;//The Pirate Hat has the double of the head width

        double scale = ((double) face.getW() * hatScale / (double) this.w);

        pirateHat.setScale(scale);
        pirateHat.setAngle(angle - 90);

        pirateHat.centralizeX(face.getX(), face.getX() + face.getW());

        pirateHat.setY(face.getY() - (int) ((pirateHat.getH()) * scale));

        pirateHat.draw(g);

    }

    private PointFeature mergePointFeatures(PointFeature biggestPointFeature, List<PointFeature> components) {

        components.remove(biggestPointFeature);

        for (int i = components.size() - 1; i > 0; i--) {

            PointFeature candidate = components.get(i);

            if (biggestPointFeature.colide(candidate)) {

                biggestPointFeature.merge(candidate);

                components.remove(i);

            }

        }

        components.add(0, biggestPointFeature);

        return biggestPointFeature;

    }

    private PointFeature findBiggestPointFeature(List<PointFeature> components) {

        if (components.isEmpty()) {
            return null;
        }

        map.clear();

        PointFeature biggestPointFeature = components.get(0);

        double bestMatch = 0;

        for (int i = 0; i < components.size(); i++) {

            PointFeature candidate = components.get(i);

            double area = candidate.getArea() / 20;

            double density = candidate.getDensity();

            if (density >= 50 || density <= 20) {
                density = density * 0.60;
            }

            double weight = area * density;

            if (weight > bestMatch) {
                biggestPointFeature = candidate;
                bestMatch = weight;
            }

            map.put(candidate, weight);

        }

        return biggestPointFeature;

    }

    private double drawAndCalculateAngle(PointFeature component, Graphics g) {

        int upperPoints = 0, upperX = 0, upperY = 0;

        int lowerPoints = 0, lowerX = 0, lowerY = 0;

        int centerY = component.getY() + component.getH() / 2;

        for (Point2D point : component.getPoints()) {

            //Point lower
            if (point.y > centerY) {
                lowerPoints++;
                lowerX += point.x;
                lowerY += point.y;
            } else {
                upperPoints++;
                upperX += point.x;
                upperY += point.y;
            }

        }

        g.setColor(Color.BLACK);
        g.drawLine(component.getX(), centerY, component.getX() + component.getW(), centerY);

        if (upperPoints > 0 && lowerPoints > 0) {

            Point2D lowerPoint = new Point2D(lowerX / lowerPoints, lowerY / lowerPoints);

            Point2D upperPoint = new Point2D(upperX / upperPoints, upperY / upperPoints);

            g.setColor(Color.BLACK);
            g.fillCircle(lowerPoint, 5);
            g.fillCircle(upperPoint, 5);

            g.setColor(Color.BLUE);
            g.drawCircle(lowerPoint, 5);
            g.drawCircle(upperPoint, 5);

            g.drawLine(lowerPoint, upperPoint);

            return upperPoint.angle(lowerPoint);

        }

        return 0d;

    }

}
