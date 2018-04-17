package anxa.com.smvideo.connection.http;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by angelaanxa on 1/19/2017.
 */

public class AnxacoachingBitmapHttpRequest {
    String charset = "UTF-8";
    HttpURLConnection conn;
    DataOutputStream wr;
    StringBuilder result;
    URL urlObj;
    JSONObject jObj = null;
    StringBuilder sbParams;
    String paramsString;
    String boundary = "12345";
    String twohypens = "--";
    String lineend = "\r\n";
    public String makeHttpRequest(String url, Bitmap bitmap)  {


            try {
                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/json");
                conn.setRequestProperty("Accept-Charset", charset);

                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);


                conn.connect();



                wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(twohypens + boundary + lineend);
                wr.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\"; filename=\"" + "anxa.jpg" + "\"" + lineend);
                wr.writeBytes("Content-Type: image/jpeg" + lineend);
                wr.writeBytes(lineend);

                OutputStream os = conn.getOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 10, os);


                wr.writeBytes(lineend);
                wr.writeBytes(twohypens);
                wr.writeBytes(boundary);
                wr.writeBytes(twohypens);
                wr.writeBytes(lineend);
                os.close();
                // wr.writeBytes(json);


            } catch (IOException e) {
                e.printStackTrace();
            }



        try {

            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            wr.flush();
            wr.close();
            Log.d("JSON Parser", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        conn.disconnect();
        // try parse the string to a JSON object
        try {
            jObj = new JSONObject(result.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON Object
        return result.toString();


    }

}
