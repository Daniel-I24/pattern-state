package ui.manager;

import auth.AuthService;
import auth.SessionManager;
import model.Project;
import service.ProjectService;
import ui.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Interfaz exclusiva del Gerente.
 * Permite crear proyectos, asignar tareas, aprobar o rechazar el trabajo
 * de los desarrolladores y avanzar el estado del proyecto.
 */
public class ManagerDashboard extends JPanel {

    private final ProjectService projectService;
    private final AuthService    authService;
    private final Runnable       alCerrarSesion;

    private final DefaultListModel<Project> modeloLista = new DefaultListModel<>();
    private final JList<Project>            listaProyectos;
    private final JPanel                    areaCentral;
    private final CardLayout                cardCentral;

    private static final String CARD_VACIO   = "vacio";
    private static final String CARD_DETALLE = "detalle";

    private ManagerProjectPanel panelDetalle;

    public ManagerDashboard(ProjectService projectService, AuthService authService,
                            Runnable alCerrarSesion) {
        this.projectService = projectService;
        this.authService    = authService;
        this.alCerrarSesion = alCerrarSesion;
        this.listaProyectos = new JList<>(modeloLista);
        this.cardCentral    = new CardLayout();
        this.areaCentral    = new JPanel(cardCentral);

        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.FONDO_PRINCIPAL);

        add(crearSidebar(),  BorderLayout.WEST);
        add(areaCentral,     BorderLayout.CENTER);

        areaCentral.add(crearPanelVacio(), CARD_VACIO);
        cardCentral.show(areaCentral, CARD_VACIO);
    }

    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(AppTheme.FONDO_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(250, 0));

        sidebar.add(crearCabeceraSidebar(), BorderLayout.NORTH);
        sidebar.add(crearScrollLista(),     BorderLayout.CENTER);
        sidebar.add(crearPieSidebar(),      BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel crearCabeceraSidebar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.FONDO_SIDEBAR);
        panel.setBorder(new EmptyBorder(20, 16, 12, 16));

        JLabel titulo = new JLabel("Mis Proyectos");
        titulo.setFont(AppTheme.FUENTE_SUBTITULO);
        titulo.setForeground(AppTheme.TEXTO_CLARO);

        String nombre = SessionManager.getInstance().getUsuarioActivo().getNombre();
        JLabel usuario = new JLabel(nombre + " · Gerente");
        usuario.setFont(AppTheme.FUENTE_PEQUEÑA);
        usuario.setForeground(AppTheme.AMARILLO_AVISO);

        panel.add(titulo,  BorderLayout.NORTH);
        panel.add(usuario, BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane crearScrollLista() {
        listaProyectos.setBackground(AppTheme.FONDO_SIDEBAR);
        listaProyectos.setForeground(AppTheme.TEXTO_CLARO);
        listaProyectos.setFont(AppTheme.FUENTE_NORMAL);
        listaProyectos.setSelectionBackground(AppTheme.ACENTO_PRIMARIO);
        listaProyectos.setFixedCellHeight(52);
        listaProyectos.setBorder(new EmptyBorder(4, 8, 4, 8));

        listaProyectos.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JPanel celda = new JPanel(new BorderLayout(0, 2));
            celda.setBackground(isSelected ? AppTheme.ACENTO_PRIMARIO : AppTheme.FONDO_SIDEBAR);
            celda.setBorder(new EmptyBorder(8, 10, 8, 10));

            JLabel nombre = new JLabel(value.getNombre());
            nombre.setFont(AppTheme.FUENTE_BOTON);
            nombre.setForeground(AppTheme.TEXTO_CLARO);

            JLabel estado = new JLabel(value.getEstadoNombre()
                + "  " + value.calcularPorcentajeProgreso() + "%");
            estado.setFont(AppTheme.FUENTE_PEQUEÑA);
            estado.setForeground(AppTheme.AMARILLO_AVISO);

            celda.add(nombre, BorderLayout.NORTH);
            celda.add(estado, BorderLayout.SOUTH);
            return celda;
        });

        listaProyectos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) seleccionarProyecto();
        });

        JScrollPane scroll = new JScrollPane(listaProyectos);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(AppTheme.FONDO_SIDEBAR);
        return scroll;
    }

    private JPanel crearPieSidebar() {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 8));
        panel.setBackground(AppTheme.FONDO_SIDEBAR);
        panel.setBorder(new EmptyBorder(12, 12, 16, 12));

        JButton btnNuevo = AppTheme.botonSecundario("+ Nuevo proyecto");
        btnNuevo.addActionListener(e -> mostrarDialogoNuevoProyecto());

        JButton btnSalir = AppTheme.botonNeutral("Cerrar sesión");
        btnSalir.addActionListener(e -> {
            SessionManager.getInstance().cerrarSesion();
            alCerrarSesion.run();
        });

        panel.add(btnNuevo);
        panel.add(btnSalir);
        return panel;
    }

    private JPanel crearPanelVacio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.FONDO_PRINCIPAL);
        JLabel msg = new JLabel("Selecciona o crea un proyecto para comenzar");
        msg.setFont(AppTheme.FUENTE_NORMAL);
        msg.setForeground(AppTheme.TEXTO_SECUNDARIO);
        panel.add(msg);
        return panel;
    }

    private void seleccionarProyecto() {
        Project proyecto = listaProyectos.getSelectedValue();
        if (proyecto == null) return;

        if (panelDetalle != null) areaCentral.remove(panelDetalle);

        panelDetalle = new ManagerProjectPanel(proyecto, projectService, authService,
                this::refrescarLista);
        areaCentral.add(panelDetalle, CARD_DETALLE);
        cardCentral.show(areaCentral, CARD_DETALLE);
    }

    public void inicializar() {
        refrescarLista();
    }

    private void refrescarLista() {
        Project seleccionado = listaProyectos.getSelectedValue();
        modeloLista.clear();
        for (Project p : projectService.obtenerTodos()) {
            modeloLista.addElement(p);
        }
        if (seleccionado != null) {
            listaProyectos.setSelectedValue(seleccionado, true);
        } else if (!modeloLista.isEmpty()) {
            listaProyectos.setSelectedIndex(0);
            seleccionarProyecto(); // Forzar mostrar el panel del primer proyecto
        }
        listaProyectos.repaint();
    }

    private void mostrarDialogoNuevoProyecto() {
        JTextField campoNombre = AppTheme.campoTexto(22);
        JTextArea  campoDesc   = AppTheme.areaTexto(3, 22);
        AppTheme.aplicarPlaceholder(campoNombre, "Ej: Sistema de facturación v2");
        AppTheme.aplicarPlaceholder(campoDesc,   "Describe el objetivo del proyecto...");

        JPanel form = new JPanel(new GridLayout(4, 1, 0, 6));
        form.setBackground(AppTheme.FONDO_PANEL);
        form.add(new JLabel("Nombre del proyecto:"));
        form.add(campoNombre);
        form.add(new JLabel("Descripción:"));
        form.add(new JScrollPane(campoDesc));

        int res = JOptionPane.showConfirmDialog(this, form,
            "Nuevo proyecto", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res != JOptionPane.OK_OPTION) return;

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
        // Forzar la selección y mostrar el panel de detalle
        seleccionarProyecto();
    }
}
