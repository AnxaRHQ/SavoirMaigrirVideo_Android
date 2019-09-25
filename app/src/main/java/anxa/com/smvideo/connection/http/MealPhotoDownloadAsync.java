package anxa.com.smvideo.connection.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.ImageManager;
import anxa.com.smvideo.util.RecipeHelper;

/**
 * Created by elaineanxa on 20/09/2019
 */
public class MealPhotoDownloadAsync extends AsyncTask<String, Void, Bitmap>
{
    private ImageView bmImage;
    private String photoID;
    private ProgressBar progressBar;

    public MealPhotoDownloadAsync(ImageView bmImage,ProgressBar progress, int photoID)
    {
        this.bmImage = bmImage;
        this.photoID = Integer.toString(photoID);
        this.progressBar = progress;
    }

    protected Bitmap doInBackground(String... urls)
    {
        String urldisplay = urls[0];
        urldisplay = AppUtil.CheckImageUrl(urldisplay);
        Bitmap mIcon11 = null;

        try {

            URL uri = new URL(urldisplay);
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            mIcon11 = BitmapFactory.decodeStream(input);

            //InputStream inputStream = new java.net.URL(urldisplay).openStream();
            //mIcon11 = BitmapFactory.decodeStream(inputStream);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return mIcon11;
    }

    protected void onPostExecute(Bitmap result)
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(View.GONE);
        }

        bmImage.setImageBitmap(result);
        ImageManager.getInstance().addImage(photoID, result);
    }
}
