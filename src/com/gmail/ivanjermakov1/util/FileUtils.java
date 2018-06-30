package com.gmail.ivanjermakov1.util;

import com.gmail.ivanjermakov1.ImageExtension;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class FileUtils {
	
	public static void saveImageToFile(BufferedImage image, String path, ImageExtension format) {
		File file = new File(path);
		try {
			ImageIO.write(image, format.toString(), file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void clearFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
	}
	
}
