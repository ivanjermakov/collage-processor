package com.gmail.ivanjermakov1.collageprocessor;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Image {
	
	private BufferedImage image;
	private Color averageColor;
	
	public Image(BufferedImage image, Color averageColor) {
		this.image = image;
		this.averageColor = averageColor;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	
	public Color getAverageColor() {
		return averageColor;
	}
	
	public static Color averageColor(BufferedImage image) {
		long sumRed = 0;
		long sumGreen = 0;
		long sumBlue = 0;
		
		for (int x = 0; x < image.getWidth(); x++) {
			for (int y = 0; y < image.getHeight(); y++) {
				Color pixel = new Color(image.getRGB(x, y));
				sumRed += pixel.getRed();
				sumGreen += pixel.getGreen();
				sumBlue += pixel.getBlue();
			}
		}
		
		long amountOfPixels = image.getWidth() * image.getHeight();
		
		int red = (int) (sumRed / amountOfPixels);
		int green = (int) (sumGreen / amountOfPixels);
		int blue = (int) (sumBlue / amountOfPixels);
		
		return new Color(red, green, blue);
	}
	
}
