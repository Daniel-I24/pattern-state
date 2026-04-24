package ui;

import auth.Role;
import auth.SessionManager;
import model.Project;
import service.ProjectService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel de detalle de un proyecto.
 * Muestra el estado actual, las acciones disponibles según el rol,
 * las tareas y el historial de cambios.
 */
public class ProjectPanel extends JPanel {

    private Project proyecto;

    private final JLabel lblNombre;
    private final JLabel lblEstado;
    private final JLabel lblDescripcionEstado;
    private final JLabel lblMensaje;
    private final JPanel panelAcciones;
    private final TaskPanel taskPanel;
    private final HistoryPanel historyPanel;

    public ProjectPanel(ProjectService projectService) {
        this.lblNombre            = new JLabel();
        this.lblEstado            = new JLabel();
        this.lblDescripcionEstado = new JLabel();
        this.lblMensaje           = new JLabel(" ");
        this.panelAcciones        = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        this.taskPanel            = new TaskPanel(projectService);
        this.historyPanel         = new HistoryPanel();

        taskPanel.setAlActualizarProyecto(this::refrescarEstado);
        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout(0, 0));
        setBackground(AppTheme.FONDO_PRINCIPAL);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.setFont(AppTheme.FUENTE_NORMAL);
        pestanas.setBackground(AppTheme.FONDO_PRINCIPAL);
        pestanas.addTab("Estado y acciones", crearPanelEstado());
        pestanas.addTab("Tareas",            taskPanel);
        pestanas.addTab("Historial",         historyPanel);

