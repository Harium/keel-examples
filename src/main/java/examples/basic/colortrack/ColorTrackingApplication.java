package examples.basic.colortrack;

import com.harium.keel.awt.PolygonHelper;
import com.harium.keel.awt.source.BufferedImageSource;
import com.harium.keel.feature.PointFeature;
import com.harium.keel.filter.ColorFilter;
import com.harium.etyl.commons.context.Application;
import com.harium.etyl.commons.graphics.Color;
import com.harium.etyl.core.graphics.Graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class ColorTrackingApplication extends Application {

	private BufferedImage image;
	private BufferedImageSource source = new BufferedImageSource();
	
	private ColorFilter blueFilter;
	
	private ColorFilter blackFilter;
	
	private PointFeature screen;
	
	private List<PointFeature> bluePointFeatures;
	
	private List<PointFeature> blackPointFeatures;
	
	public ColorTrackingApplication(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {
		
		//Define the area to search for elements
		screen = new PointFeature(0, 0, w, h);
		
		image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
				
		//Create the image with elements
		createImage(image);
		source.setImage(image);
		
		//Define blue and black filters
		blackFilter = new ColorFilter(w, h, Color.BLACK);
		
		blueFilter = new ColorFilter(w, h, Color.BLUE);
		
		//Filter the image 
		bluePointFeatures = blueFilter.filter(source, screen);
		
		blackPointFeatures = blackFilter.filter(source, screen);
		
	}
	
	private void createImage(BufferedImage image) {
		
		Graphics2D g = image.createGraphics();
				
		g.setColor(java.awt.Color.WHITE);
		
		g.fillRect(0, 0, w, h);
		
		g.setColor(java.awt.Color.BLACK);
		
		g.fillRect(40, 40, 100, 80);
		
		g.fillOval(160, 40, 100, 80);
		
		g.fillRect(300, 200, 80, 80);
		
		g.setColor(java.awt.Color.BLUE);
		
		g.fillRect(30, 200, 100, 80);
		
		g.fillRoundRect(440, 20, 20, 200, 50, 20);
		
		g.fillOval(390, 100, 80, 600);
				
	}

	@Override
	public void draw(Graphics g) {
		g.setAlpha(100);
		g.drawImage(image, 0, 0);
		
		g.setAlpha(90);
		
		//Draw a red line around the black components
		for(PointFeature component: blackPointFeatures) {
			g.setStroke(new BasicStroke(3f));
			g.setColor(Color.RED);
			g.drawPolygon(PolygonHelper.getBoundingBox(component));
		}
		
		//Draw a yellow line around the blue components
		for(PointFeature component: bluePointFeatures) {
			g.setStroke(new BasicStroke(3f));
			g.setColor(Color.YELLOW);
			g.drawRect(component.getRectangle());
		}
		
	}
	
}
