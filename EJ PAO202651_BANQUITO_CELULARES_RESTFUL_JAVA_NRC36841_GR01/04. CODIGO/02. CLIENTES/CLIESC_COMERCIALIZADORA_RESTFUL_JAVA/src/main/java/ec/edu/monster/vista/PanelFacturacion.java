package ec.edu.monster.vista;

import ec.edu.monster.modelo.Celular;
import ec.edu.monster.modelo.Evaluacion;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 * Facturación: carrito de teléfonos, datos del comprador y forma de pago.
 * <ul>
 *   <li>Efectivo: 42 % de descuento.</li>
 *   <li>Crédito directo: el Banco BanQuito verifica si es sujeto de crédito y su
 *   monto máximo; si aprueba, otorga el crédito (3 a 18 cuotas, 16.5 % anual).</li>
 * </ul>
 *
 * @author Grupo 1 - NRC 36841
 */
public class PanelFacturacion extends JPanel {

    /** Descuento por pago en efectivo (para la vista previa; el servidor lo vuelve a calcular). */
    public static final double DESCUENTO_EFECTIVO = 0.42;
    public static final double TASA_ANUAL = 16.5;
    private static final String HTML = "<html><div style='width:330px'>";

    private final JComboBox<Celular> cboCelular = new JComboBox<>();
    private final JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
    private final JButton btnAgregar = Componentes.boton("Agregar", Estilo.AZUL);
    private final JButton btnQuitar = Componentes.boton("Quitar línea", Estilo.ROJO);
    private final JButton btnRecargar = Componentes.boton("↻", Estilo.TENUE);
    private final DefaultTableModel modeloCarrito = Componentes.modelo("Código", "Celular", "Cantidad", "P. Unitario", "Subtotal");
    private final JTable tablaCarrito = Componentes.tabla(modeloCarrito);

    private final JTextField txtCedula = Componentes.campo("10 dígitos");
    private final JTextField txtNombre = Componentes.campo("Nombres y apellidos");
    private final JTextField txtDireccion = Componentes.campo("Dirección");
    private final JTextField txtTelefono = Componentes.campo("Teléfono");
    private final JTextField txtCorreo = Componentes.campo("correo@ejemplo.com");

    private final JRadioButton rbEfectivo = new JRadioButton("Efectivo (42 % de descuento)", true);
    private final JRadioButton rbCredito = new JRadioButton("Crédito directo · Banco BanQuito");
    private final JSpinner spCuotas = new JSpinner(new SpinnerNumberModel(6, 3, 18, 1));
    private final JButton btnEvaluar = Componentes.boton("Consultar al banco", Estilo.CIAN);
    private final JLabel lblEvaluacion = Componentes.etiqueta(" ", Estilo.PEQUENA, Estilo.TENUE);

    private final JLabel lblSubtotal = Componentes.etiqueta("$ 0.00", Estilo.SUBTITULO, Estilo.TEXTO);
    private final JLabel lblDescuento = Componentes.etiqueta("$ 0.00", Estilo.SUBTITULO, Estilo.VERDE);
    private final JLabel lblTotal = Componentes.etiqueta("$ 0.00", Estilo.DINERO, Estilo.DORADO);
    private final JLabel lblCuota = Componentes.etiqueta(" ", Estilo.NORMAL, Estilo.CIAN);
    private final JButton btnFacturar = Componentes.boton("FACTURAR", Estilo.VERDE);
    private final JButton btnLimpiar = Componentes.boton("Limpiar", Estilo.TENUE);
    private final Componentes.Tarjeta tarjetaPago = new Componentes.Tarjeta(new BorderLayout(0, 10));

    /** Código de celular → [celular, cantidad]. */
    private final Map<Integer, Object[]> carrito = new LinkedHashMap<>();

