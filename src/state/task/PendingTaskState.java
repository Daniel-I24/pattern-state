package state.task;

import model.Task;

/**
 * Estado inicial de una tarea: aún no ha sido iniciada.
 */
public class PendingTaskState implements TaskState {

    @Override
    public void iniciar(Task tarea) {
        tarea.cambiarEstado(new InProgressTaskState(), "Tarea iniciada.");
    }

    @Override
    public void enviarARevision(Task tarea) {
        tarea.registrarAccionInvalida("Debe iniciar la tarea antes de enviarla a revisión.");
    }

    @Override
    public void completar(Task tarea) {
        tarea.registrarAccionInvalida("No se puede completar una tarea que no ha sido iniciada.");
    }

    @Override
    public void bloquear(Task tarea) {
        tarea.cambiarEstado(new BlockedTaskState(), "Tarea bloqueada desde estado pendiente.");
    }

    @Override
    public void desbloquear(Task tarea) {
        tarea.registrarAccionInvalida("La tarea no está bloqueada.");
    }

    @Override
    public String getNombre() { return "Pendiente"; }
}
