package ui.client;

import model.Project;
import model.Task;
import ui.AppTheme;
import ui.shared.HistoryPanel;
import ui.shared.ProjectProgressBar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Panel de detalle de un proyecto desde la vista del Cliente.
 * Muestra el estado general, el progreso, el resumen de tareas
 * y permite aprobar o solicitar correcciones cuando corresponde.
 */
public class ClientProjectPanel extends JPanel {

    private final Project          proyecto;
    private final Runnable         alActualizar;
    private final ProjectProgressBar barraProgreso;
    private final JPanel           panelAcciones;
    private final JLabel           lblEstado;
    private final JLabel           lblMensaje;
    private final HistoryPanel     historyPanel;

    public ClientProjectPanel(Project proyecto, Runnable alActualizar) {
        this.proyecto      = proyecto;
        this.alActualizar  = alActualizar;
        this.barraProgreso = new ProjectProgressBar();
        this.panelAcciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        this.lblEstado     = new JLabel();
        this.lblMensaje    = new JLabel(" ");
        this.historyPanel  = new HistoryPanel();

        construirUI();
        refrescar();
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.FONDO_PRINCIPAL);

        add(crearCabecera(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(AppTheme.FUENTE_NORMAL);
        tabs.addTab("Estado del proyecto", crearPanelEstado());
        tabs.addTab("Resumen de tareas",   crearPanelResumenTareas());
        tabs.addTab("Historial",           historyPanel);
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

        panel.add(textos,        BorderLayout.WEST);
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

        // Acciones del cliente
        JPanel seccionAcciones = new JPanel(new BorderLayout(0, 10));
        seccionAcciones.setBackground(AppTheme.FONDO_PRINCIPAL);

        JLabel tituloAcciones = new JLabel("Tus acciones");
        tituloAcciones.setFont(AppTheme.FUENTE_SUBTITULO);
        tituloAcciones.setForeground(AppTheme.ACENTO_PRIMARIO);

        panelAcciones.setBackground(AppTheme.FONDO_PRINCIPAL);
        lblMensaje.setFont(AppTheme.FUENTE_NORMAL);

        seccionAcciones.add(tituloAcciones, BorderLayout.NORTH);
        seccionAcciones.add(panelAcciones,  BorderLayout.CENTER);
        seccionAcciones.add(lblMensaje,     BorderLayout.SOUTH);

        panel.add(tarjeta,         BorderLayout.NORTH);
        panel.add(seccionAcciones, BorderLayout.CENTER);
        return panel;
    }

    private JScrollPane crearPanelResumenTareas() {
        JPanel lista = new JPanel();
        lista.setLayout(new BoxLayout(lista, BoxLayout.Y_AXIS));
        lista.setBackground(AppTheme.FONDO_PRINCIPAL);
        lista.setBorder(new EmptyBorder(16, 16, 16, 16));

        for (Task tarea : proyecto.getTareas()) {
            JPanel fila = new JPanel(new BorderLayout(12, 0));
            fila.setBackground(AppTheme.colorEstadoTarea(tarea.getEstadoNombre()));
            fila.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AppTheme.BORDE_SUAVE, 1, true),
                new EmptyBorder(8, 12, 8, 12)
            ));
            fila.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JLabel lblTitulo = new JLabel(tarea.getTitulo());
            lblTitulo.setFont(AppTheme.FUENTE_NORMAL);
            lblTitulo.setForeground(AppTheme.TEXTO_PRINCIPAL);

            JLabel lblEstadoTarea = new JLabel(tarea.getEstadoNombre());
            lblEstadoTarea.setFont(AppTheme.FUENTE_BOTON);
            lblEstadoTarea.setForeground(AppTheme.ACENTO_PRIMARIO);

            fila.add(lblTitulo,      BorderLayout.WEST);
            fila.add(lblEstadoTarea, BorderLayout.EAST);
            lista.add(fila);
            lista.add(Box.createVerticalStrut(6));
        }

        JScrollPane scroll = new JScrollPane(lista);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }

    private void refrescar() {
        String estado = proyecto.getEstadoNombre();
        lblEstado.setText("Estado actual: " + estado);
        lblEstado.setBackground(AppTheme.colorEstadoProyecto(estado));
        lblEstado.setOpaque(true);

        barraProgreso.actualizar(proyecto.calcularPorcentajeProgreso());
        historyPanel.cargarHistorial(proyecto);
        construirBotonesCliente(estado);

        if (alActualizar != null) alActualizar.run();
    }

    private void construirBotonesCliente(String estado) {
        panelAcciones.removeAll();

        if (estado.equals("En Revisión del Cliente")) {
            JButton btnAceptar = AppTheme.botonSecundario("✓ Aceptar proyecto");
            btnAceptar.addActionListener(e -> {
                proyecto.aceptar();
                mostrarMensaje("Proyecto aceptado. El gerente procederá con el despliegue.", false);
                refrescar();
            });

            JButton btnCorrecciones = AppTheme.botonPeligro("✗ Solicitar correcciones");
            btnCorrecciones.addActionListener(e -> solicitarCorrecciones());

            panelAcciones.add(btnAceptar);
            panelAcciones.add(btnCorrecciones);
        } else {
            JLabel info = new JLabel("El proyecto aún no está disponible para tu revisión. Estado: " + estado);
            info.setFont(AppTheme.FUENTE_NORMAL);
            info.setForeground(AppTheme.TEXTO_SECUNDARIO);
            panelAcciones.add(info);
        }

        panelAcciones.revalidate();
        panelAcciones.repaint();
    }

    private void solicitarCorrecciones() {
        JTextArea campoComentario = AppTheme.areaTexto(4, 30);
        AppTheme.aplicarPlaceholder(campoComentario,
            "Describe qué aspectos necesitan corrección o mejora...");

        JPanel form = new JPanel(new BorderLayout(0, 8));
        form.setBackground(AppTheme.FONDO_PANEL);
        form.add(new JLabel("Comentarios para el equipo (obligatorio):"), BorderLayout.NORTH);
        form.add(new JScrollPane(campoComentario), BorderLayout.CENTER);

        int res = JOptionPane.showConfirmDialog(this, form,
            "Solicitar correcciones", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return;

        String comentario = campoComentario.getText().trim();
        if (comentario.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes describir qué necesita corrección.",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            return;
        }

        proyecto.solicitarCorrecciones();
        mostrarMensaje("Correcciones solicitadas. El equipo fue notificado.", true);
        refrescar();
    }

    private void mostrarMensaje(String texto, boolean esAviso) {
        lblMensaje.setText(texto);
        lblMensaje.setForeground(esAviso ? AppTheme.AMARILLO_AVISO : AppTheme.VERDE_EXITO);
    }
}
