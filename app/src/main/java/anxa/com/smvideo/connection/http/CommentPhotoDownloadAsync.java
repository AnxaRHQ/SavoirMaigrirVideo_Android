package anxa.com.smvideo.connection.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

import anxa.com.smvideo.ApplicationData;

/**
 * Created by elaineanxa on 26/09/2019
 */
public class CommentPhotoDownloadAsync extends AsyncTask<String, Void, Bitmap>
{
    private ImageView bmImage;
    private String path;

    public CommentPhotoDownloadAsync(ImageView bmImage)
    {
        this.bmImage = bmImage;
        this.path = bmImage.getTag().toString();
    }

    @Override
    protected Bitmap doInBackground(String... urls)
    {
        String urldisplay = urls[0];
        String coachIdToSave = urldisplay.substring(urldisplay.indexOf("users/") + 6, urldisplay.lastIndexOf("/"));

        Bitmap mIcon11 = null;

        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);

            if (!ApplicationData.getInstance().coachCommentsPhotosList.containsKey(coachIdToSave) && mIcon11 != null)
            {
                ApplicationData.getInstance().coachCommentsPhotosList.put(coachIdToSave, mIcon11);
            }

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        return mIcon11;
    }

    protected void onPostExecute(Bitmap result)
    {
        if (!bmImage.getTag().toString().equals(path)) {
            return;
        }

        bmImage.setVisibility(View.VISIBLE);

        if (result != null)
        {
           bmImage.setImageBitmap(result);
        }
    }
}
