import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollageProcessor {
	
	private BufferedImage initialImage;
	private BufferedImage resultImage;
	private List<BufferedImage> collageImages;
	private List<Color> collageImagesColor;
	private List<BufferedImage> sectors;
	private List<Color> sectorsColor;
	private List<BufferedImage> solidSectors;
	
	final int RESULT_HEIGHT = 5000;
	
	public CollageProcessor() {
	}
	
	public void prepareCollageImages(String directoryPath) {
		collageImages = new ArrayList<>();
		
		final int MAX_HEIGHT = 1000;
		
		File directory = new File(directoryPath);
		
		if (directory.isDirectory()) { // make sure it's a directory
			for (final File f : Objects.requireNonNull(directory.listFiles())) {
				BufferedImage image;
				
				try {
					image = ImageIO.read(f);
					
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
	
	public void loadInitialImage(String path) {
		try {
			initialImage = ImageIO.read(new File(path));
			
			System.out.println("Image \"" + path + "\" loaded successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadCollageImages(String DirectoryPath) {
		File directory = new File(DirectoryPath);
		
		if (directory.isDirectory()) { // make sure it's a directory
			for (final File file : Objects.requireNonNull(directory.listFiles())) {
				BufferedImage image;
				
				try {
					image = ImageIO.read(file);
					
					collageImages.add(image);

//					System.out.println("Image \"" + file.getName() + "\" successfully load.");
					
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("Collage images successfully load.");
	}
	
	public void createCollage(int rows, int cols, boolean allowRepetitions) {
		if (initialImage == null || collageImages == null) {
			throw new IllegalArgumentException("Init image wasn't initialized.");
		}
		
		if (!allowRepetitions && collageImages.size() < cols * rows) {
			throw new IllegalArgumentException("Not enough collage images.");
		}
		
		setInitialImageSectors(rows, cols);
		
		setInitialSectorsAverageColor();
		
		setSolidSectors();
		
		resultImage = concatenateSectors(sectors, rows, cols);
		
		saveImageToFile(resultImage, "images/result/img.jpg");
	}
	
	private BufferedImage concatenateSectors(List<BufferedImage> sectors, int rows, int cols) {
		BufferedImage result = new BufferedImage(RESULT_HEIGHT * initialImage.getWidth() / initialImage.getHeight(),
				RESULT_HEIGHT, BufferedImage.TYPE_INT_RGB);
		
		Graphics g = result.getGraphics();
		
		double sectorWidth = result.getWidth() / rows;
		double sectorHeight = result.getHeight() / cols;
		
		int counter = 0;
		for (int j = 0; j < cols; j++) {
			for (int i = 0; i < rows; i++) {
				
				g.drawImage(sectors.get(counter), (int)(i * sectorWidth), (int)(j * sectorHeight), (int)sectorWidth, (int)sectorHeight, null);
				counter++;
			}
		}
		
		return result;
	}
	
	private void setInitialImageSectors(int rows, int cols) {
		//clear this directory from previous files
		clearFolder(new File("images/sectors"));
		
		sectors = new ArrayList<>();
		
		System.out.println("Initial image is " + initialImage.getWidth() + "x" + initialImage.getHeight());
		
		int sectorWidth = initialImage.getWidth() / rows;
		int sectorHeight = initialImage.getHeight() / cols;
		
		for (int j = 0; j < cols; j++) {
			for (int i = 0; i < rows; i++) {
				sectors.add(initialImage.getSubimage(i * sectorWidth, j * sectorHeight, sectorWidth, sectorHeight));
			}
		}
		
		System.out.println(sectors.size() + " sectors created.");
	}
	
	private void setInitialSectorsAverageColor() {
		sectorsColor = new ArrayList<>();
		
		for (BufferedImage sector : sectors) {
			sectorsColor.add(averageColor(sector));
		}
	}
	
	public static BufferedImage copy(BufferedImage source){
		BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}
	
	private void setSolidSectors() {
		List<BufferedImage> sectors = new ArrayList<>();
		for (BufferedImage sector : this.sectors) {
			sectors.add(copy(sector));
		}
		
		solidSectors = new ArrayList<>();
		
		for (int i = 0; i < sectorsColor.size(); i++) {
			BufferedImage image = sectors.get(i);
			Graphics2D graphics = image.createGraphics();
			
			graphics.setPaint(sectorsColor.get(i));
			graphics.fillRect(0, 0, image.getWidth(), image.getHeight());
			
			solidSectors.add(image);
		}
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
	
	private void saveImageToFile(BufferedImage image, String path) {
		File outputFile = new File(path);
		try {
			ImageIO.write(image, "jpg", outputFile);

//			System.out.println("Image \"" + path + "\" saved successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static BufferedImage resizeImage(BufferedImage image, int width, int height) {
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
	
	private static void clearFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				file.delete();
			}
		}
	}
}
