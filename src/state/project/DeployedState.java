package state.project;

import model.Project;

/** El proyecto está en producción y siendo usado por los usuarios finales. */
public class DeployedState extends BaseProjectState {

    @Override
    public void pasarAMantenimiento(Project proyecto) {
        proyecto.cambiarEstado(new InMaintenanceState(), "Proyecto en fase de mantenimiento activo.");
    }

    @Override
    public void archivar(Project proyecto) {
        proyecto.cambiarEstado(new ArchivedState(), "Proyecto retirado de producción y archivado.");
    }

    @Override public String getNombre()      { return "Desplegado"; }
    @Override public String getDescripcion() { return "El proyecto está en producción."; }
}
