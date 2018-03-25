package anxa.com.smvideo.activities.account;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.MainActivity;
import anxa.com.smvideo.common.SavoirMaigrirVideoConstants;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.MessagesContract;
import anxa.com.smvideo.contracts.MessagesResponseContract;
import anxa.com.smvideo.contracts.PostMessagesContract;
import anxa.com.smvideo.contracts.VideoContract;
import anxa.com.smvideo.contracts.VideoResponseContract;
import anxa.com.smvideo.ui.CustomListView;
import anxa.com.smvideo.ui.MessagesListLayout;
import anxa.com.smvideo.ui.VideoListAdapter;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.VideoHelper;

/**
 * Created by aprilanxa on 22/03/2018.
 */

public class MessagesAccountFragment extends Fragment implements View.OnClickListener {

    MessagesListLayout commentList;
    ProgressBar progressBar;
    TextView submit_tv;
    EditText comment_et;
    PostMessagesContract newPostMessageContract;
    //    CustomDialog dialog;
    Button loadMore_btn;
    RelativeLayout loadMore_layout;

    MessagesResponseContract response;

    List<MessagesContract> items;

    long previousDate;

    boolean loadPrevious = false;
    boolean loadBrowser = false;

    Dialog freeDialog;
    AlertDialog.Builder alertBuilder;
    private static final int BROWSERTAB_ACTIVITY = 1011;
    String intentExtra;
    private int allowedQuestionsToAsk = 0;

    private Context context;
    protected ApiCaller caller;
    View mView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.context = getActivity();
        mView = inflater.inflate(R.layout.messages_account, null);

        caller = new ApiCaller();

        //header change
        ((TextView) (mView.findViewById(R.id.header_title_tv))).setText(getString(R.string.menu_account_messages));
        ((TextView) (mView.findViewById(R.id.header_right_tv))).setVisibility(View.INVISIBLE);

        items = new ArrayList<>(ApplicationData.getInstance().messagesList);

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

        progressBar.setVisibility(View.VISIBLE);

        if (items != null && items.size() > 1) {
            sort(items);
        }
        commentList.initData(items, context, null);

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
        items.clear();

        ApplicationData.getInstance().setPreviousDate(AppUtil.getCurrentDateinLong());
        previousDate = AppUtil.getCurrentDateinLong();

