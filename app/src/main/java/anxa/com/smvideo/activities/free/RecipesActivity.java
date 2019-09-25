package anxa.com.smvideo.activities.free;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.registration.RegistrationMainObjectiveActivity;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.RecipeContract;
import anxa.com.smvideo.contracts.RecipeResponseContract;
import anxa.com.smvideo.ui.CustomListView;
import anxa.com.smvideo.ui.RecipesListAdapter;

/**
 * Created by angelaanxa on 5/24/2017.
 */

public class RecipesActivity extends Fragment implements View.OnClickListener
{
    private CustomListView recipesListView;
    private RecipesListAdapter adapter;
    private List<RecipeContract> recipesList;

    private Context context;

    private ImageView backButton;
    private TextView header_right;

    protected ApiCaller caller;

    View mView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.context = getActivity();
        caller = new ApiCaller();
        mView = inflater.inflate(R.layout.recipes, null);

        //header change
        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(getString(R.string.menu_recettes));

        backButton = (ImageView) mView.findViewById(R.id.header_menu_back);
        backButton.setVisibility(View.VISIBLE);
        backButton.setImageDrawable(null);
        backButton.setBackground(context.getResources().getDrawable(R.drawable.ic_menu_white_24dp));

        header_right = (Button) (mView.findViewById(R.id.header_menu_iv));
        header_right.setBackgroundResource(0);
        header_right.setText(R.string.login_registration_button);
        header_right.setTextColor(getResources().getColor(R.color.text_orange));
        header_right.setOnClickListener(this);

        //ui
        recipesListView = (CustomListView) mView.findViewById(R.id.recipesListView);
        recipesList = new ArrayList<RecipeContract>();

        if (adapter == null) {
            adapter = new RecipesListAdapter(getActivity(), recipesList, this);
        }

        PopulateList();

