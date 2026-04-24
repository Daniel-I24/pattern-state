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
 */
public class Task {

    private final String id;
    private final String titulo;
    private String descripcion;
    private String asignadoA;
    private TaskState estadoActual;
    private final List<StateChange> historial;
    private String ultimoMensaje;

    public Task(String titulo, String descripcion, String asignadoA) {
        this.id           = UUID.randomUUID().toString();
        this.titulo       = titulo;
        this.descripcion  = descripcion;
        this.asignadoA    = asignadoA;
        this.estadoActual = new PendingTaskState();
        this.historial    = new ArrayList<>();
        this.ultimoMensaje = "";
        historial.add(new StateChange("—", estadoActual.getNombre(), asignadoA, "Tarea creada."));
    }

    /**
     * Cambia el estado actual y registra el cambio en el historial.
     */
    public void cambiarEstado(TaskState nuevoEstado, String observacion) {
        String estadoAnterior = estadoActual.getNombre();
        this.estadoActual = nuevoEstado;
        this.ultimoMensaje = observacion;
        historial.add(new StateChange(estadoAnterior, nuevoEstado.getNombre(), asignadoA, observacion));
    }

    /**
     * Registra un intento de acción inválida sin cambiar el estado.
     */
    public void registrarAccionInvalida(String mensaje) {
        this.ultimoMensaje = "⚠ " + mensaje;
    }

    // Delegación al estado actual
    public void iniciar()          { estadoActual.iniciar(this); }
    public void enviarARevision()  { estadoActual.enviarARevision(this); }
    public void completar()        { estadoActual.completar(this); }
    public void bloquear()         { estadoActual.bloquear(this); }
    public void desbloquear()      { estadoActual.desbloquear(this); }

    public boolean estaCompletada() {
        return estadoActual.getNombre().equals("Completada");
    }

    public String getId()              { return id; }
    public String getTitulo()          { return titulo; }
    public String getDescripcion()     { return descripcion; }
    public String getAsignadoA()       { return asignadoA; }
    public String getEstadoNombre()    { return estadoActual.getNombre(); }
    public String getUltimoMensaje()   { return ultimoMensaje; }
    public List<StateChange> getHistorial() { return Collections.unmodifiableList(historial); }

    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setAsignadoA(String asignadoA)     { this.asignadoA = asignadoA; }
}