        getPreviousQuestionsThread();
    }

    public void onPause() {
        super.onPause();
        context.getApplicationContext().unregisterReceiver(the_receiver);
    }

    public void postComment(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(comment_et.getWindowToken(), 0);

        // check if the text view has something
        if (comment_et != null && comment_et.getText() != null) {
            if (comment_et.getText().toString().trim().length() > 0) {

                progressBar.setVisibility(View.VISIBLE);

                newPostMessageContract.MessageChat = comment_et.getText().toString();
                newPostMessageContract.CreatedDate = AppUtil.getCurrentDateinLongGMT();
                newPostMessageContract.RegId = ApplicationData.getInstance().regId;

                postQuestionsThread();
            }
        }
    }

    public void loadMoreMessages(View view) {
        loadPrevious = true;
        startProgressBar();
        getPreviousQuestionsThread();
    }

    /**
     * Private Methods
     **/
    private void getLatestQuestionsThread() {

        caller.GetLatestMessagesThread(new AsyncResponse() {
                                           @Override
                                           public void processFinish(Object output) {
                                               MessagesContract questions;

                                               response = output != null ? (MessagesResponseContract) output : new MessagesResponseContract();

                                               if (response != null && response.Data != null && response.Data.Messages != null && response.Cursor != null) {
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
                                       }
                , ApplicationData.getInstance().userDataContract.Id, (int) (System.currentTimeMillis() / 1000L));
    }

    private void getPreviousQuestionsThread() {
        caller.GetPreviousMessagesThread(new AsyncResponse() {
                                             @Override
                                             public void processFinish(Object output) {

                                                 response = output != null ? (MessagesResponseContract) output : new MessagesResponseContract();
                                                 if (response != null && response.Data != null && response.Data.Messages != null && response.Cursor != null) {
                                                     ApplicationData.getInstance().setBeforeDate(response.Cursor.before);
                                                     ApplicationData.getInstance().setPreviousDate(response.Cursor.previous);
                                                     previousDate = response.Cursor.previous;

                                                     ApplicationData.getInstance().messagesResponseContract = response;
                                                     boolean firstIteration = false;
                                                     if (items.size() == 0) {
                                                         firstIteration = true;
                                                     }

                                                     items.addAll(response.Data.Messages);
                                                     sort(items);

                                                     commentList.updateData(items);

                                                     //save to database
//                                                      MessageDAO msgDao = new MessageDAO(context, null);
//                                                      DaoImplementer implDao = new DaoImplementer(msgDao, context);

                                                     MessagesContract message;
                                                     for (int i = 0; i < items.size(); i++) {
                                                         message = items.get(i);
                                                         if (message.CoachIdLiked > 0) {
                                                             String likeString = getResources().getString(R.string.messages_liked);
                                                             likeString = likeString.replace("%@", message.CoachLikedName);
                                                             message.MessageChat = message.MessageChat.replace("<br><br><i>" + likeString + "</i>", "");
                                                             message.MessageChat = message.MessageChat + "<br><br><i>" + likeString + "</i>";
                                                         }
//                                                          implDao.add(message);
                                                     }
                                                     if (firstIteration) {
                                                         if (items.size() > 1) {
                                                             if (items.get(items.size() - 1).CoachId > 0) {
                                                                 allowedQuestionsToAsk = 2;
                                                             }
                                                             if (items.get(items.size() - 2).CoachId > 0) {
                                                                 allowedQuestionsToAsk++;
                                                             }
                                                         } else if (items.size() == 1) {
                                                             if (items.get(items.size() - 1).CoachId > 0) {
                                                                 allowedQuestionsToAsk++;
                                                             }
                                                         } else if (items.size() == 0) {
                                                             allowedQuestionsToAsk = 2;
                                                         }

                                                         if (allowedQuestionsToAsk > 2) {
                                                             allowedQuestionsToAsk = 2;
                                                         }
                                                         if (allowedQuestionsToAsk == 0) {
                                                             ((TextView) mView.findViewById(R.id.messageLimit)).setVisibility(View.VISIBLE);
                                                             ((RelativeLayout) mView.findViewById(R.id.questionsAskContainer)).setVisibility(View.GONE);
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
                                                         loadMore_btn.setText(getResources().getString(R.string.messages_load_more));
                                                     }
                                                 }
                                             }
                                         }
                , (int) previousDate);
    }

    private void postQuestionsThread() {
        caller.PostMessage(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {

                //successful post of message
                if (output != null) {
                    //do whatever
                    MessagesContract newQuestionsContract = new MessagesContract();
                    newQuestionsContract.MessageChat = newPostMessageContract.MessageChat;
                    newQuestionsContract.CoachId = 0;
                    newQuestionsContract.DateCreated = AppUtil.getCreatedDateForDisplay(newPostMessageContract.CreatedDate);
                    newQuestionsContract.RegId = newPostMessageContract.RegId;

                    items.add(newQuestionsContract);

//                    DaoImplementer implDao = new DaoImplementer(new MessageDAO(context, null), context);
//                    implDao.add(newQuestionsContract);
                    sort(items);
                    progressBar.setVisibility(View.GONE);

                    ApplicationData.getInstance().messagesList.add(newQuestionsContract);

                    clearNewCommentUI();

                    allowedQuestionsToAsk--;
                    if (allowedQuestionsToAsk <= 0) {
                        ((TextView) mView.findViewById(R.id.messageLimit)).setVisibility(View.VISIBLE);
                        ((RelativeLayout) mView.findViewById(R.id.questionsAskContainer)).setVisibility(View.GONE);
                    }

                } else {

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

    private void hideLoadMoreButton() {
        loadMore_layout.setVisibility(View.GONE);
        loadMore_btn.setVisibility(View.GONE);
    }

    private void showLoadMoreButton() {
        loadMore_layout.setVisibility(View.VISIBLE);
        loadMore_btn.setVisibility(View.VISIBLE);
        loadMore_btn.setText(getResources().getString(R.string.messages_load_more));
    }

    private void updateUI(List<MessagesContract> updateItems) {
        sort(updateItems);
        commentList.updateData(updateItems);
    }

    private void clearNewCommentUI() {
        comment_et.setText("");
        updateUI(items);
    }

    private void sort(final List<MessagesContract> questionsItems) {
        if (questionsItems != null && questionsItems.size() > 0) {

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

    private void loadURL(String url) {
        Intent mainContentBrowser = new Intent(this.getActivity(), BrowserActivity.class);
        mainContentBrowser.putExtra("HEADER_TITLE", getResources().getString(R.string.menu_account_messages));
        mainContentBrowser.putExtra("URL_PATH", url);
        startActivityForResult(mainContentBrowser, BROWSERTAB_ACTIVITY);
    }

//    private void showFreeMessageDialog() {
//        ApplicationData.getInstance().shownFreeMessages = true;
//        freeDialog = new Dialog(this);
//        freeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        freeDialog.setContentView(R.layout.freedialog);
//        freeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        ((TextView)freeDialog.findViewById(R.id.freedialog_header)).setText(getString(R.string.QUESTIONS_DIALOG_HEADER));
//        ((TextView)freeDialog.findViewById(R.id.freedialog_content)).setText(getString(R.string.QUESTIONS_DIALOG_CONTENT));
//        ((ImageView)freeDialog.findViewById(R.id.freedialog_image)).setImageDrawable(getResources().getDrawable(R.drawable.sm_diet_intro_popup_img));
//        ((Button)freeDialog.findViewById(R.id.freedialog_button)).setText(getString(R.string.QUESTIONS_DIALOG_BUTTON));
//        ((Button)freeDialog.findViewById(R.id.freedialog_button)).setOnClickListener(new View.OnClickListener() {
//            public void onClick(View view) {
//                // Start settings activity
//                freeDialog.dismiss();
//            }
//        });
//        freeDialog.show();
//
//    }

    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.CloseButton) {
//            if (dialog != null)
//                dialog.dismiss();
//        }
        if (v == loadMore_btn) {
            loadMoreMessages(v);
        }else if (v == submit_tv){

            System.out.println("postComment");
            postComment(v);
        }
    }

    private void showCustomDialog(String message) {
//
//        dialog = new CustomDialog(context, null, null, null, true, message, null, this);
//        dialog.show();
    }
}
