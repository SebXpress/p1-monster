package ec.edu.monster.climov.vista;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.climov.R;
import ec.edu.monster.climov.controlador.ControladorComercializadora;
import ec.edu.monster.climov.modelo.Celular;
import ec.edu.monster.climov.modelo.Evaluacion;
import ec.edu.monster.climov.modelo.SesionApp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Facturación: carrito de teléfonos, comprador y forma de pago.
 * Efectivo: 42 % de descuento. Crédito directo: el Banco BanQuito verifica si
 * el comprador es sujeto de crédito y su monto máximo; si aprueba, otorga el
 * crédito (3 a 18 cuotas, 16.5 % anual) y se factura.
 *
 * @author Grupo 1 - NRC 36841
 */
public class FacturarActivity extends AppCompatActivity {

    private static final double DESCUENTO_EFECTIVO = 0.42;
    private static final double TASA_ANUAL = 16.5;

    private ControladorComercializadora controlador;
    private Spinner cboCelular;
    private EditText txtCantidad, txtCedula, txtNombre, txtDireccion, txtTelefono, txtCorreo, txtCuotas;
    private RadioButton rbCredito;
    private LinearLayout listaCarrito, panelCredito, tarjetaPago;
    private TextView lblTotales, lblTotal, lblCuota, lblMensaje;
    private ProgressBar progreso;
    private List<Celular> celulares = new ArrayList<>();
    /** Código de celular → cantidad. */
    private final Map<Integer, Integer> carrito = new LinkedHashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facturar);
        Pantalla.ajustarTeclado(this);
        controlador = new ControladorComercializadora(this);
        ((TextView) findViewById(R.id.lblTitulo)).setText("Facturar venta");
        ((TextView) findViewById(R.id.lblSubtitulo)).setText("Efectivo o crédito directo BanQuito");
        findViewById(R.id.btnCabecera).setOnClickListener(v -> finish());

        cboCelular = findViewById(R.id.cboCelular);
        txtCantidad = findViewById(R.id.txtCantidad);
        txtCedula = findViewById(R.id.txtCedula);
        txtNombre = findViewById(R.id.txtNombre);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtCorreo = findViewById(R.id.txtCorreo);
        txtCuotas = findViewById(R.id.txtCuotas);
        rbCredito = findViewById(R.id.rbCredito);
        listaCarrito = findViewById(R.id.listaCarrito);
        panelCredito = findViewById(R.id.panelCredito);
        tarjetaPago = findViewById(R.id.tarjetaPago);
        lblTotales = findViewById(R.id.lblTotales);
        lblTotal = findViewById(R.id.lblTotal);
        lblCuota = findViewById(R.id.lblCuota);
        lblMensaje = findViewById(R.id.lblMensaje);
        progreso = findViewById(R.id.progreso);

        findViewById(R.id.btnAgregar).setOnClickListener(v -> agregar());
        findViewById(R.id.btnEvaluar).setOnClickListener(v -> evaluar());
        findViewById(R.id.btnFacturar).setOnClickListener(v -> confirmarFactura());
        findViewById(R.id.btnLimpiar).setOnClickListener(v -> limpiar());
        ((android.widget.RadioGroup) findViewById(R.id.grupoPago)).setOnCheckedChangeListener((g, id) -> {
            panelCredito.setVisibility(rbCredito.isChecked() ? View.VISIBLE : View.GONE);
            lblMensaje.setText(rbCredito.isChecked() ? "El banco verificará si el comprador es sujeto de crédito y su monto máximo." : "");
            lblMensaje.setTextColor(Filas.TENUE);
            refrescar();
        });
        txtCuotas.setOnFocusChangeListener((v, f) -> refrescar());
        cargarCelulares();
        refrescar();
    }

    private void cargarCelulares() {
        progreso.setVisibility(View.VISIBLE);
        controlador.listarCelulares(null, true, l -> {
            progreso.setVisibility(View.INVISIBLE);
            celulares = l;
            cboCelular.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, l));
        }, e -> {
            progreso.setVisibility(View.INVISIBLE);
            mensaje("✖ " + e.getMessage(), Filas.ROJO);
        });
    }

    private void agregar() {
        Object sel = cboCelular.getSelectedItem();
        if (!(sel instanceof Celular)) {
            return;
        }
        int cantidad = entero(txtCantidad, 1);
        cantidad = Math.max(1, Math.min(100, cantidad));
        Celular c = (Celular) sel;
        Integer actual = carrito.get(c.getCodigo());
        carrito.put(c.getCodigo(), Math.min(100, (actual == null ? 0 : actual) + cantidad));
        txtCantidad.setText("1");
        refrescar();
    }

    private Celular buscar(int codigo) {
        for (Celular c : celulares) {
            if (c.getCodigo() == codigo) {
                return c;
            }
        }
        return null;
    }

    private double subtotal() {
        double s = 0;
        for (Map.Entry<Integer, Integer> e : carrito.entrySet()) {
            Celular c = buscar(e.getKey());
            if (c != null) {
                s += c.getPrecio() * e.getValue();
            }
        }
        return Math.round(s * 100) / 100.0;
    }

    /** Redibuja el carrito y la vista previa de totales y cuota. */
    private void refrescar() {
        listaCarrito.removeAllViews();
        float[] pesos = {2.2f, 0.6f, 1.1f, 0.5f};
        if (carrito.isEmpty()) {
            Filas.aviso(listaCarrito, "Agregue teléfonos a la venta", Filas.TENUE);
        } else {
            listaCarrito.addView(Filas.fila(this, new String[]{"Celular", "Cant.", "Subtotal", ""}, pesos, true));
            listaCarrito.addView(Filas.linea(this));
        }
        for (Map.Entry<Integer, Integer> e : new ArrayList<>(carrito.entrySet())) {
            Celular c = buscar(e.getKey());
            if (c == null) {
                continue;
            }
            LinearLayout f = Filas.fila(this, new String[]{c.nombre(), String.valueOf(e.getValue()),
                Filas.dinero(c.getPrecio() * e.getValue()), "✖"}, pesos, false);
            TextView quitar = (TextView) f.getChildAt(3);
            quitar.setTextColor(Filas.ROJO);
            quitar.setOnClickListener(v -> {
                carrito.remove(e.getKey());
                refrescar();
            });
            listaCarrito.addView(f);
        }
        double sub = subtotal();
        boolean credito = rbCredito.isChecked();
        double desc = credito ? 0 : Math.round(sub * DESCUENTO_EFECTIVO * 100) / 100.0;
        lblTotales.setText("Subtotal: $ " + Filas.dinero(sub) + "\nDescuento" + (credito ? "" : " (42 %)") + ": - $ " + Filas.dinero(desc));
        lblTotal.setText("TOTAL $ " + Filas.dinero(sub - desc));
        if (credito && sub > 0) {
            int n = entero(txtCuotas, 6);
            if (n >= 3 && n <= 18) {
                double i = TASA_ANUAL / 100 / 12;
                lblCuota.setText(n + " cuotas estimadas de $ " + Filas.dinero(sub / ((1 - Math.pow(1 + i, -n)) / i)));
            } else {
                lblCuota.setText("El plazo debe ser de 3 a 18 meses");
            }
        } else {
            lblCuota.setText("");
        }
    }

    private void evaluar() {
        refrescar();
        double monto = subtotal();
        if (monto <= 0) {
            mensaje("✖ Agregue al menos un teléfono para consultar el crédito", Filas.ROJO);
            Animaciones.sacudir(tarjetaPago);
            return;
        }
        progreso.setVisibility(View.VISIBLE);
        controlador.evaluarCredito(txtCedula.getText().toString().trim(), monto, ev -> {
            progreso.setVisibility(View.INVISIBLE);
            mostrarEvaluacion(ev, monto);
        }, e -> {
            progreso.setVisibility(View.INVISIBLE);
            mensaje("✖ " + e.getMessage(), Filas.ROJO);
            Animaciones.sacudir(tarjetaPago);
        });
    }

    private void mostrarEvaluacion(Evaluacion ev, double monto) {
        StringBuilder sb = new StringBuilder();
        if (!ev.isEsCliente()) {
            sb.append("✖ No es cliente del Banco BanQuito.");
        } else {
            sb.append(ev.getNombre()).append('\n');
            if (ev.isSujetoCredito()) {
                sb.append("✔ Sujeto de crédito · Monto máximo: $ ").append(Filas.dinero(ev.getMontoMaximo())).append('\n');
                sb.append(ev.isAlcanza() ? "✔ El valor de la compra está aprobado."
                        : "✖ El valor $ " + Filas.dinero(monto) + " supera el monto máximo.");
            } else {
                sb.append("✖ No es sujeto de crédito:");
            }
        }
        for (String m : ev.getMotivos()) {
            sb.append("\n• ").append(m);
        }
        boolean ok = ev.isSujetoCredito() && ev.isAlcanza();
        mensaje(sb.toString(), ok ? Filas.VERDE : Filas.ROJO);
        if (ok) {
            Animaciones.latido(tarjetaPago);
        } else {
            Animaciones.sacudir(tarjetaPago);
        }
        if (ev.getNombre() != null && txtNombre.getText().toString().trim().isEmpty()) {
            txtNombre.setText(ev.getNombre());
        }
    }

    private void confirmarFactura() {
        refrescar();
        new AlertDialog.Builder(this)
                .setTitle("Emitir factura")
                .setMessage(lblTotal.getText() + (rbCredito.isChecked() ? "\nCrédito directo a " + entero(txtCuotas, 0) + " cuotas" : "\nEfectivo"))
                .setPositiveButton("Facturar", (d, w) -> facturar())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void facturar() {
        progreso.setVisibility(View.VISIBLE);
        if (rbCredito.isChecked()) {
            mensaje("Consultando al Banco BanQuito y otorgando el crédito…", Filas.DORADO);
        }
        controlador.facturar(txtCedula.getText().toString().trim(), txtNombre.getText().toString().trim(),
                txtDireccion.getText().toString().trim(), txtTelefono.getText().toString().trim(),
                txtCorreo.getText().toString().trim(), rbCredito.isChecked(), entero(txtCuotas, 0),
                new LinkedHashMap<>(carrito), f -> {
                    progreso.setVisibility(View.INVISIBLE);
                    SesionApp.get().setUltimaFactura(f);
                    limpiar();
                    startActivity(new Intent(this, FacturaActivity.class).putExtra("numero", f.getNumero()));
                }, e -> {
                    progreso.setVisibility(View.INVISIBLE);
                    mensaje("✖ " + e.getMessage(), Filas.ROJO);
                    Animaciones.sacudir(tarjetaPago);
                });
    }

    private void limpiar() {
        carrito.clear();
        for (EditText t : new EditText[]{txtCedula, txtNombre, txtDireccion, txtTelefono, txtCorreo}) {
            t.setText("");
        }
        txtCuotas.setText("6");
        ((RadioButton) findViewById(R.id.rbEfectivo)).setChecked(true);
        lblMensaje.setText("");
        refrescar();
    }

    private void mensaje(String texto, int color) {
        lblMensaje.setText(texto);
        lblMensaje.setTextColor(color);
    }

    private static int entero(EditText t, int porDefecto) {
        try {
            return Integer.parseInt(t.getText().toString().trim());
        } catch (NumberFormatException e) {
            return porDefecto;
        }
    }
}
