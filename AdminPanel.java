package gui;

import entity.MenuItem;
import entity.MenuItemDAO;
import entity.Order;
import entity.OrderDAO;
import entity.OrderItem;
import entity.OrderItemDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Admin Panel for Full CRUD Operations on Menu Items and Orders
 */
public class AdminPanel extends JFrame {

    private MenuItemDAO menuItemDAO;
    private OrderDAO orderDAO;
    private OrderItemDAO orderItemDAO;

    // Menu Item components
    private JTable menuItemTable;
    private DefaultTableModel menuItemTableModel;
    private JTextField menuIdField, menuNameField, menuPriceField, menuCategoryField;

    // Order components
    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private JTable orderDetailsTable;
    private DefaultTableModel orderDetailsTableModel;

    public AdminPanel() {
        menuItemDAO = new MenuItemDAO();
        orderDAO = new OrderDAO();
        orderItemDAO = new OrderItemDAO();

        setTitle("Oak Donuts - Admin Panel");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Menu Items", createMenuItemPanel());
        tabbedPane.addTab("Orders", createOrderPanel());

        add(tabbedPane);

        loadMenuItems();
        loadOrders();
    }

    private JPanel createMenuItemPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Menu Item Details"));
        formPanel.setPreferredSize(new Dimension(350, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // ID (read-only for updates)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        menuIdField = new JTextField(20);
        menuIdField.setEditable(false);
        formPanel.add(menuIdField, gbc);

        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        menuNameField = new JTextField(20);
        formPanel.add(menuNameField, gbc);

        // Price
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Price:"), gbc);
        gbc.gridx = 1;
        menuPriceField = new JTextField(20);
        formPanel.add(menuPriceField, gbc);

        // Category
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Category:"), gbc);
        gbc.gridx = 1;
        menuCategoryField = new JTextField(20);
        formPanel.add(menuCategoryField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton insertBtn = new JButton("Insert");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");

        insertBtn.addActionListener(e -> insertMenuItem());
        updateBtn.addActionListener(e -> updateMenuItem());
        deleteBtn.addActionListener(e -> deleteMenuItem());
        clearBtn.addActionListener(e -> clearMenuItemForm());

        buttonPanel.add(insertBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(clearBtn);

        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        panel.add(formPanel, BorderLayout.WEST);

        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Menu Items"));

        String[] columnNames = {"ID", "Name", "Price", "Category"};
        menuItemTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        menuItemTable = new JTable(menuItemTableModel);
        menuItemTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && menuItemTable.getSelectedRow() != -1) {
                loadMenuItemToForm();
            }
        });

        JScrollPane scrollPane = new JScrollPane(menuItemTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> loadMenuItems());
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(refreshBtn);
        tablePanel.add(bottomPanel, BorderLayout.SOUTH);

        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Top: Orders table
        JPanel ordersPanel = new JPanel(new BorderLayout());
        ordersPanel.setBorder(BorderFactory.createTitledBorder("Orders"));

        String[] orderColumns = {"ID", "Date", "Subtotal", "Tax", "Total"};
        orderTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderTable = new JTable(orderTableModel);
        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && orderTable.getSelectedRow() != -1) {
                loadOrderDetails();
            }
        });

        JScrollPane orderScrollPane = new JScrollPane(orderTable);
        ordersPanel.add(orderScrollPane, BorderLayout.CENTER);

        JPanel orderButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteOrderBtn = new JButton("Delete Selected Order");
        JButton refreshOrderBtn = new JButton("Refresh");

        deleteOrderBtn.addActionListener(e -> deleteOrder());
        refreshOrderBtn.addActionListener(e -> loadOrders());

        orderButtonPanel.add(deleteOrderBtn);
        orderButtonPanel.add(refreshOrderBtn);
        ordersPanel.add(orderButtonPanel, BorderLayout.SOUTH);

        splitPane.setTopComponent(ordersPanel);

        // Bottom: Order details table
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Order Details"));

        String[] detailColumns = {"Item ID", "Item Name", "Quantity", "Icing", "Filling", "Price", "Total"};
        orderDetailsTableModel = new DefaultTableModel(detailColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        orderDetailsTable = new JTable(orderDetailsTableModel);

        JScrollPane detailScrollPane = new JScrollPane(orderDetailsTable);
        detailsPanel.add(detailScrollPane, BorderLayout.CENTER);

        splitPane.setBottomComponent(detailsPanel);

        panel.add(splitPane, BorderLayout.CENTER);

        return panel;
    }

    // Menu Item operations
    private void loadMenuItems() {
        menuItemTableModel.setRowCount(0);
        List<MenuItem> items = menuItemDAO.getAll();
        for (MenuItem item : items) {
            menuItemTableModel.addRow(new Object[]{
                    item.getId(),
                    item.getName(),
                    String.format("$%.2f", item.getPrice()),
                    item.getCategory()
            });
        }
    }

    private void loadMenuItemToForm() {
        int row = menuItemTable.getSelectedRow();
        menuIdField.setText(menuItemTable.getValueAt(row, 0).toString());
        menuNameField.setText(menuItemTable.getValueAt(row, 1).toString());
        String price = menuItemTable.getValueAt(row, 2).toString().replace("$", "");
        menuPriceField.setText(price);
        menuCategoryField.setText(menuItemTable.getValueAt(row, 3).toString());
    }

    private void insertMenuItem() {
        try {
            String name = menuNameField.getText().trim();
            double price = Double.parseDouble(menuPriceField.getText().trim());
            String category = menuCategoryField.getText().trim();

            if (name.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MenuItem item = new MenuItem(0, name, price, category);
            menuItemDAO.insert(item);
            loadMenuItems();
            clearMenuItemForm();
            JOptionPane.showMessageDialog(this, "Menu item inserted successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid price format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateMenuItem() {
        try {
            if (menuIdField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a menu item to update!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int id = Integer.parseInt(menuIdField.getText());
            String name = menuNameField.getText().trim();
            double price = Double.parseDouble(menuPriceField.getText().trim());
            String category = menuCategoryField.getText().trim();

            if (name.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MenuItem item = new MenuItem(id, name, price, category);
            menuItemDAO.update(item);
            loadMenuItems();
            clearMenuItemForm();
            JOptionPane.showMessageDialog(this, "Menu item updated successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid input format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMenuItem() {
        try {
            if (menuIdField.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select a menu item to delete!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete this menu item?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                int id = Integer.parseInt(menuIdField.getText());
                String name = menuNameField.getText();
                double price = Double.parseDouble(menuPriceField.getText());
                String category = menuCategoryField.getText();

                MenuItem item = new MenuItem(id, name, price, category);
                menuItemDAO.delete(item);
                loadMenuItems();
                clearMenuItemForm();
                JOptionPane.showMessageDialog(this, "Menu item deleted successfully!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error deleting menu item!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearMenuItemForm() {
        menuIdField.setText("");
        menuNameField.setText("");
        menuPriceField.setText("");
        menuCategoryField.setText("");
    }

    // Order operations
    private void loadOrders() {
        orderTableModel.setRowCount(0);
        List<Order> orders = orderDAO.getAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (Order order : orders) {
            orderTableModel.addRow(new Object[]{
                    order.getId(),
                    order.getOrderDate().format(formatter),
                    String.format("$%.2f", order.getSubtotal()),
                    String.format("$%.2f", order.getTax()),
                    String.format("$%.2f", order.getTotal())
            });
        }
    }

    private void loadOrderDetails() {
        orderDetailsTableModel.setRowCount(0);
        int row = orderTable.getSelectedRow();
        if (row == -1) return;

        int orderId = Integer.parseInt(orderTable.getValueAt(row, 0).toString());
        List<OrderItem> items = orderItemDAO.getByOrderId(orderId);

        for (OrderItem item : items) {
            orderDetailsTableModel.addRow(new Object[]{
                    item.getMenuItemId(),
                    item.getItemName(),
                    item.getQuantity(),
                    item.getIcing() != null ? item.getIcing() : "-",
                    item.getFilling() != null ? item.getFilling() : "-",
                    String.format("$%.2f", item.getPrice()),
                    String.format("$%.2f", item.getTotal())
            });
        }
    }

    private void deleteOrder() {
        int row = orderTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select an order to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this order and all its items?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int orderId = Integer.parseInt(orderTable.getValueAt(row, 0).toString());
            Order order = orderDAO.get(orderId).orElse(null);

            if (order != null) {
                orderDAO.delete(order);
                loadOrders();
                orderDetailsTableModel.setRowCount(0);
                JOptionPane.showMessageDialog(this, "Order deleted successfully!");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            AdminPanel panel = new AdminPanel();
            panel.setVisible(true);
        });
    }
}