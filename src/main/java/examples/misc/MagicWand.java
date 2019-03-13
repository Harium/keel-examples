package examples.misc;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.context.UpdateIntervalListener;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.event.MouseEvent;
import com.harium.etyl.commons.event.PointerEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.layer.BufferedLayer;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.Camera;
import com.harium.keel.camera.Webcam;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.color.RGBColorStrategy;
import com.harium.keel.filter.search.flood.FloodFillSearch;
import com.harium.keel.modifier.EnvelopeModifier;

import java.awt.image.BufferedImage;

public class MagicWand extends Application implements UpdateIntervalListener {

    private Camera cam;
    private BufferedImageSource source = new BufferedImageSource();

    private FloodFillSearch cornerFilter;

    private RGBColorStrategy colorStrategy;

    private EnvelopeModifier modifier;

    private boolean hide = false;
    private boolean pixels = true;

    private int xOffset = 40;
    private int yOffset = 40;

    private PointFeature feature;

    private BufferedLayer mirror;

    public MagicWand(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {

        loadingInfo = "Loading Images";

        cam = new Webcam();

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

        feature = new PointFeature(0, 0, w, h);

        mirror = new BufferedLayer(0, 0);

        reset(cam.getImage());

        updateAtFixedRate(20, this);

        loading = 100;
    }

    @Override
    public void timeUpdate(long now) {

        //Get the Camera image
        mirror.setBuffer(cam.getImage());

        //Normally the camera shows the image flipped, but we want to see something like a mirror
        //So we flip the image
        mirror.flipHorizontal();

        reset(mirror.getBuffer());
    }

    private void reset(BufferedImage b) {

        loading = 60;

        loadingInfo = "Start Filter";
        source.setImage(b);

        feature = cornerFilter.filterFirst(source, new PointFeature(0, 0, w, h));

        loading = 65;
        loadingInfo = "Show Result";

        loading = 70;
        loadingInfo = "Show Angle";
    }

    @Override
    public void updateMouse(PointerEvent event) {

        if (event.isButtonUp(MouseEvent.MOUSE_BUTTON_LEFT)) {
            //When mouse clicks with LeftButton, the color filter tries to find
            //the color we are clicking on
            colorStrategy.setColor(mirror.getBuffer().getRGB((int) event.getX(), (int) event.getY()));

        }
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

        mirror.draw(g);

        g.setColor(Color.BLUE);

        for (Point2D point : feature.getPoints()) {
            g.fillCircle(xOffset + (int) point.x, yOffset + (int) point.y, 5);
        }

        if (feature.getPoints().size() > 3) {

            drawBox(g, feature);

            g.drawString("Angle = " + modifier.getAngle(), 50, 25);

            g.drawString("Points = " + feature.getPoints().size(), 50, 50);

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
