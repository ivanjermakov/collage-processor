package com.gmail.ivanjermakov1.collageprocessor;

import com.gmail.ivanjermakov1.collageprocessor.util.FileUtils;
import com.gmail.ivanjermakov1.collageprocessor.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollageProcessor {
	
	private List<com.gmail.ivanjermakov1.collageprocessor.Image> images;
	private BufferedImage initialImage;
	private BufferedImage resultImage;
	private List<com.gmail.ivanjermakov1.collageprocessor.Image> sectors;
	private List<com.gmail.ivanjermakov1.collageprocessor.Image> collage;
	
	public void prepareCollageImages(String directoryPath) {
		final int MAX_HEIGHT = 100;
		
		File directory = new File(directoryPath);
		
		if (directory.isDirectory()) { // make sure it's a directory
			for (final File f : Objects.requireNonNull(directory.listFiles())) {
				BufferedImage image;
				
				try {
					image = ImageIO.read(f);
					if (image == null) break;
					
					System.out.println(f.getName());
					System.out.println(image.getHeight() + " " + image.getWidth());
					if (image.getHeight() > MAX_HEIGHT) {
						image = ImageUtils.resizeImage(image, MAX_HEIGHT * image.getWidth() / image.getHeight(), MAX_HEIGHT);
						
						File outputFile = new File(f.getPath());
						ImageIO.write(image, "jpg", outputFile);
						
						System.out.println("Image \"" + f.getPath() + "\" resized successfully.");
					}
					
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println(directoryPath + " is not a directory");
		}
	}
	
	public void sortImagesByRGB(String directoryPath) {
		List<com.gmail.ivanjermakov1.collageprocessor.Image> images = new ArrayList<>();
		
		File directory = new File(directoryPath);
		
		if (directory.isDirectory()) {
			for (final File file : Objects.requireNonNull(directory.listFiles())) {
				BufferedImage image;
				
				try {
					image = ImageIO.read(file);
					
					if (image != null) {
						images.add(new com.gmail.ivanjermakov1.collageprocessor.Image(image, com.gmail.ivanjermakov1.collageprocessor.Image.averageColor(image)));
					}
					
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		images.sort((o1, o2) -> {
			if (o1.getAverageColor().getRed() == o1.getAverageColor().getRed()) {
				if (o1.getAverageColor().getGreen() == o1.getAverageColor().getGreen()) {
					return o1.getAverageColor().getBlue() - o2.getAverageColor().getBlue();
				} else return o1.getAverageColor().getGreen() - o2.getAverageColor().getGreen();
			} else return o1.getAverageColor().getRed() - o2.getAverageColor().getRed();
		});
		
		for (int i = 0; i < images.size(); i++) {
			FileUtils.saveImageToFile(images.get(i).getImage(), directoryPath + "/" + i + ".jpg", ImageExtension.JPG);
		}
		
		System.out.println("Images sorted successfully.");
	}
	
	public void unloadCollageImagesAsBinary(String binaryFilePath) {
		if (images == null) throw new IllegalArgumentException("No collage images to unload");
		
		List<byte[]> binaryImages = new ArrayList<>();
		
		for (com.gmail.ivanjermakov1.collageprocessor.Image image : images) {
			binaryImages.add(ImageUtils.imageToByteArray(image.getImage()));
		}
		
		try {
			ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(binaryFilePath));
			out.writeObject(binaryImages);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void loadCollageImagesBinary(String binaryFilePath) {
		images = new ArrayList<>();
		
		List<byte[]> binaryImages = new ArrayList<>();
		
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(binaryFilePath));
			binaryImages = (List<byte[]>) in.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for (byte[] binaryImage : binaryImages) {
			BufferedImage image = ImageUtils.byteArrayToImage(binaryImage);
			images.add(new com.gmail.ivanjermakov1.collageprocessor.Image(image, com.gmail.ivanjermakov1.collageprocessor.Image.averageColor(image)));
		}
	}
	
	public void loadInitialImage(String path) {
		try {
			initialImage = ImageIO.read(new File(path));
			
			initialImage = ImageUtils.resizeImage(initialImage, 5000, 5000 * initialImage.getHeight() / initialImage.getWidth());
			
			System.out.println("Image \"" + path + "\" loaded successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadCollageImages(String directoryPath) {
		images = new ArrayList<>();
		
		File directory = new File(directoryPath);
		
		if (directory.isDirectory()) { // make sure it's a directory
			for (final File file : Objects.requireNonNull(directory.listFiles())) {
				BufferedImage image;
				
				try {
					image = ImageIO.read(file);
					
					if (image != null) {
						images.add(new com.gmail.ivanjermakov1.collageprocessor.Image(image, com.gmail.ivanjermakov1.collageprocessor.Image.averageColor(image)));
					}

//					System.out.println("Image \"" + file.getName() + "\" successfully load.");
				
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Collage images successfully load.");
	}
	
	public void createCollage(String path, int rows, int cols) {
		if (initialImage == null) {
			throw new IllegalArgumentException("Init image wasn't initialized.");
		} else if (images == null) {
			throw new IllegalArgumentException("Collage images wasn't initialized.");
		}
		
		setInitialImageSectors(rows, cols);
		
		collage = new ArrayList<>();
		
		for (com.gmail.ivanjermakov1.collageprocessor.Image sector : sectors) {
			collage.add(getSimilarTo(sector));
		}
		
		resultImage = concatenateSectors(collage, rows, cols);
		
		resultImage = ImageUtils.cropBlackBorder(resultImage);
		
		FileUtils.saveImageToFile(resultImage, path, ImageExtension.PNG);
		
		System.out.println("Collage created successfully.");
	}
	
	public void createCollageFromImages(String path) {
		if (images == null) throw new InvalidParameterException("Load images first");
		
		int rows = (int) Math.sqrt(images.size());
		
		FileUtils.saveImageToFile(concatenateSectors(images, rows, rows), path, ImageExtension.PNG);
	}
	
	private com.gmail.ivanjermakov1.collageprocessor.Image getSimilarTo(com.gmail.ivanjermakov1.collageprocessor.Image sector) {
		com.gmail.ivanjermakov1.collageprocessor.Image similar = images.get(images.size() - 1);
		
		for (com.gmail.ivanjermakov1.collageprocessor.Image image : images) {
			if (ImageUtils.getCloseness(image.getAverageColor(), sector.getAverageColor()) < ImageUtils.getCloseness(similar.getAverageColor(), sector.getAverageColor())) {
				similar = image;
			}
		}
		
		return similar;
	}
	
	private void setInitialImageSectors(int rows, int cols) {
		//clear this directory from previous files
//		clearFolder(new File("images/sectors"));
		
		sectors = new ArrayList<>();
		
		System.out.println("Initial image is " + initialImage.getWidth() + "x" + initialImage.getHeight());
		
		double sectorWidth = initialImage.getWidth() / rows;
		double sectorHeight = initialImage.getHeight() / cols;
		
		for (int j = 0; j < cols; j++) {
			for (int i = 0; i < rows; i++) {
				BufferedImage subImage =
						initialImage.getSubimage((int) (i * sectorWidth), (int) (j * sectorHeight), (int) sectorWidth, (int) sectorHeight);
				sectors.add(new com.gmail.ivanjermakov1.collageprocessor.Image(subImage, com.gmail.ivanjermakov1.collageprocessor.Image.averageColor(subImage)));
			}
		}
		
		System.out.println(sectors.size() + " sectors created.");
	}
	
	private BufferedImage concatenateSectors(BufferedImage image, List<com.gmail.ivanjermakov1.collageprocessor.Image> sectors, int rows, int cols) {
		int resultHeight = image.getHeight();
		int resultWidth = image.getWidth();
		
		System.out.println("Collage size: " + resultWidth + "x" + resultHeight);
		
		BufferedImage result = new BufferedImage(resultWidth, resultHeight, BufferedImage.TYPE_INT_RGB);
		
		Graphics g = result.getGraphics();
		
		double sectorWidth = resultWidth / rows;
		double sectorHeight = resultHeight / cols;
		
		int counter = 0;
		for (int j = 0; j < cols; j++) {
			for (int i = 0; i < rows; i++) {
				g.drawImage(sectors.get(counter).getImage(), (int) (i * sectorWidth), (int) (j * sectorHeight), (int) sectorWidth, (int) sectorHeight, null);
				counter++;
			}
		}
		
		return result;
	}
	
	private BufferedImage concatenateSectors(List<Image> sectors, int rows, int cols) {
		int resultHeight = initialImage.getHeight();
		int resultWidth = initialImage.getWidth();
		
		System.out.println("Collage size: " + resultWidth + "x" + resultHeight);
		
		BufferedImage result = new BufferedImage(resultWidth, resultHeight, BufferedImage.TYPE_INT_RGB);
		
		Graphics g = result.getGraphics();
		
		double sectorWidth = resultWidth / rows;
		double sectorHeight = resultHeight / cols;
		
		int counter = 0;
		for (int j = 0; j < cols; j++) {
			for (int i = 0; i < rows; i++) {
				g.drawImage(sectors.get(counter).getImage(), (int) (i * sectorWidth), (int) (j * sectorHeight), (int) sectorWidth, (int) sectorHeight, null);
				counter++;
			}
		}
		
		return result;
	}
	
}
