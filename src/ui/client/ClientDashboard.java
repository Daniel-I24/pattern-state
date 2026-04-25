package ui.client;

import auth.SessionManager;
import model.Project;
import service.ProjectService;
import ui.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Interfaz exclusiva del Cliente.
 * Permite ver el estado de los proyectos, el progreso general
 * y aprobar o solicitar correcciones cuando el proyecto llega
 * al estado "En Revisión del Cliente".
 */
public class ClientDashboard extends JPanel {

    private final ProjectService projectService;
    private final Runnable       alCerrarSesion;

    private final DefaultListModel<Project> modeloLista = new DefaultListModel<>();
    private final JList<Project>            listaProyectos;
    private final JPanel                    areaCentral;
    private final CardLayout                cardCentral;

    private static final String CARD_VACIO   = "vacio";
    private static final String CARD_DETALLE = "detalle";

    private ClientProjectPanel panelDetalle;

    public ClientDashboard(ProjectService projectService, Runnable alCerrarSesion) {
        this.projectService = projectService;
        this.alCerrarSesion = alCerrarSesion;
        this.listaProyectos = new JList<>(modeloLista);
        this.cardCentral    = new CardLayout();
        this.areaCentral    = new JPanel(cardCentral);

        construirUI();
    }

    private void construirUI() {
        setLayout(new BorderLayout());
        setBackground(AppTheme.FONDO_PRINCIPAL);

        add(crearSidebar(), BorderLayout.WEST);
        add(areaCentral,    BorderLayout.CENTER);

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

        JLabel titulo = new JLabel("Proyectos");
        titulo.setFont(AppTheme.FUENTE_SUBTITULO);
        titulo.setForeground(AppTheme.TEXTO_CLARO);

        String nombre = SessionManager.getInstance().getUsuarioActivo().getNombre();
        JLabel usuario = new JLabel(nombre + " · Cliente");
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

            boolean requiereAccion = value.getEstadoNombre().equals("En Revisión del Cliente");
            JLabel estado = new JLabel(value.getEstadoNombre()
                + (requiereAccion ? "  ← requiere tu acción" : ""));
            estado.setFont(AppTheme.FUENTE_PEQUEÑA);
            estado.setForeground(requiereAccion ? AppTheme.AMARILLO_AVISO : new Color(0xAAAAAA));

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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppTheme.FONDO_SIDEBAR);
        panel.setBorder(new EmptyBorder(12, 12, 16, 12));

        JButton btnSalir = AppTheme.botonNeutral("Cerrar sesión");
        btnSalir.addActionListener(e -> {
            SessionManager.getInstance().cerrarSesion();
            alCerrarSesion.run();
        });
        panel.add(btnSalir, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelVacio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.FONDO_PRINCIPAL);
        JLabel msg = new JLabel("Selecciona un proyecto para ver su estado");
        msg.setFont(AppTheme.FUENTE_NORMAL);
        msg.setForeground(AppTheme.TEXTO_SECUNDARIO);
        panel.add(msg);
        return panel;
    }

    private void seleccionarProyecto() {
        Project proyecto = listaProyectos.getSelectedValue();
        if (proyecto == null) return;

        if (panelDetalle != null) areaCentral.remove(panelDetalle);

        panelDetalle = new ClientProjectPanel(proyecto, this::refrescarLista);
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
}
