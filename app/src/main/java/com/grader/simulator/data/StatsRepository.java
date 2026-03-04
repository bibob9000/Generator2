package com.grader.simulator.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Calendar;

public class StatsRepository {

    private static final String PREFS_NAME      = "grade_simulator_stats";
    private static final String KEY_TOTAL        = "total_grades";
    private static final String KEY_TODAY_COUNT  = "today_count";
    private static final String KEY_TODAY_DATE   = "today_date"; 
    private static final String KEY_WEEK_COUNT   = "week_count";
    private static final String KEY_WEEK_START   = "week_start";  
    private static final String KEY_MONTH_COUNT  = "month_count";
    private static final String KEY_MONTH_PERIOD = "month_period"; 
    private static final String KEY_DAILY_RECORD = "daily_record"; 

    private final SharedPreferences prefs;

    public StatsRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }


    public void addGrade() {
        long now = System.currentTimeMillis();
        SharedPreferences.Editor editor = prefs.edit();

        int total = prefs.getInt(KEY_TOTAL, 0) + 1;
        editor.putInt(KEY_TOTAL, total);

        String todayKey = getTodayKey();
        if (!prefs.getString(KEY_TODAY_DATE, "").equals(todayKey)) {
            int prevToday = prefs.getInt(KEY_TODAY_COUNT, 0);
            int record    = prefs.getInt(KEY_DAILY_RECORD, 0);
            if (prevToday > record) {
                editor.putInt(KEY_DAILY_RECORD, prevToday);
            }
            editor.putInt(KEY_TODAY_COUNT, 1);
            editor.putString(KEY_TODAY_DATE, todayKey);
        } else {
            editor.putInt(KEY_TODAY_COUNT, prefs.getInt(KEY_TODAY_COUNT, 0) + 1);
        }

        String weekStart = getWeekStartKey();
        if (!prefs.getString(KEY_WEEK_START, "").equals(weekStart)) {
            editor.putInt(KEY_WEEK_COUNT, 1);
            editor.putString(KEY_WEEK_START, weekStart);
        } else {
            editor.putInt(KEY_WEEK_COUNT, prefs.getInt(KEY_WEEK_COUNT, 0) + 1);
        }

        String monthPeriod = getMonthPeriodKey();
        if (!prefs.getString(KEY_MONTH_PERIOD, "").equals(monthPeriod)) {
            editor.putInt(KEY_MONTH_COUNT, 1);
            editor.putString(KEY_MONTH_PERIOD, monthPeriod);
        } else {
            editor.putInt(KEY_MONTH_COUNT, prefs.getInt(KEY_MONTH_COUNT, 0) + 1);
        }

        editor.apply();
    }

    public int getTotal() {
        return prefs.getInt(KEY_TOTAL, 0);
    }

    public int getTodayCount() {
        if (!prefs.getString(KEY_TODAY_DATE, "").equals(getTodayKey())) return 0;
        return prefs.getInt(KEY_TODAY_COUNT, 0);
    }

    public int getWeekCount() {
        if (!prefs.getString(KEY_WEEK_START, "").equals(getWeekStartKey())) return 0;
        return prefs.getInt(KEY_WEEK_COUNT, 0);
    }

    public int getMonthCount() {
        if (!prefs.getString(KEY_MONTH_PERIOD, "").equals(getMonthPeriodKey())) return 0;
        return prefs.getInt(KEY_MONTH_COUNT, 0);
    }

    public int getDailyRecord() {
        int record  = prefs.getInt(KEY_DAILY_RECORD, 0);
        int today   = getTodayCount();
        return Math.max(record, today);
    }

    private String getTodayKey() {
        Calendar c = Calendar.getInstance();
        return String.format("%d%02d%02d",
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH));
    }

    private String getWeekStartKey() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return String.format("%d%02d%02d",
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1,
                c.get(Calendar.DAY_OF_MONTH));
    }

    private String getMonthPeriodKey() {
        Calendar c = Calendar.getInstance();
        return String.format("%d%02d",
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH) + 1);
    }
}
