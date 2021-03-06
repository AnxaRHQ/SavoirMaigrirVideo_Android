package anxa.com.smvideo.connection;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Hashtable;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.common.CommandConstants;
import anxa.com.smvideo.common.SavoirMaigrirVideoConstants;
import anxa.com.smvideo.connection.http.AnxamatsClient;
import anxa.com.smvideo.connection.http.AsyncBitmapResponse;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.connection.http.MasterCommand;
import anxa.com.smvideo.connection.http.SavoirMaigrirVideoApiClient;
import anxa.com.smvideo.contracts.BMContract;
import anxa.com.smvideo.contracts.BMQuestionsResponseContract;
import anxa.com.smvideo.contracts.BMResultsResponseContract;
import anxa.com.smvideo.contracts.BMVideoResponseContract;
import anxa.com.smvideo.contracts.BaseContract;
import anxa.com.smvideo.contracts.Carnet.GetCarnetSyncContract;
import anxa.com.smvideo.contracts.Carnet.MealCommentContract;
import anxa.com.smvideo.contracts.Carnet.MealPlanForDayResponseContract;
import anxa.com.smvideo.contracts.Carnet.UploadMealsDataContract;
import anxa.com.smvideo.contracts.Carnet.UploadMealsDataResponseContract;
import anxa.com.smvideo.contracts.CoachingVideosResponseContract;
import anxa.com.smvideo.contracts.CurrentBannerResponseContract;
import anxa.com.smvideo.contracts.GetAlertsResponseContract;
import anxa.com.smvideo.contracts.Graph.GetStepDataResponseContract;
import anxa.com.smvideo.contracts.Graph.StepDataContract;
import anxa.com.smvideo.contracts.LoginContract;
import anxa.com.smvideo.contracts.MessageRatingContract;
import anxa.com.smvideo.contracts.MessagesResponseContract;
import anxa.com.smvideo.contracts.Notifications.GetNotificationsContract;
import anxa.com.smvideo.contracts.Notifications.MarkNotificationAsReadContract;
import anxa.com.smvideo.contracts.PaymentConfirmationContract;
import anxa.com.smvideo.contracts.PaymentOrderGoogleContract;
import anxa.com.smvideo.contracts.PaymentOrderResponseContract;
import anxa.com.smvideo.contracts.PersonnalisationContract;
import anxa.com.smvideo.contracts.PhotoUploadDataResponseContract;
import anxa.com.smvideo.contracts.PostAnxamatsContract;
import anxa.com.smvideo.contracts.PostMessagesContract;
import anxa.com.smvideo.contracts.RecipeResponseContract;
import anxa.com.smvideo.contracts.RegisterUserResponseContract;
import anxa.com.smvideo.contracts.RegistrationResponseContract;
import anxa.com.smvideo.contracts.RepasResponseContract;
import anxa.com.smvideo.contracts.ShoppingListResponseContract;
import anxa.com.smvideo.contracts.TVPaymentOrderContract;
import anxa.com.smvideo.contracts.TVRegistrationContinueContract;
import anxa.com.smvideo.contracts.TVRegistrationContract;
import anxa.com.smvideo.contracts.TVRegistrationUpdateContract;
import anxa.com.smvideo.contracts.UserDataContract;
import anxa.com.smvideo.contracts.UserDataResponseContract;
import anxa.com.smvideo.contracts.VideoResponseContract;
import anxa.com.smvideo.contracts.WeightGraphResponseContract;
import anxa.com.smvideo.contracts.WeightHistoryContract;
import anxa.com.smvideo.contracts.WeightHistoryResponseContract;
import anxa.com.smvideo.models.RegUserProfile;
import anxa.com.smvideo.util.AppUtil;

/**
 * Created by angelaanxa on 5/23/2017.
 */

public class ApiCaller
{
    private MasterCommand masterCommand;
    private SavoirMaigrirVideoApiClient apiClient;
    private Gson gson;
    private AnxamatsClient anxamatsClient;
    {
        gson = new Gson();
        apiClient = new SavoirMaigrirVideoApiClient();
    }

