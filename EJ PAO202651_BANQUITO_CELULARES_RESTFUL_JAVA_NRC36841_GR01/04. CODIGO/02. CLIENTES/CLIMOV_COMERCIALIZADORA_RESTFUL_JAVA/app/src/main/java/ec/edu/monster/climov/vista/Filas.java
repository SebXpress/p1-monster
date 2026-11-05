package ec.edu.monster.climov.vista;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import ec.edu.monster.climov.R;
import ec.edu.monster.climov.modelo.Credito;
import ec.edu.monster.climov.modelo.Cuota;
import ec.edu.monster.climov.modelo.DetalleFactura;
import ec.edu.monster.climov.modelo.Factura;

/**
 * Crea por código las filas y tarjetas que muestran listas, facturas y tablas
 * de amortización (así cada pantalla solo arma sus datos).
 *
 * @author Grupo 1 - NRC 36841
 */
public final class Filas {

    public static final int TEXTO = Color.parseColor("#E8EEFB");
    public static final int TENUE = Color.parseColor("#8FA3C7");
    public static final int DORADO = Color.parseColor("#F5B841");
    public static final int CIAN = Color.parseColor("#2DD4BF");
    public static final int VERDE = Color.parseColor("#22C55E");
    public static final int ROJO = Color.parseColor("#F43F5E");
    public static final int AZUL = Color.parseColor("#3D8BFD");
    public static final int BORDE = Color.parseColor("#24395F");

    private Filas() {
    }

    public static int dp(Context c, int v) {
        return Math.round(v * c.getResources().getDisplayMetrics().density);
    }

    public static TextView texto(Context c, String t, int color, float sp, boolean negrita) {
        TextView v = new TextView(c);
        v.setText(t);
        v.setTextColor(color);
        v.setTextSize(sp);
        if (negrita) {
            v.setTypeface(Typeface.DEFAULT_BOLD);
        }
        return v;
    }

    /** Tarjeta redondeada (fondo panel) con margen inferior. */
    public static LinearLayout tarjeta(Context c) {
        LinearLayout t = new LinearLayout(c);
        t.setOrientation(LinearLayout.VERTICAL);
        t.setBackgroundResource(R.drawable.tarjeta);
        int p = dp(c, 14);
        t.setPadding(p, p, p, p);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = dp(c, 10);
        t.setLayoutParams(lp);
        return t;
    }

