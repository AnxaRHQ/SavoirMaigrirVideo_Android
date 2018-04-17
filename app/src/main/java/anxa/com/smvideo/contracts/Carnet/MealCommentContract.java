package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

import anxa.com.smvideo.contracts.BaseContract;

/**
 * Created by angelaanxa on 1/10/2017.
 */

public class MealCommentContract extends BaseContract {
    @SerializedName("comment_id")
    public int Id;

    @SerializedName("comment_type")
    public int Type;

    @SerializedName("comment_message")
    public String Text;

    @SerializedName("ishapi")
    public Boolean IsLiked;

    @SerializedName("comment_timestamp_utc")
    public long Timestamp;

    @SerializedName("coach")
    public CoachProfileContract Coach;
}
