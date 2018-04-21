public class Main {
	
	public static void main(String[] args) {
		
		CollageProcessor collageProcessor = new CollageProcessor();
		
		collageProcessor.loadInitialImage("images/init/img.jpg");
		
		collageProcessor.saveImageToFile(collageProcessor.initialImage, "images/result/img.jpg");
		
		
//		collageProcessor.loadCollageImages("");
		
	}
}
