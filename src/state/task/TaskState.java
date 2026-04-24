package state.task;

import model.Task;

/**
 * Interfaz que define las acciones posibles sobre una tarea.
 * Cada estado concreto decide cómo responde a cada acción.
 */
public interface TaskState {
    void iniciar(Task tarea);
    void enviarARevision(Task tarea);
    void completar(Task tarea);
    void bloquear(Task tarea);
    void desbloquear(Task tarea);
    String getNombre();
}
