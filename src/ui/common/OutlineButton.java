package ui.common;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * 圆角描边按钮（用于次要操作）
 */
public class OutlineButton extends JButton {
    
    private Color borderColor;
    private Color textColor;
    private int radius = 8;
    
    public OutlineButton(String text) {
        this(text, ThemeColors.PRIMARY);
    }
    
    public OutlineButton(String text, Color color) {
        super(text);
        this.borderColor = color;
        this.textColor = color;
        setup();
    }
    
    private void setup() {
        setOpaque(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(textColor);
        setFont(new Font("微软雅黑", Font.PLAIN, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(90, 34));
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                setForeground(Color.WHITE);
                repaint();
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                setForeground(textColor);
                repaint();
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius, radius);
        
        if (getModel().isRollover()) {
            g2.setColor(borderColor);
            g2.fill(rect);
        } else {
            g2.setColor(Color.WHITE);
            g2.fill(rect);
            g2.setColor(borderColor);
            g2.draw(rect);
        }
        
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
