package examples.misc;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.context.UpdateIntervalListener;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.Camera;
import com.harium.keel.camera.Webcam;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.ColorPointFilter;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


public class HollowController extends Application implements UpdateIntervalListener {

    private PointFeature screen;

    public HollowController(int w, int h) {
        super(w, h);
    }

    private Camera cam;

    private BufferedImage buf;

    private BufferedImageSource source = new BufferedImageSource();

    private RedLedFilter ledFilter;

    private ColorPointFilter activeFilter;

    private List<PointFeature> lastButtons;

    private List<PointFeature> components;

    @Override
    public void load() {

        cam = new Webcam();
        source.setImage(cam.getImage());

        final int w = cam.getWidth();
        final int h = cam.getHeight();

        screen = new PointFeature(w, h);

        //Loading Filters
        ledFilter = new RedLedFilter(w, h);
        activeFilter = new ColorPointFilter(w, h, Color.WHITE);

        lastButtons = new ArrayList<PointFeature>(8);

        loading = 100;

    }

    public void timeUpdate(long now) {
        System.out.println("TIME UPDATE");
    }

    @Override
    public void updateKeyboard(KeyEvent event) {
        if (event.isKeyDown(KeyEvent.VK_R)) {
            activated = false;
        }
    }

    private boolean activated = false;

    @Override
    public void draw(Graphics g) {

        buf = cam.getImage();
        source.setImage(buf);

        g.drawImage(buf, 0, 0);

        activated = false;

        for (PointFeature component : lastButtons) {

            List<PointFeature> active = activeFilter.filter(source, component);

            Color color = Color.YELLOW;

            if (active != null) {

                color = Color.RED;
                activated = true;

            }
            g.setColor(color);

            g.drawRect(component.getLayer());

        }

        if (!activated) {

            components = ledFilter.filter(source, screen);

            if (components != null) {

                Color color = Color.GREEN;
                if (components.size() == 8) {

                    lastButtons.clear();
                    lastButtons.addAll(components);

                    color = Color.BLUE;
                }

                for (PointFeature component : components) {
                    g.setColor(color);
                    g.drawRect(component.getLayer());
                    /*g.setColor(Color.WHITE);
					g.escreveLabelSombra(component.getMenorX(), component.getMenorY(), component.getW(), component.getH(), Integer.toString(component.getNumeroPontos()),Color.BLACK);*/
                }

            }
        }

    }

}
