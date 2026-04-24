package state.project;

import model.Project;

/** El proyecto está siendo probado por el equipo de QA. */
public class InTestingState extends BaseProjectState {

    @Override
    public void enviarARevisionCliente(Project proyecto) {
        proyecto.cambiarEstado(new ClientReviewState(), "Pruebas superadas. Enviado al cliente para revisión.");
    }

    @Override
    public void iniciarDesarrollo(Project proyecto) {
        proyecto.cambiarEstado(new InDevelopmentState(), "Se encontraron errores críticos. Regresando a desarrollo.");
    }

    @Override
    public void archivar(Project proyecto) {
        proyecto.cambiarEstado(new ArchivedState(), "Proyecto archivado durante las pruebas.");
    }

    @Override public String getNombre()      { return "En Pruebas"; }
    @Override public String getDescripcion() { return "El equipo de QA está verificando el proyecto."; }
}
