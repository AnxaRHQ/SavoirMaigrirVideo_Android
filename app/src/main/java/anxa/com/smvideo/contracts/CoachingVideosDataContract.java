package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by aprilanxa on 29/06/2017.
 */

public class CoachingVideosDataContract {
    @SerializedName("videos")
    public List<CoachingVideosContract> Videos;
}
