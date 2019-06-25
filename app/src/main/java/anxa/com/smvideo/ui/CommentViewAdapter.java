package anxa.com.smvideo.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import android.support.design.widget.BottomSheetDialog;

import java.io.InputStream;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.activities.MainActivity;
import anxa.com.smvideo.activities.account.MessageRatingReasonActivity;
import anxa.com.smvideo.activities.account.MessagesAccountFragment;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.connection.listener.MainActivityCallBack;
import anxa.com.smvideo.contracts.MessageRatingContract;
import anxa.com.smvideo.contracts.MessagesContract;
import anxa.com.smvideo.util.AppUtil;


public class CommentViewAdapter extends ArrayAdapter<MessagesContract> implements View.OnClickListener {

    private Context context;
    private List<MessagesContract> items;
    private LayoutInflater layoutInflater;
    private String inflater = Context.LAYOUT_INFLATER_SERVICE;
    private String currentDate;
    private MainActivityCallBack MainListener;

    private int coachId = 0;

    private static final int RATING_ACTIVITY = 150;

    private ApiCaller caller;

    public CommentViewAdapter(Context context,
                              List<MessagesContract> items,
                              MainActivityCallBack MainListener) {
        super(context, R.layout.comment_item, items);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;
        this.MainListener = MainListener;
    }

