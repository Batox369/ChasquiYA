package domain.service;

import domain.model.Conductor;

public class Sistema {
    private static Sistema instancia;
    private GestorZonas gestorZonas;

    private Sistema() {
        gestorZonas = new GestorZonas();
        inicializarDatosBase();
    }

    public static Sistema getInstancia() {
        if (instancia == null) {
            instancia = new Sistema();
        }
        return instancia;
    }

    public GestorZonas getGestorZonas() {
        return gestorZonas;
    }

    private void inicializarDatosBase() {
        gestorZonas.insertarZona(1, "Centro");
        gestorZonas.insertarZona(3, "Norte");
        gestorZonas.insertarZona(5, "Sur");
    }
    public java.util.List<String> obtenerNombresZonas() {
        return gestorZonas.obtenerNombresZonas();
    }
    public void agregarConductor(String nombre, String zona) {
        if (nombre == null || nombre.isBlank()) {
            System.out.println("⚠️ No se puede agregar un conductor sin nombre.");
            return;
        }
        if (zona == null || zona.isBlank()) {
            System.out.println("⚠️ No se puede agregar un conductor sin zona.");
            return;
        }

        Conductor nuevo = new Conductor(nombre);
        gestorZonas.agregarConductorAZona(zona, nuevo);
    }

    public void imprimirZonasYConductores() {
        gestorZonas.imprimirArbolYConductores();
    }

}
