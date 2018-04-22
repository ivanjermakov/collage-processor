import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Serializable;

public class Image {
	public BufferedImage image;
	public Color averageColor;
	
	Image(BufferedImage image, Color averageColor) {
		this.image = image;
		this.averageColor = averageColor;
	}
}
