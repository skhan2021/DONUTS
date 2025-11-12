package entity;

import core.DB;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MenuItemDAO implements DAO<MenuItem> {

    @Override
    public Optional<MenuItem> get(int id) {
        DB db = DB.getInstance();
        try {
            String sql = "SELECT * FROM MenuItem WHERE id = ?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                MenuItem item = new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("category")
                );
                return Optional.of(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting menu item: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<MenuItem> getAll() {
        DB db = DB.getInstance();
        List<MenuItem> items = new ArrayList<>();

        try {
            String sql = "SELECT * FROM MenuItem ORDER BY category, name";
            ResultSet rs = db.executeQuery(sql);

            while (rs.next()) {
                MenuItem item = new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("category")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all menu items: " + e.getMessage());
        }
        return items;
    }

    public List<MenuItem> getByCategory(String category) {
        DB db = DB.getInstance();
        List<MenuItem> items = new ArrayList<>();

        try {
            String sql = "SELECT * FROM MenuItem WHERE category = ? ORDER BY name";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                MenuItem item = new MenuItem(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("category")
                );
                items.add(item);
            }
        } catch (SQLException e) {
            System.err.println("Error getting menu items by category: " + e.getMessage());
        }
        return items;
    }

    @Override
    public void insert(MenuItem item) {
        DB db = DB.getInstance();
        try {
            String sql = "INSERT INTO MenuItem (name, price, category) VALUES (?, ?, ?)";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getCategory());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Menu item inserted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting menu item: " + e.getMessage());
        }
    }

    @Override
    public void update(MenuItem item) {
        DB db = DB.getInstance();
        try {
            String sql = "UPDATE MenuItem SET name=?, price=?, category=? WHERE id=?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setString(1, item.getName());
            stmt.setDouble(2, item.getPrice());
            stmt.setString(3, item.getCategory());
            stmt.setInt(4, item.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Menu item updated successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error updating menu item: " + e.getMessage());
        }
    }

    @Override
    public void delete(MenuItem item) {
        DB db = DB.getInstance();
        try {
            String sql = "DELETE FROM MenuItem WHERE id = ?";
            PreparedStatement stmt = db.getPreparedStatement(sql);
            stmt.setInt(1, item.getId());

            int rowsDeleted = stmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Menu item deleted successfully!");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting menu item: " + e.getMessage());
        }
    }

    public List<String> getCategories() {
        DB db = DB.getInstance();
        List<String> categories = new ArrayList<>();

        try {
            String sql = "SELECT DISTINCT category FROM MenuItem ORDER BY category";
            ResultSet rs = db.executeQuery(sql);

            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting categories: " + e.getMessage());
        }
        return categories;
    }
}