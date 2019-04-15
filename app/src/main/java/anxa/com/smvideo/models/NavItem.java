package anxa.com.smvideo.models;

/**
 * Created by aprilanxa on 31/05/2017.
 */

public class NavItem {
    public String mTitle;
    public int mIcon;

    public NavItem(String title){  mTitle = title;}

    public NavItem(String title, int icon) {
        mTitle = title;
        mIcon = icon;
    }
}

