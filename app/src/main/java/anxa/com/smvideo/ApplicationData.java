package anxa.com.smvideo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.crashlytics.android.Crashlytics;

import anxa.com.smvideo.activities.MainActivity;
import anxa.com.smvideo.contracts.CoachingVideosContract;
import anxa.com.smvideo.contracts.DietProfilesDataContract;
import anxa.com.smvideo.contracts.MessagesContract;
import anxa.com.smvideo.contracts.MessagesResponseContract;
import anxa.com.smvideo.contracts.QuestionsContract;
import anxa.com.smvideo.contracts.RepasContract;
import anxa.com.smvideo.contracts.ResultsResponseDataContract;
import anxa.com.smvideo.contracts.ShoppingListContract;
import anxa.com.smvideo.contracts.UserDataContract;
import anxa.com.smvideo.contracts.VideoContract;
import anxa.com.smvideo.contracts.WeightGraphContract;
import anxa.com.smvideo.contracts.WeightHistoryContract;
import anxa.com.smvideo.models.RegUserProfile;
import anxa.com.smvideo.util.AppUtil;
import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import anxa.com.smvideo.contracts.RecipeContract;

/**
 * Created by aprilanxa on 19/05/2017.
 */

public class ApplicationData extends Application {

    public enum SelectedFragment{
        Decouvir(0),
        Bilan(1),
        Temoignages(2),
        Recettes(3),
        MonCompte(4),
        Account_Coaching(5),
        Account_Repas(6),
        Account_Recettes(7),
        Account_Carnet(8),
        Account_Messages(9),
        Account_Conseil(10),
        Account_Exercices(11),
        Account_Suivi(12),
        Account_MonCompte(13),
        Account_Apropos(14),
        Home(15);


        private int numVal;

        SelectedFragment(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    private static ApplicationData instance = null;
    public boolean showLandingPage = true;

    public int regId = 27;
    public int currentWeekNumber = 0;
    public int selectedWeekNumber = 0;
    public int applicationId = 1;
    public String accountType = "account";
    public String userName = "User";
    public RegUserProfile regUserProfile;
    private Context context;

    public static long minimumAnxamatsSessionTime = 3000; //3 seconds
    public static long maximumAnxamatsSessionTime = 45000; //45 seconds
    public static long maximSessionTime = 300000; //300 seconds
    public static  float minWeight = 40;
    public static  float maxWeight = 200;

    private static final String PROPERTY_APP_LOGIN = "isLogin";
    private static final String PROPERTY_APP_LOGIN_USERNAME = "userName";
    private static final String PROPERTY_APP_LOGIN_PASSWORD = "password";
    private static final String PROPERTY_APP_ANXAMATS_SESSIONSTART = "anaxamatsSessionStart";
    private static final String PROPERTY_APP_SESSIONTIME = "sessopmTo,e";

    private static final String PROPERTY_APP_CURSOR_BEFORE = "cursor_before";
    private static final String PROPERTY_APP_CURSOR_PREVIOUS = "cursor_previous";

    public SelectedFragment selectedFragment = SelectedFragment.Decouvir;

    public Hashtable<String, Bitmap> recipePhotoList = new Hashtable<String, Bitmap>();
    public Hashtable<String, Bitmap> videoPhotoList = new Hashtable<String, Bitmap>();
    public List<RecipeContract> recipeList = new ArrayList<>();
    public List<RecipeContract> recipeAccountList = new ArrayList<>();
    public List<QuestionsContract> questionsList = new ArrayList<>();
    public List<VideoContract> discoverVideoList = new ArrayList<>();
    public List<VideoContract> testimonialVideoList = new ArrayList<>();
    public List<MessagesContract> messagesList = new ArrayList<>();

    /** account **/
    public UserDataContract userDataContract = null;
    public DietProfilesDataContract dietProfilesDataContract = null;

    public List<CoachingVideosContract> coachingVideoList = new ArrayList<>();
    public List<VideoContract> conseilsVideoList = new ArrayList<>();
    public List<VideoContract> exerciseVideoList = new ArrayList<>();
    public List<WeightGraphContract> weightGraphContractList = new ArrayList<>();
    public List<RepasContract> repasContractArrayList = new ArrayList<>();
    public List<ShoppingListContract> shoppingListContractArrayList = new ArrayList<>();
    public List<String> categoryList = new ArrayList<>();
    public List<String> shoppingCategoryList = new ArrayList<>();
    public ResultsResponseDataContract bilanminceurResults = new ResultsResponseDataContract();
    public RecipeContract selectedRelatedRecipe = new RecipeContract();

    public WeightHistoryContract currentWeight;
    public WeightHistoryContract initialWeightContract;

    public String currentSelectedCategory;

    public String currentDateRangeDisplay;
    public Date currentDateRangeDisplay_date;
    public Date currentDateRangeDisplay_date2;

    public boolean fromArchive = false;
    public boolean fromArchiveConseils = false;

    public String pageTitle = "Page Title";
    public String customAgent = "";

    //messages
    public MessagesResponseContract messagesResponseContract = new MessagesResponseContract();
    public String urlClicked;
    public Hashtable<String, Bitmap> coachPhotosList = new Hashtable<String, Bitmap>();
    public BitmapFactory.Options options_Avatar = new BitmapFactory.Options();



    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        instance = this;
    }


