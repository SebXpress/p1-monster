package ec.edu.monster.vista;

import ec.edu.monster.modelo.Celular;
import ec.edu.monster.modelo.Credito;
import ec.edu.monster.modelo.Cuota;
import ec.edu.monster.modelo.DetalleFactura;
import ec.edu.monster.modelo.Factura;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

/**
 * Vista de texto: menús, lectura de datos, tablas y mensajes con colores ANSI.
 *
 * @author Grupo 1 - NRC 36841
 */
public class VistaConsola {

    private static final String R = "\u001B[0m", N = "\u001B[1m", AZUL = "\u001B[94m", CIAN = "\u001B[96m",
            VERDE = "\u001B[92m", ROJO = "\u001B[91m", AMARILLO = "\u001B[93m", GRIS = "\u001B[90m", MORADO = "\u001B[95m";

    private final Scanner entrada = new Scanner(System.in, StandardCharsets.UTF_8);
    private final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    private final boolean color;

    public VistaConsola(boolean color) {
        this.color = color;
    }

    private String c(String codigo, String texto) {
        return color ? codigo + texto + R : texto;
    }

    public void banner(String url) {
        String[] arte = {
            "  ___  ___  _    _   _ _      _   ___ ___ ___ ",
            " / __|| __|| |  | | | | |    /_\\ | _ \\ __/ __|",
            "| (__ | _| | |__| |_| | |__ / _ \\|   / _|\\__ \\",
            " \\___||___||____|\\___/|____/_/ \\_\\_|_\\___|___/"};
        String[] tonos = {AMARILLO, AMARILLO, CIAN, CIAN};
        for (int k = 0; k < arte.length; k++) {
            out.println(c(N + tonos[k], "   " + arte[k]));
            pausa(60);
        }
        out.println(c(GRIS, "   Comercializadora de Teléfonos Celulares · alianza con el Banco BanQuito"));
        out.println(c(GRIS, "   Cliente CONSOLA · RESTful Java · Grupo 1 · NRC 36841"));
        out.println(c(GRIS, "   Servidor: " + url));
        linea();
    }

    public void linea() {
        out.println(c(GRIS, "   ──────────────────────────────────────────────────────────────"));
    }

    public void titulo(String t) {
        out.println();
        out.println(c(N + CIAN, "   ▶ " + t));
        linea();
    }

    public int menu() {
        out.println();
        out.println(c(N, "   MENÚ PRINCIPAL"));
        String[] op = {"Catálogo de celulares", "Registrar celular", "Actualizar celular", "Eliminar celular",
            "Facturar venta (efectivo o crédito directo)", "Consultar tabla de amortización de un préstamo", "Facturas emitidas"};
        for (int k = 0; k < op.length; k++) {
            out.println("     " + c(AZUL, (k + 1) + ".") + " " + op[k]);
        }
        out.println("     " + c(AZUL, "0.") + " Salir");
        return leerEntero("Opción", 0, op.length);
    }

    public String leerTexto(String etiqueta, String porDefecto) {
        out.print(c(AMARILLO, "   " + etiqueta) + (porDefecto != null && !porDefecto.isEmpty() ? c(GRIS, " [" + porDefecto + "]") : "") + ": ");
        String t = entrada.hasNextLine() ? entrada.nextLine().trim() : "";
        return t.isEmpty() && porDefecto != null ? porDefecto : t;
    }

    public int leerEntero(String etiqueta, int min, int max) {
        while (true) {
            String t = leerTexto(etiqueta, null);
            try {
                int n = Integer.parseInt(t);
                if (n >= min && n <= max) {
                    return n;
                }
            } catch (NumberFormatException e) {
                // se vuelve a pedir
            }
            error("Ingrese un número entre " + min + " y " + max);
        }
    }

    public double leerPrecio(String etiqueta, Double porDefecto) {
        while (true) {
            String t = leerTexto(etiqueta, porDefecto == null ? null : String.format("%.2f", porDefecto).replace(',', '.')).replace(',', '.');
            if (t.matches("\\d+(\\.\\d{1,2})?") && Double.parseDouble(t) > 0) {
                return Double.parseDouble(t);
            }
            error("Ingrese un precio mayor que cero con máximo 2 decimales (ej. 449.90)");
        }
    }

    public String leerCedula(String etiqueta) {
        while (true) {
            String t = leerTexto(etiqueta, null);
            if (t.matches("\\d{10}")) {
                return t;
            }
            error("La cédula tiene 10 dígitos");
        }
    }

    public boolean confirmar(String pregunta) {
        String t = leerTexto(pregunta + " (s/n)", "s");
        return t.equalsIgnoreCase("s") || t.equalsIgnoreCase("si") || t.equalsIgnoreCase("sí");
    }

