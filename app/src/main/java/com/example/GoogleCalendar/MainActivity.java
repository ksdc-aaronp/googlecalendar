package com.example.GoogleCalendar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.GoogleCalendar.weekview.DateTimeInterpreter;
import com.example.GoogleCalendar.weekview.MonthLoader;
import com.example.GoogleCalendar.weekview.WeekView;
import com.example.GoogleCalendar.weekview.WeekViewEvent;
import com.gjiazhe.scrollparallaximageview.ScrollParallaxImageView;
import com.gjiazhe.scrollparallaximageview.parallaxstyle.VerticalMovingStyle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;
import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ThreadLocalRandom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import androidx.core.content.res.ResourcesCompat;

import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity implements
        MyRecyclerView.AppBarTracking,
        WeekView.EventClickListener,
        MonthLoader.MonthChangeListener,
        WeekView.EventLongPressListener,
        WeekView.EmptyViewLongPressListener,
        WeekView.ScrollListener,
        AdapterView.OnItemSelectedListener
{

    private static final String TAG = "MainActivity";
    public static LocalDate lastdate = LocalDate.now();
    private String daysList[] = {"", "Monday", "Tuesday", "Wednesday",
            "Thursday", "Friday", "Saturday", "Sunday"};
    public static int topspace = 0;
    private View myshadow;
    long lasttime;
    int mycolor;
    MyRecyclerView mNestedView;
    private ViewPager monthviewpager;
    private HashMap<LocalDate, EventInfo> alleventlist;
    private DrawerLayout drawerLayout;
    private int mAppBarOffset = 0;
    private boolean mAppBarIdle = true;
    private int mAppBarMaxOffset = 0;
    private View shadow;
    private AppBarLayout mAppBar;
    private boolean mIsExpanded = false;
    private View redlay;
    private ImageView mArrowImageView;
    private TextView monthname;
    private Toolbar toolbar;
    private int lastchangeindex = -1;
    private boolean isappbarclosed = true;
    private int month;
    private int expandedfirst;
    private TextView eventnametextview, eventrangetextview;
    private View eventview, fullview;
    private GooglecalenderView calendarView;
    private ArrayList<EventModel> eventalllist;
    private boolean isgivepermission;
    private HashMap<LocalDate, Integer> indextrack;
    private HashMap<LocalDate, Integer> dupindextrack;
    private RecyclerView taskRecyclerView;
    private TaskAdapter taskAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Spinner principalSpinner;
    private String principalList[] = {"All Principals", "Imana Foods", "Noodle King", "Prima Pasta", "No Principal"};
    private ProgressBar taskProgressBarCallApp;

    View weekviewcontainer;
    private String[] var = {"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN",};
    private int[] monthresource = {
            R.drawable.bk_af8,
            R.drawable.bk_af9,
            R.drawable.bk_af10,
            R.drawable.bkg_04_apr,
            R.drawable.bkg_05_may,
            R.drawable.bkg_06_jun,
            R.drawable.bkg_07_jul,
            R.drawable.bkg_08_aug,
            R.drawable.bkg_09_sep,
            R.drawable.bkg_10_oct,
            R.drawable.bkg_11_nov,
            R.drawable.bkg_12_dec
    };
    WeekView mWeekView;

    public static void setTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        setRootView(activity);
    }

    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(false);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow().setNavigationBarColor(Color.parseColor("#f1f3f5"));
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {

            drawerLayout.openDrawer(Gravity.LEFT);
            return true;
        }
        if (item.getItemId() == R.id.action_favorite) {
            final LocalDate localDate = LocalDate.now();
            if (monthviewpager.getVisibility() == View.VISIBLE && monthviewpager.getAdapter() != null) {
                monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(localDate), false);
            }
            if (weekviewcontainer.getVisibility() == View.VISIBLE) {
                Calendar todaydate = Calendar.getInstance();
                todaydate.set(Calendar.DAY_OF_MONTH, localDate.getDayOfMonth());
                todaydate.set(Calendar.MONTH, localDate.getMonthOfYear() - 1);
                todaydate.set(Calendar.YEAR, localDate.getYear());
                mWeekView.goToDate(todaydate);

            }
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mNestedView.getLayoutManager();
            mNestedView.stopScroll();
            if (indextrack.containsKey(new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()))) {

                final Integer val = indextrack.get(new LocalDate(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth()));

                if (isAppBarExpanded()) {
                    calendarView.setCurrentmonth(new LocalDate());

                    expandedfirst = val;
                    topspace = 20;
                    linearLayoutManager.scrollToPositionWithOffset(val, 20);
                    EventBus.getDefault().post(new MonthChange(localDate, 0));
                    month = localDate.getDayOfMonth();
                    lastdate = localDate;
                } else {

//                   calendarView.setCurrentmonth(new LocalDate());
//                   calendarView.adjustheight();
//                   mIsExpanded = false;
//                   mAppBar.setExpanded(false, false);
                    expandedfirst = val;
                    topspace = 20;
                    linearLayoutManager.scrollToPositionWithOffset(val, 20);
                    EventBus.getDefault().post(new MonthChange(localDate, 0));
                    month = localDate.getDayOfMonth();
                    lastdate = localDate;

                }


            }

        }
        return super.onOptionsItemSelected(item);

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = ResourcesCompat.getFont(this, R.font.googlesansmed);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    public int getnavigationHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void setMargins(View view, int left, int top, int right, int bottom, int width, int height) {

        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            view.setLayoutParams(new CoordinatorLayout.LayoutParams(width, height));
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    public void closebtnClick() {
        eventnametextview.setVisibility(View.GONE);
        eventrangetextview.setVisibility(View.GONE);
        ValueAnimator animwidth = ValueAnimator.ofInt(getDevicewidth(), eventview.getWidth());
        animwidth.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
            layoutParams.width = val;
            redlay.setLayoutParams(layoutParams);
        });
        animwidth.setDuration(300);

        ValueAnimator animheight = ValueAnimator.ofInt(getDeviceHeight(), 0);
        animheight.addUpdateListener(valueAnimator -> {

            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
            layoutParams.height = val;
            redlay.setLayoutParams(layoutParams);
            if (redlay.getTranslationZ() != 0 && valueAnimator.getAnimatedFraction() > 0.7) {
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.fourdp));
                shape.setColor(mycolor);
                redlay.setBackground(shape);
                redlay.setTranslationZ(0);
                shadow.setVisibility(View.GONE);
            }
        });
        animheight.setDuration(300);

        ValueAnimator animx = ValueAnimator.ofFloat(0, eventview.getLeft());
        animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                Float val = (Float) valueAnimator.getAnimatedValue();

                redlay.setTranslationX(val);
            }
        });
        animx.setDuration(300);

        ValueAnimator animy = ValueAnimator.ofFloat(0, fullview.getTop() + toolbar.getHeight());

        animy.addUpdateListener(valueAnimator -> {
            Float val = (Float) valueAnimator.getAnimatedValue();
            redlay.setTranslationY(val);
        });
        animy.setDuration(300);
        animwidth.start();
        animheight.start();
        animy.start();
        animx.start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWeekView = (WeekView) findViewById(R.id.weekView);
        weekviewcontainer = findViewById(R.id.weekViewcontainer);
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);

            //for aapplying a font to subMenu ...
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }

            //the method we have create in activity
            applyFontToMenuItem(mi);
        }
        navigationView.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.Day) {
                weekviewcontainer.setVisibility(View.VISIBLE);
                monthviewpager.setVisibility(View.GONE);
                mNestedView.setVisibility(View.GONE);
                mWeekView.setNumberOfVisibleDays(1);
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setAllDayEventHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26, getResources().getDisplayMetrics()));
                Calendar todaydate = Calendar.getInstance();
                todaydate.set(Calendar.DAY_OF_MONTH, MainActivity.lastdate.getDayOfMonth());
                todaydate.set(Calendar.MONTH, MainActivity.lastdate.getMonthOfYear() - 1);
                todaydate.set(Calendar.YEAR, MainActivity.lastdate.getYear());
                mWeekView.goToDate(todaydate);
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                mAppBar.setElevation(0);
                mArrowImageView.setVisibility(View.VISIBLE);
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else if (item.getItemId() == R.id.Week) {
                weekviewcontainer.setVisibility(View.VISIBLE);
                monthviewpager.setVisibility(View.GONE);
                mNestedView.setVisibility(View.GONE);
                mWeekView.setNumberOfVisibleDays(7);
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setAllDayEventHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
                Calendar todaydate = Calendar.getInstance();
                todaydate.set(Calendar.DAY_OF_MONTH, MainActivity.lastdate.getDayOfMonth());
                todaydate.set(Calendar.MONTH, MainActivity.lastdate.getMonthOfYear() - 1);
                todaydate.set(Calendar.YEAR, MainActivity.lastdate.getYear());
                mWeekView.goToDate(todaydate);
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                mAppBar.setElevation(0);
                mArrowImageView.setVisibility(View.VISIBLE);
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else if (item.getItemId() == R.id.threeday) {
                weekviewcontainer.setVisibility(View.VISIBLE);
                monthviewpager.setVisibility(View.GONE);
                mNestedView.setVisibility(View.GONE);
                mWeekView.setNumberOfVisibleDays(3);
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                mWeekView.setAllDayEventHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()));
                Calendar todaydate = Calendar.getInstance();
                todaydate.set(Calendar.DAY_OF_MONTH, MainActivity.lastdate.getDayOfMonth());
                todaydate.set(Calendar.MONTH, MainActivity.lastdate.getMonthOfYear() - 1);
                todaydate.set(Calendar.YEAR, MainActivity.lastdate.getYear());
                mWeekView.goToDate(todaydate);
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                mAppBar.setElevation(0);
                mArrowImageView.setVisibility(View.VISIBLE);
                drawerLayout.closeDrawer(Gravity.LEFT);

            } else if (item.getItemId() == R.id.monthviewitem) {
                mAppBar.setExpanded(false, false);
                mNestedView.setVisibility(View.GONE);
                weekviewcontainer.setVisibility(View.GONE);
                monthviewpager.setVisibility(View.VISIBLE);
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(false);
                mAppBar.setElevation(0);
                mArrowImageView.setVisibility(View.INVISIBLE);
                drawerLayout.closeDrawer(Gravity.LEFT);
                monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(MainActivity.lastdate), true);

            } else {
                LocalDate localDate = new LocalDate();
                String yearstr = MainActivity.lastdate.getYear() == localDate.getYear() ? "" : MainActivity.lastdate.getYear() + "";
                monthname.setText(MainActivity.lastdate.toString("MMMM") + " " + yearstr);
                calendarView.setCurrentmonth(MainActivity.lastdate);
                calendarView.adjustheight();
                mIsExpanded = false;
                mAppBar.setExpanded(false, false);
                EventBus.getDefault().post(new MessageEvent(MainActivity.lastdate));
                monthviewpager.setVisibility(View.GONE);
                weekviewcontainer.setVisibility(View.GONE);
                mNestedView.setVisibility(View.VISIBLE);
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
                ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
                mAppBar.setElevation(20);
                mArrowImageView.setVisibility(View.VISIBLE);
                drawerLayout.closeDrawer(Gravity.LEFT);
            }
            item.setChecked(true);
            return true;
        });

        eventalllist = new ArrayList<>();
        indextrack = new HashMap<>();
        dupindextrack = new HashMap<>();
        mAppBar = findViewById(R.id.app_bar);
        redlay = findViewById(R.id.redlay);
        redlay.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        shadow = findViewById(R.id.shadow);
        eventnametextview = findViewById(R.id.textView12);
        taskRecyclerView = findViewById(R.id.taskRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        taskRecyclerView.setLayoutManager(linearLayoutManager);
        taskAdapter = new TaskAdapter();
        taskRecyclerView.setAdapter(taskAdapter);
        principalSpinner = findViewById(R.id.principal_spinner);
        taskProgressBarCallApp = findViewById(R.id.task_progressbar_call_app);
        // progressBar.setProgress(40,true);


        ArrayAdapter<String> principalAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, principalList);
        principalAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        principalSpinner.setOnItemSelectedListener(this);
        principalSpinner.setAdapter(principalAdapter);

        eventrangetextview = findViewById(R.id.textView13);
        calendarView = findViewById(R.id.calander);
        calendarView.setPadding(0, getStatusBarHeight(), 0, 0);
        mNestedView = findViewById(R.id.nestedView);
        monthviewpager = findViewById(R.id.monthviewpager);
        monthviewpager.setOffscreenPageLimit(1);
        monthviewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {

                if (monthviewpager.getVisibility() == View.GONE) return;
                if (isAppBarClosed()) {
                    Log.e("selected", i + "");
                    LocalDate localDate = new LocalDate();
                    MonthPageAdapter monthPageAdapter = (MonthPageAdapter) monthviewpager.getAdapter();
                    MonthModel monthModel = monthPageAdapter.getMonthModels().get(i);
                    String year = monthModel.getYear() == localDate.getYear() ? "" : monthModel.getYear() + "";
                    monthname.setText(monthModel.getMonthnamestr() + " " + year);
                    MainActivity.lastdate = new LocalDate(monthModel.getYear(), monthModel.getMonth(), 1);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mNestedView.setAppBarTracking(this);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mNestedView.setLayoutManager(linearLayoutManager);
        DateAdapter dateAdapter = new DateAdapter();
        mNestedView.setAdapter(dateAdapter);

        final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(dateAdapter);
        mNestedView.addItemDecoration(headersDecor);
        EventBus.getDefault().register(this);

        monthname = findViewById(R.id.monthname);
        calendarView.setMonthChangeListner(monthModel -> {
            /**
             * call when GoogleCalendarView is open  scroll viewpager available inside GoogleCalendar
             */
            LocalDate localDate = new LocalDate();
            String year = monthModel.getYear() == localDate.getYear() ? "" : monthModel.getYear() + "";
            monthname.setText(monthModel.getMonthnamestr() + " " + year);
            if (weekviewcontainer.getVisibility() == View.VISIBLE) {
                Calendar todaydate = Calendar.getInstance();
                todaydate.set(Calendar.DAY_OF_MONTH, 1);
                todaydate.set(Calendar.MONTH, monthModel.getMonth() - 1);
                todaydate.set(Calendar.YEAR, monthModel.getYear());
                mWeekView.goToDate(todaydate);

            }
        });

        isgivepermission = true;
        LocalDate mintime = new LocalDate().minusMonths(1);
        LocalDate maxtime = new LocalDate().plusMonths(1);
        alleventlist = Utility.readCalendarEvent();
        calendarView.init(alleventlist, mintime, maxtime);
        calendarView.setCurrentmonth(new LocalDate());
        calendarView.adjustheight();
        mIsExpanded = false;
        mAppBar.setExpanded(false, false);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setPadding(0, getStatusBarHeight(), 0, 0);
        mArrowImageView = findViewById(R.id.arrowImageView);
        if (monthviewpager.getVisibility() == View.VISIBLE) {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
            ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(false);
            mAppBar.setElevation(0);
            mArrowImageView.setVisibility(View.INVISIBLE);
        } else {
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
            ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
            mAppBar.setElevation(20);
            mArrowImageView.setVisibility(View.VISIBLE);
        }

        mNestedView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            LinearLayoutManager llm = (LinearLayoutManager) mNestedView.getLayoutManager();
            DateAdapter dateAdapter = (DateAdapter) mNestedView.getAdapter();

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (mAppBarOffset != 0 && isappbarclosed && newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    calendarView.setCurrentmonth(dateAdapter.geteventallList().get(expandedfirst).getLocalDate());
                    calendarView.adjustheight();
                    mIsExpanded = false;
                    mAppBar.setExpanded(false, false);
                    Log.e("callme", "statechange");

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (isappbarclosed) {
                    int pos = llm.findFirstVisibleItemPosition();
                    View view = llm.findViewByPosition(pos);
                    int currentmonth = dateAdapter.geteventallList().get(pos).getLocalDate().getMonthOfYear();
                    if (dateAdapter.geteventallList().get(pos).getType() == 1) {
                        if (dy > 0 && Math.abs(view.getTop()) > 100) {
                            if (month != currentmonth)
                                EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate(), dy));
                            month = currentmonth;
                            lastdate = dateAdapter.geteventallList().get(pos).getLocalDate();
                            expandedfirst = pos;
                        } else if (dy < 0 && Math.abs(view.getTop()) < 100 && pos - 1 > 0) {
                            pos--;
                            currentmonth = dateAdapter.geteventallList().get(pos).getLocalDate().getMonthOfYear();
                            if (month != currentmonth)
                                EventBus.getDefault().post(new MonthChange(dateAdapter.geteventallList().get(pos).getLocalDate(), dy));
                            month = currentmonth;
                            lastdate = dateAdapter.geteventallList().get(pos).getLocalDate().dayOfMonth().withMaximumValue();
                            expandedfirst = pos;
                        }
                    } else {
                        lastdate = dateAdapter.geteventallList().get(pos).getLocalDate();
                        expandedfirst = pos;
                    }
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });


        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);

        }

        mAppBar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            if (mAppBarOffset != verticalOffset) {
                mAppBarOffset = verticalOffset;
                mAppBarMaxOffset = -mAppBar.getTotalScrollRange();
                int totalScrollRange = appBarLayout.getTotalScrollRange();
                float progress = (float) (-verticalOffset) / (float) totalScrollRange;
                if (monthviewpager.getVisibility() == View.GONE && mNestedView.getVisibility() == View.VISIBLE)
                    mAppBar.setElevation(20 + (20 * Math.abs(1 - progress)));
                if (weekviewcontainer.getVisibility() == View.VISIBLE) {
                    mAppBar.setElevation(20 - (20 * Math.abs(progress)));
                }
                if (Math.abs(progress) > 0.45) {
                    ViewGroup.LayoutParams params = myshadow.getLayoutParams();
                    params.height = (int) (getResources().getDimensionPixelSize(R.dimen.fourdp) * Math.abs(progress));
                    myshadow.setLayoutParams(params);
                }


                mArrowImageView.setRotation(progress * 180);
                mIsExpanded = verticalOffset == 0;
                mAppBarIdle = mAppBarOffset >= 0 || mAppBarOffset <= mAppBarMaxOffset;
                float alpha = (float) -verticalOffset / totalScrollRange;


                if (mAppBarOffset == -appBarLayout.getTotalScrollRange()) {
                    isappbarclosed = true;
                    setExpandAndCollapseEnabled(false);
                } else {
                    setExpandAndCollapseEnabled(true);
                }

                if (mAppBarOffset == 0) {
                    expandedfirst = linearLayoutManager.findFirstVisibleItemPosition();
                    if (mNestedView.getVisibility() == View.VISIBLE) {
                        topspace = linearLayoutManager.findViewByPosition(linearLayoutManager.findFirstVisibleItemPosition()).getTop();//uncomment jigs 28 feb
                    }
                    if (isappbarclosed) {
                        isappbarclosed = false;
                        mNestedView.stopScroll();
                        calendarView.setCurrentmonth(lastdate);
                    }
                }
            }
        });

        findViewById(R.id.backsupport).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (monthviewpager.getVisibility() == View.VISIBLE) return;
                        mIsExpanded = !mIsExpanded;
                        mNestedView.stopScroll();
                        mAppBar.setExpanded(mIsExpanded, true);
                    }
                });

        /////////////////weekview implemention/////
        myshadow = findViewById(R.id.myshadow);

        mWeekView.setshadow(myshadow);
        mWeekView.setfont(ResourcesCompat.getFont(this, R.font.googlesans_regular), 0);
        mWeekView.setfont(ResourcesCompat.getFont(this, R.font.googlesansmed), 1);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);
        mWeekView.setScrollListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        ObjectAnimator animation = ObjectAnimator.ofInt(taskProgressBarCallApp, "progress", 0, 40);
        animation.setDuration(1400);
        animation.setInterpolator(new AccelerateDecelerateInterpolator());
        animation.setStartDelay(2500);
        animation.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            LocalDate mintime = new LocalDate().minusMonths(1);
            LocalDate maxtime = new LocalDate().plusMonths(1);
            alleventlist = Utility.readCalendarEvent();
            calendarView.init(alleventlist, mintime, maxtime);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isgivepermission = true;
                    lastdate = new LocalDate();
                    calendarView.setCurrentmonth(new LocalDate());
                    calendarView.adjustheight();
                    mIsExpanded = false;
                    mAppBar.setExpanded(false, false);
                    mWeekView.notifyDatasetChanged();
                }
            }, 10);
        }
    }

    /**
     * this call when user is scrolling on mNestedView(recyclerview) and month will change
     * or when toolbar top side current date button selected
     */
    @Subscribe
    public void onEvent(MonthChange event) {
        if (!isAppBarExpanded()) {

            LocalDate localDate = new LocalDate();
            String year = event.getMessage().getYear() == localDate.getYear() ? "" : event.getMessage().getYear() + "";
            monthname.setText(event.getMessage().toString("MMMM") + " " + year);


            long diff = System.currentTimeMillis() - lasttime;
            boolean check = diff > 600;
            if (check && event.mdy > 0) {
                monthname.setTranslationY(35);
                mArrowImageView.setTranslationY(35);
                lasttime = System.currentTimeMillis();
                monthname.animate().translationY(0).setDuration(200).start();
                mArrowImageView.animate().translationY(0).setDuration(200).start();

            } else if (check && event.mdy < 0) {

                monthname.setTranslationY(-35);
                mArrowImageView.setTranslationY(-35);
                lasttime = System.currentTimeMillis();
                monthname.animate().translationY(0).setDuration(200).start();
                mArrowImageView.animate().translationY(0).setDuration(200).start();
            }
        }
    }

    /**
     * call when Googlecalendarview is open and tap on any date or scroll viewpager available inside GoogleCalendar
     */
    @Subscribe
    public void onEvent(MessageEvent event) {

        int previous = lastchangeindex;
        if (previous != -1) {
            int totalremove = 0;
            for (int k = 1; k <= 3; k++) {

                if (eventalllist.get(previous).getEventname().equals("dupli") || eventalllist.get(previous).getEventname().equals("click")) {
                    totalremove++;
                    EventModel eventModel = eventalllist.remove(previous);
                }
            }
            indextrack.clear();
            indextrack.putAll(dupindextrack);
            mNestedView.getAdapter().notifyDataSetChanged();

        }

        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mNestedView.getLayoutManager();
        if (indextrack.containsKey(event.getMessage())) {
            int index = indextrack.get(event.getMessage());
            int type = eventalllist.get(index).getType();
            if (type == 0 || type == 2) {

                lastdate = event.getMessage();
                expandedfirst = index;
                topspace = 20;
                linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);
                lastchangeindex = -1;

            } else {


                lastdate = event.getMessage();


                Integer ind = indextrack.get(event.getMessage());
                ind++;
                for (int i = ind; i < eventalllist.size(); i++) {


                    if (event.getMessage().isBefore(eventalllist.get(i).getLocalDate())) {
                        ind = i;
                        break;
                    }
                }
                lastchangeindex = ind;
                int typeselect = eventalllist.get(ind + 1).getType() == 200 ? 200 : 100;
                if (!eventalllist.get(ind - 1).getEventname().startsWith("dup")) {

                    eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
                    ind++;
                }
                expandedfirst = ind;
                eventalllist.add(ind, new EventModel("click", event.getMessage(), 1000));
                ind++;
                if (!eventalllist.get(ind).getEventname().startsWith("dup")) {

                    eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
                }
                mNestedView.getAdapter().notifyDataSetChanged();

                topspace = 20;
                linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);

                for (int i = lastchangeindex; i < eventalllist.size(); i++) {
                    if (!eventalllist.get(i).getEventname().startsWith("dup"))
                        indextrack.put(eventalllist.get(i).getLocalDate(), i);
                }


            }

        } else {
            Integer ind = indextrack.get(event.getMessage().dayOfWeek().withMinimumValue().minusDays(1));
            ind++;
            for (int i = ind; i < eventalllist.size(); i++) {

                if (event.getMessage().isBefore(eventalllist.get(i).getLocalDate())) {
                    ind = i;
                    break;
                }
            }
            lastchangeindex = ind;
            int typeselect = eventalllist.get(ind + 1).getType() == 200 ? 200 : 100;
            if (!eventalllist.get(ind - 1).getEventname().startsWith("dup")) {

                eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
                ind++;
            }
            expandedfirst = ind;

            eventalllist.add(ind, new EventModel("click", event.getMessage(), 1000));
            ind++;
            if (!eventalllist.get(ind).getEventname().startsWith("dup")) {

                eventalllist.add(ind, new EventModel("dupli", event.getMessage(), typeselect));
            }

            mNestedView.getAdapter().notifyDataSetChanged();
            topspace = 20;
            linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);

            for (int i = lastchangeindex; i < eventalllist.size(); i++) {
                if (!eventalllist.get(i).getEventname().startsWith("dup"))
                    indextrack.put(eventalllist.get(i).getLocalDate(), i);
            }

        }

    }

    private int getDeviceHeight() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        int height1 = size.y;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return height1;
    }

    private int getDevicewidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        return width;
    }

    @Override
    public void onBackPressed() {
        if (taskRecyclerView.getVisibility() == View.VISIBLE) {
            closebtnClick();
        } else if (mIsExpanded) {
            mIsExpanded = false;
            mNestedView.stopScroll();
            mAppBar.setExpanded(false, true);
        } else if (mNestedView.getVisibility() == View.VISIBLE) {
            monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(MainActivity.lastdate), false);

            mNestedView.setVisibility(View.GONE);
            monthviewpager.setVisibility(View.VISIBLE);
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
            ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(false);
            mAppBar.setElevation(0);
            mArrowImageView.setVisibility(View.INVISIBLE);
        } else {
            EventBus.getDefault().unregister(this);
            super.onBackPressed();
            finish();
        }


    }

    /**
     * call only one time after googlecalendarview init() method is done
     */
    @Subscribe
    public void onEvent(final AddEvent event) {
        eventalllist = event.getArrayList();


        final TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {

            int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            int monthheight = getDeviceHeight() - actionBarHeight - getnavigationHeight() - getStatusBarHeight();
            int recyheight = monthheight - getResources().getDimensionPixelSize(R.dimen.monthtopspace);
            int singleitem = (recyheight - 18) / 6;

            monthviewpager.setAdapter(new MonthPageAdapter(getSupportFragmentManager(), event.getMonthModels(), singleitem));
            monthviewpager.setCurrentItem(calendarView.calculateCurrentMonth(LocalDate.now()), false);

        }

        indextrack = event.getIndextracker();
        for (Map.Entry<LocalDate, Integer> entry : indextrack.entrySet()) {
            dupindextrack.put(entry.getKey(), entry.getValue());
        }

        if (mNestedView.isAttachedToWindow()) {

            mNestedView.getAdapter().notifyDataSetChanged();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                LocalDate localDate = new LocalDate();
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mNestedView.getLayoutManager();
                if (indextrack.containsKey(LocalDate.now())) {
                    Integer val = indextrack.get(LocalDate.now());
                    expandedfirst = val;
                    topspace = 20;
                    linearLayoutManager.scrollToPositionWithOffset(expandedfirst, 20);
                    EventBus.getDefault().post(new MonthChange(localDate, 0));
                    month = localDate.getDayOfMonth();
                    lastdate = localDate;
                }
            }
        }, 100);


    }

    private void setExpandAndCollapseEnabled(boolean enabled) {
        if (mNestedView.isNestedScrollingEnabled() != enabled) {
            ViewCompat.setNestedScrollingEnabled(mNestedView, enabled);
        }
    }

    @Override
    public boolean isAppBarClosed() {
        return isappbarclosed;
    }

    @Override
    public int appbaroffset() {
        return expandedfirst;
    }

    public void selectdateFromMonthPager(int year, int month, int day) {
        MainActivity.lastdate = new LocalDate(year, month, day);
        LocalDate localDate = new LocalDate();
        String yearstr = MainActivity.lastdate.getYear() == localDate.getYear() ? "" : MainActivity.lastdate.getYear() + "";
        monthname.setText(MainActivity.lastdate.toString("MMMM") + " " + yearstr);
        calendarView.setCurrentmonth(MainActivity.lastdate);
        calendarView.adjustheight();
        mIsExpanded = false;
        mAppBar.setExpanded(false, false);
        EventBus.getDefault().post(new MessageEvent(new LocalDate(year, month, day)));
        monthviewpager.setVisibility(View.GONE);
        mNestedView.setVisibility(View.VISIBLE);
        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) mAppBar.getLayoutParams();
        ((MyAppBarBehavior) layoutParams.getBehavior()).setScrollBehavior(true);
        mAppBar.setElevation(20);
        mArrowImageView.setVisibility(View.VISIBLE);

    }

    @Override
    public boolean isAppBarExpanded() {

        return mAppBarOffset == 0;
    }

    @Override
    public boolean isAppBarIdle() {
        return mAppBarIdle;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String principal = parent.getItemAtPosition(position).toString();
        taskAdapter.filterTasks(principal);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        Toast.makeText(this,"Nothing Selected", Toast.LENGTH_SHORT).show();
    }


    class MonthPageAdapter extends FragmentStatePagerAdapter {
        private ArrayList<MonthModel> monthModels;
        private int singleitemheight;

        public MonthPageAdapter(FragmentManager fragmentManager, ArrayList<MonthModel> monthModels,
                                int singleitemheight) {
            super(fragmentManager);
            this.monthModels = monthModels;
            this.singleitemheight = singleitemheight;
        }

        public ArrayList<MonthModel> getMonthModels() {
            return monthModels;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return monthModels.size();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            return MonthFragment.newInstance(monthModels.get(position).getMonth(), monthModels.get(position).getYear(), monthModels.get(position).getFirstday(), monthModels.get(position).getDayModelArrayList(), alleventlist, singleitemheight);
        }

    }


    public class DateAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {

        LocalDate today = LocalDate.now();

        public ArrayList<EventModel> geteventallList() {
            return eventalllist;
        }

        @Override
        public int getItemViewType(int position) {
            if (position > 1 && eventalllist.get(position).getType() == 0 && getHeaderId(position) == getHeaderId(position - 1))
                return 5;
            if (position > 1 && eventalllist.get(position).getType() == 3 && eventalllist.get(position - 1).getType() == 1)
                return 7;
            if (position + 1 < eventalllist.size() && eventalllist.get(position).getType() == 3 && (eventalllist.get(position + 1).getType() == 1 || eventalllist.get(position + 1).getType() == 0))
                return 6;
            return eventalllist.get(position).getType();
        }

        public int getHeaderItemViewType(int position) {
            return eventalllist.get(position).getType();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 0) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.view_item, parent, false);
                return new ItemViewHolder(view);
            } else if (viewType == 5) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewitemlessspace, parent, false);
                return new ItemViewHolder(view);
            } else if (viewType == 100) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.extraspace, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewType == 200) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.liitlespace, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewType == 1) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.viewlast, parent, false);
                return new EndViewHolder(view);
            } else if (viewType == 2) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.noplanlay, parent, false);
                return new NoplanViewHolder(view);
            } else if (viewType == 1000) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.noplanlittlespace, parent, false);
                return new NoplanViewHolder(view);
            } else if (viewType == 6) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelayextrabottomspace, parent, false);
                return new RangeViewHolder(view);
            } else if (viewType == 7) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelayextratopspace, parent, false);
                return new RangeViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rangelay, parent, false);
                return new RangeViewHolder(view);
            }

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
            int viewtype = getItemViewType(position);
            if (viewtype == 0 || viewtype == 5) {

                ItemViewHolder holder = (ItemViewHolder) viewHolder;
                GradientDrawable shape = new GradientDrawable();
                shape.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.fourdp));
                shape.setColor(eventalllist.get(position).getColor());
                holder.eventtextview.setBackground(shape);
                String eventName = eventalllist.get(position).getEventname();
                holder.eventtextview.setText(eventName);
                int totalTasks;
                int completeTasks;
                String completion;

                Iterator iterator = alleventlist.values().iterator();
                while (iterator.hasNext()) {
                    EventInfo eventInfo = (EventInfo) iterator.next();


                    if (eventName.contains(eventInfo.title)) {
                        Log.i("Appointment", eventInfo.appointmentType + " " + eventInfo.title + "/n" + eventInfo.eventtitles.toString());
                        if (eventInfo.appointmentType.equals("Call")) {
                            totalTasks = ThreadLocalRandom.current().nextInt(1, 10 + 1);
                            completeTasks = ThreadLocalRandom.current().nextInt(1, totalTasks + 1);
                            holder.taskProgressBarSchedule.setVisibility(View.VISIBLE);
                            holder.taskProgressTv.setVisibility(View.VISIBLE);
                            completion = String.valueOf(completeTasks).concat("/").concat(String.valueOf(totalTasks));
                            holder.taskProgressTv.setText(completion);
                            double completionPerc = ((double)completeTasks/(double)totalTasks) * 100;
                            holder.taskProgressBarSchedule.setProgress((int) completionPerc);
                        } else if (eventInfo.appointmentType.equals("Non-Call")) {
                            holder.taskProgressBarSchedule.setVisibility(View.GONE);
                            holder.taskProgressTv.setVisibility(View.GONE);
                        } else if (eventInfo.appointmentType.equals("Interval")) {
                            holder.taskProgressBarSchedule.setVisibility(View.GONE);
                            holder.taskProgressTv.setVisibility(View.GONE);
                            holder.eventtextview.setTextColor(getColor(R.color.colorPrimaryDark));
                            holder.eventtextview.setText("Intv test");
                        }
                    }

                    while (eventInfo.nextnode != null) {
                        eventInfo = eventInfo.nextnode;
                        if (eventName.contains(eventInfo.title)) {
                            if (eventInfo.appointmentType.equals("Call")) {
                                totalTasks = ThreadLocalRandom.current().nextInt(1, 10 + 1);
                                completeTasks = ThreadLocalRandom.current().nextInt(1, totalTasks + 1);
                                holder.taskProgressBarSchedule.setVisibility(View.VISIBLE);
                                holder.taskProgressTv.setVisibility(View.VISIBLE);
                                completion = String.valueOf(completeTasks).concat("/").concat(String.valueOf(totalTasks));
                                holder.taskProgressTv.setText(completion);
                                double completionPerc = ((double)completeTasks/(double)totalTasks) * 100;
                                holder.taskProgressBarSchedule.setProgress((int) completionPerc);
                            } else {
                                holder.taskProgressBarSchedule.setVisibility(View.GONE);
                                holder.taskProgressTv.setVisibility(View.GONE);
                            }
                        }
                    }

                }

                /*if (eventalllist.get(position).)
                holder.taskProgressBarSchedule*/

                //holder.taskProgress.setVisibility(View.GONE);

                /*if (position + 1 < eventalllist.size() && eventalllist.get(position).getLocalDate().equals(today) &&
                        (!eventalllist.get(position + 1).getLocalDate().equals(today) ||
                                eventalllist.get(position + 1).getType() == 100 ||
                                eventalllist.get(position + 1).getType() == 200)) {
                    holder.circle.setVisibility(View.VISIBLE);
                    holder.line.setVisibility(View.VISIBLE);

                } else {
                    holder.circle.setVisibility(View.GONE);
                    holder.line.setVisibility(View.GONE);
                }*/
            } else if (viewtype == 1) {

                EndViewHolder holder = (EndViewHolder) viewHolder;
                holder.eventimageview.setImageResource(monthresource[eventalllist.get(position).getLocalDate().getMonthOfYear() - 1]);
                holder.monthname.setText(eventalllist.get(position).getLocalDate().toString("MMMM YYYY"));
            } else if (viewtype == 2 || viewtype == 100 || viewtype == 200 || viewtype == 1000) {

            } else {
                RangeViewHolder holder = (RangeViewHolder) viewHolder;
                holder.rangetextview.setText(eventalllist.get(position).getEventname().replaceAll("tojigs", ""));
            }

        }

        @Override
        public long getHeaderId(int position) {


            if (eventalllist.get(position).getType() == 1) return position;
            else if (eventalllist.get(position).getType() == 3) return position;
            else if (eventalllist.get(position).getType() == 100) return position;
            else if (eventalllist.get(position).getType() == 200) return position;
            LocalDate localDate = eventalllist.get(position).getLocalDate();
            String uniquestr = "" + localDate.getDayOfMonth() + localDate.getMonthOfYear() + localDate.getYear();
            return Long.parseLong(uniquestr);

        }

        @Override
        public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent, int position) {
            int viewtype = getHeaderItemViewType(position);
            if (viewtype == 2) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todayheader, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewtype == 0 && eventalllist.get(position).getLocalDate().equals(today)) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.todayheader, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else if (viewtype == 1 || viewtype == 3 || viewtype == 100 || viewtype == 200) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.empty, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.headerview, parent, false);
                return new RecyclerView.ViewHolder(view) {
                };
            }

        }

        @Override
        public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
            int viewtype = getHeaderItemViewType(position);
            if (viewtype == 0 || viewtype == 2 || viewtype == 1000) {
                TextView vartextView = holder.itemView.findViewById(R.id.textView9);
                TextView datetextView = holder.itemView.findViewById(R.id.textView10);
                vartextView.setText(var[eventalllist.get(position).getLocalDate().getDayOfWeek() - 1]);
                datetextView.setText(eventalllist.get(position).getLocalDate().getDayOfMonth() + "");
                holder.itemView.setTag(position);
            }
        }

        @Override
        public int getItemCount() {
            return eventalllist.size();
        }

        class ItemViewHolder extends RecyclerView.ViewHolder {
            TextView eventtextview;
            ProgressBar taskProgressBarSchedule;
            TextView taskProgressTv;
            //View circle, line;

            public ItemViewHolder(View itemView) {
                super(itemView);
                eventtextview = itemView.findViewById(R.id.view_item_textview);
                taskProgressBarSchedule = itemView.findViewById(R.id.task_progressbar_schedule);
                taskProgressTv = itemView.findViewById(R.id.progress_tv2);
                eventtextview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (isAppBarExpanded()) {
                            mIsExpanded = !mIsExpanded;
                            mNestedView.stopScroll();
                            mAppBar.setExpanded(mIsExpanded, true);
                            return;
                        }
                        EventInfo eventInfo = alleventlist.get(eventalllist.get(getAdapterPosition()).getLocalDate());
                        String sfs = eventalllist.get(getAdapterPosition()).getEventname();
                        while (eventInfo != null && !sfs.startsWith(eventInfo.title)) {
                            eventInfo = eventInfo.nextnode;
                        }

                        eventnametextview.setText(eventInfo.title);

                        /*if (eventInfo.appointmentType.equals("Call")) {
                            taskProgressCircle.setVisibility(View.VISIBLE);
                            taskProgressTv.setVisibility(View.VISIBLE);
                        } else {
                            taskProgressCircle.setVisibility(View.GONE);
                            taskProgressTv.setVisibility(View.GONE);
                        }*/

                        if (!eventInfo.isallday) {
                            LocalDateTime start = new LocalDateTime(eventInfo.starttime, DateTimeZone.forID(eventInfo.timezone));
                            LocalDateTime end = new LocalDateTime(eventInfo.endtime, DateTimeZone.forID(eventInfo.timezone));
                            String sf = start.toString("a").equals(end.toString("a")) ? "" : "a";
                            String rangetext = daysList[start.getDayOfWeek()] + ", " + start.toString("d MMM") + " · " + start.toString("h:mm " + sf + "") + " - " + end.toString("h:mm a");
                            eventrangetextview.setText(rangetext);
                        } else if (eventInfo.noofdayevent > 1) {
                            LocalDate localDate = new LocalDate(eventInfo.starttime, DateTimeZone.forID(eventInfo.timezone));
                            LocalDate todaydate = LocalDate.now();
                            LocalDate nextday = localDate.plusDays(eventInfo.noofdayevent - 1);
                            if (localDate.getYear() == todaydate.getYear()) {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM");
                                eventrangetextview.setText(rangetext);
                            } else {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM, YYYY");
                                eventrangetextview.setText(rangetext);
                            }
                        } else {
                            LocalDate localDate = new LocalDate(eventInfo.starttime);
                            LocalDate todaydate = LocalDate.now();
                            if (localDate.getYear() == todaydate.getYear()) {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM");
                                eventrangetextview.setText(rangetext);
                            } else {
                                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY");
                                eventrangetextview.setText(rangetext);
                            }
                        }

                        eventnametextview.setVisibility(View.GONE);
                        taskRecyclerView.setVisibility(View.GONE);
                        eventrangetextview.setVisibility(View.GONE);
                        principalSpinner.setVisibility(View.GONE);

                        final View view = mNestedView.getLayoutManager().findViewByPosition(getAdapterPosition());
                        ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                        layoutParams.height = v.getHeight();
                        layoutParams.width = v.getWidth();
                        redlay.setLayoutParams(layoutParams);
                        redlay.setTranslationX(v.getLeft());
                        redlay.setTranslationY(view.getTop() + toolbar.getHeight());
                        redlay.setTranslationZ(0);

                        GradientDrawable shape = new GradientDrawable();
                        shape.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.fourdp));
                        mycolor = eventalllist.get(getAdapterPosition()).getColor();
                        shape.setColor(mycolor);
                        redlay.setBackground(shape);

                        ValueAnimator animwidth = ValueAnimator.ofInt(redlay.getWidth(), getDevicewidth());
                        animwidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                                layoutParams.width = val;
                                redlay.setLayoutParams(layoutParams);
                            }
                        });
                        animwidth.setDuration(300);

                        ValueAnimator animheight = ValueAnimator.ofInt(redlay.getHeight(), getDeviceHeight());
                        animheight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                int val = (Integer) valueAnimator.getAnimatedValue();
                                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                                layoutParams.height = val;
                                redlay.setLayoutParams(layoutParams);
                                if (redlay.getTranslationZ() == 0 && valueAnimator.getAnimatedFraction() > 0.15) {
                                    redlay.setBackgroundColor(Color.WHITE);
                                    shadow.setVisibility(View.VISIBLE);
                                    redlay.setTranslationZ(getResources().getDimensionPixelSize(R.dimen.tendp));
                                }
                            }
                        });
                        animheight.setDuration(300);

                        ValueAnimator animx = ValueAnimator.ofFloat(redlay.getTranslationX(), 0);
                        animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                Float val = (Float) valueAnimator.getAnimatedValue();
                                redlay.setTranslationX(val);
                            }
                        });
                        animx.setDuration(300);

                        ValueAnimator animy = ValueAnimator.ofFloat(redlay.getTranslationY(), 0);
                        animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                Float val = (Float) valueAnimator.getAnimatedValue();
                                redlay.setTranslationY(val);
                            }
                        });
                        animy.setDuration(300);

                        animheight.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        eventnametextview.setVisibility(View.VISIBLE);
                                        eventrangetextview.setVisibility(View.VISIBLE);
                                        taskRecyclerView.setVisibility(View.VISIBLE);
                                        principalSpinner.setVisibility(View.VISIBLE);
                                        taskProgressBarCallApp.setVisibility(View.VISIBLE);
                                        ObjectAnimator animation = ObjectAnimator.ofInt(taskProgressBarCallApp, "progress", 0, 40);
                                        animation.setDuration(1400);
                                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                                        // animation.setStartDelay(2500);
                                        animation.start();
                                    }
                                }, 150);

                            }
                        });
                        animwidth.start();
                        animheight.start();
                        animy.start();
                        animx.start();
                        eventview = v;
                        fullview = view;

                    }
                });
                /*circle = itemView.findViewById(R.id.circle);
                line = itemView.findViewById(R.id.line);*/
            }
        }

        class EndViewHolder extends RecyclerView.ViewHolder {

            ScrollParallaxImageView eventimageview;
            TextView monthname;

            public EndViewHolder(View itemView) {
                super(itemView);
                eventimageview = itemView.findViewById(R.id.imageView);
                eventimageview.setParallaxStyles(new VerticalMovingStyle());
                monthname = itemView.findViewById(R.id.textView11);
            }
        }

        class NoplanViewHolder extends RecyclerView.ViewHolder {

            TextView noplantextview;

            public NoplanViewHolder(View itemView) {
                super(itemView);
                noplantextview = itemView.findViewById(R.id.view_noplan_textview);
            }
        }

        class RangeViewHolder extends RecyclerView.ViewHolder {

            TextView rangetextview;

            public RangeViewHolder(View itemView) {
                super(itemView);
                rangetextview = itemView.findViewById(R.id.view_range_textview);
            }
        }
    }


    ///////////////////////////////////weekview implemention///////////////////////////////////////

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        if (!isgivepermission) return new ArrayList<>();
        LocalDate initial = new LocalDate(newYear, newMonth, 1);
        int length = initial.dayOfMonth().getMaximumValue();
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

        for (int i = 1; i <= length; i++) {
            LocalDate localDate = new LocalDate(newYear, newMonth, i);
            if (alleventlist.containsKey(localDate)) {
                EventInfo eventInfo = alleventlist.get(localDate);
                while (eventInfo != null) {
                    Calendar startTime = Calendar.getInstance(TimeZone.getTimeZone(eventInfo.timezone));
                    startTime.setTimeInMillis(eventInfo.starttime);
                    Calendar endTime = (Calendar) Calendar.getInstance(TimeZone.getTimeZone(eventInfo.timezone));
                    endTime.setTimeInMillis(eventInfo.endtime);
                    int dau = Days.daysBetween(new LocalDate(eventInfo.endtime), new LocalDate(eventInfo.starttime)).getDays();

                    WeekViewEvent event = new WeekViewEvent(eventInfo.id, eventInfo.title, startTime, endTime, eventInfo.accountname);

                    event.setAllDay(eventInfo.isallday);
                    event.setColor(eventInfo.eventcolor);
//                    if (eventInfo.isallday)event.setColor(getResources().getColor(R.color.event_color_04));
//                    else event.setColor(getResources().getColor(R.color.event_color_02));
                    events.add(event);
                    eventInfo = eventInfo.nextnode;
                }
            }
        }


        return events;
    }


    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretday(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (mWeekView.getNumberOfVisibleDays() == 7)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase();
            }

            @Override
            public String interpretDate(Calendar date) {
                int dayOfMonth = date.get(Calendar.DAY_OF_MONTH);


                return dayOfMonth + "";
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

        if (isAppBarExpanded()) {
            mIsExpanded = !mIsExpanded;
            mNestedView.stopScroll();

            mAppBar.setExpanded(mIsExpanded, true);
            return;
        }
        eventnametextview.setText(event.getName());
        if (event.isAllDay() == false) {
            LocalDateTime start = new LocalDateTime(event.getStartTime().getTimeInMillis(), DateTimeZone.forTimeZone(event.getStartTime().getTimeZone()));
            LocalDateTime end = new LocalDateTime(event.getEndTime().getTimeInMillis(), DateTimeZone.forTimeZone(event.getEndTime().getTimeZone()));
            String sf = start.toString("a").equals(end.toString("a")) ? "" : "a";
            String rangetext = daysList[start.getDayOfWeek()] + ", " + start.toString("d MMM") + " · " + start.toString("h:mm " + sf + "") + " - " + end.toString("h:mm a");
            eventrangetextview.setText(rangetext);
        } else if (event.isIsmoreday()) {
            LocalDate localDate = new LocalDate(event.getActualstart().getTimeInMillis(), DateTimeZone.forTimeZone(event.getStartTime().getTimeZone()));
            LocalDate todaydate = LocalDate.now();
            LocalDate nextday = localDate.plusDays((int) (event.getNoofday() - 1));
            if (localDate.getYear() == todaydate.getYear()) {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM");
                eventrangetextview.setText(rangetext);
            } else {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY") + " - " + daysList[nextday.getDayOfWeek()] + ", " + nextday.toString("d MMM, YYYY");
                eventrangetextview.setText(rangetext);
            }
        } else {
            LocalDate localDate = new LocalDate(event.getStartTime().getTimeInMillis());
            LocalDate todaydate = LocalDate.now();
            if (localDate.getYear() == todaydate.getYear()) {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM");
                eventrangetextview.setText(rangetext);
            } else {
                String rangetext = daysList[localDate.getDayOfWeek()] + ", " + localDate.toString("d MMM, YYYY");
                eventrangetextview.setText(rangetext);
            }
        }

        eventnametextview.setVisibility(View.GONE);
        eventrangetextview.setVisibility(View.GONE);

        final View view = new View(this);
        ViewGroup.LayoutParams layoutParams1 = new ViewGroup.LayoutParams((int) eventRect.width(), (int) eventRect.height());
        view.setLeft((int) eventRect.left);
        view.setTop((int) eventRect.top);
        view.setRight((int) eventRect.right);
        view.setBottom((int) eventRect.bottom);
        view.setLayoutParams(layoutParams1);


        redlay.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
        layoutParams.height = (int) eventRect.height();
        layoutParams.width = (int) eventRect.width();
        redlay.setLayoutParams(layoutParams);
        redlay.setTranslationX(eventRect.left);
        redlay.setTranslationY(eventRect.top + toolbar.getHeight());

        if (event.getColor() != 0) {
            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.fourdp));
            mycolor = event.getColor();
            shape.setColor(mycolor);
            redlay.setBackground(shape);

        } else {
            GradientDrawable shape = new GradientDrawable();
            shape.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.fourdp));
            mycolor = Color.parseColor("#009688");
            shape.setColor(mycolor);
            redlay.setBackground(shape);
        }

        //  GradientDrawable drawable = (GradientDrawable) holder.eventtextview.getBackground();

