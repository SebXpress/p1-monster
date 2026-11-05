package ec.edu.monster.vista;

import ec.edu.monster.modelo.Celular;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Mantenimiento del catálogo de teléfonos celulares: listar/buscar, crear,
 * actualizar y eliminar (o inactivar si ya fue facturado).
 *
 * @author Grupo 1 - NRC 36841
 */
public class PanelCatalogo extends JPanel {

    private final JTextField txtBuscar = Componentes.campo("Buscar por marca o modelo");
    private final JCheckBox chkActivos = new JCheckBox("Solo activos");
    private final JButton btnBuscar = Componentes.boton("Buscar", Estilo.AZUL);
    private final DefaultTableModel modelo = Componentes.modelo("Código", "Marca", "Modelo", "Descripción", "Precio", "Estado");
    private final JTable tabla = Componentes.tabla(modelo);

    private final JLabel lblCodigo = Componentes.etiqueta("Nuevo celular", Estilo.SUBTITULO, Estilo.DORADO);
    private final JTextField txtMarca = Componentes.campo("Ej.: Samsung");
    private final JTextField txtModelo = Componentes.campo("Ej.: Galaxy A55");
    private final JTextArea txtDescripcion = new JTextArea(3, 20);
    private final JTextField txtPrecio = Componentes.campo("0.00");
    private final JComboBox<String> cboEstado = new JComboBox<>(new String[]{"ACTIVO", "INACTIVO"});
    private final JButton btnNuevo = Componentes.boton("Nuevo", Estilo.TENUE);
    private final JButton btnGuardar = Componentes.boton("Guardar", Estilo.VERDE);
    private final JButton btnEliminar = Componentes.boton("Eliminar", Estilo.ROJO);
    private final Componentes.Tarjeta tarjetaFormulario = new Componentes.Tarjeta(new BorderLayout(0, 12));

    private List<Celular> celulares = List.of();
    private Integer codigoSeleccionado;

