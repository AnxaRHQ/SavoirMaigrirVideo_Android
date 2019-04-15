package anxa.com.smvideo.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.account.AproposFragment;
import anxa.com.smvideo.activities.account.CarnetAccountFragment;
import anxa.com.smvideo.activities.account.CoachingAccountFragment;
import anxa.com.smvideo.activities.account.ConseilsFragment;
import anxa.com.smvideo.activities.account.ExerciceFragment;
import anxa.com.smvideo.activities.account.FichesFragment;
import anxa.com.smvideo.activities.account.LandingPageAccountActivity;
import anxa.com.smvideo.activities.account.MessagesAccountFragment;
import anxa.com.smvideo.activities.account.MonCompteAccountFragment;
import anxa.com.smvideo.activities.account.RecipesAccountFragment;
import anxa.com.smvideo.activities.account.RepasFragment;
import anxa.com.smvideo.activities.account.VideosFragment;
import anxa.com.smvideo.activities.account.WebinarFragment;
import anxa.com.smvideo.activities.account.WeightGraphFragment;
import anxa.com.smvideo.activities.free.BilanMinceurActivity;
import anxa.com.smvideo.activities.free.DiscoverActivity;
import anxa.com.smvideo.activities.free.LandingPageActivity;
import anxa.com.smvideo.activities.free.MonCompteActivity;
import anxa.com.smvideo.activities.free.RecipesActivity;
import anxa.com.smvideo.activities.free.TemoignagesActivity;
import anxa.com.smvideo.models.NavItem;
import anxa.com.smvideo.ui.DrawerListAdapter;
import anxa.com.smvideo.util.AppUtil;


public class MainActivity extends BaseVideoActivity implements View.OnClickListener {

    private static String TAG = MainActivity.class.getSimpleName();

    ListView mDrawerList;
    RelativeLayout mDrawerPane;
    private LinearLayout apropos_ll;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;

    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(this.getResources().getString(R.string.bilan_broadcast_subscribe));
        this.getApplicationContext().registerReceiver(the_receiver, filter);

        apropos_ll = (LinearLayout)findViewById(R.id.apropos_ll);

