package state.project;

import model.Project;

/** El equipo está aplicando las correcciones solicitadas por el cliente. */
public class InCorrectionsState extends BaseProjectState {

    @Override
    public void enviarARevisionCliente(Project proyecto) {
        proyecto.cambiarEstado(new ClientReviewState(), "Correcciones aplicadas. Enviado nuevamente al cliente.");
    }

    @Override
    public void iniciarPruebas(Project proyecto) {
        proyecto.cambiarEstado(new InTestingState(), "Correcciones completadas. Regresando a pruebas.");
    }

    @Override
    public void archivar(Project proyecto) {
        proyecto.cambiarEstado(new ArchivedState(), "Proyecto archivado durante las correcciones.");
    }

    @Override public String getNombre()      { return "En Correcciones"; }
    @Override public String getDescripcion() { return "El equipo aplica las correcciones solicitadas por el cliente."; }
}
