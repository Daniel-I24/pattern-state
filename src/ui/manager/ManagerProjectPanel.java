package ui.manager;

import auth.AuthService;
import model.Project;
import service.ProjectService;
import ui.AppTheme;
import ui.shared.HistoryPanel;
import ui.shared.ProjectProgressBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel de detalle de un proyecto desde la vista del Gerente.
 * Incluye: estado del proyecto, acciones de transición, gestión de tareas,
 * aprobación/rechazo de trabajo de desarrolladores e historial.
 */
public class ManagerProjectPanel extends JPanel {

    private final Project        proyecto;
    private final Runnable       alActualizar;

    private final JLabel             lblEstado;
    private final JLabel             lblMensaje;
    private final JPanel             panelAcciones;
    private final ProjectProgressBar barraProgreso;
    private final ManagerTaskPanel   taskPanel;
    private final HistoryPanel       historyPanel;

    public ManagerProjectPanel(Project proyecto, ProjectService projectService,
                               AuthService authService, Runnable alActualizar) {
        this.proyecto       = proyecto;
        this.alActualizar   = alActualizar;
        this.lblEstado      = new JLabel();
        this.lblMensaje     = new JLabel(" ");
        this.panelAcciones  = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        this.barraProgreso  = new ProjectProgressBar();
        this.taskPanel      = new ManagerTaskPanel(proyecto, projectService, authService, this::refrescar);
        this.historyPanel   = new HistoryPanel();

        construirUI();
        refrescar();
    }

    private void construirUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppTheme.FONDO_PRINCIPAL);

        add(crearCabecera(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FUENTE_NORMAL);
        tabs.addTab("Estado del proyecto", crearPanelEstado());
        tabs.addTab("Tareas y aprobaciones", taskPanel);
        tabs.addTab("Historial de cambios",  historyPanel);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel crearCabecera() {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setBackground(AppTheme.FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDE_SUAVE),
            new EmptyBorder(14, 20, 14, 20)
        ));

        JLabel nombre = new JLabel(proyecto.getNombre());
        nombre.setFont(AppTheme.FUENTE_TITULO);
        nombre.setForeground(AppTheme.ACENTO_PRIMARIO);

        JLabel desc = new JLabel(proyecto.getDescripcion());
        desc.setFont(AppTheme.FUENTE_PEQUEÑA);
        desc.setForeground(AppTheme.TEXTO_SECUNDARIO);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(nombre);
        textos.add(desc);

        panel.add(textos,       BorderLayout.WEST);
        panel.add(barraProgreso, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(AppTheme.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Tarjeta de estado
        JPanel tarjeta = new JPanel(new GridLayout(2, 1, 0, 6));
        tarjeta.setBackground(AppTheme.FONDO_PANEL);
        tarjeta.setBorder(AppTheme.bordePanel());

        lblEstado.setFont(AppTheme.FUENTE_SUBTITULO);
        lblEstado.setForeground(AppTheme.TEXTO_PRINCIPAL);

        JLabel lblDescEstado = new JLabel(proyecto.getEstadoDescripcion());
        lblDescEstado.setFont(AppTheme.FUENTE_NORMAL);
        lblDescEstado.setForeground(AppTheme.TEXTO_SECUNDARIO);

        tarjeta.add(lblEstado);
        tarjeta.add(lblDescEstado);

        // Panel de acciones
        JPanel seccionAcciones = new JPanel(new BorderLayout(0, 10));
        seccionAcciones.setBackground(AppTheme.FONDO_PRINCIPAL);

        JLabel tituloAcciones = new JLabel("Acciones disponibles");
        tituloAcciones.setFont(AppTheme.FUENTE_SUBTITULO);
        tituloAcciones.setForeground(AppTheme.ACENTO_PRIMARIO);

        panelAcciones.setBackground(AppTheme.FONDO_PRINCIPAL);

        lblMensaje.setFont(AppTheme.FUENTE_NORMAL);

        seccionAcciones.add(tituloAcciones, BorderLayout.NORTH);
        seccionAcciones.add(panelAcciones,  BorderLayout.CENTER);
        seccionAcciones.add(lblMensaje,     BorderLayout.SOUTH);

        panel.add(tarjeta,          BorderLayout.NORTH);
        panel.add(seccionAcciones,  BorderLayout.CENTER);
        return panel;
    }

    private void refrescar() {
        String estado = proyecto.getEstadoNombre();
        lblEstado.setText("Estado actual: " + estado);
        lblEstado.setBackground(AppTheme.colorEstadoProyecto(estado));
        lblEstado.setOpaque(true);

        barraProgreso.actualizar(proyecto.calcularPorcentajeProgreso());

        construirBotones(estado);
        historyPanel.cargarHistorial(proyecto);
        taskPanel.refrescar();

        String msg = proyecto.getUltimoMensaje();
        if (!msg.isBlank()) {
            lblMensaje.setText(msg);
            lblMensaje.setForeground(
                msg.startsWith("⚠") ? AppTheme.ROJO_ERROR : AppTheme.VERDE_EXITO);
        }

        if (alActualizar != null) alActualizar.run();
    }

    private void construirBotones(String estado) {
        panelAcciones.removeAll();

        switch (estado) {
            case "Propuesto"        -> boton("Enviar a evaluación",    () -> proyecto.evaluar());
            case "En Evaluación"    -> {
                boton("Aprobar proyecto",  () -> proyecto.aprobar());
                botonPeligro("Rechazar",   () -> proyecto.rechazar());
            }
            case "Aprobado"         -> boton("Iniciar planificación",  () -> proyecto.iniciarPlanificacion());
            case "En Planificación" -> boton("Iniciar desarrollo",     () -> proyecto.iniciarDesarrollo());
            case "En Desarrollo"    -> boton("Pasar a pruebas",        () -> proyecto.iniciarPruebas());
            case "En Pruebas"       -> {
                boton("Enviar al cliente",      () -> proyecto.enviarARevisionCliente());
                botonNeutral("Regresar a desarrollo", () -> proyecto.iniciarDesarrollo());
            }
            case "En Correcciones"  -> {
                boton("Reenviar al cliente",    () -> proyecto.enviarARevisionCliente());
                botonNeutral("Volver a pruebas",() -> proyecto.iniciarPruebas());
            }
            case "Aceptado"         -> boton("Desplegar en producción",() -> proyecto.desplegar());
            case "Desplegado"       -> boton("Pasar a mantenimiento",  () -> proyecto.pasarAMantenimiento());
        }

        if (!estado.equals("Archivado") && !estado.equals("En Revisión del Cliente")) {
            botonPeligro("Archivar proyecto", () -> {
                int ok = JOptionPane.showConfirmDialog(this,
                    "¿Archivar este proyecto? Esta acción no se puede deshacer.",
                    "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (ok == JOptionPane.YES_OPTION) proyecto.archivar();
            });
        }

        panelAcciones.revalidate();
        panelAcciones.repaint();
    }

    private void boton(String texto, Runnable accion) {
        JButton b = AppTheme.botonPrimario(texto);
        b.addActionListener(e -> { accion.run(); refrescar(); });
        panelAcciones.add(b);
    }

    private void botonPeligro(String texto, Runnable accion) {
        JButton b = AppTheme.botonPeligro(texto);
        b.addActionListener(e -> { accion.run(); refrescar(); });
        panelAcciones.add(b);
    }

    private void botonNeutral(String texto, Runnable accion) {
        JButton b = AppTheme.botonNeutral(texto);
        b.addActionListener(e -> { accion.run(); refrescar(); });
        panelAcciones.add(b);
    }
}
