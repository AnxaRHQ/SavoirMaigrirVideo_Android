package anxa.com.smvideo.ui;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import anxa.com.smvideo.R;
import anxa.com.smvideo.util.UITagHandler;

/**
 * Created by aprilanxa on 04/09/2017.
 */

public class ShoppingListRecipeAdapter extends ArrayAdapter<String> implements View.OnClickListener {

    private final Context context;
    private List<String> items = new ArrayList<String>();

    LayoutInflater layoutInflater;
    String inflater = Context.LAYOUT_INFLATER_SERVICE;
    View.OnClickListener listener;

    public ShoppingListRecipeAdapter(Context context, List<String> items, View.OnClickListener listener) {
        super(context, R.layout.listitem_recipe_shopping_list, items);

        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;
        this.listener = listener;

    }

    public void updateItems(List<String> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public void clear() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        View row = convertView;
        if (row == null) {
            LayoutInflater layoutInflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflator.inflate(R.layout.listitem_recipe_shopping_list, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.recipeTitle = ((TextView) row.findViewById(R.id.recipeTitle));
            viewHolder.recipeItem = ((TextView) row.findViewById(R.id.recipeItem));
            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        if (((String) items.get(position)).contains("TitleName")){
            String shoppingListTitle = (String) items.get(position);
            shoppingListTitle = shoppingListTitle.substring(shoppingListTitle.indexOf("TitleName") + 10);
            row.setTag(R.id.recipe_id, shoppingListTitle);
            viewHolder.recipeTitle.setText(Html.fromHtml(shoppingListTitle, null, new UITagHandler()));
            viewHolder.recipeItem.setVisibility(View.GONE);
        }else{
            String shoppingListContract = "\u25CF " + (String) items.get(position);
            row.setTag(R.id.recipe_id, shoppingListContract);
            viewHolder.recipeItem.setText(Html.fromHtml(shoppingListContract, null, new UITagHandler()));
            viewHolder.recipeTitle.setVisibility(View.GONE);

        }

        return row;
    }

    private void refreshUI() {
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
        }
    }

    private static class ViewHolder {
        TextView recipeTitle;
        TextView recipeItem;
    }
}
