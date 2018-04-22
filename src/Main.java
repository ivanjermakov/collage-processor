public class Main {
	
	public static void main(String[] args) {
		
		CollageProcessor collageProcessor = new CollageProcessor();
		
		collageProcessor.loadCollageImages("images/collage");
		collageProcessor.loadInitialImage("images/init/5.jpg");
		collageProcessor.createCollage("images/result/5.png", 300, 200);
		
	}
}
