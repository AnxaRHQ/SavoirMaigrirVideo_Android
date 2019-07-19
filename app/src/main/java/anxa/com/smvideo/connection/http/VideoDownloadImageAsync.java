package anxa.com.smvideo.connection.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.util.VideoHelper;

/**
 * Created by angelaanxa on 5/31/2017.
 */

public class VideoDownloadImageAsync extends AsyncTask<String, Void, Bitmap> {
    private ImageView bmImage;
    private ProgressBar progressBar;
    private String path;
    private String Id;

    public VideoDownloadImageAsync(ImageView bmImage, ProgressBar progress, String id) {
        this.bmImage = bmImage;
        this.path = bmImage.getTag().toString();
        this.Id = id;
        this.progressBar = progress;
    }

    protected Bitmap doInBackground(String... urls) {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        String urldisplay = urls[0];


        Bitmap mIcon11 = null;
        try {
            if (!ApplicationData.getInstance().videoPhotoList.containsKey(String.valueOf(Id))) {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);


                ApplicationData.getInstance().videoPhotoList.put(Id, mIcon11);
            }
            else {
                mIcon11 = VideoHelper.GetVideoImage(String.valueOf(Id));
            }


        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        if (!bmImage.getTag().toString().equals(path)) {
            return;
        }

        if (result != null && bmImage != null) {
            bmImage.setVisibility(View.VISIBLE);
            bmImage.setImageBitmap(result);
            progressBar.setVisibility(View.GONE);

        } else {
            bmImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }
}