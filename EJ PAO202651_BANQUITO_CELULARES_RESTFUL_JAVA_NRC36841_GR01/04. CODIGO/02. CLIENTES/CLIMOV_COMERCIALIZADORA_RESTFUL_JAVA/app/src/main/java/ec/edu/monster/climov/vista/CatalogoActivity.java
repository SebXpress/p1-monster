package ec.edu.monster.climov.vista;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.climov.R;
import ec.edu.monster.climov.controlador.ControladorComercializadora;
import ec.edu.monster.climov.modelo.Celular;
import java.util.List;
import java.util.Locale;

/**
 * Mantenimiento del catálogo de teléfonos celulares: listar/buscar, crear,
 * editar y eliminar (toque un celular para editarlo).
 *
 * @author Grupo 1 - NRC 36841
 */
public class CatalogoActivity extends AppCompatActivity {

    private static final String[] ESTADOS = {"ACTIVO", "INACTIVO"};
    private ControladorComercializadora controlador;
    private EditText txtBuscar;
    private CheckBox chkActivos;
    private LinearLayout contenido;
    private ProgressBar progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        Pantalla.ajustarTeclado(this);
        controlador = new ControladorComercializadora(this);
        ((TextView) findViewById(R.id.lblTitulo)).setText("Catálogo");
        ((TextView) findViewById(R.id.lblSubtitulo)).setText("Teléfonos celulares a la venta");
        findViewById(R.id.btnCabecera).setOnClickListener(v -> finish());
        txtBuscar = findViewById(R.id.txtBuscar);
        txtBuscar.setHint("Marca o modelo");
        chkActivos = findViewById(R.id.chkActivos);
        chkActivos.setVisibility(View.VISIBLE);
        chkActivos.setOnCheckedChangeListener((b, c) -> cargar());
        contenido = findViewById(R.id.contenido);
        progreso = findViewById(R.id.progreso);
        findViewById(R.id.btnBuscar).setOnClickListener(v -> cargar());
        txtBuscar.setOnEditorActionListener((v, a, e) -> {
            if (a == EditorInfo.IME_ACTION_SEARCH) {
                cargar();
                return true;
            }
            return false;
        });
        Button nuevo = findViewById(R.id.btnAccion);
        nuevo.setVisibility(View.VISIBLE);
        nuevo.setText("➕ Nuevo celular");
        nuevo.setOnClickListener(v -> formulario(null));
        cargar();
    }

    private void cargar() {
        progreso.setVisibility(View.VISIBLE);
        controlador.listarCelulares(txtBuscar.getText().toString().trim(), chkActivos.isChecked(), this::mostrar, e -> {
            progreso.setVisibility(View.INVISIBLE);
            contenido.removeAllViews();
            Filas.aviso(contenido, "✖ " + e.getMessage(), Filas.ROJO);
        });
    }

    private void mostrar(List<Celular> lista) {
        progreso.setVisibility(View.INVISIBLE);
        contenido.removeAllViews();
        if (lista.isEmpty()) {
            Filas.aviso(contenido, "No hay celulares", Filas.TENUE);
        }
        int i = 0;
        for (Celular c : lista) {
            LinearLayout t = Filas.tarjeta(this);
            LinearLayout arriba = new LinearLayout(this);
            arriba.setOrientation(LinearLayout.HORIZONTAL);
            TextView nombre = Filas.texto(this, c.getCodigo() + " · " + c.getMarca() + " " + c.getModelo(), Filas.TEXTO, 16, true);
            nombre.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
            arriba.addView(nombre);
            arriba.addView(Filas.texto(this, "$ " + Filas.dinero(c.getPrecio()), Filas.DORADO, 16, true));
            t.addView(arriba);
            if (c.getDescripcion() != null && !c.getDescripcion().isEmpty()) {
                t.addView(Filas.texto(this, c.getDescripcion(), Filas.TENUE, 13, false));
            }
            t.addView(Filas.texto(this, c.getEstado(), "ACTIVO".equals(c.getEstado()) ? Filas.VERDE : Filas.ROJO, 12, true));
            t.setOnClickListener(v -> formulario(c));
            contenido.addView(t);
            Animaciones.aparecer(t, 40L * i++);
        }
    }

    /** Diálogo para crear (c == null) o editar un celular. */
    private void formulario(Celular c) {
        View f = LayoutInflater.from(this).inflate(R.layout.dialogo_celular, null);
        EditText marca = f.findViewById(R.id.txtMarca);
        EditText modelo = f.findViewById(R.id.txtModelo);
        EditText descripcion = f.findViewById(R.id.txtDescripcion);
        EditText precio = f.findViewById(R.id.txtPrecio);
        Spinner estado = f.findViewById(R.id.cboEstado);
        TextView error = f.findViewById(R.id.lblError);
        estado.setAdapter(new ArrayAdapter<>(this, R.layout.item_spinner, ESTADOS));
        if (c != null) {
            marca.setText(c.getMarca());
            modelo.setText(c.getModelo());
            descripcion.setText(c.getDescripcion());
            precio.setText(String.format(Locale.US, "%.2f", c.getPrecio()));
            estado.setSelection("INACTIVO".equals(c.getEstado()) ? 1 : 0);
        }
        AlertDialog.Builder b = new AlertDialog.Builder(this)
                .setTitle(c == null ? "Nuevo celular" : "Editar celular N.º " + c.getCodigo())
                .setView(f)
                .setPositiveButton("Guardar", null)
                .setNegativeButton("Cancelar", null);
        if (c != null) {
            b.setNeutralButton("Eliminar", null);
        }
        AlertDialog d = b.create();
        d.setOnShowListener(x -> {
            d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                Celular n = new Celular();
                n.setCodigo(c == null ? null : c.getCodigo());
                n.setMarca(marca.getText().toString().trim());
                n.setModelo(modelo.getText().toString().trim());
                n.setDescripcion(descripcion.getText().toString().trim());
                n.setEstado(ESTADOS[estado.getSelectedItemPosition()]);
                try {
                    n.setPrecio(Double.parseDouble(precio.getText().toString().trim().replace(',', '.')));
                } catch (NumberFormatException e) {
                    mostrarError(error, f, "Ingrese un precio válido (ej.: 449.00)");
                    return;
                }
                controlador.guardarCelular(n, r -> {
                    d.dismiss();
                    Toast.makeText(this, controlador.getUltimoMensaje() + ": " + r.nombre(), Toast.LENGTH_LONG).show();
                    cargar();
                }, e -> mostrarError(error, f, e.getMessage()));
            });
            if (c != null) {
                d.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> new AlertDialog.Builder(this)
                        .setMessage("¿Eliminar el celular " + c.nombre() + "?")
                        .setPositiveButton("Eliminar", (z, w) -> controlador.eliminarCelular(c.getCodigo(), m -> {
                            d.dismiss();
                            Toast.makeText(this, m, Toast.LENGTH_LONG).show();
                            cargar();
                        }, e -> mostrarError(error, f, e.getMessage())))
                        .setNegativeButton("No", null).show());
            }
        });
        d.show();
    }

    private static void mostrarError(TextView error, View formulario, String mensaje) {
        error.setVisibility(View.VISIBLE);
        error.setText("✖ " + mensaje);
        Animaciones.sacudir(formulario);
    }
}
