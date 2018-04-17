package anxa.com.smvideo.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.account.ExerciseActivity;
import anxa.com.smvideo.activities.account.MoodAddActivity;
import anxa.com.smvideo.activities.account.WaterViewActivity;
import anxa.com.smvideo.common.CommonConstants;
import anxa.com.smvideo.contracts.Carnet.ExerciseContract;
import anxa.com.smvideo.contracts.Carnet.MealContract;
import anxa.com.smvideo.contracts.Carnet.MoodContract;
import anxa.com.smvideo.contracts.Carnet.PhotoContract;
import anxa.com.smvideo.contracts.Carnet.WaterContract;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.ImageManager;

public class CarnetList extends ScrollView {

    Context context;

    String inflater = Context.LAYOUT_INFLATER_SERVICE;

    //meals
    LinearLayout llAMSnack;
    LinearLayout llBreakfast;
    LinearLayout llLunch;
    LinearLayout llPMSnack;
    LinearLayout llDinner;
    LinearLayout llWater;

    LayoutInflater layoutInflater;

    //water
    RelativeLayout rlWater;
    ImageView glass_iv;
    TextView numberOfGlasses_tv;
    WaterContract waterEntry;

    //exercise
    LinearLayout llExercise;
    LinearLayout llExerciseWithData;
    ImageView exerciseImageView;
    private ListView workoutListView;
    private LinearLayout isCheckedExercise;
    private TextView exerciseChecked;

    //mood
    LinearLayout llMood;
    RelativeLayout rlMoodFilled;
    ImageView moodImageView;

    OnClickListener listener;

    List<MealContract> items;
    List<MoodContract> moodItems;
    List<ExerciseContract> workOutItems;

    private static final int BREAKFAST = 1;
    private static final int LUNCH = 2;
    private static final int DINNER = 4;
    private static final int AM_SNACK = 5;
    private static final int PM_SNACK = 6;

    public CarnetList(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        onCreateView(null);
    }

    public CarnetList(Context context, AttributeSet attrs) {
        super(context, attrs);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        onCreateView(null);
    }

    public CarnetList(Context context) {
        super(context);
        this.context = context;
        onCreateView(null);
    }

    public boolean initDate(List<MealContract> items, Context context, OnClickListener listener) {
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.items = items;
        this.context = context;
        this.listener = listener;

        return true;
    }


