package com.kids.launcher.system;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PrefUtils {

    private static final String TAG = "";
    static String KEY = "USER_DATA_:";

    public static void saveUser(User user, Context context, Gson gson) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        //Log.d(TAG, "saveUser: usage time "+user.profiles.get(0).usageTimes);
        /* SharedPreferences may very well contain other key-value pairs that do not pertain to
         * users in any way. Therefore, we shouldn't use the user's name as the "key", but we should
         * add some element to it so we can know that this SharedPreference key-value pair refers
         * to a user data. We can do that by applying a prefix.*/
        sp.edit().putString(KEY + user.username, user.toJson(gson)).apply();
    }

    public static void updateUser(User user, String oldname, Context context, Gson gson) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().remove(KEY + oldname).commit(); /* Has to be commited, not applied */
        sp.edit().putString(KEY + user.username, user.toJson(gson)).apply();
    }

    public static void deleteUser(User user, Context context, Gson gson) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().remove(KEY + user.username).commit(); /* Has to be commited */
    }

    public static User fetchUser(String username, Context context, Gson gson) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String userjson = sp.getString(KEY + username, "{]");
        return User.fromJson(gson, userjson);
    }

    public static List<User> fetchAllUsers(Context context, Gson gson) {
        List<User> userlist = new ArrayList<>(Collections.emptyList());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> spkeys = sp.getAll().keySet();
        for (String key : spkeys) {
            if (key.contains(KEY)) {
                /* Means that the key-value pair corresponds to a user data pair */
                userlist.add(User.fromJson(gson, sp.getString(key, "")));
            }
        }
        return userlist;
    }

    public static boolean userExists(String username, Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> spkeys = sp.getAll().keySet();
        String fulluser = KEY + username;
        return spkeys.contains(fulluser);
    }

    public static List<User> deleteProfileFromUser(Context context, String profileName) {
        List<User> userList = fetchAllUsers(context, new Gson());
        int profileIndex = 0;
        for (User user : userList) {
            for (Profile profile : user.profiles) {
                if (profile.name.equals(profileName)) {
                    user.profiles.remove(profileIndex);
                }
            }
            profileIndex++;
        }
        return userList;
    }

    public static void setTimeBreakStarted(Context context, String profileName, User user, float timeBreakStarted) {
        deleteUser(user, context, new Gson());
        for (Profile profile : user.profiles) {
            if (profile.name.equals(profileName)) {
                profile.timeBreakStarted = timeBreakStarted;
            }
        }
        saveUser(user, context, new Gson());
    }

//    public static Pair<Boolean, String> checkTimeup(Context context, Gson gson) {
//        Log.d(TAG, "checkTimeup: checking time up");
//        List<User> userlist = PrefUtils.fetchAllUsers(context, gson);
//        long timestamp = System.currentTimeMillis();
//        for (User user : userlist) {
//            for (Profile profile : user.profiles) {
//                Log.d(TAG, "checkTimeup: time left "+ String.valueOf(profile.timelimit-timestamp));
//                if (profile.timelimit < timestamp) {
//
//                    return new Pair<>(true, profile.name);
//                }
//            }
//        }
//        return new Pair<>(false, "");
//    }
}
