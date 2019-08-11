package examples.modifiers.edge;


import com.harium.etyl.Etyl;
import com.harium.etyl.commons.context.Application;

public class CannyEdgeExample extends Etyl {

	private static final long serialVersionUID = 1L;

	public CannyEdgeExample() {
		super(512, 512+37);
	}
	
	public static void main(String[] args) {
		CannyEdgeExample example = new CannyEdgeExample();
		example.init();
	}

	public Application startApplication() {
		return new CannyEdgeApplication(w,h);
	}	

}
