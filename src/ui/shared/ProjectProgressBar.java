package ui.shared;

import ui.AppTheme;

import javax.swing.*;
import java.awt.*;

/**
 * Componente reutilizable que muestra el progreso de un proyecto
 * como porcentaje de tareas completadas.
 */
public class ProjectProgressBar extends JPanel {

    private final JProgressBar barra;
    private final JLabel       lblPorcentaje;

    public ProjectProgressBar() {
        setLayout(new BorderLayout(8, 0));
        setOpaque(false);
        setPreferredSize(new Dimension(180, 40));

        this.barra         = new JProgressBar(0, 100);
        this.lblPorcentaje = new JLabel("0%");

        barra.setStringPainted(false);
        barra.setPreferredSize(new Dimension(120, 20));
        barra.setBackground(AppTheme.FONDO_PANEL);
        barra.setForeground(AppTheme.VERDE_EXITO);
        barra.setBorder(BorderFactory.createLineBorder(AppTheme.BORDE_SUAVE, 1, true));

        lblPorcentaje.setFont(AppTheme.FUENTE_BOTON);
        lblPorcentaje.setForeground(AppTheme.ACENTO_PRIMARIO);
        lblPorcentaje.setHorizontalAlignment(SwingConstants.CENTER);
        lblPorcentaje.setPreferredSize(new Dimension(50, 20));

        add(barra,         BorderLayout.CENTER);
        add(lblPorcentaje, BorderLayout.EAST);
    }

    public void actualizar(int porcentaje) {
        barra.setValue(porcentaje);
        lblPorcentaje.setText(porcentaje + "%");
        
        // Cambiar color según progreso
        if (porcentaje == 100) {
            barra.setForeground(AppTheme.VERDE_EXITO);
        } else if (porcentaje >= 50) {
            barra.setForeground(new Color(0x8B7355)); // café claro
        } else {
            barra.setForeground(AppTheme.AMARILLO_AVISO);
        }
    }
}
