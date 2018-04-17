package anxa.com.smvideo.connection.http;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;


public class AnxacoachingPostBitmapAsync extends AsyncTask<String,String, String> {
    AnxacoachingBitmapHttpRequest jsonParser = new AnxacoachingBitmapHttpRequest();
    public AsyncBitmapResponse Delegate;
    private ProgressDialog pDialog;
    private String ApiUrl = "";
    Gson gson = new Gson();
    Bitmap bitmap;
    int Index = 0;
    boolean ForUpload = false;

    private static final String TAG_MESSAGE = "message";
    Class<?> classType;

    public AnxacoachingPostBitmapAsync(String url)
    {
        ApiUrl = url;

    }


    public AnxacoachingPostBitmapAsync(AsyncBitmapResponse delegate, String url, Bitmap bmp, Class<?> ct)
    {
        ApiUrl = url;
        classType = ct;
        Delegate = delegate;
        bitmap = bmp;
    }


    public AnxacoachingPostBitmapAsync(AsyncBitmapResponse delegate, String url, Bitmap bmp, int index, boolean forUpload, Class<?> ct)
    {
        ApiUrl = url;
        classType = ct;
        Delegate = delegate;
        bitmap = bmp;
        Index = index;
        ForUpload = forUpload;
    }


    @Override
    protected void onPreExecute() {

    }



    @Override
    protected String doInBackground(String... params) {

        try {


            Log.d("request", "starting");

            return jsonParser.makeHttpRequest( ApiUrl , bitmap);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    @Override
    protected void onPostExecute(String response) {

        Object obt = gson.fromJson(response, classType);
        System.out.println("onPostExecute: " + response);
        System.out.println("onPostExecute1 : " + obt);
        Delegate.processFinish(obt, Index, ForUpload);

    }
}
