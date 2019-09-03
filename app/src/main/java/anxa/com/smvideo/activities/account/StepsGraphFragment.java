package anxa.com.smvideo.activities.account;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.connection.ApiCaller;
import anxa.com.smvideo.connection.http.AsyncResponse;
import anxa.com.smvideo.contracts.Graph.GetStepDataResponseContract;
import anxa.com.smvideo.contracts.Graph.StepDataContract;
import anxa.com.smvideo.ui.StepsLogsListAdapter;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.StepsUtil;

/**
 * Created by elaineanxa on 18/07/2019
 */

public class StepsGraphFragment extends BaseFragment implements View.OnClickListener, View.OnKeyListener, OnChartGestureListener
{
    private Context context;
    protected ApiCaller caller;

    private GetStepDataResponseContract response;
    private BarChart stepsBarChart;
    private List<StepDataContract> stepsLogsListCurrentDisplay = new ArrayList<>();
    private List<StepDataContract> stepsLogsList = new ArrayList<>();
    private ListView stepsLogsListView;
    private List<StepDataContract> stepsGraphDataArrayList = new ArrayList<>();
    private StepsLogsListAdapter stepsLogsListAdapter;

    private ScrollView steps_graph_scrollview;
    private Button steps_1w;
    private Button steps_1m;
    private Button steps_3m;
    private Button steps_1y;
    private TextView steps_dateRange_tv;
    private Button steps_date_left_btn;
    private Button steps_date_right_btn;
    private Button steps_submitBtn;

    private EditText steps_enter_et;

    private final int DATE_RANGE_1M = 0;
    private final int DATE_RANGE_3M = 1;
    private final int DATE_RANGE_1Y = 2;
    private final int DATE_RANGE_1W = 4;
    private int dateRangeIndex = 0;
    private int selectedDateRange;

    private double heighestSteps = 0.0;
    private double lowestSteps = 0.0;
    private boolean fromSubmitStep = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.context = getActivity();

        mView = inflater.inflate(R.layout.steps_graph, null);

        caller = new ApiCaller();

        IntentFilter filter = new IntentFilter();
        filter.addAction(this.getResources().getString(R.string.STEPS_GRAPH_BROADCAST_DELETE));
        filter.addAction(this.getResources().getString(R.string.STEPS_GRAPH_BROADCAST_EDIT));

        context.getApplicationContext().registerReceiver(the_receiver, filter);

        steps_graph_scrollview = (ScrollView) mView.findViewById(R.id.steps_graph_scrollview);

        stepsLogsListView = (ListView) mView.findViewById(R.id.steps_graph_listView);

        stepsBarChart = (BarChart) mView.findViewById(R.id.viewcontentBarGraph);

        steps_1w = (Button) mView.findViewById(R.id.steps_1w_btn);
        steps_1m = (Button) mView.findViewById(R.id.steps_1m_btn);
        steps_3m = (Button) mView.findViewById(R.id.steps_3m_btn);
        steps_1y = (Button) mView.findViewById(R.id.steps_1y_btn);

        steps_dateRange_tv = (TextView) mView.findViewById(R.id.steps_date_range);
        steps_date_left_btn = (Button) mView.findViewById(R.id.steps_date_left_btn);
        steps_date_right_btn = (Button) mView.findViewById(R.id.steps_date_right_btn);

        steps_date_left_btn.setOnClickListener(this);
        steps_date_right_btn.setOnClickListener(this);
        steps_1w.setOnClickListener(this);
        steps_1m.setOnClickListener(this);
        steps_3m.setOnClickListener(this);
        steps_1y.setOnClickListener(this);

        ((ProgressBar) mView.findViewById(R.id.steps_graph_seemore_progressBar)).setVisibility(View.GONE);

        steps_submitBtn = (Button) mView.findViewById(R.id.steps_submitButton);
        steps_submitBtn.setOnClickListener(this);

        ((Button) mView.findViewById(R.id.steps_graph_seeMoreButton)).setOnClickListener(this);
        ((Button) mView.findViewById(R.id.steps_submitButton)).setOnClickListener(this);

        steps_enter_et = (EditText) mView.findViewById(R.id.steps_enter_et);
        steps_enter_et.setOnKeyListener(this);
        ((ProgressBar) mView.findViewById(R.id.steps_graph_progressBar)).setVisibility(View.VISIBLE);

