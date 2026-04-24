package state.project;

import model.Project;

/** El cliente está revisando el proyecto entregado. Solo el cliente puede avanzar desde aquí. */
public class ClientReviewState extends BaseProjectState {

    @Override
    public void aceptar(Project proyecto) {
        proyecto.cambiarEstado(new AcceptedState(), "Cliente satisfecho. Proyecto aceptado.");
    }

    @Override
    public void solicitarCorrecciones(Project proyecto) {
        proyecto.cambiarEstado(new InCorrectionsState(), "El cliente solicitó correcciones.");
    }

    @Override public String getNombre()      { return "En Revisión del Cliente"; }
    @Override public String getDescripcion() { return "El cliente está evaluando el proyecto entregado."; }
}
