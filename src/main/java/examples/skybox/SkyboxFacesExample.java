package examples.skybox;

import com.harium.etyl.Etyl;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.loader.image.ImageLoader;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.core.source.ImageSource;
import com.harium.keel.effect.GaussianBlur;
import com.harium.keel.effect.projection.CubeToEquirectangularFaces;
import com.harium.keel.effect.resize.ResizeBicubic;

import java.awt.image.BufferedImage;

public class SkyboxFacesExample extends Etyl {

    private static final long serialVersionUID = 1L;

    public SkyboxFacesExample() {
        super(512, 256);
    }

    public static void main(String[] args) {
        SkyboxFacesExample example = new SkyboxFacesExample();
        example.init();
    }

    public Application startApplication() {
        return new SkyboxApplication(w, h);
    }

    private class SkyboxApplication extends Application {
        ImageSource top, bottom, left, right, front, back;

        BufferedImageSource input;

        public SkyboxApplication(int w, int h) {
            super(w, h);
        }

        @Override
        public void load() {
            top = load("skybox/teal1/0006.jpg");
            front = load("skybox/teal1/0002.jpg");
            right = load("skybox/teal1/0003.jpg");
            back = load("skybox/teal1/0004.jpg");
            left = load("skybox/teal1/0005.jpg");
            bottom = load("skybox/teal1/0001.jpg");

            CubeToEquirectangularFaces effect = new CubeToEquirectangularFaces().width(512).height(256);
            effect.front(front);
            effect.back(back);
            effect.left(left);
            effect.right(right);
            effect.top(top);
            effect.bottom(bottom);

            BufferedImage image = new BufferedImage(effect.getWidth(), effect.getHeight(), BufferedImage.TYPE_INT_RGB);
            input = new BufferedImageSource(image);
            ImageSource output = effect.apply(front);

            for (int j = 0; j < output.getHeight(); j++) {
                for (int i = 0; i < output.getWidth(); i++) {
                    input.setRGB(i, j, output.getRGB(i, j));
                }
            }
        }

        BufferedImageSource load(String path) {
            return new BufferedImageSource(ImageLoader.getInstance().getImage(path));
        }

        @Override
        public void draw(Graphics g) {
            g.drawImage(input.getImage(), 0, 0);
        }
    }
}
