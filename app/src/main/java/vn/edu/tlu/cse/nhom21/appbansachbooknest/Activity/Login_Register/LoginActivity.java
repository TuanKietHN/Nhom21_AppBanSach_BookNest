package vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Login_Register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Customer.MainCustomerActivity;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Employee.MainEmployeeActivity;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Database.DatabaseHelper;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private DatabaseHelper dbHelper;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegisterLink = findViewById(R.id.tvRegisterLink);

        dbHelper = new DatabaseHelper(this);

        // Xử lý hiển thị/ẩn mật khẩu
        etPassword.setCompoundDrawablePadding(10);
        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2; // Vị trí drawableEnd
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    togglePasswordVisibility();
                    return true;
                }
            }
            return false;
        });

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra thông tin đăng nhập
            if (dbHelper.checkUser(email, password)) {
                // Lấy vai trò của người dùng
                String role = dbHelper.getUserRole(email);
                int userId = dbHelper.getUserId(email); // Lấy userId từ cơ sở dữ liệu

                if (userId == -1) {
                    Toast.makeText(LoginActivity.this, "Lỗi: Không tìm thấy userId", Toast.LENGTH_LONG).show();
                    return;
                }

                if (role != null) {
                    // Lưu userId vào SharedPreferences để tự động đăng nhập lần sau
                    SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt("user_id", userId);
                    editor.putString("user_role", role);
                    editor.putString("user_email", email);
                    editor.apply();

                    if (role.equals("employee")) {
                        // Nếu là nhân viên, chuyển hướng đến MainEmployeeActivity
                        Intent intent = new Intent(LoginActivity.this, MainEmployeeActivity.class);
                        intent.putExtra("user_id", userId); // Truyền userId (nếu cần cho MainEmployeeActivity)
                        startActivity(intent);
                        finish(); // Đóng LoginActivity để không quay lại
                    } else if (role.equals("customer")) {
                        // Nếu là khách hàng, chuyển hướng đến MainCustomerActivity
                        Intent intent = new Intent(LoginActivity.this, MainCustomerActivity.class);
                        intent.putExtra("user_id", userId); // Truyền userId
                        startActivity(intent);
                        finish(); // Đóng LoginActivity
                    } else {
                        // Vai trò không hợp lệ
                        Toast.makeText(LoginActivity.this, "Vai trò không hợp lệ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Không tìm thấy vai trò
                    Toast.makeText(LoginActivity.this, "Không thể xác định vai trò người dùng", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Đăng nhập thất bại
                Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    // Hàm bật/tắt hiển thị mật khẩu
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
        } else {
            etPassword.setTransformationMethod(SingleLineTransformationMethod.getInstance());
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        etPassword.setSelection(etPassword.getText().length()); // Đặt con trỏ về cuối
    }
}