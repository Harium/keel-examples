package examples.medium.application;

import com.badlogic.gdx.math.Vector2;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.layer.BufferedLayer;
import com.harium.keel.awt.PolygonHelper;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.feature.hull.HullFeature;
import com.harium.keel.filter.process.AverageColorFilter;
import com.harium.keel.filter.track.TrackingByDarkerColorFilter;
import com.harium.keel.filter.validation.point.MaxDimensionValidation;
import com.harium.keel.filter.validation.point.MinDensityValidation;
import com.harium.keel.filter.validation.point.MinDimensionValidation;
import com.harium.keel.modifier.hull.FastConvexHullModifier;

import java.awt.image.BufferedImage;
import java.util.List;

public class MelanomaFinderApplication extends Application {

    private BufferedImage buffer;
    private BufferedImageSource source = new BufferedImageSource();

    private TrackingByDarkerColorFilter skinFilter;

    private PointFeature screen;

    private PointFeature biggestPointFeature;

    private List<PointFeature> candidates;
    private List<Vector2> list;

    private Color averageSkinColor;

    private HullFeature convexHull;

    private boolean hide = false;

    private Color avgBiggestPointFeatureColor;

    public MelanomaFinderApplication(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {

        //Load the image with elements
        buffer = new BufferedLayer("melanoma/melanoma1.png").getBuffer();
        source.setImage(buffer);

        //Process image to calculate it's Average Color
        AverageColorFilter avgColorFilter = new AverageColorFilter();

        averageSkinColor = avgColorFilter.process(source);

        int width = buffer.getWidth();
        int height = buffer.getHeight();

        //Define the area to search for elements
        screen = new PointFeature(0, 0, width, height);

        //Define skin filter
        skinFilter = new TrackingByDarkerColorFilter(w, h, averageSkinColor, 80);

        //Define validations
        skinFilter.addValidation(new MinDensityValidation(50)); //PointFeatures must have at least 50% of pixel density
        skinFilter.addValidation(new MinDimensionValidation(20)); //PointFeatures should be bigger than 20x20px
        skinFilter.addValidation(new MaxDimensionValidation(width / 2)); //PointFeatures should be smaller than (width/2)x(width/2)px

        loading = 80;

        loadingInfo = "Start Filter";

        //Search for melanoma candidates
        candidates = skinFilter.filter(source, screen);

        //Find the biggest component/candidate
        biggestPointFeature = findBiggestPointFeature(candidates);

        FastConvexHullModifier convexHullModifier = new FastConvexHullModifier();

        //Apply QuickHull Modifier in the biggest component
        convexHull = convexHullModifier.apply(biggestPointFeature);
        list = convexHull.asList();

        //Creates a new avgColorFilter
        avgColorFilter = new AverageColorFilter();

        //Calculate melonama's average color
        avgBiggestPointFeatureColor = avgColorFilter.process(source, biggestPointFeature);

        loadingInfo = "Filter Complete";
    }

    private PointFeature findBiggestPointFeature(List<PointFeature> components) {

        PointFeature biggestPointFeature = candidates.get(0);

        int biggestArea = 0;

        for (int i = 0; i < candidates.size(); i++) {

            PointFeature candidate = candidates.get(i);

            if (candidate.getArea() > biggestArea) {
                biggestPointFeature = candidate;
                biggestArea = candidate.getArea();
            }
        }

        return biggestPointFeature;
    }

    @Override
    public void draw(Graphics g) {
        g.setAlpha(100);
        g.drawImage(buffer, 0, 0);

        g.setAlpha(50);

        //Draw a black rectangle around the skin components
        for (PointFeature candidate : candidates) {
            g.setColor(Color.BLACK);
            g.drawPolygon(PolygonHelper.getBoundingBox(candidate));

            if (!hide) {
                drawPointFeaturePixels(g, candidate);
            }
        }

        //Draw Biggest PointFeature
        g.setColor(Color.BLUE);
        g.drawPolygon(PolygonHelper.getBoundingBox(biggestPointFeature));

        if (!hide) {
            g.setAlpha(50);

            drawPointFeaturePixels(g, biggestPointFeature);
            drawConvexHullMask(g, biggestPointFeature);
        }

        g.setAlpha(100);

        //Draw Information in the top-left corner

        //Draw average skin color Rectangle
        g.setColor(averageSkinColor);
        g.fillRect(0, 40, 40, 30);

        g.setColor(Color.WHITE);
        g.drawStringShadow("← avg skin color", 45, 60);

        //Draw average melanoma color rectangle
        g.setColor(avgBiggestPointFeatureColor);
        g.fillRect(0, 86, 40, 30);

        g.setColor(Color.WHITE);
        g.drawStringShadow("← avg melanoma color", 45, 106);

        //How to show/hide pixel mask
        g.drawStringX("Press H to show/hide colored pixels", 380);

    }

    private void drawPointFeaturePixels(Graphics g, PointFeature component) {

        for (Point2D point : component.getPoints()) {
            g.fillRect((int) point.x, (int) point.y, 1, 1);
        }
    }

    private void drawConvexHullMask(Graphics g, PointFeature component) {

        Point2D centroid = component.getCenter();

        for (Vector2 point : list) {
            g.drawLine(point.x, point.y, (float) centroid.x, (float) centroid.y);

            g.setColor(Color.RED);
            g.fillCircle(point.x, point.y, 5);
            g.setColor(Color.BLACK);
            g.drawCircle(point.x, point.y, 5);
        }

        g.setColor(Color.GHOST_WHITE);
        g.drawPolygon(PolygonHelper.getBoundingBox(convexHull));
    }

    @Override
    public void updateKeyboard(KeyEvent event) {
        if (event.isKeyDown(KeyEvent.VK_H)) {
            hide = !hide;
        }
    }

}