    public void update(List<MessagesContract> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View row = convertView;

        ViewHolder viewHolder = null;
        if (row == null) {
            LayoutInflater layoutInflator = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            row = layoutInflator.inflate(R.layout.comment_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.imageView = (RoundedImageView) row.findViewById(R.id.chat_avatar);
            viewHolder.chatMessage = ((TextView) row.findViewById(R.id.chat_message));
            viewHolder.chatMessage_user = ((TextView) row.findViewById(R.id.chat_message_user));
            viewHolder.chatDate = ((TextView) row.findViewById(R.id.date));
            viewHolder.chatTimestamp = ((TextView) row.findViewById(R.id.timestamp));
            viewHolder.chatLike = (ImageView) row.findViewById(R.id.chat_like);

            row.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) row.getTag();
        }

        if (items != null && items.size() > 0 && getCount() > 0) {
            MessagesContract message = items.get(position);

            //download profile image
            String proFileImageURL = AppUtil.BuildProfilePicUrl(context.getResources().getString(R.string.profile_pic_url), Integer.toString(ApplicationData.getInstance().regId));

            //download profile pic
//            new DownloadImageTask((ImageView) viewHolder.imageView)
//                    .execute(proFileImageURL);

            final ImageView viewHolder_iv = viewHolder.imageView;
            final String profileURL_img = proFileImageURL;
            final  Handler mHandler = new Handler(context.getMainLooper());
            mHandler.post(new Runnable() {
                int i=0;
                @Override
                public void run() {
                    new DownloadImageTask((ImageView) viewHolder_iv).execute(profileURL_img);
                    mHandler.postDelayed(this, 5000);
                }
            });

//            proFileImageURL = AppUtil.BuildProfilePicUrl(context.getResources().getString(R.string.profile_pic_url), Integer.toString(message.CoachId));

            Bitmap avatar = null;
            viewHolder.chatLike.setTag(message.Id);

            viewHolder.imageView.setTag(message.DateCreated);

            String date = AppUtil.getMonthDay(AppUtil.toDate(message.DateCreated));
            String time = AppUtil.getTimeOnly24(AppUtil.toDate(message.DateCreated).getTime());

            if (currentDate == null) {
                currentDate = date;
                row.findViewById(R.id.datecontainer).setVisibility(View.VISIBLE);
                viewHolder.chatDate.setText(date);

            } else if (currentDate.contentEquals(date)) {
                if (items.size() > 1 && position > 0) {
                    if (AppUtil.getMonthDay(AppUtil.toDate(items.get(position - 1).DateCreated)).equalsIgnoreCase(date)) {
                        row.findViewById(R.id.datecontainer).setVisibility(View.GONE);
                    } else {
                        row.findViewById(R.id.datecontainer).setVisibility(View.VISIBLE);
                        viewHolder.chatDate.setText(date);
                        currentDate = date;
                    }
                } else {
                    row.findViewById(R.id.datecontainer).setVisibility(View.GONE);
                }
            } else {
                row.findViewById(R.id.datecontainer).setVisibility(View.VISIBLE);
                viewHolder.chatDate.setText(date);
                currentDate = date;
            }

            coachId = message.CoachRegNo;
            viewHolder.imageView.setTag(coachId);


            if (message.CoachId > 0) { //coach comment use coach comment here

                viewHolder.chatMessage_user.setVisibility(View.GONE);
                viewHolder.chatMessage.setVisibility(View.VISIBLE);
                viewHolder.chatLike.setVisibility(View.VISIBLE);
                row.findViewById(R.id.chat_like).setTag(message.Id);

                if(message.Rating > 0)
                {
                    viewHolder.chatLike.setImageDrawable(context.getResources().getDrawable(R.drawable.like_orange));
                    viewHolder.chatLike.setClickable(false);

                }else{
                    viewHolder.chatLike.setImageDrawable(context.getResources().getDrawable(R.drawable.like_gray));
                    viewHolder.chatLike.setClickable(true);
                    viewHolder.chatLike.setOnClickListener(this);
                }

                avatar = getAvatar(message.CoachRegNo);
                proFileImageURL = AppUtil.BuildProfilePicUrl(context.getResources().getString(R.string.profile_pic_url), Integer.toString(message.CoachRegNo));

                LayoutParams params1 = (LayoutParams) (((RoundedImageView) row.findViewById(R.id.chat_avatar))).getLayoutParams();
                params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

                LayoutParams params2 = (LayoutParams) row.findViewById(R.id.timestamp).getLayoutParams();
                params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

                LayoutParams params3 = (LayoutParams) row.findViewById(R.id.chat_message_cont).getLayoutParams();
                params3.addRule(RelativeLayout.LEFT_OF, R.id.timestamp);
                params3.addRule(RelativeLayout.RIGHT_OF, R.id.chat_avatar);

                LayoutParams params4 = (LayoutParams) row.findViewById(R.id.chat_like).getLayoutParams();
                params4.addRule(RelativeLayout.BELOW, R.id.timestamp);
                params4.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
                params4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params4.setMargins(0,0,7,0);

                row.findViewById(R.id.chat_avatar).setLayoutParams(params1);
                row.findViewById(R.id.timestamp).setLayoutParams(params2);
                row.findViewById(R.id.chat_message_cont).setLayoutParams(params3);
                row.findViewById(R.id.chat_like).setLayoutParams(params4);


                row.findViewById(R.id.chat_message).setBackgroundResource(R.drawable.comment_bubble_coach);
                if (avatar == null) {
                    new AdapterDownloadImageTask(viewHolder.imageView).execute(proFileImageURL);
                } else {
                    viewHolder.imageView.setImageBitmap(avatar);
                }

                viewHolder.chatMessage.setText(Html.fromHtml(message.MessageChat));
                viewHolder.chatMessage.setLinksClickable(true);
//                viewHolder.chatMessage.setMovementMethod(MovementCheck.getInstance(context));

            } else {
                avatar = getAvatar(0);
                viewHolder.chatMessage.setVisibility(View.GONE);
                viewHolder.chatMessage_user.setVisibility(View.VISIBLE);
                viewHolder.chatLike.setVisibility(View.GONE);

                LayoutParams params1 = (LayoutParams) row.findViewById(R.id.chat_avatar).getLayoutParams();
                params1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);

                LayoutParams params2 = (LayoutParams) row.findViewById(R.id.timestamp).getLayoutParams();
                params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);

                LayoutParams params3 = (LayoutParams) row.findViewById(R.id.chat_message_cont).getLayoutParams();
                params3.addRule(RelativeLayout.RIGHT_OF, R.id.timestamp);
                params3.addRule(RelativeLayout.LEFT_OF, R.id.chat_avatar);


                LayoutParams params4 = (LayoutParams) row.findViewById(R.id.chat_like).getLayoutParams();
                params4.addRule(RelativeLayout.BELOW, R.id.timestamp);
                params4.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                params4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
                params4.setMargins(7,0,0,0);
                row.findViewById(R.id.chat_avatar).setLayoutParams(params1);
                row.findViewById(R.id.timestamp).setLayoutParams(params2);
                row.findViewById(R.id.chat_message_cont).setLayoutParams(params3);
//                row.findViewById(R.id.chat_like).setLayoutParams(params4);
                row.findViewById(R.id.chat_message_user).setBackgroundResource(R.drawable.comment_bubble_user_inverted);
                viewHolder.imageView.setImageBitmap(avatar);

                viewHolder.chatMessage_user.setText(Html.fromHtml(message.MessageChat));
                viewHolder.chatMessage_user.setLinksClickable(true);

                if(message.CoachIdLiked > 0)
                {
                    viewHolder.chatLike.setImageDrawable(context.getResources().getDrawable(R.drawable.like_orange));
                    viewHolder.chatLike.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.chatLike.setVisibility(View.GONE);
                }

            }

