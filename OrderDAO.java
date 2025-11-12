package entity;

import core.DB;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAO implements DAO<Order> {

    @Override
    public Optional<Order> get(int id) {
        DB db = DB.getInstance();
        try {
            String sql = "SELECT * FROM Orders WHERE id = ?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getDouble("subtotal"),
                        rs.getDouble("tax"),
                        rs.getDouble("total")
                );
                return Optional.of(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Order> getAll() {
        DB db = DB.getInstance();
        List<Order> orders = new ArrayList<>();

        try {
            String sql = "SELECT * FROM Orders ORDER BY order_date DESC";
            ResultSet rs = db.executeQuery(sql);

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        rs.getTimestamp("order_date").toLocalDateTime(),
                        rs.getDouble("subtotal"),
                        rs.getDouble("tax"),
                        rs.getDouble("total")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all orders: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public void insert(Order order) {
        DB db = DB.getInstance();
        try {
            String sql = "INSERT INTO Orders (order_date, subtotal, tax, total) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = db.getPreparedStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setTimestamp(1, Timestamp.valueOf(order.getOrderDate()));
            stmt.setDouble(2, order.getSubtotal());
            stmt.setDouble(3, order.getTax());
            stmt.setDouble(4, order.getTotal());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getInt(1));
                }
                System.out.println("Order inserted successfully with ID: " + order.getId());
            }
        } catch (SQLException e) {
            System.err.println("Error inserting order: " + e.getMessage());
        }
    }

    @Override
    public void update(Order order) {
        DB db = DB.getInstance();
        try {
            String sql = "UPDATE Orders SET order_date=?, subtotal=?, tax=?, total=? WHERE id=?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setTimestamp(1, Timestamp.valueOf(order.getOrderDate()));
            stmt.setDouble(2, order.getSubtotal());
            stmt.setDouble(3, order.getTax());
            stmt.setDouble(4, order.getTotal());
            stmt.setInt(5, order.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Order updated successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error updating order: " + e.getMessage());
        }
    }

    @Override
    public void delete(Order order) {
        DB db = DB.getInstance();
        try {
            // First delete all order items
            String sqlItems = "DELETE FROM OrderItem WHERE order_id = ?";
            PreparedStatement stmtItems = db.getPreparedStatement(sqlItems);
            stmtItems.setInt(1, order.getId());
            stmtItems.executeUpdate();

            // Then delete the order
            String sql = "DELETE FROM Orders WHERE id = ?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, order.getId());

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Order deleted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting order: " + e.getMessage());
        }
    }

    public int getNextOrderId() {
        DB db = DB.getInstance();
        try {
            String sql = "SELECT MAX(id) as max_id FROM Orders";
            ResultSet rs = db.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt("max_id") + 1;
            }
        } catch (SQLException e) {
            System.err.println("Error getting next order ID: " + e.getMessage());
        }
        return 1;
    }
}