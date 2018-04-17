package anxa.com.smvideo.connection.http;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Set;
import java.util.concurrent.Executor;

import anxa.com.smvideo.common.CommandConstants;
import anxa.com.smvideo.common.SavoirMaigrirVideoConstants;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.contracts.BaseContract;
import anxa.com.smvideo.util.AppUtil;

/**
 * Created by angelaanxa on 5/23/2017.
 */

public class SavoirMaigrirVideoApiClient {
    public  <T extends BaseContract> void GetAsync(AsyncResponse asyncResponse, String apiName, MasterCommand command, Class<T> classType) {
        AnxacoachingGetAsnyc client = new AnxacoachingGetAsnyc(asyncResponse, FormatUri(apiName, "get", command), classType);
        client.execute(new String()); //temporarily pass string since we already built the url
    }

    //to be used only for listview inside a fragment
    public  <T extends BaseContract> void GetAsync(AsyncResponse asyncResponse, String apiName, MasterCommand command, Class<T> classType, Executor executor) {
        AnxacoachingGetAsnyc client = new AnxacoachingGetAsnyc(asyncResponse, FormatUri(apiName, "get", command), classType);
        client.executeOnExecutor(executor, new String());
    }

    public  <T extends BaseContract> void GetAsync(AsyncResponse asyncResponse, String apiName, MasterCommand command, Hashtable params, Class<T> classType, Executor executor) {
        AnxacoachingGetAsnyc client = new AnxacoachingGetAsnyc(asyncResponse, FormatUri(apiName, "get", command, params), classType);
        client.executeOnExecutor(executor, new String());
    }

    public  <T extends BaseContract> void PostAsync(AsyncResponse asyncResponse, String apiName, MasterCommand command, String json, Class<T> classType, Executor executor) {
        AnxacoachingPostAsync client = new AnxacoachingPostAsync(asyncResponse, FormatUri(apiName, "post", command), classType);
        System.out.println("json: " + json);
        client.executeOnExecutor(executor, json);
    }
    public  <T extends BaseContract> void PostAsync(AsyncResponse asyncResponse, String apiName, MasterCommand command, String json, Hashtable params, Class<T> classType, Executor executor) {
        AnxacoachingPostAsync client = new AnxacoachingPostAsync(asyncResponse, FormatUri(apiName, "post", command, params), classType);
        System.out.println("json: " + json);
        client.executeOnExecutor(executor, json);
    }
    public String FormatUri(String apiName, String httpMethod,  MasterCommand command)  {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").encodedAuthority(WebkitURL.domainURL.replace("http://", ""));
        builder.appendPath(SavoirMaigrirVideoConstants.SM_VIDEO_API_PATH);
        if(apiName != null && !apiName.isEmpty())
        {
            builder.appendPath(apiName);
        }
        if(command.Command != null && !command.Command.isEmpty())
        {
            builder.appendPath(command.Command);
        }
        if(command.RegId > 0)
        {
            builder.appendQueryParameter("regId", String.valueOf(command.RegId));
        }

        if(command.ApplicationId > 0)
        {
            builder.appendQueryParameter("applicationId", String.valueOf(command.ApplicationId));
        }

        if(command.RegEmail != null && !command.RegEmail.isEmpty())
        {
            builder.appendQueryParameter("regEmail", command.RegEmail);
        }

        if (command.Command.equalsIgnoreCase(CommandConstants.ACCOUNT_LOGIN)) {
            if (command.IncludeData) {
                builder.appendQueryParameter("includeData", "true");
            } else {
                builder.appendQueryParameter("includeData", "false");
            }
        }


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(httpMethod);
        stringBuilder.append(apiName);
        if(command.Command != null && command.Command.length() > 0){
            stringBuilder.append("/" + command.Command);
        }

        if(command.RegId > 0){
            stringBuilder.append(String.valueOf(command.RegId));
        }

        if(command.RegEmail != null && !command.RegEmail.isEmpty())
        {
            stringBuilder.append(command.RegEmail);
        }

        stringBuilder.append(SavoirMaigrirVideoConstants.SHARED_KEY);

        String toHash = stringBuilder.toString();

        System.out.println("toHash: " + toHash);
        String sig = null;
        try {
            sig = AppUtil.SHA1(toHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        builder.appendQueryParameter("sig", sig);

        return builder.build().toString();
    }

    public String FormatUri(String apiName, String httpMethod,  MasterCommand command,  Hashtable params)  {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("http").encodedAuthority(WebkitURL.domainURL.replace("http://", ""));
        builder.appendPath(SavoirMaigrirVideoConstants.SM_VIDEO_API_PATH);
        if(apiName != null && !apiName.isEmpty())
        {
            builder.appendPath(apiName);
        }

        if(command.Command != null && !command.Command.isEmpty())
        {
            builder.appendPath(command.Command);
        }
        if(command.RegId > 0)
        {
            builder.appendQueryParameter("regId", String.valueOf(command.RegId));
        }

        if(command.ApplicationId > 0)
        {
            builder.appendQueryParameter("applicationId", String.valueOf(command.ApplicationId));
        }

        Set<String> keys = params.keySet();
        for (String p : keys) {
            if(p != "command"){
                builder.appendQueryParameter(p, String.valueOf(params.get(p)));
            }
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(httpMethod);
        stringBuilder.append(apiName);
        if(command.Command != null && command.Command.length() > 0){
            stringBuilder.append("/" + command.Command);
        }
        if(command.RegId > 0){
            stringBuilder.append(String.valueOf(command.RegId));
        }
        if(command.ApplicationId > 0){
            stringBuilder.append(String.valueOf(command.ApplicationId));
        }
        stringBuilder.append(SavoirMaigrirVideoConstants.SHARED_KEY);

        String toHash = stringBuilder.toString();
        String sig = null;
        try {
            sig = AppUtil.SHA1(toHash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        builder.appendQueryParameter("sig", sig);
        return builder.build().toString();

    }

    public  <T extends BaseContract> void PostBitmapAsyncMeal(AsyncBitmapResponse asyncResponse, String apiName, MasterCommand command, Bitmap bitmap, Class<T> classType, int index, boolean forUpload) {
        AnxacoachingPostBitmapAsync client = new AnxacoachingPostBitmapAsync(asyncResponse, FormatUri(apiName, "post", command),bitmap, index, forUpload, classType);
//        client.execute(json);
        // System.out.println("json: " + json);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            client.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            client.execute();
    }
}
