package be.tav.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class EntityTablePanel<T> extends JPanel {
    private static final long serialVersionUID = 1L;
	private JTable table;
    private DefaultTableModel model;
    public EntityTablePanel(String[] columnNames, Object[][] data, List<T> entities, Consumer<T> onDetail) {
        setLayout(new BorderLayout());
        model = new DefaultTableModel(data, columnNames) {
            private static final long serialVersionUID = 1L;

			@Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model) {
            private static final long serialVersionUID = 1L;

			@Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (column == getColumnCount() - 1) {
                    c.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else {
                    c.setCursor(Cursor.getDefaultCursor());
                }
                return c;
            }
        };
        table.setRowHeight(32);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        if (columnNames[0].equalsIgnoreCase("ID")) {
            table.removeColumn(table.getColumnModel().getColumn(0));
        }
        
        table.getColumnModel().getColumn(table.getColumnCount() - 1).setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> {
            JPanel panel = new JPanel() {
                private static final long serialVersionUID = 1L;

				@Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth();
                    int h = getHeight();
                    
                    g2.setColor(Color.DARK_GRAY);
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawArc(w/2-12, h/2-7, 24, 14, 0, 180);
                    g2.drawArc(w/2-12, h/2-7, 24, 14, 0, -180);
                    
                    g2.setColor(new Color(80, 80, 80));
                    g2.fillOval(w/2-4, h/2-4, 8, 8);
                }
            };
            panel.setOpaque(true);
            if (isSelected) {
                panel.setBackground(tbl.getSelectionBackground());
            } else if (row == hoveredRow && col == hoveredCol) {
                panel.setBackground(new Color(220, 235, 255));
            } else {
                panel.setBackground(Color.WHITE);
            }
            panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            return panel;
        });
        
        table.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == table.getColumnCount() - 1 && row >= 0 && row < entities.size()) {
                    table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    hoveredRow = row;
                    hoveredCol = col;
                } else {
                    table.setCursor(Cursor.getDefaultCursor());
                    hoveredRow = -1;
                    hoveredCol = -1;
                }
                table.repaint();
            }
        });
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hoveredRow = -1;
                hoveredCol = -1;
                table.setCursor(Cursor.getDefaultCursor());
                table.repaint();
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (col == table.getColumnCount() - 1 && row >= 0 && row < entities.size()) {
                    if (onDetail != null) {
                        onDetail.accept(entities.get(row));
                    }
                }
            }
        });
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
    private int hoveredRow = -1;
    private int hoveredCol = -1;
}