package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

public class CurrentBannerContract extends BaseContract {
    @SerializedName("bannerContent")
    public String BannerContent;

    @SerializedName("bannerLink")
    public String BannerLink;

    @SerializedName("Ctid")
    public int Ctid;

    @SerializedName("callToAction")
    public String CallToAction;
}
