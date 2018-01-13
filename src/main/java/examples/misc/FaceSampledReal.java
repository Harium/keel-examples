package examples.misc;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.linear.Point2D;
import com.harium.keel.awt.camera.CameraV4L4J;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.color.RGBColorStrategy;
import com.harium.keel.filter.search.CrossSearch;
import com.harium.keel.modifier.hull.FastConvexHullModifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class FaceSampledReal extends Application {

    private CameraV4L4J cam;
    private BufferedImageSource source = new BufferedImageSource();

    private CrossSearch colorFilter = new CrossSearch();

    private FastConvexHullModifier quickHull = new FastConvexHullModifier();

    private boolean hide = false;
    private boolean pixels = true;

    private int xOffset = 0;
    private int yOffset = 0;

    private PointFeature sampledFeature;

    private Polygon sampledPolygon = new Polygon();

    private Feature screen;

    public FaceSampledReal(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {

        loadingInfo = "Loading Images";

        cam = new CameraV4L4J(0);

        screen = new Feature(cam.getBufferedImage().getWidth(), cam.getBufferedImage().getHeight());

        RGBColorStrategy colorStrategy = new RGBColorStrategy(Color.BLACK);
        colorFilter.setSelectionStrategy(colorStrategy);

        final int MAGIC_NUMBER = 3;//Higher = Faster and less precise

        colorFilter.setBorder(MAGIC_NUMBER);
        colorFilter.setStep(MAGIC_NUMBER);

        loadingInfo = "Configuring Filter";

        loading = 60;
        reset(cam.getBufferedImage());

        loading = 100;
    }

    private void reset(BufferedImage b) {
        source.setImage(b);
        //Sampled
        sampledFeature = colorFilter.filter(source, screen).get(0);

        sampledPolygon.reset();

        //TODO Separate polygons
        List<Point2D> points = quickHull.modify(sampledFeature).getPoints();

        for (Point2D point : points) {
            sampledPolygon.addPoint((int) point.getX(), (int) point.getY());
        }
    }

    private List<PointFeature> separatePointFeatures(PointFeature feature) {

        List<PointFeature> result = new ArrayList<PointFeature>();

        List<Point2D> points = new ArrayList<Point2D>(feature.getPoints());

        PointFeature currentPointFeature = new PointFeature(0, 0);

        currentPointFeature.add(points.get(0));

        int p = 1;

        Point2D pt = points.get(p);

        final int radius = 20;

        //while(points.size()>0){

        for (int i = 1; i < points.size(); i++) {

            Point2D q = points.get(i);

            if (insideCircle((int) pt.getX(), (int) pt.getY(), radius, (int) q.getX(), (int) q.getY())) {
                currentPointFeature.add(q);
                points.remove(i);
                continue;
            }

        }

        //}


        return result;


    }

    private boolean insideCircle(int cx, int cy, int radius, int px, int py) {

        float difX = (x - cx) * (x - cx);
        float difY = (x - cx) * (x - cx);

        return difX + difY < radius * radius;

    }

    @Override
    public void updateKeyboard(KeyEvent event) {

        if (event.isKeyDown(KeyEvent.VK_H)) {
            hide = !hide;
        }

        if (event.isKeyDown(KeyEvent.VK_P)) {
            pixels = !pixels;
        }
    }

    @Override
    public void draw(Graphics g) {

        if (!hide) {
            g.drawImage(cam.getBufferedImage(), xOffset, yOffset);
        }

        reset(cam.getBufferedImage());

        g.setAlpha(60);
        //drawFeaturedPoints(g, sampledFeature, Color.GREEN);
        g.setAlpha(100);

        g.setColor(Color.GREEN);
        g.drawPolygon(sampledPolygon);

    }

    private void drawFeaturedPoints(Graphics g, PointFeature feature, Color color) {

        for (Point2D ponto : feature.getPoints()) {

            g.setColor(color);
            g.fillCircle(xOffset + (int) ponto.getX(), yOffset + (int) ponto.getY(), 5);

            //g.setColor(Color.WHITE);
            //g.drawCircle(xOffset+(int)ponto.getX(), yOffset+(int)ponto.getY(), 18);

        }

    }

}
