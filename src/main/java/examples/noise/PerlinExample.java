package examples.noise;

import com.harium.etyl.Etyl;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.keel.core.source.ImageSource;
import com.harium.keel.core.source.MatrixSource;
import com.harium.keel.effect.noise.PerlinNoise;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PerlinExample extends Etyl {

  int seed = 123;
  int width = 800;
  int height = 800;
  double persistance = 80;

  public PerlinExample() {
    super(512, 512);
  }

  public static void main(String[] args) {
    PerlinExample example = new PerlinExample();
    example.init();
  }

  public Application startApplication() {
    return new PerlinApplication(w, h);
  }

  private class PerlinApplication extends Application {

    BufferedImage image;

    public PerlinApplication(int w, int h) {
      super(w, h);
    }

    @Override
    public void load() {

      PerlinNoise noiseEffect = new PerlinNoise()
          .persistance(persistance)
          .lacunarity(1)
          .seed(seed);

      ImageSource noise = noiseEffect.apply(new MatrixSource(width, height));

      image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
      Graphics2D graphics2D = image.createGraphics();

      for (int y = 0; y < height; y++) {
        for (int x = 0; x < width; x++) {
          int gray = noise.getRGB(x, y);

          graphics2D.setColor(new Color(gray));
          graphics2D.fillRect(x, y, 1, 1);
        }
      }
    }

    @Override
    public void draw(Graphics graphics) {
      graphics.drawImage(image, 0, 0);
    }
  }
}
