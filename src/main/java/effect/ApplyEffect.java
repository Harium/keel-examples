package effect;

import com.harium.etyl.Etyl;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.keel.awt.BufferedImageExporter;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.core.Effect;
import com.harium.keel.core.source.ImageSource;
import com.harium.keel.effect.CannyEdgeDetector;
import com.harium.keel.effect.Grayscale;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ApplyEffect extends Etyl {

    // Gnome's Window Bar
    private static final int OFFSET_Y = 37;

    public ApplyEffect() {
        super(512, 512 + OFFSET_Y);
        //super(256,256);
    }

    public static void main(String[] args) {
        ApplyEffect example = new ApplyEffect();
        example.init();
    }

    public Application startApplication() {
        return new EffectApplier(w, h);
    }

    private class EffectApplier extends Application {

        boolean shouldDraw = false;
        BufferedImage image;
        ImageSource output;
        Effect effect;

        public EffectApplier(int w, int h) {
            super(w, h);
        }

        @Override
        public void load() {
            effect = new CannyEdgeDetector();
        }

        @Override
        public void draw(Graphics graphics) {
            if (!shouldDraw) {
                return;
            }
            graphics.drawImage(image, 0, OFFSET_Y);
        }

        @Override
        public void dropFiles(int x, int y, List<File> files) {
            super.dropFiles(x, y, files);

            try {
                BufferedImage image = ImageIO.read(files.get(0));
                BufferedImageSource source = new BufferedImageSource();
                source.setImage(image);

                ImageSource out = new Grayscale(Grayscale.Algorithm.Luminosity).apply(source);
                //out.setGrayscale(true);

                //output = effect.apply(out);
                this.image = new BufferedImageExporter().export(out);
                shouldDraw = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
