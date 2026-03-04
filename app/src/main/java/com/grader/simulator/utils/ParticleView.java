package com.grader.simulator.utils;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class ParticleView extends View {

    private static final int PARTICLE_COUNT = 40;
    private static final long ANIMATION_DURATION = 900; // ms

    private final List<Particle> particles = new ArrayList<>();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private ValueAnimator animator;

    private final int[] EXPLOSION_COLORS = {
        Color.parseColor("#FF0000"),
        Color.parseColor("#FF4400"),
        Color.parseColor("#FF8800"),
        Color.parseColor("#FFCC00"),
        Color.parseColor("#FFFFFF"),
        Color.parseColor("#FF2222"),
    };

    public ParticleView(Context context) {
        super(context);
    }

    public ParticleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void explode(float cx, float cy) {
        if (animator != null && animator.isRunning()) {
            animator.cancel();
        }
        particles.clear();

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            Particle p = new Particle();
            p.x          = cx;
            p.y          = cy;
            double angle = random.nextDouble() * Math.PI * 2;
            float  speed = 8f + random.nextFloat() * 22f;
            p.vx         = (float) (Math.cos(angle) * speed);
            p.vy         = (float) (Math.sin(angle) * speed);
            p.radius     = 4f + random.nextFloat() * 10f;
            p.color      = EXPLOSION_COLORS[random.nextInt(EXPLOSION_COLORS.length)];
            p.alpha      = 255;
            p.isStar     = random.nextBoolean();
            particles.add(p);
        }

        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(ANIMATION_DURATION);
        animator.setInterpolator(new DecelerateInterpolator(1.5f));
        animator.addUpdateListener(animation -> {
            float progress = animation.getAnimatedFraction();
            for (Particle p : particles) {
                p.x      += p.vx * (1 - progress);
                p.y      += p.vy * (1 - progress) + progress * 3f;
                p.alpha   = (int) (255 * (1f - progress));
                p.radius *= 0.985f;
            }
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (particles.isEmpty()) return;

        for (Particle p : particles) {
            if (p.alpha <= 0 || p.radius < 0.5f) continue;
            paint.setColor(p.color);
            paint.setAlpha(p.alpha);

            if (p.isStar) {
                paint.setStrokeWidth(p.radius / 2f);
                paint.setStyle(Paint.Style.STROKE);
                float r = p.radius;
                canvas.drawLine(p.x - r, p.y - r, p.x + r, p.y + r, paint);
                canvas.drawLine(p.x + r, p.y - r, p.x - r, p.y + r, paint);
            } else {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(p.x, p.y, p.radius, paint);
            }
        }
    }

    private static class Particle {
        float   x, y;
        float   vx, vy;
        float   radius;
        int     color;
        int     alpha;
        boolean isStar;
    }
}
