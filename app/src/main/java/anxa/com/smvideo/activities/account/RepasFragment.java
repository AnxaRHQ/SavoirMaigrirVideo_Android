package anxa.com.smvideo.activities.account;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.RecipeContract;
import anxa.com.smvideo.contracts.RecipeResponseContract;
import anxa.com.smvideo.contracts.RepasContract;
import anxa.com.smvideo.contracts.RepasResponseContract;
import anxa.com.smvideo.contracts.ShoppingListContract;
import anxa.com.smvideo.contracts.ShoppingListResponseContract;
import anxa.com.smvideo.contracts.VideoContract;
import anxa.com.smvideo.ui.CustomListView;
import anxa.com.smvideo.ui.RepasListAdapter;
import anxa.com.smvideo.ui.RepasRelatedListAdapter;
import anxa.com.smvideo.ui.ShoppingListAdapter;
import anxa.com.smvideo.ui.ShoppingListRecipeAdapter;
import anxa.com.smvideo.util.AppUtil;

import static android.view.View.GONE;

/**
 * Created by aprilanxa on 22/06/2017.
 */

public class RepasFragment extends BaseFragment implements View.OnClickListener {
    private Context context;
    protected ApiCaller caller;

    private TextView repasProgram_tv;
    private TextView shoppingList_recipe_tv;
    private EditText repasDay_et;
    private ImageButton nextDay_btn;
    private ImageButton previousDay_btn;
    private Button mealPlan_btn;
    private Button shoppingList_btn;
    private Button recettes_btn;
    private ProgressBar progressBar;

    private LinearLayout repasHeader_ll;
    private LinearLayout repasSearch_ll;

    private ScrollView repasScrollView;
    private ScrollView shoppingListScrollView;
    private LinearLayout repasMain;
    private CustomListView bfastListView;
    private CustomListView lunchListView;
    private CustomListView dinnerListView;
    private CustomListView relatedListView;

    private CustomListView shoppingListView_1;
    private CustomListView shoppingListView_2;
    private CustomListView shoppingListView_3;
    private CustomListView shoppingListView_4;
    private CustomListView shoppingListView_5;
    private CustomListView shoppingListView_6;
    private CustomListView shoppingListView_7;
    private CustomListView shoppingListView_8;
    private CustomListView shoppingListView_9;
    private CustomListView shoppingListView_10;
    private CustomListView shoppingListView_11;

    private CustomListView shoppingListView_recipe;

    private ArrayList<RepasContract> bfastList = new ArrayList<>();
    private ArrayList<RepasContract> lunchList = new ArrayList<>();
    private ArrayList<RepasContract> dinnerList = new ArrayList<>();
    private ArrayList<RepasContract> repasRelatedContent = new ArrayList<>();

    private ArrayList<ShoppingListContract> shoppingListContractsAll = new ArrayList<>();
    private ArrayList<ShoppingListContract> shoppingListRecipeContractsAll = new ArrayList<>();
    private List<String> categoriesList;
    private List<String> recipeList;
    private HashMap<String, ArrayList<ShoppingListContract>> categoryShoppingList = new HashMap<>();
    private HashMap<String, ArrayList<String>> recipeShoppingList = new HashMap<>();

    private RepasListAdapter repasListAdapter_bfast;
    private RepasListAdapter repasListAdapter_lunch;
    private RepasListAdapter repasListAdapter_dinner;

    private ShoppingListAdapter shoppingListAdapter_1;
    private ShoppingListAdapter shoppingListAdapter_2;
    private ShoppingListAdapter shoppingListAdapter_3;
    private ShoppingListAdapter shoppingListAdapter_4;
    private ShoppingListAdapter shoppingListAdapter_5;
    private ShoppingListAdapter shoppingListAdapter_6;
    private ShoppingListAdapter shoppingListAdapter_7;
    private ShoppingListAdapter shoppingListAdapter_8;
    private ShoppingListAdapter shoppingListAdapter_9;
    private ShoppingListAdapter shoppingListAdapter_10;
    private ShoppingListAdapter shoppingListAdapter_11;

    private ShoppingListRecipeAdapter shoppingListAdapter_recipe;

    private RepasRelatedListAdapter repasListAdapter_related;

    private ImageView backButton;
    private Button shareButton;
    private Button header_right;

    private boolean isUserWeek0 = false;

    private int dayOffset = 0;
    private int weekOffset = 0;
    private int weekNumber = 0;

    private int dayOffset_list = 0;
    private int weekNumber_list = 0;

    private int dayNumber = 0;

    private int totalDaysArchive = 0;
    private int totalWeeksArchive = 0;

    private static final String TAG = RepasFragment.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getActivity();
        mView = inflater.inflate(R.layout.repas_account, null);

        caller = new ApiCaller();

        //header change
        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(getString(R.string.menu_account_repas));

      /*  header_right = (Button) (mView.findViewById(R.id.header_menu_iv));
        header_right.setBackgroundResource(R.drawable.ic_print);
        header_right.setOnClickListener(this);*/

        backButton = (ImageView) ((RelativeLayout) mView.findViewById(R.id.headermenu)).findViewById(R.id.header_menu_back);
        backButton.setOnClickListener(this);

