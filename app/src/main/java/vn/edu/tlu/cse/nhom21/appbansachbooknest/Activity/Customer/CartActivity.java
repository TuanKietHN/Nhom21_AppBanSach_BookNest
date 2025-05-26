package vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Adapter.CartAdapter;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.CartItem;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Database.DatabaseHelper;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class CartActivity extends AppCompatActivity {

    private RecyclerView rvCart;
    private Button btnPlaceOrder;
    private ProgressBar progressBar;
    private TextView tvTotalPrice;
    private DatabaseHelper dbHelper;
    private CartAdapter cartAdapter;
    private List<CartItem> cartList;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        userId = getIntent().getIntExtra("user_id", -1);
        Log.d("CartActivity", "User ID: " + userId);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        rvCart = findViewById(R.id.rvCart);
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder);
        progressBar = findViewById(R.id.progressBar);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        dbHelper = new DatabaseHelper(this);
        cartList = new ArrayList<>();

        // Thiết lập RecyclerView
        rvCart.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter = new CartAdapter(this, cartList);
        rvCart.setAdapter(cartAdapter);
        rvCart.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        // Load giỏ hàng
        loadCart();

        // Thiết lập sự kiện xóa khỏi giỏ hàng
        cartAdapter.setOnDeleteClickListener(cartItem -> {
            boolean success = dbHelper.removeFromCart(cartItem.getCartId());
            if (success) {
                Toast.makeText(this, "Đã xóa " + cartItem.getBookTitle() + " khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                loadCart();
            } else {
                Toast.makeText(this, "Xóa thất bại", Toast.LENGTH_SHORT).show();
            }
        });

        // Thiết lập sự kiện đặt mua
        btnPlaceOrder.setOnClickListener(v -> placeOrder());

        // Thiết lập BottomNavigationView
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_books) {
                Intent intent = new Intent(CartActivity.this, MainCustomerActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return true;
            } else if (itemId == R.id.nav_cart) {
                return true;
            } else if (itemId == R.id.nav_orders) {
                Intent intent = new Intent(CartActivity.this, CustomerOrderActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_logout) {
                logout();
                return true;
            }
            return false;
        });

        bottomNavigation.setSelectedItemId(R.id.nav_cart);
    }

    private void loadCart() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        cartList.clear();
        cartList.addAll(dbHelper.getCartItemsList(userId));
        Log.d("CartActivity", "Cart items count: " + cartList.size());
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống! Vui lòng thêm sách.", Toast.LENGTH_LONG).show();
            rvCart.setVisibility(View.GONE);
            btnPlaceOrder.setEnabled(false);
        } else {
            rvCart.setVisibility(View.VISIBLE);
            btnPlaceOrder.setEnabled(true);
        }
        cartAdapter.notifyDataSetChanged();
        updateTotalPrice();

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : cartList) {
            totalPrice += item.getBookPrice() * item.getQuantity();
        }
        tvTotalPrice.setText(String.format("Tổng tiền: %,.0f VNĐ", totalPrice));
    }

    private void placeOrder() {
        if (cartList.isEmpty()) {
            Toast.makeText(this, "Giỏ hàng trống, vui lòng thêm sách trước khi đặt mua", Toast.LENGTH_SHORT).show();
            return;
        }

        String orderDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        boolean success = dbHelper.addOrder(userId, cartList, orderDate, "pending");
        if (success) {
            Toast.makeText(this, "Đặt mua thành công", Toast.LENGTH_SHORT).show();
            // Không gọi loadCart() ở đây vì chúng ta sẽ chuyển sang màn hình khác
            Intent intent = new Intent(CartActivity.this, CustomerOrderActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        } else {
            Toast.makeText(this, "Đặt mua thất bại", Toast.LENGTH_SHORT).show();
            Log.e("CartActivity", "Failed to place order for userId: " + userId);
        }
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(CartActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCart();
    }
}