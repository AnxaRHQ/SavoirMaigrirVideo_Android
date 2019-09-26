package anxa.com.smvideo.activities.account;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.RecipeContract;
import anxa.com.smvideo.contracts.RecipeResponseContract;
import anxa.com.smvideo.ui.CustomListView;
import anxa.com.smvideo.ui.RecipesListAdapter;

/**
 * Created by aprilanxa on 13/07/2017.
 */

public class RecipesAccountFragment extends BaseFragment implements View.OnClickListener {

    private Context context;
    protected ApiCaller caller;
    private List<RecipeContract> recipesList;
    private RecipesListAdapter adapter;
    private ProgressBar progressBar;

    private RecipeContract.RecipeTypeEnum selectedRecipeType;

    CustomListView recipesListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.context = getActivity();
        mView = inflater.inflate(R.layout.recipes, null);
        caller = new ApiCaller();

        selectedRecipeType = RecipeContract.RecipeTypeEnum.Entree;

        //header change
        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(getString(R.string.menu_account_recettes));
        ((TextView) (mView.findViewById(R.id.header_title_tv))).setVisibility(View.GONE);

        //hide header
        (mView.findViewById(R.id.headermenu)).setVisibility(View.GONE);

        //ui
        recipesListView = (CustomListView) mView.findViewById(R.id.recipesListView);

        progressBar = (ProgressBar) mView.findViewById(R.id.recipeProgressBar);

        recipesList = new ArrayList<RecipeContract>();

        if (adapter == null) {
            adapter = new RecipesListAdapter(getActivity(), recipesList, this);
        }

        addOnClickListener();

        populateList(selectedRecipeType.getNumVal());

