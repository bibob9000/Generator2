package com.grader.simulator;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.CycleInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import android.widget.Button;
import android.widget.TextView;


public class LoginActivity extends AppCompatActivity {

    // ФИО единственного легитимного пользователя
    private static final String AUTHORIZED_NAME = "аферов андрей алексеевич";

    private TextInputEditText etName;
    private TextInputLayout   tilName;
    private TextView          tvError;
    private Button            btnLogin;

    // Счётчик неудачных попыток для более злобных сообщений
    private int failCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etName   = findViewById(R.id.etName);
        tilName  = findViewById(R.id.tilName);
        tvError  = findViewById(R.id.tvError);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> attemptLogin());

        etName.setOnEditorActionListener((v, actionId, event) -> {
            attemptLogin();
            return true;
        });
    }

    private void attemptLogin() {
        String input = etName.getText() != null
                ? etName.getText().toString().trim()
                : "";

        if (TextUtils.isEmpty(input)) {
            showError(getString(R.string.login_error_empty));
            shakeField();
            return;
        }

        String normalized = input.replaceAll("\\s+", " ").toLowerCase().trim();

        if (normalized.equals(AUTHORIZED_NAME)) {
            // ✅ Добро пожаловать, Повелитель!
            onLoginSuccess();
        } else {
            // ❌ Самозванец!
            failCount++;
            showError(getErrorMessage(normalized));
            shakeField();
        }
    }


    private String getErrorMessage(String input) {
        if (input.contains("аферов") || input.contains("андрей") || input.contains("алексеевич")) {
            return getString(R.string.login_error_almost);
        }

        switch (failCount % 5) {
            case 1:
                return "🚫 Ты не тот, за кого себя выдаёшь, самозванец!";
            case 2:
                return "🤔 Серьёзно? Ещё раз попробуй, мошенник.";
            case 3:
                return "😂 Хаха, 67. Отдел безопасности уже уведомлён.";
            case 4:
                return "👮 Ещё одна попытка и поставим двойку тебе.";
            default:
                return "☠️ Система тебя не знает. Система тебя отвергает.";
        }
    }

    private void onLoginSuccess() {
        tvError.setVisibility(View.INVISIBLE);
        tilName.setBoxStrokeColor(getColor(R.color.colorGold));

        // Маленькая анимация успеха перед переходом
        btnLogin.setText("✅ ДОБРО ПОЖАЛОВАТЬ, КАРАТЕЛЬ!");
        btnLogin.setEnabled(false);

        btnLogin.postDelayed(() -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, 600);
    }

    private void showError(String message) {
        tvError.setText(message);
        tvError.setVisibility(View.VISIBLE);

        tvError.setAlpha(0f);
        tvError.animate().alpha(1f).setDuration(300).start();
    }

    private void shakeField() {
        ObjectAnimator shakeX = ObjectAnimator.ofFloat(
                tilName, "translationX",
                0f, 20f, -20f, 16f, -16f, 10f, -10f, 0f
        );
        shakeX.setDuration(400);
        shakeX.setInterpolator(new CycleInterpolator(1));
        shakeX.start();
    }
}
