import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency.*;
import java.awt.GraphicsEnvironment;

public class Main {

	public static void main(String[] args) {
		
		// Determine what the default GraphicsDevice can support.
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();

		boolean isUniformTranslucencySupported = gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.TRANSLUCENT);
		boolean isPerPixelTranslucencySupported = gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT);
		boolean isShapedWindowSupported = gd.isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT);
				
		System.out.println("UNIFORM_TRANSLUCENT  =\t"+isUniformTranslucencySupported);
		System.out.println("PERPIXEL_TRANSLUCENT =\t"+isPerPixelTranslucencySupported);
		System.out.println("PERPIXEL_TRANSPARENT =\t"+isShapedWindowSupported);
		
		if(isUniformTranslucencySupported && isPerPixelTranslucencySupported && isShapedWindowSupported) {
			System.out.println("All graphics requirements available!");
		}else {
			System.out.println("Some/All graphics requirements are not available, some transparency features may not work.\nYou may need to enable display compositing.");
		}
		
		new PosterProjector3000(true);

	}

}
