package examples.basic.subcomponent;

import com.harium.etyl.awt.AWTGraphics;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.keel.awt.PolygonHelper;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.feature.hull.HullFeature;
import com.harium.keel.filter.ColorFilter;
import com.harium.keel.filter.validation.point.MaxDimensionValidation;
import com.harium.keel.modifier.hull.FastConvexHullModifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubcomponentApplication extends Application {

    private BufferedImage image;
    private BufferedImageSource source = new BufferedImageSource();

    private ColorFilter whiteFilter;
    private ColorFilter blackFilter;

    private FastConvexHullModifier modifier;

    private Feature screen;

    private List<PointFeature> whitePointFeatures;
    private Map<Integer, List<PointFeature>> subPointFeatures;

    public SubcomponentApplication(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {

        subPointFeatures = new HashMap<Integer, List<PointFeature>>();

        //Define the area to search for elements
        screen = new PointFeature(0, 0, w, h);

        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        //Create the image with elements
        createImage(image);
        source.setImage(image);

        //Define white and black filters
        whiteFilter = new ColorFilter(w, h, Color.WHITE);
        whiteFilter.addValidation(new MaxDimensionValidation(w / 2));

        blackFilter = new ColorFilter(w, h, Color.BLACK);

        //Filter the image
        whitePointFeatures = whiteFilter.filter(source, screen);

        modifier = new FastConvexHullModifier();

        HullFeature hull = modifier.modify(whitePointFeatures.get(0));

        List<PointFeature> sub = blackFilter.filter(source, hull);

        subPointFeatures.put(0, sub);
    }

    private void createImage(BufferedImage image) {

        Graphics g = new AWTGraphics(image);

        g.setColor(Color.WHITE);

        g.fillRect(0, 0, w, h);

        g.setColor(Color.BLACK);

        g.setLineWidth(8);

        g.drawLine(40, 40, 140, 40);
        g.drawLine(40, 40, 40, 100);
        g.drawLine(140, 40, 40, 100);

        g.drawOval(180, 40, 100, 80);

        g.drawRect(300, 200, 80, 80);

        //Draw Subcomponents
        g.setLineWidth(1);
        g.fillCircle(60, 60, 5);
        g.fillCircle(80, 60, 5);

    }

    @Override
    public void draw(Graphics g) {
        g.setAlpha(100);
        g.drawImage(image, 0, 0);

        g.setAlpha(90);

        //Count subPointFeatures
        for (PointFeature component : whitePointFeatures) {
            //Draw a red line around the hull of white components
            g.setStroke(new BasicStroke(3f));
            g.setColor(Color.RED);
            g.drawPolygon(PolygonHelper.getBoundingBox(component));
        }

        for (List<PointFeature> list : subPointFeatures.values()) {
            for (PointFeature subcomponent : list) {
                g.setStroke(new BasicStroke(3f));
                g.setColor(Color.BLUE_VIOLET);
                g.drawPolygon(PolygonHelper.getBoundingBox(subcomponent));
            }
        }

    }

}
