package ui;

import auth.Role;
import auth.SessionManager;
import model.Project;
import model.Task;
import service.ProjectService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel para gestionar las tareas de un proyecto.
 * Los desarrolladores pueden avanzar el estado de las tareas.
 * El gerente puede crear y asignar tareas.
 */
public class TaskPanel extends JPanel {

    private final ProjectService projectService;
    private Project proyectoActual;
    private final JPanel listaTareas;
    private final JLabel etiquetaMensaje;
    private Runnable alActualizarProyecto;

    public TaskPanel(ProjectService projectService) {
        this.projectService = projectService;
        this.listaTareas    = new JPanel();
        this.etiquetaMensaje = new JLabel(" ");

        listaTareas.setLayout(new BoxLayout(listaTareas, BoxLayout.Y_AXIS));
        listaTareas.setBackground(AppTheme.FONDO_PRINCIPAL);

        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout(0, 12));
        setBackground(AppTheme.FONDO_PRINCIPAL);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(crearEncabezado(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(listaTareas);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDE_SUAVE, 1, true));
        scroll.setBackground(AppTheme.FONDO_PRINCIPAL);
        add(scroll, BorderLayout.CENTER);

        add(crearPiePagina(), BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.FONDO_PRINCIPAL);

        JLabel titulo = new JLabel("Tareas del proyecto");
        titulo.setFont(AppTheme.FUENTE_SUBTITULO);
        titulo.setForeground(AppTheme.ACENTO_PRIMARIO);
        panel.add(titulo, BorderLayout.WEST);

        if (SessionManager.getInstance().esGerente()) {
            JButton btnAgregar = AppTheme.botonSecundario("+ Nueva tarea");
            btnAgregar.addActionListener(e -> mostrarDialogoNuevaTarea());
            panel.add(btnAgregar, BorderLayout.EAST);
        }

