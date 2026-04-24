package model;

import state.project.ProjectState;
import state.project.ProposedState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Contexto principal del patrón State.
 * Representa un proyecto de software con su ciclo de vida completo.
 */
public class Project {

    private final String id;
    private String nombre;
    private String descripcion;
    private final String creadoPor;
    private ProjectState estadoActual;
    private final List<Task> tareas;
    private final List<StateChange> historial;
    private String ultimoMensaje;

    public Project(String nombre, String descripcion, String creadoPor) {
        this.id           = UUID.randomUUID().toString();
        this.nombre       = nombre;
        this.descripcion  = descripcion;
        this.creadoPor    = creadoPor;
        this.estadoActual = new ProposedState();
        this.tareas       = new ArrayList<>();
        this.historial    = new ArrayList<>();
        this.ultimoMensaje = "";
        historial.add(new StateChange("—", estadoActual.getNombre(), creadoPor, "Proyecto creado."));
    }

    /**
     * Cambia el estado actual y registra el evento en el historial.
     */
    public void cambiarEstado(ProjectState nuevoEstado, String observacion) {
        String estadoAnterior = estadoActual.getNombre();
        this.estadoActual  = nuevoEstado;
        this.ultimoMensaje = observacion;
        historial.add(new StateChange(estadoAnterior, nuevoEstado.getNombre(), creadoPor, observacion));
    }

    /**
     * Registra un intento de acción inválida sin cambiar el estado.
     */
    public void registrarAccionInvalida(String mensaje) {
        this.ultimoMensaje = "⚠ " + mensaje;
    }

    // Delegación al estado actual
    public void evaluar()                { estadoActual.evaluar(this); }
    public void aprobar()                { estadoActual.aprobar(this); }
    public void rechazar()               { estadoActual.rechazar(this); }
    public void iniciarPlanificacion()   { estadoActual.iniciarPlanificacion(this); }
    public void iniciarDesarrollo()      { estadoActual.iniciarDesarrollo(this); }
    public void iniciarPruebas()         { estadoActual.iniciarPruebas(this); }
    public void enviarARevisionCliente() { estadoActual.enviarARevisionCliente(this); }
    public void solicitarCorrecciones()  { estadoActual.solicitarCorrecciones(this); }
    public void aceptar()                { estadoActual.aceptar(this); }
    public void desplegar()              { estadoActual.desplegar(this); }
    public void pasarAMantenimiento()    { estadoActual.pasarAMantenimiento(this); }
    public void archivar()               { estadoActual.archivar(this); }

    public void agregarTarea(Task tarea) {
        tareas.add(tarea);
    }

    public boolean tieneTareas() {
        return !tareas.isEmpty();
    }

    public boolean todasLasTareasCompletadas() {
        return !tareas.isEmpty() && tareas.stream().allMatch(Task::estaCompletada);
    }

    public String getId()                  { return id; }
    public String getNombre()              { return nombre; }
    public String getDescripcion()         { return descripcion; }
    public String getCreadoPor()           { return creadoPor; }
    public String getEstadoNombre()        { return estadoActual.getNombre(); }
    public String getEstadoDescripcion()   { return estadoActual.getDescripcion(); }
    public String getUltimoMensaje()       { return ultimoMensaje; }
    public List<Task> getTareas()          { return Collections.unmodifiableList(tareas); }
    public List<StateChange> getHistorial(){ return Collections.unmodifiableList(historial); }

    public void setNombre(String nombre)           { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() { return nombre; }
}