    public PanelFacturacion() {
        super(new BorderLayout(16, 16));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        add(Componentes.encabezado("Facturar venta",
                "Agregue los teléfonos, ingrese el comprador y elija la forma de pago."), BorderLayout.NORTH);

        // ---- Carrito
        Componentes.Tarjeta carritoT = new Componentes.Tarjeta(new BorderLayout(0, 12));
        JPanel agregar = new JPanel(new BorderLayout(8, 0));
        agregar.setOpaque(false);
        cboCelular.setFont(Estilo.NORMAL);
        agregar.add(Componentes.campoConTitulo("Teléfono celular", cboCelular), BorderLayout.CENTER);
        JPanel cant = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        cant.setOpaque(false);
        spCantidad.setPreferredSize(Componentes.dim(70, 34));
        cant.add(Componentes.campoConTitulo("Cantidad", spCantidad));
        cant.add(Componentes.campoConTitulo(" ", btnAgregar));
        cant.add(Componentes.campoConTitulo(" ", btnRecargar));
        agregar.add(cant, BorderLayout.EAST);
        carritoT.add(agregar, BorderLayout.NORTH);
        tablaCarrito.getColumnModel().getColumn(0).setMaxWidth(70);
        tablaCarrito.getColumnModel().getColumn(2).setMaxWidth(90);
        Componentes.alinearDerecha(tablaCarrito, 2, 3, 4);
        carritoT.add(Componentes.scroll(tablaCarrito), BorderLayout.CENTER);

        JPanel totales = new JPanel(new BorderLayout());
        totales.setOpaque(false);
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izq.setOpaque(false);
        izq.add(btnQuitar);
        totales.add(izq, BorderLayout.WEST);
        JPanel cifras = new JPanel(new GridLayout(0, 2, 14, 2));
        cifras.setOpaque(false);
        cifras.add(Componentes.etiqueta("Subtotal", Estilo.NORMAL, Estilo.TENUE));
        cifras.add(lblSubtotal);
        cifras.add(Componentes.etiqueta("Descuento", Estilo.NORMAL, Estilo.TENUE));
        cifras.add(lblDescuento);
        cifras.add(Componentes.etiqueta("TOTAL", Estilo.SUBTITULO, Estilo.TEXTO));
        cifras.add(lblTotal);
        cifras.add(new JLabel());
        cifras.add(lblCuota);
        totales.add(cifras, BorderLayout.EAST);
        carritoT.add(totales, BorderLayout.SOUTH);
        add(carritoT, BorderLayout.CENTER);

        // ---- Comprador y pago
        JPanel derecha = new JPanel();
        derecha.setOpaque(false);
        derecha.setLayout(new BoxLayout(derecha, BoxLayout.Y_AXIS));
        derecha.setPreferredSize(Componentes.dim(400, 0));

        Componentes.Tarjeta compradorT = new Componentes.Tarjeta(new BorderLayout(0, 10));
        compradorT.add(Componentes.etiqueta("Comprador", Estilo.SUBTITULO, Estilo.DORADO), BorderLayout.NORTH);
        JPanel cuerpo = new JPanel(new GridLayout(0, 2, 10, 8));
        cuerpo.setOpaque(false);
        cuerpo.add(Componentes.campoConTitulo("Cédula *", txtCedula));
        cuerpo.add(Componentes.campoConTitulo("Teléfono", txtTelefono));
        cuerpo.add(Componentes.campoConTitulo("Nombre *", txtNombre));
        cuerpo.add(Componentes.campoConTitulo("Correo", txtCorreo));
        JPanel bloque = new JPanel(new BorderLayout(0, 8));
        bloque.setOpaque(false);
        bloque.add(cuerpo, BorderLayout.CENTER);
        bloque.add(Componentes.campoConTitulo("Dirección", txtDireccion), BorderLayout.SOUTH);
        compradorT.add(bloque, BorderLayout.CENTER);
        derecha.add(compradorT);
        derecha.add(javax.swing.Box.createVerticalStrut(12));

        tarjetaPago.add(Componentes.etiqueta("Forma de pago", Estilo.SUBTITULO, Estilo.DORADO), BorderLayout.NORTH);
        ButtonGroup g = new ButtonGroup();
        g.add(rbEfectivo);
        g.add(rbCredito);
        JPanel opciones = new JPanel();
        opciones.setLayout(new BoxLayout(opciones, BoxLayout.Y_AXIS));
        opciones.setOpaque(false);
        for (JRadioButton r : new JRadioButton[]{rbEfectivo, rbCredito}) {
            r.setOpaque(false);
            r.setForeground(Estilo.TEXTO);
            r.setFont(Estilo.NORMAL);
            r.setAlignmentX(LEFT_ALIGNMENT);
            opciones.add(r);
        }
        JPanel credito = new JPanel(new BorderLayout(8, 0));
        credito.setOpaque(false);
        spCuotas.setPreferredSize(Componentes.dim(80, 34));
        credito.add(Componentes.campoConTitulo("Cuotas (3 a 18)", spCuotas), BorderLayout.WEST);
        credito.add(Componentes.campoConTitulo(" ", btnEvaluar), BorderLayout.CENTER);
        credito.setAlignmentX(LEFT_ALIGNMENT);
        credito.setMaximumSize(Componentes.dim(400, 64));
        opciones.add(javax.swing.Box.createVerticalStrut(6));
        opciones.add(credito);
        lblEvaluacion.setVerticalAlignment(JLabel.TOP);
        JPanel pagoCuerpo = new JPanel(new BorderLayout(0, 8));
        pagoCuerpo.setOpaque(false);
        pagoCuerpo.add(opciones, BorderLayout.NORTH);
        pagoCuerpo.add(lblEvaluacion, BorderLayout.CENTER);
        tarjetaPago.add(pagoCuerpo, BorderLayout.CENTER);
        JPanel acciones = new JPanel(new BorderLayout(8, 0));
        acciones.setOpaque(false);
        acciones.add(btnLimpiar, BorderLayout.WEST);
        acciones.add(btnFacturar, BorderLayout.CENTER);
        tarjetaPago.add(acciones, BorderLayout.SOUTH);
        derecha.add(tarjetaPago);
        add(derecha, BorderLayout.EAST);

        // Eventos propios de la vista (cálculos de presentación)
        btnAgregar.addActionListener(e -> agregarSeleccionado());
        btnQuitar.addActionListener(e -> quitarSeleccionado());
        rbEfectivo.addActionListener(e -> cambioFormaPago());
        rbCredito.addActionListener(e -> cambioFormaPago());
        spCuotas.addChangeListener(e -> recalcular());
        btnLimpiar.addActionListener(e -> limpiar());
        cambioFormaPago();
    }

