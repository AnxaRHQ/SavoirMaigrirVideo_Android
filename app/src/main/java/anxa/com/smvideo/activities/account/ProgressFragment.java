package anxa.com.smvideo.activities.account;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Button;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.common.WebkitURL;

/**
 *
 */
public class ProgressFragment extends BaseFragment implements View.OnClickListener
{
    private Context context;
    private ImageView backButton;
    private Button weightButton, stepsButton, bicepsButton, poitrineButton, tailleButton, hanchesButton, cuisseButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.context = getActivity();

        mView = inflater.inflate(R.layout.progress_account, null);

        /* Init */

        ((TextView)(mView.findViewById(R.id.header_title_tv))).setText(getString(R.string.menu_account_poids));
        backButton = (ImageView) ((RelativeLayout) mView.findViewById(R.id.headermenu)).findViewById(R.id.header_menu_back);
        weightButton = (mView.findViewById(R.id.weight_button));
        stepsButton = (mView.findViewById(R.id.steps_button));
        bicepsButton = (mView.findViewById(R.id.biceps_button));
        poitrineButton = (mView.findViewById(R.id.poitrine_button));
        tailleButton = (mView.findViewById(R.id.taille_button));
        hanchesButton = (mView.findViewById(R.id.hanches_button));
        cuisseButton = (mView.findViewById(R.id.cuisse_button));

        /* Buttons */

        backButton.setOnClickListener(this);
        weightButton.setOnClickListener(this);
        stepsButton.setOnClickListener(this);
        bicepsButton.setOnClickListener(this);
        poitrineButton.setOnClickListener(this);
        tailleButton.setOnClickListener(this);
        hanchesButton.setOnClickListener(this);
        cuisseButton.setOnClickListener(this);

        /* Load Default View */

        weightButton.setSelected(true);
        loadWeightFragment();
        super.onCreateView(inflater, container, savedInstanceState);
        return mView;
    }

    @Override
    public void onClick(View v)
    {
        if (v == backButton)
        {
            super.removeFragment();
        }
        else if (v == weightButton)
        {
            deselectAllButton();
            weightButton.setSelected(true);
            loadWeightFragment();
        }
        else if (v == stepsButton)
        {
            deselectAllButton();
            stepsButton.setSelected(true);
            loadStepsFragment();
        }
        else if (v == bicepsButton)
        {
            deselectAllButton();
            bicepsButton.setSelected(true);
            loadBicepsFragment();
        }
        else if (v == poitrineButton)
        {
            deselectAllButton();
            poitrineButton.setSelected(true);
            loadChestFragment();
        }
        else if (v == tailleButton)
        {
            deselectAllButton();
            tailleButton.setSelected(true);
            loadWaistFragment();
        }
        else if (v == hanchesButton)
        {
            deselectAllButton();
            hanchesButton.setSelected(true);
            loadHipsFragment();
        }
        else if (v == cuisseButton)
        {
            deselectAllButton();
            cuisseButton.setSelected(true);
            loadThighsFragment();
        }
    }

    private void deselectAllButton()
    {
        weightButton.setSelected(false);
        stepsButton.setSelected(false);
        bicepsButton.setSelected(false);
        poitrineButton.setSelected(false);
        tailleButton.setSelected(false);
        hanchesButton.setSelected(false);
        cuisseButton.setSelected(false);
    }

    private void loadWeightFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS")).commit();
        } else {
        }

        try {
            Fragment fragment = new WeightGraphFragment();

            fragmentManager.beginTransaction().replace(R.id.graphContent, fragment, "CURRENT_FRAGMENT_IN_PROGRESS").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStepsFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS")).commit();
        } else {
        }

        try {
            Fragment fragment = new StepsGraphFragment();
            fragmentManager.beginTransaction().replace(R.id.graphContent, fragment, "CURRENT_FRAGMENT_IN_PROGRESS").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBicepsFragment()
    {
        Bundle bundle = new Bundle();

        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS")).commit();
        } else {
        }

        try {
            Fragment fragment = new WebkitFragment();
            bundle.putString("isHideHeader", "true");
            bundle.putString("header_title", getString(R.string.menu_account_poids));
            bundle.putString("webkit_url", WebkitURL.bicepsWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.graphContent, fragment, "CURRENT_FRAGMENT_IN_PROGRESS").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadChestFragment()
    {
        Bundle bundle = new Bundle();

        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS")).commit();
        } else {
        }

        try {
            Fragment fragment = new WebkitFragment();
            bundle.putString("isHideHeader", "true");
            bundle.putString("header_title", getString(R.string.menu_account_poids));
            bundle.putString("webkit_url", WebkitURL.chestWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.graphContent, fragment, "CURRENT_FRAGMENT_IN_PROGRESS").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadWaistFragment()
    {
        Bundle bundle = new Bundle();

        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS")).commit();
        } else {
        }

        try {
            Fragment fragment = new WebkitFragment();
            bundle.putString("isHideHeader", "true");
            bundle.putString("header_title", getString(R.string.menu_account_poids));
            bundle.putString("webkit_url", WebkitURL.waistWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.graphContent, fragment, "CURRENT_FRAGMENT_IN_PROGRESS").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHipsFragment()
    {
        Bundle bundle = new Bundle();

        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS")).commit();
        } else {
        }

        try {
            Fragment fragment = new WebkitFragment();
            bundle.putString("isHideHeader", "true");
            bundle.putString("header_title", getString(R.string.menu_account_poids));
            bundle.putString("webkit_url", WebkitURL.hipsWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.graphContent, fragment, "CURRENT_FRAGMENT_IN_PROGRESS").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadThighsFragment()
    {
        Bundle bundle = new Bundle();

        FragmentManager fragmentManager = getFragmentManager();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_PROGRESS")).commit();
        } else {
        }

        try {
            Fragment fragment = new WebkitFragment();
            bundle.putString("isHideHeader", "true");
            bundle.putString("header_title", getString(R.string.menu_account_poids));
            bundle.putString("webkit_url", WebkitURL.thighsWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));
            fragment.setArguments(bundle);
            fragmentManager.beginTransaction().replace(R.id.graphContent, fragment, "CURRENT_FRAGMENT_IN_PROGRESS").commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
