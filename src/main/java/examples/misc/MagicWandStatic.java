package examples.misc;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.FakeCamera;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.color.RGBColorStrategy;
import com.harium.keel.filter.search.flood.FloodFillSearch;
import com.harium.keel.modifier.EnvelopeModifier;

import java.awt.image.BufferedImage;
import java.util.List;

public class MagicWandStatic extends Application {

    private FakeCamera cam;
    private BufferedImageSource source = new BufferedImageSource();

    private FloodFillSearch cornerFilter;

    private RGBColorStrategy colorStrategy;

    private EnvelopeModifier modifier;

    private boolean hide = false;
    private boolean pixels = true;

    private int xOffset = 40;
    private int yOffset = 40;

    private final int IMAGES_TO_LOAD = 7;

    private List<PointFeature> features;

    public MagicWandStatic(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {

        loadingInfo = "Loading Images";

        cam = new FakeCamera();

        for (int i = 0; i < IMAGES_TO_LOAD; i++) {
            cam.addImage("/wand/wand" + Integer.toString(i) + ".png");
        }

        loading = 25;

        loadingInfo = "Configuring Filter";

        int width = cam.getWidth();
        int height = cam.getHeight();

        loading = 40;

        colorStrategy = new RGBColorStrategy(Color.BLACK);
        colorStrategy.setTolerance(0x10);

        modifier = new EnvelopeModifier();

        cornerFilter = new FloodFillSearch(width, height);
        cornerFilter.setBorder(10);

        cornerFilter.setSelectionStrategy(colorStrategy);

        cornerFilter.setComponentModifierStrategy(modifier);

        reset(cam.getImage());

        loading = 100;
    }

    private void reset(BufferedImage b) {

        loading = 60;

        loadingInfo = "Start Filter";

        source.setImage(b);

        features = cornerFilter.filter(source, new Feature(w, h));

        loading = 65;
        loadingInfo = "Show Result";

        loading = 70;
        loadingInfo = "Show Angle";
    }

    @Override
    public void updateKeyboard(KeyEvent event) {

        if (event.isKeyDown(KeyEvent.VK_RIGHT_ARROW)) {
            cam.nextFrame();
            reset(cam.getImage());
        } else if (event.isKeyDown(KeyEvent.VK_LEFT_ARROW)) {
            cam.previousFrame();
            reset(cam.getImage());
        }

        if (event.isKeyDown(KeyEvent.VK_H)) {
            hide = !hide;
        }

        if (event.isKeyDown(KeyEvent.VK_P)) {
            pixels = !pixels;
        }
    }

    @Override
    public void draw(Graphics g) {

        g.drawImage(cam.getImage(), xOffset, yOffset);

        g.setColor(Color.BLACK);

        g.drawString("Angle = " + modifier.getAngle(), 50, 25);

        g.setColor(Color.BLUE);

        for (PointFeature feature : features) {
            for (Point2D point : feature.getPoints()) {
                g.fillCircle(xOffset + (int) point.x, yOffset + (int) point.y, 5);
            }

            if (feature.getPoints().size() > 3) {

                drawBox(g, feature);

                g.drawString("Points = " + feature.getPoints().size(), 50, 50);
            }
        }
    }

    private void drawBox(Graphics g, PointFeature box) {

        g.setColor(Color.RED);

        Point2D a = box.getPoints().get(0);
        Point2D b = box.getPoints().get(1);
        Point2D c = box.getPoints().get(2);
        Point2D d = box.getPoints().get(3);

        Point2D ac = new Point2D((a.x + c.x) / 2, (a.y + c.y) / 2);
        Point2D ab = new Point2D((a.x + b.x) / 2, (a.y + b.y) / 2);

        Point2D bd = new Point2D((b.x + d.x) / 2, (b.y + d.y) / 2);
        Point2D cd = new Point2D((c.x + d.x) / 2, (c.y + d.y) / 2);

        drawLine(g, a, b);
        drawLine(g, a, c);

        drawLine(g, b, d);
        drawLine(g, c, d);

        drawPoint(g, a);
        drawPoint(g, b);
        drawPoint(g, c);
        drawPoint(g, d);

        g.setColor(Color.YELLOW);
        drawLine(g, ab, cd);
        drawPoint(g, ab);
        drawPoint(g, cd);

        g.setColor(Color.GREEN);
        drawLine(g, ac, bd);

        drawPoint(g, ac);
        drawPoint(g, bd);


        g.setColor(Color.BLACK);
        g.drawString("A", xOffset + (int) a.x - 20, yOffset + (int) a.y - 10);
        g.drawString("B", xOffset + (int) b.x + 15, yOffset + (int) b.y - 10);

        g.drawString("C", xOffset + (int) c.x - 20, yOffset + (int) c.y + 10);
        g.drawString("D", xOffset + (int) d.x + 15, yOffset + (int) d.y + 10);

    }

    private void drawLine(Graphics g, Point2D a, Point2D b) {
        g.drawLine(xOffset + (int) a.x, yOffset + (int) a.y, xOffset + (int) b.x, yOffset + (int) b.y);
    }

    private void drawPoint(Graphics g, Point2D point) {
        g.fillCircle(xOffset + (int) point.x, yOffset + (int) point.y, 3);
    }


}
