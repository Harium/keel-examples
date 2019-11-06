package examples.misc;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.keel.awt.PolygonHelper;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.FakeCamera;
import com.harium.keel.feature.Feature;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.search.CrossSearch;
import com.harium.keel.filter.search.TriangularSearch;
import com.harium.keel.filter.selection.RGBColorStrategy;
import com.harium.keel.filter.selection.skin.SkinColorStrategy;
import com.harium.keel.modifier.EnvelopeModifier;
import com.harium.keel.modifier.hull.FastConvexHullModifier;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.List;

public class FaceStatic extends Application {

    private FakeCamera cam = new FakeCamera();
    private BufferedImageSource source = new BufferedImageSource();

    private CrossSearch blackFilter = new CrossSearch();

    private CrossSearch whiteFilter = new CrossSearch();

    private TriangularSearch skinFilter;

    private boolean hide = false;
    private boolean pixels = true;
    private boolean drawCleanedOnly = false;
    private boolean drawBox = true;

    private int xOffset = 0;
    private int yOffset = 0;

    private final int IMAGES_TO_LOAD = 50;

    private PointFeature blackSampledFeature;

    private PointFeature lightDirection;

    private List<PointFeature> skinFeatures;

    private Polygon blackPolygon = new Polygon();

    private Polygon whitePolygon = new Polygon();

    private Feature screen;

    private FastConvexHullModifier modifier;

    public FaceStatic(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {

        loadingInfo = "Loading Images";

        for (int i = 1; i <= IMAGES_TO_LOAD; i++) {
            loading = i;

            cam.addImage("skin/skin" + Integer.toString(i) + ".jpg");
        }

        int width = cam.getWidth();
        int height = cam.getHeight();

        loadingInfo = "Configuring Filter";

        modifier = new FastConvexHullModifier();

        RGBColorStrategy blackColorFilter = new RGBColorStrategy(Color.BLACK.getRGB());
        blackColorFilter.setTolerance(0x50);

        blackFilter.setSelectionStrategy(blackColorFilter);
        blackFilter.setComponentModifierStrategy(modifier);

        //border: 4 and step: 4
        blackFilter.setBorder(8);
        blackFilter.setStep(4);

        //White Color
        RGBColorStrategy whiteColorFilter = new RGBColorStrategy(Color.WHITE.getRGB());
        whiteColorFilter.setTolerance(0x64);

        whiteFilter.setSelectionStrategy(whiteColorFilter);
        whiteFilter.setComponentModifierStrategy(modifier);

        whiteFilter.setBorder(4);
        whiteFilter.setStep(4);

        skinFilter = new TriangularSearch(width, height);
        skinFilter.setBorder(4);
        skinFilter.setSelectionStrategy(new SkinColorStrategy());
        skinFilter.setComponentModifierStrategy(new EnvelopeModifier());

        loading = 60;
        reset(cam.getImage());

        loading = 100;
    }

    private void reset(BufferedImage b) {
        int w = b.getWidth();
        int h = b.getHeight();

        screen = new Feature(w, h);

        source.setImage(b);

        //Sampled
        blackSampledFeature = blackFilter.filterFirst(source, screen);
        blackPolygon.reset();

        blackPolygon = PolygonHelper.getPolygon(blackSampledFeature);


        //White PointFeature
        lightDirection = whiteFilter.filterFirst(source, screen);
        whitePolygon.reset();

        whitePolygon = PolygonHelper.getPolygon(lightDirection);

        skinFeatures = skinFilter.filter(source, screen);
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

        if (event.isKeyDown(KeyEvent.VK_C)) {
            drawCleanedOnly = !drawCleanedOnly;
        }

        if (event.isKeyDown(KeyEvent.VK_B)) {
            drawBox = !drawBox;
        }
    }

    @Override
    public void draw(Graphics g) {

        if (!hide) {
            g.drawImage(cam.getImage(), xOffset, yOffset);
        }

        g.setColor(Color.BLUE);

        g.setColor(Color.BLACK);
        //g.setLineWidth(2);
        g.drawPolygon(blackPolygon);

        g.drawRect(blackSampledFeature.getRectangle());

        g.setColor(Color.WHITE);
        //g.setLineWidth(1);

        g.setColor(Color.ORANGE);
        g.drawPolygon(whitePolygon);

        g.drawRect(lightDirection.getRectangle());

        //g.setLineWidth(2);
        for (PointFeature skin : skinFeatures) {
            g.setColor(Color.BLUE_VIOLET);
            g.drawRect(skin.getRectangle());
        }
    }
}
