package anxa.com.smvideo.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



import java.util.List;

import anxa.com.smvideo.R;
import anxa.com.smvideo.contracts.Carnet.ExerciseContract;
import anxa.com.smvideo.util.AppUtil;

public class ExerciseListAdapter extends ArrayAdapter<ExerciseContract>
{
    private final Context context;
    private List<ExerciseContract> workoutArrayList;
    LayoutInflater layoutInflater;

    public ExerciseListAdapter(Context context, List<ExerciseContract> workoutTempArrayList)
    {
        super (context, R.layout.exercise_itemcell_data, workoutTempArrayList);
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        this.context = context;
        workoutArrayList = workoutTempArrayList;

        refreshUI();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ExerciseContract currentWorkout =  workoutArrayList.get(position);

        View rowView = layoutInflater.inflate(R.layout.exercise_itemcell_data, parent, false);

        /*Display Add Button*/
        if(position != 0)
        {
            ((ImageView)rowView.findViewById(R.id.exercise_displayAddButton)).setVisibility(View.GONE);
        }

        ((ImageView)rowView.findViewById(R.id.exercise_displayAddButton)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent exerciseIntent = new Intent(v.getContext(), ExerciseActivity.class);
//                exerciseIntent.putExtra("EXERCISE_STATUS", CommonConstants.COMMAND_ADDED);
//                v.getContext().startActivity(exerciseIntent);
            }
        });

        /*Description*/
        TextView exerciseDesc = (TextView)rowView.findViewById(R.id.exercise_displayDescription);
        exerciseDesc.setText(currentWorkout.Message);

        if (currentWorkout.Message == null || (currentWorkout.Message != null && currentWorkout.Message.isEmpty() ))
        {
            exerciseDesc.setVisibility(View.GONE);
        }
        else
        {
            exerciseDesc.setVisibility(View.VISIBLE);
            exerciseDesc.setText(currentWorkout.Message);
        }

        /*Body*/
        TextView exerciseDescBody = (TextView)rowView.findViewById(R.id.exercise_selectedBody);

        if (currentWorkout.Steps == 0)
        {
            if (currentWorkout.Distance == 0.0)
            {
                exerciseDescBody.setText(currentWorkout.Duration+" min");
            }
            else
            {
                exerciseDescBody.setText(currentWorkout.Duration+" min - "+currentWorkout.Distance+" km");
            }
        }
        else if (currentWorkout.Duration == 0)
        {
            if (currentWorkout.Distance == 0.0)
            {
                exerciseDescBody.setText(currentWorkout.Steps+" "+context.getString(R.string.STEPS)+" - ");
            }
            else
            {
                exerciseDescBody.setText(currentWorkout.Distance+" km - "+currentWorkout.Steps+" "+context.getString(R.string.STEPS));
            }
        }
        else if (currentWorkout.Distance == 0.0)
        {
            if (currentWorkout.Duration == 0)
            {
                exerciseDescBody.setText(currentWorkout.Steps+" "+context.getString(R.string.STEPS));
            }
            else
            {
                exerciseDescBody.setText(currentWorkout.Duration+" min - "+currentWorkout.Steps+" "+context.getString(R.string.STEPS));
            }
        }
        else
        {
            exerciseDescBody.setText(currentWorkout.Duration+" min - "+currentWorkout.Distance+" km - "+currentWorkout.Steps+" "+context.getString(R.string.STEPS));
        }
        if(currentWorkout.ExerciseId == 0)
        {
            exerciseDescBody.setText(currentWorkout.Steps+" "+context.getString(R.string.STEPS)+" "+currentWorkout.Calories+" cal"+" "+currentWorkout.Duration+" min"+" ");
        }

        /*Time*/
        ((TextView)rowView.findViewById(R.id.exercise_displayTime)).setText(AppUtil.getTimeOnly12(currentWorkout.CreationDate));

        /*Title and Image*/
        TextView exercise_displayTitle = (TextView)rowView.findViewById(R.id.exercise_displayTitle);
        ImageView exercise_displaylogo = (ImageView)rowView.findViewById(R.id.exercise_displaylogo);

        //System.out.println("CurrentWorkout: " + currentWorkout.ExerciseType);

            exercise_displayTitle.setText(AppUtil.getExercise(context, currentWorkout.ExerciseType));

        System.out.println(AppUtil.getExercise(context,currentWorkout.ExerciseType).toUpperCase());
        if(currentWorkout.ExerciseId == 0)
        { exercise_displayTitle.setText("Google Fit");
            exercise_displaylogo.setImageResource(R.drawable.exercise_display_run);
            ((TextView)rowView.findViewById(R.id.exercise_displayTime)).setVisibility(View.GONE);
        }
        else
        {
            switch (currentWorkout.ExerciseType){ /*Workout.java*/
                case 1:/*"RUN"*/
                    exercise_displaylogo.setImageResource(R.drawable.exercise_display_run);
                    break;
                case 2:/*"BIKE/CYCLING"*/
                    exercise_displaylogo.setImageResource(R.drawable.exercise_display_bike);
                    break;
                case 4:/*"WALK"*/
                    exercise_displaylogo.setImageResource(R.drawable.exercise_display_walk);
                    break;
                case 10:/*"SWIM"*/
                    exercise_displaylogo.setImageResource(R.drawable.exercise_display_swim);
                    break;
                case 35:/*"WORKOUT"*/
                    exercise_displaylogo.setImageResource(R.drawable.exercise_display_workout);
                    break;
                default:

                    exercise_displaylogo.setImageResource(R.drawable.exercise_display_other);
            }
        }



        return rowView;
    }

    private void refreshUI()
    {
        notifyDataSetChanged();
    }
}