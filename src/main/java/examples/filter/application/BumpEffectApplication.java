package examples.filter.application;

import com.harium.etyl.commons.context.Application;
import com.harium.etyl.core.graphics.Graphics;
import com.harium.etyl.util.PathHelper;
import com.harium.keel.awt.BufferedImageExporter;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.core.source.ImageSource;
import com.harium.keel.effect.normal.SobelNormalMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BumpEffectApplication extends Application {

    private BufferedImage bump;
    private BufferedImage layer;

    public BumpEffectApplication(int w, int h) {
        super(w, h);
    }

    @Override
    public void load() {
        try {
            layer = ImageIO.read(new File(PathHelper.currentDirectory() + "assets/images/effects/imp_gray.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Effect Pipeline
        BufferedImageSource source = new BufferedImageSource(layer);
        SobelNormalMap effect = new SobelNormalMap();
        ImageSource output = effect.apply(source);
        BufferedImageExporter exporter = new BufferedImageExporter();
        bump = exporter.export(output);
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(layer, 0, 0);
        g.drawImage(bump, 512, 0);
    }
}