        progressBar = (ProgressBar) mView.findViewById(R.id.repasProgressbar);

        shareButton = (Button) ((RelativeLayout) mView.findViewById(R.id.headermenu)).findViewById(R.id.shareButton);
        shareButton.setOnClickListener(this);
        shareButton.setVisibility(View.VISIBLE);
        repasMain = ((LinearLayout) mView.findViewById(R.id.repasMain));
        mealPlan_btn = (Button) mView.findViewById(R.id.meal_plan_button);
        shoppingList_btn = (Button) mView.findViewById(R.id.shopping_list_button);
        recettes_btn = (Button) mView.findViewById(R.id.recettes_button);
        mealPlan_btn.setOnClickListener(this);
        shoppingList_btn.setOnClickListener(this);
        recettes_btn.setOnClickListener(this);

        mealPlan_btn.setSelected(true);
        progressBar.setVisibility(GONE);
       /* repasProgram_tv = (TextView) (mView.findViewById(R.id.repasHeader_tv));
        shoppingList_recipe_tv = (TextView) (mView.findViewById(R.id.shoppingList_recipe_header));
        repasDay_et = (EditText) (mView.findViewById(R.id.repasDay_et));

        nextDay_btn = (ImageButton) (mView.findViewById(R.id.nextDayButton));
        previousDay_btn = (ImageButton) (mView.findViewById(R.id.previousDayButton));

        nextDay_btn.setOnClickListener(this);
        previousDay_btn.setOnClickListener(this);

        repasHeader_ll = (LinearLayout) mView.findViewById(R.id.repasHeader_ll);
        repasSearch_ll = (LinearLayout) mView.findViewById(R.id.repasSearch_ll);

        bfastListView = (CustomListView) mView.findViewById(R.id.repas_bfast_list);
        lunchListView = (CustomListView) mView.findViewById(R.id.repas_lunch_list);
        dinnerListView = (CustomListView) mView.findViewById(R.id.repas_dinner_list);
        relatedListView = (CustomListView) mView.findViewById(R.id.repas_related_list);

        shoppingListView_1 = (CustomListView) mView.findViewById(R.id.repas_shopping_list_1);
        shoppingListView_2 = (CustomListView) mView.findViewById(R.id.repas_shopping_list_2);
        shoppingListView_3 = (CustomListView) mView.findViewById(R.id.repas_shopping_list_3);
        shoppingListView_4 = (CustomListView) mView.findViewById(R.id.repas_shopping_list_4);
        shoppingListView_5 = (CustomListView) mView.findViewById(R.id.repas_shopping_list_5);
        shoppingListView_6 = (CustomListView) mView.findViewById(R.id.repas_shopping_list_6);
        shoppingListView_7 = (CustomListView) mView.findViewById(R.id.repas_shopping_list_7);
        shoppingListView_8 = (CustomListView) mView.findViewById(R.id.repas_shopping_list_8);
        shoppingListView_9 = (CustomListView) mView.findViewById(R.id.repas_shopping_list_9);
        shoppingListView_10 = (CustomListView) mView.findViewById(R.id.repas_shopping_list_10);
        shoppingListView_11 = (CustomListView) mView.findViewById(R.id.repas_shopping_list_11);

        shoppingListView_recipe = (CustomListView) mView.findViewById(R.id.repas_shopping_list_recipe);

        repasScrollView = (ScrollView) mView.findViewById(R.id.repasScrollView);
        shoppingListScrollView = (ScrollView) mView.findViewById(R.id.shoppingListScrollView);

        repasScrollView.setVisibility(View.VISIBLE);
        shoppingListScrollView.setVisibility(View.GONE);
        hideCategories();

        mealPlan_btn = (Button) mView.findViewById(R.id.meal_plan_button);
        shoppingList_btn = (Button) mView.findViewById(R.id.shopping_list_button);
        recettes_btn = (Button) mView.findViewById(R.id.recettes_button);
        mealPlan_btn.setOnClickListener(this);
        shoppingList_btn.setOnClickListener(this);
        recettes_btn.setOnClickListener(this);

        mealPlan_btn.setSelected(true);

        shoppingList_recipe_tv.setVisibility(View.GONE);

        String programHeader = getString(R.string.repas_header_meal_plan);

        programHeader = programHeader.replace("#program", AppUtil.getMealTypeString(ApplicationData.getInstance().dietProfilesDataContract.MealPlanType, context));
        programHeader = programHeader.replace("#calorie", AppUtil.getCalorieType(ApplicationData.getInstance().dietProfilesDataContract.CalorieType, context));

        repasProgram_tv.setText(programHeader);

        if (CheckFreeUser(false)) {
            weekNumber = 1;
        } else {
            weekNumber = ApplicationData.getInstance().userDataContract.WeekNumber;
        }

        dayNumber = ApplicationData.getInstance().userDataContract.DayNumber;

        System.out.println("Repas weeknumber: " + weekNumber);

        if (weekNumber == 0) {
            weekNumber = 1;
            dayNumber = 1;
            totalWeeksArchive = 2;
            totalDaysArchive = 14;
            isUserWeek0 = true;

            repasDay_et.setText(AppUtil.getRepasDateHeaderWeekDay(weekNumber, dayNumber));
        } else {
            repasDay_et.setText(AppUtil.getRepasDateHeaderWeekDay(weekNumber, dayNumber));

            //applied latest rule - display two weeks after the current coaching week
            totalDaysArchive = AppUtil.getDaysDiffToCurrent(Long.parseLong(ApplicationData.getInstance().dietProfilesDataContract.CoachingStartDate) + AppUtil.getAddDaysToCurrent());

            //applied latest rule - display two weeks after the current coaching week
            totalWeeksArchive = weekNumber + 2;
        }

        System.out.println("Repas weeknumber 2: " + weekNumber);

        getMealOfTheDay();*/

