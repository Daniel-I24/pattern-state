package ui;

import auth.SessionManager;
import model.Project;
import service.ProjectService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Panel principal del sistema tras iniciar sesión.
 * Muestra la lista de proyectos en un sidebar y el detalle en el panel central.
 */
public class DashboardPanel extends JPanel {

    private final ProjectService projectService;
    private final Runnable alCerrarSesion;

    private final DefaultListModel<Project> modeloLista;
    private final JList<Project> listaProyectos;
    private final ProjectPanel projectPanel;
    private final JLabel lblBienvenida;

    public DashboardPanel(ProjectService projectService, Runnable alCerrarSesion) {
        this.projectService = projectService;
        this.alCerrarSesion = alCerrarSesion;
        this.modeloLista    = new DefaultListModel<>();
        this.listaProyectos = new JList<>(modeloLista);
        this.projectPanel   = new ProjectPanel(projectService);
        this.lblBienvenida  = new JLabel();

        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.FONDO_PRINCIPAL);

        add(crearSidebar(),       BorderLayout.WEST);
        add(crearAreaContenido(), BorderLayout.CENTER);
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(AppTheme.FONDO_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(240, 0));

        sidebar.add(crearCabeceraSidebar(), BorderLayout.NORTH);
        sidebar.add(crearListaProyectos(),  BorderLayout.CENTER);
        sidebar.add(crearPieSidebar(),      BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel crearCabeceraSidebar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.FONDO_SIDEBAR);
        panel.setBorder(new EmptyBorder(20, 16, 16, 16));

        JLabel titulo = new JLabel("Proyectos");
        titulo.setFont(AppTheme.FUENTE_SUBTITULO);
        titulo.setForeground(AppTheme.TEXTO_CLARO);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(lblBienvenida, BorderLayout.SOUTH);

        return panel;
    }

    private JScrollPane crearListaProyectos() {
        listaProyectos.setBackground(AppTheme.FONDO_SIDEBAR);
        listaProyectos.setForeground(AppTheme.TEXTO_CLARO);
        listaProyectos.setFont(AppTheme.FUENTE_NORMAL);
        listaProyectos.setSelectionBackground(AppTheme.ACENTO_PRIMARIO);
        listaProyectos.setSelectionForeground(AppTheme.TEXTO_CLARO);
        listaProyectos.setBorder(new EmptyBorder(4, 8, 4, 8));
        listaProyectos.setFixedCellHeight(40);

        listaProyectos.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                Project p = (Project) value;
                setText("<html><b>" + p.getNombre() + "</b><br>"
                    + "<small>" + p.getEstadoNombre() + "</small></html>");
                setBorder(new EmptyBorder(6, 10, 6, 10));
                if (!isSelected) {
                    setBackground(AppTheme.FONDO_SIDEBAR);
                    setForeground(AppTheme.TEXTO_CLARO);
                }
                return this;
            }
        });

        listaProyectos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Project seleccionado = listaProyectos.getSelectedValue();
                if (seleccionado != null) {
                    projectPanel.cargarProyecto(seleccionado);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(listaProyectos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(AppTheme.FONDO_SIDEBAR);
        return scroll;
    }

    private JPanel crearPieSidebar() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(AppTheme.FONDO_SIDEBAR);
        panel.setBorder(new EmptyBorder(12, 12, 16, 12));

        if (SessionManager.getInstance().esGerente()) {
            JButton btnNuevo = AppTheme.botonSecundario("+ Nuevo proyecto");
            btnNuevo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            btnNuevo.addActionListener(e -> mostrarDialogoNuevoProyecto());
            panel.add(btnNuevo, BorderLayout.NORTH);
        }

        JButton btnSalir = AppTheme.botonNeutral("Cerrar sesión");
        btnSalir.addActionListener(e -> {
            SessionManager.getInstance().cerrarSesion();
            alCerrarSesion.run();
        });
        panel.add(btnSalir, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearAreaContenido() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.FONDO_PRINCIPAL);
        panel.add(projectPanel, BorderLayout.CENTER);
        return panel;
    }

    /** Se llama al entrar al dashboard para cargar los datos del usuario activo. */
    public void inicializar() {
        String nombre = SessionManager.getInstance().getUsuarioActivo().getNombre();
        String rol    = SessionManager.getInstance().getUsuarioActivo().getRol().getEtiqueta();

        lblBienvenida.setText("<html><small style='color:#C8A96E'>" + nombre + " · " + rol + "</small></html>");
        lblBienvenida.setFont(AppTheme.FUENTE_PEQUEÑA);

        refrescarListaProyectos();
    }

    private void refrescarListaProyectos() {
        modeloLista.clear();
        List<Project> proyectos = projectService.obtenerTodos();
        for (Project p : proyectos) {
            modeloLista.addElement(p);
        }
        if (!modeloLista.isEmpty()) {
            listaProyectos.setSelectedIndex(0);
        }
    }

    private void mostrarDialogoNuevoProyecto() {
        JTextField campoNombre = AppTheme.campoTexto(22);
        JTextArea  campoDesc   = AppTheme.areaTexto(3, 22);

        JPanel form = new JPanel(new GridLayout(4, 1, 0, 6));
        form.setBackground(AppTheme.FONDO_PANEL);
        form.add(new JLabel("Nombre del proyecto:"));
        form.add(campoNombre);
        form.add(new JLabel("Descripción:"));
        form.add(new JScrollPane(campoDesc));

        int resultado = JOptionPane.showConfirmDialog(this, form,
            "Nuevo proyecto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            String nombre = campoNombre.getText().trim();
            String desc   = campoDesc.getText().trim();

            if (nombre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "El nombre del proyecto es obligatorio.",
                    "Datos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String creadoPor = SessionManager.getInstance().getUsuarioActivo().getNombre();
            Project nuevo = projectService.crearProyecto(nombre, desc, creadoPor);
            modeloLista.addElement(nuevo);
            listaProyectos.setSelectedValue(nuevo, true);
        }
    }
}
