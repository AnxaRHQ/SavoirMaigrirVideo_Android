package anxa.com.smvideo.activities.free;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;

import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.contracts.RecipeContract;
import anxa.com.smvideo.util.RecipeHelper;
import anxa.com.smvideo.util.UITagHandler;

/**
 * Created by angelaanxa on 5/29/2017.
 */

public class RecipeActivity extends Fragment implements View.OnClickListener
{
    private List<RecipeContract> recipesList;
    View mView;
    private ImageView backButton;
    private Button header_right;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mView = inflater.inflate(R.layout.recipe, null);

        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(getString(R.string.menu_recette));

        backButton = (ImageView) mView.findViewById(R.id.header_menu_back);
        backButton.setVisibility(View.VISIBLE);
        backButton.setOnClickListener(this);

        header_right = (Button) (mView.findViewById(R.id.header_menu_iv));
        header_right.setVisibility(View.GONE);

        String myValue = this.getArguments().getString("message");
        String source = this.getArguments().getString("SOURCE");

        if (source.equalsIgnoreCase("fromRepas")) {
            updateUI(ApplicationData.getInstance().selectedRelatedRecipe);

        } else {
            int recipeId = Integer.parseInt(this.getArguments().getString("RECIPE_ID"));
            if (source.equalsIgnoreCase("fromRecettesAccount")) {
                recipesList = ApplicationData.getInstance().recipeAccountList;
                ((TextView) ((RelativeLayout) mView.findViewById(R.id.headermenu)).findViewById(R.id.header_right_tv)).setVisibility(View.GONE);
            }else{
                recipesList = ApplicationData.getInstance().recipeList;
            }
            if (recipeId > 0) {
                for (RecipeContract r : recipesList) {
                    if (r.Id == recipeId) {
                        updateUI(r);
                    }
                }
            }
        }
        return mView;
    }

    private void updateUI(RecipeContract recipeContract)
    {
        ((TextView) mView.findViewById(R.id.recipeTitle)).setText(recipeContract.Title);
        Bitmap avatar = null;
        avatar = RecipeHelper.GetRecipeImage(recipeContract.Id);
        ImageView img = (ImageView) mView.findViewById(R.id.recipeImage);
        //img.setTag(recipeContract.Id);


        if (recipeContract.ImageUrl.isEmpty())
        {
            ((ImageView) mView.findViewById(R.id.recipeImage)).setImageDrawable(this.getResources().getDrawable(R.drawable.placeholder_recipe));
        }
        else
        {
            if (avatar == null)
            {
                Glide.with(this).load(recipeContract.ImageUrl).diskCacheStrategy(DiskCacheStrategy.RESULT).into(img);
                try {
                    if (!ApplicationData.getInstance().recipePhotoList.containsKey(String.valueOf(recipeContract.Id)) && img.getDrawable() != null) {

                        ApplicationData.getInstance().recipePhotoList.put(String.valueOf(recipeContract.Id), ((GlideBitmapDrawable)img.getDrawable()).getBitmap());
                    }

                } catch (Exception e) {
                    Log.e("Error", e.getMessage());
                    e.printStackTrace();
                }
                mView.findViewById(R.id.recipeImageProgress).setVisibility(View.GONE);
            } else {
                ((ImageView) mView.findViewById(R.id.recipeImage)).setImageBitmap(avatar);
                ((ProgressBar) mView.findViewById(R.id.recipeImageProgress)).setVisibility(View.GONE);
            }
        }

        ((TextView) mView.findViewById(R.id.recipeIngredientsTitle)).setText((recipeContract.IngredientsTitle));
        ((TextView) mView.findViewById(R.id.recipeIngredients)).setText(Html.fromHtml(recipeContract.IngredientsHtml, null, new UITagHandler()));
        ((TextView) mView.findViewById(R.id.recipePreparation)).setText(Html.fromHtml(recipeContract.PreparationHtml, null, new UITagHandler()));
    }

    private Bitmap getAvatar(int recipeId)
    {
        Bitmap avatarBMP = null;
        if (recipeId > 0) {
            avatarBMP = ApplicationData.getInstance().recipePhotoList.get(String.valueOf(recipeId));

            return avatarBMP;
        }
        return avatarBMP;
    }

    @Override
    public void onClick(final View v)
    {
        if (v == backButton)
        {
            getFragmentManager().popBackStack();
        }
    }
}