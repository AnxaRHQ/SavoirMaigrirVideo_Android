package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

public class GetAlertsResponseContract extends BaseContract {
    public GetAlertsResponseContract(){
        Data = new GetAlertsDataContract();
    }


    @SerializedName("data")
    public GetAlertsDataContract Data;
}
