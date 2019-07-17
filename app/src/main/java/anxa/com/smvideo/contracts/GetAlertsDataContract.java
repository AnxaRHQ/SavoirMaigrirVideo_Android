package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class GetAlertsDataContract {
    public GetAlertsDataContract(){
        Alerts = new ArrayList<AlertsContract>();
    }
    @SerializedName("alerts")
    public List<AlertsContract> Alerts;
}
