package com.grader.simulator.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;

public class VibrationHelper {

    public static void vibrateGrade(Context context) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] timings    = {0, 80, 60, 120};
            int[]  amplitudes = {0, 200, 0, 255};
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1));
        } else {
            vibrator.vibrate(new long[]{0, 80, 60, 120}, -1);
        }
    }

    public static void vibrateAchievement(Context context) {
        Vibrator vibrator = getVibrator(context);
        if (vibrator == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            long[] timings    = {0, 150, 80, 200, 80, 300, 100, 400};
            int[]  amplitudes = {0, 128,  0, 200,  0, 230,   0, 255};
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1));
        } else {
            vibrator.vibrate(new long[]{0, 150, 80, 200, 80, 300, 100, 400}, -1);
        }
    }

    private static Vibrator getVibrator(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager manager =
                (VibratorManager) context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE);
            return manager != null ? manager.getDefaultVibrator() : null;
        } else {
            return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
    }
}
