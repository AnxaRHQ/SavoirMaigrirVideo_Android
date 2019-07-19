package anxa.com.smvideo.contracts.Graph;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import anxa.com.smvideo.contracts.BaseContract;

public class GetStepDataResponseContract extends BaseContract
{
    @SerializedName("data")
    public List<StepDataContract> Data;
}
