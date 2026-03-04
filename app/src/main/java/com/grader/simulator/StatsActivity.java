package com.grader.simulator;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.grader.simulator.data.StatsRepository;

public class StatsActivity extends AppCompatActivity {

    private StatsRepository statsRepository;

    private TextView tvStatsToday;
    private TextView tvStatsWeek;
    private TextView tvStatsMonth;
    private TextView tvStatsAllTime;
    private TextView tvRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        statsRepository = new StatsRepository(this);

        tvStatsToday   = findViewById(R.id.tvStatsToday);
        tvStatsWeek    = findViewById(R.id.tvStatsWeek);
        tvStatsMonth   = findViewById(R.id.tvStatsMonth);
        tvStatsAllTime = findViewById(R.id.tvStatsAllTime);
        tvRecord       = findViewById(R.id.tvRecord);

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        loadAndDisplayStats();
    }

    private void loadAndDisplayStats() {
        int today   = statsRepository.getTodayCount();
        int week    = statsRepository.getWeekCount();
        int month   = statsRepository.getMonthCount();
        int allTime = statsRepository.getTotal();
        int record  = statsRepository.getDailyRecord();

        // Анимируем числа от 0 до реального значения — зрелищно!
        animateNumber(tvStatsToday,   0, today,   400);
        animateNumber(tvStatsWeek,    0, week,    600);
        animateNumber(tvStatsMonth,   0, month,   800);
        animateNumber(tvStatsAllTime, 0, allTime, 1000);
        animateNumber(tvRecord,       0, record,  700);
    }

    private void animateNumber(TextView textView, int from, int to, long duration) {
        if (to == 0) {
            textView.setText("0");
            return;
        }
        ValueAnimator animator = ValueAnimator.ofInt(from, to);
        animator.setDuration(duration);
        animator.addUpdateListener(anim ->
            textView.setText(String.valueOf((int) anim.getAnimatedValue()))
        );
        animator.start();
    }
}