    public PanelCatalogo() {
        super(new BorderLayout(16, 16));
        setBackground(Estilo.FONDO);
        setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));
        add(Componentes.encabezado("Catálogo de teléfonos celulares",
                "La comercializadora no maneja inventario: solo el catálogo con precios de venta."), BorderLayout.NORTH);

        // Lista
        Componentes.Tarjeta lista = new Componentes.Tarjeta(new BorderLayout(0, 12));
        JPanel filtros = new JPanel(new BorderLayout(8, 0));
        filtros.setOpaque(false);
        filtros.add(txtBuscar, BorderLayout.CENTER);
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        der.setOpaque(false);
        chkActivos.setOpaque(false);
        chkActivos.setForeground(Estilo.TEXTO);
        der.add(chkActivos);
        der.add(btnBuscar);
        filtros.add(der, BorderLayout.EAST);
        lista.add(filtros, BorderLayout.NORTH);
        tabla.getColumnModel().getColumn(0).setMaxWidth(70);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(140);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(230);
        tabla.getColumnModel().getColumn(5).setPreferredWidth(90);
        Componentes.alinearDerecha(tabla, 4);
        lista.add(Componentes.scroll(tabla), BorderLayout.CENTER);
        add(lista, BorderLayout.CENTER);

        // Formulario
        tarjetaFormulario.setPreferredSize(Componentes.dim(390, 0));
        tarjetaFormulario.add(lblCodigo, BorderLayout.NORTH);
        JPanel campos = new JPanel(new GridLayout(0, 1, 0, 10));
        campos.setOpaque(false);
        campos.add(Componentes.campoConTitulo("Marca *", txtMarca));
        campos.add(Componentes.campoConTitulo("Modelo *", txtModelo));
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setFont(Estilo.PEQUENA);
        JPanel desc = Componentes.campoConTitulo("Descripción", new JScrollPane(txtDescripcion));
        campos.add(Componentes.campoConTitulo("Precio de venta (USD) *", txtPrecio));
        campos.add(Componentes.campoConTitulo("Estado", cboEstado));
        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(campos, BorderLayout.NORTH);
        centro.add(desc, BorderLayout.CENTER);
        tarjetaFormulario.add(centro, BorderLayout.CENTER);
        JPanel botones = new JPanel(new GridLayout(1, 3, 8, 0));
        botones.setOpaque(false);
        for (JButton b : new JButton[]{btnNuevo, btnGuardar, btnEliminar}) {
            b.setFont(Estilo.NORMAL.deriveFont(java.awt.Font.BOLD));
            b.setBorder(BorderFactory.createEmptyBorder(10, 6, 10, 6));
        }
        botones.add(btnNuevo);
        botones.add(btnGuardar);
        botones.add(btnEliminar);
        tarjetaFormulario.add(botones, BorderLayout.SOUTH);
        add(tarjetaFormulario, BorderLayout.EAST);

        tabla.getSelectionModel().addListSelectionListener(e -> {
            int f = tabla.getSelectedRow();
            if (!e.getValueIsAdjusting() && f >= 0 && f < celulares.size()) {
                cargar(celulares.get(f));
            }
        });
        btnNuevo.addActionListener(e -> limpiar());
        limpiar();
    }

    public void mostrar(List<Celular> lista) {
        celulares = lista;
        modelo.setRowCount(0);
        for (Celular c : lista) {
            modelo.addRow(new Object[]{c.getCodigo(), c.getMarca(), c.getModelo(), c.getDescripcion(),
                Estilo.dinero(c.getPrecio()), c.getEstado()});
        }
    }

    private void cargar(Celular c) {
        codigoSeleccionado = c.getCodigo();
        lblCodigo.setText("Editar celular N.º " + c.getCodigo());
        txtMarca.setText(c.getMarca());
        txtModelo.setText(c.getModelo());
        txtDescripcion.setText(c.getDescripcion() == null ? "" : c.getDescripcion());
        txtPrecio.setText(String.format(java.util.Locale.US, "%.2f", c.getPrecio()));
        cboEstado.setSelectedItem(c.getEstado());
        btnEliminar.setEnabled(true);
    }

    public final void limpiar() {
        codigoSeleccionado = null;
        tabla.clearSelection();
        lblCodigo.setText("Nuevo celular");
        txtMarca.setText("");
        txtModelo.setText("");
        txtDescripcion.setText("");
        txtPrecio.setText("");
        cboEstado.setSelectedIndex(0);
        btnEliminar.setEnabled(false);
    }

    /** Arma el celular con los datos del formulario (valida el precio). */
    public Celular leerFormulario() {
        Celular c = new Celular();
        c.setCodigo(codigoSeleccionado);
        c.setMarca(txtMarca.getText().trim());
        c.setModelo(txtModelo.getText().trim());
        c.setDescripcion(txtDescripcion.getText().trim());
        try {
            c.setPrecio(Double.parseDouble(txtPrecio.getText().trim().replace(',', '.')));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ingrese un precio válido (ej.: 449.00)");
        }
        c.setEstado((String) cboEstado.getSelectedItem());
        return c;
    }

    public void error() {
        Componentes.sacudir(tarjetaFormulario);
        Componentes.destello(tarjetaFormulario, Estilo.ROJO);
    }

    public void exito() {
        Componentes.destello(tarjetaFormulario, Estilo.VERDE);
    }

    public Integer getCodigoSeleccionado() {
        return codigoSeleccionado;
    }

    public String getFiltro() {
        return txtBuscar.getText().trim();
    }

    public boolean isSoloActivos() {
        return chkActivos.isSelected();
    }

    public JTextField getTxtBuscar() {
        return txtBuscar;
    }

    public JButton getBtnBuscar() {
        return btnBuscar;
    }

    public JButton getBtnGuardar() {
        return btnGuardar;
    }

    public JButton getBtnEliminar() {
        return btnEliminar;
    }

    public JCheckBox getChkActivos() {
        return chkActivos;
    }
}