    // ------------------------------------------------------------ carrito

    public void setCelulares(List<Celular> lista) {
        Object sel = cboCelular.getSelectedItem();
        cboCelular.setModel(new DefaultComboBoxModel<>(lista.toArray(new Celular[0])));
        if (sel != null) {
            cboCelular.setSelectedItem(sel);
        }
    }

    private void agregarSeleccionado() {
        Celular c = (Celular) cboCelular.getSelectedItem();
        if (c == null) {
            return;
        }
        int cantidad = (Integer) spCantidad.getValue();
        Object[] linea = carrito.get(c.getCodigo());
        int nueva = linea == null ? cantidad : Math.min(100, (Integer) linea[1] + cantidad);
        carrito.put(c.getCodigo(), new Object[]{c, nueva});
        spCantidad.setValue(1);
        refrescarCarrito();
    }

    private void quitarSeleccionado() {
        int f = tablaCarrito.getSelectedRow();
        if (f >= 0) {
            carrito.remove((Integer) modeloCarrito.getValueAt(f, 0));
            refrescarCarrito();
        }
    }

    private void refrescarCarrito() {
        modeloCarrito.setRowCount(0);
        for (Object[] l : carrito.values()) {
            Celular c = (Celular) l[0];
            int n = (Integer) l[1];
            modeloCarrito.addRow(new Object[]{c.getCodigo(), c.nombre(), n, Estilo.dinero(c.getPrecio()), Estilo.dinero(c.getPrecio() * n)});
        }
        lblEvaluacion.setText(" ");
        recalcular();
    }

    public double subtotal() {
        double s = 0;
        for (Object[] l : carrito.values()) {
            s += ((Celular) l[0]).getPrecio() * (Integer) l[1];
        }
        return Math.round(s * 100) / 100.0;
    }

    private void cambioFormaPago() {
        boolean credito = rbCredito.isSelected();
        spCuotas.setEnabled(credito);
        btnEvaluar.setEnabled(credito);
        Componentes.destello(tarjetaPago, credito ? Estilo.CIAN : Estilo.VERDE);
        lblEvaluacion.setText(credito ? HTML + "El banco verificará si el comprador es sujeto de crédito y su monto máximo.</div></html>" : " ");
        lblEvaluacion.setForeground(Estilo.TENUE);
        recalcular();
    }

