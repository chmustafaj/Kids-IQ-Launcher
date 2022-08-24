package com.kids.launcher.system;

import android.graphics.Bitmap;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kids.launcher.util.BitmapManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile implements Serializable {
    Calendar cal = Calendar.getInstance();    //for initializing previous day

    public int uid = 0;
    public String name = "";
    public String password = "";
    public String picture = "";
    public int breakTime = 60;   //default values
    public int maxConsecutiveTime = 10000000;
    public int dailyCharge = 0;
    public boolean unusedTimeGoesIntoDailyTime = false;
    public int maximumProfileTime = -1;
    public Bitmap picdecoded;
    public String lockTime="";
    public String lockDate="";
    public int previousDay = cal.get(Calendar.DAY_OF_WEEK);
    public float timeBreakStarted = (float) 0;
    public ArrayList<String> strUsageTimes = new ArrayList<String>(Collections.emptyList());
    public ArrayList<String> funProfiles = new ArrayList<>(Collections.emptyList());  //when the profile is used, time will be added to these profiles
    public int inputDetectionTime = -1;
    public boolean isStudyProfile = false;
    public Boolean timeUp;
    public boolean lock =false;
    public int timePassed = 0;
    public List<String> apps = new ArrayList<>(Collections.emptyList());
    // public long timelimit = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10); /* Default time limit */
    public int timelimit = -1;

    public int getTimePassed() {
        return timePassed;
    }

    public void setTimePassed(int timePassed) {
        this.timePassed = timePassed;
    }

    public String toJson(Gson gson) {
        /* Preparing a map */
        Map<String, Object> profile = new HashMap<>();

        /* Mapping a profile's different properties */
        profile.put("picture", picture);
        profile.put("maximumProfileTime", maximumProfileTime);
        profile.put("unusedTimeGoesIntoDailyTime", unusedTimeGoesIntoDailyTime);
        profile.put("breakTime", breakTime);
        profile.put("maxConsecutiveTime", maxConsecutiveTime);
        profile.put("timeBreakStarted", timeBreakStarted);
        profile.put("uid", uid);
        profile.put("name", name);
        profile.put("password", password);
        profile.put("dailyCharge", dailyCharge);
        profile.put("apps", apps);
        profile.put("timelimit", timelimit);
        profile.put("timePassed", timePassed);
        profile.put("usageTimes", strUsageTimes);
        profile.put("previousDay", previousDay);
        profile.put("funProfiles", funProfiles);
        profile.put("isStudyProfile", isStudyProfile);
        profile.put("inputDetectionTime", inputDetectionTime);
        profile.put("lockTime", lockTime);
        profile.put("lockDate", lockDate);
        profile.put("lock", lock);


        // Returning the profile's properties map as a single Json file
        return gson.toJson(profile);
    }


    public static Profile fromJson(Gson gson, String json) {
        Profile profile = new Profile();
        JsonObject profileobj = gson.fromJson(json, JsonObject.class);

        profile.uid = profileobj.getAsJsonPrimitive("uid").getAsInt();
        profile.name = profileobj.getAsJsonPrimitive("name").getAsString();
        profile.breakTime = profileobj.getAsJsonPrimitive("breakTime").getAsInt();
        profile.maxConsecutiveTime = profileobj.getAsJsonPrimitive("maxConsecutiveTime").getAsInt();
        profile.dailyCharge = profileobj.getAsJsonPrimitive("dailyCharge").getAsInt();
        profile.unusedTimeGoesIntoDailyTime = profileobj.getAsJsonPrimitive("unusedTimeGoesIntoDailyTime").getAsBoolean();
        profile.password = profileobj.getAsJsonPrimitive("password").getAsString();
        profile.picture = profileobj.getAsJsonPrimitive("picture").getAsString();
        profile.timeBreakStarted = profileobj.getAsJsonPrimitive("timeBreakStarted").getAsFloat();
        profile.previousDay = profileobj.getAsJsonPrimitive("previousDay").getAsInt();
        profile.maximumProfileTime = profileobj.getAsJsonPrimitive("maximumProfileTime").getAsInt();
        profile.picdecoded = BitmapManager.base64ToBitmap(profile.picture);
        JsonArray applist = profileobj.getAsJsonArray("apps");
        for (JsonElement appy : applist) {
            profile.apps.add(appy.getAsString());
        }
        JsonArray usageTimes = profileobj.getAsJsonArray("usageTimes");
        for (JsonElement usageTime : usageTimes) {
            profile.strUsageTimes.add(usageTime.getAsString());
        }
        JsonArray profiles = profileobj.getAsJsonArray("funProfiles");
        for (JsonElement p : profiles) {
            profile.funProfiles.add(p.getAsString());
        }
        profile.timelimit = profileobj.getAsJsonPrimitive("timelimit").getAsInt();
        profile.timePassed = profileobj.getAsJsonPrimitive("timePassed").getAsInt();
        profile.isStudyProfile = profileobj.getAsJsonPrimitive("isStudyProfile").getAsBoolean();
        profile.inputDetectionTime = profileobj.getAsJsonPrimitive("inputDetectionTime").getAsInt();
        profile.lock = profileobj.getAsJsonPrimitive("lock").getAsBoolean();
        profile.lockTime = profileobj.getAsJsonPrimitive("lockTime").getAsString();
        profile.lockDate = profileobj.getAsJsonPrimitive("lockDate").getAsString();
        return profile;
    }
}
