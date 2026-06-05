package ui.common;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * 美化表格
 */
public class StyledTable extends JTable {
    
    public StyledTable() {
        setup();
    }
    
    private void setup() {
        setRowHeight(36);
        setFont(new Font("微软雅黑", Font.PLAIN, 13));
        setGridColor(new Color(240, 240, 240));
        setShowGrid(false);
        setShowHorizontalLines(true);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionBackground(ThemeColors.TABLE_SELECTION);
        setSelectionForeground(ThemeColors.TEXT_PRIMARY);
        
        // 表头样式
        JTableHeader header = getTableHeader();
        header.setFont(new Font("微软雅黑", Font.BOLD, 13));
        header.setBackground(ThemeColors.TABLE_HEADER_BG);
        header.setForeground(ThemeColors.TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(0, 40));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, ThemeColors.PRIMARY));
        
        // 单元格居中
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        setDefaultRenderer(Object.class, centerRenderer);
    }
    
    @Override
    public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (!isRowSelected(row)) {
            c.setBackground(row % 2 == 0 ? Color.WHITE : ThemeColors.TABLE_ROW_ALT);
        }
        return c;
    }
}
