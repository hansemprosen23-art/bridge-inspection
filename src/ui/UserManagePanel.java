package ui;

import entity.User;
import service.UserService;
import ui.common.*;
import util.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理面板
 * 负责模块: 曹城钧
 */
public class UserManagePanel extends JPanel implements RefreshablePanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private RoundedButton addBtn, editBtn, deleteBtn, resetPwdBtn;
    private OutlineButton refreshBtn;

    private JTextField usernameField, realNameField, phoneField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;

    private List<User> currentList = new ArrayList<>();
    private int selectedId = -1;
    private boolean dataLoaded = false;
    private LoadingOverlay loadingOverlay;

    public UserManagePanel() {
        setLayout(new BorderLayout(12, 12));
        setBackground(ThemeColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        initTable();
        initButtonBar();
        initFormPanel();

        loadingOverlay = new LoadingOverlay();
        add(loadingOverlay, 0);
    }

    private void initTable() {
        String[] columns = {"ID", "用户名", "真实姓名", "角色", "电话", "创建时间"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new StyledTable();
        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row >= 0 && currentList != null && row < currentList.size()) {
                    User u = currentList.get(row);
                    selectedId = u.getId();
                    fillForm(u);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(0, 300));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.NORTH);
    }

    private void initButtonBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setOpaque(false);

        addBtn = new RoundedButton("新增用户", ThemeColors.SUCCESS);
        editBtn = new RoundedButton("修改信息", ThemeColors.INFO);
        deleteBtn = new RoundedButton("删除用户", ThemeColors.DANGER);
        resetPwdBtn = new RoundedButton("重置密码", ThemeColors.WARNING);
        refreshBtn = new OutlineButton("刷新", ThemeColors.TEXT_SECONDARY);

        panel.add(addBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(resetPwdBtn);
        panel.add(refreshBtn);

        addBtn.addActionListener(e -> doAdd());
        editBtn.addActionListener(e -> doEdit());
        deleteBtn.addActionListener(e -> doDelete());
        resetPwdBtn.addActionListener(e -> doResetPassword());
        refreshBtn.addActionListener(e -> {
            setBusy(true);
            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    loadData();
                    return null;
                }
                @Override
                protected void done() {
                    clearForm();
                    setBusy(false);
                }
            }.execute();
        });

        add(panel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        lbl.setForeground(ThemeColors.TEXT_SECONDARY);
        return lbl;
    }

    private void initFormPanel() {
        CardPanel card = new CardPanel(new GridBagLayout());
        card.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "用户信息",
                javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.TOP,
                new Font("微软雅黑", Font.BOLD, 15), ThemeColors.WARNING));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 12, 10, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 第1行
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("用户名*"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        usernameField = new JTextField(20);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.add(usernameField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("密码*"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.add(passwordField, gbc);

        // 第2行
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("真实姓名"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        realNameField = new JTextField(20);
        realNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.add(realNameField, gbc);

        gbc.gridx = 2;
        gbc.weightx = 0;
        card.add(createLabel("角色"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        roleBox = new JComboBox<>(new String[]{"inspector", "admin"});
        roleBox.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        card.add(roleBox, gbc);

        // 第3行
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0;
        card.add(createLabel("电话"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        phoneField = new JTextField(20);
        phoneField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeColors.BORDER),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        card.add(phoneField, gbc);

        add(card, BorderLayout.SOUTH);
    }

    @Override
    public void refreshDataIfVisible() {
        if (dataLoaded) {
            SwingUtilities.invokeLater(() -> loadData());
            return;
        }
        dataLoaded = true;
        loadData();
    }

    @Override
    public void setBusy(boolean busy) {
        if (busy) {
            loadingOverlay.showOverlay();
        } else {
            loadingOverlay.hideOverlay();
        }
    }

    private void loadData() {
        currentList = UserService.getInstance().getAllUsers();
        refreshTable();
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        if (currentList == null || currentList.isEmpty()) return;
        for (User u : currentList) {
            tableModel.addRow(new Object[]{
                    u.getId(), u.getUsername(), u.getRealName(),
                    u.isAdmin() ? "管理员" : "检查员", u.getPhone(), u.getCreateTime()
            });
        }
    }

    private void doAdd() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名和密码不能为空！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setRealName(realNameField.getText().trim());
        user.setRole((String) roleBox.getSelectedItem());
        user.setPhone(phoneField.getText().trim());

        if (UserService.getInstance().addUser(user)) {
            JOptionPane.showMessageDialog(this, "添加成功！密码已自动加密存储。");
            Logger.info("管理员添加用户: " + username);
            refreshBtn.doClick();
        } else {
            JOptionPane.showMessageDialog(this, "添加失败，用户名可能已存在！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doEdit() {
        if (selectedId <= 0) {
            JOptionPane.showMessageDialog(this, "请先选择要修改的用户！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = new User();
        user.setId(selectedId);
        user.setRealName(realNameField.getText().trim());
        user.setRole((String) roleBox.getSelectedItem());
        user.setPhone(phoneField.getText().trim());

        if (UserService.getInstance().updateUser(user)) {
            JOptionPane.showMessageDialog(this, "修改成功！");
            refreshBtn.doClick();
        } else {
            JOptionPane.showMessageDialog(this, "修改失败！", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doDelete() {
        if (selectedId <= 0) {
            JOptionPane.showMessageDialog(this, "请先选择要删除的用户！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "确定要删除该用户吗？", "确认删除", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            if (UserService.getInstance().deleteUser(selectedId)) {
                JOptionPane.showMessageDialog(this, "删除成功！");
                refreshBtn.doClick();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void doResetPassword() {
        if (selectedId <= 0) {
            JOptionPane.showMessageDialog(this, "请先选择要重置密码的用户！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String newPwd = JOptionPane.showInputDialog(this, "请输入新密码:");
        if (newPwd != null && !newPwd.trim().isEmpty()) {
            if (UserService.getInstance().resetPassword(selectedId, newPwd.trim())) {
                JOptionPane.showMessageDialog(this, "密码重置成功！新密码已加密存储。");
                Logger.info("管理员重置用户密码 ID=" + selectedId);
            } else {
                JOptionPane.showMessageDialog(this, "密码重置失败！", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void fillForm(User u) {
        usernameField.setText(u.getUsername());
        usernameField.setEditable(false);
        passwordField.setText("");
        passwordField.setEnabled(false);
        realNameField.setText(u.getRealName());
        roleBox.setSelectedItem(u.getRole());
        phoneField.setText(u.getPhone());
    }

    private void clearForm() {
        selectedId = -1;
        usernameField.setText("");
        usernameField.setEditable(true);
        passwordField.setText("");
        passwordField.setEnabled(true);
        realNameField.setText("");
        roleBox.setSelectedIndex(0);
        phoneField.setText("");
    }
}
