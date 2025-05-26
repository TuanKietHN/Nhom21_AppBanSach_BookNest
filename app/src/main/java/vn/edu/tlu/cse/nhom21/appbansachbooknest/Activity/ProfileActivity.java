package vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity;

import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Database.DatabaseHelper;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.User;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvRole;
    private EditText etUsername, etEmail, etPassword;
    private Button btnUpdate;
    private CheckBox checkboxTogglePassword;
    private ImageButton btnBack;
    private DatabaseHelper dbHelper;
    private int userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        tvRole = findViewById(R.id.tvRole);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnUpdate = findViewById(R.id.btnUpdate);
        checkboxTogglePassword = findViewById(R.id.checkboxTogglePassword);
        btnBack = findViewById(R.id.btnBack);
        dbHelper = new DatabaseHelper(this);

        btnBack.setOnClickListener(v -> finish());

        loadUserInfo();

        btnUpdate.setOnClickListener(v -> updateUserInfo());

        // Xử lý sự kiện khi CheckBox được tích/bỏ tích
        checkboxTogglePassword.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Hiển thị mật khẩu
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            } else {
                // Ẩn mật khẩu
                etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            }
            // Di chuyển con trỏ về cuối văn bản sau khi thay đổi inputType
            etPassword.setSelection(etPassword.getText().length());
        });
    }

    private void loadUserInfo() {
        user = dbHelper.getUser(userId);
        if (user != null) {
            tvRole.setText("Vai trò: " + (user.getRole().equals("customer") ? "Khách hàng" : "Nhân viên"));
            etUsername.setText(user.getUsername());
            etEmail.setText(user.getEmail());
            etPassword.setText(user.getPassword());
        } else {
            Toast.makeText(this, "Lỗi: Không thể tải thông tin người dùng", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void updateUserInfo() {
        String username = etUsername.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean updated = dbHelper.updateUser(userId, username, email, password);
        if (updated) {
            Toast.makeText(this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
        }
    }
}