package vn.edu.tlu.cse.nhom21.appbansachbooknest.Model;

import java.util.List;

public class Order {
    private int orderId;
    private int userId;
    private String orderDate;
    private String status;
    private String username;
    private List<OrderDetail> orderDetails;

    // Constructor cho nhân viên (có username)
    public Order(int orderId, int userId, String orderDate, String status, String username, List<OrderDetail> orderDetails) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.username = username;
        this.orderDetails = orderDetails;
    }

    // Constructor cho khách hàng (không cần username)
    public Order(int orderId, int userId, String orderDate, String status, List<OrderDetail> orderDetails) {
        this.orderId = orderId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.username = "User " + userId;
        this.orderDetails = orderDetails;
    }

    public int getOrderId() { return orderId; }
    public int getUserId() { return userId; }
    public String getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public String getUsername() { return username; }
    public List<OrderDetail> getOrderDetails() { return orderDetails; }
}