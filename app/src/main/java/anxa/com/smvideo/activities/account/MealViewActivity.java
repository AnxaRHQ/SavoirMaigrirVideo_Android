package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.InputStream;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.MainActivity;
import anxa.com.smvideo.activities.NpnaOfferActivity;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.connection.http.MealPhotoDownloadAsync;
import anxa.com.smvideo.contracts.Carnet.CommentGroupContract;
import anxa.com.smvideo.contracts.Carnet.FoodGroupContract;
import anxa.com.smvideo.contracts.Carnet.MealCommentContract;
import anxa.com.smvideo.contracts.Carnet.MealContract;
import anxa.com.smvideo.contracts.Carnet.PhotoContract;
import anxa.com.smvideo.contracts.Carnet.UploadMealsDataContract;
import anxa.com.smvideo.ui.CarnetCommentListLayout;
import anxa.com.smvideo.ui.CustomDialog;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.ImageManager;

/**
 * Created by aprilanxa on 18/01/2017.
 */

public class MealViewActivity extends Activity implements View.OnClickListener, PopupMenu.OnMenuItemClickListener
{
    MealContract currentMeal = new MealContract();
    CarnetCommentListLayout commentlist;

    ImageView backButton;

    TextView menuButton;

    TextView tv_desc;
    TextView tv_time;
    TextView tv_title;
    TextView submit_tv;
    EditText comment_et;
    ProgressBar progressBar;
    ImageView iv_mainPhoto;
    ImageView[] iv_thumbnails;
    PopupMenu popupMenu;

    CustomDialog dialog;

    int selectedPhotoIndex = 0;

    private ApiCaller caller;

    private final static int MENU_EDIT_MEAL = 11;
    private final static int MENU_DELETE_MEAL = 15;
    private final static int MENU_CANCEL = 16;

    private final static int FG_BEVERAGES = 1;
    private final static int FG_VEGETABLES = 2;
    private final static int FG_SWEETS = 3;
    private final static int FG_FRUITS = 4;
    private final static int FG_CEREALS = 5;
    private final static int FG_PROTEINS = 6;
    private final static int FG_DAIRIES = 7;

    private final String COMMAND_DELETED = "Deleted";

    private String intentExtra;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        currentMeal = ApplicationData.getInstance().currentMealView;

        intentExtra = getIntent().getStringExtra("STACK_STATUS");

