package com.kids.launcher.activity.timeLimit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class TimeManagementUtils {            //This is to get and set the time passed for a profile for the day
    private static final String TAG = "";
    private static TimeManagementUtils instance;
    private SharedPreferences sharedPreferences;
    private static final String TIME_PASSED_KEY = "time_passed";
    //private static final String PREVIOUS_DAY_KEY = "previous_day";

    private TimeManagementUtils(Context context) {
        sharedPreferences = context.getSharedPreferences("data_base", Context.MODE_PRIVATE);

        //The Utils will be a singleton class. There will be only one instance of AllBooks
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        if (null == getAllTimePassed()) {
            editor.putString(TIME_PASSED_KEY, gson.toJson(new ArrayList<Pair<String, Integer>>()));
            editor.commit();
        }


    }

    //There will be only one instace of the utils class
    public static TimeManagementUtils getInstance(Context context) {
        if (null != instance) {
            return instance;
        } else {
            instance = new TimeManagementUtils(context);
            return instance;
        }

    }

    public ArrayList<Pair<String, Integer>> getAllTimePassed() {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Pair<String, Integer>>>() {
        }.getType();
        ArrayList<Pair<String, Integer>> times = gson.fromJson(sharedPreferences.getString(TIME_PASSED_KEY, null), type);
        Log.d(TAG, "getAllTimePassed: " + times);
        Boolean isEmpty;
        if (null == times) {
            isEmpty = true;
        } else {
            isEmpty = false;
        }
        return times;
    }

    public boolean setTimePassed(String profileName, int timePassed) {
        Pair<String, Integer> pair = new Pair<>(profileName, timePassed);
        Log.d(TAG, "setTimePassed: " + pair.first + " " + pair.second);
        ArrayList<Pair<String, Integer>> times = getAllTimePassed();
        Log.d(TAG, "setTimePassed: " + times);
        boolean profileAlreadyPresent = false;
        try {
            if (null != times) {
                int count = 0;
                for (Pair<String, Integer> p : times) {
                    if (p.first.equals(profileName)) {
                        profileAlreadyPresent = true;
                        times.remove(count);
                        times.add(new Pair<>(profileName, timePassed));
                        Gson gson = new Gson();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(TIME_PASSED_KEY);
                        editor.putString(TIME_PASSED_KEY, gson.toJson(times));
                        editor.commit();
                        Log.d(TAG, "setTimePassed: chaging time to " + timePassed);
                        Log.d(TAG, "setTimePassed: time changed to " + getTimeByProfileName(profileName)); //not getting changed time
                        Log.d(TAG, "setTimePassed: array " + times);
                    }
                    count++;
                }
                if (!profileAlreadyPresent) {
                    Pair<String, Integer> newPair = new Pair<>(profileName, 0);
                    times.add(newPair);
                    Gson gson = new Gson();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(TIME_PASSED_KEY);
                    editor.putString(TIME_PASSED_KEY, gson.toJson(times));
                    editor.commit();

                }
            }

//            if(times.add(pair)){
//                Log.d(TAG, "setTimePassed: times added");
//                Gson gson = new Gson();
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.remove(TIME_PASSED_KEY);
//                editor.putString(TIME_PASSED_KEY,gson.toJson(times));
//                editor.commit();
//                return true;
//            }
        } catch (ConcurrentModificationException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTimeByProfileName(String profileName) {
        ArrayList<Pair<String, Integer>> times = getAllTimePassed();
        if (null != times) {
            for (Pair<String, Integer> p : times) {
                if (p.first.equals(profileName)) {
                    Log.d(TAG, "getTimeByProfileName: returning " + p.second + p.first + " array " + times);
                    return p.second;
                }
            }
        }
        return 0;
    }
}
