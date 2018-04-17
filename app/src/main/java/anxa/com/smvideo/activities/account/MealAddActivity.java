package anxa.com.smvideo.activities.account;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncBitmapResponse;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.Carnet.FoodGroupContract;
import anxa.com.smvideo.contracts.Carnet.MealContract;
import anxa.com.smvideo.contracts.Carnet.MealPlanForDayResponseContract;
import anxa.com.smvideo.contracts.Carnet.PhotoContract;
import anxa.com.smvideo.contracts.Carnet.UploadMealsDataContract;
import anxa.com.smvideo.contracts.PhotoUploadDataResponseContract;
import anxa.com.smvideo.models.Meal;
import anxa.com.smvideo.ui.CustomDialog;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.ImageManager;

import static anxa.com.smvideo.contracts.Carnet.PhotoContract.PHOTO_STATUS.ONGOING_UPLOADPHOTO;
import static anxa.com.smvideo.contracts.Carnet.PhotoContract.PHOTO_STATUS.SYNC_UPLOADPHOTO;


public class MealAddActivity extends Activity implements OnClickListener, OnTimeChangedListener {

    final int MAX_DESC = 500;
    final int RESULTCODE_CRMDONE = 80;
    final int CAMERA_SELECT = 999;
    final int GALLERY_SELECT = 888;
    final int MEAL_OPTIONS_CODE = 777;

    int DATE_YEAR;
    int DATE_MONTH;
    int DATE_DAY;

    MealContract mealToAdd;

    TextView mealDescriptionCount;
    TextView progressTitle;
    TextView mealTime;

    EditText mealDesc;

    Button done_picker;
    Button btn_retry;
    Button btn_later;
    Button btn_submit;

    ImageButton btn_cancelSaving;

    CheckBox checked;

    CustomDialog dialog;
    AlertDialog.Builder builderFoodGroupTips;
    Dialog dialogfoodGroupTips;
    List<ImageButton> foodGroups;

    ProgressBar progress;
    ProgressBar savingProgressBar;

    RelativeLayout mealTimeContainer;
    RelativeLayout savingLayout;
    LinearLayout progressLayout;
    LinearLayout retryLayout;
    LinearLayout timePickerContainer;
    LinearLayout activeMealLayout;

    LinearLayout mealFoodGroupLayout;
    LinearLayout mealRatingLayout;

    TimePicker timepicker;

    int descRemainCount = 500;
    byte mealViewState;
    int uploadIndex = 0;
    boolean forUpload = false;
    String mealStatus;

    private final String COMMAND_ADDED = "Added";
    private final String COMMAND_UPDATED = "Updated";
    private final String COMMAND_DELETED = "Deleted";

    private ApiCaller caller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final int mealtype = getIntent().getIntExtra("MEAL_TYPE", 1);

        caller = new ApiCaller();

