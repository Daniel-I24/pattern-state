package ui.manager;

import auth.AuthService;
import auth.SessionManager;
import auth.User;
import model.Project;
import model.Task;
import service.ProjectService;
import ui.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel de gestión de tareas desde la vista del Gerente.
 * Permite crear tareas, asignarlas a desarrolladores, aprobarlas o rechazarlas
 * con justificación, y habilitar la siguiente tarea secuencial.
 */
public class ManagerTaskPanel extends JPanel {

    private final Project        proyecto;
    private final ProjectService projectService;
    private final AuthService    authService;
    private final Runnable       alActualizar;

    private final JPanel listaTareas;
    private final JLabel lblMensaje;

    public ManagerTaskPanel(Project proyecto, ProjectService projectService,
                            AuthService authService, Runnable alActualizar) {
        this.proyecto       = proyecto;
        this.projectService = projectService;
        this.authService    = authService;
        this.alActualizar   = alActualizar;
        this.listaTareas    = new JPanel();
        this.lblMensaje     = new JLabel(" ");

        listaTareas.setLayout(new BoxLayout(listaTareas, BoxLayout.Y_AXIS));
        listaTareas.setBackground(AppTheme.FONDO_PRINCIPAL);

        construirUI();
        refrescar();
    }

    private void construirUI() {
        setLayout(new BorderLayout(0, 10));
        setBackground(AppTheme.FONDO_PRINCIPAL);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        add(crearEncabezado(), BorderLayout.NORTH);

        JScrollPane scroll = new JScrollPane(listaTareas);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDE_SUAVE, 1, true));
        add(scroll, BorderLayout.CENTER);

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pie.setBackground(AppTheme.FONDO_PRINCIPAL);
        lblMensaje.setFont(AppTheme.FUENTE_PEQUEÑA);
        pie.add(lblMensaje);
        add(pie, BorderLayout.SOUTH);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.FONDO_PRINCIPAL);

        JLabel titulo = new JLabel("Tareas del proyecto - Aprobación");
        titulo.setFont(AppTheme.FUENTE_SUBTITULO);
        titulo.setForeground(AppTheme.ACENTO_PRIMARIO);

        JButton btnNuevaTarea = AppTheme.botonPrimario("+ Nueva tarea");
        btnNuevaTarea.addActionListener(e -> mostrarDialogoNuevaTarea());

        panel.add(titulo, BorderLayout.WEST);
        panel.add(btnNuevaTarea, BorderLayout.EAST);
        return panel;
    }

    public void refrescar() {
        listaTareas.removeAll();

        List<Integer> grupos = proyecto.getOrdenesDeGrupos();
        int grupoActivo      = proyecto.obtenerGrupoActivo();

        if (grupos.isEmpty()) {
            JLabel vacio = new JLabel("  No hay tareas. Agrega la primera tarea al proyecto.");
            vacio.setFont(AppTheme.FUENTE_NORMAL);
            vacio.setForeground(AppTheme.TEXTO_SECUNDARIO);
            vacio.setBorder(new EmptyBorder(20, 0, 0, 0));
            listaTareas.add(vacio);
        } else {
            for (int orden : grupos) {
                listaTareas.add(crearEncabezadoGrupo(orden, grupoActivo));
                for (Task tarea : proyecto.getTareasPorOrden(orden)) {
                    listaTareas.add(crearTarjetaTarea(tarea, orden == grupoActivo));
                    listaTareas.add(Box.createVerticalStrut(6));
                }
                listaTareas.add(Box.createVerticalStrut(10));
            }
        }

        listaTareas.revalidate();
        listaTareas.repaint();
    }

    private JPanel crearEncabezadoGrupo(int orden, int grupoActivo) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panel.setBackground(AppTheme.FONDO_PRINCIPAL);

        String etiqueta = orden == grupoActivo ? "▶ Grupo " + orden + " (activo)" : "Grupo " + orden;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(AppTheme.FUENTE_BOTON);
        lbl.setForeground(orden == grupoActivo ? AppTheme.VERDE_EXITO : AppTheme.TEXTO_SECUNDARIO);

        panel.add(lbl);
        return panel;
    }

    private JPanel crearTarjetaTarea(Task tarea, boolean grupoActivo) {
        JPanel tarjeta = new JPanel(new BorderLayout(12, 0));
        tarjeta.setBackground(AppTheme.colorEstadoTarea(tarea.getEstadoNombre()));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDE_SUAVE, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Información
        JPanel info = new JPanel(new GridLayout(3, 1, 0, 2));
        info.setOpaque(false);

        JLabel lblTitulo = new JLabel(tarea.getTitulo());
        lblTitulo.setFont(AppTheme.FUENTE_SUBTITULO);
        lblTitulo.setForeground(AppTheme.TEXTO_PRINCIPAL);

        JLabel lblAsig = new JLabel("Asignado a: " + tarea.getAsignadoANombre()
            + "  |  " + tarea.getDescripcion());
        lblAsig.setFont(AppTheme.FUENTE_PEQUEÑA);
        lblAsig.setForeground(AppTheme.TEXTO_SECUNDARIO);

        JLabel lblFeedback = new JLabel(" ");
        if (tarea.getJustificacionRechazo() != null) {
            lblFeedback.setText("⚠ Rechazo: " + tarea.getJustificacionRechazo());
            lblFeedback.setForeground(AppTheme.ROJO_ERROR);
            lblFeedback.setFont(AppTheme.FUENTE_PEQUEÑA);
        }

        info.add(lblTitulo);
        info.add(lblAsig);
        info.add(lblFeedback);

        // Estado + botones
        JPanel derecha = new JPanel(new BorderLayout(0, 4));
        derecha.setOpaque(false);

        JLabel lblEstado = new JLabel(tarea.getEstadoNombre());
        lblEstado.setFont(AppTheme.FUENTE_BOTON);
        lblEstado.setForeground(AppTheme.ACENTO_PRIMARIO);
        lblEstado.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botones.setOpaque(false);

        if (tarea.esperaAprobacion()) {
            JButton btnAprobar = AppTheme.botonSecundario("✓ Aprobar");
            btnAprobar.addActionListener(e -> aprobarTarea(tarea));

            JButton btnRechazar = AppTheme.botonPeligro("✗ Rechazar");
            btnRechazar.addActionListener(e -> rechazarTarea(tarea));

            botones.add(btnAprobar);
            botones.add(btnRechazar);
        }

        derecha.add(lblEstado, BorderLayout.NORTH);
        derecha.add(botones,   BorderLayout.SOUTH);

        tarjeta.add(info,    BorderLayout.CENTER);
        tarjeta.add(derecha, BorderLayout.EAST);
        return tarjeta;
    }

    private void aprobarTarea(Task tarea) {
        String gerente = SessionManager.getInstance().getUsuarioActivo().getNombre();
        projectService.aprobarTarea(proyecto, tarea, gerente);
        mostrarMensaje("✓ Tarea aprobada: " + tarea.getTitulo(), false);
        refrescar();
        if (alActualizar != null) alActualizar.run();
    }

    private void rechazarTarea(Task tarea) {
        JTextArea campoJustificacion = AppTheme.areaTexto(4, 30);
        AppTheme.aplicarPlaceholder(campoJustificacion,
            "Describe qué debe corregir o mejorar el desarrollador...");

        JPanel form = new JPanel(new BorderLayout(0, 8));
        form.setBackground(AppTheme.FONDO_PANEL);
        form.add(new JLabel("Justificación del rechazo (obligatoria):"), BorderLayout.NORTH);
        form.add(new JScrollPane(campoJustificacion), BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(this, form,
            "Rechazar tarea: " + tarea.getTitulo(),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return;

        String justificacion = campoJustificacion.getText().trim();
        if (justificacion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "La justificación es obligatoria para rechazar.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String gerente = SessionManager.getInstance().getUsuarioActivo().getNombre();
        projectService.rechazarTarea(proyecto, tarea, gerente, justificacion);
        mostrarMensaje("⚠ Tarea rechazada. El desarrollador fue notificado.", true);
        refrescar();
        if (alActualizar != null) alActualizar.run();
    }

    private void mostrarMensaje(String texto, boolean esError) {
        lblMensaje.setText(texto);
        lblMensaje.setForeground(esError ? AppTheme.ROJO_ERROR : AppTheme.VERDE_EXITO);
    }

    private void mostrarDialogoNuevaTarea() {
        // Obtener lista de desarrolladores registrados
        List<User> desarrolladores = authService.obtenerDesarrolladores();
        if (desarrolladores.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay desarrolladores registrados en el sistema.",
                "Sin desarrolladores", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Formulario
        JTextField campoTitulo = AppTheme.campoTexto(25);
        JTextArea campoDesc = AppTheme.areaTexto(3, 25);
        JTextField campoOrden = AppTheme.campoTexto(25);
        JComboBox<User> comboDesarrollador = new JComboBox<>(desarrolladores.toArray(new User[0]));

        AppTheme.aplicarPlaceholder(campoTitulo, "Ej: Implementar módulo de autenticación");
        AppTheme.aplicarPlaceholder(campoDesc, "Describe los requisitos de la tarea...");
        AppTheme.aplicarPlaceholder(campoOrden, "Ej: 1 (tareas con mismo número se ejecutan en paralelo)");

        comboDesarrollador.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(value == null ? "" : value.getNombre() + " (" + value.getCorreo() + ")");
            label.setOpaque(true);
            label.setBackground(isSelected ? AppTheme.ACENTO_PRIMARIO : Color.WHITE);
            label.setForeground(isSelected ? Color.WHITE : AppTheme.TEXTO_PRINCIPAL);
            label.setBorder(new EmptyBorder(4, 8, 4, 8));
            return label;
        });

        JPanel form = new JPanel(new GridLayout(8, 1, 0, 6));
        form.setBackground(AppTheme.FONDO_PANEL);
        form.add(new JLabel("Título de la tarea:"));
        form.add(campoTitulo);
        form.add(new JLabel("Descripción:"));
        form.add(new JScrollPane(campoDesc));
        form.add(new JLabel("Número de orden (1, 2, 3...):"));
        form.add(campoOrden);
        form.add(new JLabel("Asignar a desarrollador:"));
        form.add(comboDesarrollador);

        int res = JOptionPane.showConfirmDialog(this, form,
            "Nueva tarea para: " + proyecto.getNombre(),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return;

        // Validaciones
        String titulo = campoTitulo.getText().trim();
        String desc = campoDesc.getText().trim();
        String ordenStr = campoOrden.getText().trim();
        User desarrollador = (User) comboDesarrollador.getSelectedItem();

        if (titulo.isEmpty() || ordenStr.isEmpty() || desarrollador == null) {
            JOptionPane.showMessageDialog(this,
                "Todos los campos son obligatorios.",
                "Datos incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orden;
        try {
            orden = Integer.parseInt(ordenStr);
            if (orden < 1) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "El número de orden debe ser un entero positivo (1, 2, 3...).",
                "Orden inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear tarea
        projectService.agregarTarea(proyecto, titulo, desc,
            desarrollador.getId(), desarrollador.getNombre(), orden);

        mostrarMensaje("✓ Tarea creada y asignada a " + desarrollador.getNombre(), false);
        refrescar();
        if (alActualizar != null) alActualizar.run();
    }
}
