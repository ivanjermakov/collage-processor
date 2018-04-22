public class Main {
	
	public static void main(String[] args) {
		
		CollageProcessor collageProcessor = new CollageProcessor();

//		collageProcessor.prepareCollageImages("images/collage");
//		collageProcessor.sortImagesByRGB("images/collage");
//		collageProcessor.unloadCollageImagesAsBinary("images/collage.dat");
		
//		collageProcessor.loadCollageImagesBinary("images/collage.dat");

		collageProcessor.loadCollageImages("images/collage");
		collageProcessor.loadInitialImage("images/init/0.png");
//		collageProcessor.createCollage("images/result/3.2.png", 300, 200);
		
		collageProcessor.createCollageFromImages("images/result/collage2.png");
	}
}
