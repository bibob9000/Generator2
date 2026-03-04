package com.grader.simulator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.grader.simulator.data.StatsRepository;
import com.grader.simulator.model.Rank;
import com.grader.simulator.utils.ParticleView;
import com.grader.simulator.utils.VibrationHelper;

import nl.dionsegijn.konfetti.core.Party;
import nl.dionsegijn.konfetti.core.PartyFactory;
import nl.dionsegijn.konfetti.core.Position;
import nl.dionsegijn.konfetti.core.emitter.Emitter;
import nl.dionsegijn.konfetti.xml.KonfettiView;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    // ——— Views ———
    private Button       btnGiveGrade;
    private TextView     tvCounter;
    private TextView     tvRank;
    private TextView     tvNextRank;
    private TextView     tvFeedbackMessage;
    private ParticleView particleView;
    private KonfettiView konfettiView;
    private View         rootLayout;

    private StatsRepository statsRepository;
    private final Random    random = new Random();

    private String[] gradeMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        statsRepository = new StatsRepository(this);
        gradeMessages   = getResources().getStringArray(R.array.grade_messages);

        updateUI();

        btnGiveGrade.setOnClickListener(v -> onGiveGradeClicked());
        findViewById(R.id.btnStats).setOnClickListener(v -> openStats());
    }

    private void initViews() {
        btnGiveGrade      = findViewById(R.id.btnGiveGrade);
        tvCounter         = findViewById(R.id.tvCounter);
        tvRank            = findViewById(R.id.tvRank);
        tvNextRank        = findViewById(R.id.tvNextRank);
        tvFeedbackMessage = findViewById(R.id.tvFeedbackMessage);
        particleView      = findViewById(R.id.particleView);
        konfettiView      = findViewById(R.id.konfettiView);
        rootLayout        = findViewById(R.id.rootLayout);
    }

    private void onGiveGradeClicked() {
        int previousTotal = statsRepository.getTotal();

        // 1. Сохраняем двойку
        statsRepository.addGrade();
        int newTotal = statsRepository.getTotal();

        // 2. Вибрация — телефон должен почувствовать вину
        VibrationHelper.vibrateGrade(this);

        // 3. Анимация кнопки — удар печатью
        animateGradeButton();

        // 4. Взрыв частиц из центра кнопки
        triggerParticleExplosion();

        // 5. Обновляем счётчик с анимацией
        animateCounterUpdate(previousTotal, newTotal);

        // 6. Показываем случайное сообщение
        showRandomFeedbackMessage();

        // 7. Проверяем новое звание
        Rank newRank = Rank.checkNewRankUnlocked(previousTotal, newTotal);
        if (newRank != null) {
            // Небольшая задержка — сначала показываем счётчик, потом фейерверк
            rootLayout.postDelayed(() -> triggerAchievementUnlocked(newRank), 500);
        }

        // 8. Обновляем UI звания
        updateRankUI(newTotal);
    }

    private void animateGradeButton() {
        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(btnGiveGrade, "scaleX", 1f, 0.88f);
        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(btnGiveGrade, "scaleY", 1f, 0.88f);
        ObjectAnimator scaleUpX   = ObjectAnimator.ofFloat(btnGiveGrade, "scaleX", 0.88f, 1f);
        ObjectAnimator scaleUpY   = ObjectAnimator.ofFloat(btnGiveGrade, "scaleY", 0.88f, 1f);

        scaleDownX.setDuration(80);
        scaleDownY.setDuration(80);
        scaleUpX.setDuration(350);
        scaleUpY.setDuration(350);
        scaleUpX.setInterpolator(new OvershootInterpolator(3f));
        scaleUpY.setInterpolator(new OvershootInterpolator(3f));

        AnimatorSet down = new AnimatorSet();
        down.playTogether(scaleDownX, scaleDownY);

        AnimatorSet up = new AnimatorSet();
        up.playTogether(scaleUpX, scaleUpY);

        AnimatorSet full = new AnimatorSet();
        full.playSequentially(down, up);
        full.start();

        // Вспышка — экран слегка краснеет
        flashScreen();
    }


    private void flashScreen() {
        ValueAnimator flashAnim = ValueAnimator.ofArgb(
                Color.parseColor("#33FF0000"),  //  Красный
                Color.parseColor("#000D0D0D")   // Прозрачный
        );
        flashAnim.setDuration(300);
        flashAnim.addUpdateListener(anim -> {
            rootLayout.setBackgroundColor((int) anim.getAnimatedValue());
        });
        flashAnim.start();
    }


    private void triggerParticleExplosion() {
        // Координаты центра кнопки
        int[] btnLocation = new int[2];
        btnGiveGrade.getLocationInWindow(btnLocation);
        float cx = btnLocation[0] + btnGiveGrade.getWidth() / 2f;
        float cy = btnLocation[1] + btnGiveGrade.getHeight() / 2f;

        // Конвертируем в координаты ParticleView
        int[] pvLocation = new int[2];
        particleView.getLocationInWindow(pvLocation);
        float relX = cx - pvLocation[0];
        float relY = cy - pvLocation[1];

        particleView.explode(relX, relY);
    }

    private void animateCounterUpdate(int from, int to) {
        ValueAnimator countAnim = ValueAnimator.ofInt(from, to);
        countAnim.setDuration(200);
        countAnim.addUpdateListener(anim ->
            tvCounter.setText(String.valueOf(anim.getAnimatedValue()))
        );

        ObjectAnimator jumpUp   = ObjectAnimator.ofFloat(tvCounter, "translationY", 0f, -20f);
        ObjectAnimator jumpDown = ObjectAnimator.ofFloat(tvCounter, "translationY", -20f, 0f);
        jumpUp.setDuration(80);
        jumpDown.setDuration(200);
        jumpDown.setInterpolator(new BounceInterpolator());

        AnimatorSet jumpSet = new AnimatorSet();
        jumpSet.playSequentially(jumpUp, jumpDown);
        jumpSet.start();
        countAnim.start();
    }

    private void showRandomFeedbackMessage() {
        String message = gradeMessages[random.nextInt(gradeMessages.length)];
        tvFeedbackMessage.setText(message);

        tvFeedbackMessage.animate()
            .alpha(1f)
            .setDuration(200)
            .withEndAction(() ->
                tvFeedbackMessage.animate()
                    .alpha(0f)
                    .setStartDelay(1500)
                    .setDuration(500)
                    .start()
            )
            .start();
    }

    private void triggerAchievementUnlocked(Rank newRank) {
        // 1. Мощная вибрация
        VibrationHelper.vibrateAchievement(this);

        // 2. Конфетти — красное, потому что мы злодеи
        launchAchievementConfetti();

        // 3. Открываем полноэкранный оверлей достижения
        rootLayout.postDelayed(() -> {
            Intent intent = new Intent(MainActivity.this, AchievementOverlayActivity.class);
            intent.putExtra("rank_emoji", newRank.emoji);
            intent.putExtra("rank_title", newRank.title);
            intent.putExtra("rank_desc",  newRank.description);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }, 800);
    }

    private void launchAchievementConfetti() {
        // Конфетти из конфетти-библиотеки Konfetti
        // Красно-золотой дождь с небес злодея
        List<Integer> colors = Arrays.asList(
            Color.parseColor("#FF0000"),
            Color.parseColor("#FFD700"),
            Color.parseColor("#FF4400"),
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#FF8800")
        );

        Party party = new PartyFactory(
            new Emitter(3, TimeUnit.SECONDS).perSecond(80)
        )
        .colors(colors)
        .spread(360)
        .setSpeedBetween(2f, 10f)
        .position(new Position.Relative(0.5, 0.0))
        .build();

        konfettiView.start(party);
    }

    private void updateUI() {
        int total = statsRepository.getTotal();
        tvCounter.setText(String.valueOf(total));
        updateRankUI(total);
    }

    private void updateRankUI(int total) {
        Rank current = Rank.getCurrentRank(total);
        tvRank.setText(current.emoji + " " + current.title);
        tvNextRank.setText(Rank.getProgressText(total));
    }

    private void openStats() {
        startActivity(new Intent(this, StatsActivity.class));
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Обновляем UI при возврате из статистики или оверлея
        updateUI();
    }
}
