package examples.modifiers.edge;

import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.core.source.ImageSource;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.modifier.edge.CannyEdgeModifier;
import com.harium.keel.modifier.edge.EdgeModifier;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.linear.Point2D;
import com.harium.etyl.loader.image.ImageLoader;

import java.awt.image.BufferedImage;
import java.util.List;

public class CannyEdgeApplication extends Application {

    private PointFeature screen;
    private ImageSource source;
    private EdgeModifier modifier;

    private List<Point2D> result;

    public CannyEdgeApplication(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {
        BufferedImage image = ImageLoader.getInstance().getImage("hand/dorso.jpg");
        source = new BufferedImageSource(image);
        screen = new PointFeature(0, 0, image.getWidth(), image.getHeight());

        modifier = new CannyEdgeModifier();
        applyModifier();
    }

    private void applyModifier() {
        result = modifier.modify(source, screen);
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(this);

        g.setColor(Color.WHITE);
        for (Point2D point : result) {
            g.fillRect((int) point.getX(), (int) point.getY(), 1, 1);
        }
    }

}
