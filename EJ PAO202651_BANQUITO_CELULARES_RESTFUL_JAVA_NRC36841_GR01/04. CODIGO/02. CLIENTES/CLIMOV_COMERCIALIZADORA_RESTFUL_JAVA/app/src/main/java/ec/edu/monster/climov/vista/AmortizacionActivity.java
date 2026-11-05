package ec.edu.monster.climov.vista;

import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.monster.climov.R;
import ec.edu.monster.climov.controlador.ControladorComercializadora;
import ec.edu.monster.climov.modelo.Credito;
import java.util.List;

/**
 * Consulta de la tabla de amortización por cédula: muestra los créditos del
 * cliente en el Banco BanQuito y la tabla del crédito elegido.
 *
 * @author Grupo 1 - NRC 36841
 */
public class AmortizacionActivity extends AppCompatActivity {

    private ControladorComercializadora controlador;
    private EditText txtCedula;
    private LinearLayout contenido;
    private ProgressBar progreso;
    private List<Credito> creditos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        Pantalla.ajustarTeclado(this);
        controlador = new ControladorComercializadora(this);
        ((TextView) findViewById(R.id.lblTitulo)).setText("Tabla de amortización");
        ((TextView) findViewById(R.id.lblSubtitulo)).setText("Crédito directo BanQuito · 16.5 % anual");
        findViewById(R.id.btnCabecera).setOnClickListener(v -> finish());
        txtCedula = findViewById(R.id.txtBuscar);
        txtCedula.setHint("Cédula del cliente");
        txtCedula.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtCedula.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        contenido = findViewById(R.id.contenido);
        progreso = findViewById(R.id.progreso);
        findViewById(R.id.btnBuscar).setOnClickListener(v -> consultar());
        txtCedula.setOnEditorActionListener((v, a, e) -> {
            consultar();
            return a == EditorInfo.IME_ACTION_SEARCH || a == EditorInfo.IME_ACTION_DONE;
        });
        Filas.aviso(contenido, "Ingrese la cédula para ver sus créditos y la tabla de amortización", Filas.TENUE);
    }

    private void consultar() {
        progreso.setVisibility(View.VISIBLE);
        controlador.creditosCliente(txtCedula.getText().toString().trim(), l -> {
            progreso.setVisibility(View.INVISIBLE);
            creditos = l;
            mostrar(l.isEmpty() ? null : l.get(0));
        }, e -> {
            progreso.setVisibility(View.INVISIBLE);
            contenido.removeAllViews();
            Filas.aviso(contenido, "✖ " + e.getMessage(), Filas.ROJO);
            Animaciones.sacudir(txtCedula);
        });
    }

    /** Chips con los créditos del cliente y la tabla del seleccionado. */
    private void mostrar(Credito sel) {
        contenido.removeAllViews();
        if (sel == null) {
            Filas.aviso(contenido, "El cliente no tiene créditos en el Banco BanQuito", Filas.TENUE);
            return;
        }
        HorizontalScrollView hs = new HorizontalScrollView(this);
        LinearLayout chips = new LinearLayout(this);
        chips.setOrientation(LinearLayout.HORIZONTAL);
        for (Credito c : creditos) {
            TextView chip = Filas.texto(this, "N.º " + c.getCodigoCredito() + " · $ " + Filas.dinero(c.getMonto()) + " · " + c.getEstado(),
                    c == sel ? Filas.DORADO : Filas.TEXTO, 13, c == sel);
            chip.setBackgroundResource(R.drawable.chip);
            int p = Filas.dp(this, 10);
            chip.setPadding(p + 4, p, p + 4, p);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.rightMargin = Filas.dp(this, 8);
            chip.setLayoutParams(lp);
            chip.setOnClickListener(v -> mostrar(c));
            chips.addView(chip);
        }
        hs.addView(chips);
        hs.setPadding(0, 0, 0, Filas.dp(this, 10));
        contenido.addView(hs);
        TextView cliente = Filas.texto(this, sel.getNombre() + " · C.I. " + sel.getCedula() + " · " + sel.getFecha(), Filas.TENUE, 13, false);
        cliente.setPadding(0, 0, 0, Filas.dp(this, 8));
        contenido.addView(cliente);
        Filas.tablaAmortizacion(contenido, sel);
        Animaciones.aparecer(contenido, 0);
    }
}
