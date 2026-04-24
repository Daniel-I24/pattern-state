package state.project;

import model.Project;

/** El cliente aceptó el proyecto. Listo para ser desplegado en producción. */
public class AcceptedState extends BaseProjectState {

    @Override
    public void desplegar(Project proyecto) {
        proyecto.cambiarEstado(new DeployedState(), "Proyecto desplegado en producción exitosamente.");
    }

    @Override public String getNombre()      { return "Aceptado"; }
    @Override public String getDescripcion() { return "El cliente aceptó el proyecto. Pendiente de despliegue."; }
}
