package anxa.com.smvideo.activities;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import anxa.com.smvideo.BuildConfig;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import anxa.com.smvideo.AlarmReceiver;
import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.account.AproposFragment;
import anxa.com.smvideo.activities.account.CoachingAccountFragment;
import anxa.com.smvideo.activities.account.DtsWebkitFragment;
import anxa.com.smvideo.activities.account.LandingPageAccountActivity;
import anxa.com.smvideo.activities.account.MessagesAccountFragment;
import anxa.com.smvideo.activities.account.MonCompteAccountFragment;
import anxa.com.smvideo.activities.account.NotificationsActivity;
import anxa.com.smvideo.activities.account.SearchFragment;
import anxa.com.smvideo.activities.account.VideosFragment;
import anxa.com.smvideo.activities.account.WebkitFragment;
import anxa.com.smvideo.activities.free.BilanMinceurActivity;
import anxa.com.smvideo.activities.free.DiscoverActivity;
import anxa.com.smvideo.activities.free.LandingPageActivity;
import anxa.com.smvideo.activities.free.MonCompteActivity;
import anxa.com.smvideo.activities.free.RecipesActivity;
import anxa.com.smvideo.activities.free.TemoignagesActivity;
import anxa.com.smvideo.common.WebkitURL;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.AlertsContract;
import anxa.com.smvideo.contracts.GetAlertsResponseContract;
import anxa.com.smvideo.contracts.Notifications.NotificationsContract;
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
    private ImageView notifHeader;
    ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter filter = new IntentFilter();
        filter.addAction(this.getResources().getString(R.string.bilan_broadcast_subscribe));
        this.getApplicationContext().registerReceiver(the_receiver, filter);

        //apropos_ll = (LinearLayout)findViewById(R.id.apropos_ll);

        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free"))
        {
            mNavItems.add(new NavItem(getString(R.string.menu_home), R.drawable.icon_home));
            mNavItems.add(new NavItem(getString(R.string.menu_decouvrir), R.drawable.decouvrez_ico));
            mNavItems.add(new NavItem(getString(R.string.menu_bilan), R.drawable.bilanminceur_ico));
            mNavItems.add(new NavItem(getString(R.string.menu_temoignages), R.drawable.temoignage_ico));
            mNavItems.add(new NavItem(getString(R.string.menu_recettes), R.drawable.recettes_ico));
            mNavItems.add(new NavItem(getString(R.string.menu_mon_compte), R.drawable.compte_ico));
        }
        else {
            String welcome_message;
            if (ApplicationData.getInstance().userDataContract.FirstName != null)
            {
                welcome_message = getString(R.string.welcome_account_1).replace("%@", ApplicationData.getInstance().userDataContract.FirstName).concat(getString(R.string.welcome_account_2).replace("%f", AppUtil.getCurrentDayName(AppUtil.getCurrentDayNumber()))).concat(getString(R.string.welcome_account_3).replace("%d", Integer.toString(ApplicationData.getInstance().currentWeekNumber)));
                System.out.println("welcome message: " + welcome_message);
            } else {
                welcome_message = getString(R.string.welcome_account_1).replace("%@", ApplicationData.getInstance().userName).concat(getString(R.string.welcome_account_2).replace("%f", AppUtil.getCurrentDayName(AppUtil.getCurrentDayNumber()))).concat(getString(R.string.welcome_account_3).replace("%d", Integer.toString(ApplicationData.getInstance().currentWeekNumber)));
                System.out.println("welcome message: " + welcome_message);
            }

            mNavItems.add(new NavItem(getString(R.string.menu_home)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_fiches)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_nutrition)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_coaching)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_communaute)));
            mNavItems.add(new NavItem(getString(R.string.left_nav_account_videos)));
            //mNavItems.add(new NavItem(getString(R.string.menu_account_boutique)));
            //mNavItems.add(new NavItem(getString(R.string.menu_account_espacevip)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_notifications)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_invitations)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_message)));
            mNavItems.add(new NavItem(getString(R.string.menu_account_paramducompte)));
            mNavItems.add(new NavItem(getString(R.string.mon_compte_disconnect)));
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

        //landing page on the first launch
        /*if (ApplicationData.getInstance().showLandingPage) {
            if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free")) {
                launchActivity(LandingPageActivity.class);
            } else {
                goToHomePage();
            }
        }*/
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT") != null) {
            
        }else{
                if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free"))
                {
                    if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Home) {
                        goToHomePage();
                    }else if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Account_Apropos) {
                        goToAproposPage();
                    }else {
                        selectItemFromDrawer(ApplicationData.getInstance().selectedFragment.getNumVal()+1);
                    }
                } else {
                    getUserAlerts();
                    //initial
                    if (ApplicationData.getInstance().selectedFragment.getNumVal() < 5) {
                        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Coaching;
                    } else if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Account_Apropos) {
                        goToAproposPage();
                    } else if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Home) {
                        goToHomePage();
                    } else if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Account_CoachingNative) {

                        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_CoachingNative;
                        goToFragmentPage(new CoachingAccountFragment());

                    } else if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Account_Messages) {
                        selectItemFromDrawer(8);
                    } else if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Account_Carnet) {

                        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Carnet;

                        MessagesAccountFragment messagesAccountFragment = new MessagesAccountFragment();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("fromNotifications", false);
                        bundle.putString("selectedButton", getString(R.string.menu_account_carnet));
                        messagesAccountFragment.setArguments(bundle);

                        goToFragmentPage(messagesAccountFragment);

                    } else if (ApplicationData.getInstance().selectedFragment == ApplicationData.SelectedFragment.Account_MonCompte) {
                        selectItemFromDrawer(9);
                    } else {
                        selectItemFromDrawer(ApplicationData.getInstance().selectedFragment.getNumVal() - 4);
                    }
                }
            }

    }

    /*
 * Called when a particular item from the navigation drawer is selected.*/
    private void selectItemFromDrawer(int position)
    {
        Fragment fragment = new LandingPageAccountActivity();
        Bundle bundle = new Bundle();
        System.out.println("selected from drawer: " + position);

        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free"))
        {
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
                    fragment = new LandingPageAccountActivity();
                    break;
            }
        } else {
            switch (position) {
                case 0:
                    goToHomePage();
                    break;
                case 1: //fiches

                    if (!CheckFreeUser(true))
                    {
                        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Fiches;
                        fragment = new WebkitFragment();

                        bundle.putString("header_title", getString(R.string.nav_account_fiches));
                        bundle.putString("webkit_url", WebkitURL.fichesWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));
                        fragment.setArguments(bundle);
                    }

                    break;
                case 2: //nutrition

                    if (!CheckFreeUser(true))
                    {
                        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Nutrition;
                        fragment = new WebkitFragment();
                        bundle.putString("header_title", getString(R.string.nav_account_nutrition));
                        bundle.putString("webkit_url", WebkitURL.nutritionWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));
                        fragment.setArguments(bundle);
                    }

                    break;
                case 3: //coaching

                    if (!CheckFreeUser(true))
                    {
                        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Coaching;
                        fragment = new WebkitFragment();
                        bundle.putString("header_title", getString(R.string.nav_account_coaching));
                        bundle.putString("webkit_url", WebkitURL.coachingWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));
                        fragment.setArguments(bundle);
                    }

                    break;
                case 4: //community

                    if (!CheckFreeUser(true))
                    {
                        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Communaute;
                        fragment = new WebkitFragment();
                        bundle.putString("header_title", getString(R.string.nav_account_communaute));
                        bundle.putString("webkit_url", WebkitURL.communityWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));
                        fragment.setArguments(bundle);
                    }

                    break;
                case 5: //500 videos

                    if (!CheckFreeUser(true))
                    {
                        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Videos;
                        fragment = new VideosFragment();
                    }

                    break;
                case 6: //notifications

                    if (!CheckFreeUser(true))
                    {
                        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Home;
                        Intent intent = new Intent(this, NotificationsActivity.class);
                        startActivity(intent);
                    }

                    break;
                case 7: //invitations

                    if (!CheckFreeUser(true))
                    {
                        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Invitations;
                        fragment = new WebkitFragment();
                        bundle.putString("header_title", getString(R.string.menu_account_invitations));
                        bundle.putString("webkit_url", WebkitURL.invitationsWebkitUrl.replace("%regId", Integer.toString(ApplicationData.getInstance().userDataContract.Id)));
                        fragment.setArguments(bundle);
                    }

                    break;
                case 8: //messages

                    ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Messages;
                    bundle.putBoolean("fromNotifications", false);
                    fragment = new MessagesAccountFragment();
                    fragment.setArguments(bundle);

                    break;
                case 9: //parameters du compte

                    ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_MonCompte;
                    fragment = new MonCompteAccountFragment();

                    break;
                case 10: //deconnection
                    logoutUser();
                    break;
                default:
                    fragment = new LandingPageAccountActivity();
                    break;
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
        if(bundle.getString("header_title") != null)
        {
            setTitle(bundle.getString("header_title"));
        }


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
        else if (view.getId() == R.id.header_search) {
            if (!CheckFreeUser(true))
            {
                //search menu
                goToSearchPage();
            }
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

    private void goToHomePage()
    {
        if (ApplicationData.getInstance().accountType.equalsIgnoreCase("free"))
        {
            ApplicationData.getInstance().accountType = "free";
            Intent mainIntent = new Intent(this, LandingPageActivity.class);
            startActivity(mainIntent);
        }
        else
        {
            Fragment fragment = new LandingPageAccountActivity();
            getUserAlerts();
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

            //mDrawerLayout.closeDrawer(mDrawerPane);
        }
    }

    public void goToNotificationsPage(View view)
    {
        if (!CheckFreeUser(true))
        {
            Intent intent = new Intent(this, NotificationsActivity.class);
            startActivity(intent);
        }
    }

    public void goToWebinarPage(View view)
    {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Consultation;
        Bundle bundle = new Bundle();
        bundle.putString("header_title", getString(R.string.nav_account_webinars));
        bundle.putString("webkit_url", WebkitURL.webinarWebkitUrl);

        Fragment fragment = new WebkitFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragment.setArguments(bundle);

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

    private void goToFragmentPage(Fragment fragment)
    {
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

    private void goToDtsPage(ApplicationData.SelectedFragment selectedFragment, Bundle bundle)
    {
        ApplicationData.getInstance().selectedFragment = selectedFragment;

        Fragment fragment = new DtsWebkitFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragment.setArguments(bundle);

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

    private void goToSearchPage()
    {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Search;

        Fragment fragment = new SearchFragment();
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

    public void getUserAlerts()
    {
        caller.GetUserAlerts(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                AlertsContract Alert;

                GetAlertsResponseContract response = output != null ? (GetAlertsResponseContract) output : new GetAlertsResponseContract();
                Log.d("UserAlerts", "");

                if (response != null && response.Data != null && response.Data.Alerts != null) {

                    System.out.println("check if there is content: " + ApplicationData.getInstance().alertsDataArrayList.size());
                    ApplicationData.getInstance().alertsDataArrayList.clear();
//                    ApplicationData.getInstance().alertsDataArrayList.clear();
                    for (int i = 0; i < response.Data.Alerts.size(); i++) {
                        if (i < response.Data.Alerts.size()) {
                            Alert = response.Data.Alerts.get(i);
//                      Log.d("UserAlerts", Alert.Message );
                            StartNotification(Alert, i);
                        }
                    }
                }

            }
        }, ApplicationData.getInstance().regId);
    }

    private void StartNotification(AlertsContract AlertsData, int NotificationID)
    {
        System.out.println("startnotification: " + AlertsData.Message);
        System.out.println("NotificationID: " + NotificationID);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getDefault());
        Calendar calCurr = Calendar.getInstance();
        calCurr.setTimeZone(TimeZone.getDefault());

        Intent notificationIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        notificationIntent.putExtra("Title", getResources().getString(R.string.app_name));
        notificationIntent.putExtra("Ticker", AlertsData.Message);
        notificationIntent.putExtra("Message", AlertsData.Message);
        notificationIntent.putExtra("NotificationID", NotificationID + (2 * 100));
        notificationIntent.setPackage(BuildConfig.APPLICATION_ID);

        PendingIntent broadcast = PendingIntent.getBroadcast(this, NotificationID + (2 * 100), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Log.d("Notification", "Do I need a notification? " + AlertsData.IsRemind );
        if (AlertsData.IsRemind == true) {
            // Uncomment only when debugging
            //cal.add(Calendar.SECOND, 5);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, broadcast);

//            Log.d("Notification", "Notification Time should be " + AlertsData.starttime);
            cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(AlertsData.starttime.substring(0, 2)));
            cal.set(Calendar.MINUTE, Integer.parseInt(AlertsData.starttime.substring(3)));
            cal.set(Calendar.SECOND, 0);
            cal.setFirstDayOfWeek(Calendar.MONDAY);

            if (AlertsData.DaysOfWeek != null) {
                for (char ch: AlertsData.DaysOfWeek.toCharArray()) {
                    if (AlertsData.DaysOfWeek.equalsIgnoreCase("7")) {
                        cal.set(Calendar.DAY_OF_WEEK, 1);
                    } else {
                        cal.set(Calendar.DAY_OF_WEEK, Integer.parseInt(String.valueOf(ch)) + 1);
                    }
                }
            }

            //add local notifications
            NotificationsContract contractData = new NotificationsContract();
            contractData.ErrorCount = AlertsData.starttime;
            contractData.notification_text = AlertsData.Message;
            contractData.coach_profile_picture = "coachPicture";
            contractData.is_read = false;
            contractData.date_created_utc = calCurr.getTimeInMillis();

            contractData.notification_id = NotificationID;


            System.out.println("AppUtil display_date: " + AppUtil.getDateInLongUtc(cal, AlertsData.starttime));


            Date date1 = cal.getTime();
            Date date2 = calCurr.getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy hh:mm");
            Log.d("Notification 1", "Daily notif set dt" + df.format(cal.getTime()));

            switch (AlertsData.AlertType) {
                case 1: //Daily


                    if( date1.compareTo(date2) < 0) {
                        cal.add(Calendar.HOUR, 24);
                    }
                    //alarmManager.cancel(broadcast);

                    if (Build.VERSION.SDK_INT >= 23) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,  cal.getTimeInMillis(), broadcast);
                    } else if (Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                    }
                    //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY, broadcast);
                    //alarmManager.set(AlarmManager.RTC_WAKEUP, AppUtil.getDateInLong(cal, AlertsData.starttime), broadcast);
                    Log.d("Notification", "Daily notif set" + cal.getTimeInMillis());
                    contractData.display_date = cal.getTimeInMillis()/1000L;
                    contractData.toolaction_id = "daily";
                    contractData.tooltype_id = AlertsData.AlertItem;
                    break;
                case 2: //Weekly
                    if( date1.compareTo(date2) < 0) {
                        cal.add(Calendar.WEEK_OF_YEAR, 1);
                    }
                    //alarmManager.cancel(broadcast);
                    if (Build.VERSION.SDK_INT >= 23) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                    } else if (Build.VERSION.SDK_INT >= 19) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);
                    }
                    // if(AppUtil.getDateInLong(cal, AlertsData.starttime) > calCurr.getTime().getTime() / 1000L){
                    //alarmManager.set(AlarmManager.RTC_WAKEUP, AppUtil.getDateInMillisUtc(cal, AlertsData.starttime), broadcast);
                    //}
                    //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, broadcast);
                    contractData.display_date = cal.getTimeInMillis()/1000L;
                    contractData.toolaction_id = "weekly";
                    contractData.tooltype_id = AlertsData.AlertItem;
                    break;
                case 3: //Monthly
                    if( date1.compareTo(date2) < 0) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, AppUtil.getDateInLong(cal, AlertsData.starttime), broadcast);
                    }
                    //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 31, broadcast);
                    //alarmManager.set(AlarmManager.RTC_WAKEUP, AppUtil.getDateInLong(cal, AlertsData.starttime), broadcast);
                    contractData.display_date = cal.getTimeInMillis();
                    contractData.toolaction_id = "monthly";
                    contractData.tooltype_id = AlertsData.AlertItem;
                    break;
                case 4: //Yearly
                    if( date1.compareTo(date2) < 0) {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, AppUtil.getDateInLong(cal, AlertsData.starttime), broadcast);
                    }
                    //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 365, broadcast);
                    //alarmManager.set(AlarmManager.RTC_WAKEUP, AppUtil.getDateInLong(cal, AlertsData.starttime), broadcast);
                    contractData.display_date = cal.getTimeInMillis();
                    contractData.toolaction_id = "yearly";
                    contractData.tooltype_id = AlertsData.AlertItem;
                    break;

            }
            if(!ApplicationData.getInstance().alertsDataArrayList.contains(contractData)){
                ApplicationData.getInstance().alertsDataArrayList.add(contractData);
            }

            //}

            //Log.d("Notification", "Creating notification #" + NotificationID + " for " + cal.getTime());
        }
    }



    /* Free Users */

    public boolean CheckFreeUser(boolean withDialog)
    {
        if (ApplicationData.getInstance().userDataContract.MembershipType == 0 && ApplicationData.getInstance().userDataContract.WeekNumber > 1)
        {
            if (withDialog)
            {
                showFreeExpiredDialog();
            }

            return true;
        }

        return false;
    }

    private void showFreeExpiredDialog()
    {
        final Dialog freeExpiredDialog = new Dialog(this);
        freeExpiredDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        freeExpiredDialog.setContentView(R.layout.free_expired_dialog);
        freeExpiredDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((TextView) freeExpiredDialog.findViewById(R.id.dialog_content)).setText(getString(R.string.FREE_1WEEKTRIAL_EXPIRED));

        ((Button) freeExpiredDialog.findViewById(R.id.dialog_cancel)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                freeExpiredDialog.dismiss();
            }
        });

        ((Button) freeExpiredDialog.findViewById(R.id.dialog_payment)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                freeExpiredDialog.dismiss();
                goToPremiumPayment();
            }
        });

        freeExpiredDialog.show();
    }

    private void goToPremiumPayment()
    {
        Intent mainContentBrowser = new Intent(this, NpnaOfferActivity.class);
        mainContentBrowser.putExtra("UPGRADE_PAYMENT", true);
        startActivity(mainContentBrowser);
    }
}
