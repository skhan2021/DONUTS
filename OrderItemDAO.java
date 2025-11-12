package entity;

import core.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderItemDAO implements DAO<OrderItem> {

    @Override
    public Optional<OrderItem> get(int id) {
        DB db = DB.getInstance();
        try {
            String sql = "SELECT * FROM OrderItem WHERE id = ?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                OrderItem item = new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("menu_item_id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getString("icing"),
                        rs.getString("filling"),
                        rs.getDouble("price"),
                        rs.getDouble("total")
                );
                return Optional.of(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order item: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<OrderItem> getAll() {
        DB db = DB.getInstance();
        List<OrderItem> items = new ArrayList<>();

        try {
            String sql = "SELECT * FROM OrderItem";
            ResultSet rs = db.executeQuery(sql);

            while (rs.next()) {
                OrderItem item = new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("menu_item_id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getString("icing"),
                        rs.getString("filling"),
                        rs.getDouble("price"),
                        rs.getDouble("total")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all order items: " + e.getMessage());
        }
        return items;
    }

    public List<OrderItem> getByOrderId(int orderId) {
        DB db = DB.getInstance();
        List<OrderItem> items = new ArrayList<>();

        try {
            String sql = "SELECT * FROM OrderItem WHERE order_id = ?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderItem item = new OrderItem(
                        rs.getInt("id"),
                        rs.getInt("order_id"),
                        rs.getInt("menu_item_id"),
                        rs.getString("item_name"),
                        rs.getInt("quantity"),
                        rs.getString("icing"),
                        rs.getString("filling"),
                        rs.getDouble("price"),
                        rs.getDouble("total")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order items by order ID: " + e.getMessage());
        }
        return items;
    }

    @Override
    public void insert(OrderItem item) {
        DB db = DB.getInstance();
        try {
            String sql = "INSERT INTO OrderItem (order_id, menu_item_id, item_name, quantity, icing, filling, price, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, item.getOrderId());
            stmt.setInt(2, item.getMenuItemId());
            stmt.setString(3, item.getItemName());
            stmt.setInt(4, item.getQuantity());
            stmt.setString(5, item.getIcing());
            stmt.setString(6, item.getFilling());
            stmt.setDouble(7, item.getPrice());
            stmt.setDouble(8, item.getTotal());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Order item inserted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting order item: " + e.getMessage());
        }
    }

    @Override
    public void update(OrderItem item) {
        DB db = DB.getInstance();
        try {
            String sql = "UPDATE OrderItem SET order_id=?, menu_item_id=?, item_name=?, quantity=?, icing=?, filling=?, price=?, total=? WHERE id=?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, item.getOrderId());
            stmt.setInt(2, item.getMenuItemId());
            stmt.setString(3, item.getItemName());
            stmt.setInt(4, item.getQuantity());
            stmt.setString(5, item.getIcing());
            stmt.setString(6, item.getFilling());
            stmt.setDouble(7, item.getPrice());
            stmt.setDouble(8, item.getTotal());
            stmt.setInt(9, item.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Order item updated successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error updating order item: " + e.getMessage());
        }
    }

    @Override
    public void delete(OrderItem item) {
        DB db = DB.getInstance();
        try {
            String sql = "DELETE FROM OrderItem WHERE id = ?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, item.getId());

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Order item deleted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting order item: " + e.getMessage());
        }
    }

    public void deleteByOrderId(int orderId) {
        DB db = DB.getInstance();
        try {
            String sql = "DELETE FROM OrderItem WHERE order_id = ?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting order items: " + e.getMessage());
        }
    }
}