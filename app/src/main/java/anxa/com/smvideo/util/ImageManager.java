package anxa.com.smvideo.util;

import android.graphics.Bitmap;

import java.util.HashMap;

public class ImageManager {
	
	public HashMap<String, Bitmap> bitmapListing = new HashMap<String, Bitmap>();
	Bitmap bmp =  null;
	static ImageManager instance;
	
	public static ImageManager getInstance(){
	if (instance == null)
		instance = new ImageManager();
	return instance;
	}
	
	public Bitmap findImage(String url){
		bmp = bitmapListing.get(url);
		return bmp;
	}
	
	public void clearList(){
		bitmapListing = null;
		System.gc();
		bitmapListing = new HashMap<String, Bitmap>();
	}
	
	synchronized public void addImage(String photoid, Bitmap bmp){
		//scale image first to make sure we are using and saving an optimized version
		if (bmp!=null && photoid !=null)
			bitmapListing.put(photoid.trim(), bmp);
		
		
	}
	
}
