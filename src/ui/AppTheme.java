package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Define la paleta de colores, fuentes y estilos visuales del sistema.
 * Centralizar el tema evita valores mágicos dispersos en la UI.
 */
public final class AppTheme {

    // Paleta de colores
    public static final Color FONDO_PRINCIPAL   = new Color(0xF5F0E8); // crema
    public static final Color FONDO_PANEL       = new Color(0xFFFDF7); // crema claro
    public static final Color FONDO_SIDEBAR     = new Color(0x3D2B1F); // café oscuro
    public static final Color ACENTO_PRIMARIO   = new Color(0x6B4C35); // café medio
    public static final Color ACENTO_SECUNDARIO = new Color(0x8B7355); // café claro
    public static final Color VERDE_EXITO       = new Color(0x4A7C59); // verde bosque
    public static final Color VERDE_CLARO       = new Color(0xD4E6D4); // verde suave
    public static final Color ROJO_ERROR        = new Color(0xA0522D); // sienna
    public static final Color AMARILLO_AVISO    = new Color(0xC8A96E); // beige dorado
    public static final Color TEXTO_PRINCIPAL   = new Color(0x2C1810); // café muy oscuro
    public static final Color TEXTO_SECUNDARIO  = new Color(0x6B5744); // café medio
    public static final Color TEXTO_CLARO       = new Color(0xFFFDF7); // crema
    public static final Color BORDE_SUAVE       = new Color(0xD4C4B0); // beige

    // Fuentes
    public static final Font FUENTE_TITULO    = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FUENTE_NORMAL    = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FUENTE_PEQUEÑA   = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FUENTE_BOTON     = new Font("Segoe UI", Font.BOLD, 12);

    private AppTheme() {}

    /** Aplica el look and feel base al sistema. */
    public static void aplicar() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("Panel.background",      FONDO_PRINCIPAL);
        UIManager.put("Label.foreground",      TEXTO_PRINCIPAL);
        UIManager.put("Label.font",            FUENTE_NORMAL);
        UIManager.put("TextField.font",        FUENTE_NORMAL);
        UIManager.put("TextArea.font",         FUENTE_NORMAL);
        UIManager.put("ComboBox.font",         FUENTE_NORMAL);
        UIManager.put("Table.font",            FUENTE_NORMAL);
        UIManager.put("TableHeader.font",      FUENTE_SUBTITULO);
        UIManager.put("List.font",             FUENTE_NORMAL);
        UIManager.put("OptionPane.messageFont",FUENTE_NORMAL);
    }

    /** Crea un botón con el estilo primario del sistema. */
    public static JButton botonPrimario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(FUENTE_BOTON);
        boton.setBackground(ACENTO_PRIMARIO);
        boton.setForeground(TEXTO_CLARO);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(new EmptyBorder(10, 20, 10, 20));
        boton.setOpaque(true);
        return boton;
    }

    /** Crea un botón con el estilo secundario (verde). */
    public static JButton botonSecundario(String texto) {
        JButton boton = botonPrimario(texto);
        boton.setBackground(VERDE_EXITO);
        return boton;
    }

    /** Crea un botón de peligro (rojo/sienna). */
    public static JButton botonPeligro(String texto) {
        JButton boton = botonPrimario(texto);
        boton.setBackground(ROJO_ERROR);
        return boton;
    }

    /** Crea un botón de acción neutral (café claro). */
    public static JButton botonNeutral(String texto) {
        JButton boton = botonPrimario(texto);
        boton.setBackground(ACENTO_SECUNDARIO);
        return boton;
    }

    /** Crea un campo de texto con el estilo del sistema. */
    public static JTextField campoTexto(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(FUENTE_NORMAL);
        campo.setBackground(FONDO_PANEL);
        campo.setForeground(TEXTO_PRINCIPAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE_SUAVE, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return campo;
    }

    /** Crea un campo de contraseña con el estilo del sistema. */
    public static JPasswordField campoContrasena(int columnas) {
        JPasswordField campo = new JPasswordField(columnas);
        campo.setFont(FUENTE_NORMAL);
        campo.setBackground(FONDO_PANEL);
        campo.setForeground(TEXTO_PRINCIPAL);
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE_SUAVE, 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));
        return campo;
    }

    /** Crea un área de texto con el estilo del sistema. */
    public static JTextArea areaTexto(int filas, int columnas) {
        JTextArea area = new JTextArea(filas, columnas);
        area.setFont(FUENTE_NORMAL);
        area.setBackground(FONDO_PANEL);
        area.setForeground(TEXTO_PRINCIPAL);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(new EmptyBorder(8, 10, 8, 10));
        return area;
    }

    /** Borde redondeado con color suave para paneles. */
    public static Border bordePanel() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDE_SUAVE, 1, true),
            new EmptyBorder(16, 16, 16, 16)
        );
    }

    /** Retorna el color de fondo asociado a un estado de proyecto. */
    public static Color colorEstadoProyecto(String estado) {
        return switch (estado) {
            case "Propuesto"              -> new Color(0xE8E0D5);
            case "En Evaluación"          -> new Color(0xFFF3CD);
            case "Aprobado"               -> new Color(0xD4E6D4);
            case "En Planificación"       -> new Color(0xD0E8F0);
            case "En Desarrollo"          -> new Color(0xCCE5FF);
            case "En Pruebas"             -> new Color(0xE2D9F3);
            case "En Revisión del Cliente"-> new Color(0xFFE4B5);
            case "En Correcciones"        -> new Color(0xFFD9B5);
            case "Aceptado"               -> new Color(0xC8E6C9);
            case "Desplegado"             -> new Color(0xA5D6A7);
            case "En Mantenimiento"       -> new Color(0xB2DFDB);
            case "Archivado"              -> new Color(0xEEEEEE);
            default                       -> FONDO_PANEL;
        };
    }

    /** Retorna el color de fondo asociado a un estado de tarea. */
    public static Color colorEstadoTarea(String estado) {
        return switch (estado) {
            case "Pendiente"                -> new Color(0xF5F0E8);
            case "En Progreso"              -> new Color(0xCCE5FF);
            case "En Revisión"              -> new Color(0xFFF3CD);
            case "Pendiente de Aprobación"  -> new Color(0xFFE4B5);
            case "Completada"               -> new Color(0xC8E6C9);
            case "Bloqueada"                -> new Color(0xFFCDD2);
            default                         -> FONDO_PANEL;
        };
    }

    /** Aplica un placeholder a un JTextField que desaparece al enfocar. */
    public static void aplicarPlaceholder(JTextField campo, String placeholder) {
        campo.setForeground(TEXTO_SECUNDARIO);
        campo.setText(placeholder);
        campo.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(TEXTO_PRINCIPAL);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (campo.getText().trim().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(TEXTO_SECUNDARIO);
                }
            }
        });
    }

    /** Aplica un placeholder a un JTextArea que desaparece al enfocar. */
    public static void aplicarPlaceholder(JTextArea area, String placeholder) {
        area.setForeground(TEXTO_SECUNDARIO);
        area.setText(placeholder);
        area.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusGained(java.awt.event.FocusEvent e) {
                if (area.getText().equals(placeholder)) {
                    area.setText("");
                    area.setForeground(TEXTO_PRINCIPAL);
                }
            }
            @Override public void focusLost(java.awt.event.FocusEvent e) {
                if (area.getText().trim().isEmpty()) {
                    area.setText(placeholder);
                    area.setForeground(TEXTO_SECUNDARIO);
                }
            }
        });
    }
}