    public ApplicationData() {
        super();
    }


    public static ApplicationData getInstance() {
        return instance;
    }

    public boolean isLoggedIn(Context context) {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getBoolean(PROPERTY_APP_LOGIN, false);

    }

    private SharedPreferences getGCMPreferences(Context context) {
        return getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    public void saveLoginCredentials(String username, String password) {
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_APP_LOGIN_PASSWORD, password);
        editor.putString(PROPERTY_APP_LOGIN_USERNAME, username);
        editor.commit();
    }

    public String getSavedUserName() {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getString(PROPERTY_APP_LOGIN_USERNAME, "");
    }

    public String getSavedPassword() {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getString(PROPERTY_APP_LOGIN_PASSWORD, "");
    }

    public void clearLoginCredentials() {
        //called when user logged out
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_APP_LOGIN_PASSWORD, "");
//        editor.putString(PROPERTY_APP_LOGIN_USERNAME, "");
        editor.commit();
    }

    public void clearAllLoginCredentials() {
        //called when user logged out
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_APP_LOGIN_PASSWORD, "");
        editor.putString(PROPERTY_APP_LOGIN_USERNAME, "");
        editor.commit();
    }

    public void setIsLogin(Context context, Boolean isLogin) {

        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(PROPERTY_APP_LOGIN, isLogin);
        editor.commit();
    }

    public long getAnxamatsSessionStart() {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getLong(PROPERTY_APP_ANXAMATS_SESSIONSTART, 0);

    }

    public void setAnxamatsSessionStart(Context context, long value) {

        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PROPERTY_APP_ANXAMATS_SESSIONSTART, value);
        editor.commit();
    }

    public long getSessionTime() {
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getLong(PROPERTY_APP_SESSIONTIME, 0);

    }

    public void setSessionTime(Context context, long value) {

        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PROPERTY_APP_SESSIONTIME, value);
        editor.commit();
    }

    public long getBeforeDate(){
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getLong(PROPERTY_APP_CURSOR_BEFORE, 0);
    }

    public void setBeforeDate(long beforeDate){
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PROPERTY_APP_CURSOR_BEFORE, beforeDate);
        editor.commit();
    }

    public long getPreviousDate(){
        final SharedPreferences prefs = getGCMPreferences(context);
        return prefs.getLong(PROPERTY_APP_CURSOR_PREVIOUS, 0);
    }

    public void setPreviousDate(long previousDate){
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(PROPERTY_APP_CURSOR_PREVIOUS, previousDate);
        editor.commit();
    }


    public Bitmap getUserProfilePhoto() {
        return userProfilePhoto;
    }

    public void setUserProfilePhoto(Bitmap userProfilePhoto) {
        this.userProfilePhoto = userProfilePhoto;
    }

    private Bitmap userProfilePhoto;


}
