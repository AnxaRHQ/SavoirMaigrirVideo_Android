package anxa.com.smvideo.activities.account;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.MessagesContract;
import anxa.com.smvideo.contracts.MessagesResponseContract;
import anxa.com.smvideo.contracts.PostMessagesContract;
import anxa.com.smvideo.ui.MessagesListLayout;
import anxa.com.smvideo.util.AppUtil;

/**
 * Created by aprilanxa on 22/03/2018.
 */

public class MessagesAccountFragment extends BaseFragment implements View.OnClickListener {

    MessagesListLayout commentList;
    ProgressBar progressBar;
    TextView submit_tv;
    EditText comment_et;
    PostMessagesContract newPostMessageContract;
    Button loadMore_btn;
    RelativeLayout loadMore_layout;
    RelativeLayout carnetContent;
    RelativeLayout messagesContent;

    MessagesResponseContract response;

    List<MessagesContract> items;
    Button messages_btn;
    Button carnet_btn;
    Button menu_button;
    private ImageView backButton;

    long previousDate;

    boolean loadPrevious = false;
    boolean loadBrowser = false;

    Dialog freeDialog;
    AlertDialog.Builder alertBuilder;
    private static final int BROWSERTAB_ACTIVITY = 1011;
    String intentExtra;
    private int allowedQuestionsToAsk = 0;
    private int totalCreditsWeek = 0;

    public Context context;
    protected ApiCaller caller;
    TextView charCount;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.context = getActivity();
        mView = inflater.inflate(R.layout.messages_account, null);

        caller = new ApiCaller();

        //header change
        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(getString(R.string.menu_account_dieticienne));

        items = new ArrayList<>(ApplicationData.getInstance().messagesList);

        menu_button = (Button) mView.findViewById(R.id.header_menu_iv);

        submit_tv = (TextView) mView.findViewById(R.id.btnSubmit);
        submit_tv.setOnClickListener(this);
        comment_et = (EditText) mView.findViewById(R.id.comment_et);
        comment_et.setHint(R.string.messages_add_a_comment);
        commentList = (MessagesListLayout) mView.findViewById(R.id.commentlist);
        loadMore_btn = (Button) mView.findViewById(R.id.loadMoreButton);
        loadMore_btn.setOnClickListener(this);
        progressBar = (ProgressBar) mView.findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        loadMore_layout = (RelativeLayout) mView.findViewById(R.id.loadMoreLayout);
        messagesContent = (RelativeLayout) mView.findViewById(R.id.messagesContent);
        carnetContent = (RelativeLayout) mView.findViewById(R.id.carnetContent);

        messages_btn = (Button) mView.findViewById(R.id.messages_button);
        carnet_btn = (Button) mView.findViewById(R.id.carnet_button);

        messages_btn.setSelected(true);
        messages_btn.setOnClickListener(this);
        carnet_btn.setOnClickListener(this);
        progressBar.setVisibility(View.VISIBLE);

        charCount = (TextView)mView.findViewById(R.id.charCount);
        comment_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub

                int length = comment_et.length();
                String convert = String.valueOf(length) + "/450";
                charCount.setText(convert);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

        if (items != null && items.size() > 1) {
            sort(items);
        }
        commentList.initData(items, context, null);

        backButton = (ImageView) ((RelativeLayout) mView.findViewById(R.id.headermenu)).findViewById(R.id.header_menu_back);
        backButton.setOnClickListener(this);

        if ((ApplicationData.getInstance().userDataContract.SubscriptionType == 65
                || ApplicationData.getInstance().userDataContract.SubscriptionType == 66
                || ApplicationData.getInstance().userDataContract.SubscriptionType == 67
                || ApplicationData.getInstance().userDataContract.SubscriptionType == 68
                || ApplicationData.getInstance().userDataContract.SubscriptionType == 95) && !ApplicationData.getInstance().userDataContract.IsVip) {
            showSubTypeCannotAskDialog();
        }

        /* Notifications Conditions */