        getLatestStepsData();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private final BroadcastReceiver the_receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equalsIgnoreCase(context.getString(R.string.progress_refresh))) {
                fromSubmitStep = false;
            } else if (intent.getAction().equalsIgnoreCase(context.getString(R.string.STEPS_GRAPH_BROADCAST_DELETE))) {
                fromSubmitStep = true;
                deleteStepsData(ApplicationData.getInstance().currentSteps);
            } else if (intent.getAction().equalsIgnoreCase(context.getString(R.string.STEPS_GRAPH_BROADCAST_EDIT))) {
                fromSubmitStep = false;
                updateStepsData(ApplicationData.getInstance().currentSteps);
            }
        }
    };

    private void postStepsData()
    {
        try {
            int stepsInput_int = Integer.parseInt(((EditText) mView.findViewById(R.id.steps_enter_et)).getText().toString());

            this.getActivity().runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    (mView.findViewById(R.id.steps_graph_progressBar)).setVisibility(View.VISIBLE);
                    steps_submitBtn.setEnabled(false);
                    dismissKeyboard();
                    steps_enter_et.setText("");
                }
            });

            fromSubmitStep = true;

            StepDataContract contract = new StepDataContract();
            contract.RegNo = ApplicationData.getInstance().userDataContract.AjRegNo; //ajregno of user
            contract.StepDate = AppUtil.getDateStringGetSync(AppUtil.getCurrentDateinDate());
            contract.Steps = stepsInput_int;

            caller.PostAddSteps(new AsyncResponse()
            {
                @Override
                public void processFinish(Object output) {
                    Log.d("PostAddSteps", output.toString());

                    ((EditText) mView.findViewById(R.id.steps_enter_et)).setText("");
                    getLatestStepsData();
                    steps_submitBtn.setEnabled(true);
                }
            }, ApplicationData.getInstance().userDataContract.Id, "Manual", contract);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            ((EditText) mView.findViewById(R.id.steps_enter_et)).setText("");
        }
    }

    private void deleteStepsData(StepDataContract stepsToDelete)
    {
        caller.PostDeleteSteps(new AsyncResponse()
        {
            @Override
            public void processFinish(Object output)
            {
                Log.d("PostDeleteSteps", output.toString());
                getLatestStepsData();
            }
        }, ApplicationData.getInstance().userDataContract.Id, stepsToDelete);
    }

    private void updateStepsData(StepDataContract stepsToUpdate)
    {
        caller.PostEditSteps(new AsyncResponse()
        {
            @Override
            public void processFinish(Object output)
            {
                Log.d("PostEditSteps", output.toString());
                dismissKeyboard();
                getLatestStepsData();
            }
        }, ApplicationData.getInstance().userDataContract.Id, stepsToUpdate);
    }

    private void getLatestStepsData()
    {
        // 2 years
        Date fromDate = new Date();
        Calendar cal = Calendar.getInstance();
        Date today = AppUtil.getCurrentDateinDate();
        cal.add(Calendar.YEAR, -5); // to get previous year add -5
        fromDate = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try
        {
            caller.GetAllSteps(new AsyncResponse()
            {
                @Override
                public void processFinish(Object output)
                {
                    ((ProgressBar) mView.findViewById(R.id.steps_graph_progressBar)).setVisibility(View.GONE);

                    response = output != null ? (GetStepDataResponseContract) output : new GetStepDataResponseContract();

                    if (response != null & !response.Message.equalsIgnoreCase("Failed"))
                    {
                        ApplicationData.getInstance().stepsList = response.Data;
                        populateStepsDataFromAPI(response);
                    }
                }
            }, ApplicationData.getInstance().userDataContract.Id, AppUtil.getDateStringGetSync(fromDate), AppUtil.getDateStringGetSync(AppUtil.getCurrentDateinDate()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateStepsDataFromAPI(GetStepDataResponseContract response)
    {
        /**
         //save to database
         StepsDAO stepsDao = new StepsDAO(this, null);
         DaoImplementer implDao = new DaoImplementer(stepsDao, this);

         for (StepDataContract stepDataContract: response.Data){
         implDao.addStep(stepDataContract);
         }
         System.out.println("steps DB: " + stepsDao.getAllSteps().size());
         **/

        //sort stepsdata
        sortStepsData(response.Data);

        stepsLogsList = StepsUtil.getStepsLogsList();

        if (response.Data.size()>0)
        {
            StepDataContract latestSteps = response.Data.get(0);
            String currenStepsString = getResources().getString(R.string.LAST_RECORDED_STEPS) + " " + latestSteps.StepDate;
            final String stringToDisplay = currenStepsString.replace("%d", Integer.toString(latestSteps.Steps));
            ((TextView) mView.findViewById(R.id.currentSteps_tv)).setText(stringToDisplay);

            if (response.Data.size() < 10) {
                mView.findViewById(R.id.steps_graph_date_ll).setVisibility(View.GONE);
            } else {
                mView.findViewById(R.id.steps_graph_date_ll).setVisibility(View.VISIBLE);
                steps_1w.setSelected(true);
                steps_1m.setSelected(false);
                steps_3m.setSelected(false);
                steps_1y.setSelected(false);

                steps_dateRange_tv.setText(AppUtil.get1WDateRangeDisplay(true, true));
            }

            populateStepsLogs();
            selectedDateRange = DATE_RANGE_1W;

            stepsGraphDataArrayList = StepsUtil.get1WStepsList(true);
            updateStepsGraph(selectedDateRange);
        }
    }

    private void populateStepsLogs()
    {
        System.out.println("getStepsLogpopulateStepsLogssList: " + stepsLogsList.size());

        if (fromSubmitStep)
        {
            stepsLogsListCurrentDisplay.clear();
            selectedDateRange = DATE_RANGE_1W;
            steps_1w.setSelected(true);
            steps_1m.setSelected(false);
            steps_3m.setSelected(false);
            steps_1y.setSelected(false);

            steps_dateRange_tv.setText(AppUtil.get1WDateRangeDisplay(true, true));
            dateRangeIndex = 0;
            steps_date_right_btn.setTextColor(getResources().getColor(R.color.text_darkgray));

            updateStepsGraph(DATE_RANGE_1W);
        }

        sortStepsData(stepsLogsList);

        if (stepsLogsList.size() > 7)
        {
            ((LinearLayout) mView.findViewById(R.id.steps_graph_seeMoreLayout)).setVisibility(View.VISIBLE);

            for (int i = 0; i < 7; i++)
            {
                stepsLogsListCurrentDisplay.add(stepsLogsList.get(i));
            }
        }
        else
        {
            ((LinearLayout) mView.findViewById(R.id.steps_graph_seeMoreLayout)).setVisibility(View.GONE);
            stepsLogsListCurrentDisplay = stepsLogsList;
        }

        if (stepsLogsListView.getAdapter() == null)
        {
            stepsLogsListAdapter = new StepsLogsListAdapter(this.context, stepsLogsListCurrentDisplay);
            stepsLogsListAdapter.notifyDataSetChanged();
            stepsLogsListView.invalidateViews();
            stepsLogsListView.refreshDrawableState();
            stepsLogsListView.setAdapter(stepsLogsListAdapter);
        }
        else
        {
            stepsLogsListAdapter.updateItems(stepsLogsListCurrentDisplay);
        }

        AppUtil.setListViewHeightBasedOnChildren(stepsLogsListView);

        stepsLogsListView.setFocusable(false);
        steps_graph_scrollview.smoothScrollTo(0, 0);
    }

    public void updateStepsGraph(int selectedDateRange_)
    {
        switch (selectedDateRange_)
        {
            case DATE_RANGE_1M:
                stepsGraphDataArrayList = StepsUtil.get1MStepsList(true, 0);
                break;
            case DATE_RANGE_3M:
                stepsGraphDataArrayList = StepsUtil.get3MStepsList(true);
                break;
            case DATE_RANGE_1Y:
                stepsGraphDataArrayList = StepsUtil.get1YStepsList(true, 0);
                break;
            case DATE_RANGE_1W:
                stepsGraphDataArrayList = StepsUtil.get1WStepsList(true);
                break;
            default:
                break;
        }

        populateStepsData();

        createStepsGraph(stepsGraphDataArrayList);
    }

    private void populateStepsData()
    {
        heighestSteps = StepsUtil.getHeighestSteps(stepsGraphDataArrayList);
        lowestSteps = StepsUtil.getLowestSteps(stepsGraphDataArrayList);

        if (selectedDateRange == DATE_RANGE_1W)
        {
            ((TextView) mView.findViewById(R.id.calories_burned_value_tv)).setText(String.format("%.0f", StepsUtil.getTotalCalories(stepsGraphDataArrayList)));
            ((TextView) mView.findViewById(R.id.km_travelled_value_tv)).setText(String.format("%.1f", StepsUtil.getTotalKmTravelled(stepsGraphDataArrayList)));

            DecimalFormat formatter = new DecimalFormat("###,###,###");
            String stepsString = formatter.format(StepsUtil.getTotalSteps(stepsGraphDataArrayList));

            ((TextView) mView.findViewById(R.id.steps_taken_value_tv)).setText(stepsString);
        }
        else
        {
            ((TextView) mView.findViewById(R.id.calories_burned_value_tv)).setText(String.format("%.0f", ApplicationData.getInstance().currentTotalCalories));
            ((TextView) mView.findViewById(R.id.km_travelled_value_tv)).setText(String.format("%.1f", ApplicationData.getInstance().currentKmTravelled));

            DecimalFormat formatter = new DecimalFormat("###,###,###");
            String stepsString = formatter.format(ApplicationData.getInstance().currentTotalSteps);

            ((TextView) mView.findViewById(R.id.steps_taken_value_tv)).setText(stepsString);
        }

        createStepsGraph(stepsGraphDataArrayList);
    }

    private void createStepsGraph(List<StepDataContract> stepsGraphList)
    {
        stepsBarChart.setOnChartGestureListener(this);
        stepsBarChart.setDrawGridBackground(false);

        // no description text
        stepsBarChart.setDescription("");
        stepsBarChart.setNoDataTextDescription(getResources().getString(R.string.NO_DATA));

        // enable value highlighting
        //stepsBarChart.setHighlightEnabled(true);

        // enable touch gestures
        stepsBarChart.setTouchEnabled(false);

        // enable scaling and dragging
        stepsBarChart.setDragEnabled(true);
        stepsBarChart.setScaleEnabled(true);
        stepsBarChart.setScaleXEnabled(false);
        stepsBarChart.setScaleYEnabled(true);
        stepsBarChart.getLegend().setEnabled(false);
        stepsBarChart.setDrawBorders(true);
        stepsBarChart.setBorderWidth(0.5f);
        stepsBarChart.setBorderColor(Color.LTGRAY);

        // if disabled, scaling can be done on x- and y-axis separately
        stepsBarChart.setPinchZoom(true);

        XAxis xAxis = stepsBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.GRAY);
        xAxis.setGridColor(Color.LTGRAY);
        xAxis.setDrawGridLines(true);
        xAxis.setGridLineWidth(0.6f);
        xAxis.setAxisLineColor(Color.LTGRAY);
        xAxis.setSpaceBetweenLabels(2);

        YAxis rightAxis = stepsBarChart.getAxisRight();
        rightAxis.setEnabled(false);

        YAxis leftAxis = stepsBarChart.getAxisLeft();
        leftAxis.setEnabled(true);
        leftAxis.setDrawAxisLine(true);
        //set grid color to light gray - 10/20/15
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setGridLineWidth(0.5f);
        leftAxis.setLabelCount(5, true);
        leftAxis.setAxisMaxValue((float) heighestSteps + 1000);

        if (lowestSteps - 1000 > 0) {
            leftAxis.setAxisMinValue((float) lowestSteps - 1000);
        } else {
            leftAxis.setAxisMinValue(0f);
        }

        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setStartAtZero(false);
        stepsDataToGraphArray(stepsGraphList);
        stepsBarChart.setBackgroundColor(Color.WHITE);

        stepsBarChart.getAxisLeft().setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float v, YAxis yAxis) {
                return StepsUtil.format((double) v, 0);
            }
        });

        stepsBarChart.invalidate();
    }

    private void stepsDataToGraphArray(List<StepDataContract> stepsList)
    {
        List<String> dayArray = new ArrayList<>();
        ArrayList<BarEntry> valsList = new ArrayList<>();

        for (int i = 0; i < stepsList.size(); i++)
        {
            float steps = ((float) (stepsList.get(i).Steps));
            BarEntry c1e1 = new BarEntry(steps, i);
            valsList.add(c1e1);

            String dayData = "";

            if (selectedDateRange == DATE_RANGE_1W || selectedDateRange == DATE_RANGE_1M) {
                dayData = new SimpleDateFormat("MMM dd").format(AppUtil.stringToDateWeight(stepsList.get(i).StepDate));
            } else if (selectedDateRange == DATE_RANGE_1Y || selectedDateRange == DATE_RANGE_3M) {
                dayData = new SimpleDateFormat("MMM").format(AppUtil.stringToDateWeight(stepsList.get(i).StepDate));
            } else {
                dayData = new SimpleDateFormat("MMM dd").format(AppUtil.stringToDateWeight(stepsList.get(i).StepDate));
            }

            dayArray.add(dayData);
        }

        BarDataSet dataSet1 = new BarDataSet(valsList, "Data 2");
        dataSet1.setDrawValues(false);
        dataSet1.setBarSpacePercent(50f);
        dataSet1.setColor(Color.parseColor("#4CB6EB"));
        dataSet1.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData data = new BarData(dayArray, dataSet1);
        stepsBarChart.setData(data);
        stepsBarChart.animateY(4000);
    }

    private void sortStepsData(List<StepDataContract> response)
    {
        Collections.sort(response, new Comparator<StepDataContract>()
        {
            public int compare(StepDataContract o1, StepDataContract o2)
            {
                return o2.StepDate.compareTo(o1.StepDate);
            }
        });
    }

    private void seeMoreStepsLogs()
    {
        Collections.sort(stepsLogsList, new Comparator<StepDataContract>() {
            public int compare(StepDataContract o1, StepDataContract o2) {
                return o2.StepDate.compareTo(o1.StepDate);
            }
        });

        int stepsLogsSize = stepsLogsListCurrentDisplay.size();

        if (stepsLogsList.size() >= stepsLogsSize + 7) {
            (mView.findViewById(R.id.steps_graph_seeMoreButton)).setVisibility(View.VISIBLE);
            for (int i=0; i<7; i++){
                stepsLogsListCurrentDisplay.add(stepsLogsList.get(i + stepsLogsSize));
            }
        } else {
            stepsLogsListCurrentDisplay = stepsLogsList;
            (mView.findViewById(R.id.steps_graph_seeMoreButton)).setVisibility(View.GONE);
        }

        if (stepsLogsListView.getAdapter() == null)
        {
            stepsLogsListAdapter = new StepsLogsListAdapter(this.context, stepsLogsListCurrentDisplay);
            stepsLogsListAdapter.notifyDataSetChanged();
            stepsLogsListView.invalidateViews();
            stepsLogsListView.refreshDrawableState();
            stepsLogsListView.setAdapter(stepsLogsListAdapter);
        }
        else
        {
            stepsLogsListAdapter.updateItems(stepsLogsListCurrentDisplay);
        }

        ((ProgressBar) mView.findViewById(R.id.steps_graph_seemore_progressBar)).setVisibility(View.GONE);

        AppUtil.setListViewHeightBasedOnChildren(stepsLogsListView);
    }

    private void dismissKeyboard()
    {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow((mView.findViewById(R.id.steps_enter_et)).getWindowToken(), 0);
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.steps_graph_seeMoreButton)
        {
            ((ProgressBar) mView.findViewById(R.id.steps_graph_seemore_progressBar)).setVisibility(View.VISIBLE);
            seeMoreStepsLogs();
        }
        else if (v.getId() == R.id.steps_submitButton)
        {
            postStepsData();
        }
        else if (v.getId() == R.id.steps_1w_btn)
        {
            selectedDateRange = DATE_RANGE_1W;
            steps_1w.setSelected(true);
            steps_1m.setSelected(false);
            steps_3m.setSelected(false);
            steps_1y.setSelected(false);
            steps_dateRange_tv.setText(AppUtil.get1WDateRangeDisplay(true, true));
            dateRangeIndex = 0;
            steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
            updateStepsGraph(DATE_RANGE_1W);
        }
        else if (v.getId() == R.id.steps_1m_btn)
        {
            selectedDateRange = DATE_RANGE_1M;
            steps_1w.setSelected(false);
            steps_1m.setSelected(true);
            steps_3m.setSelected(false);
            steps_1y.setSelected(false);
            steps_dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(true, true, dateRangeIndex));
            dateRangeIndex = 0;
            steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
            updateStepsGraph(DATE_RANGE_1M);
        }
        else if (v.getId() == R.id.steps_3m_btn)
        {
            selectedDateRange = DATE_RANGE_3M;
            steps_1w.setSelected(false);
            steps_1m.setSelected(false);
            steps_3m.setSelected(true);
            steps_1y.setSelected(false);
            steps_dateRange_tv.setText(AppUtil.get3MDateRangeDisplay(true, true, dateRangeIndex));
            dateRangeIndex = 0;
            steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
            updateStepsGraph(DATE_RANGE_3M);
        }
        else if (v.getId() == R.id.steps_1y_btn)
        {
            selectedDateRange = DATE_RANGE_1Y;
            steps_1w.setSelected(false);
            steps_1m.setSelected(false);
            steps_3m.setSelected(false);
            steps_1y.setSelected(true);
            steps_dateRange_tv.setText(AppUtil.get1YDateRangeDisplay(true, false));
            dateRangeIndex = 0;
            steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
            updateStepsGraph(DATE_RANGE_1Y);
        }
        else if (v == steps_date_left_btn)
        {
            if (selectedDateRange == DATE_RANGE_1M)
            {
                dateRangeIndex++;
                steps_dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(false, true, dateRangeIndex));
                stepsGraphDataArrayList = StepsUtil.get1MStepsList(false, dateRangeIndex);

                populateStepsData();
            }
            else if (selectedDateRange == DATE_RANGE_1W)
            {
                dateRangeIndex++;
                steps_dateRange_tv.setText(AppUtil.get1WDateRangeDisplay(false, true));
                stepsGraphDataArrayList = StepsUtil.get1WStepsList(false);

                populateStepsData();
            }
            else if (selectedDateRange == DATE_RANGE_3M)
            {
                dateRangeIndex++;
                steps_dateRange_tv.setText(AppUtil.get3MDateRangeDisplay(false, true, dateRangeIndex));
                stepsGraphDataArrayList = StepsUtil.get3MStepsList(false);

                populateStepsData();
            }
            else if (selectedDateRange == DATE_RANGE_1Y)
            {
                dateRangeIndex++;
                steps_dateRange_tv.setText(AppUtil.get1YDateRangeDisplay(false, true));
                stepsGraphDataArrayList = StepsUtil.get1YStepsList(false, dateRangeIndex);

                populateStepsData();
            }
            steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_orangedark));
        }
        else if (v == steps_date_right_btn)
        {
            if (selectedDateRange == DATE_RANGE_1W)
            {
                if (dateRangeIndex > 0)
                {
                    dateRangeIndex--;
                    steps_dateRange_tv.setText(AppUtil.get1WDateRangeDisplay(false, false));
                    stepsGraphDataArrayList = StepsUtil.get1WStepsList(false);

                    populateStepsData();

                    if (dateRangeIndex <= 0)
                    {
                        steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
                    }
                }
                else
                {
                    steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
                }
            }
            else if (selectedDateRange == DATE_RANGE_1M)
            {
                if (dateRangeIndex > 0)
                {
                    dateRangeIndex--;
                    steps_dateRange_tv.setText(AppUtil.get1MDateRangeDisplay(false, false, dateRangeIndex));
                    stepsGraphDataArrayList = StepsUtil.get1MStepsList(false, dateRangeIndex);

                    populateStepsData();

                    if (dateRangeIndex <= 0)
                    {
                        steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
                    }

                }
                else
                {
                    steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
                }
            }
            else if (selectedDateRange == DATE_RANGE_3M)
            {
                if (dateRangeIndex > 0)
                {
                    dateRangeIndex--;
                    steps_dateRange_tv.setText(AppUtil.get3MDateRangeDisplay(false, false, dateRangeIndex));
                    stepsGraphDataArrayList = StepsUtil.get3MStepsList(false);

                    populateStepsData();

                    if (dateRangeIndex <= 0)
                    {
                        steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
                    }

                }
                else
                {
                    steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
                }
            }
            else if (selectedDateRange == DATE_RANGE_1Y)
            {
                if (dateRangeIndex > 0)
                {
                    dateRangeIndex--;
                    steps_dateRange_tv.setText(AppUtil.get1YDateRangeDisplay(false, false));
                    stepsGraphDataArrayList = StepsUtil.get1YStepsList(false, dateRangeIndex);

                    populateStepsData();

                    if (dateRangeIndex <= 0)
                    {
                        steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
                    }
                }
                else
                {
                    steps_date_right_btn.setTextColor(ContextCompat.getColor(this.context, R.color.text_darkgray));
                }
            }
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        // If the event is a key-down event on the "enter" button
        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER))
        {
            // Perform action on key press
            return true;
        }
        return false;
    }

    /**
     * start of OnChartGestureListener
     **/

    @Override
    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture)
    {

    }

    @Override
    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture)
    {

    }

    @Override
    public void onChartLongPressed(MotionEvent me)
    {

    }

    @Override
    public void onChartDoubleTapped(MotionEvent me)
    {

    }

    @Override
    public void onChartSingleTapped(MotionEvent me)
    {

    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY)
    {

    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY)
    {

    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY)
    {

    }

    /**
     * end of OnChartGestureListener
     **/
}
