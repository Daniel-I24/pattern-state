package state.project;

import model.Project;

/** El proyecto fue aprobado y puede iniciar su planificación. */
public class ApprovedState extends BaseProjectState {

    @Override
    public void iniciarPlanificacion(Project proyecto) {
        proyecto.cambiarEstado(new PlanningState(), "Planificación del proyecto iniciada.");
    }

    @Override
    public void archivar(Project proyecto) {
        proyecto.cambiarEstado(new ArchivedState(), "Proyecto aprobado pero archivado por decisión del gerente.");
    }

    @Override public String getNombre()      { return "Aprobado"; }
    @Override public String getDescripcion() { return "El proyecto fue aprobado y espera planificación."; }
}
