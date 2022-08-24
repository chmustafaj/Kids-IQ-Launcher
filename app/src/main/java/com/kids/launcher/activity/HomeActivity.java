package com.kids.launcher.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.role.RoleManager;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.util.Pair;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.kids.launcher.BuildConfig;
import com.kids.launcher.R;
import com.kids.launcher.activity.homeparts.HpAppDrawer;
import com.kids.launcher.activity.homeparts.HpDesktopOption;
import com.kids.launcher.activity.homeparts.HpDragOption;
import com.kids.launcher.activity.homeparts.HpInitSetup;
import com.kids.launcher.activity.homeparts.HpSearchBar;
import com.kids.launcher.activity.timeLimit.ScreenReceiver;
import com.kids.launcher.activity.timeLimit.TimeManagementUtils;
import com.kids.launcher.activity.timeLimit.subFeatures.InputDetection;
import com.kids.launcher.interfaces.AppDeleteListener;
import com.kids.launcher.manager.Setup;
import com.kids.launcher.model.App;
import com.kids.launcher.model.Item;
import com.kids.launcher.notifications.NotificationListener;
import com.kids.launcher.receivers.AppUpdateReceiver;
import com.kids.launcher.receivers.ShortcutReceiver;
import com.kids.launcher.system.PrefUtils;
import com.kids.launcher.system.Profile;
import com.kids.launcher.system.UsageTime;
import com.kids.launcher.system.User;
import com.kids.launcher.util.AppManager;
import com.kids.launcher.util.AppSettings;
import com.kids.launcher.util.DatabaseHelper;
import com.kids.launcher.util.Definitions.ItemPosition;
import com.kids.launcher.util.LauncherAction;
import com.kids.launcher.util.LauncherAction.Action;
import com.kids.launcher.util.Tool;
import com.kids.launcher.viewutil.DialogHelper;
import com.kids.launcher.viewutil.MinibarAdapter;
import com.kids.launcher.viewutil.WidgetHost;
import com.kids.launcher.widget.AppDrawerController;
import com.kids.launcher.widget.AppItemView;
import com.kids.launcher.widget.Desktop;
import com.kids.launcher.widget.Desktop.OnDesktopEditListener;
import com.kids.launcher.widget.DesktopOptionView;
import com.kids.launcher.widget.Dock;
import com.kids.launcher.widget.GroupPopupView;
import com.kids.launcher.widget.ItemOptionView;
import com.kids.launcher.widget.MinibarView;
import com.kids.launcher.widget.PagerIndicator;
import com.kids.launcher.widget.SearchBar;

