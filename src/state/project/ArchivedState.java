package state.project;

/** Estado terminal. El proyecto fue cerrado y no admite más transiciones. */
public class ArchivedState extends BaseProjectState {

    @Override public String getNombre()      { return "Archivado"; }
    @Override public String getDescripcion() { return "El proyecto fue cerrado. No admite más cambios."; }
}
