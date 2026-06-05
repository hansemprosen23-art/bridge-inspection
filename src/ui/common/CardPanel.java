package ui.common;

import javax.swing.*;
import java.awt.*;

/**
 * 带阴影和圆角的卡片面板
 */
public class CardPanel extends JPanel {
    
    private int radius = 12;
    private int shadowSize = 4;
    
    public CardPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
        setBackground(ThemeColors.CARD_BG);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }
    
    public CardPanel() {
        this(new BorderLayout());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制阴影
        g2.setColor(new Color(0, 0, 0, 20));
        g2.fillRoundRect(shadowSize, shadowSize, getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, radius, radius);
        
        // 绘制背景
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth() - shadowSize * 2, getHeight() - shadowSize * 2, radius, radius);
        
        g2.dispose();
    }
}
