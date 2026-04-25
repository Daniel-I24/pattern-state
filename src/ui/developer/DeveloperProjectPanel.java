package ui.developer;

import auth.SessionManager;
import model.Project;
import model.Task;
import ui.AppTheme;
import ui.shared.HistoryPanel;
import ui.shared.ProjectProgressBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel de detalle de un proyecto desde la vista del Desarrollador.
 * Muestra las tareas asignadas al desarrollador activo, el progreso
 * del proyecto y el feedback del gerente en caso de rechazo.
 */
public class DeveloperProjectPanel extends JPanel {

    private final Project          proyecto;
    private final Runnable         alActualizar;
    private final ProjectProgressBar barraProgreso;
    private final JPanel           listaTareas;
    private final JLabel           lblMensaje;
    private final HistoryPanel     historyPanel;

    public DeveloperProjectPanel(Project proyecto, Runnable alActualizar) {
        this.proyecto      = proyecto;
        this.alActualizar  = alActualizar;
        this.barraProgreso = new ProjectProgressBar();
        this.listaTareas   = new JPanel();
        this.lblMensaje    = new JLabel(" ");
        this.historyPanel  = new HistoryPanel();

        listaTareas.setLayout(new BoxLayout(listaTareas, BoxLayout.Y_AXIS));
        listaTareas.setBackground(AppTheme.FONDO_PRINCIPAL);

        construirUI();
        refrescarSinCallback(); // Usar versión sin callback en el constructor
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.FONDO_PRINCIPAL);

        add(crearCabecera(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FUENTE_NORMAL);
        tabs.addTab("Mis tareas",          crearPanelTareas());
        tabs.addTab("Historial del proyecto", historyPanel);
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

        JLabel estado = new JLabel("Estado del proyecto: " + proyecto.getEstadoNombre());
        estado.setFont(AppTheme.FUENTE_PEQUEÑA);
        estado.setForeground(AppTheme.TEXTO_SECUNDARIO);

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(nombre);
        textos.add(estado);

        panel.add(textos,        BorderLayout.WEST);
        panel.add(barraProgreso, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelTareas() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(AppTheme.FONDO_PRINCIPAL);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("Tareas asignadas a mí");
        titulo.setFont(AppTheme.FUENTE_SUBTITULO);
        titulo.setForeground(AppTheme.ACENTO_PRIMARIO);

        JScrollPane scroll = new JScrollPane(listaTareas);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDE_SUAVE, 1, true));

