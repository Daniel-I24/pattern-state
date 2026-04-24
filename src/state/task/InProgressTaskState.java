package state.task;

import model.Task;

/**
 * La tarea está siendo trabajada activamente por un desarrollador.
 */
public class InProgressTaskState implements TaskState {

    @Override
    public void iniciar(Task tarea) {
        tarea.registrarAccionInvalida("La tarea ya está en progreso.");
    }

    @Override
    public void enviarARevision(Task tarea) {
        tarea.cambiarEstado(new UnderReviewTaskState(), "Tarea enviada a revisión.");
    }

    @Override
    public void completar(Task tarea) {
        tarea.registrarAccionInvalida("Debe enviar la tarea a revisión antes de completarla.");
    }

    @Override
    public void bloquear(Task tarea) {
        tarea.cambiarEstado(new BlockedTaskState(), "Tarea bloqueada durante el desarrollo.");
    }

    @Override
    public void desbloquear(Task tarea) {
        tarea.registrarAccionInvalida("La tarea no está bloqueada.");
    }

    @Override
    public String getNombre() { return "En Progreso"; }
}