    public void onCreateView(ViewGroup container) {
        View row = null;

        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.carnetlist, null, false);
        }

        llAMSnack = (LinearLayout) row.findViewById(R.id.mealitem_amsnack);
        ((TextView) llAMSnack.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, AM_SNACK));
        ((TextView) llAMSnack.findViewById(R.id.mealtime)).setText("");

        llBreakfast = (LinearLayout) row.findViewById(R.id.mealitem_breakfast);
        ((TextView) llBreakfast.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, BREAKFAST));
        ((TextView) llBreakfast.findViewById(R.id.mealtime)).setText("");

        llLunch = (LinearLayout) row.findViewById(R.id.mealitem_lunch);
        ((TextView) llLunch.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, LUNCH));
        ((TextView) llLunch.findViewById(R.id.mealtime)).setText("");

        llPMSnack = (LinearLayout) row.findViewById(R.id.mealitem_pmsnack);
        ((TextView) llPMSnack.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, PM_SNACK));
        ((TextView) llPMSnack.findViewById(R.id.mealtime)).setText("");

        llDinner = (LinearLayout) row.findViewById(R.id.mealitem_dinner);
        ((TextView) llDinner.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, DINNER));
        ((TextView) llDinner.findViewById(R.id.mealtime)).setText("");

        llExercise = (LinearLayout) row.findViewById(R.id.mealitem_exercise);
        llExerciseWithData = (LinearLayout) row.findViewById(R.id.mealitem_exercisedata);
        llExerciseWithData.setVisibility(LinearLayout.GONE);

        exerciseImageView = (ImageView) llExercise.findViewById(R.id.exercisephoto);
        exerciseImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        workoutListView = (ListView) llExerciseWithData.findViewById(R.id.exerciseListView);
        isCheckedExercise = (LinearLayout) llExerciseWithData.findViewById(R.id.exerciseView_checked);
        isCheckedExercise.setVisibility(View.GONE);

        //exerciseChecked = (TextView) llExerciseWithData.findViewById(R.id.checked);
        //exerciseChecked.setText(context.getString(R.string.EXERCISE));

        llMood = (LinearLayout) row.findViewById(R.id.mealitem_mood);
        rlMoodFilled = (RelativeLayout) row.findViewById(R.id.mealitem_moodfilled);

        moodImageView = (ImageView) llMood.findViewById(R.id.addHapimomentPhoto);
        moodImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        llWater = (LinearLayout) row.findViewById(R.id.mealitem_water);
        rlWater = (RelativeLayout) llWater.findViewById(R.id.waterCell_rl);
        rlWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(v);
            }
        });

        glass_iv = (ImageView) llWater.findViewById(R.id.glass_iv);
        numberOfGlasses_tv = (TextView) llWater.findViewById(R.id.glasses_water);
        numberOfGlasses_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addWater(v);
            }
        });

        try {
            removeView(row);
        } catch (Exception e) {
        }

        addView(row);

        exerciseImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExercise(v);
            }
        });
        moodImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewMood(v);
            }
        });
        rlMoodFilled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMood(v);
            }
        });
    }

    public boolean refreshUI() {

        waterEntry = new WaterContract();

        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

        for (int i = 0; i < ApplicationData.getInstance().waterList.size(); i++) {
            Date waterDate = AppUtil.toDateGMT(ApplicationData.getInstance().waterList.get(i).CreationDate);

            if (fmt.format(ApplicationData.getInstance().currentSelectedDate).equals(fmt.format(waterDate))) {
                waterEntry = ApplicationData.getInstance().waterList.get(i);
                break;
            }

        }
        if (waterEntry.NumberOfGlasses > 0) {
            try {
                ApplicationData.getInstance().currentWater = waterEntry;
                updateWaterItem(waterEntry.NumberOfGlasses, llWater);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            updateWaterItem(0, llWater);
        }

        updateMealItem(llBreakfast, BREAKFAST);
        updateMealItem(llAMSnack, AM_SNACK);
        updateMealItem(llLunch, LUNCH);
        updateMealItem(llPMSnack, PM_SNACK);
        updateMealItem(llDinner, DINNER);

        if (items == null)
            return false;

        for (int i = 0; i < items.size(); i++) {
            MealContract currentMeal = items.get(i);
            if (currentMeal == null)
                return false;

            switch (currentMeal.MealType) {
                case 1:
                    updateMealItem(currentMeal, llBreakfast);
                    break;
                case 5:
                    updateMealItem(currentMeal, llAMSnack);
                    break;
                case 2:
                    updateMealItem(currentMeal, llLunch);
                    break;
                case 6:
                    updateMealItem(currentMeal, llPMSnack);
                    break;
                case 4:
                    updateMealItem(currentMeal, llDinner);
                    break;
                default:
                    break;
            }
        }

        return true;
    }

    public void refreshMoodData() {

        if (moodItems != null && moodItems.size() > 0) {
            rlMoodFilled.setVisibility(VISIBLE);
            llMood.setVisibility(GONE);

            MoodContract moodToday = new MoodContract();
            moodToday = moodItems.get(0);
            ApplicationData.getInstance().currentMood = moodToday;
            updateHapiMood(moodToday.MoodType);
            updateHunger(moodToday.IsHungry != null ? moodToday.IsHungry : false);
            if (moodToday.Message != null && moodToday.Message.length() > 0) {
                ((TextView) rlMoodFilled.findViewById(R.id.moodDescription)).setText(moodToday.Message);
                ((TextView) rlMoodFilled.findViewById(R.id.moodDescription)).setVisibility(VISIBLE);
            } else {
                ((TextView) rlMoodFilled.findViewById(R.id.moodDescription)).setVisibility(GONE);
            }
            // init listview
            CarnetCommentListLayout commentlist = (CarnetCommentListLayout)  rlMoodFilled.findViewById((R.id.commentlist));
            commentlist.updateData(moodToday.Comments.Comments);
        } else {
            llMood.setVisibility(VISIBLE);
            rlMoodFilled.setVisibility(GONE);
        }

    }

    public void refreshExerciseData() {
        System.out.println("refreshExerciseUI: " + workOutItems);

        if (workOutItems != null && workOutItems.size() > 0) {
            llExercise.setVisibility(LinearLayout.GONE);
            llExerciseWithData.setVisibility(LinearLayout.VISIBLE);

            sort(workOutItems);

            ExerciseListAdapter exerciseListAdapter = new ExerciseListAdapter(context, workOutItems);
            exerciseListAdapter.notifyDataSetChanged();
            workoutListView.invalidateViews();
            workoutListView.refreshDrawableState();
            workoutListView.setAdapter(exerciseListAdapter);

            setListViewHeightBasedOnChildren(workoutListView);


            workoutListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position,
                                        long id) {

                    System.out.println("View Exercise here!");
                    //System.out.println("ExerciseID: " + workoutListView.getAdapter().getItem(position));

                ExerciseContract workoutObj = (ExerciseContract) workoutListView.getAdapter().getItem(position);
                if(workoutObj.ExerciseId > 0) {
                    ApplicationData.getInstance().currentWorkOut = workoutObj;
                    System.out.println("ExerciseID: " + workoutObj.ExerciseId);
//                    Intent mainIntent = new Intent(context, ExerciseViewActivity.class);
//                    mainIntent.putExtra("EXERCISE_ACTIVITY_ID", workoutObj.ExerciseId);
//                    context.startActivity(mainIntent);
                }

                }
            });
            //endif*/


        } else {
            llExercise.setVisibility(LinearLayout.VISIBLE);
            llExerciseWithData.setVisibility(LinearLayout.GONE);
        }//endif


    }


    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(), MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

