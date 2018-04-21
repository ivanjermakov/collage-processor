public class Main {
	
	public static void main(String[] args) {
		
		CollageProcessor collageProcessor = new CollageProcessor();
		
		collageProcessor.loadCollageImages("images/collage");
		collageProcessor.loadInitialImage("images/init/1.jpg");
		collageProcessor.createCollage("images/result/1.png", 500, 300, true);
		
		collageProcessor.loadInitialImage("images/init/2.jpg");
		collageProcessor.createCollage("images/result/2.png", 500, 300, true);
		
		collageProcessor.loadInitialImage("images/init/3.jpg");
		collageProcessor.createCollage("images/result/3.png", 500, 300, true);
		
		collageProcessor.loadInitialImage("images/init/4.jpg");
		collageProcessor.createCollage("images/result/4.png", 500, 300, true);
		
	}
}
