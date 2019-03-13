package examples.misc;

import com.badlogic.gdx.math.Quaternion;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.FakeCamera;
import com.harium.keel.core.strategy.ComponentModifierStrategy;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.color.RGBColorStrategy;
import com.harium.keel.filter.search.CornerSearch;
import com.harium.keel.modifier.PositCoplanarModifier;
import com.harium.keel.modifier.hull.AugmentedMarkerModifier;

import java.awt.image.BufferedImage;

public class AugmentedRealityStatic extends Application {

    private FakeCamera cam;
    private BufferedImageSource source = new BufferedImageSource();

    private CornerSearch cornerFilter;

    private RGBColorStrategy colorStrategy;

    private ComponentModifierStrategy modifier;

    private PositCoplanarModifier positModifier;

    private boolean hide = false;
    private boolean pixels = true;

    private int xOffset = 40;
    private int yOffset = 40;

    private PointFeature feature;

    public AugmentedRealityStatic(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {
        loadingInfo = "Loading Images";

        cam = new FakeCamera();

        cam.addImage("reality/30angle.png");
        cam.addImage("reality/45angle.png");
        cam.addImage("reality/60angle.png");
        cam.addImage("reality/5015angle.png");
        cam.addImage("reality/6030angle.png");

        loading = 25;

        loadingInfo = "Configuring Filter";

        int width = cam.getWidth();
        int height = cam.getHeight();

        loading = 40;

        colorStrategy = new RGBColorStrategy(Color.BLACK);
        colorStrategy.setTolerance(0x30);

        modifier = new AugmentedMarkerModifier();

        //modifier = new JarvisMarchModifier();

        positModifier = new PositCoplanarModifier(width, height);

        cornerFilter = new CornerSearch(width, height);

        cornerFilter.setBorder(10);
        cornerFilter.setStep(1);

        cornerFilter.setSelectionStrategy(colorStrategy);

        cornerFilter.setComponentModifierStrategy(modifier);

        feature = new PointFeature(0, 0, w, h);

        reset(cam.getImage());

        loading = 100;
    }

    private void reset(BufferedImage b) {

        loading = 60;

        loadingInfo = "Start Filter";

        source.setImage(b);
        feature = cornerFilter.filterFirst(source, new Feature(w, h));

        positModifier.modifyComponent(feature);

        loading = 65;
        loadingInfo = "Show Result";

        loading = 70;
        loadingInfo = "Show Angle";
    }

    @Override
    public void updateKeyboard(KeyEvent event) {

        if (event.isKeyDown(KeyEvent.VK_RIGHT)) {
            reset(cam.nextFrame());
        } else if (event.isKeyDown(KeyEvent.VK_LEFT)) {
            reset(cam.previousFrame());
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

        g.setColor(Color.BLUE);

        for (Point2D point : feature.getPoints()) {
            g.fillCircle(xOffset + (int) point.x, yOffset + (int) point.y, 5);
        }

        int textHeight = 25;

        if (feature.getPoints().size() > 3) {

            drawBox(g, feature);

            g.drawString("Points = " + feature.getPoints().size(), 50, textHeight + 25);

            Quaternion quaternion = positModifier.getAxis().calculateQuaternion();
            g.drawString("Pitch = " + quaternion.getPitch(), 50, textHeight + 50);
            g.drawString("Yaw = " + quaternion.getYaw(), 50, textHeight + 75);
            g.drawString("Roll = " + quaternion.getRoll(), 50, textHeight + 100);

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

        g.setColor(Color.ORANGE);
        drawPoint(g, box.getCenter());

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
