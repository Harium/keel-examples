package examples.misc;

import com.github.sarxos.webcam.Webcam;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.event.MouseEvent;
import com.harium.etyl.commons.event.PointerEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.custom.gesture.GestureRegex;
import com.harium.keel.custom.gesture.PolygonMatcher;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.search.ColoredPointSearch;
import com.harium.keel.filter.selection.RGBColorStrategy;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class AirWrite extends Application {

    private Webcam cam;
    private BufferedImageSource source = new BufferedImageSource();

    private ColoredPointSearch colorFilter;

    private RGBColorStrategy colorStrategy;

    private final int NUMBER_OF_POINTS = 45;

    private List<Point2D> points;

    private boolean hide = false;
    private boolean pixels = true;
    private boolean freeze = false;

    private int xImage = 0;
    private int yImage = 0;

    private String match = "_";

    private PointFeature component = new PointFeature((int) w, (int) h);

    private PolygonMatcher matcher = new PolygonMatcher();

    private BufferedImage mirror;

    public AirWrite(int w, int h) {
        super(w, h);
    }

    private Feature screen;

    @Override
    public void load() {

        loadingInfo = "Open Camera";

        cam = Webcam.getDefault();
        int width = cam.getViewSize().width;
        int height = cam.getViewSize().height;

        screen = new Feature(0, 0, width, height);

        loadingInfo = "Setting PolygonMatcher";
        matcher.setMinDistance(8);

        loadingInfo = "Setting Filter";

        colorStrategy = new RGBColorStrategy(Color.BLACK);

        colorFilter = new ColoredPointSearch(width, height, Color.BLACK);
        colorFilter.setBorder(95);
        colorFilter.setSelectionStrategy(colorStrategy);

        points = component.getPoints();

        for (int i = 0; i < NUMBER_OF_POINTS; i++) {
            points.add(new Point2D(0, 0));
        }

        loading = 100;
    }

    private void reset(BufferedImage b) {
        source.setImage(b);

        PointFeature point = colorFilter.filterFirst(source, screen);

        if (!freeze) {

            if (point != null) {

                Point2D firstPoint = points.get(0);

                firstPoint.setLocation(point.getX(), point.getY());

                Collections.rotate(points, -1);
            }
        }

    }

    @Override
    public void updateMouse(PointerEvent event) {
        if (event.isButtonUp(MouseEvent.MOUSE_BUTTON_LEFT)) {
            colorStrategy.setColor(mirror.getRGB(event.getX(), event.getY()));
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

        if (event.isKeyDown(KeyEvent.VK_SPACE)) {
            freeze = !freeze;
        }

        if (event.isKeyDown(KeyEvent.VK_ESC)) {
            match = "_";
        }

        if (event.isKeyDown(KeyEvent.VK_EQUALS)) {
            colorStrategy.setOffsetTolerance(+1);
        }

        if (event.isKeyDown(KeyEvent.VK_MINUS)) {
            colorStrategy.setOffsetTolerance(-1);
        }

        if (event.isKeyDown(KeyEvent.VK_UP_ARROW)) {
            colorFilter.setBorder(colorFilter.getBorder() + 1);
            System.out.println("Border: " + colorFilter.getBorder());
        }

        if (event.isKeyDown(KeyEvent.VK_DOWN_ARROW)) {
            colorFilter.setBorder(colorFilter.getBorder() - 1);
        }

    }

    @Override
    public void draw(Graphics g) {

        BufferedImage b = cam.getImage();

        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);

        tx.translate(-b.getWidth(), 0);

        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

        mirror = op.filter(b, null);

        reset(mirror);

        //reset(b);

        g.drawImage(mirror, xImage, yImage);

        g.setColor(colorStrategy.getColor());
        g.fillRect(0, 0, 30, 30);
        g.setColor(Color.WHITE);
        g.drawStringShadow(Integer.toString(colorStrategy.getMaxToleranceRed()), 10, 15);

        int border = colorFilter.getBorder();

        g.drawRect(border, border, mirror.getWidth() - border * 2, mirror.getHeight() - border * 2);

        g.setColor(Color.WHITE);

        for (Point2D point : points) {

            g.setColor(Color.WHITE);
            g.fillCircle(xImage + (int) point.x, yImage + (int) point.y, 5);
            g.setColor(Color.BLACK);
            g.drawCircle(xImage + (int) point.x, yImage + (int) point.y, 5);

        }

        String regex = matcher.toRegExp(points);

        if (checkRegex(regex)) {
            resetPoints();
        }


        g.drawStringShadow(regex, 20, 20);

        g.drawStringShadow(match, 50, 50);

    }

    private void resetPoints() {

        for (Point2D point : points) {
            point.setLocation(0, 0);
        }

    }

    private boolean checkRegexNumber(String regex) {

        if (regex.matches(GestureRegex.ONE)) {
            match += "1";
            return true;
        }

        if (regex.matches(GestureRegex.TWO)) {
            match += "2";
            return true;
        }

        if (regex.matches(GestureRegex.THREE)) {
            match += "3";
            return true;
        }

        if (regex.matches(GestureRegex.FOUR)) {
            match += "4";
            return true;
        }

        if (regex.matches(GestureRegex.FIVE)) {
            match += "5";
            return true;
        }

        if (regex.matches(GestureRegex.SIX)) {
            match += "6";
            return true;
        }

        if (regex.matches(GestureRegex.SEVEN)) {
            match += "7";
            return true;
        }

        if (regex.matches(GestureRegex.EIGHT)) {
            match += "8";
            return true;
        }

        if (regex.matches(GestureRegex.NINE) || regex.matches(GestureRegex.NINE_CCW)) {
            match += "9";
            return true;
        }

        return false;

    }


    private boolean checkRegex(String regex) {

        if (checkRegexNumber(regex)) {
            return true;
        }

        if (regex.matches(GestureRegex.PLUS) || regex.matches(GestureRegex.PLUS_LEFT_HANDED)) {
            match += "+";
            return true;
        }

        if (regex.matches(GestureRegex.RIGHT_ARROW)) {
            match += "→";
            return true;
        } else if (regex.matches(GestureRegex.LEFT_ARROW)) {
            match += "←";
            return true;
        } else if (regex.matches(GestureRegex.UP_ARROW)) {
            match += "↑";
            return true;
        } else if (regex.matches(GestureRegex.DOWN_ARROW) || regex.matches(GestureRegex.DOWN_ARROW_LEFT_HANDED)) {
            match += "↓";
            return true;
        }


        return false;
    }

}
