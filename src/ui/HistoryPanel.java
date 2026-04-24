package ui;

import model.Project;
import model.StateChange;
import model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel que muestra el historial de cambios de estado de un proyecto y sus tareas.
 */
public class HistoryPanel extends JPanel {

    private final JTable tablaHistorial;
    private final DefaultTableModel modeloTabla;

    public HistoryPanel() {
        setLayout(new BorderLayout(0, 12));
        setBackground(AppTheme.FONDO_PRINCIPAL);
        setBorder(new EmptyBorder(16, 16, 16, 16));

        JLabel titulo = new JLabel("Historial de cambios");
        titulo.setFont(AppTheme.FUENTE_SUBTITULO);
        titulo.setForeground(AppTheme.ACENTO_PRIMARIO);
        add(titulo, BorderLayout.NORTH);

        String[] columnas = {"Fecha", "Estado anterior", "Estado nuevo", "Responsable", "Observación"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        tablaHistorial = new JTable(modeloTabla);
        tablaHistorial.setFont(AppTheme.FUENTE_NORMAL);
        tablaHistorial.setRowHeight(28);
        tablaHistorial.setBackground(AppTheme.FONDO_PANEL);
        tablaHistorial.setGridColor(AppTheme.BORDE_SUAVE);
        tablaHistorial.setSelectionBackground(AppTheme.VERDE_CLARO);
        tablaHistorial.getTableHeader().setBackground(AppTheme.ACENTO_PRIMARIO);
        tablaHistorial.getTableHeader().setForeground(AppTheme.TEXTO_CLARO);
        tablaHistorial.getTableHeader().setFont(AppTheme.FUENTE_BOTON);
        tablaHistorial.setShowGrid(true);

        // Alineación centrada para las primeras columnas
        DefaultTableCellRenderer centrado = new DefaultTableCellRenderer();
        centrado.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 4; i++) {
            tablaHistorial.getColumnModel().getColumn(i).setCellRenderer(centrado);
        }

        tablaHistorial.getColumnModel().getColumn(0).setPreferredWidth(130);
        tablaHistorial.getColumnModel().getColumn(1).setPreferredWidth(160);
        tablaHistorial.getColumnModel().getColumn(2).setPreferredWidth(160);
        tablaHistorial.getColumnModel().getColumn(3).setPreferredWidth(140);
        tablaHistorial.getColumnModel().getColumn(4).setPreferredWidth(280);

        JScrollPane scroll = new JScrollPane(tablaHistorial);
        scroll.setBackground(AppTheme.FONDO_PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(AppTheme.BORDE_SUAVE, 1, true));
        add(scroll, BorderLayout.CENTER);
    }

    /** Carga el historial del proyecto y de todas sus tareas. */
    public void cargarHistorial(Project proyecto) {
        modeloTabla.setRowCount(0);

        agregarFilas("Proyecto: " + proyecto.getNombre(), proyecto.getHistorial());

        for (Task tarea : proyecto.getTareas()) {
            agregarFilas("Tarea: " + tarea.getTitulo(), tarea.getHistorial());
        }
    }

    private void agregarFilas(String fuente, List<StateChange> cambios) {
        for (StateChange cambio : cambios) {
            modeloTabla.addRow(new Object[]{
                cambio.getFechaFormateada(),
                cambio.getEstadoAnterior(),
                cambio.getEstadoNuevo(),
                cambio.getResponsable(),
                cambio.getObservacion()
            });
        }
    }
}
