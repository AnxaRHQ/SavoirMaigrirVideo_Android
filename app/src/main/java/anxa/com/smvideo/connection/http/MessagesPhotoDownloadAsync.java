package anxa.com.smvideo.connection.http;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.util.AppUtil;

/**
 * Created by elaineanxa on 01/10/2019
 */
public class MessagesPhotoDownloadAsync  extends AsyncTask<String, Void, Bitmap>
{
    private ImageView bmImage;
    private String path;

    public MessagesPhotoDownloadAsync(ImageView bmImage)
    {
        this.bmImage = bmImage;
        this.path = bmImage.getTag().toString();
    }

    protected Bitmap doInBackground(String... urls)
    {
        String urldisplay = urls[0];
        String coachIdToSave = urldisplay.substring(urldisplay.indexOf("users/") + 6, urldisplay.lastIndexOf("/"));

        Bitmap mIcon11 = null;

        try
        {
            urldisplay = AppUtil.CheckImageUrl(urldisplay);
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);

            if (!ApplicationData.getInstance().coachPhotosList.containsKey(coachIdToSave) && mIcon11 != null)
            {
                ApplicationData.getInstance().coachPhotosList.put(coachIdToSave, mIcon11);
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
        } else {
            bmImage.setVisibility(View.GONE);
        }
    }
}
