package app.domain.service;

import app.domain.model.*;
import app.ui.MainFrame;

import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Random;

/**
 * Gestiona la simulación del movimiento de los conductores en el mapa.
 * Utiliza un Timer para actualizar las posiciones de forma periódica.
 */
public class SimuladorMovimientoConductores {

    private static final int VELOCIDAD_M_POR_S = 10; // 10 metros por segundo
    private static final int INTERVALO_ACTUALIZACION_MS = 10; // Actualiza la pantalla ~20 veces por segundo

    private final Timer timer;
    private final List<Conductor> conductores;
    private final GrafoZonas grafo;
    private final Random random = new Random();
    private final GestorRutas gestorRutas = new GestorRutas(); // El simulador ahora necesita su propio gestor de rutas
    private final MainFrame mainFrame; // <-- ¡NUEVO! Referencia al frame principal
    private final Runnable onUpdateCallback;

    /**
     * Constructor del simulador.
     * @param mainFrame La instancia del frame principal para acceder a datos globales como el viaje actual.
     * @param conductores La lista de conductores a simular.
     * @param grafo El grafo de zonas para saber las rutas posibles.
     * @param onUpdateCallback Una acción (como `mapa.repaint()`) que se ejecutará cada vez que se actualice una posición.
     */
    public SimuladorMovimientoConductores(MainFrame mainFrame, List<Conductor> conductores, GrafoZonas grafo, Runnable onUpdateCallback) {
        this.mainFrame = mainFrame; // <-- ¡NUEVO!
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
            // --- ¡NUEVA LÓGICA UNIFICADA! ---

            // 1. Si el conductor no tiene un destino (está quieto en un nodo).
            if (conductor.getZonaDestinoViaje() == null) {
                if (conductor.getEstado() == EstadoConductor.OCUPADO) {
                    // Está OCUPADO y quieto: es el momento de calcular su ruta de recogida.
                    if (conductor.getRutaAsignada() == null) {
                        calcularYAsignarRutaDeRecogida(conductor);
                    }
                    // Una vez calculada (o si ya la tenía), inicia el siguiente tramo.
                    iniciarSiguienteTramo(conductor);
                } else {
                    // Está DISPONIBLE y quieto: inicia un viaje aleatorio.
                    iniciarViajeAleatorio(conductor);
                }
                // En cualquier caso, si se le asignó un nuevo destino, pasamos al siguiente tick.
                if (conductor.getZonaDestinoViaje() != null) {
                    continue;
                }
            }

            // --- ¡CORRECCIÓN! ---
            // Si el conductor no tiene un destino, no hay movimiento que procesar.
            if (conductor.getZonaDestinoViaje() == null) continue;

            // 2. Si el conductor SÍ tiene un destino (está en movimiento entre dos nodos).
            long tiempoTranscurrido = tiempoActual - conductor.getTiempoInicioViajeMs();
            double progreso = (double) tiempoTranscurrido / conductor.getDuracionViajeMs();

            if (progreso >= 1.0) {
                // Ha llegado al final de un tramo.
                Zona zonaAlcanzada = conductor.getZonaDestinoViaje();
                conductor.setZonaActualId(zonaAlcanzada.getId());
                conductor.setPosicionActual(new Coordenada(zonaAlcanzada.getLongitud(), zonaAlcanzada.getLatitud()));
                conductor.iniciarTramo(zonaAlcanzada, null, 0); // Detiene el movimiento temporalmente.

                // Ahora que está quieto, en el siguiente tick, la lógica del punto 1 decidirá qué hacer.

            } else {
                // Sigue en movimiento.
                double x = conductor.getZonaOrigenViaje().getLongitud() + (conductor.getZonaDestinoViaje().getLongitud() - conductor.getZonaOrigenViaje().getLongitud()) * progreso;
                double y = conductor.getZonaOrigenViaje().getLatitud() + (conductor.getZonaDestinoViaje().getLatitud() - conductor.getZonaOrigenViaje().getLatitud()) * progreso;
                conductor.setPosicionActual(new Coordenada(x, y));
            }
        }
    }

    private void calcularYAsignarRutaDeRecogida(Conductor conductor) {
        System.out.println("[SIM-DEBUG] Conductor " + conductor.getNombreCompleto() + " está quieto y OCUPADO. Calculando ruta de recogida.");
        Viaje viajeAsignado = findActiveTripForConductor(conductor.getId());
        if (viajeAsignado != null) {
            Zona zonaOrigenViaje = viajeAsignado.getRutaZonas().get(0);
            Zona zonaActualConductor = grafo.getZona(conductor.getZonaActualId());
            List<Zona> rutaDeRecogida = gestorRutas.calcularRutaMasCorta(grafo, zonaActualConductor, zonaOrigenViaje);
            conductor.setRutaAsignada(rutaDeRecogida);
            System.out.println("[SIM-DEBUG] Ruta de recogida calculada y asignada. Tramos: " + (rutaDeRecogida != null ? rutaDeRecogida.size() : 0));
        }
    }

    // Método de ayuda para encontrar el viaje activo. Esto es una simplificación.
    private Viaje findActiveTripForConductor(int conductorId) {
        // En un sistema real, tendrías una lista de viajes activos.
        // Aquí, accedemos al viaje desde el mapaPanel, asumiendo que solo hay uno.
        Viaje viajeActual = mainFrame.getRMapaPanel().getViajeActual();
        if (viajeActual != null && viajeActual.getConductorId() != null && viajeActual.getConductorId() == conductorId) {
            return viajeActual;
        }
        return null;
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

    /**
     * Inicia el siguiente tramo de la ruta de recogida asignada a un conductor.
     */
    private void iniciarSiguienteTramo(Conductor conductor) {
        // Si el conductor ha llegado al final de su ruta de recogida, se detiene.
        if (conductor.getRutaAsignada() != null && conductor.getZonaActualId() == conductor.getRutaAsignada().get(conductor.getRutaAsignada().size() - 1).getId()) {
            System.out.println("[SIM] Conductor " + conductor.getNombreCompleto() + " ha llegado al punto de recogida.");
            conductor.setRutaAsignada(null); // Limpia la ruta, ya llegó.
            return; // No inicia nuevo tramo.
        }

        List<Zona> ruta = conductor.getRutaAsignada();
        if (ruta == null || ruta.isEmpty()) return;

        Zona zonaActual = grafo.getZona(conductor.getZonaActualId());
        int indiceActual = ruta.indexOf(zonaActual);

        if (indiceActual != -1 && indiceActual < ruta.size() - 1) {
            Zona siguienteZona = ruta.get(indiceActual + 1);
            double distancia = grafo.getDistanciaEntre(zonaActual, siguienteZona);
            long duracionMs = (long) ((distancia / VELOCIDAD_M_POR_S) * 1000);

            System.out.println("[SIM] -> " + conductor.getNombreCompleto() + " yendo de " + zonaActual.getNombre() + " a " + siguienteZona.getNombre());
            conductor.iniciarTramo(zonaActual, siguienteZona, duracionMs);
        }
    }
}