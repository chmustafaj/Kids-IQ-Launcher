package com.kids.launcher.system;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.kids.launcher.util.BitmapManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    public String username;
    public String password;
    public String birthday;
    public String picture;
    public Bitmap picdecoded;
    public String lockTime="";
    public String lockDate="";
    public boolean lock=false;
    public ArrayList<Profile> profiles = new ArrayList<Profile>(Collections.emptyList());

    public String toJson(Gson gson) {
        /* Preparing a map */
        Map<String, Object> user = new HashMap<>();

        /* Mapping a user's different properties */
        user.put("username", username);
        user.put("password", password);
        user.put("birthday", birthday);
        user.put("picture", picture);
        user.put("lock",lock);
        user.put("lockTime",lockTime);
        user.put("lockDate",lockDate);


        /* Mapping a user's profiles to a single 'profile' key */
        List<String> profilejson = new ArrayList<>(Collections.emptyList());
        for (Profile p : profiles) {
            profilejson.add(p.toJson(gson));
        }
        user.put("profiles", profilejson);

        // Returning the user's properties map as a single Json file
        return gson.toJson(user);
    }

    public static User fromJson(Gson gson, String json) {
        User user = new User();

        Log.e("Json", json);
        JsonObject userobject = gson.fromJson(json, JsonObject.class);
        user.username = userobject.getAsJsonPrimitive("username").getAsString();
        user.password = userobject.getAsJsonPrimitive("password").getAsString();
        user.birthday = userobject.getAsJsonPrimitive("birthday").getAsString();
        user.picture = userobject.getAsJsonPrimitive("picture").getAsString();
        user.lock = userobject.getAsJsonPrimitive("lock").getAsBoolean();
        user.lockTime = userobject.getAsJsonPrimitive("lockTime").getAsString();
        user.lockDate = userobject.getAsJsonPrimitive("lockDate").getAsString();
        user.picdecoded = BitmapManager.base64ToBitmap(user.picture);
        user.profiles = new ArrayList<>(Collections.emptyList());

        JsonArray profilearray = userobject.getAsJsonArray("profiles");
        for (JsonElement p : profilearray) {
            Profile prof = Profile.fromJson(gson, p.getAsString());
            user.profiles.add(prof);
        }

        return user;
    }

    public Profile getProfile(String profileName) {
        for (Profile profile : profiles) {
            if (profile.name.equals(profileName)) {
                return profile;
            }
        }
        return new Profile();
    }
}
