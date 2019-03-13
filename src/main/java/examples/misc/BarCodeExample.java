package examples.misc;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.FakeCamera;
import com.harium.keel.custom.BarCodeFilter;
import com.harium.keel.feature.PointFeature;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class BarCodeExample extends Application {

    private FakeCamera cam = new FakeCamera();
    private BufferedImageSource source = new BufferedImageSource();

    private BarCodeFilter filter = new BarCodeFilter((int) w, (int) h);

    private boolean hide = false;
    private boolean pixels = true;

    private int xOffset = 40;
    private int yOffset = 40;

    private List<PointFeature> result;

    private PointFeature screen;

    public BarCodeExample(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {

        screen = new PointFeature(0, 0, w, h);

        filter.getSearchStrategy().setBorder(2);

        loadingInfo = "Loading Images";

        cam.addImage("wand/wand6.png");

        loading = 25;
        loadingInfo = "Configuring Filter";

        filter = new BarCodeFilter(cam.getWidth(), cam.getHeight());

        reset(cam.getImage());

        loading = 100;
    }

    private void reset(BufferedImage b) {

        loading = 65;
        loadingInfo = "Show Result";

        source.setImage(b);
        result = filter.filter(source, screen);

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

        g.drawImage(cam.getImage(), xOffset, yOffset + 200);

        int offset = 1;
        for (PointFeature feature : result) {

            drawBox(g, feature, offset % 2 * 20);

            offset++;
        }
    }

    private void drawBox(Graphics g, PointFeature box, int downOffset) {

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

        g.drawString(Integer.toString((int) (d.x - a.x)), xOffset + (int) d.x - 12, yOffset + (int) d.y + 20 + downOffset);

    }

    private void drawLine(Graphics g, Point2D a, Point2D b) {
        g.drawLine(xOffset + (int) a.x, yOffset + (int) a.y, xOffset + (int) b.x, yOffset + (int) b.y);
    }

    private void drawPoint(Graphics g, Point2D point) {
        g.fillCircle(xOffset + (int) point.x, yOffset + (int) point.y, 3);
    }

}
