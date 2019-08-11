package examples.modifiers.edge;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.loader.image.ImageLoader;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.core.source.ImageSource;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.modifier.edge.CannyEdgeModifier;
import com.harium.keel.modifier.edge.EdgeModifier;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class CannyEdgeApplication extends Application {

    int offsetY = 37;

    private PointFeature screen;
    private ImageSource source;
    private EdgeModifier modifier;

    private List<Point2D> result = new ArrayList<>();

    public CannyEdgeApplication(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {
        BufferedImage image = ImageLoader.getInstance().getImage("lena.jpg");
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
            g.fillRect((int) point.x, (int) point.y + offsetY, 1, 1);
        }
    }

}
