package app.domain.model;

/**
 * Enum para gestionar las diferentes fases de un conductor durante un viaje asignado.
 */
public enum TripPhase {
    NONE,                   // No está en un viaje
    MOVING_TO_PICKUP,       // Yendo a recoger al pasajero
    WAITING_AT_PICKUP,      // Esperando en el punto de recogida
    MOVING_TO_DESTINATION,  // Llevando al pasajero a su destino
    WAITING_AT_DESTINATION  // Esperando en el destino final antes de volver a estar disponible
}
