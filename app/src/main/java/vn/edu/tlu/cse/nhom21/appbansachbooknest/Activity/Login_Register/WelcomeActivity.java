package vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Login_Register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Customer.MainCustomerActivity;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Employee.MainEmployeeActivity;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // Ánh xạ nút "Get Started"
        MaterialButton btnGetStarted = findViewById(R.id.btnGetStarted);

        // Thiết lập sự kiện nhấn cho nút "Get Started"
        btnGetStarted.setOnClickListener(v -> {
            // Kiểm tra trạng thái đăng nhập
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            int userId = sharedPreferences.getInt("user_id", -1);
            String userRole = sharedPreferences.getString("user_role", null);

            Intent intent;
            if (userId != -1 && userRole != null) {
                // Người dùng đã đăng nhập
                if (userRole.equals("customer")) {
                    intent = new Intent(WelcomeActivity.this, MainCustomerActivity.class);
                    intent.putExtra("user_id", userId);
                } else if (userRole.equals("userRole")) {
                    intent = new Intent(WelcomeActivity.this, MainEmployeeActivity.class);
                    intent.putExtra("user_id", userId);
                } else {
                    // Vai trò không hợp lệ, chuyển đến LoginActivity
                    intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                }
            } else {
                // Người dùng chưa đăng nhập, chuyển đến LoginActivity
                intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            finish();
        });
    }
}