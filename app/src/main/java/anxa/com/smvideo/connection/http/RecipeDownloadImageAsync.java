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
import java.util.ArrayList;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.RecipeHelper;

/**
 * Created by angelaanxa on 5/30/2017.
 */

public class RecipeDownloadImageAsync extends AsyncTask<String, Void, Bitmap>
{
    private ImageView bmImage;
    private String path;
    private int Id;
    private ProgressBar progressBar;

    public RecipeDownloadImageAsync(ImageView bmImage,ProgressBar progress, int id)
    {
        this.bmImage = bmImage;
        this.path = bmImage.getTag().toString();
        this.Id = id;
        this.progressBar = progress;
    }

    protected Bitmap doInBackground(String... urls)
    {
        String urldisplay = urls[0];
        urldisplay = AppUtil.CheckImageUrl(urldisplay);
        Bitmap mIcon11 = null;

        try {

            if (!ApplicationData.getInstance().recipePhotoList.containsKey(String.valueOf(Id)))
            {
                InputStream inputStream = new java.net.URL(urldisplay).openStream();

                if (inputStream != null)
                {
                    mIcon11 = BitmapFactory.decodeStream(inputStream);
                    ApplicationData.getInstance().recipePhotoList.put(String.valueOf(Id), mIcon11);
                }

            } else {
                mIcon11 = RecipeHelper.GetRecipeImage(Id);
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return mIcon11;
    }

    protected void onPostExecute(Bitmap result)
    {
        if (!bmImage.getTag().toString().equals(path))
        {
            return;
        }

        if (result != null && bmImage != null)
        {
            bmImage.setVisibility(View.VISIBLE);
            bmImage.setImageBitmap(result);
            progressBar.setVisibility(View.GONE);
        } else {
            bmImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }
}