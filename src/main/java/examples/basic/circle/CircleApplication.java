package examples.basic.circle;


import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.classifier.CircleClassifier;
import com.harium.keel.core.source.ImageSource;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.ColorFilter;
import com.harium.etyl.awt.AWTGraphics;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CircleApplication extends Application {

    Color color = Color.BLUE;
    ColorFilter colorFilter;

    BufferedImage drawing;

    List<PointFeature> components;
    Set<PointFeature> circle = new HashSet<>();

    public CircleApplication(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {
        drawing = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        drawScreen(drawing);

        ImageSource source = new BufferedImageSource(drawing);
        Feature screen = new Feature(w, h);

        colorFilter = new ColorFilter(w, h, color);
        components = colorFilter.filter(source, screen);

        CircleClassifier classifier = new CircleClassifier();

        for (PointFeature component : components) {
            if (classifier.classify(component)) {
                circle.add(component);
            }
        }
    }

    private void drawScreen(BufferedImage image) {
        Graphics g = new AWTGraphics(image);
        g.setColor(Color.BLACK);
        g.fillRect(this);

        g.setColor(color);
        g.fillCircle(120, 200, 80);
        g.fillCircle(300, 200, 60);

        g.fillRect(30, 400, 260, 20);
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(drawing, 0, 0);

        for (PointFeature component : components) {
            if (!circle.contains(component)) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GREEN);
            }
            g.drawRect(component.getRectangle());
        }
    }
}
