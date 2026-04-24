package state.project;

import model.Project;

/** El equipo está definiendo tareas, tiempos y recursos del proyecto. */
public class PlanningState extends BaseProjectState {

    @Override
    public void iniciarDesarrollo(Project proyecto) {
        if (!proyecto.tieneTareas()) {
            proyecto.registrarAccionInvalida("Debe agregar al menos una tarea antes de iniciar el desarrollo.");
            return;
        }
        proyecto.cambiarEstado(new InDevelopmentState(), "Desarrollo iniciado.");
    }

    @Override
    public void archivar(Project proyecto) {
        proyecto.cambiarEstado(new ArchivedState(), "Proyecto archivado durante la planificación.");
    }

    @Override public String getNombre()      { return "En Planificación"; }
    @Override public String getDescripcion() { return "Se están definiendo tareas y recursos del proyecto."; }
}
