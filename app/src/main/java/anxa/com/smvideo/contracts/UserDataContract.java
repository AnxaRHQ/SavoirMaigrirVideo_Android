package anxa.com.smvideo.contracts;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by aprilanxa on 29/06/2017.
 */

public class UserDataContract extends BaseContract
{
    @SerializedName("Id")
    public int Id;

    @SerializedName("Email")
    public String Email;

    @SerializedName("EmailConfirmed")
    public boolean EmailConfirmed;

    @SerializedName("PasswordHash")
    public String PasswordHash;

    @SerializedName("Phone")
    public String Phone;

    @SerializedName("PhoneConfirmed")
    public boolean PhoneConfirmed;

    @SerializedName("UserName")
    public String userName;

    @SerializedName("FirstName")
    public String FirstName;

    @SerializedName("MiddleName")
    public String MiddleName;

    @SerializedName("LastName")
    public String LastName;

    @SerializedName("PictureUrl")
    public String PictureUrl;

    @SerializedName("MembershipType")
    public String MembershipType;

    @SerializedName("AlertType")
    public String AlertType;

    @SerializedName("AlertContact")
    public String AlertContact;

    @SerializedName("PayoutCompleted")
    public boolean PayoutCompleted;

    @SerializedName("Activities")
    public List<ActivitiesDataContract> Activities;

    @SerializedName("DietProfiles")
    public List<DietProfilesDataContract> DietProfiles;

    @SerializedName("IsBlackOut")
    public boolean IsBlackOut;

    @SerializedName("AjRegNo")
    public int AjRegNo;

    @SerializedName("WeekNumber")
    public int WeekNumber;

    @SerializedName("DayNumber")
    public int DayNumber;

    @SerializedName("IsAnyVip")
    public boolean IsAnyVip;

    @SerializedName("SubscriptionType")
    public byte SubscriptionType;

    @SerializedName("DateRegistered")
    public String DateRegistered;

    @SerializedName("IsVip")
    public boolean IsVip;

    @SerializedName("Deleted")
    public boolean deleted;

    @SerializedName("ModifiedDate")
    public String modifiedDate;

    @SerializedName("CreatedDate")
    public String createdDate;
}
