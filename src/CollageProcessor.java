import javafx.fxml.LoadException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class CollageProcessor {
	
	public BufferedImage initialImage;
	
	public List<BufferedImage> collageImages;
	
	public CollageProcessor() {
	
	}
	
	public void loadInitialImage(String PATH) {
		try {
			initialImage = ImageIO.read(new File(PATH));
			
			System.out.println("Image \"" + PATH + "\" loaded successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadCollageImages(String PATH) {
	
	}
	
	public void saveImageToFile(BufferedImage image, String PATH) {
		File outputFile = new File(PATH);
		try {
			ImageIO.write(image, "jpg", outputFile);
			
			System.out.println("Image \"" + PATH + "\" saved successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
