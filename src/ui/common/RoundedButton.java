package ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 圆角渐变按钮
 */
public class RoundedButton extends JButton {
    
    private Color bgColor;
    private Color hoverColor;
    private int radius = 8;
    
    public RoundedButton(String text) {
        this(text, ThemeColors.PRIMARY);
    }
    
    public RoundedButton(String text, Color bgColor) {
        super(text);
        this.bgColor = bgColor;
        this.hoverColor = bgColor.brighter();
        setup();
    }
    
    private void setup() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("微软雅黑", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(90, 34));
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                bgColor = hoverColor;
                repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                bgColor = bgColor.darker();
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 绘制圆角背景
        RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
        g2.setColor(bgColor);
        g2.fill(rect);
        
        // 绘制文字
        g2.setColor(getForeground());
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(getText());
        int textHeight = fm.getAscent();
        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2 - 3;
        g2.drawString(getText(), x, y);
        
        g2.dispose();
    }
}
