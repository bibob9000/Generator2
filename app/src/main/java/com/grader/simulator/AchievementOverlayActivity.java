package com.grader.simulator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.grader.simulator.utils.VibrationHelper;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.xml.KonfettiView;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class AchievementOverlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);

        String emoji = getIntent().getStringExtra("rank_emoji");
        String title = getIntent().getStringExtra("rank_title");
        String desc  = getIntent().getStringExtra("rank_desc");

        TextView tvEmoji = findViewById(R.id.tvAchievementEmoji);
        TextView tvTitle = findViewById(R.id.tvAchievementTitle);
        TextView tvDesc  = findViewById(R.id.tvAchievementDesc);

        if (emoji != null) tvEmoji.setText(emoji);
        if (title != null) tvTitle.setText(title.toUpperCase());
        if (desc  != null) tvDesc.setText(desc);

        findViewById(R.id.achievementRoot).setOnClickListener(v -> dismissWithAnimation());

        startAchievementShow();
    }

    private void startAchievementShow() {
        CardView card = findViewById(R.id.cardAchievement);

        card.setTranslationY(800f);
        card.setAlpha(0f);
        card.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(600)
            .setInterpolator(new BounceInterpolator())
            .withStartAction(this::launchConfetti)
            .start();

        card.postDelayed(() -> VibrationHelper.vibrateAchievement(this), 300);

        card.postDelayed(this::pulseTitle, 700);
    }


    private void pulseTitle() {
        TextView tvTitle = findViewById(R.id.tvAchievementTitle);

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(tvTitle, "scaleX", 1f, 1.15f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(tvTitle, "scaleY", 1f, 1.15f, 1f);
        scaleX.setDuration(400);
        scaleY.setDuration(400);
        scaleX.setInterpolator(new OvershootInterpolator());
        scaleY.setInterpolator(new OvershootInterpolator());
        scaleX.setRepeatCount(3);
        scaleY.setRepeatCount(3);

        AnimatorSet pulseSet = new AnimatorSet();
        pulseSet.playTogether(scaleX, scaleY);
        pulseSet.start();
    }

    private void launchConfetti() {
        KonfettiView konfettiView = findViewById(R.id.konfettiViewAchievement);

        List<Integer> colors = Arrays.asList(
            Color.parseColor("#FF0000"),
            Color.parseColor("#FFD700"),
            Color.parseColor("#FF6600"),
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#FF00FF"),
            Color.parseColor("#00FFFF")
        );

        Party partyLeft = new PartyFactory(
            new Emitter(4, TimeUnit.SECONDS).perSecond(60)
        )
        .colors(colors)
        .spread(60)
        .setSpeedBetween(4f, 14f)
        .position(new Position.Relative(0.0, 0.5))
        .build();

        Party partyRight = new PartyFactory(
            new Emitter(4, TimeUnit.SECONDS).perSecond(60)
        )
        .colors(colors)
        .spread(60)
        .setSpeedBetween(4f, 14f)
        .position(new Position.Relative(1.0, 0.5))
        .build();

        Party partyTop = new PartyFactory(
            new Emitter(2, TimeUnit.SECONDS).perSecond(40)
        )
        .colors(colors)
        .spread(360)
        .setSpeedBetween(2f, 8f)
        .position(new Position.Relative(0.5, 0.0))
        .build();

        konfettiView.start(partyLeft, partyRight, partyTop);
    }

    private void dismissWithAnimation() {
        CardView card = findViewById(R.id.cardAchievement);
        card.animate()
            .alpha(0f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(250)
            .withEndAction(this::finish)
            .start();
    }

    @Override
    public void onBackPressed() {
        dismissWithAnimation();
    }
}