//               if (eventalllist.get(position).getType()==0)drawable.setColor(eventalllist.get(position).getColor());
//               else drawable.setColor(Color.BLACK);
        redlay.setTranslationZ(0);

        ValueAnimator animwidth = ValueAnimator.ofInt(redlay.getWidth(), getDevicewidth());
        animwidth.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.width = val;
                redlay.setLayoutParams(layoutParams);
            }
        });
        animwidth.setDuration(300);

        ValueAnimator animheight = ValueAnimator.ofInt(redlay.getHeight(), getDeviceHeight());
        animheight.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = redlay.getLayoutParams();
                layoutParams.height = val;
                redlay.setLayoutParams(layoutParams);
                if (redlay.getTranslationZ() == 0 && valueAnimator.getAnimatedFraction() > 0.2) {
                    redlay.setBackgroundColor(Color.WHITE);
                    shadow.setVisibility(View.VISIBLE);
                    redlay.setTranslationZ(getResources().getDimensionPixelSize(R.dimen.tendp));
                }
            }
        });
        animheight.setDuration(300);

        ValueAnimator animx = ValueAnimator.ofFloat(redlay.getTranslationX(), 0);
        animx.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                redlay.setTranslationX(val);
            }
        });
        animx.setDuration(300);

        ValueAnimator animy = ValueAnimator.ofFloat(redlay.getTranslationY(), 0);
        animy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                redlay.setTranslationY(val);
            }
        });
        animy.setDuration(300);

        animheight.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        eventnametextview.setVisibility(View.VISIBLE);
                        eventrangetextview.setVisibility(View.VISIBLE);
                        taskRecyclerView.setVisibility(View.VISIBLE);
                        principalSpinner.setVisibility(View.VISIBLE);
                        //progressBar.setVisibility(View.VISIBLE);
                        ObjectAnimator animation = ObjectAnimator.ofInt(taskProgressBarCallApp, "progress", 0, 40);
                        animation.setDuration(1400);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        // animation.setStartDelay(2500);
                        animation.start();

                    }
                }, 150);

            }
        });
        animwidth.start();
        animheight.start();
        animy.start();
        animx.start();
        eventview = view;
        fullview = view;
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {


        if (weekviewcontainer.getVisibility() == View.GONE || !isgivepermission) return;
        if (isAppBarClosed()) {

            LocalDate localDate = new LocalDate(newFirstVisibleDay.get(Calendar.YEAR), newFirstVisibleDay.get(Calendar.MONTH) + 1, newFirstVisibleDay.get(Calendar.DAY_OF_MONTH));
            MainActivity.lastdate = localDate;

            String year = localDate.getYear() == LocalDate.now().getYear() ? "" : localDate.getYear() + "";
            if (!monthname.getText().equals(localDate.toString("MMM") + " " + year)) {
                MainActivity.lastdate = localDate;
                calendarView.setCurrentmonth(localDate);
                calendarView.adjustheight();
                mIsExpanded = false;
                mAppBar.setExpanded(false, false);
                monthname.setText(localDate.toString("MMM") + " " + year);

            }

            // EventBus.getDefault().post(new MessageEvent(new LocalDate(monthModel.getYear(),monthModel.getMonth(),1)));
            // if (monthChangeListner!=null)monthChangeListner.onmonthChange(myPagerAdapter.monthModels.get(position));
        } else {
            // calendarView.setCurrentmonth(i);
        }
    }
}
