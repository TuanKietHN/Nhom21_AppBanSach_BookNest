package vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Customer;

import android.content.Intent;
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
import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Database.DatabaseHelper;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.Order;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Adapter.OrderAdapter;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.OrderDetail;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class CustomerOrderActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private ProgressBar progressBar;
    private DatabaseHelper dbHelper;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_order);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        rvOrders = findViewById(R.id.rvOrders);
        progressBar = findViewById(R.id.progressBar);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        dbHelper = new DatabaseHelper(this);
        orderList = new ArrayList<>();

        // Thiết lập RecyclerView
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new OrderAdapter(this, orderList);
        rvOrders.setAdapter(orderAdapter);
        rvOrders.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Load danh sách đơn hàng
        loadOrders();

        // Thiết lập BottomNavigationView
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_books) {
                Intent intent = new Intent(CustomerOrderActivity.this, MainCustomerActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return true;
            } else if (itemId == R.id.nav_cart) {
                Intent intent = new Intent(CustomerOrderActivity.this, CartActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return true;
            } else if (itemId == R.id.nav_orders) {
                return true;
            }
            return false;
        });

        bottomNavigation.setSelectedItemId(R.id.nav_orders);
    }

    private void loadOrders() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        orderList.clear();
        Cursor cursor = null;
        try {
            cursor = dbHelper.getOrders(userId);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int orderIdIndex = cursor.getColumnIndex("order_id");
                    int orderDateIndex = cursor.getColumnIndex("order_date");
                    int statusIndex = cursor.getColumnIndex("status");

                    if (orderIdIndex == -1 || orderDateIndex == -1 || statusIndex == -1) {
                        Toast.makeText(this, "Lỗi: Không tìm thấy cột trong cơ sở dữ liệu", Toast.LENGTH_LONG).show();
                        return;
                    }

                    int orderId = cursor.getInt(orderIdIndex);
                    String orderDate = cursor.getString(orderDateIndex);
                    String status = cursor.getString(statusIndex);

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
                    orderList.add(new Order(orderId, userId, orderDate, status, orderDetails));
                } while (cursor.moveToNext());
            } else {
                Toast.makeText(this, "Bạn chưa có đơn hàng nào!", Toast.LENGTH_LONG).show();
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
}