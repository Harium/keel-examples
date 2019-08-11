package examples.basic.geometric;

import com.harium.etyl.Etyl;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.event.MouseEvent;
import com.harium.etyl.commons.event.PointerEvent;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.commons.math.Vector2i;
import com.harium.etyl.core.graphics.Graphics;
import java.util.ArrayList;
import java.util.List;

public class CentroidExample extends Etyl {

  private static final long serialVersionUID = 1L;

  public CentroidExample() {
    super(800, 480);
  }

  public static void main(String[] args) {
    CentroidExample example = new CentroidExample();
    example.init();
  }

  public Application startApplication() {
    return new CentroidApplication(w, h);
  }

  class CentroidApplication extends Application {

    public static final int RADIUS = 5;

    Vector2i centroid = new Vector2i();
    private List<Vector2i> feature = new ArrayList<>();

    public CentroidApplication(int w, int h) {
      super(w, h);
    }

    @Override
    public void load() {

    }

    @Override
    public void updateMouse(PointerEvent event) {
      if (event.isButtonUp(MouseEvent.MOUSE_BUTTON_LEFT)) {
        feature.add(new Vector2i(event.getX(), event.getY()));

        int x = 0, y = 0;
        for (Vector2i point : feature) {
          x += point.x;
          y += point.y;
        }
        centroid.set(x / feature.size(), y / feature.size());
      }


    }

    @Override
    public void draw(Graphics g) {
      g.setColor(Color.CYAN);

      for (Vector2i point : feature) {
        g.setColor(Color.BLACK);
        g.drawCircle(point.x, point.y, RADIUS);
      }
      g.setColor(Color.CYAN);
      g.drawCircle(centroid.x, centroid.y, RADIUS);
    }
  }
}
