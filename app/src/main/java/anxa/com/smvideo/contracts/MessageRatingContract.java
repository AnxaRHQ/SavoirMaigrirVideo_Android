package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

/**
 * Created by angelaanxa on 3/28/2018.
 */

public class MessageRatingContract extends BaseContract {
    @SerializedName("questionId")
    public int QuestionId;

    @SerializedName("rating")
    public Integer Rating;

    @SerializedName("ratingComment")
    public String RatingComment;
}