        if(ApplicationData.getInstance().userDataContract != null)
        {
            caller.GetMealPlanForDay(new AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    MealPlanForDayResponseContract response = output != null ? (MealPlanForDayResponseContract) output : new MealPlanForDayResponseContract();
                    mealToAdd.DefaultMealDescription = response.Data.MealPlanText;
                    //mealDesc.setText(mealToAdd.DefaultMealDescription);
                }
            }, ApplicationData.getInstance().userDataContract.Id, AppUtil.getDateStringGetSync(ApplicationData.getInstance().currentSelectedDate), mealtype);

        }

        setContentView(R.layout.mymeals_add);

        // init progressBar
        progress = (ProgressBar) findViewById(R.id.progressBar);

        mealStatus = getIntent().getStringExtra("MEAL_STATUS");

        mealTimeContainer = ((RelativeLayout) findViewById(R.id.meal_time_container));
        mealTimeContainer.setOnClickListener(this);

        btn_submit = (Button) findViewById(R.id.submitButton);
        btn_submit.setOnClickListener(this);
        btn_submit.setEnabled(false);

        done_picker = (Button) findViewById(R.id.date_save_tv);
        done_picker.setBackgroundDrawable(null);
        done_picker.setOnClickListener(this);

        timepicker = (TimePicker) findViewById(R.id.TimePicker);


        timePickerContainer = (LinearLayout) findViewById(R.id.timepickercontainer);

        //retry saving layout
        savingLayout = (RelativeLayout) findViewById(R.id.savingLayout);
        retryLayout = (LinearLayout) findViewById(R.id.retryLayout);
        progressLayout = (LinearLayout) findViewById(R.id.progressLayout);
        activeMealLayout = (LinearLayout) findViewById(R.id.addmealLayout);
        mealFoodGroupLayout = (LinearLayout) findViewById(R.id.meal_food_group_ll);

        progressTitle = (TextView) findViewById(R.id.progressTitle);
        savingProgressBar = (ProgressBar) findViewById(R.id.savingProgressBar);
        savingProgressBar.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.text_orange), PorterDuff.Mode.SRC_IN);

        btn_cancelSaving = (ImageButton) findViewById(R.id.btn_cancelSaving);

        btn_retry = (Button) findViewById(R.id.btn_retry);
        btn_later = (Button) findViewById(R.id.btn_later);

        mealDesc = ((EditText) findViewById(R.id.mealdesc));
        mealDesc.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
                descRemainCount = MAX_DESC - s.length();
                updateMealDescCount();
                if (descRemainCount<MAX_DESC){
                    btn_submit.setEnabled(true);
                }else{
                    if (mealToAdd.Album.Photos.size()==0){
                        btn_submit.setEnabled(false);
                    }else{
                        btn_submit.setEnabled(true);
                    }
                }
            }
        });

        mealDescriptionCount = ((TextView) findViewById(R.id.mealdesccount));
        updateMealDescCount();

            timepicker.setIs24HourView(true);

        timepicker.setOnTimeChangedListener(this);

        mealToAdd = new MealContract();
        mealToAdd.MealType = mealtype;
        mealToAdd.IsApproved = false;
        mealToAdd.IsCommented = false;

        btn_submit.setVisibility(View.VISIBLE);

        mealToAdd.MealCreationDate = AppUtil.dateToLongFormat(ApplicationData.getInstance().currentSelectedDate);

        System.out.println("onCreate MealCreationDate: " + mealToAdd.MealCreationDate);

        String time = AppUtil.getTimeOnly12();//get current time
        mealTime = (TextView) findViewById(R.id.mealtime);
        mealTime.setText(time);
        setTimerPicker(false);

        DATE_YEAR = AppUtil.getIntYearonDate(ApplicationData.getInstance().currentSelectedDate);
        DATE_MONTH = AppUtil.getIntMonthonDate(ApplicationData.getInstance().currentSelectedDate);
        DATE_DAY = AppUtil.getIntDayonDate(ApplicationData.getInstance().currentSelectedDate);

        //update header title
        ((TextView) findViewById(R.id.header_title)).setText(AppUtil.getMonthDay(ApplicationData.getInstance().currentSelectedDate));

        //update meal title
        ((TextView) findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(this, mealtype)); //imageview

        initFoodGroups();

        if (mealStatus.equalsIgnoreCase("edit")) {
            updateUI();
            updatePhotoUI();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                timepicker.setHour(AppUtil.getHour());
                timepicker.setMinute(AppUtil.getMinute());

            } else {
                timepicker.setCurrentHour(AppUtil.getHour());
                timepicker.setCurrentMinute(AppUtil.getMinute());
            }
        }

        checked = (CheckBox) findViewById(R.id.checkBox);
        checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mealDesc.setText(mealToAdd.DefaultMealDescription);
                } else {
                    mealDesc.setText("");
                }
            }
        });
    }


    private void updateUI() {
        mealToAdd = ApplicationData.getInstance().currentMealView;

        String time = AppUtil.getTimeOnly12(mealToAdd.MealCreationDate);

        //set date
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(AppUtil.dateToUnixTimestamp(AppUtil.toDateGMT(mealToAdd.MealCreationDate)) * 1000L);

        DATE_YEAR = c.get(Calendar.YEAR);
        DATE_MONTH = c.get(Calendar.MONTH) + 1;
        DATE_DAY = c.get(Calendar.DAY_OF_MONTH);

        mealTime = (TextView) findViewById(R.id.mealtime);
        mealTime.setText(time);

        mealDesc.setText(mealToAdd.Description);

        descRemainCount = MAX_DESC - mealToAdd.Description.length();

        if (mealToAdd.FoodGroups != null && mealToAdd.FoodGroups.size() > 0) {
            List<FoodGroupContract> foodG = mealToAdd.FoodGroups;
            for (int i = 0; i < foodG.size(); i++) {
                updateFoodGroup(foodG.get(i).FoodGroupId+1);
            }
        }
        if (mealToAdd.Album.Photos != null && mealToAdd.Album.PhotoCount > 0) {
            updatePhotoUI();
        }

    }

    private void initFoodGroups() {

        ((ImageButton) findViewById(R.id.fg_protein)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.fg_starch)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.fg_vegetable)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.fg_fats)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.fg_dairy)).setOnClickListener(this);
        ((ImageButton) findViewById(R.id.fg_fruit)).setOnClickListener(this);

        foodGroups = new ArrayList<>();
        foodGroups.add((ImageButton) findViewById(R.id.fg_protein));
        foodGroups.add((ImageButton) findViewById(R.id.fg_starch));
        foodGroups.add((ImageButton) findViewById(R.id.fg_vegetable));
        foodGroups.add((ImageButton) findViewById(R.id.fg_fats));
        foodGroups.add((ImageButton) findViewById(R.id.fg_dairy));
        foodGroups.add((ImageButton) findViewById(R.id.fg_fruit));

    }

    private void updateFoodGroup(int id) {

        if (id == R.id.fg_protein || id == Meal.FOODGROUP.PROTEIN.getValue()) {
            if (foodGroups.get(0).isSelected())
                foodGroups.get(0).setSelected(false);
            else
                foodGroups.get(0).setSelected(true);
        } else if (id == R.id.fg_starch || id == Meal.FOODGROUP.STARCHES.getValue()) {
            if (foodGroups.get(1).isSelected())
                foodGroups.get(1).setSelected(false);
            else
                foodGroups.get(1).setSelected(true);
        } else if (id == R.id.fg_vegetable || id == Meal.FOODGROUP.VEGETABLE.getValue()) {
            if (foodGroups.get(2).isSelected())
                foodGroups.get(2).setSelected(false);
            else
                foodGroups.get(2).setSelected(true);
        } else if (id == R.id.fg_fats || id == Meal.FOODGROUP.FATS.getValue()) {
            if (foodGroups.get(3).isSelected())
                foodGroups.get(3).setSelected(false);
            else
                foodGroups.get(3).setSelected(true);
        } else if (id == R.id.fg_dairy || id == Meal.FOODGROUP.DAIRY.getValue()) {
            if (foodGroups.get(4).isSelected())
                foodGroups.get(4).setSelected(false);
            else
                foodGroups.get(4).setSelected(true);
        } else if (id == R.id.fg_fruit || id == Meal.FOODGROUP.FRUITS.getValue()) {
            if (foodGroups.get(5).isSelected())
                foodGroups.get(5).setSelected(false);
            else
                foodGroups.get(5).setSelected(true);
        }
    }


    private void updateMealDescCount() {
        mealDescriptionCount.setText(descRemainCount + "/" + MAX_DESC);
    }


    public String UniqueIDgen() {
        UUID uniqueKey = UUID.randomUUID();
        return uniqueKey.toString();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        if (mealViewState == Meal.MEALSTATE_ADD) {
            //delete meal if existing in core data
        }

    }

    private void setTimerPicker(Boolean isPickerShown) {
        if (isPickerShown) {
            //need to update with the current time on the time selected
            done_picker.setVisibility(View.VISIBLE);
            timePickerContainer.setVisibility(View.VISIBLE);

        } else { //hide picker
            done_picker.setVisibility(View.GONE);
            timePickerContainer.setVisibility(View.GONE);
        }
        //set time
    }

    private void showCustomDialog(String message, String title) {

        dialog = new CustomDialog(this, null, null, null, true, message, title, this);
        dialog.show();
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY_SELECT);
    }


    private void cameraPage(int imageCode) {

        Intent mainIntent = new Intent(this, Camera2Activity.class);
        mainIntent.putExtra("MEDIA_TYPE", imageCode);
        startActivityForResult(mainIntent, CAMERA_SELECT);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.fg_dairy || v.getId() == R.id.fg_fruit || v.getId() == R.id.fg_protein ||
                v.getId() == R.id.fg_starch || v.getId() == R.id.fg_fats || v.getId() == R.id.fg_vegetable) {
            updateFoodGroup(v.getId());
        } else if (v.getId() == R.id.CloseButton) {
            if (dialogfoodGroupTips != null)
                dialogfoodGroupTips.dismiss();
            if (dialog != null)
                dialog.dismiss();
        } else if (v.getId() == R.id.meal_time_container) {
            //show time picker
            setTimerPicker(true);
        } else if (v.getId() == R.id.date_save_tv) {
            setTimerPicker(false);
        } else if (v == btn_submit) {
            //submit button
            saveMeal();
        } else if (v.getId() == R.id.btn_close_1 || v.getId() == R.id.btn_close_2 || v.getId() == R.id.btn_close_3 || v.getId() == R.id.btn_close_4 || v.getId() == R.id.btn_close_5) {
            //delete photo
            deleteImage((Integer) v.getTag());
        }
    }

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        Date date = AppUtil.formatDate(view, DATE_MONTH, DATE_DAY, DATE_YEAR);

        System.out.println("onTimeChanged: " + date);

        String time = AppUtil.getTimeOnlyMeals(date.getTime() / 1000);//get current time
        if (mealTime != null)
            mealTime.setText(time);

        mealToAdd.MealCreationDate = AppUtil.dateToLongFormat(date);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULTCODE_CRMDONE) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra("isAdd", true);
                intent.putExtra("LOADCOACH", data.getBooleanExtra("LOADCOACH", false));
                setResult(RESULT_OK, intent);
            }
        } else if (requestCode == 999/*camera page*/) {
            if (resultCode == RESULT_OK) {

                if (data.getStringExtra("ACTIVITY_PHOTO") != null) {

                    Boolean isGallery = data.getBooleanExtra("ACTIVITY_ISGALLERY", false);
                    int inSampleSize = data.getIntExtra("ACTIVITY_SAMPLESIZE", 2);
                    String selectedImageURI = data.getStringExtra("ACTIVITY_PHOTO");
                    File file = new File(selectedImageURI);

                    //if (file.exists()){
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    options.inJustDecodeBounds = true;
                    //options.inSampleSize = 2;

                    try {
                        Bitmap b = null;
                        if (isGallery) {
                            try {
                                b = BitmapFactory.decodeFile(file.getPath(), options);
                            } catch (Exception e) {
                            }

                        } else {

                            System.out.println("camera");
                            ExifInterface ei = new ExifInterface(file.getPath());
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

                            Matrix matrix = new Matrix();
                            switch (orientation) {
                                case ExifInterface.ORIENTATION_NORMAL:

                                case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                                    matrix.setScale(-1, 1);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    matrix.setRotate(180);
                                    break;
                                case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                                    matrix.setRotate(180);
                                    matrix.postScale(-1, 1);
                                    break;
                                case ExifInterface.ORIENTATION_TRANSPOSE:
                                    matrix.setRotate(90);
                                    matrix.postScale(-1, 1);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    matrix.setRotate(90);
                                    break;
                                case ExifInterface.ORIENTATION_TRANSVERSE:
                                    matrix.setRotate(-90);
                                    matrix.postScale(-1, 1);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    matrix.setRotate(-90);
                                    break;
                                default:

                            }


                            //options.inSampleSize = 2;
                            Bitmap btemp = BitmapFactory.decodeFile(Uri.fromFile(file).getPath(), options);



                            final int REQUIRED_SIZE = 1200;

                            int width_tmp = options.outWidth, height_tmp = options.outHeight;
                            int scale = 1;
                            while (true) {
                                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                                    break;
                                }
                                width_tmp /= 2;
                                height_tmp /= 2;
                                scale *= 2;
                            }

                            BitmapFactory.Options o2 = new BitmapFactory.Options();
                            o2.inSampleSize = scale;
                            Bitmap btemp2 = BitmapFactory.decodeFile(Uri.fromFile(file).getPath(), o2);


                            b = Bitmap.createBitmap(btemp2, 0, 0,width_tmp,height_tmp, matrix, true);
                            if (b.getWidth() > ApplicationData.getInstance().maxWidthCameraView || b.getHeight() > ApplicationData.getInstance().maxHeightCameraView) { //max w & h
                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                options.inJustDecodeBounds = true; //4, 8, etc. the more value, the worst quality of image
                                if (b != null)
                                    b.compress(Bitmap.CompressFormat.JPEG, 100, bs);

                                System.out.println("camera: MAX rotate H " + b.getHeight() + "***W" + b.getWidth());
                            }
                        }

                        updateAlbum(b);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (data.getByteArrayExtra("ACTIVITY_PHOTO_BYTE") != null) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    int inSampleSize = data.getIntExtra("ACTIVITY_SAMPLESIZE", 2);
                    options.inSampleSize = inSampleSize;
                    Bitmap b = BitmapFactory.decodeByteArray(data.getByteArrayExtra("ACTIVITY_PHOTO_BYTE"), 0, data.getByteArrayExtra("ACTIVITY_PHOTO_BYTE").length, options);
                    updateAlbum(b);

                    b = null;
                }

            }//result_ok
        } else if (requestCode == GALLERY_SELECT) {
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                try {

                    ByteArrayOutputStream bs = new ByteArrayOutputStream();

                    Bitmap bitmap = decodeUri(selectedImage);
                    if (bitmap != null)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);

                    updateAlbum(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == MEAL_OPTIONS_CODE) {
//            if (ApplicationEx.getInstance().userRatingSetting) {
//                mealRatingLayout.setVisibility(View.VISIBLE);
//                mealFoodGroupLayout.setVisibility(View.GONE);
//            } else {
//                mealRatingLayout.setVisibility(View.GONE);
//                mealFoodGroupLayout.setVisibility(View.VISIBLE);
//            }

        }
        //onActivityResult
    }//end on activity

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inSampleSize = 2;
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 1200;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;

        ByteArrayOutputStream bs = new ByteArrayOutputStream();

        return BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o2);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateAlbum(bm);
    }

    private void updateAlbum(Bitmap b) {
        PhotoContract newPhoto = new PhotoContract();
        newPhoto.image = b;
        newPhoto.state = ONGOING_UPLOADPHOTO;

        if (mealToAdd.Album.Photos == null) {
            mealToAdd.Album.Photos = new ArrayList<PhotoContract>();
        }

        mealToAdd.Album.Photos.add(newPhoto);

        updatePhotoUI();
    }

    public void goToCameraPage(View view) {
        cameraPage(Camera2Activity.REQUEST_CODE_CAMERA);
    }

    public void goToGalleryPage(View view) {
        galleryIntent();
    }

    private void showFoodGroupDialog() {
        LayoutInflater inflater = getLayoutInflater();

        //location dialogs
        if (builderFoodGroupTips == null)
            builderFoodGroupTips = new AlertDialog.Builder(this);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.foodgroup_dialog, null);

        ((ImageView) layout.findViewById(R.id.CloseButton)).setOnClickListener(this);

        builderFoodGroupTips.setView(layout);
        dialogfoodGroupTips = builderFoodGroupTips.create();
        dialogfoodGroupTips.requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogfoodGroupTips.show();
    }

    public void goBackToMain(View view) {
        finish();
    }

    private void saveMeal() {

        //Check completeness . It needs to have atleast one photo or a  text description
        if (mealDesc.getText() == null || mealDesc.getText().length() <= 0) {
            try {
                if (mealToAdd.Album.Photos == null) {
                    showCustomDialog(getResources().getString(R.string.ALERTMESSAGE_MEALDESCRIPTION), getResources().getString(R.string.dashboard_activities_content2));
                    return;
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        List<FoodGroupContract> foodgroup = new ArrayList<>();
        FoodGroupContract newFG;

        for (int i = 0; i < foodGroups.size(); i++) {
            if (((ImageView) foodGroups.get(i)).isSelected()) {
                switch (i) {
                    case 0:
                        newFG = new FoodGroupContract();
                        newFG.FoodGroupId = 6 - 1;
                        foodgroup.add(newFG);
                        break;
                    case 1:
                        newFG = new FoodGroupContract();
                        newFG.FoodGroupId = 5 - 1;
                        foodgroup.add(newFG);
                        break;
                    case 2:
                        newFG = new FoodGroupContract();
                        newFG.FoodGroupId = 2 - 1;
                        foodgroup.add(newFG);
                        break;
                    case 3:
                        newFG = new FoodGroupContract();
                        newFG.FoodGroupId = 8 - 1;
                        foodgroup.add(newFG);
                        break;
                    case 4:
                        newFG = new FoodGroupContract();
                        newFG.FoodGroupId = 7 - 1;
                        foodgroup.add(newFG);
                        break;
                    case 5:
                        newFG = new FoodGroupContract();
                        newFG.FoodGroupId = 4 - 1;
                        foodgroup.add(newFG);
                        break;
                }
            }//end if

        }//end for

        mealToAdd.FoodGroups = foodgroup;

        if (mealDesc.getText() != null)
            mealToAdd.Description = mealDesc.getText().toString();

        Intent intent = new Intent();

        if (mealToAdd.Album.Photos != null && mealToAdd.Album.Photos.size() > 0) {
            intent.putExtra("withphoto", true);
        } else {
            intent.putExtra("withphoto", false);
        }

        intent.putExtra("tempmealid", mealToAdd.MealId);

        if (mealStatus.equalsIgnoreCase("add")) {
            saveMealToApi(COMMAND_ADDED);

            mealToAdd.MealId = 0;

        } else {
            saveMealToApi(COMMAND_UPDATED);
        }
    }

    private void saveMealToApi(String command) {

        System.out.println("saveMealToAPI: " + command + " creationDate: " + mealToAdd.MealCreationDate);
        showSavingLayout(true, false);

        if (mealToAdd.Album.Photos.size() > 0) {

            mealToAdd.Command = command;
            for (int i = 0; i < mealToAdd.Album.Photos.size(); i++) {
                uploadIndex = i;
                if (i + 1 == mealToAdd.Album.Photos.size()) {
                    forUpload = true;
                }
                if (mealToAdd.Album.Photos.get(i).state == ONGOING_UPLOADPHOTO) {

                    caller.PostUploadMealPhoto(new AsyncBitmapResponse() {
                        @Override
                        public void processFinish(Object output, int index, boolean forMealUpload) {
                            // Log.d("PostUploadPhoto", output.toString());
                            PhotoUploadDataResponseContract response = output != null ? (PhotoUploadDataResponseContract) output : new PhotoUploadDataResponseContract();
                            if (response.Data.ImageUrl != null && !response.Data.ImageUrl.isEmpty()) {
                                mealToAdd.Album.Photos.get(index).UrlLarge = response.Data.ImageUrl;
                                mealToAdd.Album.Photos.get(index).state = SYNC_UPLOADPHOTO;
                            } else {
                                mealToAdd.Album.Photos.remove(index);
                            }
                            boolean forApiUpload = true;
                            for(int j = 0; j < mealToAdd.Album.Photos.size(); j++)
                            {
                                if( mealToAdd.Album.Photos.get(j).state != SYNC_UPLOADPHOTO)
                                {
                                    forApiUpload = false;
                                }
                            }
                            if (forApiUpload) {
                                UploadMealsDataContract contract = new UploadMealsDataContract();

                                contract.Meals.add(mealToAdd);
                                caller.PostCarnetSync(new AsyncResponse() {
                                    @Override
                                    public void processFinish(Object output) {
                                        // Log.d("PostMeal", output.toString());
                                        showSavingLayout(false, false);

                                        //broadcast the update
                                        Intent broadInt = new Intent();
                                        broadInt.setAction(getResources().getString(R.string.meallist_getmeal_week));
                                        sendBroadcast(broadInt);

                                        finish();
                                    }
                                }, ApplicationData.getInstance().userDataContract.Id, contract);
                            }
                        }
                    }, ApplicationData.getInstance().userDataContract.Id, mealToAdd.Album.Photos.get(i).image, uploadIndex, forUpload);
                } else {
                    UploadMealsDataContract contract = new UploadMealsDataContract();

                    contract.Meals.add(mealToAdd);
                    caller.PostCarnetSync(new AsyncResponse() {
                        @Override
                        public void processFinish(Object output) {
                            //Log.d("PostMeal", output.toString());
                            showSavingLayout(false, false);

                            //broadcast the update
                            Intent broadInt = new Intent();
                            broadInt.setAction(getResources().getString(R.string.meallist_getmeal_week));
                            sendBroadcast(broadInt);

                            finish();
                        }
                    }, ApplicationData.getInstance().userDataContract.Id, contract);
                }


            }
        } else {
            UploadMealsDataContract contract = new UploadMealsDataContract();
            mealToAdd.Command = command;
            contract.Meals.add(mealToAdd);
            caller.PostCarnetSync(new AsyncResponse() {
                @Override
                public void processFinish(Object output) {
                    // Log.d("PostMeal", output.toString());
                    showSavingLayout(false, false);

                    //broadcast the update
                    Intent broadInt = new Intent();
                    broadInt.setAction(getResources().getString(R.string.meallist_getmeal_week));
                    sendBroadcast(broadInt);

                    finish();
                }
            }, ApplicationData.getInstance().userDataContract.Id, contract);
        }
    }

    private void showSavingLayout(boolean saving, boolean failed) {
        if (saving) {

            ((TextView) findViewById(R.id.header_title)).setText(AppUtil.getMonthDay(AppUtil.toDate(mealToAdd.MealCreationDate)));

            savingLayout.setVisibility(View.VISIBLE);
            activeMealLayout.setEnabled(false);

            if (failed) {
                //if failed, show the retry layout.
                //change text of progress bar to FAILED
                //set progress to 0
                retryLayout.setVisibility(View.VISIBLE);
                progressTitle.setText(getResources().getString(R.string.SAVING_PROGRESS_FAILED));
                savingProgressBar.setIndeterminate(false);
                savingProgressBar.setProgress(0);

            } else {
                retryLayout.setVisibility(View.GONE);
                progressTitle.setText(getResources().getString(R.string.SAVING_PROGRESS_TEXT));
                savingProgressBar.setIndeterminate(true);
                savingProgressBar.setVisibility(View.VISIBLE);
            }
        } else {
            savingLayout.setVisibility(View.GONE);
            activeMealLayout.setEnabled(true);
        }
    }

    private void updatePhotoUI() {
        List<PhotoContract> photos = mealToAdd.Album.Photos;

        if (photos.size()==0 && mealDesc.getText().length()<1){
            btn_submit.setEnabled(false);
        }else{
            btn_submit.setEnabled(true);
        }

        for (int i = 0; i < photos.size(); i++) {

            PhotoContract photo = photos.get(i);

            if (i == 0) { //display thumb at thumb 2

                ((RelativeLayout) findViewById(R.id.ll_imagethumb2)).setVisibility(View.VISIBLE);
                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb2)).setImageBitmap(photo.image);
                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(Integer.toString(photo.PhotoId));
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb2)).setImageResource(AppUtil.getPhotoResource(mealToAdd.MealType)); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb2)).setImageBitmap(bmp); // use
                }

                ((ImageView) findViewById(R.id.btn_close_2)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_2)).setTag(0/*photo index*/);

            } else if (i == 1) { //display thumb at thumb 3
                ((RelativeLayout) findViewById(R.id.ll_imagethumb3)).setVisibility(View.VISIBLE);

                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb3)).setImageBitmap(photo.image);
                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(Integer.toString(photo.PhotoId));
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb3)).setImageResource(AppUtil.getPhotoResource(mealToAdd.MealType)); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb3)).setImageBitmap(bmp); // use

                }

                ((ImageView) findViewById(R.id.btn_close_3)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_3)).setTag(1/*photo index*/);

            } else if (i == 2) { //display thumb at thumb 4

                ((RelativeLayout) findViewById(R.id.ll_imagethumb4)).setVisibility(View.VISIBLE);

                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb4)).setImageBitmap(photo.image);

                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(Integer.toString(photo.PhotoId));
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb4)).setImageResource(AppUtil.getPhotoResource(mealToAdd.MealType)); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb4)).setImageBitmap(bmp); // use
                }

                ((ImageView) findViewById(R.id.btn_close_4)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_4)).setTag(2/*photo index*/);

            } else if (i == 3) { //display thumb at thumb 5

                ((RelativeLayout) findViewById(R.id.ll_imagethumb5)).setVisibility(View.VISIBLE);

                if (photo.image != null)
                    ((ImageView) findViewById(R.id.imagethumb5)).setImageBitmap(photo.image);
                else {
                    Bitmap bmp = ImageManager.getInstance().findImage(Integer.toString(photo.PhotoId));
                    if (bmp == null)
                        ((ImageView) findViewById(R.id.imagethumb5)).setImageResource(AppUtil.getPhotoResource(mealToAdd.MealType)); // use
                    else
                        ((ImageView) findViewById(R.id.imagethumb5)).setImageBitmap(bmp); // use
                }

                ((ImageView) findViewById(R.id.btn_close_5)).setOnClickListener(this);

                ((ImageView) findViewById(R.id.btn_close_5)).setTag(3/*photo index*/);

            } else if (i == 4) { //display thumb at thumb 1
                ((RelativeLayout) findViewById(R.id.ll_imagethumb1)).setVisibility(View.VISIBLE);
                ((RelativeLayout) findViewById(R.id.rl_imagethumb)).setVisibility(View.INVISIBLE);

                if (photo.image != null) {
                    ((ImageView) findViewById(R.id.imagethumb1)).setImageBitmap(photo.image);
                } else {
                    Bitmap bmp = ImageManager.getInstance().findImage(Integer.toString(photo.PhotoId));
                    if (bmp == null) {
                        ((ImageView) findViewById(R.id.imagethumb1)).setImageResource(AppUtil.getPhotoResource(mealToAdd.MealType)); // use
                    } else {
                        ((ImageView) findViewById(R.id.imagethumb1)).setImageBitmap(bmp); // use
                    }
                }

                ((ImageView) findViewById(R.id.btn_close_1)).setOnClickListener(this);
                ((ImageView) findViewById(R.id.btn_close_1)).setTag(4/*photo index*/);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case GALLERY_SELECT:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);//
                    startActivityForResult(Intent.createChooser(intent, "Select File"), GALLERY_SELECT);
                } else {
                    //do something like displaying a message that he didn`t allow the app to access gallery and you wont be able to let him select from gallery
                }

                break;
        }
    }

    private void deleteImage(int photoIndex) {
        ((RelativeLayout) findViewById(R.id.rl_imagethumb)).setVisibility(View.VISIBLE);

        if (mealToAdd.Album.Photos == null) {
            mealToAdd.Album.Photos = new ArrayList<PhotoContract>();
        }

        try {
            mealToAdd.Album.Photos.remove(photoIndex);
        } catch (Exception e) {

        }

        mealToAdd.Album.PhotoCount = mealToAdd.Album.Photos.size();

        System.out.println("deleteImage: " + mealToAdd.Album.Photos);

        clearPhotoUI();
        updatePhotoUI();
    }

    private void clearPhotoUI() {
        ((RelativeLayout) findViewById(R.id.ll_imagethumb2)).setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.ll_imagethumb3)).setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.ll_imagethumb4)).setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.ll_imagethumb5)).setVisibility(View.GONE);
        ((RelativeLayout) findViewById(R.id.ll_imagethumb1)).setVisibility(View.GONE);

    }
}
