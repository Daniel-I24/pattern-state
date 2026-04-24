package state.project;

import model.Project;

/** El proyecto recibe soporte y mejoras continuas post-despliegue. */
public class InMaintenanceState extends BaseProjectState {

    @Override
    public void archivar(Project proyecto) {
        proyecto.cambiarEstado(new ArchivedState(), "Proyecto finalizado y archivado tras mantenimiento.");
    }

    @Override public String getNombre()      { return "En Mantenimiento"; }
    @Override public String getDescripcion() { return "El proyecto recibe soporte y mejoras continuas."; }
}