        //landing page on the first launch
        if (ApplicationData.getInstance().showLandingPage) {
            if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
                launchActivity(LandingPageActivity.class);
            } else {
                launchActivity(LandingPageAccountActivity.class);
            }
        }

        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
            ((TextView) (findViewById(R.id.slide_nav_header_tv))).setText(getString(R.string.welcome_message));
            mNavItems.add(new NavItem(getString(R.string.menu_home), R.drawable.icon_home));
            mNavItems.add(new NavItem(getString(R.string.menu_decouvrir), R.drawable.decouvrez_ico));
            mNavItems.add(new NavItem(getString(R.string.menu_bilan), R.drawable.bilanminceur_ico));
            mNavItems.add(new NavItem(getString(R.string.menu_temoignages), R.drawable.temoignage_ico));
            mNavItems.add(new NavItem(getString(R.string.menu_recettes), R.drawable.recettes_ico));
            mNavItems.add(new NavItem(getString(R.string.menu_mon_compte), R.drawable.compte_ico));
        } else {

            String welcome_message;
            if (ApplicationData.getInstance().userDataContract.FirstName != null) {
//                welcome_message = getString(R.string.welcome_account_1).replace("%@", ApplicationData.getInstance().userDataContract.FirstName).concat(getString(R.string.welcome_account_2).replace("%d", Integer.toString(ApplicationData.getInstance().currentWeekNumber)));
                welcome_message = getString(R.string.welcome_account_1).replace("%@", ApplicationData.getInstance().userDataContract.FirstName).concat(getString(R.string.welcome_account_2).replace("%f", AppUtil.getCurrentDayName(AppUtil.getCurrentDayNumber()))).concat(getString(R.string.welcome_account_3).replace("%d", Integer.toString(ApplicationData.getInstance().currentWeekNumber)));
                System.out.println("welcome message: " + welcome_message);

            } else {
                welcome_message = getString(R.string.welcome_account_1).replace("%@", ApplicationData.getInstance().userName).concat(getString(R.string.welcome_account_2).replace("%f", AppUtil.getCurrentDayName(AppUtil.getCurrentDayNumber()))).concat(getString(R.string.welcome_account_3).replace("%d", Integer.toString(ApplicationData.getInstance().currentWeekNumber)));
                System.out.println("welcome message: " + welcome_message);

            }
            ((TextView) (findViewById(R.id.slide_nav_header_tv))).setText(welcome_message);
            mNavItems.add(new NavItem(getString(R.string.menu_home)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_repas)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_webinars)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_dieticienne)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_session)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_videos)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_poids)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_communaute)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_fiches)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_ambassadrice)));
        }

        // DrawerLayout
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        // Populate the Navigation Drawer with options
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);
        mDrawerList = (ListView) findViewById(R.id.navList);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);
        mDrawerList.setAdapter(adapter);

        // Drawer Item click listeners
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItemFromDrawer(position);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
            if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Home) {
                goToHomePage();
            }else if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Account_Apropos) {
                goToAproposPage();
            }else {
                selectItemFromDrawer(ApplicationData.getInstance().selectedFragment.getNumVal()+1);
            }
        } else {
            //initial
            if (ApplicationData.getInstance().selectedFragment.getNumVal() < 5) {
                ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Coaching;
            } else if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Account_Apropos) {
                goToAproposPage();
            } else if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Home) {
                goToHomePage();
            } else {
                selectItemFromDrawer(ApplicationData.getInstance().selectedFragment.getNumVal() - 4);
            }
        }
    }

    /*
 * Called when a particular item from the navigation drawer is selected.*/
    private void selectItemFromDrawer(int position) {
        Fragment fragment = new RecipesActivity();

        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
            switch (position) {
                case 0:
                    goToHomePage();
                    break;
                case 1: //decouvir
                    fragment = new DiscoverActivity();
                    break;
                case 2: //bilan
                    fragment = new BilanMinceurActivity();
                    break;
                case 3: //temoignages
                    fragment = new TemoignagesActivity();
                    break;
                case 4: //recetters
                    fragment = new RecipesActivity();
                    break;
                case 5: //mon compte
                    fragment = new MonCompteActivity();
                    break;
                default:
                    fragment = new RecipesActivity();
            }
        } else {
            switch (position) {
                case 0:
                    goToHomePage();
                    break;
                case 1: //repas
                    ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Repas;
                    fragment = new RepasFragment();
                    break;
                case 2: //consultations
                    ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Consultation;
                    fragment = new WebinarFragment();
                    break;
                case 3: //recettes
                    ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Messages;
                    fragment = new MessagesAccountFragment();
                    break;
                case 4: //session
                    ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Coaching;

                    if (!ApplicationData.getInstance().fromArchive)
                        ApplicationData.getInstance().selectedWeekNumber = AppUtil.getCurrentWeekNumber(Long.parseLong(ApplicationData.getInstance().dietProfilesDataContract.CoachingStartDate), new Date());
                    fragment = new CoachingAccountFragment();
                    break;
                case 5: //500 videos
                    ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Messages;
                    fragment = new VideosFragment();
                    break;
                case 6: //courbes
                    ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Weight;
                    fragment = new WeightGraphFragment();
                    break;
                case 7: //communaute

                    break;
                case 8: //fiches
                    ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Fiches;
                    fragment = new FichesFragment();
                    break;
                case 9: //ambassadrice

                    break;
                case 10: //apropos
                    ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Apropos;
                    fragment = new AproposFragment();
                    break;
                case 12: //fiches
                    ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Fiches;
                    fragment = new FichesFragment();
                    break;
                default:
                    fragment = new CoachingAccountFragment();

            }
        }

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


        mDrawerList.setItemChecked(position, true);
        setTitle(mNavItems.get(position).mTitle);

        // Close the drawer
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.header_menu_iv) {
            //burger menu
            ApplicationData.getInstance().fromArchive = false;
            ApplicationData.getInstance().fromArchiveConseils = false;
            if (ApplicationData.getInstance().accountType.equalsIgnoreCase("account")) {
                ApplicationData.getInstance().selectedWeekNumber = AppUtil.getCurrentWeekNumber(Long.parseLong(ApplicationData.getInstance().dietProfilesDataContract.CoachingStartDate), new Date());
            }
            mDrawerLayout.openDrawer(Gravity.LEFT);
        }
    }

    public void launchActivity(Class obj) {
        Intent intent = new Intent(this, obj);
        startActivity(intent);
    }

    public void onBackPressed(View view) {
        getFragmentManager().popBackStack();
    }

    private BroadcastReceiver the_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(context.getResources().getString(R.string.bilan_broadcast_subscribe))) {
                selectItemFromDrawer(ApplicationData.getInstance().selectedFragment.getNumVal());
            }
        }
    };

    public void goToAproposPage(View view) {
        goToAproposPage();
    }

    private void goToAproposPage() {
        Fragment fragment = new AproposFragment();

        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Apropos;
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

        mDrawerLayout.closeDrawer(mDrawerPane);

    }

    private void goToHomePage() {
        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
            ApplicationData.getInstance().accountType = "free";
            Intent mainIntent = new Intent(this, LandingPageActivity.class);
            startActivity(mainIntent);
        }else{
            ApplicationData.getInstance().accountType = "account";
            Intent mainIntent = new Intent(this, LandingPageAccountActivity.class);
            startActivity(mainIntent);
        }
    }
}