//    public static boolean isCheckedExercise(List<Workout> workoutItemsList) {
//        for (Workout workoutObj : workoutItemsList) {
//            if (workoutObj.isChecked) {
//                return true;
//            }
//        }
//
//        return false;
//    }

    public boolean updateData(List<MealContract> items) {
        // TODO Auto-generated method stub
        this.items = items;
        refreshUI();
        return false;
    }

    public boolean updateMoodData(List<MoodContract> items) {
        // TODO Auto-generated method stub
        this.moodItems = items;
        refreshMoodData();
        return false;
    }

    public boolean updateExerciseData(List<ExerciseContract> items) {
        // TODO Auto-generated method stub
        this.workOutItems = items;
        refreshExerciseData();
        return false;
    }


    /**
     * Private Methods
     **/
    private void updateMealItem(LinearLayout layout, int mealType) {
        // set clickable on meal info;
        ((ImageView) layout.findViewById(R.id.mealinfo)).setOnClickListener(listener);
        ((ImageView) layout.findViewById(R.id.mealinfo)).setTag(mealType);
        ((TextView) layout.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, mealType));
        ((TextView) layout.findViewById(R.id.mealtime)).setVisibility(View.INVISIBLE);

        ImageView photoMain = ((ImageView) layout.findViewById(R.id.mealphoto));
        photoMain.setOnClickListener(listener);
        photoMain.setTag(mealType);
        photoMain.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        photoMain.setImageResource(R.drawable.meal_addameal);

        ((LinearLayout) layout.findViewById(R.id.multiPhoto_layout)).setVisibility(View.GONE);
        ((TextView) layout.findViewById(R.id.mealdesc)).setVisibility(View.GONE);
        ((LinearLayout) layout.findViewById(R.id.icon_container)).setVisibility(View.GONE);
        ((LinearLayout) layout.findViewById(R.id.meal_uploadfailed)).setVisibility(View.GONE);
    }

    private void updateMealItem(MealContract meal, LinearLayout layout) {
        System.out.println("updateMealItem " + meal.Description);
        layout.setTag(meal);

        // set clickable on meal info;
        ((ImageView) layout.findViewById(R.id.mealinfo)).setOnClickListener(listener);
        ((ImageView) layout.findViewById(R.id.mealinfo)).setTag(meal.MealType);

        ((TextView) layout.findViewById(R.id.mealtitle)).setText(AppUtil.getMealTitle(context, meal.MealType));
        ((TextView) layout.findViewById(R.id.mealtime)).setVisibility(View.VISIBLE);

        ////update meal_creation_date = date of the meal(date tab)
        ((TextView) layout.findViewById(R.id.mealtime)).setText(AppUtil.getTimeOnly12(meal.MealCreationDate));

        ImageView photoMain = ((ImageView) layout.findViewById(R.id.mealphoto));
        photoMain.setOnClickListener(listener);
        photoMain.setTag(meal);
        photoMain.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ((LinearLayout) layout.findViewById(R.id.icon_container)).setVisibility(View.VISIBLE);

        // approved & commented
        if (meal.IsApproved) {
            ((LinearLayout) layout.findViewById(R.id.icon_container_approved)).setVisibility(View.VISIBLE);
            ((TextView) layout.findViewById(R.id.approved_text)).setVisibility(View.VISIBLE);
            ((ImageView) layout.findViewById(R.id.approved_icon)).setVisibility(View.VISIBLE);
            // add spacer too
            ((View) layout.findViewById(R.id.spacer)).setVisibility(View.VISIBLE);

        } else {
            ((LinearLayout) layout.findViewById(R.id.icon_container_approved)).setVisibility(View.GONE);
            ((TextView) layout.findViewById(R.id.approved_text)).setVisibility(View.GONE);
            ((ImageView) layout.findViewById(R.id.approved_icon)).setVisibility(View.GONE);
            // remove spacer too
            ((View) layout.findViewById(R.id.spacer)).setVisibility(View.GONE);
        }

        if (meal.IsCommented) {
            ((LinearLayout) layout.findViewById(R.id.icon_container_approved)).setVisibility(View.VISIBLE);
            ((TextView) layout.findViewById(R.id.comment_text)).setVisibility(View.VISIBLE);
            ((ImageView) layout.findViewById(R.id.comment_icon)).setVisibility(View.VISIBLE);
        } else {
            ((TextView) layout.findViewById(R.id.comment_text)).setVisibility(View.GONE);
            ((ImageView) layout.findViewById(R.id.comment_icon)).setVisibility(View.GONE);
            ((View) layout.findViewById(R.id.spacer)).setVisibility(View.GONE);

        }

//        // text Description
        if (meal.Description != null && meal.Description.length() > 0) {
            ((TextView) layout.findViewById(R.id.mealdesc)).setVisibility(View.VISIBLE);
            ((TextView) layout.findViewById(R.id.mealdesc)).setText(meal.Description);
        } else {
            ((TextView) layout.findViewById(R.id.mealdesc)).setVisibility(View.GONE);
        }

        if (meal.Album != null && meal.Album.PhotoCount > 0) {
            // photoMain.setTag(1);

            if (meal.Album.PhotoCount > 0)
                updatePhotoMain(meal.Album.Photos.get(0), photoMain, meal.MealType);

            if (meal.Album.PhotoCount > 1) {
                ((LinearLayout) layout.findViewById(R.id.multiPhoto_layout)).setVisibility(View.VISIBLE);

                if (meal.Album.PhotoCount >= 2) {
                    ImageView photoThumb1 = ((ImageView) layout.findViewById(R.id.mealphoto1));
                    photoThumb1.setOnClickListener(listener);
                    photoThumb1.setTag(meal);
                    updatePhotoThumb(meal.Album.Photos.get(0), photoThumb1, meal.MealType);

                    ImageView photoThumb2 = ((ImageView) layout.findViewById(R.id.mealphoto2));
                    photoThumb2.setOnClickListener(listener);
                    photoThumb2.setTag(meal);
                    updatePhotoThumb(meal.Album.Photos.get(1), photoThumb2, meal.MealType);

                }
                if (meal.Album.Photos.size() >= 3) {
                    ImageView photoThumb3 = ((ImageView) layout.findViewById(R.id.mealphoto3));
                    photoThumb3.setOnClickListener(listener);
                    photoThumb3.setTag(meal);
                    updatePhotoThumb(meal.Album.Photos.get(2), photoThumb3, meal.MealType);

                }
                if (meal.Album.Photos.size() >= 4) {
                    ImageView photoThumb4 = ((ImageView) layout.findViewById(R.id.mealphoto4));
                    photoThumb4.setOnClickListener(listener);
                    photoThumb4.setTag(meal);
                    updatePhotoThumb(meal.Album.Photos.get(3), photoThumb4, meal.MealType);

                }
                if (meal.Album.Photos.size() >= 5) {
                    ImageView photoThumb5 = ((ImageView) layout.findViewById(R.id.mealphoto5));
                    photoThumb5.setOnClickListener(listener);
                    photoThumb5.setTag(meal);
                    updatePhotoThumb(meal.Album.Photos.get(4), photoThumb5, meal.MealType);
                }
            }

        } else {
            updatePhotoMain(null, photoMain, meal.MealType);
            ((LinearLayout) layout.findViewById(R.id.multiPhoto_layout)).setVisibility(View.GONE);
        }

        ((LinearLayout) layout.findViewById(R.id.icon_container)).setVisibility(View.VISIBLE);

    }

    private void updateWaterItem(int progress, LinearLayout layout) {

        ((TextView) llWater.findViewById(R.id.mealtitle)).setText(getResources().getString(R.string.MEALTYPE_WATER));
        //set amount of water
        updateWaterThumb(progress);
    }

    private void updateWaterThumb(int waterProgress) {
        if (waterProgress == 0) {
            glass_iv.setImageDrawable(getResources().getDrawable(R.drawable.water_glass_gray));
            numberOfGlasses_tv.setText(getResources().getString(R.string.WATER_JOURNAL_EMPTY));
            numberOfGlasses_tv.setTextColor(getResources().getColor(R.color.text_darkgray));
            numberOfGlasses_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    addWater(v);
                }
            });

            rlWater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addWater(v);
                }
            });


        } else {
            glass_iv.setImageDrawable(getResources().getDrawable(R.drawable.water_glass_blue));
            String glassesString = getResources().getString(R.string.WATER_JOURNAL_NOT_EMPTY);
            glassesString = glassesString.replace("%@", Integer.toString(waterProgress));
            numberOfGlasses_tv.setText(glassesString);
            numberOfGlasses_tv.setTextColor(getResources().getColor(R.color.water_tint));
            numberOfGlasses_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewWater(v, ApplicationData.getInstance().currentWater);
                }
            });

            rlWater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewWater(v, ApplicationData.getInstance().currentWater);
                }
            });

        }
    }

    //EXERCISE
    private void addExercise(View v) {
        Intent exerciseIntent = new Intent(v.getContext(), ExerciseActivity.class);
        exerciseIntent.putExtra("EXERCISE_STATUS", CommonConstants.COMMAND_ADDED);
        v.getContext().startActivity(exerciseIntent);
    }

    /* Sort workout */
    public void sort(final List<ExerciseContract> workoutObj) {
        if (workoutObj != null && workoutObj.size() > 0) {
            Collections.sort(workoutObj, new Comparator<ExerciseContract>() {
                public int compare(ExerciseContract workout1, ExerciseContract workout2) {
                    Integer x = (int)(((ExerciseContract) workout1).CreationDate);
                    Integer y = (int)(((ExerciseContract) workout2).CreationDate);
                    return x.compareTo(y);
                    //return ((ExerciseContract) workout1).CreationDate.compareTo(((ExerciseContract) workout2).CreationDate);
                }
            });
        }
    }


    private void addNewMood(View v) {
        Intent mainIntent = new Intent(v.getContext(), MoodAddActivity.class);
        mainIntent.putExtra("MOOD_STATUS", CommonConstants.COMMAND_ADDED);
        v.getContext().startActivity(mainIntent);
    }

    private void editMood(View v) {
        Intent mainIntent = new Intent(v.getContext(), MoodAddActivity.class);
        mainIntent.putExtra("MOOD_STATUS", CommonConstants.COMMAND_UPDATED);
        v.getContext().startActivity(mainIntent);
    }

    private void addWater(View v) {
        Intent mainIntent = new Intent(v.getContext(), WaterViewActivity.class);
        mainIntent.putExtra("WATER_STATUS", "add");
        v.getContext().startActivity(mainIntent);
    }

    private void viewWater(View v, WaterContract water) {
        ApplicationData.getInstance().currentWater = water;
        Intent mainIntent = new Intent(v.getContext(), WaterViewActivity.class);
        mainIntent.putExtra("WATER_STATUS", "view");
        v.getContext().startActivity(mainIntent);
    }

    private void updateHapiMood(int moodValue) {
        if (moodValue == 1) {
            ((ImageView) rlMoodFilled.findViewById(R.id.moodImage)).setImageDrawable(context.getResources().getDrawable(R.drawable.mood1));
            ((TextView) rlMoodFilled.findViewById(R.id.moodTitle)).setText(context.getString(R.string.CARNETMINCEUR_MOOD_1));
        }
        if (moodValue == 2) {
            ((ImageView) rlMoodFilled.findViewById(R.id.moodImage)).setImageDrawable(context.getResources().getDrawable(R.drawable.mood2));
            ((TextView) rlMoodFilled.findViewById(R.id.moodTitle)).setText(context.getString(R.string.CARNETMINCEUR_MOOD_2));
        }
        if (moodValue == 3) {
            ((ImageView) rlMoodFilled.findViewById(R.id.moodImage)).setImageDrawable(context.getResources().getDrawable(R.drawable.mood3));
            ((TextView) rlMoodFilled.findViewById(R.id.moodTitle)).setText(context.getString(R.string.CARNETMINCEUR_MOOD_3));
        }
        if (moodValue == 4) {
            ((ImageView) rlMoodFilled.findViewById(R.id.moodImage)).setImageDrawable(context.getResources().getDrawable(R.drawable.mood4));
            ((TextView) rlMoodFilled.findViewById(R.id.moodTitle)).setText(context.getString(R.string.CARNETMINCEUR_MOOD_4));
        }
        if (moodValue == 5) {
            ((ImageView) rlMoodFilled.findViewById(R.id.moodImage)).setImageDrawable(context.getResources().getDrawable(R.drawable.mood5));
            ((TextView) rlMoodFilled.findViewById(R.id.moodTitle)).setText(context.getString(R.string.CARNETMINCEUR_MOOD_5));
        }
    }

    private void updateHunger(boolean isHungry) {
        if (isHungry) {
            ((ImageView) rlMoodFilled.findViewById(R.id.hungryImage)).setImageDrawable(context.getResources().getDrawable(R.drawable.hunger_yes));
            ((TextView) rlMoodFilled.findViewById(R.id.hungryDescription)).setText(context.getString(R.string.CARNETMINCEUR_HUNGRY));
        } else {
            ((ImageView) rlMoodFilled.findViewById(R.id.hungryImage)).setImageDrawable(context.getResources().getDrawable(R.drawable.hunger_no));
            ((TextView) rlMoodFilled.findViewById(R.id.hungryDescription)).setText(context.getString(R.string.CARNETMINCEUR_NOTHUNGRY));
        }

    }

    public ImageView updatePhotoMain(PhotoContract photo, ImageView item, int type) {
        if (photo != null) {
            // try getting on the ImageManager
            Bitmap bmp = ImageManager.getInstance().findImage(Integer.toString(photo.PhotoId));
            if (bmp == null) {
                new DownloadImageTask(item, photo.PhotoId).execute(photo.UrlLarge);
            } else
                item.setImageBitmap(bmp);
        } else
            item.setImageResource(AppUtil.getPhotoResource(type));

        return item;

    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        final ImageView bmImage;
        String photoid = "";

        public DownloadImageTask(ImageView bmImage, int photoID) {
            this.bmImage = bmImage;
            this.photoid = Integer.toString(photoID);
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);

            ImageManager.getInstance().addImage(photoid, result);

        }
    }

    public ImageView updatePhotoThumb(PhotoContract photo, ImageView item, int type) {
        if (photo != null) {
            // try getting on the ImageManager
            Bitmap bmp = ImageManager.getInstance().findImage(Integer.toString(photo.PhotoId));
            if (bmp == null) {
                new DownloadImageTask(item, photo.PhotoId).execute(photo.UrlLarge);
            } else
                item.setImageBitmap(bmp);
        } else
            item.setImageResource(AppUtil.getPhotoResource(type)); // use

        return item;
    }



}