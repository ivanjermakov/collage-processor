package com.gmail.ivanjermakov1.collageprocessor.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
	
	public static int getCloseness(Color a1, Color a2) {
		return Math.abs(a1.getRed() - a2.getRed()) +
				Math.abs(a1.getGreen() - a2.getGreen()) +
				Math.abs(a1.getBlue() - a2.getBlue());
	}
	
	public static BufferedImage getBufferedImageCopy(BufferedImage image) {
		ColorModel cm = image.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = image.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null)
				.getSubimage(0, 0, image.getWidth(), image.getHeight());
	}
	
	public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
		float scaleX = (float) width / image.getWidth();
		float scaleY = (float) height / image.getHeight();
		float scale = Math.min(scaleX, scaleY);
		int w = Math.round(image.getWidth() * scale);
		int h = Math.round(image.getHeight() * scale);
		
		int type = image.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
		
		boolean scaleDown = scale < 1;
		
		if (scaleDown) {
			// multi-pass bilinear div 2
			int currentW = image.getWidth();
			int currentH = image.getHeight();
			BufferedImage resized = image;
			while (currentW > w || currentH > h) {
				currentW = Math.max(w, currentW / 2);
				currentH = Math.max(h, currentH / 2);
				
				BufferedImage temp = new BufferedImage(currentW, currentH, type);
				Graphics2D g2 = temp.createGraphics();
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.drawImage(resized, 0, 0, currentW, currentH, null);
				g2.dispose();
				resized = temp;
			}
			return resized;
		} else {
			Object hint = scale > 2 ? RenderingHints.VALUE_INTERPOLATION_BICUBIC : RenderingHints.VALUE_INTERPOLATION_BILINEAR;
			
			BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = resized.createGraphics();
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
			g2.drawImage(image, 0, 0, w, h, null);
			g2.dispose();
			return resized;
		}
	}
	
	public static byte[] imageToByteArray(BufferedImage image) {
		ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "jpg", bAOS);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bAOS.toByteArray();
	}
	
	public static BufferedImage byteArrayToImage(byte[] imageData) {
		ByteArrayInputStream bAIS = new ByteArrayInputStream(imageData);
		try {
			return ImageIO.read(bAIS);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static BufferedImage cropBlackBorder(BufferedImage image) {
		BufferedImage cropped = getBufferedImageCopy(image);
		
		//crop vertically
		for (int y = cropped.getHeight() - 1; y >= 0; y--) {
			int color = cropped.getRGB(0, y) & 0x00ffffff;
			
			if (color != 0) {
				cropped = cropped.getSubimage(0, 0, cropped.getWidth(), y + 1);
				break;
			}
		}
		
		//crop horizontally
		for (int x = cropped.getWidth() - 1; x >= 0; x--) {
			int color = cropped.getRGB(x, 0) & 0x00ffffff;
			
			if (color != 0) {
				cropped = cropped.getSubimage(0, 0, x + 1, cropped.getHeight());
				break;
			}
		}
		
		if (image.getWidth() * image.getHeight() != cropped.getWidth() * cropped.getHeight()) {
			System.out.println("Resized image from " + image.getWidth() + "x" + image.getHeight() + " to " +
					cropped.getWidth() + "x" + cropped.getHeight());
		}
		
		return cropped;
	}
	
}
