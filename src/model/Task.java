package model;

import state.task.PendingTaskState;
import state.task.TaskState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Representa una tarea dentro de un proyecto.
 * Aplica el patrón State para gestionar su ciclo de vida.
 *
 * Una tarea puede ser secuencial (depende de que la anterior esté aprobada)
 * o paralela (puede trabajarse al mismo tiempo que otras del mismo grupo).
 */
public class Task {

    private final String id;
    private String titulo;
    private String descripcion;
    private String asignadoAId;
    private String asignadoANombre;
    private TaskState estadoActual;
    private final List<StateChange> historial;
    private String ultimoMensaje;

    // Orden de ejecución: tareas con el mismo orden son paralelas,
    // las de orden mayor esperan a que todas las anteriores estén aprobadas
    private final int orden;
    private String justificacionRechazo;

    public Task(String titulo, String descripcion, String asignadoAId,
                String asignadoANombre, int orden) {
        this.id               = UUID.randomUUID().toString();
        this.titulo           = titulo;
        this.descripcion      = descripcion;
        this.asignadoAId      = asignadoAId;
        this.asignadoANombre  = asignadoANombre;
        this.orden            = orden;
        this.estadoActual     = new PendingTaskState();
        this.historial        = new ArrayList<>();
        this.ultimoMensaje    = "";
        historial.add(new StateChange("—", estadoActual.getNombre(),
                asignadoANombre, "Tarea creada y asignada."));
    }

    /**
     * Cambia el estado actual y registra el evento en el historial.
     */
    public void cambiarEstado(TaskState nuevoEstado, String observacion) {
        String anterior    = estadoActual.getNombre();
        this.estadoActual  = nuevoEstado;
        this.ultimoMensaje = observacion;
        historial.add(new StateChange(anterior, nuevoEstado.getNombre(),
                asignadoANombre, observacion));
    }

    /**
     * El gerente aprueba la tarea: pasa a Completada y limpia el rechazo anterior.
     */
    public void aprobarPorGerente(String nombreGerente) {
        String anterior   = estadoActual.getNombre();
        this.estadoActual = new state.task.CompletedTaskState();
        this.justificacionRechazo = null;
        this.ultimoMensaje = "Tarea aprobada por el gerente.";
        historial.add(new StateChange(anterior, estadoActual.getNombre(),
                nombreGerente, ultimoMensaje));
    }

    /**
     * El gerente rechaza la tarea con una justificación.
     * La tarea regresa a En Progreso para que el desarrollador corrija.
     */
    public void rechazarPorGerente(String nombreGerente, String justificacion) {
        String anterior           = estadoActual.getNombre();
        this.estadoActual         = new state.task.InProgressTaskState();
        this.justificacionRechazo = justificacion;
        this.ultimoMensaje        = "Rechazada: " + justificacion;
        historial.add(new StateChange(anterior, estadoActual.getNombre(),
                nombreGerente, "Rechazada — " + justificacion));
    }

    /** Registra un intento de acción inválida sin cambiar el estado. */
    public void registrarAccionInvalida(String mensaje) {
        this.ultimoMensaje = "⚠ " + mensaje;
    }

    // Delegación al estado actual
    public void iniciar()         { estadoActual.iniciar(this); }
    public void marcarComoLista() { estadoActual.enviarARevision(this); }
    public void bloquear()        { estadoActual.bloquear(this); }
    public void desbloquear()     { estadoActual.desbloquear(this); }

    public boolean estaCompletada() {
        return estadoActual.getNombre().equals("Completada");
    }

    public boolean esperaAprobacion() {
        return estadoActual.getNombre().equals("Pendiente de Aprobación");
    }

    public boolean estaBloqueada() {
        return estadoActual.getNombre().equals("Bloqueada");
    }

    public boolean estaDisponible() {
        String nombre = estadoActual.getNombre();
        return nombre.equals("Pendiente") || nombre.equals("En Progreso")
            || nombre.equals("Bloqueada");
    }

    public String getId()                  { return id; }
    public String getTitulo()              { return titulo; }
    public String getDescripcion()         { return descripcion; }
    public String getAsignadoAId()         { return asignadoAId; }
    public String getAsignadoANombre()     { return asignadoANombre; }
    public int    getOrden()               { return orden; }
    public String getEstadoNombre()        { return estadoActual.getNombre(); }
    public String getUltimoMensaje()       { return ultimoMensaje; }
    public String getJustificacionRechazo(){ return justificacionRechazo; }
    public List<StateChange> getHistorial(){ return Collections.unmodifiableList(historial); }

    public void setTitulo(String titulo)               { this.titulo = titulo; }
    public void setDescripcion(String descripcion)     { this.descripcion = descripcion; }
    public void setAsignadoAId(String id)              { this.asignadoAId = id; }
    public void setAsignadoANombre(String nombre)      { this.asignadoANombre = nombre; }
}
