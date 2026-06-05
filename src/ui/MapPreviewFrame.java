package ui;

import ui.common.CardPanel;
import ui.common.RoundedButton;
import ui.common.ThemeColors;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

/**
 * 地图预览窗口
 * 使用静态地图 API 显示桥梁位置
 * 支持高德地图静态图 API
 */
public class MapPreviewFrame extends JFrame {

    private String longitude;
    private String latitude;
    private String bridgeName;

    public MapPreviewFrame(String longitude, String latitude, String bridgeName) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.bridgeName = bridgeName;

        setTitle("桥梁地图定位 - " + bridgeName);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());

        // 地图显示区域
        JLabel mapLabel = new JLabel("正在加载地图...", SwingConstants.CENTER);
        mapLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        mapLabel.setBackground(Color.WHITE);
        mapLabel.setOpaque(true);

        // 尝试加载地图图片
        loadMapImage(mapLabel);

        JScrollPane scrollPane = new JScrollPane(mapLabel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        // 底部信息栏
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bottomPanel.setBackground(ThemeColors.BACKGROUND);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, ThemeColors.BORDER));

        JLabel infoLabel = new JLabel(String.format("桥梁: %s  |  经度: %s  |  纬度: %s", bridgeName, longitude, latitude));
        infoLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        infoLabel.setForeground(ThemeColors.TEXT_PRIMARY);

        RoundedButton openBrowserBtn = new RoundedButton("在浏览器中打开", ThemeColors.INFO);
        openBrowserBtn.addActionListener(e -> openInBrowser());

        bottomPanel.add(infoLabel);
        bottomPanel.add(Box.createHorizontalGlue());
        bottomPanel.add(openBrowserBtn);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * 加载地图图片
     * 使用高德静态地图 API（无需 key 的简化版）
     * 或使用本地 HTML 方式
     */
    private void loadMapImage(JLabel mapLabel) {
        SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
            @Override
            protected ImageIcon doInBackground() {
                try {
                    // 方案1: 使用高德静态地图 API（需要申请 key）
                    // String apiKey = "YOUR_AMAP_KEY"; // 替换为实际 key
                    // String url = String.format(
                    //     "https://restapi.amap.com/v3/staticmap?location=%s,%s&zoom=14&size=750*500&markers=mid,0xFF0000,%s:%s,%s&key=%s",
                    //     longitude, latitude, bridgeName, longitude, latitude, apiKey
                    // );

                    // 方案2: 使用 OpenStreetMap 静态图（无需 key）
                    double lon = Double.parseDouble(longitude);
                    double lat = Double.parseDouble(latitude);
                    String url = String.format(
                        "https://static-maps.openstreetmap.org/?center=%f,%f&zoom=14&size=750x500&markers=%f,%f,red-pushpin",
                        lat, lon, lat, lon
                    );

                    URL mapUrl = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
                    conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);

                    try (InputStream in = conn.getInputStream()) {
                        byte[] imageData = in.readAllBytes();
                        if (imageData.length > 0) {
                            ImageIcon icon = new ImageIcon(imageData);
                            Image scaled = icon.getImage().getScaledInstance(750, 500, Image.SCALE_SMOOTH);
                            return new ImageIcon(scaled);
                        }
                    }
                } catch (Exception e) {
                    // 静态图加载失败，使用备用方案
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        mapLabel.setIcon(icon);
                        mapLabel.setText("");
                    } else {
                        mapLabel.setText("<html><center>地图加载失败<br>请使用「在浏览器中打开」查看</center></html>");
                    }
                } catch (Exception e) {
                    mapLabel.setText("<html><center>地图加载失败<br>请使用「在浏览器中打开」查看</center></html>");
                }
            }
        };
        worker.execute();
    }

    /**
     * 在浏览器中打开地图
     */
    private void openInBrowser() {
        try {
            String url = String.format(
                "https://www.openstreetmap.org/?mlat=%s&mlon=%s&zoom=16&layers=M",
                latitude, longitude
            );
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "无法打开浏览器: " + e.getMessage(),
                "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
