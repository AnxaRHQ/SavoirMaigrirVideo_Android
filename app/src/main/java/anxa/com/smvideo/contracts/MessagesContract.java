package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by aprilanxa on 22/03/2018.
 */

public class MessagesContract {
    @SerializedName("regId")
    public int RegId ;
    @SerializedName("title")
    public String Title;
    @SerializedName("messageChat")
    public String MessageChat;
    @SerializedName("coachId")
    public int CoachId ;
    @SerializedName("coachRegNo")
    public int CoachRegNo;
    @SerializedName("coachName")
    public String CoachName ;
    @SerializedName("isViewed")
    public boolean IsViewed ;
    @SerializedName("isDeleted")
    public boolean IsDeleted;
    @SerializedName("createdDate")
    public long DateCreated;
    @SerializedName("creditsUsed")
    public byte CreditsUsed;
    @SerializedName("category")
    public byte Category;
    @SerializedName("rating")
    public byte Rating;
    @SerializedName("ratingComment")
    public String RatingComment;
    @SerializedName("ratingDate")
    public long RatingDate;
    @SerializedName("mediaUrl")
    public String MediaUrl;
    @SerializedName("mediaType")
    public byte MediaType;
    @SerializedName("coachIdLiked")
    public int CoachIdLiked;
    @SerializedName("coachLikedName")
    public String CoachLikedName;
}
