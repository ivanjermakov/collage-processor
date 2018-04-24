import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollageProcessor {
	
	enum Format {
		PNG,
		JPG
	}
	
	public List<Image> images;
	private BufferedImage initialImage;
	private BufferedImage resultImage;
	private List<Image> sectors;
	private List<BufferedImage> solidSectors;
	private List<Image> collage;
	
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
						image = resizeImage(image, MAX_HEIGHT * image.getWidth() / image.getHeight(), MAX_HEIGHT);
						
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
		List<Image> images = new ArrayList<>();
		
		File directory = new File(directoryPath);
		
		if (directory.isDirectory()) {
			for (final File file : Objects.requireNonNull(directory.listFiles())) {
				BufferedImage image;
				
				try {
					image = ImageIO.read(file);
					
					if (image != null) {
						images.add(new Image(image, Image.averageColor(image)));
					}
					
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		images.sort((o1, o2) -> {
			if (o1.averageColor.getRed() == o1.averageColor.getRed()) {
				if (o1.averageColor.getGreen() == o1.averageColor.getGreen()) {
					return o1.averageColor.getBlue() - o2.averageColor.getBlue();
				} else return o1.averageColor.getGreen() - o2.averageColor.getGreen();
			} else return o1.averageColor.getRed() - o2.averageColor.getRed();
		});
		
		for (int i = 0; i < images.size(); i++) {
			saveImageToFile(images.get(i).image, directoryPath + "/" + i + ".jpg", Format.JPG);
		}
		
		System.out.println("Images sorted successfully.");
	}
	
	public void unloadCollageImagesAsBinary(String binaryFilePath) {
		if (images == null) throw new IllegalArgumentException("No collage images to unload");
		
		List<byte[]> binaryImages = new ArrayList<>();
		
		for (Image image : images) {
			binaryImages.add(imageToByteArray(image.image));
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
			BufferedImage image = byteArrayToImage(binaryImage);
			images.add(new Image(image, Image.averageColor(image)));
		}
	}
	
	public void loadInitialImage(String path) {
		try {
			initialImage = ImageIO.read(new File(path));
			
			initialImage = resizeImage(initialImage, 5000, 5000 * initialImage.getHeight() / initialImage.getWidth());
			
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
						images.add(new Image(image, Image.averageColor(image)));
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
		
		for (Image sector : sectors) {
			collage.add(getSimilarTo(sector));
		}
		
		resultImage = concatenateSectors(collage, rows, cols);
		
		saveImageToFile(resultImage, path, Format.PNG);
		
		System.out.println("Collage created successfully.");
	}
	
	public void createCollageFromImages(String path) {
		if (images == null) throw new InvalidParameterException("Load images first");
		
		int rows = (int) Math.sqrt(images.size());
		
		saveImageToFile(concatenateSectors(images, rows, rows), path, Format.PNG);
	}
	
	
	private Image getSimilarTo(Image sector) {
		Image similar = images.get(images.size() - 1);
		
		for (Image image : images) {
			if (getCloseness(image.averageColor, sector.averageColor) < getCloseness(similar.averageColor, sector.averageColor)) {
				similar = image;
			}
		}
		
		return similar;
	}
	
	private int getCloseness(Color a1, Color a2) {
		return Math.abs(a1.getRed() - a2.getRed()) +
				Math.abs(a1.getGreen() - a2.getGreen()) +
				Math.abs(a1.getBlue() - a2.getBlue());
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
				g.drawImage(sectors.get(counter).image, (int) (i * sectorWidth), (int) (j * sectorHeight), (int) sectorWidth, (int) sectorHeight, null);
				counter++;
			}
		}
		
		return result;
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
				sectors.add(new Image(subImage, Image.averageColor(subImage)));
			}
		}
		
		System.out.println(sectors.size() + " sectors created.");
	}
	
	private BufferedImage copy(BufferedImage source) {
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}
	
	private void setSolidSectors() {
		List<Image> sectors = new ArrayList<>();
		for (Image sector : this.sectors) {
			sectors.add(new Image(copy(sector.image), sector.averageColor));
		}
		
		solidSectors = new ArrayList<>();
		
		for (Image sector : sectors) {
			BufferedImage image = sector.image;
			Graphics2D graphics = image.createGraphics();
			
			graphics.setPaint(sector.averageColor);
			graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
			
			solidSectors.add(image);
		}
	}
	
	private void saveImageToFile(BufferedImage image, String path, Format format) {
		File file = new File(path);
		try {
			ImageIO.write(image, format.toString(), file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private BufferedImage resizeImage(BufferedImage image, int width, int height) {
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
	
	private void clearFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
	}
	
	private byte[] imageToByteArray(BufferedImage image) {
		ByteArrayOutputStream bAOS = new ByteArrayOutputStream();
		try {
			ImageIO.write(image, "jpg", bAOS);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return bAOS.toByteArray();
	}
	
	private BufferedImage byteArrayToImage(byte[] imageData) {
		ByteArrayInputStream bAIS = new ByteArrayInputStream(imageData);
		try {
			return ImageIO.read(bAIS);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
