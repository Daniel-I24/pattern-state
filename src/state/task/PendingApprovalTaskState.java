package state.task;

import model.Task;

/**
 * La tarea fue marcada como lista por el desarrollador
 * y espera la aprobación del gerente para considerarse completada.
 */
public class PendingApprovalTaskState implements TaskState {

    @Override
    public void iniciar(Task tarea) {
        tarea.registrarAccionInvalida("La tarea ya fue enviada para aprobación.");
    }

    @Override
    public void enviarARevision(Task tarea) {
        tarea.registrarAccionInvalida("La tarea ya está pendiente de aprobación.");
    }

    @Override
    public void completar(Task tarea) {
        tarea.registrarAccionInvalida("Solo el gerente puede aprobar esta tarea.");
    }

    @Override
    public void bloquear(Task tarea) {
        tarea.registrarAccionInvalida("No se puede bloquear una tarea en espera de aprobación.");
    }

    @Override
    public void desbloquear(Task tarea) {
        tarea.registrarAccionInvalida("La tarea no está bloqueada.");
    }

    @Override
    public String getNombre() { return "Pendiente de Aprobación"; }
}
