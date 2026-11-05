package ec.edu.monster.vista;

import ec.edu.monster.modelo.Credito;
import ec.edu.monster.modelo.Cuota;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Tabla de amortización de un crédito del Banco BanQuito con su resumen
 * (monto, plazo, cuota fija, intereses y total a pagar).
 *
 * @author Grupo 1 - NRC 36841
 */
public class TablaAmortizacion extends JPanel {

    private final DefaultTableModel modelo = Componentes.modelo("# Cuota", "Valor Cuota", "Interés Pagado", "Capital Pagado", "Saldo");
    private final JTable tabla = Componentes.tabla(modelo);
    private final JLabel lblTitulo = Componentes.etiqueta("Seleccione un crédito", Estilo.SUBTITULO, Estilo.DORADO);
    private final JLabel lblMonto = valor();
    private final JLabel lblCuota = valor();
    private final JLabel lblIntereses = valor();
    private final JLabel lblTotal = valor();

    public TablaAmortizacion() {
        super(new BorderLayout(0, 10));
        setOpaque(false);
        JPanel resumen = new JPanel(new GridLayout(2, 4, 12, 2));
        resumen.setOpaque(false);
        for (String t : new String[]{"Monto financiado", "Cuota fija mensual", "Total intereses", "Total a pagar"}) {
            resumen.add(Componentes.etiqueta(t, Estilo.PEQUENA, Estilo.TENUE));
        }
        resumen.add(lblMonto);
        resumen.add(lblCuota);
        resumen.add(lblIntereses);
        resumen.add(lblTotal);
        JPanel arriba = new JPanel(new BorderLayout(0, 8));
        arriba.setOpaque(false);
        arriba.add(lblTitulo, BorderLayout.NORTH);
        arriba.add(resumen, BorderLayout.CENTER);
        add(arriba, BorderLayout.NORTH);
        Componentes.alinearDerecha(tabla, 1, 2, 3, 4);
        add(Componentes.scroll(tabla), BorderLayout.CENTER);
    }

    private static JLabel valor() {
        return Componentes.etiqueta("—", Estilo.SUBTITULO, Estilo.TEXTO);
    }

    public void mostrar(Credito c) {
        modelo.setRowCount(0);
        if (c == null) {
            lblTitulo.setText("Seleccione un crédito");
            for (JLabel l : new JLabel[]{lblMonto, lblCuota, lblIntereses, lblTotal}) {
                l.setText("—");
            }
            return;
        }
        lblTitulo.setText("Crédito N.º " + c.getCodigoCredito() + " · " + c.getNumeroCuotas() + " meses · "
                + Estilo.dinero(c.getTasaAnual()) + " % anual · " + c.getEstado());
        lblMonto.setText("$ " + Estilo.dinero(c.getMonto()));
        lblCuota.setText("$ " + Estilo.dinero(c.getCuota()));
        lblCuota.setForeground(Estilo.CIAN);
        lblIntereses.setText("$ " + Estilo.dinero(c.getTotalIntereses()));
        lblTotal.setText("$ " + Estilo.dinero(c.getTotalPagar()));
        for (Cuota q : c.getTabla()) {
            if (q.getNumeroCuota() == 0) {
                modelo.addRow(new Object[]{"0", "", "", "", Estilo.dinero(q.getSaldo())});
            } else {
                modelo.addRow(new Object[]{q.getNumeroCuota(), Estilo.dinero(q.getValorCuota()), Estilo.dinero(q.getInteresPagado()),
                    Estilo.dinero(q.getCapitalPagado()), Estilo.dinero(q.getSaldo())});
            }
        }
    }
}
