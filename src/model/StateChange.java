package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Registro inmutable de un cambio de estado.
 * Se usa tanto para proyectos como para tareas.
 */
public class StateChange {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final String estadoAnterior;
    private final String estadoNuevo;
    private final String responsable;
    private final LocalDateTime fecha;
    private final String observacion;

    public StateChange(String estadoAnterior, String estadoNuevo, String responsable, String observacion) {
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo    = estadoNuevo;
        this.responsable    = responsable;
        this.observacion    = observacion;
        this.fecha          = LocalDateTime.now();
    }

    public String getEstadoAnterior() { return estadoAnterior; }
    public String getEstadoNuevo()    { return estadoNuevo; }
    public String getResponsable()    { return responsable; }
    public String getObservacion()    { return observacion; }
    public LocalDateTime getFecha()   { return fecha; }

    public String getFechaFormateada() {
        return fecha.format(FORMATO);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s → %s  |  %s  |  %s",
                getFechaFormateada(), estadoAnterior, estadoNuevo, responsable, observacion);
    }
}