        if (getArguments().getBoolean("fromNotifications") == true)
        {
            if (getArguments().get("selectedButton") == getString(R.string.menu_account_messages))
            {
                messages_btn.setSelected(true);
            }
            else if (getArguments().get("selectedButton") == getString(R.string.menu_account_carnet))
            {
                messages_btn.setSelected(false);
                carnet_btn.setSelected(true);
                messagesContent.setVisibility(View.GONE);
                carnetContent.setVisibility(View.VISIBLE);
                loadCarnetFragment();
            }

            menu_button.setVisibility(View.GONE);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessagesAccountFragment.super.goBackToNotifications();
                }
            });
        }
        else
        {
            if (getArguments().get("selectedButton") == getString(R.string.menu_account_carnet))
            {
                messages_btn.setSelected(false);
                carnet_btn.setSelected(true);
                messagesContent.setVisibility(View.GONE);
                carnetContent.setVisibility(View.VISIBLE);
                loadCarnetFragment();
            }
            else
            {
                messages_btn.setSelected(true);
                backButton.setOnClickListener(this);
            }
        }

        super.onCreateView(inflater, container, savedInstanceState);
        return mView;
    }

    public void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter();
        filter.addAction("END_OF_LINE");
        filter.addAction("SCROLL_STARTED");
        filter.addAction("TOP_OF_LINE");
        filter.addAction("URL_LOAD");
        context.getApplicationContext().registerReceiver(the_receiver, filter);

        loadPrevious = false;
        newPostMessageContract = new PostMessagesContract();
        //items.clear();

        ApplicationData.getInstance().setPreviousDate(AppUtil.getCurrentDateinLong());
        previousDate = AppUtil.getCurrentDateinLong();

        if (getArguments().getBoolean("firstIteration"))
        {
            getPreviousQuestionsThread(true);

            Bundle b = getArguments();
            b.putBoolean("firstIteration", false);

            this.setArguments(b);
        }
        else
        {
            getPreviousQuestionsThread(false);
        }
    }

    public void onPause() {
        super.onPause();
        context.getApplicationContext().unregisterReceiver(the_receiver);
    }

    public void postComment(View view)
    {
        if (!CheckFreeUser(true))
        {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(comment_et.getWindowToken(), 0);

            // check if the text view has something
            if (comment_et != null && comment_et.getText() != null)
            {
                if (comment_et.getText().toString().trim().length() > 0)
                {
                    progressBar.setVisibility(View.VISIBLE);

                    newPostMessageContract.MessageChat = comment_et.getText().toString();
                    newPostMessageContract.CreatedDate = AppUtil.getCurrentDateinLongGMT();
                    newPostMessageContract.RegId = ApplicationData.getInstance().regId;

                    postQuestionsThread();
                }
            }
        }
    }

    private void showSubTypeCannotAskDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setMessage(getResources().getString(R.string.message_cannotaskquesetionbecauseofsubtype))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        submit_tv.setEnabled(false);
                    }
                })
                .show();
    }
    public void loadMoreMessages(View view) {
        loadPrevious = true;
        startProgressBar();
        getPreviousQuestionsThread(false);
    }

    /**
     * Private Methods
     **/
    private void getLatestQuestionsThread()
    {
        caller.GetLatestMessagesThread(new AsyncResponse()
        {
            @Override
            public void processFinish(Object output)
            {
                MessagesContract questions;

                response = output != null ? (MessagesResponseContract) output : new MessagesResponseContract();

                if (response != null && response.Data != null && response.Data.Messages != null && response.Cursor != null)
                {
                    ApplicationData.getInstance().setBeforeDate(response.Cursor.before);
                    ApplicationData.getInstance().setPreviousDate(response.Cursor.previous);
                    previousDate = response.Cursor.previous;

                    //do whatever
                    items.addAll(response.Data.Messages);

                    ApplicationData.getInstance().messagesResponseContract = response;
                    ApplicationData.getInstance().messagesList.clear();
                    ApplicationData.getInstance().messagesList.addAll(items);

                    updateUI(items);

                    progressBar.setVisibility(View.GONE);

                    if (loadPrevious) {
                        loadMore_layout.setVisibility(View.GONE);
                        loadMore_btn.setVisibility(View.GONE);
                    } else {
                        loadMore_layout.setVisibility(View.VISIBLE);
                        loadMore_btn.setVisibility(View.VISIBLE);
                        loadMore_btn.setText(getResources().getString(R.string.messages_load_more));
                    }
                }
            }
        }, ApplicationData.getInstance().userDataContract.Id, (int) (System.currentTimeMillis() / 1000L));
    }

    private void getPreviousQuestionsThread(final boolean firstIteration)
    {
        caller.GetPreviousMessagesThread(new AsyncResponse()
        {
            @Override
            public void processFinish(Object output)
            {
                response = output != null ? (MessagesResponseContract) output : new MessagesResponseContract();

                if (response != null && response.Data != null && response.Data.Messages != null && response.Cursor != null)
                {
                    ApplicationData.getInstance().setBeforeDate(response.Cursor.before);
                    ApplicationData.getInstance().setPreviousDate(response.Cursor.previous);
                    previousDate = response.Cursor.previous;

                    ApplicationData.getInstance().messagesResponseContract = response;

                    items.addAll(response.Data.Messages);
                    sort(items);

                    commentList.updateData(items);

                    //save to database
                    // MessageDAO msgDao = new MessageDAO(context, null);
                    // DaoImplementer implDao = new DaoImplementer(msgDao, context);

                    MessagesContract message;

                    for (int i = 0; i < items.size(); i++)
                    {
                        message = items.get(i);
                        if (message.CoachIdLiked > 0)
                        {
                            if (isAdded())
                            {
                                String likeString = getResources().getString(R.string.messages_liked);
                                likeString = likeString.replace("%@", message.CoachLikedName);
                                message.MessageChat = message.MessageChat.replace("<br><br><i>" + likeString + "</i>", "");
                                message.MessageChat = message.MessageChat + "<br><br><i>" + likeString + "</i>";
                            }
                            else
                            {
                                message.MessageChat = "";
                            }
                        }
                        // implDao.add(message);
                    }

                    totalCreditsWeek = response.Data.CreditsUsedWeek;

                    if (firstIteration)
                    {
                        try
                        {
                            DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                            Date date1 = format.parse("05/11/2018");
                            Date date2 = format.parse("04/02/2019");
                            Date dateRegistered =  format.parse(ApplicationData.getInstance().userDataContract.DateRegistered);

                            if(!ApplicationData.getInstance().userDataContract.IsVip && dateRegistered.compareTo(date1) >= 0 && dateRegistered.compareTo(date2) < 0  && firstIteration)
                            {
                                if (response.Data.CreditsUsedWeek  >= 2)
                                {
                                    ((TextView)mView.findViewById(R.id.messageLimit)).setVisibility(View.VISIBLE);
                                    ((RelativeLayout)mView.findViewById(R.id.questionsAskContainer)).setVisibility(View.GONE);
                                }
                                else
                                {
                                    if (items.size() > 1) {
                                        if (items.get(items.size() - 1).CoachId > 0) {
                                            allowedQuestionsToAsk = 2;
                                        }
                                        if (items.get(items.size() - 2).CoachId > 0) {
                                            allowedQuestionsToAsk++;
                                        }
                                    }
                                    else if(items.size() == 1) {
                                        if (items.get(items.size() - 1).CoachId > 0) {
                                            allowedQuestionsToAsk = 2;
                                        }
                                    }
                                    else if(items.size() == 0) {
                                        allowedQuestionsToAsk = 2;
                                    }

                                    if(allowedQuestionsToAsk > 2)
                                    {
                                        allowedQuestionsToAsk = 2;
                                    }
                                    if(response != null && response.Data != null && response.Data.IsLastMessageArchivedByCoach)
                                    {
                                        allowedQuestionsToAsk = 2;
                                    }
                                    if(allowedQuestionsToAsk == 0)
                                    {
                                        ((TextView)mView.findViewById(R.id.messageLimit)).setVisibility(View.VISIBLE);
                                        ((RelativeLayout)mView.findViewById(R.id.questionsAskContainer)).setVisibility(View.GONE);
                                    }
                                }
                            }
                            else if(!ApplicationData.getInstance().userDataContract.IsVip && dateRegistered.compareTo(date2) >= 0) {
                                if( response.Data.CreditsUsedWeek  >= 1){
                                    ((TextView)mView.findViewById(R.id.messageLimit)).setVisibility(View.VISIBLE);
                                    ((RelativeLayout)mView.findViewById(R.id.questionsAskContainer)).setVisibility(View.GONE);
                                }
                                else {
                                    if (items.size() > 1) {
                                        if (items.get(items.size() - 1).CoachId > 0) {
                                            allowedQuestionsToAsk = 1;
                                        }
                                        if (items.get(items.size() - 2).CoachId > 0) {
                                            allowedQuestionsToAsk++;
                                        }
                                    }
                                    else if(items.size() == 1){
                                        if (items.get(items.size() - 1).CoachId > 0) {
                                            allowedQuestionsToAsk++;
                                        }
                                    }
                                    else if(items.size() == 0) {
                                        allowedQuestionsToAsk = 1;
                                    }

                                    if(allowedQuestionsToAsk > 1)
                                    {
                                        allowedQuestionsToAsk = 1;
                                    }
                                    if(response != null && response.Data != null && response.Data.IsLastMessageArchivedByCoach)
                                    {
                                        allowedQuestionsToAsk = 1;
                                    }
                                    if(allowedQuestionsToAsk == 0)
                                    {
                                        ((TextView)mView.findViewById(R.id.messageLimit)).setVisibility(View.VISIBLE);
                                        ((RelativeLayout)mView.findViewById(R.id.questionsAskContainer)).setVisibility(View.GONE);
                                    }
                                }
                            } else {
                                if (ApplicationData.getInstance().userDataContract.IsVip) {
                                    allowedQuestionsToAsk = 10;
                                } else {
                                    if (items.size() > 1) {
                                        if (items.get(items.size() - 1).CoachId > 0) {
                                            allowedQuestionsToAsk = 1;
                                        }
                                        if (items.get(items.size() - 2).CoachId > 0) {
                                            allowedQuestionsToAsk++;
                                        }
                                    }
                                    else if(items.size() == 1){
                                        if (items.get(items.size() - 1).CoachId > 0) {
                                            allowedQuestionsToAsk++;
                                        }
                                    }
                                    else if(items.size() == 0) {
                                        allowedQuestionsToAsk = 1;
                                    }

                                    if(allowedQuestionsToAsk > 1)
                                    {
                                        allowedQuestionsToAsk = 1;
                                    }
                                    if(response != null && response.Data != null && response.Data.IsLastMessageArchivedByCoach)
                                    {
                                        allowedQuestionsToAsk = 1;
                                    }
                                    if(allowedQuestionsToAsk == 0)
                                    {
                                        ((TextView)mView.findViewById(R.id.messageLimit)).setVisibility(View.VISIBLE);
                                        ((RelativeLayout)mView.findViewById(R.id.questionsAskContainer)).setVisibility(View.GONE);
                                    }
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    ApplicationData.getInstance().messagesResponseContract = response;
                    ApplicationData.getInstance().messagesList.clear();
                    ApplicationData.getInstance().messagesList.addAll(response.Data.Messages);

                    progressBar.setVisibility(View.GONE);

                    if (loadPrevious) {
                        loadMore_layout.setVisibility(View.GONE);
                        loadMore_btn.setVisibility(View.GONE);
                    } else {
                        loadMore_layout.setVisibility(View.VISIBLE);
                        loadMore_btn.setVisibility(View.VISIBLE);
                        Activity activity = getActivity();

                        if (activity != null) {
                            loadMore_btn.setText(getResources().getString(R.string.messages_load_more));
                        }
                    }
                }
            }
        }, (int) previousDate);
    }

    private void postQuestionsThread()
    {
        caller.PostMessage(new AsyncResponse()
        {
            @Override
            public void processFinish(Object output)
            {
                //successful post of message
                if (output != null)
                {
                    //do whatever
                    MessagesContract newQuestionsContract = new MessagesContract();
                    newQuestionsContract.MessageChat = newPostMessageContract.MessageChat;
                    newQuestionsContract.CoachId = 0;
                    newQuestionsContract.DateCreated = AppUtil.getMessageCreatedDateForDisplay(newPostMessageContract.CreatedDate);
                    newQuestionsContract.RegId = newPostMessageContract.RegId;

                    items.add(newQuestionsContract);

//                    DaoImplementer implDao = new DaoImplementer(new MessageDAO(context, null), context);
//                    implDao.add(newQuestionsContract);
                    sort(items);
                    progressBar.setVisibility(View.GONE);

                    ApplicationData.getInstance().messagesList.add(newQuestionsContract);

                    clearNewCommentUI();

                    if (!ApplicationData.getInstance().userDataContract.IsVip)
                    {
                        allowedQuestionsToAsk--;

                        allowedQuestionsToAsk--;

                        if (allowedQuestionsToAsk <= 0)
                        {
                            ((TextView) mView.findViewById(R.id.messageLimit)).setVisibility(View.VISIBLE);
                            ((RelativeLayout) mView.findViewById(R.id.questionsAskContainer)).setVisibility(View.GONE);
                        }
                    }
                }
                else
                {
                    progressBar.setVisibility(View.GONE);

                    final String messageDialog = getResources().getString(R.string.ALERTMESSAGE_OFFLINE);

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            comment_et.setText(newPostMessageContract.MessageChat);
                            showCustomDialog(messageDialog);
                        }
                    });
                }
            }
        }, newPostMessageContract);
    }

    private BroadcastReceiver the_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == "TOP_OF_LINE") {
                if (previousDate < 1) {
                    hideLoadMoreButton();
                } else {
                    showLoadMoreButton();
                }
            } else if (intent.getAction() == "SCROLL_STARTED") {
            } else if (intent.getAction() == "URL_LOAD") {
                loadURL(ApplicationData.getInstance().urlClicked);
            }
        }
    };

    private void hideLoadMoreButton()
    {
        loadMore_layout.setVisibility(View.GONE);
        loadMore_btn.setVisibility(View.GONE);
    }

    private void showLoadMoreButton()
    {
        loadMore_layout.setVisibility(View.VISIBLE);
        loadMore_btn.setVisibility(View.VISIBLE);
        loadMore_btn.setText(getResources().getString(R.string.messages_load_more));
    }

    private void updateUI(List<MessagesContract> updateItems)
    {
        sort(updateItems);
        commentList.updateData(updateItems);
    }

    private void clearNewCommentUI()
    {
        comment_et.setText("");
        updateUI(items);
    }

    private void sort(final List<MessagesContract> questionsItems)
    {
        if (questionsItems != null && questionsItems.size() > 0)
        {
            Collections.sort(questionsItems, new Comparator<Object>() {
                public int compare(Object notif1, Object notif2) {
                    return (AppUtil.toDate(((MessagesContract) notif1).DateCreated).compareTo(AppUtil.toDate(((MessagesContract) notif2).DateCreated)));
                }
            });
        }
    }

    private void startProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void stopProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void loadURL(String url)
    {
        Intent mainContentBrowser = new Intent(this.getActivity(), BrowserActivity.class);
        mainContentBrowser.putExtra("HEADER_TITLE", getResources().getString(R.string.menu_account_messages));
        mainContentBrowser.putExtra("URL_PATH", url);
        startActivityForResult(mainContentBrowser, BROWSERTAB_ACTIVITY);
    }

    @Override
    public void onClick(View v)
    {
//        if (v.getId() == R.id.CloseButton) {
//            if (dialog != null)
//                dialog.dismiss();
//        }
        if (v == loadMore_btn)
        {
            loadMoreMessages(v);
        }
        else if (v == submit_tv)
        {
            System.out.println("postComment");
            postComment(v);
        }
        else if (v == messages_btn)
        {
            messages_btn.setSelected(true);
            carnet_btn.setSelected(false);
            messagesContent.setVisibility(View.VISIBLE);
            carnetContent.setVisibility(View.GONE);
            //removeFragment();
        }
        else if(v == carnet_btn)
        {
            messages_btn.setSelected(false);
            carnet_btn.setSelected(true);
            loadCarnetFragment();
        }
        else if(v == backButton)
        {
            super.removeFragment();
        }
    }

    private void loadCarnetFragment()
    {
        FragmentManager fragmentManager = getFragmentManager();

        if (getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_DIETITIAN") != null) {
            fragmentManager.beginTransaction().remove(getFragmentManager().findFragmentByTag("CURRENT_FRAGMENT_IN_DIETITIAN")).commit();
        } else { }

        try {
            Fragment fragment = new CarnetAccountFragment();
            fragmentManager.beginTransaction().replace(R.id.carnetContent, fragment, "CURRENT_FRAGMENT_IN_DIETITIAN").commit();
            messagesContent.setVisibility(View.GONE);
            carnetContent.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showCustomDialog(String message) {
//
//        dialog = new CustomDialog(context, null, null, null, true, message, null, this);
//        dialog.show();
    }
}
