package ui.common;

import javax.swing.*;
import java.awt.*;

/**
 * 加载遮罩组件
 * 显示在半透明背景上的 Loading 动画，避免用户重复操作并提示加载中
 */
public class LoadingOverlay extends JPanel {

    private final JLabel label;
    private final Timer timer;
    private int dotCount = 0;

    public LoadingOverlay() {
        setOpaque(false);
        setVisible(false);
        setLayout(new GridBagLayout());

        // 半透明背景
        JPanel glass = new JPanel(new GridBagLayout());
        glass.setBackground(new Color(255, 255, 255, 180));
        glass.setOpaque(true);

        label = new JLabel("加载中", SwingConstants.CENTER);
        label.setFont(new Font("微软雅黑", Font.BOLD, 16));
        label.setForeground(ThemeColors.PRIMARY);
        label.setIconTextGap(10);

        glass.add(label);
        add(glass, new GridBagConstraints());

        // 动态省略号动画
        timer = new Timer(300, e -> {
            dotCount = (dotCount + 1) % 4;
            String dots = "";
            for (int i = 0; i < dotCount; i++) dots += ".";
            label.setText("加载中" + dots);
        });
    }

    public void showOverlay() {
        setVisible(true);
        timer.start();
        SwingUtilities.invokeLater(() -> {
            Container parent = getParent();
            if (parent != null) {
                setBounds(0, 0, parent.getWidth(), parent.getHeight());
                parent.revalidate();
                parent.repaint();
            }
        });
    }

    public void hideOverlay() {
        timer.stop();
        setVisible(false);
        SwingUtilities.invokeLater(() -> {
            Container parent = getParent();
            if (parent != null) {
                parent.revalidate();
                parent.repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 确保遮罩覆盖整个父容器
        Container parent = getParent();
        if (parent != null && (getWidth() != parent.getWidth() || getHeight() != parent.getHeight())) {
            setBounds(0, 0, parent.getWidth(), parent.getHeight());
        }
    }
}
