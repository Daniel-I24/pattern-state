package ui;

import auth.AuthService;
import auth.SessionManager;
import auth.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Pantalla de inicio de sesión.
 * Valida credenciales y redirige al dashboard según el rol del usuario.
 */
public class LoginPanel extends JPanel {

    private final AuthService authService;
    private final Runnable alIniciarSesion;
    private final Runnable alIrARegistro;

    private final JTextField campoCorreo;
    private final JPasswordField campoContrasena;
    private final JLabel etiquetaError;

    public LoginPanel(AuthService authService, Runnable alIniciarSesion, Runnable alIrARegistro) {
        this.authService      = authService;
        this.alIniciarSesion  = alIniciarSesion;
        this.alIrARegistro    = alIrARegistro;

        this.campoCorreo      = AppTheme.campoTexto(24);
        this.campoContrasena  = AppTheme.campoContrasena(24);
        this.etiquetaError    = new JLabel(" ");

        construirUI();
    }

    private void construirUI() {
        setLayout(new GridBagLayout());
        setBackground(AppTheme.FONDO_PRINCIPAL);

        JPanel tarjeta = crearTarjeta();
        add(tarjeta);
    }

    private JPanel crearTarjeta() {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BoxLayout(tarjeta, BoxLayout.Y_AXIS));
        tarjeta.setBackground(AppTheme.FONDO_PANEL);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppTheme.BORDE_SUAVE, 1, true),
            new EmptyBorder(40, 50, 40, 50)
        ));

        tarjeta.add(crearEncabezado());
        tarjeta.add(Box.createVerticalStrut(30));
        tarjeta.add(crearFormulario());
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(crearEtiquetaError());
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(crearBotones());
        tarjeta.add(Box.createVerticalStrut(20));
        tarjeta.add(crearEnlaceRegistro());

        return tarjeta;
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppTheme.FONDO_PANEL);
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titulo = new JLabel("Gestión de Proyectos");
        titulo.setFont(AppTheme.FUENTE_TITULO);
        titulo.setForeground(AppTheme.ACENTO_PRIMARIO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Inicia sesión para continuar");
        subtitulo.setFont(AppTheme.FUENTE_NORMAL);
        subtitulo.setForeground(AppTheme.TEXTO_SECUNDARIO);
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titulo);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitulo);
        return panel;
    }

    private JPanel crearFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(AppTheme.FONDO_PANEL);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        agregarCampo(panel, gbc, 0, "Correo electrónico", campoCorreo);
        agregarCampo(panel, gbc, 1, "Contraseña", campoContrasena);

        return panel;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridy = fila * 2;
        JLabel label = new JLabel(etiqueta);
        label.setFont(AppTheme.FUENTE_PEQUEÑA);
        label.setForeground(AppTheme.TEXTO_SECUNDARIO);
        panel.add(label, gbc);

        gbc.gridy = fila * 2 + 1;
        panel.add(campo, gbc);
    }

    private JPanel crearEtiquetaError() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(AppTheme.FONDO_PANEL);
        etiquetaError.setFont(AppTheme.FUENTE_PEQUEÑA);
        etiquetaError.setForeground(AppTheme.ROJO_ERROR);
        panel.add(etiquetaError);
        return panel;
    }

    private JPanel crearBotones() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(AppTheme.FONDO_PANEL);

        JButton btnIngresar = AppTheme.botonPrimario("Iniciar sesión");
        btnIngresar.setPreferredSize(new Dimension(200, 40));
        btnIngresar.addActionListener(e -> intentarLogin());

        campoContrasena.addActionListener(e -> intentarLogin());

        panel.add(btnIngresar);
        return panel;
    }

    private JPanel crearEnlaceRegistro() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(AppTheme.FONDO_PANEL);

        JLabel texto = new JLabel("¿No tienes cuenta? ");
        texto.setFont(AppTheme.FUENTE_PEQUEÑA);
        texto.setForeground(AppTheme.TEXTO_SECUNDARIO);

        JButton enlace = new JButton("Regístrate aquí");
        enlace.setFont(AppTheme.FUENTE_PEQUEÑA);
        enlace.setForeground(AppTheme.ACENTO_PRIMARIO);
        enlace.setBorderPainted(false);
        enlace.setContentAreaFilled(false);
        enlace.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        enlace.addActionListener(e -> alIrARegistro.run());

        panel.add(texto);
        panel.add(enlace);
        return panel;
    }

    private void intentarLogin() {
        String correo     = campoCorreo.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());

        if (correo.isEmpty() || contrasena.isEmpty()) {
            etiquetaError.setText("Por favor completa todos los campos.");
            return;
        }

        User usuario = authService.iniciarSesion(correo, contrasena);
        if (usuario == null) {
            etiquetaError.setText("Correo o contraseña incorrectos.");
            campoContrasena.setText("");
            return;
        }

        SessionManager.getInstance().iniciarSesion(usuario);
        limpiarFormulario();
        alIniciarSesion.run();
    }

    private void limpiarFormulario() {
        campoCorreo.setText("");
        campoContrasena.setText("");
        etiquetaError.setText(" ");
    }
}