        caller = new ApiCaller();

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.mymeals_view);

        submit_tv = (TextView) findViewById(R.id.btnSubmit);
        submit_tv.setOnClickListener(this);

        comment_et = (EditText) findViewById(R.id.comment_et);

        if (currentMeal!=null)
        {
            //update header title
            ((TextView) findViewById(R.id.header_title_tv)).setText(AppUtil.getFRMonthDay(AppUtil.toDateGMT(currentMeal.MealCreationDate)));
        }

        progressBar = (ProgressBar)findViewById(R.id.mealProgressBar);

        // init listview
        commentlist = (CarnetCommentListLayout) findViewById(R.id.commentlist);

        if (currentMeal.Album != null)
            iv_thumbnails = new ImageView[currentMeal.Album.PhotoCount];

        initUI();

        addContent();
    }

    @Override
    public void onClick(View v)
    {
        if (v == backButton)
        {
            finish();
        }
        if (v.getId() == R.id.header_menu_iv) {
            popupMenu.show();
        } else if (v.getId() == R.id.chat_status) {
            if (v.getTag() != null && v.getTag() instanceof CommentGroupContract) {
                CommentGroupContract comment = (CommentGroupContract) v.getTag();
            }
        } else if (v instanceof ImageView) {
            // thumbnail or main photo
            if (v.getId() == R.id.mealphoto) {
                // if photo exist full screen it, else do nothing
                loadFullScreenImage(v);

            } else if (v.getId() == R.id.mealphoto_thumb1
                    || v.getId() == R.id.mealphoto_thumb2
                    || v.getId() == R.id.mealphoto_thumb3
                    || v.getId() == R.id.mealphoto_thumb4
                    || v.getId() == R.id.mealphoto_thumb5) {
                loadFullScreenImage(v);
            }
        } else if (v == submit_tv) {
//             check if the textview has something
            if (comment_et != null && comment_et.getText() != null && comment_et.getText().length() > 0) {

                if (!CheckFreeUser(true))
                {
                    String comment_message = comment_et.getText().toString();
                    comment_et.setText("");
                    createNewComment(comment_message);
                }
            }
        } else if (v.getId() == R.id.CloseButton) {
            dialog.dismiss();
        } else if (v.getId() == R.id.OtherButton) {
            dialog.dismiss();
        }else if (v.getId() == R.id.OtherButton || v.getId() == R.id.YesButton || v.getId() == R.id.NoButton) {
            //delete meal
            if (dialog != null)
                dialog.dismiss();
            if (v.getId() == R.id.YesButton)
                deleteCurrentMeal();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void goBackToMain(View view) {

        if (intentExtra.equalsIgnoreCase("FROM_NOTIFICATION")){
            Intent main = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(main);
            finish();
        }else {
            finish();
        }
    }


    /**
     * PopUp Menu Callback
     **/
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_EDIT_MEAL:
                editMeal(currentMeal);
                break;
            case MENU_DELETE_MEAL:
                showCustomDialog();
                break;
            case MENU_CANCEL:
                break;
        }
        return false;
    }

    private void deleteCurrentMeal() {
        deleteMealToApi();
    }


    /**Private Methods**/

    private void initUI() {

        // title
        tv_desc = (TextView) findViewById(R.id.mealdesc);
        tv_time = (TextView) findViewById(R.id.mealtime);
        tv_title = (TextView) findViewById(R.id.mealtitle);
        iv_mainPhoto = (ImageView) findViewById(R.id.mealphoto);

        tv_title.setText(AppUtil.getMealTitle(this, currentMeal.MealType));
        tv_time.setText(AppUtil.getTimeOnly12(currentMeal.MealCreationDate));

        tv_desc.setText(currentMeal.Description);

        backButton = (ImageView) findViewById(R.id.header_menu_back);
        backButton.setOnClickListener(this);

        menuButton = (TextView)findViewById(R.id.header_menu_iv);
        menuButton.setBackgroundResource(R.drawable.ic_edit);
        menuButton.setOnClickListener(this);

        // check approved /commented UIedit
        if (currentMeal.IsApproved) {
            ((LinearLayout) findViewById(R.id.icon_container_approved)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.approved_text)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.approved_icon)).setVisibility(View.VISIBLE);
            // add spacer too
            ((View) findViewById(R.id.spacer)).setVisibility(View.VISIBLE);

        } else {
            ((LinearLayout) findViewById(R.id.icon_container_approved)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.approved_text)).setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.approved_icon)).setVisibility(View.GONE);
            // remove spacer too
            ((View) findViewById(R.id.spacer)).setVisibility(View.GONE);
        }

        if (currentMeal.IsCommented) {
            ((LinearLayout) findViewById(R.id.icon_container_approved)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.comment_text)).setVisibility(View.VISIBLE);
            ((ImageView) findViewById(R.id.comment_icon)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) findViewById(R.id.comment_text)).setVisibility(View.GONE);
            ((ImageView) findViewById(R.id.comment_icon)).setVisibility(View.GONE);
        }

        // remove all foodgroups
        ((LinearLayout) findViewById(R.id.row1)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_1)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_2)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_3)).setVisibility(View.GONE);

        ((LinearLayout) findViewById(R.id.row2)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_4)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_5)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_6)).setVisibility(View.GONE);

        ((LinearLayout) findViewById(R.id.row3)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_7)).setVisibility(View.GONE);
        ((ImageButton) findViewById(R.id.fg_8)).setVisibility(View.GONE);

        if (currentMeal.FoodGroups != null && currentMeal.FoodGroups.size() > 0) {

            for (int i = 0; i < currentMeal.FoodGroups.size(); i++) {

                int res = getResourceByFoodGroup(currentMeal.FoodGroups.get(i));

                if (i == 0) {
                    ((LinearLayout) findViewById(R.id.row1)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_1)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_1)).setImageResource(res);
                } else if (i == 1) {
                    ((ImageButton) findViewById(R.id.fg_2)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_2)).setImageResource(res);
                } else if (i == 2) {
                    ((ImageButton) findViewById(R.id.fg_3)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_3)).setImageResource(res);
                } else if (i == 3) {
                    ((LinearLayout) findViewById(R.id.row2)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_4)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_4)).setImageResource(res);
                } else if (i == 4) {
                    ((ImageButton) findViewById(R.id.fg_5)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_5)).setImageResource(res);
                } else if (i == 5) {
                    ((ImageButton) findViewById(R.id.fg_6)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_6)).setImageResource(res);
                } else if (i == 6) {
                    ((LinearLayout) findViewById(R.id.row3)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_7)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_7)).setImageResource(res);
                } else if (i == 7) {
                    ((ImageButton) findViewById(R.id.fg_8)).setVisibility(View.VISIBLE);
                    ((ImageButton) findViewById(R.id.fg_8)).setImageResource(res);
                }

            }// end for
        } else {
            ((View) findViewById(R.id.separate)).setVisibility(View.GONE);
        }// end if

        // check if the meal has photos
        iv_mainPhoto = (ImageView) findViewById(R.id.mealphoto);

        //	// hide thumbnail by default
        ((LinearLayout) findViewById(R.id.mealphoto_thumbcontainer)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mealphoto_thumb1_containers)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mealphoto_thumb2_containers)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mealphoto_thumb3_containers)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mealphoto_thumb4_containers)).setVisibility(View.GONE);
        ((LinearLayout) findViewById(R.id.mealphoto_thumb5_containers)).setVisibility(View.GONE);

        if (currentMeal == null || currentMeal.Album == null || currentMeal.Album.PhotoCount <= 0) {
            // update the placeholder photo depending on the meal type
            iv_mainPhoto.setImageResource(AppUtil.getPhotoResource(currentMeal.MealType));
            iv_mainPhoto.setOnClickListener(this);
            progressBar.setVisibility(View.GONE);

        } else {
            // display the bitmap and thumbnail

            List<PhotoContract> photos = currentMeal.Album.Photos;
            if (photos != null && photos.size() > 1) { //do not display thumbnail for just one photo

                ((LinearLayout) findViewById(R.id.mealphoto_thumbcontainer)).setVisibility(View.VISIBLE);

                for (int i = 0; i < iv_thumbnails.length; i++) {
                    if (i == 0) {
                        ((LinearLayout) findViewById(R.id.mealphoto_thumb1_containers)).setVisibility(View.VISIBLE);
                        iv_thumbnails[i] = (ImageView) findViewById(R.id.mealphoto_thumb1);
                    } else if (i == 1) {
                        ((LinearLayout) findViewById(R.id.mealphoto_thumb2_containers)).setVisibility(View.VISIBLE);
                        iv_thumbnails[i] = (ImageView) findViewById(R.id.mealphoto_thumb2);
                    } else if (i == 2) {
                        ((LinearLayout) findViewById(R.id.mealphoto_thumb3_containers)).setVisibility(View.VISIBLE);
                        iv_thumbnails[i] = (ImageView) findViewById(R.id.mealphoto_thumb3);
                    } else if (i == 3) {
                        ((LinearLayout) findViewById(R.id.mealphoto_thumb4_containers)).setVisibility(View.VISIBLE);
                        iv_thumbnails[i] = (ImageView) findViewById(R.id.mealphoto_thumb4);
                    } else if (i == 4) {
                        ((LinearLayout) findViewById(R.id.mealphoto_thumb5_containers)).setVisibility(View.VISIBLE);
                        iv_thumbnails[i] = (ImageView) findViewById(R.id.mealphoto_thumb5);
                    }

                    if (i > photos.size()) {
                        iv_thumbnails[i].setImageResource(AppUtil.getPhotoResource(currentMeal.MealType)); // use

                    } else {
                        updatePhotoThumb(photos.get(i), null, iv_thumbnails[i], currentMeal.MealType);
                    }

                    iv_thumbnails[i].setOnClickListener(this);
                    iv_thumbnails[i].setTag(i);// set photo index as tag;
                }
            } else {
                // remove the thumbnails
                ((LinearLayout) findViewById(R.id.mealphoto_thumbcontainer)).setVisibility(View.GONE);
            }

            // set the selected image
            if (photos.get(selectedPhotoIndex) != null) {
                Bitmap bmp_main = ImageManager.getInstance().findImage(Integer.toString(photos.get(selectedPhotoIndex).PhotoId));
                if (bmp_main==null)
                    new MealPhotoDownloadAsync(iv_mainPhoto, progressBar, photos.get(selectedPhotoIndex).PhotoId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,photos.get(selectedPhotoIndex).UrlLarge);
                else
                {
                    iv_mainPhoto.setImageBitmap(bmp_main);
                    progressBar.setVisibility(View.GONE);
                }

            }else {
                Bitmap bmp = ImageManager.getInstance().findImage(Integer.toString(photos.get(selectedPhotoIndex).PhotoId));
                if (bmp == null){
                    new MealPhotoDownloadAsync(iv_mainPhoto, progressBar, photos.get(selectedPhotoIndex).PhotoId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,photos.get(selectedPhotoIndex).UrlLarge);
                }

            }

            iv_mainPhoto.setOnClickListener(this);
            iv_mainPhoto.setTag(selectedPhotoIndex);
        }

        popupMenu = new PopupMenu(this, findViewById(R.id.header_menu_iv));
        popupMenu.getMenu().add(Menu.NONE, MENU_EDIT_MEAL, Menu.NONE, getResources().getString(R.string.MENU_EDIT_MEAL));
        popupMenu.getMenu().add(Menu.NONE, MENU_DELETE_MEAL, Menu.NONE, getResources().getString(R.string.MENU_DELETE_MEAL));
        popupMenu.getMenu().add(Menu.NONE, MENU_CANCEL, Menu.NONE, getResources().getString(R.string.NOTIFICATIONS_CANCEL));
        popupMenu.setOnMenuItemClickListener(this);
    }

    private void createNewComment(String comment_message) {
        MealCommentContract comment = new MealCommentContract();
        comment.Id = currentMeal.MealId;
        comment.Text = comment_message;
        comment.Timestamp = AppUtil.getCurrentDateinLongGMT();
        currentMeal.Comments.Comments.add(comment);

        commentlist = (CarnetCommentListLayout) findViewById(R.id.commentlist);

        hideKeyboard();

        commentlist.updateData(currentMeal.Comments.Comments);

        nonBlockingUI(comment);
    }

    private void hideKeyboard() {
        // hide virtual keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comment_et.getWindowToken(), 0);
    }

    public void nonBlockingUI(final MealCommentContract comment) {
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                submitComment(comment);

            }
        });
    }

    public void submitComment(MealCommentContract currentcomment) {

//        String username = Integer.toString(ApplicationData.getInstance().userDataContract.Id);
//        if (username != null) {
//            //pass a valid meal data of the regid used
//
//            caller.PostComment(new AsyncResponse() {
//                @Override
//                public void processFinish(Object output) {
//                    Log.d("PostComment", output.toString());
//
//                }
//            }, Integer.parseInt(ApplicationData.getInstance().userProfile.getReg_id()), currentMeal.MealId, currentcomment);
//        }
    }


    private int getResourceByFoodGroup(FoodGroupContract fg) {
        switch (fg.FoodGroupId+1) {
            case FG_BEVERAGES:
                return R.drawable.btn_fg_drinks;
            case FG_VEGETABLES:
                return R.drawable.btn_fg_vegetables;
            case FG_SWEETS:
                return R.drawable.btn_fg_sweets;
            case FG_FRUITS:
                return R.drawable.btn_fg_fruits;
            case FG_CEREALS:
                return R.drawable.btn_fg_starches;
            case FG_PROTEINS:
                return R.drawable.btn_fg_protein;
            case FG_DAIRIES:
                return R.drawable.btn_fg_dairy;
            default:
                return R.drawable.btn_fg_fat;
        }
    }

    public ImageView updatePhotoThumb(PhotoContract photo, ProgressBar progressBar, ImageView item, int type) {
        /*if (photo != null && photo.UrlLarge != null)
        {
            if (progressBar != null)
            {
                progressBar.setVisibility(View.VISIBLE);
            }

            new DownloadImageTask(item, progressBar, photo.PhotoId).execute(photo.UrlLarge);
        }
        else */

        if (photo != null)
        {
            //progressBar.setVisibility(View.GONE);

            // try getting on the ImageManager
            Bitmap bmp = ImageManager.getInstance().findImage(Integer.toString(photo.PhotoId));
            if (bmp == null)
                //item.setImageResource(AppUtil.getPhotoResource(type)); // use
                // dummy
                new MealPhotoDownloadAsync(item, progressBar, photo.PhotoId).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,photo.UrlLarge);
            else
                item.setImageBitmap(bmp);

        } else {
            item.setImageResource(AppUtil.getPhotoResource(type)); // use
            //progressBar.setVisibility(View.GONE);
        }
        return item;
    }


    private void editMeal(MealContract meal) {
        ApplicationData.getInstance().currentMealView = meal;

        Intent mainIntent;
        mainIntent = new Intent(this, MealAddActivity.class);
        mainIntent.putExtra("MEAL_STATUS", "edit");
        mainIntent.putExtra("MEAL_TYPE", meal.MealType);
        this.startActivityForResult(mainIntent, ApplicationData.REQUESTCODE_MEALEDIT);
    }

    private void showCustomDialog() {
        dialog = new CustomDialog(this, null, getResources().getString(R.string.btn_yes), getResources().getString(R.string.btn_no), false, getResources().getString(R.string.ALERTMESSAGE_DELETE_MEAL), null, this);
        dialog.show();
    }

    public void loadFullScreenImage(View v) {
        // TODO Auto-generated method stub
        // click event for photo
        if (v.getTag() == null)
            return;
    }

    private void addContent()
    {
        if (currentMeal.Album != null)
            iv_thumbnails = new ImageView[currentMeal.Album.Photos.size()];

        //sort this messages here
        if (currentMeal.Comments.Comments.size() > 0) {
            commentlist.initData(currentMeal.Comments.Comments, this, this, null);
            ((View)findViewById(R.id.separate2)).setVisibility(View.VISIBLE);
        }else{
            ((View)findViewById(R.id.separate2)).setVisibility(View.GONE);
        }

        // init fg container
        if (currentMeal.FoodGroups == null || currentMeal.FoodGroups.size() == 0) {
            ((LinearLayout) findViewById(R.id.fg_container)).setVisibility(View.GONE);
        } else {
            ((LinearLayout) findViewById(R.id.fg_container)).setVisibility(View.VISIBLE);
        }
    }

    private void deleteMealToApi()
    {
        //pass a valid water data of the regid used
        UploadMealsDataContract contract = new UploadMealsDataContract();
        currentMeal.Command = COMMAND_DELETED;
        contract.Meals.add(currentMeal);

        caller.PostCarnetSync(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                Log.d("PostMeal", output.toString());

                //broadcast the update
                Intent broadInt = new Intent();
                broadInt.setAction(getResources().getString(R.string.meallist_getmeal_week));
                sendBroadcast(broadInt);

                finish();
            }
        }, ApplicationData.getInstance().regId,contract);
    }

    /* Free Users */

    public boolean CheckFreeUser(boolean withDialog)
    {
        if (ApplicationData.getInstance().userDataContract.MembershipType == 0 && ApplicationData.getInstance().userDataContract.WeekNumber > 1)
        {
            if (withDialog)
            {
                showFreeExpiredDialog();
            }

            return true;
        }

        return false;
    }

    private void showFreeExpiredDialog()
    {
        final Dialog freeExpiredDialog = new Dialog(this);
        freeExpiredDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        freeExpiredDialog.setContentView(R.layout.free_expired_dialog);
        freeExpiredDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((TextView) freeExpiredDialog.findViewById(R.id.dialog_content)).setText(getString(R.string.FREE_1WEEKTRIAL_EXPIRED));

        ((Button) freeExpiredDialog.findViewById(R.id.dialog_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                freeExpiredDialog.dismiss();
            }
        });

        ((Button) freeExpiredDialog.findViewById(R.id.dialog_payment)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                freeExpiredDialog.dismiss();
                goToPremiumPayment();
            }
        });

        freeExpiredDialog.show();
    }

    private void goToPremiumPayment()
    {
        Intent mainContentBrowser = new Intent(this, NpnaOfferActivity.class);
        mainContentBrowser.putExtra("UPGRADE_PAYMENT", true);
        startActivity(mainContentBrowser);
    }
}