        JPanel pie = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pie.setBackground(AppTheme.FONDO_PRINCIPAL);
        lblMensaje.setFont(AppTheme.FUENTE_PEQUEÑA);
        pie.add(lblMensaje);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(pie,    BorderLayout.SOUTH);
        return panel;
    }

    private void refrescar() {
        barraProgreso.actualizar(proyecto.calcularPorcentajeProgreso());
        historyPanel.cargarHistorial(proyecto);
        construirListaTareas();
        if (alActualizar != null) alActualizar.run();
    }

    // Versión sin callback para evitar bucles infinitos en el constructor
    private void refrescarSinCallback() {
        barraProgreso.actualizar(proyecto.calcularPorcentajeProgreso());
        historyPanel.cargarHistorial(proyecto);
        construirListaTareas();
        // NO llamar a alActualizar aquí
    }

    private void construirListaTareas() {
        listaTareas.removeAll();
        String devId       = SessionManager.getInstance().getUsuarioActivo().getId();
        int    grupoActivo = proyecto.obtenerGrupoActivo();

        List<Task> misTareas = proyecto.getTareas().stream()
                .filter(t -> t.getAsignadoAId().equals(devId))
                .toList();

        if (misTareas.isEmpty()) {
            JLabel vacio = new JLabel("  No tienes tareas asignadas en este proyecto.");
            vacio.setFont(AppTheme.FUENTE_NORMAL);
            vacio.setForeground(AppTheme.TEXTO_SECUNDARIO);
            vacio.setBorder(new EmptyBorder(20, 0, 0, 0));
            listaTareas.add(vacio);
        } else {
            for (Task tarea : misTareas) {
                boolean esActiva = tarea.getOrden() == grupoActivo;
                listaTareas.add(crearTarjetaTarea(tarea, esActiva));
                listaTareas.add(Box.createVerticalStrut(8));
            }
        }

        listaTareas.revalidate();
        listaTareas.repaint();
    }

    private JPanel crearTarjetaTarea(Task tarea, boolean esActiva) {
        JPanel tarjeta = new JPanel(new BorderLayout(12, 0));
        tarjeta.setBackground(AppTheme.colorEstadoTarea(tarea.getEstadoNombre()));
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(
                esActiva ? AppTheme.VERDE_EXITO : AppTheme.BORDE_SUAVE, 2, true),
            new EmptyBorder(12, 14, 12, 14)
        ));
        tarjeta.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        // Información
        JPanel info = new JPanel(new GridLayout(4, 1, 0, 2));
        info.setOpaque(false);

        JLabel lblTitulo = new JLabel(tarea.getTitulo());
        lblTitulo.setFont(AppTheme.FUENTE_SUBTITULO);
        lblTitulo.setForeground(AppTheme.TEXTO_PRINCIPAL);

        JLabel lblDesc = new JLabel(tarea.getDescripcion());
        lblDesc.setFont(AppTheme.FUENTE_PEQUEÑA);
        lblDesc.setForeground(AppTheme.TEXTO_SECUNDARIO);

        JLabel lblOrden = new JLabel("Grupo de ejecución: " + tarea.getOrden()
            + (esActiva ? "  ← activo ahora" : "  (bloqueado)"));
        lblOrden.setFont(AppTheme.FUENTE_PEQUEÑA);
        lblOrden.setForeground(esActiva ? AppTheme.VERDE_EXITO : AppTheme.TEXTO_SECUNDARIO);

        JLabel lblFeedback = new JLabel(" ");
        if (tarea.getJustificacionRechazo() != null) {
            lblFeedback.setText("⚠ Corrección requerida: " + tarea.getJustificacionRechazo());
            lblFeedback.setForeground(AppTheme.ROJO_ERROR);
            lblFeedback.setFont(AppTheme.FUENTE_PEQUEÑA);
        }

        info.add(lblTitulo);
        info.add(lblDesc);
        info.add(lblOrden);
        info.add(lblFeedback);

        // Estado + botones
        JPanel derecha = new JPanel(new BorderLayout(0, 6));
        derecha.setOpaque(false);

        JLabel lblEstado = new JLabel(tarea.getEstadoNombre());
        lblEstado.setFont(AppTheme.FUENTE_BOTON);
        lblEstado.setForeground(AppTheme.ACENTO_PRIMARIO);
        lblEstado.setHorizontalAlignment(SwingConstants.RIGHT);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        botones.setOpaque(false);

        if (esActiva) {
            agregarBotonesTarea(botones, tarea);
        } else {
            JLabel bloqueado = new JLabel("🔒 Bloqueada");
            bloqueado.setFont(AppTheme.FUENTE_PEQUEÑA);
            bloqueado.setForeground(AppTheme.TEXTO_SECUNDARIO);
            botones.add(bloqueado);
        }

        derecha.add(lblEstado, BorderLayout.NORTH);
        derecha.add(botones,   BorderLayout.SOUTH);

        tarjeta.add(info,    BorderLayout.CENTER);
        tarjeta.add(derecha, BorderLayout.EAST);
        return tarjeta;
    }

    private void agregarBotonesTarea(JPanel panel, Task tarea) {
        switch (tarea.getEstadoNombre()) {
            case "Pendiente" -> {
                JButton btn = AppTheme.botonPrimario("Iniciar tarea");
                btn.addActionListener(e -> { tarea.iniciar(); mostrarMensaje(tarea); refrescar(); });
                panel.add(btn);
            }
            case "En Progreso" -> {
                JButton btn = AppTheme.botonSecundario("Marcar como lista ✓");
                btn.addActionListener(e -> { tarea.marcarComoLista(); mostrarMensaje(tarea); refrescar(); });
                JButton btnBloquear = AppTheme.botonPeligro("Bloquear");
                btnBloquear.addActionListener(e -> { tarea.bloquear(); mostrarMensaje(tarea); refrescar(); });
                panel.add(btn);
                panel.add(btnBloquear);
            }
            case "Bloqueada" -> {
                JButton btn = AppTheme.botonNeutral("Desbloquear");
                btn.addActionListener(e -> { tarea.desbloquear(); mostrarMensaje(tarea); refrescar(); });
                panel.add(btn);
            }
            case "Pendiente de Aprobación" -> {
                JLabel espera = new JLabel("⏳ Esperando aprobación del gerente");
                espera.setFont(AppTheme.FUENTE_PEQUEÑA);
                espera.setForeground(AppTheme.AMARILLO_AVISO);
                panel.add(espera);
            }
            case "Completada" -> {
                JLabel ok = new JLabel("✅ Aprobada por el gerente");
                ok.setFont(AppTheme.FUENTE_PEQUEÑA);
                ok.setForeground(AppTheme.VERDE_EXITO);
                panel.add(ok);
            }
        }
    }

    private void mostrarMensaje(Task tarea) {
        String msg = tarea.getUltimoMensaje();
        lblMensaje.setText(msg);
        lblMensaje.setForeground(
            msg.startsWith("⚠") ? AppTheme.ROJO_ERROR : AppTheme.VERDE_EXITO);
    }
}