        return panel;
    }

    private JPanel crearPiePagina() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(AppTheme.FONDO_PRINCIPAL);
        etiquetaMensaje.setFont(AppTheme.FUENTE_PEQUEÑA);
        etiquetaMensaje.setForeground(AppTheme.ROJO_ERROR);
        panel.add(etiquetaMensaje);
        return panel;
    }

    public void cargarProyecto(Project proyecto) {
        this.proyectoActual = proyecto;
        refrescarLista();
    }

    public void setAlActualizarProyecto(Runnable callback) {
        this.alActualizarProyecto = callback;
    }

    private void refrescarLista() {
        listaTareas.removeAll();

        if (proyectoActual == null) return;

        List<Task> tareas = proyectoActual.getTareas();
        if (tareas.isEmpty()) {
            JLabel vacio = new JLabel("  No hay tareas registradas.");
            vacio.setFont(AppTheme.FUENTE_NORMAL);
            vacio.setForeground(AppTheme.TEXTO_SECUNDARIO);
            vacio.setBorder(new EmptyBorder(20, 0, 0, 0));
            listaTareas.add(vacio);
        } else {
            for (Task tarea : tareas) {
                listaTareas.add(crearTarjetaTarea(tarea));
                listaTareas.add(Box.createVerticalStrut(8));
            }
        }

        listaTareas.revalidate();
        listaTareas.repaint();
    }

    private JPanel crearTarjetaTarea(Task tarea) {
        JPanel tarjeta = new JPanel(new BorderLayout(12, 0));
        tarjeta.setBackground(AppTheme.colorEstadoTarea(tarea.getEstadoNombre()));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDE_SUAVE, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Información de la tarea
        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);

        JLabel lblTitulo = new JLabel(tarea.getTitulo());
        lblTitulo.setFont(AppTheme.FUENTE_SUBTITULO);
        lblTitulo.setForeground(AppTheme.TEXTO_PRINCIPAL);

        JLabel lblDetalle = new JLabel("Asignado a: " + tarea.getAsignadoA() + "  |  " + tarea.getDescripcion());
        lblDetalle.setFont(AppTheme.FUENTE_PEQUEÑA);
        lblDetalle.setForeground(AppTheme.TEXTO_SECUNDARIO);

        info.add(lblTitulo);
        info.add(lblDetalle);

        // Estado
        JLabel lblEstado = new JLabel(tarea.getEstadoNombre());
        lblEstado.setFont(AppTheme.FUENTE_BOTON);
        lblEstado.setForeground(AppTheme.ACENTO_PRIMARIO);
        lblEstado.setHorizontalAlignment(SwingConstants.CENTER);
        lblEstado.setPreferredSize(new Dimension(120, 30));

        // Botones de acción
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        acciones.setOpaque(false);
        acciones.add(lblEstado);

        if (puedeGestionarTareas()) {
            agregarBotonesTarea(acciones, tarea);
        }

        tarjeta.add(info, BorderLayout.CENTER);
        tarjeta.add(acciones, BorderLayout.EAST);

        return tarjeta;
    }

    private void agregarBotonesTarea(JPanel panel, Task tarea) {
        String estado = tarea.getEstadoNombre();

        switch (estado) {
            case "Pendiente" -> {
                JButton btnIniciar = AppTheme.botonPrimario("Iniciar");
                btnIniciar.addActionListener(e -> ejecutarAccionTarea(tarea, "iniciar"));
                JButton btnBloquear = AppTheme.botonPeligro("Bloquear");
                btnBloquear.addActionListener(e -> ejecutarAccionTarea(tarea, "bloquear"));
                panel.add(btnIniciar);
                panel.add(btnBloquear);
            }
            case "En Progreso" -> {
                JButton btnRevisar = AppTheme.botonNeutral("Enviar a revisión");
                btnRevisar.addActionListener(e -> ejecutarAccionTarea(tarea, "revision"));
                JButton btnBloquear = AppTheme.botonPeligro("Bloquear");
                btnBloquear.addActionListener(e -> ejecutarAccionTarea(tarea, "bloquear"));
                panel.add(btnRevisar);
                panel.add(btnBloquear);
            }
            case "En Revisión" -> {
                JButton btnCompletar = AppTheme.botonSecundario("Completar");
                btnCompletar.addActionListener(e -> ejecutarAccionTarea(tarea, "completar"));
                panel.add(btnCompletar);
            }
            case "Bloqueada" -> {
                JButton btnDesbloquear = AppTheme.botonSecundario("Desbloquear");
                btnDesbloquear.addActionListener(e -> ejecutarAccionTarea(tarea, "desbloquear"));
                panel.add(btnDesbloquear);
            }
        }
    }

    private void ejecutarAccionTarea(Task tarea, String accion) {
        switch (accion) {
            case "iniciar"    -> tarea.iniciar();
            case "revision"   -> tarea.enviarARevision();
            case "completar"  -> tarea.completar();
            case "bloquear"   -> tarea.bloquear();
            case "desbloquear"-> tarea.desbloquear();
        }

        String mensaje = tarea.getUltimoMensaje();
        etiquetaMensaje.setText(mensaje);
        etiquetaMensaje.setForeground(
            mensaje.startsWith("⚠") ? AppTheme.ROJO_ERROR : AppTheme.VERDE_EXITO
        );

        refrescarLista();
        if (alActualizarProyecto != null) alActualizarProyecto.run();
    }

    private void mostrarDialogoNuevaTarea() {
        if (proyectoActual == null) return;

        JTextField campoTitulo = AppTheme.campoTexto(20);
        JTextField campoDesc   = AppTheme.campoTexto(20);
        JTextField campoAsig   = AppTheme.campoTexto(20);

        JPanel form = new JPanel(new GridLayout(6, 1, 0, 6));
        form.setBackground(AppTheme.FONDO_PANEL);
        form.add(new JLabel("Título de la tarea:"));
        form.add(campoTitulo);
        form.add(new JLabel("Descripción:"));
        form.add(campoDesc);
        form.add(new JLabel("Asignado a:"));
        form.add(campoAsig);

        int resultado = JOptionPane.showConfirmDialog(this, form,
            "Nueva tarea", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            String titulo = campoTitulo.getText().trim();
            String desc   = campoDesc.getText().trim();
            String asig   = campoAsig.getText().trim();

            if (titulo.isEmpty() || asig.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El título y el responsable son obligatorios.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            projectService.agregarTarea(proyectoActual, titulo, desc, asig);
            refrescarLista();
            if (alActualizarProyecto != null) alActualizarProyecto.run();
        }
    }

    private boolean puedeGestionarTareas() {
        Role rol = SessionManager.getInstance().getUsuarioActivo().getRol();
        return rol == Role.GERENTE || rol == Role.DESARROLLADOR;
    }
}