import net.gsantner.opoc.util.ContextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public final class HomeActivity extends InputDetection implements OnDesktopEditListener {
    public static final Companion Companion = new Companion();
    public static final int REQUEST_CREATE_APPWIDGET = 0x6475;
    public static final int REQUEST_PERMISSION_STORAGE = 0x3648;
    public static final int REQUEST_PICK_APPWIDGET = 0x2678;
    private BroadcastReceiver mReceiver = null;  //for detecting when the screen turns off
    private static final String TAG = "";
    // receiver variables
    private static final IntentFilter _appUpdateIntentFilter = new IntentFilter();
    private static final IntentFilter _shortcutIntentFilter = new IntentFilter();
    private static final IntentFilter _timeChangedIntentFilter = new IntentFilter();
    public static WidgetHost _appWidgetHost;
    public static AppWidgetManager _appWidgetManager;
    public static boolean ignoreResume;
    public static float _itemTouchX;
    public static float _itemTouchY;
    // static launcher variables
    public static HomeActivity _launcher;
    public static DatabaseHelper _db;
    public static HpDesktopOption _desktopOption;
    public static boolean active = false;
    static int TimePassed = 0;
    static float timestamp = 0;
    static int pauseAfterTime = 0;
    static float t1Mon, t2Mon, t1Tue, t2Tue, t1Wed, t2Wed, t1Thu, t2Thu, t1Fri, t2Fri, t1Sat, t2Sat, t1Sun, t2Sun;
    static int countShowPopup;

    static {
        _timeChangedIntentFilter.addAction(Intent.ACTION_TIME_TICK);
        _timeChangedIntentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        _timeChangedIntentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        _appUpdateIntentFilter.addDataScheme("package");
        _appUpdateIntentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        _appUpdateIntentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        _appUpdateIntentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        _shortcutIntentFilter.addAction("com.android.launcher.action.INSTALL_SHORTCUT");
    }

    String profileName;
    AtomicBoolean timeUp;
    ArrayList<UsageTime> usageTimes = new ArrayList<>();
    int count = 0;
    private AppUpdateReceiver _appUpdateReceiver;
    private ShortcutReceiver _shortcutReceiver;
    private BroadcastReceiver _timeChangedReceiver;
    private int cx;
    private int cy;
    private Handler windowCloseHandler = new Handler();
    private Runnable windowCloserRunnable = new Runnable() {
        @Override
        public void run() {
            ActivityManager am = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
            if (cn != null && cn.getClassName().equals("com.android.systemui.recent.RecentsActivity")) {
                toggleRecents();
            }
        }
    };
    public static Pair<Boolean, String> checkTimeup(Context context, Gson gson, String profileName) {
        //checking if the profile or user is locked
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm");
        String strCurrentTime = mdformat.format(calendar.getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
        String currentDate = df.format(calendar.getTime());
        Date currTime=null;
        Date curr=null;
        try {
            curr = df.parse(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            currTime=mdformat.parse(strCurrentTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        int timePass = 0;
        List<User> userlist = PrefUtils.fetchAllUsers(context, gson);
        for (User user : userlist) {
            for (Profile profile : user.profiles) {
                if (profile.name.equals(profileName)) {
                    Date lockDate=null;
                    Date lockTime=null;
                    Log.d(TAG, "checkTimeup: lock time "+profile.lockTime);
                    Log.d(TAG, "checkTimeup: lock date "+profile.lockDate);
                    Log.d(TAG, "checkTimeup: current date "+currentDate);
                    Log.d(TAG, "checkTimeup: current time "+strCurrentTime);
                    Log.d(TAG, "checkTimeup: lock "+profile.lock);
                    try {
                        lockDate =df.parse(profile.lockDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        lockTime=mdformat.parse(profile.lockTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(lockDate!=null&&curr!=null&&lockTime!=null){
                        if(lockDate.after(curr)){
                            Toast.makeText(context, "Profile Locked", Toast.LENGTH_SHORT).show();
                            return new Pair<>(true, profile.name);
                        }else if(curr.equals(lockDate)){
                            if(lockTime.after(currTime)){
                                Toast.makeText(context, "Profile Locked", Toast.LENGTH_SHORT).show();
                                return new Pair<>(true, profile.name);
                            }
                        }
                    } //checking if user is blocked
                    try {
                        lockDate =df.parse(user.lockDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    try {
                        lockTime=mdformat.parse(user.lockTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if(lockDate!=null&&curr!=null&&lockTime!=null){
                        if(lockDate.after(curr)){
                            Toast.makeText(context, "User Locked", Toast.LENGTH_SHORT).show();
                            return new Pair<>(true, profile.name);
                        }else if(curr.equals(lockDate)){
                            if(lockTime.after(currTime)){
                                Toast.makeText(context, "User Locked", Toast.LENGTH_SHORT).show();
                                return new Pair<>(true, profile.name);
                            }
                        }
                    }
                    if(profile.lock){
                        Toast.makeText(context, "Profile Locked", Toast.LENGTH_SHORT).show();
                        return new Pair<>(true, profile.name);
                    }
                    if(user.lock){
                        Toast.makeText(context, "User Locked", Toast.LENGTH_SHORT).show();
                        return new Pair<>(true, profile.name);
                    }
                    //checking if user is taking a 30 minute break after 60 seconds

                    float currentTime = timeFromStringToFloat(strCurrentTime);
                    if (profile.timeBreakStarted != 0) {
                        if (currentTime - profile.timeBreakStarted > (float) profile.breakTime / 60) {   // TODO: 07/08/2022 change to 1 later. This is for seconds for testing purposes
                            pauseAfterTime = 0;
                            PrefUtils.setTimeBreakStarted(context, profile.name, user, 0);
                        }
                    }
                    if (pauseAfterTime > profile.maxConsecutiveTime) {   //take a break
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat mdf = new SimpleDateFormat("HH:mm");
                        String strBreakStartedAt = mdf.format(c.getTime());
                        if (profile.timeBreakStarted == 0) {
                            PrefUtils.setTimeBreakStarted(context, profile.name, user, timeFromStringToFloat(strBreakStartedAt));
                        }
                        Toast.makeText(context, "Take a break", Toast.LENGTH_SHORT).show();
                        return new Pair<>(true, profile.name);
                    }


                    Log.d(TAG, "checkTimeup: protile time limit " + profile.timelimit);
                    if (profile.timelimit == -1) {
                    }
                    timePass = TimeManagementUtils.getInstance(context).getTimeByProfileName(profileName);
                    if (profile.timelimit - timePass >= 0) {
                        TimeManagementUtils.getInstance(context).setTimePassed(profileName, timePass + 1);

                    }
                    if(profile.timelimit!=-1){
                        if (profile.timelimit - timePass < 0) {
                            Log.d(TAG, "checkTimeup: time up");

                            return new Pair<>(true, profile.name);
                        }
                    }
                    Calendar cal = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
                    String strDate = dateFormat.format(cal.getTime());
                    float presentTime = timeFromStringToFloat(strDate);
                    int day = cal.get(Calendar.DAY_OF_WEEK);
                    switch (day) {
                        case Calendar.SUNDAY:
                            if (presentTime < t1Sun || presentTime > t2Sun) {
                                return new Pair<>(true, profile.name);
                            }
                        case Calendar.MONDAY:
                            if (presentTime < t1Mon || presentTime > t2Mon) {
                                Log.d(TAG, "checkTimeup: usage time up");
                                return new Pair<>(true, profile.name);
                            }
                        case Calendar.TUESDAY:
                            if (presentTime < t1Tue || presentTime > t2Tue) {
                                return new Pair<>(true, profile.name);

                            }
                        case Calendar.WEDNESDAY:
                            if (presentTime < t1Wed || presentTime > t2Wed) {
                                return new Pair<>(true, profile.name);
                            }
                        case Calendar.FRIDAY:
                            if (presentTime < t1Fri || presentTime > t2Fri) {
                                return new Pair<>(true, profile.name);
                            }
                        case Calendar.THURSDAY:
                            if (presentTime < t1Thu || presentTime > t2Thu) {
                                return new Pair<>(true, profile.name);
                            }
                            break;
                        case Calendar.SATURDAY:
                            if (presentTime < t1Sat || presentTime > t2Sat) {
                                return new Pair<>(true, profile.name);
                            }
                    }
                }

            }
        }
        return new Pair<>(false, "");
    }

    public static void addDailyTimeCharge(Profile profile, User user, Context context) {     //for daily profile charge
        Calendar calendar = Calendar.getInstance();
        int currentDay = calendar.get(Calendar.DAY_OF_WEEK);
        int timeToIncreaseInMaximumDailyTime = 0;
        int timePassed = TimeManagementUtils.getInstance(context).getTimeByProfileName(profile.name);  //time the child used the profile yesterday
        int oldTimeLimit = user.profiles.get(user.profiles.indexOf(profile)).timelimit;
        int newTimeLimit = oldTimeLimit;
        int dailyCharge = user.profiles.get(user.profiles.indexOf(profile)).dailyCharge;
        int maxProfileTime = user.profiles.get(user.profiles.indexOf(profile)).maximumProfileTime;
        boolean unusedTimeShouldBeAdded = user.profiles.get(user.profiles.indexOf(profile)).unusedTimeGoesIntoDailyTime;
        if (currentDay != profile.previousDay) {
            if (unusedTimeShouldBeAdded) {
                dailyCharge += (oldTimeLimit - timePassed);       //leftover time from yesterday wil be added to the charge
            }
            if (dailyCharge > timePassed) {
                TimeManagementUtils.getInstance(context).setTimePassed(profile.name, 0);
                timeToIncreaseInMaximumDailyTime = dailyCharge - timePassed;
                newTimeLimit = oldTimeLimit + timeToIncreaseInMaximumDailyTime;
            } else {
                TimeManagementUtils.getInstance(context).setTimePassed(profile.name, timePassed - dailyCharge);
            }
            if (newTimeLimit > maxProfileTime) {
                newTimeLimit = maxProfileTime;   //the maximum daily profile time cannot exceed the maximum time in profile time account
            }
            user.profiles.get(user.profiles.indexOf(profile)).timelimit = newTimeLimit;
            PrefUtils.saveUser(user, context, new Gson());
        }
    }

    public void earnTimeForProfiles(Context context) {
        Log.d(TAG, "earnTimeForProfiles: running");
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (active) {
                List<User> userlist = PrefUtils.fetchAllUsers(HomeActivity.this, new Gson());
                for (User user : userlist) {
                    for (Profile profile : user.profiles) {
                        if (profile.name.equals(profileName)) {
                            addTimeToFunProfiles(profile.funProfiles, context);
                        }
                    }
                }
            }
            if (active) {
                earnTimeForProfiles(context);
            } else {
            }
        }, 15000);  // TODO: 11/08/2022 change to 15 minutes later
    }

    int addedTimeCount = 0;

    public void addTimeToFunProfiles(ArrayList<String> funProfileNames, Context context) {
        for (String funProfileName : funProfileNames) {
            Log.d(TAG, "addTimeToFunProfiles: added time count " + addedTimeCount);
            Log.d(TAG, "addTimeToFunProfiles: adding time");
            Log.d(TAG, "addTimeToFunProfiles: time passed for " + funProfileName + " " + TimeManagementUtils.getInstance(context).getTimeByProfileName(funProfileName));
            if (TimeManagementUtils.getInstance(context).getTimeByProfileName(funProfileName) > 15) {  //loop runs twice so 30 minutes will be added
                TimeManagementUtils.getInstance(context).setTimePassed(funProfileName, TimeManagementUtils.getInstance(context).getTimeByProfileName(funProfileName) - 15);
                Toast.makeText(context, "Gained bonus time!", Toast.LENGTH_SHORT).show();

            } else {
                TimeManagementUtils.getInstance(context).setTimePassed(funProfileName, 0);
                Toast.makeText(context, "Gained bonus time!", Toast.LENGTH_SHORT).show();

            }
        }
    }


    public final DrawerLayout getDrawerLayout() {
        return findViewById(R.id.drawer_layout);
    }

    public final Desktop getDesktop() {
        return findViewById(R.id.desktop);
    }

    public final Dock getDock() {
        return findViewById(R.id.dock);
    }

    public final AppDrawerController getAppDrawerController() {
        return findViewById(R.id.appDrawerController);
    }

    public final GroupPopupView getGroupPopup() {
        return findViewById(R.id.groupPopup);
    }

    public final SearchBar getSearchBar() {
        return findViewById(R.id.searchBar);
    }

    public final View getBackground() {
        return findViewById(R.id.background_frame);
    }

    public final PagerIndicator getDesktopIndicator() {
        return findViewById(R.id.desktopIndicator);
    }

    public final DesktopOptionView getDesktopOptionView() {
        return findViewById(R.id.desktop_option);
    }

    public final ItemOptionView getItemOptionView() {
        return findViewById(R.id.item_option);
    }

    public final FrameLayout getMinibarFrame() {
        return findViewById(R.id.minibar_frame);
    }

    public final View getStatusView() {
        return findViewById(R.id.status_frame);
    }

    public final View getNavigationView() {
        return findViewById(R.id.navigation_frame);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: home activity Started");
        final IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenReceiver();
        registerReceiver(mReceiver, filter);
        active = true;
        addedTimeCount = 0;
        countShowPopup = 1;
        Intent intent = getIntent();

        if (null != intent) {
            profileName = intent.getStringExtra("profile_name");
        }
        List<User> userlist = PrefUtils.fetchAllUsers(HomeActivity.this, new Gson());
        for (User user : userlist) {
            for (Profile profile : user.profiles) {
                if (profile.name.equals(profileName)) {
                    ArrayList<UsageTime> usageTimes = convertUsageTimeInStringToObj(profile.strUsageTimes);
                    setUsageTimes(usageTimes);
                    if (profile.inputDetectionTime != -1) {
                        DISCONNECT_TIMEOUT = profile.inputDetectionTime * 1000L;   //converting to millis
                        // TODO: 12/08/2022 convert from seconds to minutes 
                    }
                    addDailyTimeCharge(profile, user, HomeActivity.this);
                }
            }
        }

        Companion.setLauncher(this);
        AndroidThreeTen.init(this);
        Log.d(TAG, "onCreate: active " + active);
        if (active) {
            Log.d(TAG, "onCreate: running time check");
            runPeriodicTimeUpCheck();
            earnTimeForProfiles(HomeActivity.this);
        }
        AppSettings appSettings = AppSettings.get();

        ContextUtils contextUtils = new ContextUtils(getApplicationContext());
        contextUtils.setAppLanguage(appSettings.getLanguage());
        super.onCreate(savedInstanceState);
        if (!Setup.wasInitialised()) {
            Setup.init(new HpInitSetup(this));
        }

        Companion.setLauncher(this);
        _db = Setup.dataManager();

        setContentView(getLayoutInflater().inflate(R.layout.activity_home, null));

        // transparent status and navigation
        Window window = getWindow();
        View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(1536);

        init();
    }

    private void init() {
        _appWidgetManager = AppWidgetManager.getInstance(this);
        _appWidgetHost = new WidgetHost(getApplicationContext(), R.id.app_widget_host);
        _appWidgetHost.startListening();

        // item drag and drop
        HpDragOption hpDragOption = new HpDragOption();
        View findViewById = findViewById(R.id.leftDragHandle);
        View findViewById2 = findViewById(R.id.rightDragHandle);
        hpDragOption.initDragNDrop(this, findViewById, findViewById2, getItemOptionView());

        registerBroadcastReceiver();
        initAppManager();
        initSettings();
        initViews();
    }

    protected void initAppManager() {
        if (Setup.appSettings().getAppFirstLaunch()) {
            Setup.appSettings().setAppFirstLaunch(false);
            Setup.appSettings().setAppShowIntro(false);
            Item appDrawerBtnItem = Item.newActionItem(8);
            appDrawerBtnItem._x = 2;
            _db.saveItem(appDrawerBtnItem, 0, ItemPosition.Dock);
        }
        Setup.appLoader().addUpdateListener(it -> {
            getDesktop().initDesktop();
            getDock().initDock();
            return false;
        });
        Setup.appLoader().addDeleteListener(new AppDeleteListener() {
            @Override
            public boolean onAppDeleted(List<App> apps) {
                getDesktop().initDesktop();
                getDock().initDock();
                return false;
            }
        });
        AppManager.getInstance(this).init();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        if (requestCode == Activity.RESULT_OK) {

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void showLauncherSelection() {

        RoleManager roleManager = (RoleManager) this.getSystemService(Context.ROLE_SERVICE);
        if (roleManager.isRoleAvailable(RoleManager.ROLE_HOME) && !roleManager.isRoleHeld(RoleManager.ROLE_HOME)) {
            Intent intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME);
            startActivityForResult(intent, -1);

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            windowCloseHandler.post(windowCloserRunnable);
        }
    }

    private void toggleRecents() {
        Intent closeRecents = new Intent("com.android.systemui.recent.action.TOGGLE_RECENTS");
        closeRecents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ComponentName recents = new ComponentName("com.android.systemui", "com.android.systemui.recent.RecentsActivity");
        closeRecents.setComponent(recents);
        this.startActivity(closeRecents);
    }

    protected void initViews() {
        new HpSearchBar(this, getSearchBar()).initSearchBar();
        getAppDrawerController().init();
        getDock().setHome(this);
        getDesktop().setDesktopEditListener(this);
        getDesktop().setPageIndicator(getDesktopIndicator());
        getDesktopIndicator().setMode(Setup.appSettings().getDesktopIndicatorMode());

        AppSettings appSettings = Setup.appSettings();

        _desktopOption = new HpDesktopOption(this);

        getDesktopOptionView().setDesktopOptionViewListener(_desktopOption);
        getDesktopOptionView().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDesktopOptionView().updateLockIcon(appSettings.getDesktopLock());
            }
        }, 100);

        getDesktop().addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                getDesktopOptionView().updateHomeIcon(appSettings.getDesktopPageCurrent() == position);
            }

            public void onPageScrollStateChanged(int state) {
            }
        });

        new HpAppDrawer(this, findViewById(R.id.appDrawerIndicator)).initAppDrawer(getAppDrawerController());
        initMinibar();
    }


    @Override
    protected void onPause() {
        // when the screen is about to turn off
        if (ScreenReceiver.wasScreenOn) {
            // this is the case when onPause() is called by the system due to a screen state change
            active = false;
            Log.e("MYAPP", "SCREEN TURNED OFF");
        } else {
            // this is when onPause() is called when the screen state has not changed
        }
        super.onPause();
    }

    public final void initMinibar() {
        final ArrayList<LauncherAction.ActionDisplayItem> items = AppSettings.get().getMinibarArrangement();
        MinibarView minibar = findViewById(R.id.minibar);
        minibar.setAdapter(new MinibarAdapter(this, items));
        minibar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                LauncherAction.RunAction(items.get(i), HomeActivity.this);
            }
        });
    }

    public final void initSettings() {
        updateHomeLayout();

        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getDesktopFullscreen()) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        // set background colors
        getDesktop().setBackgroundColor(appSettings.getDesktopBackgroundColor());
        getDock().setBackgroundColor(appSettings.getDockColor());

        // set frame colors
        getMinibarFrame().setBackgroundColor(appSettings.getMinibarBackgroundColor());
        getStatusView().setBackgroundColor(appSettings.getDesktopInsetColor());
        getNavigationView().setBackgroundColor(appSettings.getDesktopInsetColor());

        // lock the minibar
        getDrawerLayout().setDrawerLockMode(appSettings.getMinibarEnable() ? DrawerLayout.LOCK_MODE_UNLOCKED : DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void registerBroadcastReceiver() {
        _appUpdateReceiver = new AppUpdateReceiver();
        _shortcutReceiver = new ShortcutReceiver();
        _timeChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Intent.ACTION_TIME_TICK)
                        || intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)
                        || intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
                    updateSearchClock();
                }
            }
        };

        // register all receivers
        registerReceiver(_appUpdateReceiver, _appUpdateIntentFilter);
        registerReceiver(_shortcutReceiver, _shortcutIntentFilter);
        registerReceiver(_timeChangedReceiver, _timeChangedIntentFilter);
    }

    public final void onStartApp(@NonNull Context context, @NonNull App app, @Nullable View view) {
        if (BuildConfig.APPLICATION_ID.equals(app._packageName)) {
            LauncherAction.RunAction(Action.LauncherSettings, context);
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && app._userHandle != null) {
                LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
                List<LauncherActivityInfo> activities = launcherApps.getActivityList(app.getPackageName(), app._userHandle);
                for (int intent = 0; intent < activities.size(); intent++) {
                    if (app.getComponentName().equals(activities.get(intent).getComponentName().toString()))
                        launcherApps.startMainActivity(activities.get(intent).getComponentName(), app._userHandle, null, getActivityAnimationOpts(view));
                }
            } else {
                Intent intent = Tool.getIntentFromApp(app);
                context.startActivity(intent, getActivityAnimationOpts(view));
            }

            // close app drawer and other items in advance
            // annoying to wait for app drawer to close
            handleLauncherResume();
        } catch (Exception e) {
            e.printStackTrace();
            Tool.toast(context, R.string.toast_app_uninstalled);
        }
    }

    private Bundle getActivityAnimationOpts(View view) {
        Bundle bundle = null;
        if (view == null) {
            return null;
        }

        ActivityOptions options = null;
        if (VERSION.SDK_INT >= 23) {
            int left = 0;
            int top = 0;
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            if (view instanceof AppItemView) {
                width = (int) ((AppItemView) view).getIconSize();
                left = (int) ((AppItemView) view).getDrawIconLeft();
                top = (int) ((AppItemView) view).getDrawIconTop();
            }
            options = ActivityOptions.makeClipRevealAnimation(view, left, top, width, height);
        } else if (VERSION.SDK_INT < 21) {
            options = ActivityOptions.makeScaleUpAnimation(view, 0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }

        if (options != null) {
            bundle = options.toBundle();
        }

        return bundle;
    }

    public void onStartDesktopEdit() {
        Tool.visibleViews(100, getDesktopOptionView());
        updateDesktopIndicator(false);
        updateDock(false);
        updateSearchBar(false);
    }

    public void onFinishDesktopEdit() {
        Tool.invisibleViews(100, getDesktopOptionView());
        updateDesktopIndicator(true);
        updateDock(true);
        updateSearchBar(true);
    }

    public final void dimBackground() {
        Tool.visibleViews(200, getBackground());
    }

    public final void unDimBackground() {
        Tool.invisibleViews(200, getBackground());
    }

    public final void clearRoomForPopUp() {
        Tool.invisibleViews(200, getDesktop());
        updateDesktopIndicator(false);
        updateDock(false);
    }

    public final void unClearRoomForPopUp() {
        Tool.visibleViews(200, getDesktop());
        updateDesktopIndicator(true);
        updateDock(true);
    }

    public final void updateDock(boolean show) {
        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getDockEnable() && show) {
            Tool.visibleViews(100, getDock());
        } else {
            if (appSettings.getDockEnable()) {
                Tool.invisibleViews(100, getDock());
            } else {
                Tool.goneViews(100, getDock());
            }
        }
    }

    public final void updateSearchBar(boolean show) {
        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getSearchBarEnable() && show) {
            Tool.visibleViews(100, getSearchBar());
        } else {
            if (appSettings.getSearchBarEnable()) {
                Tool.invisibleViews(100, getSearchBar());
            } else {
                Tool.goneViews(100, getSearchBar());
            }
        }
    }

    public final void updateDesktopIndicator(boolean show) {
        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getDesktopShowIndicator() && show) {
            Tool.visibleViews(100, getDesktopIndicator());
        } else {
            Tool.goneViews(100, getDesktopIndicator());
        }
    }

    public final void updateSearchClock() {
        TextView textView = getSearchBar()._searchClock;

        if (textView.getText() != null) {
            try {
                getSearchBar().updateClock();
            } catch (Exception e) {
                getSearchBar()._searchClock.setText(R.string.bad_format);
            }
        }
    }

    public final void updateHomeLayout() {
        updateSearchBar(true);
        updateDock(true);
        updateDesktopIndicator(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_APPWIDGET) {
                _desktopOption.configureWidget(data);
            } else if (requestCode == REQUEST_CREATE_APPWIDGET) {
                _desktopOption.createWidget(data);
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra("appWidgetId", -1);
            if (appWidgetId != -1) {
                _appWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    @Override
    public void onBackPressed() {
        handleLauncherResume();
    }

    @Override
    protected void onStart() {
        _appWidgetHost.startListening();
        _launcher = this;

        super.onStart();
        active = true;
        addedTimeCount = 0;
        Toast.makeText(_launcher, "Tracking started", Toast.LENGTH_SHORT).show();
    }

    private void checkNotificationPermissions() {
        Set<String> appList = NotificationManagerCompat.getEnabledListenerPackages(this);
        for (String app : appList) {
            if (app.equals(getPackageName())) {
                // Already allowed, so request a full update when returning to the home screen from another app.
                Intent i = new Intent(NotificationListener.UPDATE_NOTIFICATIONS_ACTION);
                i.setPackage(getPackageName());
                i.putExtra(NotificationListener.UPDATE_NOTIFICATIONS_COMMAND, NotificationListener.UPDATE_NOTIFICATIONS_UPDATE);
                sendBroadcast(i);
                return;
            }
        }

        // Request the required permission otherwise.
        DialogHelper.alertDialog(this, getString(R.string.notification_title), getString(R.string.notification_summary), getString(R.string.enable), new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                Tool.toast(HomeActivity.this, getString(R.string.toast_notification_permission_required));
                startActivity(new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        _appWidgetHost.startListening();
        _launcher = this;

        // handle restart if something needs to be reset
        AppSettings appSettings = Setup.appSettings();
        if (appSettings.getAppRestartRequired()) {
            appSettings.setAppRestartRequired(false);
            recreate();
            return;
        }

        if (appSettings.getNotificationStatus()) {
            // Ask user to allow the Notification permission if not already provided.
            checkNotificationPermissions();
        }

        // handle launcher rotation
        if (appSettings.getDesktopOrientationMode() == 2) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else if (appSettings.getDesktopOrientationMode() == 1) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        handleLauncherResume();
        active = true;
        addedTimeCount = 0;
        earnTimeForProfiles(HomeActivity.this);
        resetDisconnectTimer();   //for input detection

        // only when screen turns on
        if (!ScreenReceiver.wasScreenOn) {
            // this is when onResume() is called due to a screen state change
            active = true;
            Log.e("MYAPP", "SCREEN TURNED ON");
        } else {
            // this is when onResume() is called when the screen state has not changed
        }
        runPeriodicTimeUpCheck();

    }

    @Override
    protected void onDestroy() {
        _appWidgetHost.stopListening();
        _launcher = null;
        unregisterReceiver(_appUpdateReceiver);
        unregisterReceiver(_shortcutReceiver);
        unregisterReceiver(_timeChangedReceiver);
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        super.onDestroy();
    }

    private void handleLauncherResume() {
        if (ignoreResume) {
            // only triggers when a new activity is launched that should leave launcher state alone
            // uninstall package activity and pick widget activity
            ignoreResume = false;
        } else {
            getSearchBar().collapse();
            getGroupPopup().collapse();
            // close app option menu
            getItemOptionView().collapse();
            // close minibar
            getDrawerLayout().closeDrawers();
            if (getDesktop().getInEditMode()) {
                // exit desktop edit mode
                getDesktop().getCurrentPage().performClick();
            } else if (getAppDrawerController().getDrawer().getVisibility() == View.VISIBLE) {
                closeAppDrawer();
            }
            if (getDesktop().getCurrentItem() != 0) {
                AppSettings appSettings = Setup.appSettings();
                getDesktop().setCurrentItem(appSettings.getDesktopPageCurrent());
            }
        }
    }

    public final void openAppDrawer() {
        openAppDrawer(null, 0, 0);
    }

    public final void openAppDrawer(View view, int x, int y) {
        if (!(x > 0 && y > 0) && view != null) {
            int[] pos = new int[2];
            view.getLocationInWindow(pos);
            cx = pos[0];
            cy = pos[1];

            cx += view.getWidth() / 2f;
            cy += view.getHeight() / 2f;
            if (view instanceof AppItemView) {
                AppItemView appItemView = (AppItemView) view;
                if (appItemView != null && appItemView.getShowLabel()) {
                    cy -= Tool.dp2px(14) / 2f;
                }
            }
            cy -= getAppDrawerController().getPaddingTop();
        } else {
            cx = x;
            cy = y;
        }
        getAppDrawerController().open(cx, cy);
    }

    public final void closeAppDrawer() {
        getAppDrawerController().close(cx, cy);
    }

    public AtomicBoolean runPeriodicTimeUpCheck() {//do in background thread
        pauseAfterTime++;
        Log.d(TAG, "runPeriodicTimeUpCheck: running time check" + count);
        count++;
        AtomicBoolean timeUp = new AtomicBoolean(false);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Pair<Boolean, String> pair = HomeActivity.checkTimeup(getApplicationContext(), new Gson(), profileName);
            Log.e("Timeup", pair.first.toString());
            if (pair.first) {
                timeUp.set(true);
                showTimeUpPopup(profileName);
            }
            Log.d(TAG, "runPeriodicTimeUpCheck: " + profileName + " active " + active);
            if (active) {
                runPeriodicTimeUpCheck();
            }
        }, 1000);   // TODO: 07/08/2022 change to make it for minutes instead of seconds

        return timeUp;
    }

    public void showTimeUpPopup(String expiredProfile) {

        if (countShowPopup > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.putExtra("show_time_over_dialog", "true");
            intent.putExtra("expired_profile", expiredProfile);
            startActivity(intent);
            countShowPopup--;
        }

    }

    void setUsageTimes(ArrayList<UsageTime> usageTime) {
        for (int i = 0; i < usageTime.size(); i++) {
            int dayOne = usageTime.get(i).dayOne;
            int dayTwo = usageTime.get(i).dayTwo;
            int counter = dayOne;
            for (int k = counter; k <= dayTwo; k++) {
                switch (k) {
                    case 2:
                        t1Mon = usageTime.get(i).timeOne;
                        t2Mon = usageTime.get(i).timeTwo;

                        break;
                    case 3:
                        t1Tue = usageTime.get(i).timeOne;
                        t2Tue = usageTime.get(i).timeTwo;
                        break;

                    case 4:
                        t1Wed = usageTime.get(i).timeOne;
                        t2Wed = usageTime.get(i).timeTwo;
                        break;

                    case 5:
                        t1Thu = usageTime.get(i).timeOne;
                        t2Thu = usageTime.get(i).timeTwo;
                        break;

                    case 6:
                        t1Fri = usageTime.get(i).timeOne;
                        t2Fri = usageTime.get(i).timeTwo;
                        break;

                    case 7:
                        t1Sat = usageTime.get(i).timeOne;
                        t2Sat = usageTime.get(i).timeTwo;
                        break;

                    case 1:
                        t1Sun = usageTime.get(i).timeOne;
                        t2Sun = usageTime.get(i).timeTwo;
                        break;

                }
            }
        }

    }

    public ArrayList<UsageTime> convertUsageTimeInStringToObj(ArrayList<String> strUsageTimes) {
        ArrayList<UsageTime> usageTimes = new ArrayList<>();
        for (int i = 0; i < strUsageTimes.size(); i++) {
            UsageTime usageTime = new UsageTime();
            String[] arrUsageTime = strUsageTimes.get(i).split(",");
            usageTime.timeOne = timeFromStringToFloat(arrUsageTime[0]);
            usageTime.timeTwo = timeFromStringToFloat(arrUsageTime[1]);
            usageTime.dayOne = Integer.parseInt(arrUsageTime[2]);
            usageTime.dayTwo = Integer.parseInt(arrUsageTime[3]);
            usageTimes.add(usageTime);

        }
        return usageTimes;
    }

    static float timeFromStringToFloat(String time) {
        String[] timeArray = time.split(":");
        float hour = Integer.parseInt(timeArray[0]);
        float minutes = Integer.parseInt(timeArray[1]);
        return hour + (minutes / 60);
    }


    public static final class Companion {
        private Companion() {
        }

        public final HomeActivity getLauncher() {
            return _launcher;
        }

        public final void setLauncher(@Nullable HomeActivity v) {
            _launcher = v;
        }
    }


}
