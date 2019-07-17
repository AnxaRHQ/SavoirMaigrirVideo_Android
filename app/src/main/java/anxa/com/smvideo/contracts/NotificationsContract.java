package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

public class NotificationsContract extends BaseContract {
    /**<notification_id>1167231</notification_id>
     <tooltype_id>OneToOneCoaching</tooltype_id>
     <toolaction_id>Commented</toolaction_id>
     <tool_id>6839973</tool_id>
     <toolextra_id1>2</toolextra_id1>
     <tool_content_title>Déjeuner du dimanche 12 mars 2017</tool_content_title>
     <is_read>false</is_read>
     <is_deleted>false</is_deleted>
     <date_created_utc>1489587480</date_created_utc>
     <display_date>1489325400</display_date>
     <notification_text>
     Delphine a commenté votre "Déjeuner" du 12-3-2017
     </notification_text>
     <coach_profile_picture>http://img1.aujourdhui.com/users/305139/small.jpg</coach_profile_picture>
     <ErrorCount>0</ErrorCount>
     </MobileNotificationContract>**/
    @SerializedName("notification_id")
    public int notification_id ;
    @SerializedName("tooltype_id")
    public String tooltype_id;
    @SerializedName("toolaction_id")
    public String toolaction_id;
    @SerializedName("tool_id")
    public int tool_id ;
    @SerializedName("toolextra_id1")
    public int toolextra_id1;
    @SerializedName("tool_content_title")
    public String tool_content_title ;
    @SerializedName("is_read")
    public boolean is_read ;
    @SerializedName("is_deleted")
    public boolean is_deleted;
    @SerializedName("date_created_utc")
    public long date_created_utc;
    @SerializedName("display_date")
    public long display_date;
    @SerializedName("notification_text")
    public String notification_text;
    @SerializedName("coach_profile_picture")
    public String coach_profile_picture;
    @SerializedName("ErrorCount")
    public String ErrorCount;
}
