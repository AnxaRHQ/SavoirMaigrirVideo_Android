package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.Notifications.GetNotificationsContract;
import anxa.com.smvideo.contracts.Notifications.MarkNotificationAsReadContract;
import anxa.com.smvideo.contracts.Notifications.NotificationsContract;
import anxa.com.smvideo.ui.CustomListView;
import anxa.com.smvideo.ui.NotificationListAdapter;
import anxa.com.smvideo.util.AppUtil;

/**
 * Created by elaineanxa on 15/07/2019
 */

public class NotificationsActivity extends Activity implements View.OnClickListener, ListView.OnScrollListener
{
    private ImageView backButton;
    private Button backButtonRight;

    private RelativeLayout dummy_view;
    private CustomListView notifListView;
    private View notifLoadMoreProgress;
    private NotificationListAdapter adapter;

    private int Question = 1; //old personal questions tool
    private int Journal = 2; // old carnet minceur
    private int OneToOneCoaching = 3; //new meal journal
    private int QuestionMessage = 4;
    private int Webinar = 13;
    private int LaPauseCafe = 14;

    protected ApiCaller caller;
    private GetNotificationsContract response;
    private List<NotificationsContract> notificationList;

    private long previousDate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notifications);

        ((TextView) (findViewById(R.id.header_title_tv))).setText(getString(R.string.NOTIFICATIONS_TITLE));

        backButton = (ImageView) ((RelativeLayout) findViewById(R.id.headermenu)).findViewById(R.id.header_menu_back);
        backButton.setVisibility(View.GONE);

        backButtonRight = (Button) (findViewById(R.id.header_menu_iv));
        backButtonRight.setBackgroundResource(R.drawable.ic_chevron_right_white_24dp);
        backButtonRight.setVisibility(View.VISIBLE);
        backButtonRight.setOnClickListener(this);

        dummy_view = (RelativeLayout) findViewById(R.id.dummy_view_notif);
        notifListView = (CustomListView) (findViewById(R.id.notifListView));
        notifListView.setOnScrollListener(this);

        notifLoadMoreProgress = getLayoutInflater()
                .inflate(R.layout.progress_bar_footer, null, false);

        /* Load Data */

        notificationList = new ArrayList<>(ApplicationData.getInstance().notificationList.values());

        if (notificationList.size() > 0)
        {
            sort(notificationList);
        }

        if (adapter == null) {
            adapter = new NotificationListAdapter(this, notificationList, this);
        }

        notifListView.setAdapter(adapter);

        /* API Calls */

        caller = new ApiCaller();

        ApplicationData.getInstance().setPreviousDate(AppUtil.getCurrentDateinLong());
        previousDate = AppUtil.getCurrentDateinLong();

        getNotifications();
    }

    @Override
    public void onClick(View v)
    {
        if (v == backButtonRight)
        {
            finish();
        }
        else
        {
            int notifId = (Integer) v.getTag(R.id.notif_id);

            //update notif
            NotificationsContract item = getNotificationByID(notifId);

            if (!item.coach_profile_picture.equalsIgnoreCase("coachPicture")) {
                if (!item.is_read) {
                    markNotifAsReadFirst(item);
                } else {
                    if (Integer.parseInt(item.tooltype_id) == OneToOneCoaching) {
                        launchCarnetPage();

                    } else if (Integer.parseInt(item.tooltype_id) == QuestionMessage) {
                        //new message
                        launchMessagesPage();
                    }
                }
            } else {
                launchCarnetPage();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if(firstVisibleItem + visibleItemCount == totalItemCount)
        {
            if(notifLoadMoreProgress != null)
            {
                if(notifListView.getHeight() == notifLoadMoreProgress.getBottom()) {
                    notifListView.removeFooterView(notifLoadMoreProgress);
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                && (notifListView.getLastVisiblePosition() - notifListView.getHeaderViewsCount() -
                notifListView.getFooterViewsCount()) >= (adapter.getCount() - 1))
        {
            if (ApplicationData.getInstance().getNotificationsCount() != 0)
            {
                notifListView.addFooterView(notifLoadMoreProgress);

                previousDate = ApplicationData.getInstance().getPreviousDate();

                getNotifications();
            }
            else
            {
                notifListView.removeFooterView(notifLoadMoreProgress);
            }
        }
    }

    /* API Calls */

    private void getNotifications()
    {
        caller.GetNotificationsThread(new AsyncResponse()
        {
            @Override
            public void processFinish(Object output)
            {
                response = output != null ? (GetNotificationsContract) output : new GetNotificationsContract();

                if (response != null && response.Data != null && response.Data.Notifications != null && response.Cursor != null)
                {
                    System.out.println("notifications: " + response.Data.Notifications.size());

                    ApplicationData.getInstance().setNotificationsCount(response.Data.Notifications.size());

                    previousDate = response.Cursor.previous;

                    ApplicationData.getInstance().setPreviousDate(previousDate);

                    List<NotificationsContract> notificationsList = (List<NotificationsContract>) response.Data.Notifications;

                    for (NotificationsContract notif : notificationsList)
                    {
                        NotificationsContract n = ApplicationData.getInstance().notificationList.get(notif.notification_id);

                        ApplicationData.getInstance().notificationList.put(notif.notification_id + "", notif);
                    }

                    int unreadCount = 0;

                    for (NotificationsContract notif : ApplicationData.getInstance().notificationList.values())
                    {
                        if (!notif.is_read) {
                            unreadCount++;
                        }
                    }

                    ApplicationData.getInstance().unreadNotifications = unreadCount;

                    notificationList = new ArrayList<>(ApplicationData.getInstance().notificationList.values());

                    sort(notificationList);
                    adapter.updateItems(notificationList);

                    dummy_view.setVisibility(View.GONE);
                }
            }
        }, (int) previousDate);
    }

    private void markNotifAsRead(final int notifId, final int tooltype_id, final int tool_id)
    {
        caller.MarkNotificationAsRead(new AsyncResponse()
        {
            @Override
            public void processFinish(Object output)
            {
                MarkNotificationAsReadContract response_mark = output != null ? (MarkNotificationAsReadContract) output : new MarkNotificationAsReadContract();

                if (response_mark != null)
                {
                    System.out.println("marknotif: " + response_mark.Message);

                    if (response_mark.Message.equalsIgnoreCase("Successful"))
                    {
                        int unreadCount = 0;

                        //update notification
                        NotificationsContract toUpdate = getNotificationByID(notifId);
                        toUpdate.is_read = true;

                        ApplicationData.getInstance().notificationList.remove(notifId);
                        ApplicationData.getInstance().notificationList.put(Integer.toString(notifId), toUpdate);

                        for (NotificationsContract notif : ApplicationData.getInstance().notificationList.values())
                        {
                            if (!notif.is_read) {
                                unreadCount++;
                            }
                        }

                        ApplicationData.getInstance().unreadNotifications = unreadCount;

                        notificationList = new ArrayList<>(ApplicationData.getInstance().notificationList.values());
                        sort(notificationList);

                        adapter.updateItems(notificationList);

                        dummy_view.setVisibility(View.GONE);

                        //proceed to view
                        if (tooltype_id == OneToOneCoaching)
                        {
                            //meal journal
                            launchCarnetPage();
                        }
                        else if (tooltype_id == QuestionMessage)
                        {
                            //new message
                            launchMessagesPage();
                        }
                    }
                }
                System.out.println("marknotif: " + output);
            }
        }, notifId);
    }

    private void sort(final List<NotificationsContract> items)
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (items != null && items.size() > 0) {
                    Collections.sort(items, new Comparator<NotificationsContract>() {
                        public int compare(NotificationsContract notif1, NotificationsContract notif2) {
                            return Long.toString(notif2.display_date).compareTo(Long.toString(notif1.display_date));
                        }
                    });
                }
            }
        });
    }

    private NotificationsContract getNotificationByID(int notifid)
    {
        for (NotificationsContract notif : notificationList) {
            if (notif.notification_id == notifid) {
                return notif;
            }
        }
        return null;
    }

    private void markNotifAsReadFirst(NotificationsContract notificationsContract)
    {
        markNotifAsRead(notificationsContract.notification_id, Integer.parseInt(notificationsContract.tooltype_id), notificationsContract.tool_id);
    }

    private void launchMessagesPage()
    {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Notifications;

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MessagesAccountFragment messagesAccountFragment = new MessagesAccountFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromNotifications", true);
        bundle.putString("selectedButton", getString(R.string.menu_account_messages));
        messagesAccountFragment.setArguments(bundle);

        fragmentTransaction.add(R.id.notificationsview, messagesAccountFragment, "");
        fragmentTransaction.commit();
    }

    private void launchCarnetPage()
    {
        ApplicationData.getInstance().selectedFragment = ApplicationData.SelectedFragment.Account_Notifications;

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        MessagesAccountFragment messagesAccountFragment = new MessagesAccountFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromNotifications", true);
        bundle.putString("selectedButton", getString(R.string.menu_account_carnet));
        messagesAccountFragment.setArguments(bundle);

        fragmentTransaction.add(R.id.notificationsview, messagesAccountFragment, "");
        fragmentTransaction.commit();
    }
}