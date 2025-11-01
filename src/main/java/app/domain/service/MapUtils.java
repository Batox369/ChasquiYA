package app.domain.service;

import app.domain.model.Zona;

public class MapUtils {

    // 1. Definimos el "bounding box" (caja delimitadora) de tus 6 zonas.
    private static final double MIN_LONGITUD = -77.065842; // Los Olivos
    private static final double MAX_LONGITUD = -76.953611; // La Molina

    private static final double MIN_LATITUD = -12.121389; // Miraflores
    private static final double MAX_LATITUD = -11.984013; // Los Olivos

    // 2. Definimos el rango geográfico
    private static final double RANGO_LONGITUD = MAX_LONGITUD - MIN_LONGITUD;
    private static final double RANGO_LATITUD = MAX_LATITUD - MIN_LATITUD;

    // 3. Padding (margen) para que los nodos no queden pegados al borde
    private static final double PADDING_FACTOR = 0.15; // 15% de padding

    /**
     * Escala las coordenadas geográficas (lat, lon) a coordenadas de píxeles (x, y)
     * para que encajen en el tamaño del panel.
     */
    public static void escalarZona(Zona zona, double anchoMapa, double altoMapa) {

        // --- CÁLCULO DEL ÁREA DE DIBUJO ---
        // (AQUÍ ESTABA EL ERROR: DEBE SER 1.0 MENOS EL PADDING)

        // 1. Calculamos el ancho y alto del área donde SÍ podemos dibujar
        double anchoDibujo = anchoMapa * (1.0 - (PADDING_FACTOR * 2));
        double altoDibujo = altoMapa * (1.0 - (PADDING_FACTOR * 2));

        // 2. Calculamos el offset (el padding en píxeles)
        // (AQUÍ ESTABA EL OTRO ERROR: TU CÁLCULO DE OFFSET ERA INCORRECTO)
        double offsetX = anchoMapa * PADDING_FACTOR;
        double offsetY = altoMapa * PADDING_FACTOR;

        // --- CÁLCULO DE POSICIÓN ---

        // 3. Convertimos (lon, lat) a un porcentaje (0.0 a 1.0)
        double x_percent = (zona.getLongitud() - MIN_LONGITUD) / RANGO_LONGITUD;
        double y_percent = (zona.getLatitud() - MIN_LATITUD) / RANGO_LATITUD;

        // 4. Calculamos la coordenada X final
        double x = offsetX + (x_percent * anchoDibujo);

        // 5. Calculamos la coordenada Y final (invertida)
        // (1.0 - y_percent) porque la Latitud crece hacia ARRIBA,
        // pero los píxeles Y crecen hacia ABAJO.
        double y = offsetY + ((1.0 - y_percent) * altoDibujo);

        // 6. Guardamos los píxeles (x, y) calculados en el objeto Zona
        zona.setCoordenadasMapa(x, y);
    }
}