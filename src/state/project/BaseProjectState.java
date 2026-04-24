package state.project;

import model.Project;

/**
 * Clase base que provee el comportamiento por defecto para todas las transiciones.
 * Cada estado concreto solo sobreescribe las transiciones que le son válidas,
 * evitando repetir el mismo mensaje de error en cada clase.
 */
public abstract class BaseProjectState implements ProjectState {

    @Override public void evaluar(Project p)               { invalida(p); }
    @Override public void aprobar(Project p)               { invalida(p); }
    @Override public void rechazar(Project p)              { invalida(p); }
    @Override public void iniciarPlanificacion(Project p)  { invalida(p); }
    @Override public void iniciarDesarrollo(Project p)     { invalida(p); }
    @Override public void iniciarPruebas(Project p)        { invalida(p); }
    @Override public void enviarARevisionCliente(Project p){ invalida(p); }
    @Override public void solicitarCorrecciones(Project p) { invalida(p); }
    @Override public void aceptar(Project p)               { invalida(p); }
    @Override public void desplegar(Project p)             { invalida(p); }
    @Override public void pasarAMantenimiento(Project p)   { invalida(p); }
    @Override public void archivar(Project p)              { invalida(p); }

    private void invalida(Project proyecto) {
        proyecto.registrarAccionInvalida(
            "La acción no está permitida en el estado actual: " + getNombre()
        );
    }
}
