package state.project;

import model.Project;

/** El proyecto está siendo analizado para decidir si se aprueba o rechaza. */
public class UnderEvaluationState extends BaseProjectState {

    @Override
    public void aprobar(Project proyecto) {
        proyecto.cambiarEstado(new ApprovedState(), "Proyecto aprobado. Listo para planificación.");
    }

    @Override
    public void rechazar(Project proyecto) {
        proyecto.cambiarEstado(new ArchivedState(), "Proyecto rechazado y archivado.");
    }

    @Override public String getNombre()      { return "En Evaluación"; }
    @Override public String getDescripcion() { return "El proyecto está siendo evaluado por el gerente."; }
}
