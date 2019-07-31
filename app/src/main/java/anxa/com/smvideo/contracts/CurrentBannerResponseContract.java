package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

public class CurrentBannerResponseContract extends BaseContract {
    @SerializedName("data")
    public CurrentBannerContract Banner;
}