        return mView;
    }

    public void PopulateList()
    {
        //ui
        if (ApplicationData.getInstance().recipeList != null && ApplicationData.getInstance().recipeList.size() > 0) {
            AddOnClickListener();
            recipesList = ApplicationData.getInstance().recipeList;
            List<RecipeContract> currentViewRecipeList = new ArrayList<>();
            for (RecipeContract r : recipesList) {
                if (r.RecipeType == RecipeContract.RecipeTypeEnum.Entree.getNumVal()) {
                    currentViewRecipeList.add(r);
                }
            }
            recipesListView.setAdapter(adapter);
            adapter.updateItems(currentViewRecipeList);
        } else {
            //api call
            AddOnClickListener();
            caller.GetFreeRecipes(new AsyncResponse() {

                @Override
                public void processFinish(Object output) {
                    if (output != null) {

                        RecipeResponseContract c = (RecipeResponseContract) output;
                        //INITIALIZE ALL ONCLICK AND API RELATED PROCESS HERE TO AVOID CRASHES
                        if (c != null && c.Data != null && c.Data.Recipes != null) {

                            recipesList = (List<RecipeContract>) c.Data.Recipes;
                            ApplicationData.getInstance().recipeList = recipesList;

                            updateRecipesList();
                        }
                    }
                }
            });
        }
    }

    private void updateRecipesList()
    {
        List<RecipeContract> currentViewRecipeList = new ArrayList<RecipeContract>();
        for (RecipeContract r : recipesList) {
            if (r.RecipeType == RecipeContract.RecipeTypeEnum.Entree.getNumVal()) {
                currentViewRecipeList.add(r);
            }
        }
        recipesListView.setAdapter(adapter);
        adapter.updateItems(currentViewRecipeList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v)
    {
        if (v == header_right)
        {
            goToRegistrationPage();
        }
        else
        {
            List<RecipeContract> currentViewRecipeList = new ArrayList<>();

            RecipeContract.RecipeTypeEnum recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Entree;

            if (v.getId() == R.id.button_entree || v.getId() == R.id.button_salad || v.getId() == R.id.button_plat || v.getId() == R.id.button_dessert || v.getId() == R.id.button_soup
                || v.getId() == R.id.button_appetizer || v.getId() == R.id.button_drink || v.getId() == R.id.button_sauce || v.getId() == R.id.button_encas || v.getId() == R.id.button_simplissime || v.getId() == R.id.button_thermomix)
            {
                recipesListView.setAdapter(null);
                recipesListView.setAdapter(adapter);

                if (Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN)
                {
                    ((Button) v.findViewById(v.getId())).setBackgroundDrawable(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
                } else {
                    ((Button) v.findViewById(v.getId())).setBackground(getResources().getDrawable(R.drawable.button_orange_roundedcorners));
                }

                if (v.getId() == R.id.button_entree)
                {
                    UpdateCategoryButtons(RecipeContract.RecipeTypeEnum.Entree);
                    recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Entree;
                }
                if (v.getId() == R.id.button_salad)
                {
                    UpdateCategoryButtons(RecipeContract.RecipeTypeEnum.Salad);
                    recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Salad;
                }
                if (v.getId() == R.id.button_plat)
                {
                    UpdateCategoryButtons(RecipeContract.RecipeTypeEnum.Plat);
                    recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Plat;
                }
                if (v.getId() == R.id.button_dessert)
                {
                    UpdateCategoryButtons(RecipeContract.RecipeTypeEnum.Dessert);
                    recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Dessert;
                }
                if (v.getId() == R.id.button_soup)
                {
                    UpdateCategoryButtons(RecipeContract.RecipeTypeEnum.Soup);
                    recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Soup;
                }
                if (v.getId() == R.id.button_appetizer)
                {
                    UpdateCategoryButtons(RecipeContract.RecipeTypeEnum.Appetizer);
                    recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Appetizer;
                }
                if (v.getId() == R.id.button_drink)
                {
                    UpdateCategoryButtons(RecipeContract.RecipeTypeEnum.Drink);
                    recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Drink;
                }
                if (v.getId() == R.id.button_sauce)
                {
                    UpdateCategoryButtons(RecipeContract.RecipeTypeEnum.Sauce);
                    recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Sauce;
                }
                if (v.getId() == R.id.button_encas)
                {
                    UpdateCategoryButtons(RecipeContract.RecipeTypeEnum.Snack);
                    recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Snack;
                }
                if (v.getId() == R.id.button_simplissime)
                {
                    UpdateCategoryButtons(RecipeContract.RecipeTypeEnum.Other);
                    recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Other;
                }
                if (v.getId() == R.id.button_thermomix)
                {
                    UpdateCategoryButtons(RecipeContract.RecipeTypeEnum.Other);
                    recipeCategoryToSearch = RecipeContract.RecipeTypeEnum.Other;
                }

                for (RecipeContract r : recipesList)
                {
                    if (r.RecipeType == recipeCategoryToSearch.getNumVal())
                    {
                        currentViewRecipeList.add(r);
                    }
                }

                adapter.updateItems(currentViewRecipeList);
                recipesListView.setAdapter(adapter);
            }
            else
            {
                int recipeId = (Integer) v.getTag(R.id.recipe_id);

                Fragment fragment = new RecipeActivity();
                FragmentManager fragmentManager = getFragmentManager();
                Bundle bundle = new Bundle();
                bundle.putString("RECIPE_ID", String.valueOf(recipeId));
                bundle.putString("SOURCE", "fromRecipes");
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction().add(R.id.mainContent, fragment, "RECIPE_FRAGMENT").addToBackStack(null)
                        .commit();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // Check which request we're responding to
        if (requestCode == 1) { }
    }

    private void AddOnClickListener()
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

    private void UpdateCategoryButtons(RecipeContract.RecipeTypeEnum enumVal)
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
            ((Button) mView.findViewById(R.id.button_entree)).setBackgroundColor(getResources().getColor(R.color.recipeCategoryBackground));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Salad)
        {
            ((Button) mView.findViewById(R.id.button_salad)).setBackgroundColor(getResources().getColor(R.color.recipeCategoryBackground));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Soup)
        {
            ((Button) mView.findViewById(R.id.button_soup)).setBackgroundColor(getResources().getColor(R.color.recipeCategoryBackground));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Dessert)
        {
            ((Button) mView.findViewById(R.id.button_dessert)).setBackgroundColor(getResources().getColor(R.color.recipeCategoryBackground));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Plat)
        {
            ((Button) mView.findViewById(R.id.button_plat)).setBackgroundColor(getResources().getColor(R.color.recipeCategoryBackground));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Appetizer)
        {
            ((Button) mView.findViewById(R.id.button_appetizer)).setBackgroundColor(getResources().getColor(R.color.recipeCategoryBackground));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Drink)
        {
            ((Button) mView.findViewById(R.id.button_drink)).setBackgroundColor(getResources().getColor(R.color.recipeCategoryBackground));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Sauce)
        {
            ((Button) mView.findViewById(R.id.button_sauce)).setBackgroundColor(getResources().getColor(R.color.recipeCategoryBackground));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Snack)
        {
            ((Button) mView.findViewById(R.id.button_encas)).setBackgroundColor(getResources().getColor(R.color.recipeCategoryBackground));
        }
        if (enumVal == RecipeContract.RecipeTypeEnum.Other)
        {
            ((Button) mView.findViewById(R.id.button_simplissime)).setBackgroundColor(getResources().getColor(R.color.recipeCategoryBackground));
        }
    }

    private Gson gson;{
        gson = new Gson();
    }

    private void goToRegistrationPage()
    {
        Intent mainIntent = new Intent(context, RegistrationMainObjectiveActivity.class);
        startActivity(mainIntent);
    }
}

