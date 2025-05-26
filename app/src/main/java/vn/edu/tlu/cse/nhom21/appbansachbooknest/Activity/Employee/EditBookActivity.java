package vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Employee;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Login_Register.LoginActivity;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Adapter.BookAdapter;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Database.DatabaseHelper;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.Book;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class EditBookActivity extends AppCompatActivity {

    private RecyclerView rvBooks;
    private TextView tvNoBooks;
    private EditText etBookTitle, etBookAuthor, etBookGenre, etBookPrice, etBookImage;
    private Button btnAddBook, btnUpdateBook, btnDeleteBook;
    private DatabaseHelper dbHelper;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private Book selectedBook;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        // Hiển thị nút quay lại
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Lấy userId từ Intent (nếu có)
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        rvBooks = findViewById(R.id.rvBooks);
        tvNoBooks = findViewById(R.id.tvNoBooks);
        etBookTitle = findViewById(R.id.etBookTitle);
        etBookAuthor = findViewById(R.id.etBookAuthor);
        etBookGenre = findViewById(R.id.etBookGenre);
        etBookPrice = findViewById(R.id.etBookPrice);
        etBookImage = findViewById(R.id.etBookImage);
        btnAddBook = findViewById(R.id.btnAddBook);
        btnUpdateBook = findViewById(R.id.btnUpdateBook);
        btnDeleteBook = findViewById(R.id.btnDeleteBook);
        BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);

        dbHelper = new DatabaseHelper(this);
        bookList = new ArrayList<>();

        // Thiết lập RecyclerView
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new BookAdapter(this, bookList, true, userId);
        rvBooks.setAdapter(bookAdapter);
        // Tắt cuộn lồng của RecyclerView để NestedScrollView xử lý cuộn
        rvBooks.setNestedScrollingEnabled(false);

        // Load danh sách sách
        loadBooks();

        // Thiết lập sự kiện click cho các mục trong RecyclerView
        bookAdapter.setOnItemClickListener(book -> {
            selectedBook = book;
            etBookTitle.setText(book.getTitle());
            etBookAuthor.setText(book.getAuthor());
            etBookGenre.setText(book.getGenre());
            etBookPrice.setText(String.valueOf(book.getPrice()));
            etBookImage.setText(book.getImage());
        });

        // Thiết lập sự kiện cho các nút
        btnAddBook.setOnClickListener(v -> addBook());
        btnUpdateBook.setOnClickListener(v -> updateBook());
        btnDeleteBook.setOnClickListener(v -> deleteBook());

        // Thiết lập BottomNavigationView
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                Intent intent = new Intent(EditBookActivity.this, MainEmployeeActivity.class);
                intent.putExtra("user_id", userId);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.nav_edit) {
                return true;
            } else if (itemId == R.id.nav_list) {
                Intent intent = new Intent(EditBookActivity.this, OrderListActivity.class);
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

        // Đánh dấu mục "Edit" là mục hiện tại
        bottomNavigation.setSelectedItemId(R.id.nav_edit);
    }

    private void loadBooks() {
        bookList.clear();
        Cursor cursor = null;
        try {
            cursor = dbHelper.getAllBooks();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex("book_id");
                    int titleIndex = cursor.getColumnIndex("title");
                    int authorIndex = cursor.getColumnIndex("author");
                    int genreIndex = cursor.getColumnIndex("genre");
                    int priceIndex = cursor.getColumnIndex("price");
                    int imageIndex = cursor.getColumnIndex("image");

                    if (idIndex == -1 || titleIndex == -1 || authorIndex == -1 ||
                            genreIndex == -1 || priceIndex == -1 || imageIndex == -1) {
                        Toast.makeText(this, "Lỗi: Không tìm thấy cột trong cơ sở dữ liệu", Toast.LENGTH_LONG).show();
                        return;
                    }

                    int id = cursor.getInt(idIndex);
                    String title = cursor.getString(titleIndex);
                    String author = cursor.getString(authorIndex);
                    String genre = cursor.getString(genreIndex);
                    double price = cursor.getDouble(priceIndex);
                    String image = cursor.getString(imageIndex);
                    bookList.add(new Book(id, title, author, genre, price, image));
                } while (cursor.moveToNext());
            } else {
                tvNoBooks.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khi tải danh sách sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        bookAdapter.notifyDataSetChanged();
        tvNoBooks.setVisibility(bookList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void addBook() {
        String title = etBookTitle.getText().toString().trim();
        String author = etBookAuthor.getText().toString().trim();
        String genre = etBookGenre.getText().toString().trim();
        String priceStr = etBookPrice.getText().toString().trim();
        String image = etBookImage.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || priceStr.isEmpty() || image.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.addBook(title, author, genre, price, image);
        if (success) {
            // Tạo thông báo cho tất cả khách hàng
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Cursor cursor = dbHelper.getAllUsersWithRole("customer");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int customerId = cursor.getInt(cursor.getColumnIndex("id"));
                    dbHelper.addNotification(customerId, "Sách mới '" + title + "' đã được thêm!", date);
                } while (cursor.moveToNext());
                cursor.close();
            }

            // Tạo thông báo cho nhân viên (trừ người thực hiện hành động)
            Cursor employeeCursor = dbHelper.getAllUsersWithRole("employee");
            if (employeeCursor != null && employeeCursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int employeeId = employeeCursor.getInt(employeeCursor.getColumnIndex("id"));
                    if (employeeId != userId) { // Không gửi thông báo cho chính người thêm sách
                        dbHelper.addNotification(employeeId, "Sách mới '" + title + "' đã được thêm bởi nhân viên khác!", date);
                    }
                } while (employeeCursor.moveToNext());
                employeeCursor.close();
            }

            Toast.makeText(this, "Thêm sách thành công", Toast.LENGTH_SHORT).show();
            clearInputs();
            loadBooks();
        } else {
            Toast.makeText(this, "Thêm sách thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateBook() {
        if (selectedBook == null) {
            Toast.makeText(this, "Vui lòng chọn một cuốn sách để sửa", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etBookTitle.getText().toString().trim();
        String author = etBookAuthor.getText().toString().trim();
        String genre = etBookGenre.getText().toString().trim();
        String priceStr = etBookPrice.getText().toString().trim();
        String image = etBookImage.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || genre.isEmpty() || priceStr.isEmpty() || image.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean success = dbHelper.updateBook(selectedBook.getId(), title, author, genre, price, image);
        if (success) {
            // Tạo thông báo cho tất cả khách hàng khi sách được cập nhật (tùy chọn)
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Cursor cursor = dbHelper.getAllUsersWithRole("customer");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int customerId = cursor.getInt(cursor.getColumnIndex("id"));
                    dbHelper.addNotification(customerId, "Sách '" + title + "' đã được cập nhật thông tin!", date);
                } while (cursor.moveToNext());
                cursor.close();
            }

            // Tạo thông báo cho nhân viên (trừ người thực hiện hành động)
            Cursor employeeCursor = dbHelper.getAllUsersWithRole("employee");
            if (employeeCursor != null && employeeCursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int employeeId = employeeCursor.getInt(employeeCursor.getColumnIndex("id"));
                    if (employeeId != userId) {
                        dbHelper.addNotification(employeeId, "Sách '" + title + "' đã được cập nhật bởi nhân viên khác!", date);
                    }
                } while (employeeCursor.moveToNext());
                employeeCursor.close();
            }

            Toast.makeText(this, "Sửa sách thành công", Toast.LENGTH_SHORT).show();
            clearInputs();
            loadBooks();
            selectedBook = null;
        } else {
            Toast.makeText(this, "Sửa sách thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBook() {
        if (selectedBook == null) {
            Toast.makeText(this, "Vui lòng chọn một cuốn sách để xóa", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = selectedBook.getTitle();
        boolean success = dbHelper.deleteBook(selectedBook.getId());
        if (success) {
            // Tạo thông báo cho tất cả khách hàng khi sách bị xóa (tùy chọn)
            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            Cursor cursor = dbHelper.getAllUsersWithRole("customer");
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int customerId = cursor.getInt(cursor.getColumnIndex("id"));
                    dbHelper.addNotification(customerId, "Sách '" + title + "' đã bị xóa khỏi hệ thống!", date);
                } while (cursor.moveToNext());
                cursor.close();
            }

            // Tạo thông báo cho nhân viên (trừ người thực hiện hành động)
            Cursor employeeCursor = dbHelper.getAllUsersWithRole("employee");
            if (employeeCursor != null && employeeCursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int employeeId = employeeCursor.getInt(employeeCursor.getColumnIndex("id"));
                    if (employeeId != userId) {
                        dbHelper.addNotification(employeeId, "Sách '" + title + "' đã bị xóa bởi nhân viên khác!", date);
                    }
                } while (employeeCursor.moveToNext());
                employeeCursor.close();
            }

            Toast.makeText(this, "Xóa sách thành công", Toast.LENGTH_SHORT).show();
            clearInputs();
            loadBooks();
            selectedBook = null;
        } else {
            Toast.makeText(this, "Xóa sách thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void clearInputs() {
        etBookTitle.setText("");
        etBookAuthor.setText("");
        etBookGenre.setText("");
        etBookPrice.setText("");
        etBookImage.setText("");
    }

    private void logout() {
        // Xóa thông tin đăng nhập trong SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        // Chuyển hướng về LoginActivity
        Intent intent = new Intent(EditBookActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}