package service;

import model.Project;
import model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servicio que centraliza la lógica de negocio sobre proyectos.
 * Actúa como repositorio en memoria durante la ejecución.
 */
public class ProjectService {

    private final List<Project> proyectos = new ArrayList<>();

    public Project crearProyecto(String nombre, String descripcion, String creadoPor) {
        Project proyecto = new Project(nombre, descripcion, creadoPor);
        proyectos.add(proyecto);
        return proyecto;
    }

    public Task agregarTarea(Project proyecto, String titulo, String descripcion, String asignadoA) {
        Task tarea = new Task(titulo, descripcion, asignadoA);
        proyecto.agregarTarea(tarea);
        return tarea;
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
