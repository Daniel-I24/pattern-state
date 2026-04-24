package state.project;

import model.Project;

/** El equipo está construyendo el proyecto activamente. */
public class InDevelopmentState extends BaseProjectState {

    @Override
    public void iniciarPruebas(Project proyecto) {
        if (!proyecto.todasLasTareasCompletadas()) {
            proyecto.registrarAccionInvalida(
                "No se puede pasar a pruebas: hay tareas sin completar. Completa todas las tareas primero."
            );
            return;
        }
        proyecto.cambiarEstado(new InTestingState(), "Desarrollo finalizado. Iniciando fase de pruebas.");
    }

    @Override
    public void archivar(Project proyecto) {
        proyecto.cambiarEstado(new ArchivedState(), "Proyecto archivado durante el desarrollo.");
    }

    @Override public String getNombre()      { return "En Desarrollo"; }
    @Override public String getDescripcion() { return "El equipo está construyendo el proyecto."; }
}