        super.onCreateView(inflater, container, savedInstanceState);

      showRepasFragment(true);
        return mView;
    }

    private void showRepasFragment(boolean isRepas)
    {
        Bundle bundle = new Bundle();
        bundle.putString("header_title", getString(R.string.nav_account_webinars));
        if(isRepas) {
            bundle.putString("webkit_url", WebkitURL.repasWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));
        }else{
            bundle.putString("webkit_url", WebkitURL.shoppingWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));

        }
        bundle.putBoolean("hide_header", true);

        Fragment fragment = new WebkitFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragment.setArguments(bundle);

        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_REPAS") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_REPAS")).commit();
        } else { }

        try {
            fragmentManager.beginTransaction().replace(R.id.repasMain, fragment, "CURRENT_FRAGMENT_IN_REPAS").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Repas */

    private void getMealOfTheDay() {
        if (CheckFreeUser(false)) {
            weekNumber = 1;
        }

        System.out.println("Repas getMealOfTheDay: " + weekNumber + " dayNumber: " + dayNumber);

        caller.GetAccountRepas(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                //INITIALIZE ALL ONCLICK AND API RELATED PROCESS HERE TO AVOID CRASHES
                if (output != null) {
                    RepasResponseContract c = (RepasResponseContract) output;
                    if (c != null && c.Data != null) {
                        ApplicationData.getInstance().repasContractArrayList = c.Data.Repas;
                        updateRepasList();
                        progressBar.setVisibility(GONE);
                    }
                }
            }
        }, weekNumber, dayNumber);
    }

    private void updateRepasList() {
        if (repasListAdapter_bfast == null) {
            repasListAdapter_bfast = new RepasListAdapter(getActivity(), bfastList, this);
        }
        if (repasListAdapter_lunch == null) {
            repasListAdapter_lunch = new RepasListAdapter(getActivity(), lunchList, this);
        }
        if (repasListAdapter_dinner == null) {
            repasListAdapter_dinner = new RepasListAdapter(getActivity(), dinnerList, this);
        }
        if (repasListAdapter_related == null) {
            repasListAdapter_related = new RepasRelatedListAdapter(getActivity(), repasRelatedContent, this);
        }

        if (ApplicationData.getInstance().repasContractArrayList != null) {
            repasRelatedContent = new ArrayList<>();
            bfastList = new ArrayList<>();
            lunchList = new ArrayList<>();
            dinnerList = new ArrayList<>();

            for (RepasContract repasContract : ApplicationData.getInstance().repasContractArrayList) {
                switch (repasContract.mealType) {
                    case 1:
                        bfastList.add(repasContract);
                        break;
                    case 2:
                        lunchList.add(repasContract);
                        break;
                    case 4:
                        dinnerList.add(repasContract);
                        break;
                    default:
                        break;
                }
                if (repasContract.linkCtid > 0) {
//                    if (!repasRelatedContent.contains(repasContract)) {
//                        repasRelatedContent.add(repasContract);
//                    }
                    boolean uniqueContent = true;
                    if (repasRelatedContent.size() > 0) {
                        for (RepasContract currentRepas : repasRelatedContent) {
                            if (currentRepas.itemName.equalsIgnoreCase(repasContract.itemName)) {
                                uniqueContent = false;
                            }
                        }
                        if (uniqueContent)
                            repasRelatedContent.add(repasContract);
                    } else {
                        repasRelatedContent.add(repasContract);

                    }
                }
            }

            System.out.println("Repas repasRelatedContent: " + repasRelatedContent);

            bfastListView.setAdapter(repasListAdapter_bfast);
            lunchListView.setAdapter(repasListAdapter_lunch);
            dinnerListView.setAdapter(repasListAdapter_dinner);
            relatedListView.setAdapter(repasListAdapter_related);

            repasListAdapter_bfast.updateItems(bfastList);
            repasListAdapter_lunch.updateItems(lunchList);
            repasListAdapter_dinner.updateItems(dinnerList);
            repasListAdapter_related.updateItems(repasRelatedContent);

            Collections.sort(repasRelatedContent, new Comparator<RepasContract>() {
                public int compare(RepasContract v1, RepasContract v2) {
                    return v1.itemName.compareTo(v2.itemName);
                }
            });
        }
    }

    /* Courses */

    private void getShoppingList() {
        System.out.println("Repas weeknumber getShoppingList: " + weekNumber);

        //dayOffset = 0, today
        categoriesList = new ArrayList<String>();
        recipeList = new ArrayList<String>();
        shoppingListContractsAll = new ArrayList<>();
        shoppingListRecipeContractsAll = new ArrayList<>();

        caller.GetAccountShoppingList(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                //INITIALIZE ALL ONCLICK AND API RELATED PROCESS HERE TO AVOID CRASHES
                if (output != null) {
                    ShoppingListResponseContract c = (ShoppingListResponseContract) output;
                    if (c != null && c.Data != null) {
                        ApplicationData.getInstance().shoppingListContractArrayList = c.Data.ShoppingList;

                        //get category of video with index 1
                        for (ShoppingListContract v : c.Data.ShoppingList) {
                            shoppingListContractsAll.add(v);

                            //get all categories
                            if (!categoriesList.contains(AppUtil.getShoppingCategoryString(v.Category, context))) {
                                categoriesList.add(AppUtil.getShoppingCategoryString(v.Category, context));
                            }
                        }

                        ApplicationData.getInstance().shoppingCategoryList = new ArrayList<String>();
                        ApplicationData.getInstance().shoppingCategoryList = categoriesList;

                        getListPerCategory();

                        //shopping list recipe
                        for (ShoppingListContract v : c.Data.ShoppingListRecipe) {
                            shoppingListRecipeContractsAll.add(v);

                            //get all categories
                            if (!recipeList.contains(v.Title)) {
                                recipeList.add(v.Title);
                            }
                        }
                        getListPerRecipe();

                        progressBar.setVisibility(GONE);
                    }
                }
            }
        }, weekNumber, dayNumber);
    }

    /*   private void openScreenshot(File imageFile) {
           Intent intent = new Intent();
           intent.setAction(Intent.ACTION_VIEW);
           Uri uri = Uri.fromFile(imageFile);
           intent.setDataAndType(uri, "application/pdf");
           startActivity(intent);
       }*/
    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    private void generatePdf(ViewGroup v) {
        if (isStoragePermissionGranted()) {
            try {
                /*v.measure(View.MeasureSpec.makeMeasureSpec(
                        View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                v.layout(0, 0, v.getMeasuredWidth(),
                        v.getMeasuredHeight());
                v.setDrawingCacheEnabled(true);
                v.buildDrawingCache();*/


                PdfDocument document = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                    document = new PdfDocument();

                    int pageNumber = 1;
                    PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(v.getChildAt(0).getWidth(),
                            v.getChildAt(0).getHeight(), pageNumber).create();
                    PdfDocument.Page page = document.startPage(pageInfo);
                    //wb.draw(page.getCanvas());
                    v.draw(page.getCanvas());
                    document.finishPage(page);


                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                    //bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    String docName = "repas.pdf";
                    if (shoppingList_btn.isSelected()) {
                        docName = "shoppinglist.pdf";
                    }
                    if (recettes_btn.isSelected()) {
                        docName = "recipe.pdf";
                    }
                    File f = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), docName);
                    document.writeTo(new FileOutputStream(f));
                    document.close();
                    //openScreenshot(f);

                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                /*    Uri uri = Uri.fromFile(f);
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());*/
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("application/pdf");

                    sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + f));
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void removeRepasFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_REPAS") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_REPAS")).commit();
        }
    }


    private void getSpecificRecipe(int recipeId) {
        caller.GetAccountRecipeCtid(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                RecipeResponseContract c = (RecipeResponseContract) output;
                if (c != null && c.Data != null && c.Data.Recipes != null) {
                    proceedToRecipePage(c.Data.Recipes.get(0));
                }
            }
        }, recipeId, dayNumber);
    }

    private void proceedToRecipePage(RecipeContract recipeContract) {
        ApplicationData.getInstance().selectedRelatedRecipe = recipeContract;

        Intent mainIntent = new Intent(this.getActivity(), RecipeAccountActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(mainIntent);


    }




    private void getListPerCategory() {
        for (String catPerShopping : categoriesList) {

            ArrayList<ShoppingListContract> shoppingListPerCat = new ArrayList<>();
            for (ShoppingListContract v : shoppingListContractsAll) {
                if (v.Category == AppUtil.getCategoryTypeIndex(catPerShopping, context)) {
                    shoppingListPerCat.add(v);
                }
            }
            categoryShoppingList.put(catPerShopping, shoppingListPerCat);

            if (AppUtil.getCategoryTypeIndex(catPerShopping, context) == 1) {
                if (shoppingListAdapter_1 == null) {
                    shoppingListAdapter_1 = new ShoppingListAdapter(getActivity(), getItemsPerCategoryInSummary(shoppingListPerCat), this);
                } else {
                    shoppingListAdapter_1.updateItems(getItemsPerCategoryInSummary(shoppingListPerCat));
                }

                ((TextView) mView.findViewById(R.id.shopping_category_1)).setVisibility(View.VISIBLE);

            } else if (AppUtil.getCategoryTypeIndex(catPerShopping, context) == 2) {
                if (shoppingListAdapter_2 == null) {
                    shoppingListAdapter_2 = new ShoppingListAdapter(getActivity(), getItemsPerCategoryInSummary(shoppingListPerCat), this);
                } else {
                    shoppingListAdapter_2.updateItems(getItemsPerCategoryInSummary(shoppingListPerCat));
                }
                ((TextView) mView.findViewById(R.id.shopping_category_2)).setVisibility(View.VISIBLE);

            } else if (AppUtil.getCategoryTypeIndex(catPerShopping, context) == 3) {
                if (shoppingListAdapter_3 == null) {
                    shoppingListAdapter_3 = new ShoppingListAdapter(getActivity(), getItemsPerCategoryInSummary(shoppingListPerCat), this);
                } else {
                    shoppingListAdapter_3.updateItems(getItemsPerCategoryInSummary(shoppingListPerCat));
                }

                ((TextView) mView.findViewById(R.id.shopping_category_3)).setVisibility(View.VISIBLE);

            } else if (AppUtil.getCategoryTypeIndex(catPerShopping, context) == 4) {
                if (shoppingListAdapter_4 == null) {
                    shoppingListAdapter_4 = new ShoppingListAdapter(getActivity(), getItemsPerCategoryInSummary(shoppingListPerCat), this);
                } else {

                    shoppingListAdapter_4.updateItems(getItemsPerCategoryInSummary(shoppingListPerCat));
                }

                ((TextView) mView.findViewById(R.id.shopping_category_4)).setVisibility(View.VISIBLE);

            } else if (AppUtil.getCategoryTypeIndex(catPerShopping, context) == 5) {
                if (shoppingListAdapter_5 == null) {
                    shoppingListAdapter_5 = new ShoppingListAdapter(getActivity(), getItemsPerCategoryInSummary(shoppingListPerCat), this);
                } else {
                    shoppingListAdapter_5.updateItems(getItemsPerCategoryInSummary(shoppingListPerCat));
                }

                ((TextView) mView.findViewById(R.id.shopping_category_5)).setVisibility(View.VISIBLE);

            } else if (AppUtil.getCategoryTypeIndex(catPerShopping, context) == 6) {
                if (shoppingListAdapter_6 == null) {
                    shoppingListAdapter_6 = new ShoppingListAdapter(getActivity(), getItemsPerCategoryInSummary(shoppingListPerCat), this);
                } else {
                    shoppingListAdapter_6.updateItems(getItemsPerCategoryInSummary(shoppingListPerCat));
                }

                ((TextView) mView.findViewById(R.id.shopping_category_6)).setVisibility(View.VISIBLE);

            } else if (AppUtil.getCategoryTypeIndex(catPerShopping, context) == 7) {
                if (shoppingListAdapter_7 == null) {
                    shoppingListAdapter_7 = new ShoppingListAdapter(getActivity(), getItemsPerCategoryInSummary(shoppingListPerCat), this);
                } else {
                    shoppingListAdapter_7.updateItems(getItemsPerCategoryInSummary(shoppingListPerCat));
                }

                ((TextView) mView.findViewById(R.id.shopping_category_7)).setVisibility(View.VISIBLE);

            } else if (AppUtil.getCategoryTypeIndex(catPerShopping, context) == 8) {
                if (shoppingListAdapter_8 == null) {
                    shoppingListAdapter_8 = new ShoppingListAdapter(getActivity(), getItemsPerCategoryInSummary(shoppingListPerCat), this);
                } else {
                    shoppingListAdapter_8.updateItems(getItemsPerCategoryInSummary(shoppingListPerCat));
                }

                ((TextView) mView.findViewById(R.id.shopping_category_8)).setVisibility(View.VISIBLE);

            } else if (AppUtil.getCategoryTypeIndex(catPerShopping, context) == 9) {
                if (shoppingListAdapter_9 == null) {
                    shoppingListAdapter_9 = new ShoppingListAdapter(getActivity(), getItemsPerCategoryInSummary(shoppingListPerCat), this);
                } else {
                    shoppingListAdapter_9.updateItems(getItemsPerCategoryInSummary(shoppingListPerCat));
                }

                ((TextView) mView.findViewById(R.id.shopping_category_9)).setVisibility(View.VISIBLE);

            } else if (AppUtil.getCategoryTypeIndex(catPerShopping, context) == 10) {
                if (shoppingListAdapter_10 == null) {
                    shoppingListAdapter_10 = new ShoppingListAdapter(getActivity(), getItemsPerCategoryInSummary(shoppingListPerCat), this);
                } else {
                    shoppingListAdapter_10.updateItems(getItemsPerCategoryInSummary(shoppingListPerCat));
                }

                ((TextView) mView.findViewById(R.id.shopping_category_10)).setVisibility(View.VISIBLE);

            } else if (AppUtil.getCategoryTypeIndex(catPerShopping, context) == 11) {
                if (shoppingListAdapter_11 == null) {
                    shoppingListAdapter_11 = new ShoppingListAdapter(getActivity(), getItemsPerCategoryInSummary(shoppingListPerCat), this);
                } else {
                    shoppingListAdapter_11.updateItems(getItemsPerCategoryInSummary(shoppingListPerCat));
                }
                ((TextView) mView.findViewById(R.id.shopping_category_11)).setVisibility(View.VISIBLE);
            }
        }

        shoppingListView_1.setAdapter(shoppingListAdapter_1);
        shoppingListView_2.setAdapter(shoppingListAdapter_2);
        shoppingListView_3.setAdapter(shoppingListAdapter_3);
        shoppingListView_4.setAdapter(shoppingListAdapter_4);
        shoppingListView_5.setAdapter(shoppingListAdapter_5);
        shoppingListView_6.setAdapter(shoppingListAdapter_6);
        shoppingListView_7.setAdapter(shoppingListAdapter_7);
        shoppingListView_8.setAdapter(shoppingListAdapter_8);
        shoppingListView_9.setAdapter(shoppingListAdapter_9);
        shoppingListView_10.setAdapter(shoppingListAdapter_10);
        shoppingListView_11.setAdapter(shoppingListAdapter_11);
    }

    private void getListPerRecipe() {
        for (String recipe : recipeList) {
            ArrayList<String> shoppingListPerRecipe = new ArrayList<>();
            for (ShoppingListContract v : shoppingListRecipeContractsAll) {
                if (v.Title.equalsIgnoreCase(recipe)) {
                    String recipeItem = "";
                    if (v.Quantity == 0) {
                        recipeItem = v.ItemName;
                    } else {
                        DecimalFormat format = new DecimalFormat("#,###.#");
                        recipeItem = format.format(v.Quantity) + " " + v.ItemName;
                    }
                    shoppingListPerRecipe.add(recipeItem);
                }
            }
            recipeShoppingList.put(recipe, shoppingListPerRecipe);
        }

        ArrayList<String> shoppingListAllRecipe = new ArrayList<>();
        for (String recipe : recipeList) {
            shoppingListAllRecipe.add("TitleName " + recipe);
            for (String recipeItem : recipeShoppingList.get(recipe)) {
                shoppingListAllRecipe.add(recipeItem);
            }
        }

        if (shoppingListAdapter_recipe == null) {
            shoppingListAdapter_recipe = new ShoppingListRecipeAdapter(getActivity(), shoppingListAllRecipe, this);
        } else {
            shoppingListAdapter_recipe.updateItems(shoppingListAllRecipe);
        }

        shoppingListView_recipe.setAdapter(shoppingListAdapter_recipe);
        shoppingList_recipe_tv.setVisibility(View.VISIBLE);
    }

    private ArrayList<String> getItemsPerCategoryInSummary(ArrayList<ShoppingListContract> shoppingListContractArrayList) {
        ArrayList<String> listItems = new ArrayList<>();
        ArrayList<String> displayListItems = new ArrayList<>();
        HashMap<String, String> quantityItemNameMap = new HashMap<>();

        for (ShoppingListContract v : shoppingListContractArrayList) {
            if (!listItems.contains(v.ItemName)) {
                listItems.add(v.ItemName);
            }
        }

        for (String itemName : listItems) {
            float sumQuantity = 0;
            for (ShoppingListContract v : shoppingListContractArrayList) {
                if (itemName.equalsIgnoreCase(v.ItemName)) {
                    sumQuantity = sumQuantity + v.Quantity;
                }
            }
            quantityItemNameMap.put(itemName, Float.toString(sumQuantity));
        }

        for (String key : quantityItemNameMap.keySet()) {
            String displayString;

            if (quantityItemNameMap.get(key).equalsIgnoreCase("0.0")) {
                displayString = key;
            } else {
                DecimalFormat format = new DecimalFormat("#,###.#");
                displayString = format.format(Float.parseFloat(quantityItemNameMap.get(key))) + " " + key;
            }
            displayListItems.add(displayString);
        }

        Collections.sort(displayListItems, new Comparator<String>() {

            @Override
            public int compare(String o1, String o2) {
                int temp = o1.length() - o2.length();
                o1 = o1.replaceAll("\\d", "");
                o2 = o2.replaceAll("\\d", "");

                int compareTo = o1.compareTo(o2);
                if (compareTo == 0) {
                    return temp;
                } else {
                    return compareTo;
                }
            }
        });

        return displayListItems;
    }

    private void hideCategories() {
        ((TextView) mView.findViewById(R.id.shopping_category_1)).setVisibility(GONE);
        ((TextView) mView.findViewById(R.id.shopping_category_2)).setVisibility(GONE);
        ((TextView) mView.findViewById(R.id.shopping_category_3)).setVisibility(GONE);
        ((TextView) mView.findViewById(R.id.shopping_category_4)).setVisibility(GONE);
        ((TextView) mView.findViewById(R.id.shopping_category_5)).setVisibility(GONE);
        ((TextView) mView.findViewById(R.id.shopping_category_6)).setVisibility(GONE);
        ((TextView) mView.findViewById(R.id.shopping_category_7)).setVisibility(GONE);
        ((TextView) mView.findViewById(R.id.shopping_category_8)).setVisibility(GONE);
        ((TextView) mView.findViewById(R.id.shopping_category_9)).setVisibility(GONE);
        ((TextView) mView.findViewById(R.id.shopping_category_10)).setVisibility(GONE);
        ((TextView) mView.findViewById(R.id.shopping_category_11)).setVisibility(GONE);
        shoppingList_recipe_tv.setVisibility(GONE);

        if (shoppingListAdapter_1 != null) {
            shoppingListAdapter_1.clear();
        }
        if (shoppingListAdapter_2 != null) {
            shoppingListAdapter_2.clear();
        }
        if (shoppingListAdapter_3 != null) {
            shoppingListAdapter_3.clear();
        }
        if (shoppingListAdapter_4 != null) {
            shoppingListAdapter_4.clear();
        }
        if (shoppingListAdapter_5 != null) {
            shoppingListAdapter_5.clear();
        }
        if (shoppingListAdapter_6 != null) {
            shoppingListAdapter_6.clear();
        }
        if (shoppingListAdapter_7 != null) {
            shoppingListAdapter_7.clear();
        }
        if (shoppingListAdapter_8 != null) {
            shoppingListAdapter_8.clear();
        }
        if (shoppingListAdapter_9 != null) {
            shoppingListAdapter_9.clear();
        }
        if (shoppingListAdapter_10 != null) {
            shoppingListAdapter_10.clear();
        }
        if (shoppingListAdapter_11 != null) {
            shoppingListAdapter_11.clear();
        }
        if (shoppingListAdapter_recipe != null) {
            shoppingListAdapter_recipe.clear();
        }
    }

    /* Recettes */


    private void loadRecetteFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_REPAS") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_REPAS")).commit();
        } else {
        }

        try {
            Fragment fragment = new RecipesAccountFragment();
            fragmentManager.beginTransaction().replace(R.id.recettesContent, fragment, "CURRENT_FRAGMENT_IN_REPAS").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

      /*  repasHeader_ll.setVisibility(GONE);
        repasSearch_ll.setVisibility(GONE);*/
    }

    @Override
    public void onClick(final View v) {
        System.out.println("onclick dayoffset: " + dayOffset + " totalDaysArchive: " + totalDaysArchive);
        System.out.println("onclick weekNumber: " + weekNumber + " dayNumber: " + dayNumber);
        System.out.println("onclick weekNumber: " + weekNumber + " totalWeeksArchive: " + totalWeeksArchive);

        if (v == nextDay_btn)
        {
            progressBar.setVisibility(View.VISIBLE);

            /* Repas */

            if (mealPlan_btn.isSelected()) {
                showRepasFragment(true);
                /*if (isUserWeek0) {
                    isPreviousButtonDisabled(false);

                    if (weekNumber == 1 && dayNumber != 7) {
                        isNextButtonDisabled((dayNumber == 6) ? true : false);

                        dayNumber++;
                        getDateString();
                        getMealOfTheDay();
                    }
                } else {
                    isPreviousButtonDisabled(false);

                    if (CheckFreeUser(false)) {
                        if (weekNumber == 1 && dayNumber != 7) {
                            isNextButtonDisabled((dayNumber == 6) ? true : false);

                            dayNumber++;
                            getDateString();
                            getMealOfTheDay();
                        }
                    } else {
                        *//* Limit up to +2 weeks only *//*

                        if (weekNumber == (ApplicationData.getInstance().userDataContract.WeekNumber + 2) && dayNumber == 7) {
                        } else {
                            isPreviousButtonDisabled(false);
                            isNextButtonDisabled((weekNumber == (ApplicationData.getInstance().userDataContract.WeekNumber + 2) && dayNumber == 6) ? true : false);

                            if (dayNumber == 7) {
                                weekNumber++;
                                dayNumber = 1;
                            } else {
                                dayNumber++;
                            }

                            getDateString();
                            getMealOfTheDay();
                        }
                    }
                }*/
            }

            /* Courses */

            else if (shoppingList_btn.isSelected()) {
                showRepasFragment(false);
                /*if (isUserWeek0) {
                    isPreviousButtonDisabled(true);
                    isNextButtonDisabled(true);
                } else {
                    if (!CheckFreeUser(false)) {
                        isPreviousButtonDisabled(false);

                        *//* Limit up to +2 weeks only *//*

                        if (weekNumber != (ApplicationData.getInstance().userDataContract.WeekNumber + 2)) {
                            weekNumber++;

                            hideCategories();
                            getShoppingListDateString();
                            getShoppingList();

                            isNextButtonDisabled((weekNumber == (ApplicationData.getInstance().userDataContract.WeekNumber + 2)) ? true : false);
                        }
                    } else {
                        isPreviousButtonDisabled(true);
                        isNextButtonDisabled(true);
                    }
                }*/
            }
        }
        else if (v == previousDay_btn)
        {
           /* progressBar.setVisibility(View.VISIBLE);

            *//* Repas *//*

            if (mealPlan_btn.isSelected()) {
                if (isUserWeek0) {
                    isNextButtonDisabled(false);

                    if (weekNumber == 1 && dayNumber != 1) {
                        isPreviousButtonDisabled((dayNumber == 2) ? true : false);

                        dayNumber--;
                        getDateString();
                        getMealOfTheDay();
                    }
                } else {
                    isNextButtonDisabled(false);

                    if (CheckFreeUser(false)) {
                        if (weekNumber == 1 && dayNumber != 1) {
                            isPreviousButtonDisabled((dayNumber == 2) ? true : false);

                            dayNumber--;
                            getDateString();
                            getMealOfTheDay();
                        }
                    } else {
                        *//* Limit up to +2 weeks only *//*

                        if (weekNumber == 1 && dayNumber == 1) {
                        } else {
                            isPreviousButtonDisabled((weekNumber == 1 && dayNumber == 2) ? true : false);

                            if (dayNumber == 1) {
                                weekNumber--;
                                dayNumber = 7;
                            } else {
                                dayNumber--;
                            }

                            getDateString();
                            getMealOfTheDay();
                        }
                    }
                }
            }

            *//* Courses *//*

            else if (shoppingList_btn.isSelected()) {
                if (isUserWeek0) {
                    isPreviousButtonDisabled(true);
                    isNextButtonDisabled(true);
                } else {
                    if (!CheckFreeUser(false)) {
                        isNextButtonDisabled(false);

                        if (weekNumber != 1) {
                            weekNumber--;

                            hideCategories();
                            getShoppingListDateString();
                            getShoppingList();

                            isPreviousButtonDisabled((weekNumber == 1) ? true : false);
                        }
                    } else {
                        isPreviousButtonDisabled(true);
                        isNextButtonDisabled(true);
                    }
                }
            }*/
        }
        else if (v == mealPlan_btn)
        {  showRepasFragment(true);
            mealPlan_btn.setSelected(true);
            shoppingList_btn.setSelected(false);
            recettes_btn.setSelected(false);
            /*repasHeader_ll.setVisibility(View.VISIBLE);
            repasSearch_ll.setVisibility(View.VISIBLE);

            if (isUserWeek0) {
                weekNumber = 1;
                dayNumber = 1;
            } else {
                if (CheckFreeUser(false)) {
                    weekNumber = 1;
                } else {
                    weekNumber = ApplicationData.getInstance().userDataContract.WeekNumber;
                }

                dayNumber = ApplicationData.getInstance().userDataContract.DayNumber;
            }

            isPreviousButtonDisabled(false);
            isNextButtonDisabled(false);

            repasDay_et.setText(AppUtil.getRepasDateHeaderWeekDay(weekNumber, dayNumber));

            mealPlan_btn.setSelected(true);
            shoppingList_btn.setSelected(false);
            recettes_btn.setSelected(false);

            repasScrollView.setVisibility(View.VISIBLE);
            shoppingListScrollView.setVisibility(View.GONE);
            removeRepasFragment();*/
        }
        else if (v == shoppingList_btn)
        { showRepasFragment(false);
            mealPlan_btn.setSelected(false);
            shoppingList_btn.setSelected(true);
            recettes_btn.setSelected(false);
           /* progressBar.setVisibility(View.VISIBLE);

            System.out.println("Repas weeknumber shoppingList_btn: " + weekNumber);

            repasHeader_ll.setVisibility(View.VISIBLE);
            repasSearch_ll.setVisibility(View.VISIBLE);

            if (isUserWeek0) {
                weekNumber = 1;
                dayNumber = 1;
                repasDay_et.setText("Semaine 1");

                isPreviousButtonDisabled(true);
                isNextButtonDisabled(true);
            } else {
                if (CheckFreeUser(false)) {
                    weekNumber = 1;

                    isPreviousButtonDisabled(true);
                    isNextButtonDisabled(true);
                } else {
                    weekNumber = ApplicationData.getInstance().userDataContract.WeekNumber;

                    isPreviousButtonDisabled(false);
                    isNextButtonDisabled(false);
                }

                dayNumber = ApplicationData.getInstance().userDataContract.DayNumber;
                repasDay_et.setText(AppUtil.getShoppingListDateHeader(weekNumber));
            }

            mealPlan_btn.setSelected(false);
            shoppingList_btn.setSelected(true);
            recettes_btn.setSelected(false);

            repasScrollView.setVisibility(View.GONE);
            shoppingListScrollView.setVisibility(View.VISIBLE);

            getShoppingList();
            removeRepasFragment();*/
        }
        else if (v == recettes_btn)
        {
            progressBar.setVisibility(View.GONE);

            mealPlan_btn.setSelected(false);
            shoppingList_btn.setSelected(false);
            recettes_btn.setSelected(true);
          /*  repasScrollView.setVisibility(GONE);
            shoppingListScrollView.setVisibility(GONE);*/
            removeRepasFragment();
            loadRecetteFragment();
        } else if (v == backButton) {
            super.removeFragment();
        }
        else if (v == shareButton) {
            if (shoppingList_btn.isSelected()) {
                generatePdf(repasMain);
            }else if(mealPlan_btn.isSelected()){
                generatePdf(repasMain);
            }else{
                generatePdf((RelativeLayout)mView.findViewById(R.id.recettesContent));
            }

        }else if (v == header_right) {
        } else {
        }
    }

    /* Date Headers */

    private void getDateString() {
        repasDay_et.setText(AppUtil.getRepasDateHeaderWeekDay(weekNumber, dayNumber));
    }

    private void getShoppingListDateString() {
        repasDay_et.setText(AppUtil.getShoppingListDateHeader(weekNumber));
    }

    private void isPreviousButtonDisabled(boolean isDisabled) {
        previousDay_btn.setImageResource(isDisabled ? R.drawable.ic_chevron_left_gray : R.drawable.ic_chevron_left_white_24dp);
    }

    private void isNextButtonDisabled(boolean isDisabled) {
        nextDay_btn.setImageResource(isDisabled ? R.drawable.ic_chevron_right_gray : R.drawable.ic_chevron_right_white_24dp);
    }
}
