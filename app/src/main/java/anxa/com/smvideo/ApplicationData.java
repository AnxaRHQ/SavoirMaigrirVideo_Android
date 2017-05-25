package anxa.com.smvideo;

import android.app.Application;
import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import anxa.com.smvideo.contracts.RecipeContract;

/**
 * Created by aprilanxa on 19/05/2017.
 */

public class ApplicationData extends Application {

    private static ApplicationData instance = null;
    public Hashtable<String, Bitmap> recipePhotoList = new Hashtable<String, Bitmap>();
    public List<RecipeContract> recipeList = new ArrayList<>();
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }


    public ApplicationData() {
        super();
    }


    public static ApplicationData getInstance() {
        return instance;
    }

}
