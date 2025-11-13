package app.domain.service;

import app.domain.model.*;

import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

/**
 * Gestiona la simulación del movimiento de los conductores en el mapa.
 * Utiliza un Timer para actualizar las posiciones de forma periódica.
 */
public class SimuladorMovimientoConductores {

    private static final int VELOCIDAD_M_POR_S = 5; // 10 metros por segundo
    private static final int INTERVALO_ACTUALIZACION_MS = 10; // Actualiza la pantalla ~20 veces por segundo

    private final Timer timer;
    private final List<Conductor> conductores;
    private final GrafoZonas grafo;
    private final Random random = new Random();
    private final Runnable onUpdateCallback;

    /**
     * Constructor del simulador.
     * @param conductores La lista de conductores a simular.
     * @param grafo El grafo de zonas para saber las rutas posibles.
     * @param onUpdateCallback Una acción (como `mapa.repaint()`) que se ejecutará cada vez que se actualice una posición.
     */
    public SimuladorMovimientoConductores(List<Conductor> conductores, GrafoZonas grafo, Runnable onUpdateCallback) {
        this.conductores = conductores;
        this.grafo = grafo;
        this.onUpdateCallback = onUpdateCallback;

        inicializarPosiciones();

        // El ActionListener que se ejecutará en cada "tick" del timer.
        ActionListener taskPerformer = evt -> {
            actualizarPosiciones();
            this.onUpdateCallback.run(); // Llama a repaint() en el mapaPanel
        };

        this.timer = new Timer(INTERVALO_ACTUALIZACION_MS, taskPerformer);
    }

    /**
     * Coloca a cada conductor en su zona inicial.
     */
    private void inicializarPosiciones() {
        for (Conductor conductor : conductores) {
            if (conductor.getZonaActualId() != null) {
                Zona zonaActual = grafo.getZona(conductor.getZonaActualId());
                if (zonaActual != null) {
                    conductor.setPosicionActual(new Coordenada(zonaActual.getLongitud(), zonaActual.getLatitud()));
                }
            }
        }
    }

    /**
     * Inicia la simulación.
     */
    public void start() {
        timer.start();
        System.out.println("▶️  Simulador de movimiento iniciado.");
    }

    /**
     * Detiene la simulación.
     */
    public void stop() {
        timer.stop();
        System.out.println("⏹️  Simulador de movimiento detenido.");
    }

    /**
     * El corazón de la simulación. Se llama en cada tick del timer.
     */
    private void actualizarPosiciones() {
        long tiempoActual = System.currentTimeMillis();

        for (Conductor conductor : conductores) {
            // Si el conductor está OCUPADO (en movimiento)
            if (conductor.getEstado() == EstadoConductor.OCUPADO) {
                long tiempoTranscurrido = tiempoActual - conductor.getTiempoInicioViajeMs();
                double progreso = (double) tiempoTranscurrido / conductor.getDuracionViajeMs();

                if (progreso >= 1.0) {
                    // El viaje ha terminado, llegó a la zona de destino.
                    conductor.setEstado(EstadoConductor.DISPONIBLE);
                    conductor.setZonaActualId(conductor.getZonaDestinoViaje().getId());
                    conductor.setPosicionActual(new Coordenada(conductor.getZonaDestinoViaje().getLongitud(), conductor.getZonaDestinoViaje().getLatitud()));
                } else {
                    // El viaje está en curso, calculamos la posición intermedia (interpolación lineal).
                    double x = conductor.getZonaOrigenViaje().getLongitud() + (conductor.getZonaDestinoViaje().getLongitud() - conductor.getZonaOrigenViaje().getLongitud()) * progreso;
                    double y = conductor.getZonaOrigenViaje().getLatitud() + (conductor.getZonaDestinoViaje().getLatitud() - conductor.getZonaOrigenViaje().getLatitud()) * progreso;
                    conductor.setPosicionActual(new Coordenada(x, y));
                }
            }
            // Si el conductor está DISPONIBLE (esperando en una zona)
            else if (conductor.getEstado() == EstadoConductor.DISPONIBLE) {
                // Decidimos si inicia un nuevo viaje aleatorio.
                iniciarViajeAleatorio(conductor);
            }
        }
    }

    private void iniciarViajeAleatorio(Conductor conductor) {
        Zona zonaActual = grafo.getZona(conductor.getZonaActualId());
        if (zonaActual == null) return;

        List<Arista> aristasSalientes = grafo.getAristasSalientes(zonaActual);
        if (aristasSalientes.isEmpty()) return; // No tiene a dónde ir.

        // Elige una arista (ruta) al azar
        Arista aristaElegida = aristasSalientes.get(random.nextInt(aristasSalientes.size()));
        Zona zonaDestino = grafo.getZona(aristaElegida.getIdZonaB());

        // Calcula cuánto tardará el viaje en milisegundos
        double distancia = aristaElegida.getDistancia(); // Distancia en metros
        long duracionMs = (long) ((distancia / VELOCIDAD_M_POR_S) * 1000);

        // Inicia el tramo
        conductor.iniciarTramo(zonaActual, zonaDestino, duracionMs);
    }
}