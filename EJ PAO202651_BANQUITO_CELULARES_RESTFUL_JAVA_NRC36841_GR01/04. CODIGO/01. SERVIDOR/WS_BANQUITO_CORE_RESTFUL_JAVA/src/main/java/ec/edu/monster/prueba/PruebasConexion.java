package ec.edu.monster.prueba;

import ec.edu.monster.db.ConexionBD;
import ec.edu.monster.modelo.CuotaAmortizacion;
import ec.edu.monster.modelo.MontoMaximo;
import ec.edu.monster.modelo.VerificacionCredito;
import ec.edu.monster.servicio.CalculadoraAmortizacion;
import ec.edu.monster.servicio.ServicioCredito;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Pruebas del Core BanQuito (NetBeans: clic derecho → Run File).
 * Cada prueba abre su conexión y SIEMPRE la cierra en el finally.
 *
 * @author Grupo 1 - NRC 36841
 */
public class PruebasConexion {

    private static int ok, fallos;

    public static void main(String[] args) {
        System.out.println("=== PRUEBAS CORE BANQUITO ===");
        pruebaConexion();
        pruebaConteos();
        pruebaAmortizacionEjemplo();
        pruebaReglas();
        System.out.printf("%n=== %d OK, %d fallos ===%n", ok, fallos);
    }

    static void verificar(String nombre, boolean condicion) {
        if (condicion) {
            ok++;
            System.out.println("[OK]    " + nombre);
        } else {
            fallos++;
            System.out.println("[FALLO] " + nombre);
        }
    }

    static void pruebaConexion() {
        Connection cn = null;
        try {
            cn = ConexionBD.getInstancia().getConexion();
            verificar("Conexión a " + ConexionBD.getInstancia().getUrl(), cn.isValid(3));
        } catch (Exception e) {
            verificar("Conexión: " + e.getMessage(), false);
        } finally {
            ConexionBD.cerrar(cn);
        }
        verificar("Singleton: siempre la misma instancia", ConexionBD.getInstancia() == ConexionBD.getInstancia());
    }

    static void pruebaConteos() {
        Connection cn = null;
        try {
            cn = ConexionBD.getInstancia().getConexion();
            try (Statement st = cn.createStatement()) {
                verificar("Al menos 5 clientes", contar(st, "CLIENTE") >= 5);
                verificar("Al menos 5 cuentas", contar(st, "CUENTA") >= 5);
                verificar("Al menos 50 movimientos", contar(st, "MOVIMIENTO") >= 50);
            }
        } catch (Exception e) {
            verificar("Conteos: " + e.getMessage(), false);
        } finally {
            ConexionBD.cerrar(cn);
        }
    }

    static int contar(Statement st, String tabla) throws Exception {
        try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM " + tabla)) {
            rs.next();
            int n = rs.getInt(1);
            System.out.println("        " + tabla + ": " + n);
            return n;
        }
    }

    static void pruebaAmortizacionEjemplo() {
        BigDecimal monto = new BigDecimal("450");
        verificar("Cuota de $450 a 6 meses = 78.65 (ejemplo del enunciado)",
                CalculadoraAmortizacion.cuota(monto, 6).compareTo(new BigDecimal("78.65")) == 0);
        List<CuotaAmortizacion> t = CalculadoraAmortizacion.tabla(monto, 6);
        System.out.println("        # Cuota | Valor Cuota | Interés | Capital | Saldo");
        for (CuotaAmortizacion f : t) {
            System.out.printf("        %7d | %11s | %7s | %7s | %s%n", f.getNumeroCuota(), f.getValorCuota(), f.getInteresPagado(), f.getCapitalPagado(), f.getSaldo());
        }
        verificar("Interés de la cuota 1 = 6.19", t.get(1).getInteresPagado().compareTo(new BigDecimal("6.19")) == 0);
        verificar("Saldo final = 0.00", t.get(6).getSaldo().signum() == 0);
    }

    static void pruebaReglas() {
        ServicioCredito s = ServicioCredito.getInstancia();
        try {
            VerificacionCredito v1 = s.verificarSujeto("1723456719");
            verificar("María Fernanda (mujer, depósitos recientes) ES sujeto de crédito", v1.isSujetoCredito());
            verificar("Diego Mora (hombre de 22 años) NO es sujeto", !s.verificarSujeto("1730543210").isSujetoCredito());
            verificar("Patricia Vaca (sin depósito en el último mes) NO es sujeto", !s.verificarSujeto("1822109847").isSujetoCredito());
            verificar("Luis Castro (crédito activo) NO es sujeto", !s.verificarSujeto("1711002343").isSujetoCredito());
            verificar("Cédula que no es cliente NO es sujeto", !s.verificarSujeto("1700000001").isEsCliente());
            MontoMaximo m = s.montoMaximo(1);
            verificar("Monto máximo de María = ((1200 - 400) * 30%) * 6 = 1440.00",
                    m.getMontoMaximo().compareTo(new BigDecimal("1440.00")) == 0);
        } catch (Exception e) {
            verificar("Reglas: " + e.getMessage(), false);
        }
    }
}
