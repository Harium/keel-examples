package examples.medium.skin;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.event.MouseEvent;
import com.harium.etyl.commons.event.PointerEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.Camera;
import com.harium.keel.camera.FakeCamera;
import com.harium.keel.core.Filter;
import com.harium.keel.core.helper.ColorHelper;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.ExpandableColorFilter;
import com.harium.keel.filter.HardColorFilter;
import com.harium.keel.filter.SkinColorFilter;
import com.harium.keel.filter.process.AverageColorFilter;
import com.harium.keel.filter.selection.skin.SkinColorKovacNewStrategy;
import com.harium.keel.filter.validation.point.MinDimensionValidation;

import java.awt.image.BufferedImage;
import java.util.*;

public class SimpleFaceFinderApplication extends Application {

    protected Camera cam = new FakeCamera();
    private BufferedImageSource source = new BufferedImageSource();

    private final int IMAGES_TO_LOAD = 20;

    private SkinColorFilter skinFilter;

    protected PointFeature bestCandidate;
    private List<PointFeature> skinPointFeatures;
    private List<PointFeature> darkPointFeatures;
    private Map<PointFeature, Integer> counts = new HashMap<PointFeature, Integer>();
    protected List<PointFeature> facePointFeatures = new ArrayList<PointFeature>();

    private PointFeature screen;

    private Color color = Color.BLACK;

    private boolean drawPoints = false;
    private boolean leftPoints = true;

    public SimpleFaceFinderApplication(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {
        loading = 0;

        loadingInfo = "Loading Images";
        initCamera();
        loadingInfo = "Configuring Filter";

        loading = 25;
        reset();
        loading = 50;
    }

    protected void initCamera() {
        for (int i = 1; i <= IMAGES_TO_LOAD; i++) {
            loading = i;

            ((FakeCamera) cam).addImage("skin/skin" + Integer.toString(i) + ".jpg");
        }
    }

    @Override
    public void updateKeyboard(KeyEvent event) {
        if (event.isKeyDown(KeyEvent.VK_RIGHT)) {
            ((FakeCamera) cam).nextFrame();
            reset();
        } else if (event.isKeyDown(KeyEvent.VK_LEFT)) {
            ((FakeCamera) cam).previousFrame();
            reset();
        } else if (event.isKeyDown(KeyEvent.VK_SPACE)) {
            drawPoints = !drawPoints;
        }

        if (event.isKeyDown(KeyEvent.VK_1)) {
            leftPoints = true;
        }

        if (event.isKeyDown(KeyEvent.VK_2)) {
            leftPoints = false;
        }
    }

    @Override
    public void updateMouse(PointerEvent event) {

        if (event.isButtonUp(MouseEvent.MOUSE_BUTTON_LEFT)) {
            int x = event.getX();
            int y = event.getY();

            BufferedImage buffer = cam.getImage();

            if (x < buffer.getWidth() && y < buffer.getHeight()) {

                int rgb = buffer.getRGB(x, y);
                final int R = ColorHelper.getRed(rgb);
                final int G = ColorHelper.getGreen(rgb);
                final int B = ColorHelper.getBlue(rgb);

                System.out.println(R + " " + G + " " + B + " RG_MOD=" + (R - G) + " R-B=" + (R - B));
            }
        }
    }

    protected void reset() {
        BufferedImage image = cam.getImage();
        source.setImage(image);

        //Define the area to search for elements
        int w = image.getWidth();
        int h = image.getHeight();

        screen = new PointFeature(0, 0, w, h);
        skinFilter = new SkinColorFilter(w, h, new SkinColorKovacNewStrategy());
        HardColorFilter colorFilter = new HardColorFilter(w, h, new Color(40, 40, 40), 25);

        Filter filter = skinFilter.getSearchStrategy();
        filter.setStep(2);
        filter.setBorder(20);

        //Remove components smaller than 20x20
        skinFilter.addValidation(new MinDimensionValidation(20));
        skinPointFeatures = skinFilter.filter(source, screen);

        colorFilter.addValidation(new MinDimensionValidation(3));
        darkPointFeatures = colorFilter.filter(source, screen);

        //Evaluate components
        //validatePointFeatures();
        bestCandidate = evaluatePointFeature(skinPointFeatures);

        Color faceColor = new AverageColorFilter().filter(source, bestCandidate);

        ExpandableColorFilter featureFilter = new ExpandableColorFilter(w, h, faceColor, 30);
        //featureFilter.getSearchStrategy().setBorder(2);
        featureFilter.getSearchStrategy().setStep(4);
        featureFilter.addValidation(new MinDimensionValidation(2));

        facePointFeatures = featureFilter.filter(source, bestCandidate);

        //System.out.println("Fc "+facePointFeatures.size());
        color = randomColor();
    }

    private void validatePointFeatures() {
        for (int i = skinPointFeatures.size() - 1; i >= 0; i--) {
            PointFeature component = skinPointFeatures.get(i);

            //Vertical trim component
            //component = trim(component);

            //Remove components near from left border
            if (component.getX() < 20 + 10) {
                skinPointFeatures.remove(i);
                continue;
            }

            if (component.getX() + component.getW() > h - 10) {
                skinPointFeatures.remove(i);
                continue;
            }
        }
    }

    private PointFeature evaluatePointFeature(List<PointFeature> components) {
        int higher = 0;
        PointFeature faceCandidate = components.get(0);

        for (PointFeature component : components) {
            int count = 0;
            for (PointFeature dc : darkPointFeatures) {
                if (component.colide(dc)) {
                    count++;
                }
            }
            if (count > higher) {
                higher = count;
                faceCandidate = component;
            }
            counts.put(component, count);
        }

        return faceCandidate;
    }

    private Color randomColor() {
        int r = new Random().nextInt(255);
        int g = new Random().nextInt(255);
        int b = new Random().nextInt(255);

        return new Color(r, g, b);
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(cam.getImage(), 0, 0);

        g.setColor(color);
        drawPointFeature(g, bestCandidate);

        g.setColor(Color.RED);
        for (PointFeature feature : facePointFeatures) {
            drawAllPoints(g, feature);
            g.drawRect(feature.getRectangle());
        }

        //Draw a red line around the components
        //drawPointFeatures(g);

        //Draw dark components
        /*g.setStroke(new BasicStroke(3f));
        g.setColor(Color.BLACK);

		for(PointFeature component:darkPointFeatures) {
			g.drawRect(component.getRectangle());
		}*/
    }

    protected void drawPointFeatures(Graphics g) {
        for (int i = 0; i < skinPointFeatures.size(); i++) {
            PointFeature component = skinPointFeatures.get(i);

            g.setColor(color);
            drawPointFeature(g, component);
        }
    }

    protected void drawPointFeature(Graphics g, PointFeature component) {
        //g.setStroke(new BasicStroke(3f));
        g.drawRect(component.getRectangle());

        g.setColor(Color.BLACK);

        int count = counts.get(component);

        g.drawString(Integer.toString(count), component.getRectangle());

        if (drawPoints) {
            drawPoints(g, component);
        }
    }

    public void drawPoints(Graphics g, PointFeature component) {
        for (Point2D point : component.getPoints()) {

            if (leftPoints) {
                if (point.x < w / 2) {
                    g.fillRect((int) point.x, (int) point.y, 1, 1);
                }
            } else if (point.x >= w / 2) {
                g.fillRect((int) point.x, (int) point.y, 1, 1);
            }
        }
    }

    public void drawAllPoints(Graphics g, PointFeature component) {
        for (Point2D point : component.getPoints()) {
            g.fillRect((int) point.x, (int) point.y, 1, 1);
        }
    }
}
