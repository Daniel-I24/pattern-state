package model;

import state.project.ProjectState;
import state.project.ProposedState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Contexto principal del patrón State.
 * Representa un proyecto de software con su ciclo de vida completo.
 *
 * Las tareas se organizan por orden: las del mismo número son paralelas,
 * las de número mayor esperan a que todas las anteriores estén aprobadas.
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
        this.id            = UUID.randomUUID().toString();
        this.nombre        = nombre;
        this.descripcion   = descripcion;
        this.creadoPor     = creadoPor;
        this.estadoActual  = new ProposedState();
        this.tareas        = new ArrayList<>();
        this.historial     = new ArrayList<>();
        this.ultimoMensaje = "";
        historial.add(new StateChange("—", estadoActual.getNombre(), creadoPor, "Proyecto creado."));
    }

    /** Cambia el estado actual y registra el evento en el historial. */
    public void cambiarEstado(ProjectState nuevoEstado, String observacion) {
        String anterior    = estadoActual.getNombre();
        this.estadoActual  = nuevoEstado;
        this.ultimoMensaje = observacion;
        historial.add(new StateChange(anterior, nuevoEstado.getNombre(), creadoPor, observacion));
    }

    /** Registra un intento de acción inválida sin cambiar el estado. */
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

    /** Todas las tareas del proyecto están aprobadas por el gerente. */
    public boolean todasLasTareasCompletadas() {
        return !tareas.isEmpty() && tareas.stream().allMatch(Task::estaCompletada);
    }

    /**
     * Retorna las tareas que un desarrollador puede trabajar ahora.
     * Una tarea está disponible si su grupo de orden es el más bajo
     * que aún no está completamente aprobado.
     */
    public List<Task> tareasDisponiblesParaDesarrollador(String desarrolladorId) {
        int grupoActivo = obtenerGrupoActivo();
        return tareas.stream()
                .filter(t -> t.getOrden() == grupoActivo)
                .filter(t -> t.getAsignadoAId().equals(desarrolladorId))
                .collect(Collectors.toList());
    }

    /**
     * Retorna las tareas que el gerente puede aprobar o rechazar
     * (las que están en estado "Pendiente de Aprobación").
     */
    public List<Task> tareasEsperandoAprobacion() {
        return tareas.stream()
                .filter(Task::esperaAprobacion)
                .collect(Collectors.toList());
    }

    /**
     * El grupo activo es el menor número de orden cuyas tareas
     * no están todas completadas todavía.
     */
    public int obtenerGrupoActivo() {
        return tareas.stream()
                .filter(t -> !t.estaCompletada())
                .mapToInt(Task::getOrden)
                .min()
                .orElse(-1);
    }

    /** Progreso general: porcentaje de tareas completadas. */
    public int calcularPorcentajeProgreso() {
        if (tareas.isEmpty()) return 0;
        long completadas = tareas.stream().filter(Task::estaCompletada).count();
        return (int) (completadas * 100 / tareas.size());
    }

    /** Tareas agrupadas por orden, ordenadas de menor a mayor. */
    public List<Integer> getOrdenesDeGrupos() {
        return tareas.stream()
                .mapToInt(Task::getOrden)
                .distinct()
                .sorted()
                .boxed()
                .collect(Collectors.toList());
    }

    public List<Task> getTareasPorOrden(int orden) {
        return tareas.stream()
                .filter(t -> t.getOrden() == orden)
                .collect(Collectors.toList());
    }

    public String getId()                   { return id; }
    public String getNombre()               { return nombre; }
    public String getDescripcion()          { return descripcion; }
    public String getCreadoPor()            { return creadoPor; }
    public String getEstadoNombre()         { return estadoActual.getNombre(); }
    public String getEstadoDescripcion()    { return estadoActual.getDescripcion(); }
    public String getUltimoMensaje()        { return ultimoMensaje; }
    public List<Task> getTareas()           { return Collections.unmodifiableList(tareas); }
    public List<StateChange> getHistorial() { return Collections.unmodifiableList(historial); }

    public void setNombre(String nombre)           { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    @Override
    public String toString() { return nombre; }
}
