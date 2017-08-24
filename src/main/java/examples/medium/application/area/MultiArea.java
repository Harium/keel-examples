package examples.medium.application.area;

import com.harium.keel.awt.PolygonHelper;
import com.harium.keel.feature.Component;
import com.harium.etyl.commons.layer.GeometricLayer;
import com.harium.etyl.linear.Point2D;

import java.awt.*;

public class MultiArea {

    private int areas = 1;

    private Polygon[] polygons;
    private GeometricLayer[] layers;

    public MultiArea(int areas) {
        super();

        this.areas = areas;

        layers = new GeometricLayer[areas];
        polygons = new Polygon[areas];
        for (int i = 0; i < areas; i++) {
            layers[i] = new GeometricLayer();
            polygons[i] = new Polygon();
        }
    }

    public void generateArea(Point2D p, Point2D q) {

        double distance = p.distance(q);

        Point2D[] pPoints = generatePoints(p, q, distance / areas);

        Point2D p1 = pPoints[0];
        Point2D p2 = pPoints[1];

        double interval = distance / areas;

        Point2D c = p.distantPoint(q, interval);
        Point2D nq = q;

        for (int i = 0; i < areas; i++) {

            Component component = new Component();

            double subdistance = interval * (i + 1);
            nq = p.distantPoint(q, subdistance + interval);

            Point2D[] cPoints = generatePoints(c, nq, subdistance);

            Point2D c1 = cPoints[0];
            Point2D c2 = cPoints[1];

            component.add((int) p1.getX(), (int) p1.getY());
            component.add((int) p2.getX(), (int) p2.getY());
            component.add((int) c2.getX(), (int) c2.getY());
            component.add((int) c1.getX(), (int) c1.getY());

            layers[i] = component.getLayer();
            polygons[i] = PolygonHelper.getPolygon(component);

            p1 = c1;
            p2 = c2;

            c = nq;
        }

    }

    private static Point2D[] generatePoints(Point2D p, Point2D q, double d) {

        Point2D[] points = new Point2D[2];

        Point2D p1 = p.distantPoint(q, 50);
        p1.rotate(p, -90);
        Point2D p2 = p.distantPoint(q, 50);
        p2.rotate(p, +90);

        points[0] = p1;
        points[1] = p2;

        return points;
    }

    public Polygon[] getPolygons() {
        return polygons;
    }

    public GeometricLayer[] getLayers() {
        return layers;
    }

    public int getAreas() {
        return areas;
    }

}