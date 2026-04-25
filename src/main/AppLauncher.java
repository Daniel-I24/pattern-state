package main;

import auth.AuthService;
import auth.Role;
import auth.SessionManager;
import auth.User;
import service.ProjectService;
import ui.AppTheme;
import ui.LoginPanel;
import ui.RegisterPanel;
import ui.manager.ManagerDashboard;
import ui.developer.DeveloperDashboard;
import ui.client.ClientDashboard;

import javax.swing.*;
import java.awt.*;

/**
 * Punto de entrada de la aplicación.
 * Gestiona la navegación entre login, registro y los 3 dashboards específicos por rol.
 */
public class AppLauncher {

    private static final String PANTALLA_LOGIN     = "login";
    private static final String PANTALLA_REGISTRO  = "registro";
    private static final String PANTALLA_GERENTE   = "gerente";
    private static final String PANTALLA_DEV       = "desarrollador";
    private static final String PANTALLA_CLIENTE   = "cliente";

    private final JFrame ventana;
    private final CardLayout cardLayout;
    private final JPanel contenedor;

    private final AuthService    authService;
    private final ProjectService projectService;

    private ManagerDashboard    dashboardGerente;
    private DeveloperDashboard  dashboardDev;
    private ClientDashboard     dashboardCliente;

    public AppLauncher() {
        this.authService    = new AuthService();
        this.projectService = new ProjectService();
        this.cardLayout     = new CardLayout();
        this.contenedor     = new JPanel(cardLayout);
        this.ventana        = new JFrame("Sistema de Gestión de Proyectos - Patrón State");

        configurarVentana();
        registrarPantallas();
        mostrar(PANTALLA_LOGIN);
    }

    private void configurarVentana() {
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1200, 750);
        ventana.setMinimumSize(new Dimension(1000, 650));
        ventana.setLocationRelativeTo(null);
        ventana.setContentPane(contenedor);
    }

    private void registrarPantallas() {
        LoginPanel loginPanel = new LoginPanel(
            authService,
            this::mostrarDashboardSegunRol,
            () -> mostrar(PANTALLA_REGISTRO)
        );

        RegisterPanel registerPanel = new RegisterPanel(
            authService,
            () -> mostrar(PANTALLA_LOGIN),
            () -> mostrar(PANTALLA_LOGIN)
        );

        contenedor.add(loginPanel,    PANTALLA_LOGIN);
        contenedor.add(registerPanel, PANTALLA_REGISTRO);
    }

    /**
     * Redirige al dashboard correspondiente según el rol del usuario activo.
     * Cada dashboard se recrea en cada inicio de sesión para reflejar datos actualizados.
     */
    private void mostrarDashboardSegunRol() {
        User usuario = SessionManager.getInstance().getUsuarioActivo();
        if (usuario == null) return;

        Role rol = usuario.getRol();

        switch (rol) {
            case GERENTE -> {
                if (dashboardGerente != null) contenedor.remove(dashboardGerente);
                dashboardGerente = new ManagerDashboard(projectService, authService,
                        () -> mostrar(PANTALLA_LOGIN));
                contenedor.add(dashboardGerente, PANTALLA_GERENTE);
                dashboardGerente.inicializar();
                mostrar(PANTALLA_GERENTE);
            }
            case DESARROLLADOR -> {
                if (dashboardDev != null) contenedor.remove(dashboardDev);
                dashboardDev = new DeveloperDashboard(projectService,
                        () -> mostrar(PANTALLA_LOGIN));
                contenedor.add(dashboardDev, PANTALLA_DEV);
                dashboardDev.inicializar();
                mostrar(PANTALLA_DEV);
            }
            case CLIENTE -> {
                if (dashboardCliente != null) contenedor.remove(dashboardCliente);
                dashboardCliente = new ClientDashboard(projectService,
                        () -> mostrar(PANTALLA_LOGIN));
                contenedor.add(dashboardCliente, PANTALLA_CLIENTE);
                dashboardCliente.inicializar();
                mostrar(PANTALLA_CLIENTE);
            }
        }
    }

    private void mostrar(String pantalla) {
        cardLayout.show(contenedor, pantalla);
    }

    public static void main(String[] args) {
        AppTheme.aplicar();
        SwingUtilities.invokeLater(() -> {
            AppLauncher app = new AppLauncher();
            app.ventana.setVisible(true);
        });
    }
}
