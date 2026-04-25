package service;

import model.Project;
import model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servicio central que gestiona proyectos y tareas en memoria.
 * Es la única fuente de verdad compartida entre todas las interfaces de usuario.
 */
public class ProjectService {

    private final List<Project> proyectos = new ArrayList<>();

    public Project crearProyecto(String nombre, String descripcion, String creadoPor) {
        Project proyecto = new Project(nombre, descripcion, creadoPor);
        proyectos.add(proyecto);
        return proyecto;
    }

    /**
     * Agrega una tarea a un proyecto con orden de ejecución definido por el gerente.
     * Las tareas con el mismo orden son paralelas; las de orden mayor son secuenciales.
     */
    public Task agregarTarea(Project proyecto, String titulo, String descripcion,
                             String asignadoAId, String asignadoANombre, int orden) {
        Task tarea = new Task(titulo, descripcion, asignadoAId, asignadoANombre, orden);
        proyecto.agregarTarea(tarea);
        return tarea;
    }

    /**
     * El gerente aprueba una tarea. Si todas las tareas del grupo actual
     * quedan aprobadas, el siguiente grupo se desbloquea automáticamente.
     */
    public void aprobarTarea(Project proyecto, Task tarea, String nombreGerente) {
        tarea.aprobarPorGerente(nombreGerente);
    }

    /**
     * El gerente rechaza una tarea con una justificación obligatoria.
     */
    public void rechazarTarea(Project proyecto, Task tarea,
                              String nombreGerente, String justificacion) {
        tarea.rechazarPorGerente(nombreGerente, justificacion);
    }

    public List<Project> obtenerTodos() {
        return Collections.unmodifiableList(proyectos);
    }

    public Project buscarPorId(String id) {
        return proyectos.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}
