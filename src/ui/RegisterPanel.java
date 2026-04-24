package ui;

import auth.AuthService;
import auth.Role;
import auth.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Pantalla de registro de nuevos usuarios.
 * Permite elegir nombre, correo, contraseña y rol.
 */
public class RegisterPanel extends JPanel {

    private final AuthService authService;
    private final Runnable alRegistrarse;
    private final Runnable alIrALogin;

    private final JTextField campoNombre;
    private final JTextField campoCorreo;
    private final JPasswordField campoContrasena;
    private final JPasswordField campoConfirmar;
    private final JComboBox<Role> comboRol;
    private final JLabel etiquetaError;

    public RegisterPanel(AuthService authService, Runnable alRegistrarse, Runnable alIrALogin) {
        this.authService   = authService;
        this.alRegistrarse = alRegistrarse;
        this.alIrALogin    = alIrALogin;

        this.campoNombre     = AppTheme.campoTexto(24);
        this.campoCorreo     = AppTheme.campoTexto(24);
        this.campoContrasena = AppTheme.campoContrasena(24);
        this.campoConfirmar  = AppTheme.campoContrasena(24);
        this.comboRol        = new JComboBox<>(Role.values());
        this.etiquetaError   = new JLabel(" ");

        construirUI();
    }

    private void construirUI() {
        setLayout(new GridBagLayout());
        setBackground(AppTheme.FONDO_PRINCIPAL);
        add(crearTarjeta());
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
        tarjeta.add(Box.createVerticalStrut(24));
        tarjeta.add(crearFormulario());
        tarjeta.add(Box.createVerticalStrut(8));
        tarjeta.add(crearEtiquetaError());
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(crearBotones());
        tarjeta.add(Box.createVerticalStrut(16));
        tarjeta.add(crearEnlaceLogin());

        return tarjeta;
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(AppTheme.FONDO_PANEL);

        JLabel titulo = new JLabel("Crear cuenta");
        titulo.setFont(AppTheme.FUENTE_TITULO);
        titulo.setForeground(AppTheme.ACENTO_PRIMARIO);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitulo = new JLabel("Completa los datos para registrarte");
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
        gbc.insets = new Insets(4, 0, 4, 0);
        gbc.fill   = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        String[] etiquetas = {"Nombre completo", "Correo electrónico", "Contraseña", "Confirmar contraseña"};
        JComponent[] campos = {campoNombre, campoCorreo, campoContrasena, campoConfirmar};

        for (int i = 0; i < etiquetas.length; i++) {
            gbc.gridy = i * 2;
            JLabel label = new JLabel(etiquetas[i]);
            label.setFont(AppTheme.FUENTE_PEQUEÑA);
            label.setForeground(AppTheme.TEXTO_SECUNDARIO);
            panel.add(label, gbc);

            gbc.gridy = i * 2 + 1;
            panel.add(campos[i], gbc);
        }

        gbc.gridy = 8;
        JLabel labelRol = new JLabel("Rol");
        labelRol.setFont(AppTheme.FUENTE_PEQUEÑA);
        labelRol.setForeground(AppTheme.TEXTO_SECUNDARIO);
        panel.add(labelRol, gbc);

        gbc.gridy = 9;
        comboRol.setFont(AppTheme.FUENTE_NORMAL);
        comboRol.setBackground(AppTheme.FONDO_PANEL);
        panel.add(comboRol, gbc);

        return panel;
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

        JButton btnRegistrar = AppTheme.botonSecundario("Crear cuenta");
        btnRegistrar.setPreferredSize(new Dimension(200, 40));
        btnRegistrar.addActionListener(e -> intentarRegistro());

        panel.add(btnRegistrar);
        return panel;
    }

    private JPanel crearEnlaceLogin() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(AppTheme.FONDO_PANEL);

        JLabel texto = new JLabel("¿Ya tienes cuenta? ");
        texto.setFont(AppTheme.FUENTE_PEQUEÑA);
        texto.setForeground(AppTheme.TEXTO_SECUNDARIO);

        JButton enlace = new JButton("Inicia sesión");
        enlace.setFont(AppTheme.FUENTE_PEQUEÑA);
        enlace.setForeground(AppTheme.ACENTO_PRIMARIO);
        enlace.setBorderPainted(false);
        enlace.setContentAreaFilled(false);
        enlace.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        enlace.addActionListener(e -> alIrALogin.run());

        panel.add(texto);
        panel.add(enlace);
        return panel;
    }

    private void intentarRegistro() {
        String nombre     = campoNombre.getText().trim();
        String correo     = campoCorreo.getText().trim();
        String contrasena = new String(campoContrasena.getPassword());
        String confirmar  = new String(campoConfirmar.getPassword());
        Role rol          = (Role) comboRol.getSelectedItem();

        if (nombre.isEmpty() || correo.isEmpty() || contrasena.isEmpty()) {
            etiquetaError.setText("Todos los campos son obligatorios.");
            return;
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            etiquetaError.setText("Ingresa un correo electrónico válido.");
            return;
        }
        if (contrasena.length() < 4) {
            etiquetaError.setText("La contraseña debe tener al menos 4 caracteres.");
            return;
        }
        if (!contrasena.equals(confirmar)) {
            etiquetaError.setText("Las contraseñas no coinciden.");
            return;
        }

        User usuario = authService.registrar(nombre, correo, contrasena, rol);
        if (usuario == null) {
            etiquetaError.setText("Este correo ya está registrado.");
            return;
        }

        limpiarFormulario();
        JOptionPane.showMessageDialog(this,
            "Cuenta creada exitosamente.\nYa puedes iniciar sesión.",
            "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
        alRegistrarse.run();
    }

    private void limpiarFormulario() {
        campoNombre.setText("");
        campoCorreo.setText("");
        campoContrasena.setText("");
        campoConfirmar.setText("");
        etiquetaError.setText(" ");
    }
}
