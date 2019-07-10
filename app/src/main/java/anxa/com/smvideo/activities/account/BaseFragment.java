package anxa.com.smvideo.activities.account;

import android.app.Fragment;
import android.app.FragmentManager;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;

public class BaseFragment extends Fragment {
    protected void removeFragment()
    {
        Fragment fragment = new LandingPageAccountActivity();

        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Home;
        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT")).commit();
        } else {
        }

        try {

            fragmentManager.beginTransaction().replace(R.id.mainContent, fragment, "CURRENT_FRAGMENT").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
