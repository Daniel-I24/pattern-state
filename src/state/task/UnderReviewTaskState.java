package state.task;

import model.Task;

/**
 * La tarea fue entregada y está siendo revisada por el equipo.
 */
public class UnderReviewTaskState implements TaskState {

    @Override
    public void iniciar(Task tarea) {
        tarea.registrarAccionInvalida("La tarea ya fue enviada a revisión.");
    }

    @Override
    public void enviarARevision(Task tarea) {
        tarea.registrarAccionInvalida("La tarea ya está en revisión.");
    }

    @Override
    public void completar(Task tarea) {
        tarea.cambiarEstado(new CompletedTaskState(), "Revisión aprobada. Tarea completada.");
    }

    @Override
    public void bloquear(Task tarea) {
        tarea.cambiarEstado(new BlockedTaskState(), "Tarea bloqueada durante la revisión.");
    }

    @Override
    public void desbloquear(Task tarea) {
        tarea.registrarAccionInvalida("La tarea no está bloqueada.");
    }

    @Override
    public String getNombre() { return "En Revisión"; }
}
