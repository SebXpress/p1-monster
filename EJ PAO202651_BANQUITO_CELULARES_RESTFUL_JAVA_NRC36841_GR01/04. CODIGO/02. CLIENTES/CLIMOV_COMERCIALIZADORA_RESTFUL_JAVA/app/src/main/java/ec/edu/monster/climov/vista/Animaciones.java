package ec.edu.monster.climov.vista;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

/**
 * Animaciones reutilizables de la app.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class Animaciones {

    private Animaciones() {
    }

    /** El Monster flota arriba y abajo de forma continua. */
    public static void flotar(View v) {
        ObjectAnimator a = ObjectAnimator.ofFloat(v, "translationY", 0f, -18f);
        a.setDuration(1600);
        a.setRepeatMode(ValueAnimator.REVERSE);
        a.setRepeatCount(ValueAnimator.INFINITE);
        a.start();
    }

    /** Sacude la vista (error). */
    public static void sacudir(View v) {
        ObjectAnimator.ofFloat(v, "translationX", 0, -24, 24, -16, 16, -8, 8, 0).setDuration(450).start();
    }

    /** Aparece desde abajo con desvanecido. */
    public static void aparecer(View v, long retraso) {
        v.setAlpha(0f);
        v.setTranslationY(40f);
        v.animate().alpha(1f).translationY(0f).setStartDelay(retraso).setDuration(400)
                .setInterpolator(new DecelerateInterpolator()).start();
    }

    /** Efecto de "latido" al mostrar el resultado. */
    public static void latido(View v) {
        v.setScaleX(0.92f);
        v.setScaleY(0.92f);
        v.animate().scaleX(1f).scaleY(1f).setDuration(450).setInterpolator(new android.view.animation.OvershootInterpolator()).start();
    }

    /** El número sube contando hasta su valor final. */
    public static void contar(TextView t, double valorFinal, String sufijo) {
        ValueAnimator a = ValueAnimator.ofFloat(0f, 1f);
        a.setDuration(800);
        a.setInterpolator(new DecelerateInterpolator());
        a.addUpdateListener(an -> t.setText(formato(valorFinal * (float) an.getAnimatedValue()) + " " + sufijo));
        a.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                t.setText(formato(valorFinal) + " " + sufijo);
            }
        });
        a.start();
    }

    /** Gira la vista media vuelta. */
    public static void girar(View v) {
        v.animate().rotationBy(180f).setDuration(350).start();
    }

    /** 1234.5 -> "1,234.50" */
    public static String formato(double v) {
        return String.format(java.util.Locale.US, "%,.2f", v);
    }
}