    public ApiCaller()
    {
        masterCommand = new MasterCommand();
        anxamatsClient = new AnxamatsClient();
    }

    public void PostAnxamatsActiveTime(AsyncResponse asyncResponse, long activeTimeMilliseconds, int userId)
    {
        PostAnxamatsContract contract = new PostAnxamatsContract();
        contract.ApplicationId = SavoirMaigrirVideoConstants.ANXAMATS_APPLICATIONID;
        contract.ApplicationUserId = userId;
        contract.EventId = AnxamatsEvents.ANXAMATS_EVENTS_ACTIVETIME.getValue();
        contract.LogDetails = AppUtil.SortableDateTimeNow();
        contract.EventValue = String.valueOf(activeTimeMilliseconds);

        anxamatsClient.PostAsync(asyncResponse, gson.toJson(contract), PostAnxamatsContract.class) ;
    }

    public void GetFreeDiscover(AsyncResponse asyncResponse)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.FREE_DISCOVER;

        apiClient.GetAsync(asyncResponse, CommandConstants.API_VIDEOS, command, VideoResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetFreeTestimonials(AsyncResponse asyncResponse)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.FREE_TESTIMONIALS;

        apiClient.GetAsync(asyncResponse, CommandConstants.API_VIDEOS, command, VideoResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetFreeRecipes(AsyncResponse asyncResponse)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.FREE_RECIPES;

        apiClient.GetAsync(asyncResponse, CommandConstants.API_RECIPES, command, RecipeResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetBilanMinceurVideo(AsyncResponse asyncResponse)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.BILANMINCEUR_VIDEO;

        apiClient.GetAsync(asyncResponse, CommandConstants.API_BILANMINCEUR, command, BMVideoResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetBilanMinceurQuestions(AsyncResponse asyncResponse, String Gender)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.BILANMINCEUR_QUESTIONS;

        Hashtable params = new Hashtable();
        params.put("gender", Gender);

        apiClient.GetAsync(asyncResponse, CommandConstants.API_BILANMINCEUR, command, params, BMQuestionsResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void PostBilanMinceurQuestions(AsyncResponse asyncResponse, BMContract contract)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.BILANMINCEUR_RESULTS;

        apiClient.PostAsync(asyncResponse, CommandConstants.API_BILANMINCEUR, command, gson.toJson(contract), BMResultsResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }


    /**
     * Account
     **/

    public void PostLogin(AsyncResponse asyncResponse, LoginContract loginContract)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_LOGIN;
        command.RegEmail = loginContract.Email;
        command.IncludeData = true;

        apiClient.PostAsync(asyncResponse, CommandConstants.API_USER, command, gson.toJson(loginContract), UserDataResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetAccountUserData(AsyncResponse asyncResponse)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = ApplicationData.getInstance().regId;

        Hashtable params = new Hashtable();
        params.put("applicationId", ApplicationData.getInstance().applicationId);
        params.put("includeData", true);

        apiClient.GetAsync(asyncResponse, CommandConstants.API_USER, command, params, UserDataResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void PostUpdateAccountUserData(AsyncResponse asyncResponse, UserDataContract userDataContract)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = ApplicationData.getInstance().regId;
        command.Command = CommandConstants.ACCOUNT_EDIT_PROFILE;

        apiClient.PostAsync(asyncResponse, CommandConstants.API_USER, command, gson.toJson(userDataContract), UserDataResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetAccountCoaching(AsyncResponse asyncResponse, int currentWeek)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_COACHING +"/"+ ApplicationData.getInstance().regId + "/" + currentWeek;
        command.RegId = ApplicationData.getInstance().regId;

        apiClient.GetAsync(asyncResponse, CommandConstants.API_VIDEOS, command, CoachingVideosResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetAccountConseils(AsyncResponse asyncResponse)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_CONSEILS;

        apiClient.GetAsync(asyncResponse, CommandConstants.API_VIDEOS, command, VideoResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetAccountExercice(AsyncResponse asyncResponse)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_EXERCICE;

        apiClient.GetAsync(asyncResponse, CommandConstants.API_VIDEOS, command, VideoResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetVideos(AsyncResponse asyncResponse)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_VIDEOS;
        Hashtable params = new Hashtable();
        params.put("catId", "");
        apiClient.GetAsync(asyncResponse, CommandConstants.API_VIDEOS, command, params, VideoResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public void GetAccountRepas(AsyncResponse asyncResponse, int weekNumber, int dayNumber)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_REPAS;
        command.RegId = ApplicationData.getInstance().regId;

        Hashtable params = new Hashtable();
        params.put("mealPlan", ApplicationData.getInstance().dietProfilesDataContract.MealPlanType);
        params.put("calorieType", ApplicationData.getInstance().dietProfilesDataContract.CalorieType);
        params.put("weekNumber", weekNumber);
        params.put("dayNumber", dayNumber);

        apiClient.GetAsync(asyncResponse, CommandConstants.API_TV, command, params, RepasResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void PostPersonnalisation(AsyncResponse asyncResponse, String regId, PersonnalisationContract perso)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = Integer.parseInt(regId);
        command.Command = "personnalisation";

        apiClient.PostAsync(asyncResponse, "registration", command, gson.toJson(perso), PersonnalisationContract.class, AsyncTask.THREAD_POOL_EXECUTOR);;
    }

    private final int COMMAND_QUESTIONSLIMIT = 10;

    /**
     * @param asyncResponse
     * @param userId        - regId of user
     * @param after         - unix timestamp, the value of this is the latest timestamp record in local storage
     */
    public void GetLatestMessagesThread(AsyncResponse asyncResponse, int userId, int after)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_MESSAGES;
        command.RegId = ApplicationData.getInstance().regId;

        Hashtable params = new Hashtable();
        params.put("regId", userId);
        params.put("command", CommandConstants.COMMAND_QUESTIONSGETCURSOR);
        params.put("after", after > 0 ? after : System.currentTimeMillis() / 1000L);
        params.put("limit", COMMAND_QUESTIONSLIMIT);

        apiClient.GetAsync(asyncResponse, CommandConstants.API_HELP, command, params, MessagesResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * @param asyncResponse
     * @param userId        regid
     * @param before        - unix timestamp, the value of this is the oldest timestamp record in local storage
     */
    public void GetPreviousMessagesThread(AsyncResponse asyncResponse, int before)
    {
        MasterCommand command = new MasterCommand();
//        command.Command = CommandConstants.ACCOUNT_MESSAGES;
        command.RegId = ApplicationData.getInstance().regId;

        Hashtable params = new Hashtable();
//        params.put("regId", ApplicationData.getInstance().regId);
//        params.put("command", CommandConstants.COMMAND_QUESTIONSGETCURSOR);
        params.put("before", before > 0 ? before : System.currentTimeMillis() / 1000L);
        params.put("limit", COMMAND_QUESTIONSLIMIT);

        apiClient.GetAsync(asyncResponse, CommandConstants.ACCOUNT_MESSAGES, command, params, MessagesResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * @param asyncResponse
     * @param contract      message contract, should not be null
     */

    public void PostMessage(AsyncResponse asyncResponse, PostMessagesContract contract)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = ApplicationData.getInstance().regId;
        command.Command = CommandConstants.COMMAND_QUESTIONSNEW;

        apiClient.PostAsync(asyncResponse, "message", command, gson.toJson(contract), MessagesResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetAccountShoppingList(AsyncResponse asyncResponse, int weekNumber, int dayNumber)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_SHOPPING_LIST;
        command.RegId = ApplicationData.getInstance().regId;

        Hashtable params = new Hashtable();
        params.put("mealProfile", ApplicationData.getInstance().dietProfilesDataContract.MealPlanType);
        params.put("calorieType", ApplicationData.getInstance().dietProfilesDataContract.CalorieType);
        params.put("weekNumber", weekNumber);

        apiClient.GetAsync(asyncResponse, CommandConstants.API_TV, command, params, ShoppingListResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetAccountRecipeCtid(AsyncResponse asyncResponse, int recipeCtid, int returnMealDay)
    {
        MasterCommand command = new MasterCommand();
//        command.Command = CommandConstants.ACCOUNT_RECIPE_CTID;

        Hashtable params = new Hashtable();
        params.put("recipeCtid", recipeCtid);
        params.put("returnMealDay", returnMealDay);

        apiClient.GetAsync(asyncResponse, CommandConstants.API_RECIPES, command, params, RecipeResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetAccountRecettes(AsyncResponse asyncResponse, int recipeType)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_RECIPES;
        command.RegId = ApplicationData.getInstance().regId;

        Hashtable params = new Hashtable();
        params.put("recipeType", recipeType);
        apiClient.GetAsync(asyncResponse, CommandConstants.API_RECIPES, command, params, RecipeResponseContract.class, AsyncTask.SERIAL_EXECUTOR);
    }

    public void GetAccountGraphData(AsyncResponse asyncResponse)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_GRAPH_DATA;
        command.RegId = ApplicationData.getInstance().regId;

        Hashtable params = new Hashtable();
        params.put("applicationId", ApplicationData.getInstance().applicationId);

        apiClient.GetAsync(asyncResponse, CommandConstants.API_TV, command, params, WeightGraphResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetAccountGraphHistory(AsyncResponse asyncResponse)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_GRAPH_HISTORY;
        command.RegId = ApplicationData.getInstance().regId;

        Hashtable params = new Hashtable();
        params.put("applicationId", ApplicationData.getInstance().applicationId);

        apiClient.GetAsync(asyncResponse, CommandConstants.API_TV, command, params, WeightHistoryResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void PostWeight(AsyncResponse asyncResponse, WeightHistoryContract weightGraphContract)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_POST_WEIGHT;
        command.RegId = ApplicationData.getInstance().regId;
        command.ApplicationId = ApplicationData.getInstance().applicationId;

        apiClient.PostAsync(asyncResponse, CommandConstants.API_TV, command, gson.toJson(weightGraphContract), WeightHistoryResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void EditWeight(AsyncResponse asyncResponse, WeightHistoryContract weightGraphContract)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.ACCOUNT_EDIT_WEIGHT;
        command.RegId = ApplicationData.getInstance().regId;
        command.ApplicationId = ApplicationData.getInstance().applicationId;

        apiClient.PostAsync(asyncResponse, CommandConstants.API_TV, command, gson.toJson(weightGraphContract), WeightHistoryResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetAllSteps(AsyncResponse asyncResponse, int userId, String dateFrom, String dateTo)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = ApplicationData.getInstance().regId;

        Hashtable params = new Hashtable();
        params.put("from", dateFrom);
        params.put("to", dateTo);

        apiClient.GetAsync(asyncResponse, CommandConstants.ACCOUNT_GETALLSTEPS, command, params, GetStepDataResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void PostAddSteps(AsyncResponse asyncResponse, int userId, String deviceType, StepDataContract contract)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = userId;
        command.DeviceType = deviceType;

        apiClient.PostAsync(asyncResponse, CommandConstants.ACCOUNT_POST_STEP, command, gson.toJson(contract), StepDataContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void PostEditSteps(AsyncResponse asyncResponse, int userId, StepDataContract contract)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = userId;
        command.Command = CommandConstants.ACCOUNT_EDIT_STEP;

        apiClient.PostAsync(asyncResponse, CommandConstants.ACCOUNT_POST_STEP, command, gson.toJson(contract), StepDataContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void PostDeleteSteps(AsyncResponse asyncResponse, int userId, StepDataContract contract)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = userId;
        command.Command = CommandConstants.ACCOUNT_DELETE_STEP;

        apiClient.PostAsync(asyncResponse, CommandConstants.ACCOUNT_POST_STEP, command, gson.toJson(contract), StepDataContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void PostGoogleOrder(AsyncResponse asyncResponse, TVPaymentOrderContract tvPaymentOrderContract)
    {
        MasterCommand command = new MasterCommand();
        //command.RegId = Integer.parseInt(regId);
        Hashtable params = new Hashtable();
        params.put("orderEmail", tvPaymentOrderContract.email);

        command.Command = CommandConstants.COMMAND_GOOGLE_ORDER;

        apiClient.PostAsync(asyncResponse, "tv/payment", command, gson.toJson(tvPaymentOrderContract), params, PaymentOrderResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);;
    }

    public void PostConfirmGoogleOrder(AsyncResponse asyncResponse, PaymentConfirmationContract paymentConfirmationContract)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.COMMAND_GOOGLE_ORDER_CONFIRM;
        Hashtable params = new Hashtable();
        params.put("confirmPaymentId", paymentConfirmationContract.PaymentId);
        apiClient.PostAsync(asyncResponse, "tv/payment", command, gson.toJson(paymentConfirmationContract), params, BaseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);;
    }

    public void PostGoogleOrderUpdate(AsyncResponse asyncResponse, PaymentOrderGoogleContract paymentOrderGoogleContract, int regId)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.COMMAND_GOOGLE_ORDER_UPDATE;
        command.RegId = regId;

        apiClient.PostAsync(asyncResponse, "tv/payment", command, gson.toJson(paymentOrderGoogleContract), PaymentOrderResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);;
    }

    public void PostRegistration(AsyncResponse asyncResponse, TVRegistrationContract tvRegistrationContract, String email)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.COMMAND_REGISTRATION;
        command.RegEmail = email;

        apiClient.PostAsync(asyncResponse, CommandConstants.API_REGISTRATION, command, gson.toJson(tvRegistrationContract), RegistrationResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);;
    }

    public void PostRegistration(AsyncResponse asyncResponse, RegUserProfile userProfile)
    {
        MasterCommand command = new MasterCommand();
        command.Email = userProfile.getEmail();
        command.RegEmail = userProfile.getEmail();
        command.Command = CommandConstants.COMMAND_REGISTRATION;

        apiClient.PostAsync(asyncResponse, CommandConstants.API_REGISTRATION, command, gson.toJson(userProfile), RegistrationResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);;
    }

    public void PostRegistrationUpdate(AsyncResponse asyncResponse, TVRegistrationUpdateContract tvRegistrationContract, int regid)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.COMMAND_REGISTRATIONUPDATE;
        command.RegId = regid;

        apiClient.PostAsync(asyncResponse, CommandConstants.API_REGISTRATION, command, gson.toJson(tvRegistrationContract), BaseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);;
    }

    public void PostRegisterContinue(AsyncResponse asyncResponse, TVRegistrationContinueContract tvRegistrationContinueContract)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.COMMAND_REGISTRATIONCONTINUE;
        command.RegId = tvRegistrationContinueContract.regId;
         apiClient.PostAsync(asyncResponse, CommandConstants.API_REGISTRATION, command, gson.toJson(tvRegistrationContinueContract), BaseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void PostCarnetSync(AsyncResponse asyncResponse, int userId, UploadMealsDataContract contract)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = userId;
        command.Command = CommandConstants.COMMAND_CARNETSYNC;

        apiClient.PostAsync(asyncResponse, "meal", command, gson.toJson(contract), UploadMealsDataResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * @param asyncResponse
     * @param userId
     * @param dateFrom
     * @param dateTo
     */
    public void GetCarnetSync(AsyncResponse asyncResponse, int userId, String dateFrom, String dateTo)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = ApplicationData.getInstance().regId;

        Hashtable params = new Hashtable();
        params.put("dateFrom", dateFrom);
        params.put("dateTo", dateTo);

        apiClient.GetAsync(asyncResponse, CommandConstants.COMMAND_MEALSYNC, command, params, GetCarnetSyncContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void postMealComment(AsyncResponse asyncResponse, int userId, String mealID, MealCommentContract contract)
    {
        MasterCommand command = new MasterCommand();
        command.RegId   = ApplicationData.getInstance().regId;
        command.mealId  = mealID;
        command.Command = "comment";

        apiClient.PostAsync(asyncResponse, CommandConstants.COMMAND_MEALCOMMENT, command, gson.toJson(contract), MealCommentContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetMealPlanForDay(AsyncResponse asyncResponse, int userId, String selectedDate, int mealType)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = ApplicationData.getInstance().regId;

        Hashtable params = new Hashtable();
        params.put("sel", selectedDate);
        params.put("mType", mealType);

        apiClient.GetAsync(asyncResponse, CommandConstants.COMMAND_CARNETMEALPLANFORDAY, command, params, MealPlanForDayResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void CheckRegistration(AsyncResponse asyncResponse, UserDataContract contract)
    {
        MasterCommand command = new MasterCommand();
        command.Command = CommandConstants.COMMAND_REGISTRATIONCHECK;
        command.RegEmail = contract.Email;
        apiClient.PostAsync(asyncResponse, CommandConstants.API_REGISTRATION, command, gson.toJson(contract), UserDataContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public enum AnxamatsEvents
    {
        ANXAMATS_EVENTS_ACTIVETIME(1);

        private final int value;

        private AnxamatsEvents(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public void PostUploadMealPhoto(AsyncBitmapResponse asyncResponse, int userId, Bitmap bitmap, int index, boolean forUpload)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = userId;
        command.Command = CommandConstants.COMMAND_UPLOADPHOTO;
        apiClient.PostBitmapAsyncMeal(asyncResponse, "photo", command, bitmap, PhotoUploadDataResponseContract.class, index, forUpload);
    }

    public void PostRating(AsyncResponse asyncResponse, String regId, MessageRatingContract contract)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = ApplicationData.getInstance().userDataContract.Id;
        command.Command = "postRating";

        apiClient.PostAsync(asyncResponse, "message", command, gson.toJson(contract), MessageRatingContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void PostRequestCode(AsyncResponse asyncResponse, String telNum, String regId)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = Integer.parseInt(regId);
        command.Command = CommandConstants.COMMAND_SEND;

        JSONObject json = new JSONObject();
        try {

            json.put("phone", telNum);
            json.put("code", "");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        apiClient.PostAsync(asyncResponse, "accesscode", command, json.toString(), RegisterUserResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);;
    }

    public void PostValidateCode(AsyncResponse asyncResponse, String telNum, String regId, String code)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = Integer.parseInt(regId);
        command.Command = CommandConstants.COMMAND_VALIDATE;

        JSONObject json = new JSONObject();
        try {

            json.put("phone", telNum);
            json.put("code", code);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        apiClient.PostAsync(asyncResponse, "accesscode", command, json.toString(), RegisterUserResponseContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetUserAlerts(AsyncResponse asyncResponse, int userId)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = userId;
        command.Command = "alert-with-webinars";

        apiClient.GetAsync(asyncResponse, "alert", command, GetAlertsResponseContract.class);
    }

    public void GetNotificationsThread(AsyncResponse asyncResponse, int before)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = ApplicationData.getInstance().userDataContract.Id;

        Hashtable params = new Hashtable();
        params.put("before", before > 0 ? before : System.currentTimeMillis() / 1000L);

        apiClient.GetAsync(asyncResponse, CommandConstants.COMMAND_GETNOTIFICATIONS, command, params, GetNotificationsContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void MarkNotificationAsRead(AsyncResponse asyncResponse, int notificationId)
    {
        MasterCommand command = new MasterCommand();
        command.RegId = ApplicationData.getInstance().userDataContract.Id;
        command.Command = CommandConstants.COMMAND_MARKNOTIFASREAD;

        JSONObject commentObj = new JSONObject();
        try
        {
            commentObj.put("notification_id", notificationId);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        apiClient.PostAsync(asyncResponse, CommandConstants.COMMAND_GETNOTIFICATIONS, command, commentObj.toString(), MarkNotificationAsReadContract.class, AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void GetCurrentBanner(AsyncResponse asyncResponse, int userId) {

        MasterCommand command = new MasterCommand();
        command.RegId = userId;
        command.Command = CommandConstants.COMMAND_CURRENTBANNERGET;

        apiClient.GetAsync(asyncResponse, "alert", command, CurrentBannerResponseContract.class);
    }
}
