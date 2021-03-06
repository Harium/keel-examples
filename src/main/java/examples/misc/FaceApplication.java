package examples.misc;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.KeyEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.commons.layer.Layer;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.geometry.Point2D;
import com.harium.etyl.layer.ImageLayer;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.camera.Camera;
import com.harium.keel.camera.Webcam;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.ColorPointFilter;

import java.util.List;

public class FaceApplication extends Application {

    private PointFeature screen;

    private boolean[][] emptyMask;

    public FaceApplication(int w, int h) {
        super(w, h);
    }

    private Camera cam;

    private Layer lastFace;

    private BufferedImageSource buf;

    private ColorPointFilter findFaceFilter;
    private ColorPointFilter findEye;

    private List<PointFeature> faces;
    private List<PointFeature> eyes;

    @Override
    public void load() {

        cam = new Webcam();

        int w = cam.getWidth();
        int h = cam.getHeight();

        screen = new PointFeature(w, h);

        emptyMask = new boolean[w][h];

        for (int j = 0; j < h; j++) {

            for (int i = 0; i < w; i++) {
                emptyMask[i][j] = false;
            }

        }

        //findFace = new FindSkinFilter(w,h);
        findFaceFilter = new ColorPointFilter(w, h);
        //findFaceFilter.setSelectionStrategy(new SkinColorStrategy());

        findEye = new ColorPointFilter(w, h, Color.BLACK);

        lastFace = new ImageLayer(0, 0, w, h);

        loading = 100;

    }

    @Override
    public void updateKeyboard(KeyEvent event) {

        if (event.isKeyDown(KeyEvent.VK_RIGHT)) {
            maxX++;
            System.out.println(maxX);
        } else if (event.isKeyDown(KeyEvent.VK_LEFT)) {
            maxX--;
            System.out.println(maxX);
        }

        if (event.isKeyDown(KeyEvent.VK_UP)) {
            //distFace+=0.1;
            maxPontos += 20;
            System.out.println(maxPontos);
        } else if (event.isKeyDown(KeyEvent.VK_DOWN)) {
            //distFace-=0.1;
            maxPontos -= 20;
            System.out.println(maxPontos);
        }

        if (event.isKeyDown(KeyEvent.VK_R)) {

        }
    }

    private double distFace = 4.0;

    @Override
    public void draw(Graphics g) {

        buf = new BufferedImageSource(cam.getImage());

        g.drawImage(buf.getImage(), 0, 0);

        faces = findFaceFilter.filter(buf, screen);

        for (PointFeature face : faces) {
            eyes.addAll(findEye.filter(buf, face));
        }

        for (PointFeature component : faces) {

            //TODO Draw Pixels
            g.setColor(Color.GREEN);
            for (Point2D point : component.getPoints()) {
                g.drawRect(point.x, point.y, 1, 1);
            }

            //TODO Write Number of Points
            g.setColor(Color.WHITE);
            g.drawRect(component.getLayer());
            g.drawStringShadow(Integer.toString(component.getPointCount()), component.getLowestX(), component.getLowestY(), component.getW(), component.getH(), Color.BLACK);

        }

        for (PointFeature component : eyes) {

            g.setColor(Color.BLUE);
            for (Point2D point : component.getPoints()) {
                g.drawRect(point.x, point.y, 1, 1);
            }

            //TODO Write Number of Points
            g.setColor(Color.BLUE);
            g.drawRect(component.getLayer());
            g.drawStringShadow(Integer.toString(component.getPointCount()), component.getLowestX(), component.getLowestY(), component.getW(), component.getH(), Color.BLACK);

        }

        //TODO DRAW only the biggest Face
        //pintaFace(g, faces);

        //TODO Draw Marks

		/*g.setColor(Color.WHITE);
        g.escreveSombra(19,15,"maxPontos = "+Integer.toString(maxPontos));

		g.escreveSombra(19,35,"maxPontos/9 = "+Integer.toString(maxPontos/9));

		g.escreveSombra(19,55,"maxPontos/5 = "+Integer.toString(maxPontos/5));

		g.escreveSombra(19,75,"distFace = "+Double.toString(distFace));

		g.escreveSombra(19,95,"LarguraFace = "+Integer.toString(lastFace.getW()));

		g.escreveSombra(19,105,"LarguraFace/2 = "+Integer.toString(lastFace.getW()/2));
		g.escreveSombra(19,115,"LarguraFace/5 = "+Integer.toString(lastFace.getW()/5));
		g.escreveSombra(19,125,"LarguraFace*5 = "+Integer.toString(lastFace.getW()*5));*/

    }

    private int maxX = 600;
    private int maxPontos = 700;

    private void pintaFace(Graphics g, List<PointFeature> componentes) {

        int maiorRelevancia = 0;

        for (PointFeature componente : componentes) {

            if (componente.getPointCount() >= maxPontos) {

                if (componente.getHighestX() < maxX) {

                    //Boxes Verticais
                    if (componente.getH() > componente.getW()) {
                        //Desenha possíveis faces
                        //g.setColor(Color.GREEN);
                        //g.drawRect(lista.getCamada());

                        int relevancia = (componente.getH() * componente.getW()) - componente.getLowestY();

                        if (relevancia > maiorRelevancia) {
                            maiorRelevancia = relevancia;
                            //TODO Apenas mudar quando diferença for grande para garantir estabilidade
                            if (lastFace.getW() * lastFace.getH() < componente.getH() * componente.getH())
                                lastFace = componente.getLayer();
                        }
                    }

                }

            }
        }

        //Posso remover daqui
        if (lastFace != null) {
            g.setColor(Color.RED);
            g.drawRect(lastFace);
        }

        //return achaIris(g, bimg);

    }

}
