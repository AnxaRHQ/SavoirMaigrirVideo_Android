package anxa.com.smvideo.connection.http;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by angelaanxa on 5/23/2017.
 */

public class AnxacoachingGetAsnyc extends AsyncTask<String,String, String> {
    AnxacoachingHttpRequest jsonParser = new AnxacoachingHttpRequest();
    public AsyncResponse Delegate;
    private ProgressDialog pDialog;
    private String ApiUrl = "";
    Gson gson = new Gson();

    private static final String TAG_MESSAGE = "message";
    Class<?> classType;

    public AnxacoachingGetAsnyc( String url)
    {
        ApiUrl = url;

    }


    public AnxacoachingGetAsnyc( AsyncResponse delegate, String url, Class<?> ct)
    {
        ApiUrl = url;
        classType = ct;
        Delegate = delegate;
        System.out.println("Async url: " + url);
    }




    @Override
    protected void onPreExecute() {

    }



    @Override
    protected  String doInBackground(String... params) {

        try {
            Log.d("request", "starting");

            return jsonParser.makeHttpRequest(ApiUrl , "GET", params[0]);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return  "";
    }

    @Override
    protected void onPostExecute(String response) {

        Object obt = gson.fromJson(response, classType);

        Delegate.processFinish(obt);

    }
}