    /** Vista previa: descuento, total y cuota estimada (sistema francés, 16.5 % anual). */
    private void recalcular() {
        double sub = subtotal();
        double desc = rbEfectivo.isSelected() ? Math.round(sub * DESCUENTO_EFECTIVO * 100) / 100.0 : 0;
        lblSubtotal.setText("$ " + Estilo.dinero(sub));
        lblDescuento.setText("- $ " + Estilo.dinero(desc));
        lblTotal.setText("$ " + Estilo.dinero(sub - desc));
        if (rbCredito.isSelected() && sub > 0) {
            int n = getNumeroCuotas();
            double i = TASA_ANUAL / 100 / 12;
            double cuota = sub / ((1 - Math.pow(1 + i, -n)) / i);
            lblCuota.setText(n + " cuotas de $ " + Estilo.dinero(cuota));
        } else {
            lblCuota.setText(" ");
        }
    }

    // ------------------------------------------------------------ resultado de la evaluación

    public void mostrarEvaluacion(Evaluacion ev) {
        StringBuilder sb = new StringBuilder(HTML);
        if (!ev.isEsCliente()) {
            sb.append("<b>No es cliente del Banco BanQuito.</b>");
        } else {
            sb.append("<b>").append(ev.getNombre()).append("</b><br>");
            if (ev.isSujetoCredito()) {
                sb.append("✔ Sujeto de crédito · Monto máximo: <b>$ ").append(Estilo.dinero(ev.getMontoMaximo())).append("</b><br>");
                sb.append(ev.isAlcanza() ? "✔ El valor de la compra está aprobado."
                        : "✖ El valor $ " + Estilo.dinero(subtotal()) + " supera el monto máximo.");
            } else {
                sb.append("✖ No es sujeto de crédito:");
            }
        }
        for (String m : ev.getMotivos()) {
            sb.append("<br>• ").append(m);
        }
        lblEvaluacion.setText(sb.append("</div></html>").toString());
        boolean ok = ev.isSujetoCredito() && ev.isAlcanza();
        lblEvaluacion.setForeground(ok ? Estilo.VERDE : Estilo.ROJO);
        Componentes.destello(tarjetaPago, ok ? Estilo.VERDE : Estilo.ROJO);
        if (ev.getNombre() != null && txtNombre.getText().isBlank()) {
            txtNombre.setText(ev.getNombre());
        }
    }

    public void mensajePago(String texto, Color color) {
        lblEvaluacion.setText(HTML + texto + "</div></html>");
        lblEvaluacion.setForeground(color);
    }

    public void error() {
        Componentes.sacudir(tarjetaPago);
        Componentes.destello(tarjetaPago, Estilo.ROJO);
    }

    public final void limpiar() {
        carrito.clear();
        refrescarCarrito();
        for (JTextField t : new JTextField[]{txtCedula, txtNombre, txtDireccion, txtTelefono, txtCorreo}) {
            t.setText("");
        }
        rbEfectivo.setSelected(true);
        spCuotas.setValue(6);
        cambioFormaPago();
    }

    // ------------------------------------------------------------ datos para el controlador

    /** Código de celular → cantidad. */
    public Map<Integer, Integer> getItems() {
        Map<Integer, Integer> m = new LinkedHashMap<>();
        carrito.forEach((k, v) -> m.put(k, (Integer) v[1]));
        return m;
    }

    public List<String> getDatosComprador() {
        List<String> l = new ArrayList<>();
        for (JTextField t : new JTextField[]{txtCedula, txtNombre, txtDireccion, txtTelefono, txtCorreo}) {
            l.add(t.getText().trim());
        }
        return l;
    }

    public boolean isCredito() {
        return rbCredito.isSelected();
    }

    public int getNumeroCuotas() {
        return (Integer) spCuotas.getValue();
    }

    public String getCedula() {
        return txtCedula.getText().trim();
    }

    public JButton getBtnEvaluar() {
        return btnEvaluar;
    }

    public JButton getBtnFacturar() {
        return btnFacturar;
    }

    public JButton getBtnRecargar() {
        return btnRecargar;
    }
}