    public void ok(String m) {
        out.println(c(N + VERDE, "   ✔ " + m));
    }

    public void error(String m) {
        out.println(c(N + ROJO, "   ✘ " + m));
    }

    public void info(String m) {
        out.println(c(GRIS, "   " + m));
    }

    public void esperando(String m) {
        out.print(c(GRIS, "   " + m));
        for (int k = 0; k < 3; k++) {
            pausa(120);
            out.print(c(GRIS, "."));
        }
        out.println();
    }

    public void pausa(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void tablaCelulares(List<Celular> l) {
        out.println(c(N, String.format("   %-5s %-12s %-22s %12s  %-9s", "Cód.", "Marca", "Modelo", "Precio", "Estado")));
        for (Celular x : l) {
            String fila = String.format("   %-5d %-12s %-22s %12s  %-9s", x.getCodigo(), x.getMarca(), x.getModelo(), dinero(x.getPrecio()), x.getEstado());
            out.println("ACTIVO".equals(x.getEstado()) ? fila : c(GRIS, fila));
        }
        info(l.size() + " celular(es)");
    }

    public void factura(Factura f) {
        out.println();
        out.println(c(N + MORADO, "   ╔══════════════════════════════════════════════════════════╗"));
        String cab = "FACTURA N.º " + f.getNumero();
        out.println(c(N + MORADO, "   ║  " + cab + " ".repeat(Math.max(1, 55 - cab.length() - f.getFecha().length())) + f.getFecha() + " ║"));
        out.println(c(N + MORADO, "   ╚══════════════════════════════════════════════════════════╝"));
        out.println("   Cliente : " + f.getCliente() + "  (" + f.getCedula() + ")");
        out.println("   Pago    : " + (f.esCredito() ? c(CIAN, "CRÉDITO DIRECTO BanQuito") : c(VERDE, "EFECTIVO (42 % de descuento)")));
        out.println(c(N, String.format("   %-4s %-30s %5s %12s %12s", "#", "Celular", "Cant.", "P. unitario", "Subtotal")));
        for (DetalleFactura d : f.getDetalles()) {
            out.println(String.format("   %-4d %-30s %5d %12s %12s", d.getLinea(), d.getCelular(), d.getCantidad(), dinero(d.getPrecioUnitario()), dinero(d.getSubtotal())));
        }
        out.println(String.format("   %55s %12s", "Subtotal:", dinero(f.getSubtotal())));
        out.println(String.format("   %55s %12s", "Descuento:", "-" + dinero(f.getDescuento())));
        out.println(c(N + VERDE, String.format("   %55s %12s", "TOTAL:", dinero(f.getTotal()))));
        if (f.getCredito() != null) {
            amortizacion(f.getCredito());
        }
    }

    public void amortizacion(Credito cr) {
        out.println();
        out.println(c(N + CIAN, String.format("   Crédito %d · %s · $%s en %d cuotas · tasa %.2f %% anual · %s",
                cr.getCodigoCredito(), cr.getNombre(), dinero(cr.getMonto()).trim(), cr.getNumeroCuotas(), cr.getTasaAnual(), cr.getEstado())));
        out.println(c(N, String.format("   %8s %13s %15s %15s %13s", "# Cuota", "Valor Cuota", "Interés Pagado", "Capital Pagado", "Saldo")));
        for (Cuota q : cr.getTabla()) {
            if (q.getNumeroCuota() == 0) {
                out.println(String.format("   %8d %13s %15s %15s %13s", 0, "", "", "", dinero(q.getSaldo())));
            } else {
                out.println(String.format("   %8d %13s %15s %15s %13s", q.getNumeroCuota(), dinero(q.getValorCuota()), dinero(q.getInteresPagado()),
                        dinero(q.getCapitalPagado()), dinero(q.getSaldo())));
                pausa(25);
            }
        }
        out.println(c(GRIS, String.format("   Total intereses: %s · Total a pagar: %s", dinero(cr.getTotalIntereses()).trim(), dinero(cr.getTotalPagar()).trim())));
    }

    public void tablaFacturas(List<Factura> l) {
        out.println(c(N, String.format("   %-6s %-19s %-12s %-28s %-9s %12s", "N.º", "Fecha", "Cédula", "Cliente", "Pago", "Total")));
        for (Factura f : l) {
            out.println(String.format("   %-6d %-19s %-12s %-28s %-9s %12s", f.getNumero(), f.getFecha(), f.getCedula(),
                    corto(f.getCliente(), 28), f.getFormaPago(), dinero(f.getTotal())));
        }
        info(l.size() + " factura(s)");
    }

    private static String corto(String s, int n) {
        return s == null ? "" : s.length() <= n ? s : s.substring(0, n - 1) + "…";
    }

    public static String dinero(double v) {
        return String.format("%,.2f", v);
    }
}
