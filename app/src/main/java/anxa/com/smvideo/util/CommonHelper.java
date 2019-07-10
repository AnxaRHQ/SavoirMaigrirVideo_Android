package anxa.com.smvideo.util;

import android.content.Context;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.models.NavItem;

public  class CommonHelper {
    public static ArrayList<NavItem> BuildNav(Context context)
    {
        ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
            mNavItems.add(new NavItem(context.getString(R.string.menu_home), R.drawable.icon_home));
            mNavItems.add(new NavItem(context.getString(R.string.menu_decouvrir), R.drawable.decouvrez_ico));
            mNavItems.add(new NavItem(context.getString(R.string.menu_bilan), R.drawable.bilanminceur_ico));
            mNavItems.add(new NavItem(context.getString(R.string.menu_temoignages), R.drawable.temoignage_ico));
            mNavItems.add(new NavItem(context.getString(R.string.menu_recettes), R.drawable.recettes_ico));
            mNavItems.add(new NavItem(context.getString(R.string.menu_mon_compte), R.drawable.compte_ico));
        } else {


            mNavItems.add(new NavItem(context.getString(R.string.menu_home)));
            mNavItems.add(new NavItem(context.getString(R.string.menu_account_repas)));
            mNavItems.add(new NavItem(context.getString(R.string.menu_account_webinars)));
            mNavItems.add(new NavItem(context.getString(R.string.menu_account_dieticienne)));
            mNavItems.add(new NavItem(context.getString(R.string.menu_account_session)));
            mNavItems.add(new NavItem(context.getString(R.string.menu_account_videos)));
            mNavItems.add(new NavItem(context.getString(R.string.menu_account_poids)));
            mNavItems.add(new NavItem(context.getString(R.string.menu_account_communaute)));
            mNavItems.add(new NavItem(context.getString(R.string.menu_account_fiches)));
            mNavItems.add(new NavItem(context.getString(R.string.menu_account_ambassadrice)));
            mNavItems.add(new NavItem(context.getString(R.string.menu_account_compte)));
        }
        return mNavItems;
    }

}