        add(crearCabecera(), BorderLayout.NORTH);
        add(pestanas, BorderLayout.CENTER);
    }

    private JPanel crearCabecera() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.FONDO_PANEL);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppTheme.BORDE_SUAVE),
            new EmptyBorder(16, 20, 16, 20)
        ));

        lblNombre.setFont(AppTheme.FUENTE_TITULO);
        lblNombre.setForeground(AppTheme.ACENTO_PRIMARIO);

        panel.add(lblNombre, BorderLayout.WEST);
        return panel;
    }

    private JPanel crearPanelEstado() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(AppTheme.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(crearTarjetaEstado(), BorderLayout.NORTH);
        panel.add(crearPanelAccionesConMensaje(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearTarjetaEstado() {
        JPanel tarjeta = new JPanel(new GridLayout(2, 1, 0, 6));
        tarjeta.setBackground(AppTheme.FONDO_PANEL);
        tarjeta.setBorder(AppTheme.bordePanel());

        lblEstado.setFont(AppTheme.FUENTE_SUBTITULO);
        lblEstado.setForeground(AppTheme.TEXTO_PRINCIPAL);

        lblDescripcionEstado.setFont(AppTheme.FUENTE_NORMAL);
        lblDescripcionEstado.setForeground(AppTheme.TEXTO_SECUNDARIO);

        tarjeta.add(lblEstado);
        tarjeta.add(lblDescripcionEstado);

        return tarjeta;
    }

    private JPanel crearPanelAccionesConMensaje() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(AppTheme.FONDO_PRINCIPAL);

        JLabel lblAcciones = new JLabel("Acciones disponibles");
        lblAcciones.setFont(AppTheme.FUENTE_SUBTITULO);
        lblAcciones.setForeground(AppTheme.ACENTO_PRIMARIO);

        panelAcciones.setBackground(AppTheme.FONDO_PRINCIPAL);

        lblMensaje.setFont(AppTheme.FUENTE_NORMAL);
        lblMensaje.setForeground(AppTheme.VERDE_EXITO);

        panel.add(lblAcciones,   BorderLayout.NORTH);
        panel.add(panelAcciones, BorderLayout.CENTER);
        panel.add(lblMensaje,    BorderLayout.SOUTH);

        return panel;
    }

    /** Carga un proyecto en el panel y refresca toda la vista. */
    public void cargarProyecto(Project proyecto) {
        this.proyecto = proyecto;
        refrescarEstado();
        taskPanel.cargarProyecto(proyecto);
        historyPanel.cargarHistorial(proyecto);
    }

    private void refrescarEstado() {
        if (proyecto == null) return;

        lblNombre.setText(proyecto.getNombre());

        String estado = proyecto.getEstadoNombre();
        lblEstado.setText("Estado actual: " + estado);
        lblDescripcionEstado.setText(proyecto.getEstadoDescripcion());

        // Color de fondo según estado
        Color colorEstado = AppTheme.colorEstadoProyecto(estado);
        lblEstado.setOpaque(true);
        lblEstado.setBackground(colorEstado);

        construirBotonesAccion();
        historyPanel.cargarHistorial(proyecto);

        String mensaje = proyecto.getUltimoMensaje();
        if (!mensaje.isBlank()) {
            lblMensaje.setText(mensaje);
            lblMensaje.setForeground(
                mensaje.startsWith("⚠") ? AppTheme.ROJO_ERROR : AppTheme.VERDE_EXITO
            );
        }
    }

    private void construirBotonesAccion() {
        panelAcciones.removeAll();
        Role rol = SessionManager.getInstance().getUsuarioActivo().getRol();
        String estado = proyecto.getEstadoNombre();

        if (rol == Role.GERENTE) {
            agregarBotonesGerente(estado);
        } else if (rol == Role.CLIENTE) {
            agregarBotonesCliente(estado);
        } else {
            JLabel info = new JLabel("Los desarrolladores gestionan las tareas en la pestaña 'Tareas'.");
            info.setFont(AppTheme.FUENTE_NORMAL);
            info.setForeground(AppTheme.TEXTO_SECUNDARIO);
            panelAcciones.add(info);
        }

        panelAcciones.revalidate();
        panelAcciones.repaint();
    }

    private void agregarBotonesGerente(String estado) {
        switch (estado) {
            case "Propuesto"        -> agregarBoton("Enviar a evaluación", () -> proyecto.evaluar());
            case "En Evaluación"    -> {
                agregarBoton("Aprobar",   () -> proyecto.aprobar());
                agregarBoton("Rechazar",  () -> proyecto.rechazar());
            }
            case "Aprobado"         -> agregarBoton("Iniciar planificación", () -> proyecto.iniciarPlanificacion());
            case "En Planificación" -> agregarBoton("Iniciar desarrollo",    () -> proyecto.iniciarDesarrollo());
            case "En Desarrollo"    -> agregarBoton("Pasar a pruebas",       () -> proyecto.iniciarPruebas());
            case "En Pruebas"       -> {
                agregarBoton("Enviar al cliente",       () -> proyecto.enviarARevisionCliente());
                agregarBoton("Regresar a desarrollo",   () -> proyecto.iniciarDesarrollo());
            }
            case "En Correcciones"  -> {
                agregarBoton("Reenviar al cliente",     () -> proyecto.enviarARevisionCliente());
                agregarBoton("Volver a pruebas",        () -> proyecto.iniciarPruebas());
            }
            case "Aceptado"         -> agregarBoton("Desplegar en producción", () -> proyecto.desplegar());
            case "Desplegado"       -> agregarBoton("Pasar a mantenimiento",   () -> proyecto.pasarAMantenimiento());
        }

        // El gerente puede archivar en casi cualquier estado
        if (!estado.equals("Archivado") && !estado.equals("En Revisión del Cliente")) {
            agregarBotonPeligro("Archivar proyecto", () -> {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Seguro que deseas archivar este proyecto? Esta acción no se puede deshacer.",
                    "Confirmar archivo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) proyecto.archivar();
            });
        }
    }

    private void agregarBotonesCliente(String estado) {
        if (estado.equals("En Revisión del Cliente")) {
            agregarBoton("Aceptar proyecto",         () -> proyecto.aceptar());
            agregarBotonPeligro("Solicitar correcciones", () -> proyecto.solicitarCorrecciones());
        } else {
            JLabel info = new JLabel("El proyecto aún no está disponible para tu revisión.");
            info.setFont(AppTheme.FUENTE_NORMAL);
            info.setForeground(AppTheme.TEXTO_SECUNDARIO);
            panelAcciones.add(info);
        }
    }

    private void agregarBoton(String texto, Runnable accion) {
        JButton boton = AppTheme.botonPrimario(texto);
        boton.addActionListener(e -> {
            accion.run();
            refrescarEstado();
            taskPanel.cargarProyecto(proyecto);
        });
        panelAcciones.add(boton);
    }

    private void agregarBotonPeligro(String texto, Runnable accion) {
        JButton boton = AppTheme.botonPeligro(texto);
        boton.addActionListener(e -> {
            accion.run();
            refrescarEstado();
        });
        panelAcciones.add(boton);
    }
}