            viewHolder.chatTimestamp.setText(time);

        }
        return row;
    }

    @Override
    public int getCount() {
        return items.size();// more than zero
    }

    private class AdapterDownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView bmImage;
        private String path;

        public AdapterDownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
            this.path = bmImage.getTag().toString();
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            String coachIdToSave = urldisplay.substring(urldisplay.indexOf("users/") + 6, urldisplay.lastIndexOf("/"));

            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);

                if (!ApplicationData.getInstance().coachPhotosList.containsKey(coachIdToSave) && mIcon11 != null) {
                    ApplicationData.getInstance().coachPhotosList.put(coachIdToSave, mIcon11);
                }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            if (!bmImage.getTag().toString().equals(path)) {
                return;
            }

            if (result != null && bmImage != null) {
                bmImage.setVisibility(View.VISIBLE);
                bmImage.setImageBitmap(result);

            } else {
                bmImage.setVisibility(View.GONE);
            }
        }
    }

    private Bitmap getAvatar(int coachId) {
        Bitmap avatarBMP = null;
        if (coachId > 0) {
            avatarBMP = ApplicationData.getInstance().coachPhotosList.get(String.valueOf(coachId));

            return avatarBMP;

        } else { //user comment use his image instead
            if (ApplicationData.getInstance().getUserProfilePhoto() != null)
                avatarBMP = ApplicationData.getInstance().getUserProfilePhoto();
            else if (ApplicationData.getInstance().getUserProfilePhoto() == null)
                avatarBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_default_avatar, ApplicationData.getInstance().options_Avatar);

            if (avatarBMP == null)
                avatarBMP = BitmapFactory.decodeResource(context.getResources(), R.drawable.profile_default_avatar, ApplicationData.getInstance().options_Avatar);
        }

        return avatarBMP;

    }

    private static class ViewHolder {
        RoundedImageView imageView;
        TextView chatMessage;
        TextView chatMessage_user;
        TextView chatDate;
        TextView chatTimestamp;
        ImageView chatLike;

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.chat_like) {
            final View chatLikeView = v;
            final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(context);

            final View sheetView = ((MainActivity) context).getLayoutInflater().inflate(R.layout.coach_rating, null);
//            final View sheetView = ((MessagesAccountFragment) context).getLayoutInflater().inflate(R.layout.coach_rating, null);
            Button cancelButton = (Button) sheetView.findViewById(R.id.bottomModalCancel);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mBottomSheetDialog.dismiss();
                }
            });
            final ImageView image0 = (ImageView) sheetView.findViewById(R.id.ratingimage0);
            image0.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveRating(0, "", mBottomSheetDialog, chatLikeView, image0);

                }
            });
            final ImageView image1 = (ImageView) sheetView.findViewById(R.id.ratingimage1);
            image1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveRating(1, "", mBottomSheetDialog, chatLikeView, image1);
                }
            });
            final ImageView image2 = (ImageView) sheetView.findViewById(R.id.ratingimage2);
            image2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveRating(2, "", mBottomSheetDialog, chatLikeView, image2);
                }
            });
            final ImageView image3 = (ImageView) sheetView.findViewById(R.id.ratingimage3);
            image3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveRating(3, "", mBottomSheetDialog, chatLikeView, image3);
                }
            });
            final ImageView image4 = (ImageView) sheetView.findViewById(R.id.ratingimage4);
            image4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveRating(4, "", mBottomSheetDialog, chatLikeView, image4);
                }
            });
            final ImageView image5 = (ImageView) sheetView.findViewById(R.id.ratingimage5);
            image5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveRating(5, "", mBottomSheetDialog, chatLikeView, image5);
                }
            });
            final ImageView image6 = (ImageView) sheetView.findViewById(R.id.ratingimage6);
            image6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveRating(6, "", mBottomSheetDialog, chatLikeView, image6);
                }
            });
            final ImageView image7 = (ImageView) sheetView.findViewById(R.id.ratingimage7);
            image7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveRating(7, "", mBottomSheetDialog, chatLikeView, image7);
                }
            });
            final ImageView image8 = (ImageView) sheetView.findViewById(R.id.ratingimage8);
            image8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveRating(8, "", mBottomSheetDialog, chatLikeView, image8);
                }
            });
            final ImageView image9 = (ImageView) sheetView.findViewById(R.id.ratingimage9);
            image9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveRating(9, "", mBottomSheetDialog, chatLikeView, image9);
                }
            });
            final ImageView image10 = (ImageView) sheetView.findViewById(R.id.ratingimage10);
            image10.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SaveRating(10, "", mBottomSheetDialog, chatLikeView, image10);
                }
            });
            mBottomSheetDialog.setContentView(sheetView);
            mBottomSheetDialog.show();

        }
    }


    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        final ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urlDisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urlDisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }

            ApplicationData.getInstance().setUserProfilePhoto(mIcon11);

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void SaveRating(final int rating, String reason, final BottomSheetDialog dialog, final View v, ImageView img) {
        img.setImageDrawable(context.getResources().getDrawable(R.drawable.rating_button_orange));
        MessageRatingContract contract = new MessageRatingContract();
        contract.QuestionId = Integer.parseInt(v.getTag().toString());
        contract.Rating = rating;
        caller = new ApiCaller();

        System.out.println("onclick messageID: " + v.getTag());


        caller.PostRating(new AsyncResponse() {
            @Override
            public void processFinish(Object output) {
                ((ImageView) v).setImageDrawable(context.getResources().getDrawable(R.drawable.like_orange));
                v.setOnClickListener(null);
            }
        }, Integer.toString(ApplicationData.getInstance().userDataContract.Id), contract);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);


        // set dialog message
        alertDialogBuilder
                .setMessage(context.getResources().getString(R.string.message_rating_thankyou))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();


                    }
                });
        // create alert dialog
        final AlertDialog alertDialog = alertDialogBuilder.create();

        if (rating <= 5) {
            Intent intent = new Intent(context, MessageRatingReasonActivity.class);
            intent.putExtra("QUESTIONID", Integer.parseInt(v.getTag().toString()));
            ((MainActivity)getContext()).startActivityForResult(intent, 2);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    alertDialog.show();
                }
            }, 2000);

        } else {
            alertDialog.show();

        }
        dialog.dismiss();
    }
}