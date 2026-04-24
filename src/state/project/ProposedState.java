package state.project;

import model.Project;

/** El proyecto fue registrado y espera ser evaluado por el gerente. */
public class ProposedState extends BaseProjectState {

    @Override
    public void evaluar(Project proyecto) {
        proyecto.cambiarEstado(new UnderEvaluationState(), "Proyecto enviado a evaluación.");
    }

    @Override
    public void archivar(Project proyecto) {
        proyecto.cambiarEstado(new ArchivedState(), "Proyecto archivado sin ser evaluado.");
    }

    @Override public String getNombre()      { return "Propuesto"; }
    @Override public String getDescripcion() { return "El proyecto fue registrado y espera evaluación."; }
}
