package entity;

import java.time.LocalDateTime;

public class Order {
    private int id;
    private LocalDateTime orderDate;
    private double subtotal;
    private double tax;
    private double total;

    public Order(int id, LocalDateTime orderDate, double subtotal, double tax, double total) {
        this.id = id;
        this.orderDate = orderDate;
        this.subtotal = subtotal;
        this.tax = tax;
        this.total = total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTax() { return tax; }
    public void setTax(double tax) { this.tax = tax; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}