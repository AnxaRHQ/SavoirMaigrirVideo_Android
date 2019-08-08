package anxa.com.smvideo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.http.RecipeDownloadImageAsync;
import anxa.com.smvideo.contracts.RecipeContract;
import anxa.com.smvideo.util.RecipeHelper;

/**
 * Created by angelaanxa on 5/25/2017.
 */

public class RecipesListAdapter extends ArrayAdapter<RecipeContract> implements View.OnClickListener
{
    private final Context context;
    private List<RecipeContract> items = new ArrayList<RecipeContract>();

    LayoutInflater layoutInflater;
    String inflater = Context.LAYOUT_INFLATER_SERVICE;
    View.OnClickListener listener;

    public RecipesListAdapter(Context context, List<RecipeContract> items, View.OnClickListener listener)
    {
        super(context, R.layout.listitem_recipe, items);

        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    public void updateItems(List<RecipeContract> items)
    {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public void clear()
    {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;

        View row = convertView;

        if (row == null) {
            LayoutInflater layoutInflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflator.inflate(R.layout.listitem_recipe, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.recipeImage = (ImageView) row.findViewById(R.id.recipeImage);
            viewHolder.recipeTitle = ((TextView) row.findViewById(R.id.recipeTitle));
            viewHolder.recipeImageProgress = ((ProgressBar) row.findViewById(R.id.recipeImageProgress));
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        RecipeContract recipe = (RecipeContract) items.get(position);
        row.setTag(R.id.recipe_id, recipe.Id);
        row.setOnClickListener(this);

        Bitmap avatar = null;
        avatar = RecipeHelper.GetRecipeImage(recipe.Id);

        //display message
        viewHolder.recipeTitle.setText(recipe.Title);

        viewHolder.recipeImage.setTag(recipe.Id);

        if (avatar == null) {
            new RecipeDownloadImageAsync(viewHolder.recipeImage, viewHolder.recipeImageProgress, recipe.Id).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, recipe.ImageUrl);
        } else {
            viewHolder.recipeImage.setImageBitmap(avatar);
            viewHolder.recipeImageProgress.setVisibility(View.GONE);
        }

        return row;
    }

    private void refreshUI() {
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v)
    {
        if (v != null) {
            if (items != null && items.size() > 0) {
                int pos = (Integer) v.getTag(R.id.recipe_id);
                if (listener != null) {
                    listener.onClick(v);
                }
            }
        }
    }

    private static class ViewHolder
    {
        ImageView recipeImage;
        TextView recipeTitle;
        ProgressBar recipeImageProgress;
    }
}
