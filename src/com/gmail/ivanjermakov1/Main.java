package com.gmail.ivanjermakov1;

public class Main {
	
	public static void main(String[] args) {
		
		CollageProcessor collageProcessor = new CollageProcessor();

		collageProcessor.loadCollageImages("images/collage");
		collageProcessor.loadInitialImage("images/init/1.jpg");
		collageProcessor.createCollage("images/result/1.png", 400, 600);
	}
}
