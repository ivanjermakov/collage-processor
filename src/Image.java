import java.awt.*;
import java.awt.image.BufferedImage;

public class Image {
	public BufferedImage image;
	public Color averageColor;
	
	Image(BufferedImage image, Color averageColor) {
		this.image = image;
		this.averageColor = averageColor;
	}
}
