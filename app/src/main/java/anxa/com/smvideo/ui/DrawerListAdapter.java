package anxa.com.smvideo.ui;

import android.content.Context;
import android.opengl.Visibility;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import anxa.com.smvideo.R;
import anxa.com.smvideo.models.NavItem;

/**
 * Created by aprilanxa on 31/05/2017.
 */

public class DrawerListAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<NavItem> mNavItems;

    public DrawerListAdapter(Context context, ArrayList<NavItem> navItems) {
        mContext = context;
        mNavItems = navItems;
    }

    @Override
    public int getCount() {
        return mNavItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mNavItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.drawer_item, null);
        }
        else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.title);
        ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        View divider = (View)view.findViewById(R.id.vwDivider);

        titleView.setText( mNavItems.get(position).mTitle );
        iconView.setImageResource(mNavItems.get(position).mIcon);
        if(position==5) {
            divider.setVisibility(View.VISIBLE);
        }else{
            divider.setVisibility(View.GONE);
        }

        return view;
    }
}