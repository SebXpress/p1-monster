package ec.edu.monster.servicio;

import ec.edu.monster.modelo.CuotaAmortizacion;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Tabla de amortización de cuota fija (sistema francés), pago mensual.
 *
 * <pre>
 *                 Valor Teléfono
 * Cuota = ---------------------------------------
 *          1 - (1 + TasaPeriodo) ^ -NúmeroCuotas
 *          -------------------------------------
 *                      TasaPeriodo
 * TasaPeriodo = 16.5 % / 12
 * </pre>
 *
 * Igual que la tabla de ejemplo del enunciado (hecha en Excel con PAGO): la
 * cuota, el interés y el capital se calculan con toda la precisión y se
 * redondean a 2 decimales; el saldo mostrado es el saldo anterior menos el
 * capital pagado y la última cuota deja el saldo en 0.00. Con 450 a 6 cuotas
 * reproduce exactamente el ejemplo (cuota 78.65, interés 6.19, saldo 377.54 ...).
 *
 * @author Grupo 1 - NRC 36841
 */
public final class CalculadoraAmortizacion {

    /** Tasa de interés anual del crédito (16.5 %). */
    public static final BigDecimal TASA_ANUAL = new BigDecimal("16.50");
    public static final int CUOTAS_MIN = 3;
    public static final int CUOTAS_MAX = 18;
    private static final MathContext MC = MathContext.DECIMAL128;

    private CalculadoraAmortizacion() {
    }

    /** Tasa del periodo mensual: 0.165 / 12. */
    public static BigDecimal tasaPeriodo() {
        return TASA_ANUAL.divide(BigDecimal.valueOf(100), MC).divide(BigDecimal.valueOf(12), MC);
    }

    /** Cuota fija sin redondear. */
    public static BigDecimal cuotaExacta(BigDecimal monto, int cuotas) {
        BigDecimal i = tasaPeriodo();
        BigDecimal factor = BigDecimal.ONE.add(i).pow(-cuotas, MC);           // (1+i)^-n
        BigDecimal divisor = BigDecimal.ONE.subtract(factor).divide(i, MC);    // (1-(1+i)^-n)/i
        return monto.divide(divisor, MC);
    }

    /** Cuota fija redondeada a 2 decimales (la que paga el cliente cada mes). */
    public static BigDecimal cuota(BigDecimal monto, int cuotas) {
        return r2(cuotaExacta(monto, cuotas));
    }

    /**
     * Filas de la tabla: la 0 solo muestra el saldo inicial; de la 1 a la n:
     * interés = saldo * tasa, capital = cuota - interés, saldo = saldo - capital.
     */
    public static List<CuotaAmortizacion> tabla(BigDecimal monto, int cuotas) {
        BigDecimal i = tasaPeriodo();
        BigDecimal cuota = cuotaExacta(monto, cuotas);
        List<CuotaAmortizacion> filas = new ArrayList<>();
        filas.add(fila(0, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, monto));
        BigDecimal saldo = monto;                 // saldo exacto (para el interés)
        BigDecimal saldoMostrado = r2(monto);     // saldo de la tabla
        for (int k = 1; k <= cuotas; k++) {
            BigDecimal interes = saldo.multiply(i, MC);
            BigDecimal capital = cuota.subtract(interes, MC);
            saldo = saldo.subtract(capital, MC);
            saldoMostrado = k == cuotas ? BigDecimal.ZERO : saldoMostrado.subtract(r2(capital));
            filas.add(fila(k, cuota, interes, capital, saldoMostrado));
        }
        return filas;
    }

    private static CuotaAmortizacion fila(int n, BigDecimal cuota, BigDecimal interes, BigDecimal capital, BigDecimal saldo) {
        CuotaAmortizacion f = new CuotaAmortizacion();
        f.setNumeroCuota(n);
        f.setValorCuota(r2(cuota));
        f.setInteresPagado(r2(interes));
        f.setCapitalPagado(r2(capital));
        f.setSaldo(r2(saldo));
        return f;
    }

    public static BigDecimal r2(BigDecimal v) {
        return v.setScale(2, RoundingMode.HALF_UP);
    }
}
