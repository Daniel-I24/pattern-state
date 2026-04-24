package state.project;

import model.Project;

/**
 * Interfaz que define todas las transiciones posibles de un proyecto.
 * Cada estado concreto decide qué acciones son válidas en ese momento.
 */
public interface ProjectState {
    void evaluar(Project proyecto);
    void aprobar(Project proyecto);
    void rechazar(Project proyecto);
    void iniciarPlanificacion(Project proyecto);
    void iniciarDesarrollo(Project proyecto);
    void iniciarPruebas(Project proyecto);
    void enviarARevisionCliente(Project proyecto);
    void solicitarCorrecciones(Project proyecto);
    void aceptar(Project proyecto);
    void desplegar(Project proyecto);
    void pasarAMantenimiento(Project proyecto);
    void archivar(Project proyecto);
    String getNombre();
    String getDescripcion();
}
