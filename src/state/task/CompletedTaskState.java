package state.task;

import model.Task;

/**
 * La tarea fue completada exitosamente. Estado terminal.
 */
public class CompletedTaskState implements TaskState {

    @Override
    public void iniciar(Task tarea) {
        tarea.registrarAccionInvalida("La tarea ya fue completada.");
    }

    @Override
    public void enviarARevision(Task tarea) {
        tarea.registrarAccionInvalida("La tarea ya fue completada.");
    }

    @Override
    public void completar(Task tarea) {
        tarea.registrarAccionInvalida("La tarea ya está completada.");
    }

    @Override
    public void bloquear(Task tarea) {
        tarea.registrarAccionInvalida("No se puede bloquear una tarea completada.");
    }

    @Override
    public void desbloquear(Task tarea) {
        tarea.registrarAccionInvalida("La tarea no está bloqueada.");
    }

    @Override
    public String getNombre() { return "Completada"; }
}
