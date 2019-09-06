package anxa.com.smvideo.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import anxa.com.smvideo.ApplicationData;
import anxa.com.smvideo.R;
import anxa.com.smvideo.contracts.WeightHistoryContract;
import anxa.com.smvideo.util.AppUtil;
import anxa.com.smvideo.util.DecimalInputTextWatcher;

/**
 * Created by aprilanxa on 12/07/2017.
 */

public class WeightLogsListAdapter extends ArrayAdapter<WeightHistoryContract> implements View.OnClickListener {

    Context context;

    List<WeightHistoryContract> items;
    WeightHistoryContract selectedWeight;
    LayoutInflater layoutInflater;

    String inflater = Context.LAYOUT_INFLATER_SERVICE;

    TextView date_tv;
    TextView weight_tv;
    TextView device_tv;

    EditText date;
    EditText weight;

    List<String> actions;
    View.OnClickListener listener;

    boolean isChangeDate = false;

    final Calendar myCalendar = Calendar.getInstance();

    public WeightLogsListAdapter(Context context, List<WeightHistoryContract> items) {

        super(context, R.layout.weight_logs_list, items);
        layoutInflater = (LayoutInflater) context.getSystemService(inflater);
        this.context = context;
        this.items = items;
        this.listener = listener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View rowView = layoutInflater.inflate(R.layout.weight_logs_list, parent, false);

        WeightHistoryContract currentWeight = (WeightHistoryContract) items.get(position);
        String weightDateString = AppUtil.getWeightDateFormat(AppUtil.toDate(currentWeight.Date));

        date_tv = ((TextView) rowView.findViewById(R.id.text_date));
        date_tv.setText(weightDateString);

        String weightString = currentWeight.WeightKg + " kg";
        weight_tv = ((TextView) rowView.findViewById(R.id.text_weight));
        weight_tv.setText(weightString);

        device_tv = ((TextView) rowView.findViewById(R.id.text_bmi));
        device_tv.setText(Float.toString(currentWeight.Bmi));

        ((ImageButton) rowView.findViewById(R.id.weight_log_edit)).setVisibility(View.VISIBLE);
        ((ImageButton) rowView.findViewById(R.id.weight_log_edit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsDialog(v, items.get(position));
            }
        });

        if (position >= getCount())
            ((View) rowView.findViewById(R.id.separator)).setVisibility(View.GONE);

        return rowView;
    }


    public void updateItems(List<WeightHistoryContract> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();// more than zero
    }


    @Override
    public void onClick(View v) {
        listener.onClick(v);
    }

    private void showOptionsDialog(View v, WeightHistoryContract selectedWeight) {

        this.selectedWeight = selectedWeight;

        ApplicationData.getInstance().currentWeight = selectedWeight;

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    private void showDeleteDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked

                        ApplicationData.getInstance().currentWeight = selectedWeight;
                        Intent broadint = new Intent();
                        broadint.setAction(context.getResources().getString(R.string.WEIGHT_GRAPH_BROADCAST_DELETE));
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

    private void showEditDialog() {

        AlertDialog.Builder alertDialog;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            alertDialog = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
        }else{
            alertDialog = new AlertDialog.Builder(context);
        }

        TextView title = new TextView(context);
        title.setText(context.getResources().getString(R.string.WEIGHT_GRAPH_EDIT_WEIGHT));
        title.setBackgroundColor(Color.WHITE);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.parseColor("#F68802"));
        title.setTextSize(20);

        alertDialog.setCustomTitle(title);

        final TextView date_tv = new TextView(context);
        final TextView weight_tv = new TextView(context);

        date = new EditText(context);
        date.setText(AppUtil.getEditWeightDateFormat(AppUtil.toDate(selectedWeight.Date)));
        weight = new EditText(context);
        weight.setText(String.format("%.2f", selectedWeight.WeightKg));

        date_tv.setText(context.getResources().getString(R.string.WEIGHT_GRAPH_DATE));
        date.setInputType(InputType.TYPE_NULL);
        date.requestFocus();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(selectedWeight.Date);
            }
        });

        weight_tv.setText("Poids (kg)");

        //date.setInputType(InputType.TYPE_DATETIME_VARIATION_NORMAL | InputType.TYPE_DATETIME_VARIATION_DATE);
        weight.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        weight.setKeyListener(DigitsKeyListener.getInstance("0123456789,."));
        weight.addTextChangedListener(new DecimalInputTextWatcher(weight, 2));

        int maxLength = 5;
        InputFilter[] fArray = new InputFilter[1];
        fArray[0] = new InputFilter.LengthFilter(maxLength);
        weight.setFilters(fArray);

        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setBackgroundColor(Color.WHITE);
        ll.addView(date_tv);
        ll.addView(date);
        ll.addView(weight_tv);
        ll.addView(weight);
        ll.setPadding(5, 5, 5, 5);
        alertDialog.setView(ll);

        alertDialog.setCancelable(true);
        alertDialog.setPositiveButton(context.getResources().getString(R.string.btn_ok), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                try {
                    selectedWeight.WeightKg = Float.valueOf(weight.getText().toString().replace(",", "."));
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(weight_tv.getWindowToken(), 0);
                    if( selectedWeight.WeightKg >= ApplicationData.getInstance().minWeight &&  selectedWeight.WeightKg <= ApplicationData.getInstance().maxWeight){
                        {
                            selectedWeight.Date = myCalendar.getTime().getTime() / 1000;
                        }
                        ApplicationData.getInstance().currentWeight = selectedWeight;




                        Intent broadint = new Intent();
                        broadint.setAction(context.getResources().getString(R.string.WEIGHT_GRAPH_BROADCAST_EDIT));
                        context.sendBroadcast(broadint);
                    }else{
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
                        }else{
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setMessage(context.getResources().getString(R.string.ALERTMESSAGE_ERRORWEIGHT_RANGE)).setPositiveButton(context.getResources().getString(R.string.btn_ok),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.cancel();
                                    }
                                }).show();
                    }

                }catch (NumberFormatException e){
                    e.printStackTrace();

                    String weightString = weight.getText().toString();

                    selectedWeight.WeightKg = Float.parseFloat(weightString.replace(',', '.'));
                    if (isChangeDate) {
                        selectedWeight.Date = myCalendar.getTime().getTime() / 1000;
                    }
                    ApplicationData.getInstance().currentWeight = selectedWeight;

                    Intent broadint = new Intent();
                    broadint.setAction(context.getResources().getString(R.string.WEIGHT_GRAPH_BROADCAST_EDIT));
                    context.sendBroadcast(broadint);
                }
            }
        });

        alertDialog.setNegativeButton(context.getResources().getString(R.string.btn_cancel), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {
                //ACTION
            }
        });

        AlertDialog alert = alertDialog.create();
        alert.show();
    }


    private void showDatePicker(final long selectedDate)
    {
        Locale.setDefault(Locale.FRANCE);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                //view.init(year, monthOfYear, dayOfMonth, null);
                updateLabel();
            }



        };
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedDate * 1000);

        DatePickerDialog dialog = new DatePickerDialog(context, date, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();

    }

    private void updateLabel() {
        date.setText(AppUtil.getEditWeightDateFormat(myCalendar.getTime()));
    }
}