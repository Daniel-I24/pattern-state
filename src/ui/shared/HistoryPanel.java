package ui.shared;

import model.Project;
import model.StateChange;
import model.Task;
import ui.AppTheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel reutilizable que muestra el historial completo de cambios
 * de estado de un proyecto y todas sus tareas.
 */
public class HistoryPanel extends JPanel {

    private final JTable             tablaHistorial;
    private final DefaultTableModel  modeloTabla;

    public HistoryPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(AppTheme.FONDO_PRINCIPAL);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("Historial de cambios");
        titulo.setFont(AppTheme.FUENTE_SUBTITULO);
        titulo.setForeground(AppTheme.ACENTO_PRIMARIO);
        add(titulo, BorderLayout.NORTH);

        String[] columnas = {"Fecha", "Origen", "Estado anterior", "Estado nuevo", "Responsable", "Observación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setFont(AppTheme.FUENTE_NORMAL);
        tablaHistorial.setRowHeight(32);
        tablaHistorial.setBackground(AppTheme.FONDO_PANEL);
        tablaHistorial.setGridColor(AppTheme.BORDE_SUAVE);
        tablaHistorial.setSelectionBackground(AppTheme.VERDE_CLARO);
        tablaHistorial.getTableHeader().setBackground(AppTheme.ACENTO_PRIMARIO);
        tablaHistorial.getTableHeader().setForeground(AppTheme.TEXTO_CLARO);
        tablaHistorial.getTableHeader().setFont(AppTheme.FUENTE_BOTON);
        tablaHistorial.setShowGrid(true);

        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 5; i++) {
            tablaHistorial.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }

        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(100);
        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(140);
        tablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(140);
        tablaHistorial.getColumnModel().getColumn(4).setPreferredWidth(120);
        tablaHistorial.getColumnModel().getColumn(5).setPreferredWidth(280);

        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.setBackground(AppTheme.FONDO_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDE_SUAVE, 1, true));
        add(scroll, BorderLayout.CENTER);
    }

    /** Carga el historial completo del proyecto y todas sus tareas. */
    public void cargarHistorial(Project proyecto) {
        modeloTabla.setRowCount(0);

        // Historial del proyecto
        agregarFilas("Proyecto", proyecto.getHistorial());

        // Historial de cada tarea
        for (Task tarea : proyecto.getTareas()) {
            agregarFilas("Tarea: " + tarea.getTitulo(), tarea.getHistorial());
        }
    }

    private void agregarFilas(String origen, List<StateChange> cambios) {
        for (StateChange cambio : cambios) {
            modeloTabla.addRow(new Object[]{
                cambio.getFechaFormateada(),
                origen,
                cambio.getEstadoAnterior(),
                cambio.getEstadoNuevo(),
                cambio.getResponsable(),
                cambio.getObservacion()
            });
        }
    }
}
