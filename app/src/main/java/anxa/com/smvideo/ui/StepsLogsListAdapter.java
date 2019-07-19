package anxa.com.smvideo.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.contracts.Graph.StepDataContract;
import anxa.com.smvideo.util.AppUtil;

/**
 * Created by elaineanxa on 18/07/2019
 */
public class StepsLogsListAdapter extends ArrayAdapter<StepDataContract> implements View.OnClickListener
{
    Context context;

    List<StepDataContract> items;
    StepDataContract selectedSteps;
    LayoutInflater layoutInflater;

    String inflater = Context.LAYOUT_INFLATER_SERVICE;

    TextView date_tv;
    TextView steps_tv;
    TextView device_tv;

    EditText date;
    EditText steps;

    List<String> actions;
    View.OnClickListener listener;

    final Calendar myCalendar = Calendar.getInstance();

    public StepsLogsListAdapter(Context context, List<StepDataContract> items)
    {
        super(context, R.layout.steps_log_list, items);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View rowView = layoutInflater.inflate(R.layout.steps_log_list, parent, false);

        StepDataContract currentSteps = (StepDataContract) items.get(position);
        String stepsDateString = currentSteps.StepDate;

        date_tv = ((TextView) rowView.findViewById(R.id.text_date));
        date_tv.setText(stepsDateString);

        String stepsString = Integer.toString(currentSteps.Steps);
        steps_tv = ((TextView) rowView.findViewById(R.id.text_steps));
        steps_tv.setText(stepsString);

        device_tv = ((TextView) rowView.findViewById(R.id.text_device));

        switch (currentSteps.DeviceType)
        {
            case 0:
                device_tv.setText(context.getResources().getString(R.string.GRAPH_MANUAL_INPUT));
                break;
            case 1:
                device_tv.setText("HapiTrack");
                break;
            case 2:
                device_tv.setText("HapiBand");
                break;
            case 6:
                device_tv.setText("HapiScale");
                break;
            case 7:
                device_tv.setText("HapiScalePlus");
                break;
            case 11:
                device_tv.setText("Fitbug");
                break;
            case 12:
                device_tv.setText("Fitbit");
                break;
            case 14:
                device_tv.setText("Withings");
                break;
            case 50:
                device_tv.setText("RunKeeper");
                break;
            case 58:
                device_tv.setText("Iphone");
                break;
            case 59:
                device_tv.setText("HapiIOS");
                break;
            case 70:
                device_tv.setText("Actipod");
                break;
            case 90:
                device_tv.setText("Google Fit");
                break;
        }

        if (currentSteps.DeviceType == 0)
        {
            ((ImageButton) rowView.findViewById(R.id.steps_log_edit)).setVisibility(View.VISIBLE);
            ((ImageButton) rowView.findViewById(R.id.steps_log_edit)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showOptionsDialog(v, items.get(position));
                }
            });
        } else {
            ((ImageButton) rowView.findViewById(R.id.steps_log_edit)).setVisibility(View.GONE);
        }

        if (position >= getCount())
            ((View) rowView.findViewById(R.id.separator)).setVisibility(View.GONE);

        return rowView;
    }

    public void updateItems(List<StepDataContract> items)
    {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public void onClick(View v)
    {
        listener.onClick(v);
    }

    private void showOptionsDialog(View v, StepDataContract selectedSteps)
    {
        this.selectedSteps = selectedSteps;
        ApplicationData.getInstance().currentSteps = selectedSteps;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        showEditDialog();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        showDeleteDialog();
                        break;
                }
            }
        };

        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_NoActionBar);
        }else{
            builder = new AlertDialog.Builder(context);
        }

        builder.setMessage(null).setPositiveButton(context.getResources().getString(R.string.btn_edit), dialogClickListener)
                .setNegativeButton(context.getResources().getString(R.string.btn_delete), dialogClickListener).show();
    }

    private void showDeleteDialog()
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        ApplicationData.getInstance().currentSteps = selectedSteps;
                        Intent broadint = new Intent();
                        broadint.setAction(context.getResources().getString(R.string.STEPS_GRAPH_BROADCAST_DELETE));
                        context.sendBroadcast(broadint);

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else{
            builder = new AlertDialog.Builder(context);
        }
        builder.setMessage(context.getResources().getString(R.string.ALERTMESSAGE_DELETE_MEAL)).setPositiveButton(context.getResources().getString(R.string.btn_yes), dialogClickListener)
                .setNegativeButton(context.getResources().getString(R.string.btn_no), dialogClickListener).show();
    }

    private void showEditDialog()
    {
        AlertDialog.Builder alertDialog;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else{
            alertDialog = new AlertDialog.Builder(context);
        }

        TextView title = new TextView(context);
        title.setText(context.getResources().getString(R.string.EDIT_STEPS));
        title.setBackgroundColor(Color.WHITE);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.parseColor("#F68802"));
        title.setTextSize(20);

        alertDialog.setCustomTitle(title);

        final TextView date_edit_tv = new TextView(context);
        final TextView steps_edit_tv = new TextView(context);

        date = new EditText(context);
        date.setText(selectedSteps.StepDate);
        steps = new EditText(context);
        steps.setText(Integer.toString(selectedSteps.Steps));

        date_edit_tv.setText(context.getResources().getString(R.string.WEIGHT_GRAPH_DATE));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        steps_edit_tv.setText(context.getResources().getString(R.string.STEPS_BUTTON));

        date.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL | InputType.TYPE_DATETIME_VARIATION_DATE);
        steps.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);
        ll.addView(date_edit_tv);
        ll.addView(date);
        ll.addView(steps_edit_tv);
        ll.addView(steps);
        ll.setPadding(5, 5, 5, 5);
        alertDialog.setView(ll);

        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton("Update", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                selectedSteps.Steps = Integer.parseInt(steps.getText().toString());
                selectedSteps.StepDate = AppUtil.getDateinStringWeight(myCalendar.getTime());

                ApplicationData.getInstance().currentSteps = selectedSteps;

                Intent broadint = new Intent();
                broadint.setAction(context.getResources().getString(R.string.STEPS_GRAPH_BROADCAST_EDIT));
                context.sendBroadcast(broadint);
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id) {
                //ACTION
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    private void showDatePicker()
    {
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        new DatePickerDialog(context, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel()
    {
        date.setText(AppUtil.getEditWeightDateFormat(myCalendar.getTime()));
    }
}
