package ec.edu.monster.climov.vista;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Ajusta la pantalla al teclado y a las barras del sistema.
 * En Android 15+ las apps se dibujan de borde a borde y "adjustResize" ya no
 * mueve el contenido: aquí se agrega como relleno el alto del teclado para
 * que el campo que se escribe nunca quede tapado.
 *
 * @author Grupo 1 - NRC 36841
 */
public final class Pantalla {

    private Pantalla() {
    }

    public static void ajustarTeclado(Activity a) {
        View raiz = ((ViewGroup) a.findViewById(android.R.id.content)).getChildAt(0);
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (v, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(barras.left, barras.top, barras.right, Math.max(barras.bottom, teclado.bottom));
            View foco = a.getCurrentFocus();
            if (foco != null && teclado.bottom > 0) {
                desplazarHasta(v, foco);
            }
            return WindowInsetsCompat.CONSUMED;
        });
        // Al pasar de un campo a otro con el teclado abierto, también se desplaza
        raiz.getViewTreeObserver().addOnGlobalFocusChangeListener((anterior, nuevo) -> {
            WindowInsetsCompat w = ViewCompat.getRootWindowInsets(raiz);
            if (nuevo != null && w != null && w.isVisible(WindowInsetsCompat.Type.ime())) {
                desplazarHasta(raiz, nuevo);
            }
        });
    }

    /** Lleva el ScrollView hasta el campo con foco (después de que el teclado aparece). */
    private static void desplazarHasta(View raiz, View foco) {
        ScrollView sv = buscarScroll(raiz);
        if (sv == null) {
            return;
        }
        sv.postDelayed(() -> {
            int[] pos = new int[2];
            int[] posSv = new int[2];
            foco.getLocationInWindow(pos);
            sv.getLocationInWindow(posSv);
            int destino = sv.getScrollY() + (pos[1] - posSv[1]) - sv.getHeight() / 3;
            sv.smoothScrollTo(0, Math.max(0, destino));
        }, 150);
    }

    private static ScrollView buscarScroll(View v) {
        if (v instanceof ScrollView) {
            return (ScrollView) v;
        }
        if (v instanceof ViewGroup) {
            ViewGroup g = (ViewGroup) v;
            for (int i = 0; i < g.getChildCount(); i++) {
                ScrollView s = buscarScroll(g.getChildAt(i));
                if (s != null) {
                    return s;
                }
            }
        }
        return null;
    }
}