    /** Fila de tabla: la primera columna a la izquierda, el resto (montos) a la derecha. */
    public static LinearLayout fila(Context c, String[] celdas, float[] pesos, boolean cabecera) {
        LinearLayout f = new LinearLayout(c);
        f.setOrientation(LinearLayout.HORIZONTAL);
        f.setPadding(0, dp(c, 6), 0, dp(c, 6));
        for (int i = 0; i < celdas.length; i++) {
            TextView t = texto(c, celdas[i], cabecera ? TENUE : TEXTO, cabecera ? 11 : 13, cabecera);
            t.setGravity(i == 0 ? Gravity.START : Gravity.END);
            t.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, pesos[i]));
            f.addView(t);
        }
        return f;
    }

    public static View linea(Context c) {
        View v = new View(c);
        v.setBackgroundColor(BORDE);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1));
        return v;
    }

    public static String dinero(double v) {
        return Animaciones.formato(v);
    }

    /** Agrega el resumen y la tabla de amortización del crédito. */
    public static void tablaAmortizacion(LinearLayout destino, Credito cr) {
        Context c = destino.getContext();
        LinearLayout t = tarjeta(c);
        t.addView(texto(c, "📊 Crédito N.º " + cr.getCodigoCredito() + " · " + cr.getNumeroCuotas() + " meses · "
                + dinero(cr.getTasaAnual()) + " % anual · " + cr.getEstado(), DORADO, 15, true));
        TextView res = texto(c, "Monto: $ " + dinero(cr.getMonto()) + "\nCuota fija mensual: $ " + dinero(cr.getCuota())
                + "\nTotal intereses: $ " + dinero(cr.getTotalIntereses()) + "\nTotal a pagar: $ " + dinero(cr.getTotalPagar()),
                TEXTO, 14, false);
        res.setLineSpacing(dp(c, 3), 1f);
        res.setPadding(0, dp(c, 8), 0, dp(c, 8));
        t.addView(res);
        float[] pesos = {0.7f, 1.1f, 1.1f, 1.1f, 1.2f};
        t.addView(fila(c, new String[]{"#", "Cuota", "Interés", "Capital", "Saldo"}, pesos, true));
        t.addView(linea(c));
        for (Cuota q : cr.getTabla()) {
            if (q.getNumeroCuota() == 0) {
                t.addView(fila(c, new String[]{"0", "", "", "", dinero(q.getSaldo())}, pesos, false));
            } else {
                t.addView(fila(c, new String[]{String.valueOf(q.getNumeroCuota()), dinero(q.getValorCuota()),
                    dinero(q.getInteresPagado()), dinero(q.getCapitalPagado()), dinero(q.getSaldo())}, pesos, false));
            }
            t.addView(linea(c));
        }
        destino.addView(t);
    }

    /** Agrega la factura completa (cabecera, detalle, totales y, si es a crédito, la tabla). */
    public static void factura(LinearLayout destino, Factura f) {
        Context c = destino.getContext();
        LinearLayout t = tarjeta(c);
        t.addView(texto(c, "FACTURA N.º " + f.getNumero(), DORADO, 20, true));
        TextView d = texto(c, f.getFecha() + "\nCliente: " + f.getCliente() + "\nCédula: " + f.getCedula(), TEXTO, 14, false);
        d.setLineSpacing(dp(c, 3), 1f);
        t.addView(d);
        t.addView(texto(c, f.esCredito()
                ? "Crédito directo BanQuito N.º " + f.getCodigoCredito() + " · " + f.getNumeroCuotas() + " cuotas de $ " + dinero(f.getValorCuota())
                : "Efectivo · 42 % de descuento", f.esCredito() ? CIAN : VERDE, 14, true));
        float[] pesos = {2.2f, 0.6f, 1f, 1.1f};
        LinearLayout cab = fila(c, new String[]{"Celular", "Cant.", "P. Unit.", "Subtotal"}, pesos, true);
        cab.setPadding(0, dp(c, 12), 0, dp(c, 4));
        t.addView(cab);
        t.addView(linea(c));
        for (DetalleFactura x : f.getDetalles()) {
            t.addView(fila(c, new String[]{x.getCelular(), String.valueOf(x.getCantidad()), dinero(x.getPrecioUnitario()),
                dinero(x.getSubtotal())}, pesos, false));
        }
        t.addView(linea(c));
        TextView tot = texto(c, "Subtotal: $ " + dinero(f.getSubtotal()) + "\nDescuento: - $ " + dinero(f.getDescuento()),
                TEXTO, 14, false);
        tot.setGravity(Gravity.END);
        tot.setPadding(0, dp(c, 8), 0, 0);
        t.addView(tot);
        TextView total = texto(c, "TOTAL $ " + dinero(f.getTotal()), DORADO, 24, true);
        total.setGravity(Gravity.END);
        t.addView(total);
        destino.addView(t);
        Animaciones.latido(t);
        if (f.getCredito() != null) {
            tablaAmortizacion(destino, f.getCredito());
        }
    }

    /** Mensaje centrado (lista vacía o error). */
    public static void aviso(LinearLayout destino, String mensaje, int color) {
        TextView v = texto(destino.getContext(), mensaje, color, 14, false);
        v.setGravity(Gravity.CENTER);
        v.setPadding(0, dp(destino.getContext(), 24), 0, dp(destino.getContext(), 24));
        destino.addView(v);
    }
}
