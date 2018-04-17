package anxa.com.smvideo.contracts.Carnet;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angelaanxa on 1/10/2017.
 */

public class CommentGroupContract {
    public CommentGroupContract()
    {
        Comments = new ArrayList<MealCommentContract>();
    }

    @SerializedName("comment_count")
    public int Count;

    @SerializedName("comment")
    public List<MealCommentContract> Comments;
}
