package examples.misc;

import com.harium.keel.awt.camera.FakeCamera;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.core.strategy.ComponentModifierStrategy;
import com.harium.keel.feature.Component;
import com.harium.keel.filter.color.ColorStrategy;
import com.harium.keel.filter.search.CornerSearch;
import com.harium.keel.modifier.PositCoplanarModifier;
import com.harium.keel.modifier.hull.AugmentedMarkerModifier;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.linear.Point2D;

import java.awt.image.BufferedImage;

public class AugmentedRealityStatic extends Application {

    private FakeCamera cam;
    private BufferedImageSource source = new BufferedImageSource();

    private CornerSearch cornerFilter;

    private ColorStrategy colorStrategy;

    private ComponentModifierStrategy modifier;

    private PositCoplanarModifier positModifier;

    private boolean hide = false;
    private boolean pixels = true;

    private int xOffset = 40;
    private int yOffset = 40;

    private Component feature;

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

        int width = cam.getBufferedImage().getWidth();

        int height = cam.getBufferedImage().getHeight();

        loading = 40;

        colorStrategy = new ColorStrategy(Color.BLACK);
        colorStrategy.setTolerance(0x30);

        modifier = new AugmentedMarkerModifier();

        //modifier = new JarvisMarchModifier();

        positModifier = new PositCoplanarModifier(width, height);

        cornerFilter = new CornerSearch(width, height);

        cornerFilter.setBorder(10);
        cornerFilter.setStep(1);

        cornerFilter.setPixelStrategy(colorStrategy);

        cornerFilter.setComponentModifierStrategy(modifier);

        feature = new Component(0, 0, w, h);

        reset(cam.getBufferedImage());

        loading = 100;
    }

    private void reset(BufferedImage b) {

        loading = 60;

        loadingInfo = "Start Filter";

        source.setImage(b);
        feature = cornerFilter.filterFirst(source, new Component(0, 0, w, h));

        positModifier.modifyComponent(feature);

        loading = 65;
        loadingInfo = "Show Result";

        loading = 70;
        loadingInfo = "Show Angle";
    }

    @Override
    public void updateKeyboard(KeyEvent event) {

        if (event.isKeyDown(KeyEvent.VK_RIGHT)) {
            cam.nextFrame();
            reset(cam.getBufferedImage());
        } else if (event.isKeyDown(KeyEvent.VK_LEFT)) {
            cam.previousFrame();
            reset(cam.getBufferedImage());
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

        g.drawImage(cam.getBufferedImage(), xOffset, yOffset);

        g.setColor(Color.BLUE);

        for (Point2D ponto : feature.getPoints()) {
            g.fillCircle(xOffset + (int) ponto.getX(), yOffset + (int) ponto.getY(), 5);
        }

        int textHeight = 25;

        if (feature.getPoints().size() > 3) {

            drawBox(g, feature);

            g.drawString("Points = " + feature.getPoints().size(), 50, textHeight + 25);

            g.drawString("Angle = " + positModifier.getAxis().getAngle(), 50, textHeight + 50);

            g.drawString("AxisX = " + positModifier.getAxis().getRotationX(), 50, textHeight + 75);

            g.drawString("AxisY = " + positModifier.getAxis().getRotationY(), 50, textHeight + 100);

            g.drawString("AxisZ = " + positModifier.getAxis().getRotationZ(), 50, textHeight + 125);

        }

    }

    private void drawBox(Graphics g, Component box) {

        g.setColor(Color.RED);

        Point2D a = box.getPoints().get(0);
        Point2D b = box.getPoints().get(1);
        Point2D c = box.getPoints().get(2);
        Point2D d = box.getPoints().get(3);

        Point2D ac = new Point2D((a.getX() + c.getX()) / 2, (a.getY() + c.getY()) / 2);
        Point2D ab = new Point2D((a.getX() + b.getX()) / 2, (a.getY() + b.getY()) / 2);

        Point2D bd = new Point2D((b.getX() + d.getX()) / 2, (b.getY() + d.getY()) / 2);
        Point2D cd = new Point2D((c.getX() + d.getX()) / 2, (c.getY() + d.getY()) / 2);

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
        g.drawString("A", xOffset + (int) a.getX() - 20, yOffset + (int) a.getY() - 10);
        g.drawString("B", xOffset + (int) b.getX() + 15, yOffset + (int) b.getY() - 10);

        g.drawString("C", xOffset + (int) c.getX() - 20, yOffset + (int) c.getY() + 10);
        g.drawString("D", xOffset + (int) d.getX() + 15, yOffset + (int) d.getY() + 10);

    }

    private void drawLine(Graphics g, Point2D a, Point2D b) {
        g.drawLine(xOffset + (int) a.getX(), yOffset + (int) a.getY(), xOffset + (int) b.getX(), yOffset + (int) b.getY());
    }

    private void drawPoint(Graphics g, Point2D point) {
        g.fillCircle(xOffset + (int) point.getX(), yOffset + (int) point.getY(), 3);
    }

}