        super.onCreateView(inflater, container, savedInstanceState);
        return mView;
    }

    public void populateList(int recipeType)
    {
        if (ApplicationData.getInstance().recipeAccountList != null && ApplicationData.getInstance().recipeAccountList.size() > 0) {
            recipesList = ApplicationData.getInstance().recipeAccountList;
            List<RecipeContract> currentViewRecipeList = new ArrayList<>();
            for (RecipeContract r : recipesList) {
                if (r.RecipeType == selectedRecipeType.getNumVal()) {
                    currentViewRecipeList.add(r);
                }
            }
            recipesListView.setAdapter(adapter);
            adapter.updateItems(currentViewRecipeList);

        } else {
            //api call
            caller.GetAccountRecettes(new AsyncResponse() {

                @Override
                public void processFinish(Object output) {
                    if (output != null) {
                        RecipeResponseContract c = (RecipeResponseContract) output;
                        //INITIALIZE ALL ONCLICK AND API RELATED PROCESS HERE TO AVOID CRASHES

                        if (c != null && c.Data != null && c.Data.Recipes != null) {
                            ApplicationData.getInstance().recipeAccountList.clear();

                            recipesList = (List<RecipeContract>) c.Data.Recipes;

                            Collections.sort(recipesList, new Comparator() {
                                @Override
                                public int compare(Object o1, Object o2) {
                                    RecipeContract p1 = (RecipeContract) o1;
                                    RecipeContract p2 = (RecipeContract) o2;
                                    return Integer.valueOf(p1.Id).compareTo(p2.Id);
                                }
                            });

                            ApplicationData.getInstance().recipeAccountList.addAll(recipesList);

                            updateRecipesList();
                        }
                    }
                }

            }, selectedRecipeType.getNumVal());

        }
    }

    private void getRecipePerCategory(final int selectedRecipeTypeParam)
    {
        progressBar.setVisibility(View.VISIBLE);

        caller.GetAccountRecettes(new AsyncResponse() {

            @Override
            public void processFinish(Object output) {
                if (output != null) {
                    RecipeResponseContract c = (RecipeResponseContract) output;
                    //INITIALIZE ALL ONCLICK AND API RELATED PROCESS HERE TO AVOID CRASHES

                    if (c != null && c.Data != null && c.Data.Recipes != null) {

                        recipesList = (List<RecipeContract>) c.Data.Recipes;

                        Collections.sort(recipesList, new Comparator() {
                            @Override
                            public int compare(Object o1, Object o2) {
                                RecipeContract p1 = (RecipeContract) o1;
                                RecipeContract p2 = (RecipeContract) o2;
                                return Integer.valueOf(p1.Id).compareTo(p2.Id);
                            }
                        });

                        ApplicationData.getInstance().recipeAccountList.addAll(recipesList);

                        updateRecipesListPerCategory(selectedRecipeTypeParam);
                    }

                    progressBar.setVisibility(View.INVISIBLE);
                }

            }

        }, selectedRecipeTypeParam);
    }

    private void updateRecipesList()
    {
        List<RecipeContract> currentViewRecipeList = new ArrayList<RecipeContract>();
        for (RecipeContract r : recipesList)
        {
            if (r.RecipeType == RecipeContract.RecipeTypeEnum.Entree.getNumVal())
            {
                currentViewRecipeList.add(r);
            }
        }
        recipesListView.setAdapter(adapter);
        adapter.updateItems(currentViewRecipeList);

        adapter.notifyDataSetChanged();
    }

    private void updateRecipesListPerCategory(int category)
    {
        List<RecipeContract> currentViewRecipeList = new ArrayList<RecipeContract>();
        for (RecipeContract r : recipesList) {
            if (r.RecipeType == category) {
                currentViewRecipeList.add(r);
            }
        }
        recipesListView.setAdapter(adapter);
        adapter.clear();
        adapter.updateItems(currentViewRecipeList);

        adapter.notifyDataSetChanged();
    }

    private void addOnClickListener()
    {
        ((Button) mView.findViewById(R.id.button_entree)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.button_salad)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.button_plat)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.button_dessert)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.button_soup)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.button_appetizer)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.button_drink)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.button_sauce)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.button_encas)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.button_simplissime)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.button_thermomix)).setOnClickListener(this);
    }

    @Override
    public void onClick(View v)
    {
        List<RecipeContract> currentViewRecipeList = new ArrayList<>();

        RecipeContract.RecipeTypeEnum recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Entree;

        if (v.getId() == R.id.button_entree || v.getId() == R.id.button_salad || v.getId() == R.id.button_plat || v.getId() == R.id.button_dessert || v.getId() == R.id.button_soup
                || v.getId() == R.id.button_appetizer || v.getId() == R.id.button_drink || v.getId() == R.id.button_sauce || v.getId() == R.id.button_encas || v.getId() == R.id.button_simplissime || v.getId() == R.id.button_thermomix)
        {
            recipesListView.setAdapter(null);
            recipesListView.setAdapter(adapter);
            if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                ((Button) v.findViewById(v.getId())).setBackgroundDrawable(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
            } else {
                ((Button) v.findViewById(v.getId())).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
            }
            if (v.getId() == R.id.button_entree) {
                updateCategoryButtons(RecipeContract.RecipeTypeEnum.Entree);
                recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Entree;
            }
            if (v.getId() == R.id.button_salad) {
                updateCategoryButtons(RecipeContract.RecipeTypeEnum.Salad);
                recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Salad;
            }
            if (v.getId() == R.id.button_plat) {
                updateCategoryButtons(RecipeContract.RecipeTypeEnum.Plat);
                recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Plat;
            }
            if (v.getId() == R.id.button_dessert) {
                updateCategoryButtons(RecipeContract.RecipeTypeEnum.Dessert);
                recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Dessert;
            }
            if (v.getId() == R.id.button_soup) {

                updateCategoryButtons(RecipeContract.RecipeTypeEnum.Soup);
                recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Soup;
            }
            if (v.getId() == R.id.button_appetizer)
            {
                updateCategoryButtons(RecipeContract.RecipeTypeEnum.Appetizer);
                recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Appetizer;
            }
            if (v.getId() == R.id.button_drink)
            {
                updateCategoryButtons(RecipeContract.RecipeTypeEnum.Drink);
                recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Drink;
            }
            if (v.getId() == R.id.button_sauce)
            {
                updateCategoryButtons(RecipeContract.RecipeTypeEnum.Sauce);
                recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Sauce;
            }
            if (v.getId() == R.id.button_encas)
            {
                updateCategoryButtons(RecipeContract.RecipeTypeEnum.Snack);
                recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Snack;
            }
            if (v.getId() == R.id.button_simplissime)
            {
                updateCategoryButtons(RecipeContract.RecipeTypeEnum.Simplissime);
                recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Simplissime;
            }
            if (v.getId() == R.id.button_thermomix)
            {
                updateCategoryButtons(RecipeContract.RecipeTypeEnum.Thermomix);
                recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Thermomix;
            }

            for (RecipeContract r : recipesList) {
                if (r.RecipeType == recipeCategoryToSearch.getNumVal()) {
                    currentViewRecipeList.add(r);
                }
            }

            if (currentViewRecipeList.size() > 0) {
                adapter.updateItems(currentViewRecipeList);
            } else {
                adapter.clear();
                getRecipePerCategory(recipeCategoryToSearch.getNumVal());
            }
        }
        else
        {
            int recipeId = (Integer) v.getTag(R.id.recipe_id);

            proceedToRecipePage(recipeId);

//            Fragment fragment = new RecipeActivity();
//            FragmentManager fragmentManager = getFragmentManager();
//            Bundle bundle = new Bundle();
//            bundle.putString("SOURCE", "fromRecettesAccount");
//            bundle.putString("RECIPE_ID", String.valueOf(recipeId));
//
//            fragment.setArguments(bundle);
//            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT")).add(R.id.mainContent, fragment, "RECIPE_FRAGMENT").addToBackStack(null)
//                    .commit();
        }
    }

    private void proceedToRecipePage(int recipeId)
    {
        if (recipeId > 0) {
            for (RecipeContract r : recipesList) {
                if (r.Id == recipeId) {
                    ApplicationData.getInstance().selectedRelatedRecipe = r;
                }
            }
        }
        // Intent mainIntent = new Intent(context, RecipeAccountActivity.class);
        //mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivityForResult(mainIntent, REQUEST_CODE_RECIPEVIEW);

        Fragment fragment = new RecipeAccountActivity();
        FragmentManager fragmentManager = getFragmentManager();

        fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_REPAS")).add(R.id.mainContent, fragment, "RECIPE_FRAGMENT").addToBackStack(null)
                .commit();
    }

    private void updateCategoryButtons(RecipeContract.RecipeTypeEnum enumVal)
    {
        ((Button) mView.findViewById(R.id.button_entree)).setBackgroundColor(Color.TRANSPARENT);
        ((Button) mView.findViewById(R.id.button_salad)).setBackgroundColor(Color.TRANSPARENT);
        ((Button) mView.findViewById(R.id.button_plat)).setBackgroundColor(Color.TRANSPARENT);
        ((Button) mView.findViewById(R.id.button_dessert)).setBackgroundColor(Color.TRANSPARENT);
        ((Button) mView.findViewById(R.id.button_soup)).setBackgroundColor(Color.TRANSPARENT);
        ((Button) mView.findViewById(R.id.button_appetizer)).setBackgroundColor(Color.TRANSPARENT);
        ((Button) mView.findViewById(R.id.button_drink)).setBackgroundColor(Color.TRANSPARENT);
        ((Button) mView.findViewById(R.id.button_sauce)).setBackgroundColor(Color.TRANSPARENT);
        ((Button) mView.findViewById(R.id.button_encas)).setBackgroundColor(Color.TRANSPARENT);
        ((Button) mView.findViewById(R.id.button_simplissime)).setBackgroundColor(Color.TRANSPARENT);
        ((Button) mView.findViewById(R.id.button_thermomix)).setBackgroundColor(Color.TRANSPARENT);

        if (enumVal == RecipeContract.RecipeTypeEnum.Entree)
        {
            ((Button) mView.findViewById(R.id.button_entree)).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Salad)
        {
            ((Button) mView.findViewById(R.id.button_salad)).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Soup)
        {
            ((Button) mView.findViewById(R.id.button_soup)).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Dessert)
        {
            ((Button) mView.findViewById(R.id.button_dessert)).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Plat)
        {
            ((Button) mView.findViewById(R.id.button_plat)).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Appetizer)
        {
            ((Button) mView.findViewById(R.id.button_appetizer)).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Drink)
        {
            ((Button) mView.findViewById(R.id.button_drink)).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Sauce)
        {
            ((Button) mView.findViewById(R.id.button_sauce)).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Snack)
        {
            ((Button) mView.findViewById(R.id.button_encas)).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Simplissime)
        {
            ((Button) mView.findViewById(R.id.button_simplissime)).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Thermomix)
        {
            ((Button) mView.findViewById(R.id.button_thermomix)).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //super.onActivityResult(requestCode, resultCode, data);
        //finish
    }
}
