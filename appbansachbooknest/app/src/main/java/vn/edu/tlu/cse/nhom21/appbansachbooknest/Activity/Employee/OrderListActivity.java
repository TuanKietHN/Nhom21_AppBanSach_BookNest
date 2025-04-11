package vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Employee;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Login_Register.LoginActivity;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Adapter.OrderAdapter;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Database.DatabaseHelper;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.Order;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.OrderDetail;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class OrderListActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private ProgressBar progressBar;
    private BottomNavigationView bottomNavigation;
    private DatabaseHelper dbHelper;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        rvOrders = findViewById(R.id.rvOrders);
        progressBar = findViewById(R.id.progressBar);
        bottomNavigation = findViewById(R.id.bottomNavigation);

        dbHelper = new DatabaseHelper(this);
        orderList = new ArrayList<>();

        // Thiết lập RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, orderList, this::confirmOrder);
        rvOrders.setAdapter(orderAdapter);
        rvOrders.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Load danh sách đơn hàng
        loadOrders();

        // Thiết lập BottomNavigationView
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(OrderListActivity.this, MainEmployeeActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_edit) {
                Intent intent = new Intent(OrderListActivity.this, EditBookActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_list) {
                return true;
            } else if (itemId == R.id.nav_logout) {
                logout();
                return true;
            }
            return false;
        });

        bottomNavigation.setSelectedItemId(R.id.nav_list);
    }

    private void loadOrders() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        orderList.clear();
        Cursor cursor = null;
        try {
            cursor = dbHelper.getAllOrders();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int orderIdIndex = cursor.getColumnIndex("order_id");
                    int userIdIndex = cursor.getColumnIndex("user_id");
                    int orderDateIndex = cursor.getColumnIndex("order_date");
                    int statusIndex = cursor.getColumnIndex("status");
                    int usernameIndex = cursor.getColumnIndex("username");

                    if (orderIdIndex == -1 || userIdIndex == -1 || orderDateIndex == -1 ||
                            statusIndex == -1 || usernameIndex == -1) {
                        Toast.makeText(this, "Lỗi: Không tìm thấy cột trong cơ sở dữ liệu", Toast.LENGTH_LONG).show();
                        return;
                    }

                    int orderId = cursor.getInt(orderIdIndex);
                    int userId = cursor.getInt(userIdIndex);
                    String orderDate = cursor.getString(orderDateIndex);
                    String status = cursor.getString(statusIndex);
                    String username = cursor.getString(usernameIndex);

                    // Lấy chi tiết đơn hàng
                    List<OrderDetail> orderDetails = new ArrayList<>();
                    Cursor detailCursor = dbHelper.getOrderDetails(orderId);
                    if (detailCursor != null && detailCursor.moveToFirst()) {
                        do {
                            int bookIdIndex = detailCursor.getColumnIndex("book_id");
                            int quantityIndex = detailCursor.getColumnIndex("quantity");
                            int titleIndex = detailCursor.getColumnIndex("title");
                            int authorIndex = detailCursor.getColumnIndex("author");
                            int priceIndex = detailCursor.getColumnIndex("price");
                            int imageIndex = detailCursor.getColumnIndex("image");

                            int bookId = detailCursor.getInt(bookIdIndex);
                            int quantity = detailCursor.getInt(quantityIndex);
                            String title = detailCursor.getString(titleIndex);
                            String author = detailCursor.getString(authorIndex);
                            double price = detailCursor.getDouble(priceIndex);
                            String image = detailCursor.getString(imageIndex);
                            orderDetails.add(new OrderDetail(bookId, title, author, price, image, quantity));
                        } while (detailCursor.moveToNext());
                        detailCursor.close();
                    }
                    orderList.add(new Order(orderId, userId, orderDate, status, username, orderDetails));
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "Không có đơn hàng nào trong cơ sở dữ liệu", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tải danh sách đơn hàng: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        orderAdapter.notifyDataSetChanged();

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void confirmOrder(int orderId) {
        // Tìm userId của đơn hàng
        int orderUserId = -1;
        for (Order order : orderList) {
            if (order.getOrderId() == orderId) {
                orderUserId = order.getUserId();
                break;
            }
        }

        if (orderUserId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.updateOrderStatus(orderId, "confirmed");
        if (success) {
            // Tạo thông báo cho khách hàng
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            dbHelper.addNotification(orderUserId, "Đơn hàng #" + orderId + " của bạn đã được xác nhận!", date);

            // Tạo thông báo cho các nhân viên khác (trừ người thực hiện hành động)
            Cursor employeeCursor = dbHelper.getAllUsersWithRole("employee");
            if (employeeCursor != null && employeeCursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int employeeId = employeeCursor.getInt(employeeCursor.getColumnIndex("id"));
                    if (employeeId != userId) {
                        dbHelper.addNotification(employeeId, "Đơn hàng #" + orderId + " đã được xác nhận bởi nhân viên khác!", date);
                    }
                } while (employeeCursor.moveToNext());
                employeeCursor.close();
            }

            Toast.makeText(this, "Đã xác nhận đơn hàng #" + orderId, Toast.LENGTH_SHORT).show();
            loadOrders();
        } else {
            Toast.makeText(this, "Xác nhận thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void logout() {
        // Xóa thông tin đăng nhập trong SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Chuyển hướng về LoginActivity
        Intent intent = new Intent(OrderListActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        finish();
    }
}