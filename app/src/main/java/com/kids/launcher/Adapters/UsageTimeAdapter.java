package com.kids.launcher.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kids.launcher.R;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.UsageTime;

import java.util.ArrayList;

public class UsageTimeAdapter extends RecyclerView.Adapter<UsageTimeAdapter.ViewHolder> {
    private static final String TAG = "";
    ArrayList<UsageTime> usageTimes = new ArrayList<>();
    Profile profile = new Profile();
    String oldUsageTime, newUsageTime;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usage_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.t1.setText(String.valueOf((int) usageTimes.get(position).timeOne));
        Log.d(TAG, "onBindViewHolder: time 1: " + String.valueOf((int) usageTimes.get(position).timeOne));
        holder.t2.setText(String.valueOf((int) usageTimes.get(position).timeTwo));
        String dayOne = "";
        String dayTwo = "";
        switch (usageTimes.get(position).dayOne) {
            case 1:
                dayOne = "Sunday";
                break;
            case 2:
                dayOne = "Monday";
                break;
            case 3:
                dayOne = "Tuesday";
                break;
            case 4:
                dayOne = "Wednesday";
                break;
            case 5:
                dayOne = "Thursday";
                break;
            case 6:
                dayOne = "Friday";
                break;
            case 7:
                dayOne = "Saturday";
                break;
        }
        switch (usageTimes.get(position).dayTwo) {
            case 1:
                dayTwo = "Sunday";
                break;
            case 2:
                dayTwo = "Monday";
                break;
            case 3:
                dayTwo = "Tuesday";
                break;
            case 4:
                dayTwo = "Wednesday";
                break;
            case 5:
                dayTwo = "Thursday";
                break;
            case 6:
                dayTwo = "Friday";
                break;
            case 7:
                dayTwo = "Saturday";
                break;
        }
        holder.d1.setText(dayOne);
        if (dayTwo.equals(dayOne)) {   //Not displaying both dats if both days are the same. Single day is selected
            dayTwo="";
            holder.line.setVisibility(View.INVISIBLE);
        }
        holder.d2.setText(dayTwo);

    }

    @Override
    public int getItemCount() {
        return usageTimes.size();
    }

    public void setUsageTimes(ArrayList<UsageTime> usageTimes) {
        this.usageTimes = usageTimes;
        notifyDataSetChanged();
    }
//    public void setProfile(Profile profile){
//        this.profile=profile;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView t1, t2, d1, d2, line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            t1 = itemView.findViewById(R.id.txtTime1);
            t2 = itemView.findViewById(R.id.txtTime2);
            d1 = itemView.findViewById(R.id.txtDay1);
            d2 = itemView.findViewById(R.id.txtDay2);
            line =itemView.findViewById(R.id.line);
        }
    }
}
