package vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Login_Register;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.regex.Pattern;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Database.DatabaseHelper;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etPassword, etConfirmPassword;
    private Spinner spinnerRole;
    private TextView tvPasswordStrength;
    private DatabaseHelper dbHelper;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    // Tiêu chí kiểm tra mật khẩu
    private static final int MIN_LENGTH = 8;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[@#$%^&+=!]");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        spinnerRole = findViewById(R.id.spinnerRole);
        tvPasswordStrength = findViewById(R.id.tvPasswordStrength);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView tvLoginLink = findViewById(R.id.tvLoginLink);

        dbHelper = new DatabaseHelper(this);

        // Thiết lập Spinner cho vai trò
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        // Kiểm tra độ mạnh mật khẩu theo thời gian thực (chỉ để hiển thị, không chặn đăng ký)
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = s.toString();
                String strengthMessage = checkPasswordStrength(password);
                tvPasswordStrength.setText(strengthMessage);
                if (strengthMessage.equals("Mật khẩu mạnh")) {
                    etPassword.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.GREEN));
                    tvPasswordStrength.setTextColor(Color.GREEN);
                } else {
                    etPassword.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.RED));
                    tvPasswordStrength.setTextColor(Color.RED);
                }
            }
        });

        // Xử lý hiển thị/ẩn mật khẩu cho etPassword
        etPassword.setCompoundDrawablePadding(10);
        etPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etPassword.getRight() - etPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    togglePasswordVisibility(etPassword, true);
                    return true;
                }
            }
            return false;
        });

        // Xử lý hiển thị/ẩn mật khẩu cho etConfirmPassword
        etConfirmPassword.setCompoundDrawablePadding(10);
        etConfirmPassword.setOnTouchListener((v, event) -> {
            final int DRAWABLE_END = 2;
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (etConfirmPassword.getRight() - etConfirmPassword.getCompoundDrawables()[DRAWABLE_END].getBounds().width())) {
                    togglePasswordVisibility(etConfirmPassword, false);
                    return true;
                }
            }
            return false;
        });

        btnRegister.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String role = spinnerRole.getSelectedItem().toString();

            // Kiểm tra các trường có rỗng không
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra định dạng email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(RegisterActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra mật khẩu có khớp không
            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            // Đăng ký người dùng (không kiểm tra độ mạnh mật khẩu)
            if (dbHelper.addUser(username, email, password, role)) {
                Toast.makeText(RegisterActivity.this, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại, email đã tồn tại", Toast.LENGTH_SHORT).show();
            }
        });

        tvLoginLink.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    // Hàm kiểm tra độ mạnh mật khẩu (chỉ để hiển thị, không chặn đăng ký)
    private String checkPasswordStrength(String password) {
        // Kiểm tra độ dài
        if (password.length() < MIN_LENGTH) {
            return "Mật khẩu phải có ít nhất " + MIN_LENGTH + " ký tự";
        }

        // Kiểm tra chữ cái in hoa
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            return "Mật khẩu phải chứa ít nhất 1 chữ cái in hoa";
        }

        // Kiểm tra chữ cái thường
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            return "Mật khẩu phải chứa ít nhất 1 chữ cái thường";
        }

        // Kiểm tra số
        if (!DIGIT_PATTERN.matcher(password).find()) {
            return "Mật khẩu phải chứa ít nhất 1 số";
        }

        // Kiểm tra ký tự đặc biệt
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            return "Mật khẩu phải chứa ít nhất 1 ký tự đặc biệt (@#$%^&+=!)";
        }

        return "Mật khẩu mạnh";
    }

    // Hàm bật/tắt hiển thị mật khẩu
    private void togglePasswordVisibility(EditText editText, boolean isPasswordField) {
        if (isPasswordField) {
            if (isPasswordVisible) {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
            } else {
                editText.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
            }
            isPasswordVisible = !isPasswordVisible;
        } else {
            if (isConfirmPasswordVisible) {
                editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
            } else {
                editText.setTransformationMethod(SingleLineTransformationMethod.getInstance());
                editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
            }
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
        }
        editText.setSelection(editText.getText().length());
    }
}