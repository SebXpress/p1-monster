package ec.edu.monster.climov.controlador;

import android.os.Handler;
import android.os.Looper;
import ec.edu.monster.climov.modelo.ExcepcionCliente;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Ejecuta las llamadas al servidor en un hilo secundario y entrega el
 * resultado en el hilo principal (Android no permite red en el hilo de UI).
 *
 * @author Grupo 1 - NRC 36841
 */
public final class Tareas {

    public interface Trabajo<T> {
        T ejecutar() throws ExcepcionCliente;
    }

    public interface Exito<T> {
        void listo(T resultado);
    }

    public interface Fallo {
        void error(ExcepcionCliente e);
    }

    private static final ExecutorService HILOS = Executors.newFixedThreadPool(2);
    private static final Handler UI = new Handler(Looper.getMainLooper());

    private Tareas() {
    }

    public static <T> void ejecutar(Trabajo<T> trabajo, Exito<T> exito, Fallo fallo) {
        HILOS.execute(() -> {
            try {
                T r = trabajo.ejecutar();
                UI.post(() -> exito.listo(r));
            } catch (ExcepcionCliente e) {
                UI.post(() -> fallo.error(e));
            } catch (RuntimeException e) {
                UI.post(() -> fallo.error(new ExcepcionCliente("Error: " + e.getMessage(), 0)));
            }
        });
    }
}
