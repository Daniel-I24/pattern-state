package state.task;

import model.Task;

/**
 * La tarea está bloqueada por algún impedimento externo.
 * Solo puede ser desbloqueada para continuar su flujo.
 */
public class BlockedTaskState implements TaskState {

    @Override
    public void iniciar(Task tarea) {
        tarea.registrarAccionInvalida("La tarea está bloqueada. Desbloquéela primero.");
    }

    @Override
    public void enviarARevision(Task tarea) {
        tarea.registrarAccionInvalida("La tarea está bloqueada. Desbloquéela primero.");
    }

    @Override
    public void completar(Task tarea) {
        tarea.registrarAccionInvalida("La tarea está bloqueada. Desbloquéela primero.");
    }

    @Override
    public void bloquear(Task tarea) {
        tarea.registrarAccionInvalida("La tarea ya está bloqueada.");
    }

    @Override
    public void desbloquear(Task tarea) {
        tarea.cambiarEstado(new InProgressTaskState(), "Tarea desbloqueada. Continúa en progreso.");
    }

    @Override
    public String getNombre() { return "Bloqueada"; }
}
