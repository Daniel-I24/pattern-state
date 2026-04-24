package main;

import auth.AuthService;
import service.ProjectService;
import ui.AppTheme;
import ui.DashboardPanel;
import ui.LoginPanel;
import ui.RegisterPanel;

import javax.swing.*;
import java.awt.*;

/**
 * Punto de entrada de la aplicación.
 * Gestiona la navegación entre las pantallas principales.
 */
public class AppLauncher {

    private static final String PANTALLA_LOGIN     = "login";
    private static final String PANTALLA_REGISTRO  = "registro";
    private static final String PANTALLA_DASHBOARD = "dashboard";

    private final JFrame ventana;
    private final CardLayout cardLayout;
    private final JPanel contenedor;

    private final AuthService    authService;
    private final ProjectService projectService;

    private DashboardPanel dashboardPanel;

    public AppLauncher() {
        this.authService    = new AuthService();
        this.projectService = new ProjectService();
        this.cardLayout     = new CardLayout();
        this.contenedor     = new JPanel(cardLayout);
        this.ventana        = new JFrame("Sistema de Gestión de Proyectos");

        configurarVentana();
        registrarPantallas();
        mostrar(PANTALLA_LOGIN);
    }

    private void configurarVentana() {
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(1100, 700);
        ventana.setMinimumSize(new Dimension(900, 600));
        ventana.setLocationRelativeTo(null);
        ventana.setContentPane(contenedor);
    }

    private void registrarPantallas() {
        LoginPanel loginPanel = new LoginPanel(
            authService,
            () -> mostrarDashboard(),
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

    private void mostrarDashboard() {
        // Se recrea el dashboard en cada inicio de sesión para reflejar el usuario activo
        if (dashboardPanel != null) {
            contenedor.remove(dashboardPanel);
        }
        dashboardPanel = new DashboardPanel(projectService, () -> mostrar(PANTALLA_LOGIN));
        contenedor.add(dashboardPanel, PANTALLA_DASHBOARD);
        dashboardPanel.inicializar();
        mostrar(PANTALLA_DASHBOARD);
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
