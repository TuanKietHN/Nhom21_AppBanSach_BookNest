package vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Employee;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.Login_Register.LoginActivity;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.NotificationsActivity;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Adapter.BookAdapter;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Database.DatabaseHelper;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.Book;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity.ProfileActivity;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class MainEmployeeActivity extends AppCompatActivity {

    private RecyclerView rvBooks;
    private SearchView searchView;
    private BottomNavigationView bottomNavigation;
    private FloatingActionButton fabAddBook;
    private ProgressBar progressBar;
    private TextView tvGreeting;
    private ImageButton btnNotifications, btnProfile;
    private TextView tvNotificationCount;
    private BookAdapter bookAdapter;
    private List<Book> bookList;
    private DatabaseHelper dbHelper;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_employee);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        rvBooks = findViewById(R.id.rvBooks);
        searchView = findViewById(R.id.searchView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        fabAddBook = findViewById(R.id.fabAddBook);
        progressBar = findViewById(R.id.progressBar);
        tvGreeting = findViewById(R.id.tvGreeting);
        btnNotifications = findViewById(R.id.btnNotifications);
        btnProfile = findViewById(R.id.btnProfile);
        tvNotificationCount = findViewById(R.id.tvNotificationCount);

        if (rvBooks == null || searchView == null || bottomNavigation == null || fabAddBook == null || progressBar == null || tvGreeting == null || btnNotifications == null || btnProfile == null || tvNotificationCount == null) {
            Log.e("MainEmployeeActivity", "One or more views are null");
            Toast.makeText(this, "Lỗi: Không thể tải giao diện", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        dbHelper = new DatabaseHelper(this);
        bookList = new ArrayList<>();

        // Áp dụng hiệu ứng fade_in cho tvGreeting
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        tvGreeting.startAnimation(fadeIn);

        // Thiết lập RecyclerView
        rvBooks.setLayoutManager(new LinearLayoutManager(this));
        bookAdapter = new BookAdapter(this, bookList, true, userId);
        rvBooks.setAdapter(bookAdapter);
        rvBooks.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        // Load danh sách sách
        loadBooks();

        // Thiết lập SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterBooks(newText);
                return true;
            }
        });

        // Thiết lập BottomNavigationView
        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                return true;
            } else if (itemId == R.id.nav_edit) {
                Intent intent = new Intent(MainEmployeeActivity.this, EditBookActivity.class);
                intent.putExtra("user_id", userId); // Đảm bảo truyền userId
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.nav_list) {
                Intent intent = new Intent(MainEmployeeActivity.this, OrderListActivity.class);
                intent.putExtra("user_id", userId); // Thêm dòng này để truyền userId
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                return true;
            } else if (itemId == R.id.nav_logout) {
                logout();
                return true;
            }
            return false;
        });

        // Thiết lập FloatingActionButton
        fabAddBook.setOnClickListener(v -> {
            Intent intent = new Intent(MainEmployeeActivity.this, EditBookActivity.class);
            intent.putExtra("user_id", userId); // Đảm bảo truyền userId
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Thiết lập sự kiện nhấn icon thông báo
        btnNotifications.setOnClickListener(v -> {
            Intent intent = new Intent(MainEmployeeActivity.this, NotificationsActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Thiết lập sự kiện nhấn icon user
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainEmployeeActivity.this, ProfileActivity.class);
            intent.putExtra("user_id", userId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        // Cập nhật số lượng thông báo chưa đọc
        updateNotificationCount();
    }

    private void loadBooks() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        bookList.clear();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM books", null);
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
                        Log.e("MainEmployeeActivity", "One or more columns not found in database");
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
                Log.d("MainEmployeeActivity", "Loaded " + bookList.size() + " books");
            } else {
                Log.d("MainEmployeeActivity", "No books found in database");
                Toast.makeText(this, "Không có sách nào trong cơ sở dữ liệu. Vui lòng thêm sách mới!", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("MainEmployeeActivity", "Error loading books: " + e.getMessage());
            Toast.makeText(this, "Lỗi khi tải danh sách sách: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        bookAdapter.updateList(bookList);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void filterBooks(String query) {
        if (bookList == null) {
            Log.e("MainEmployeeActivity", "bookList is null in filterBooks");
            return;
        }

        List<Book> filteredList = new ArrayList<>();
        for (Book book : bookList) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(book);
            }
        }
        bookAdapter.updateList(filteredList);
    }

    private void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(MainEmployeeActivity.this, LoginActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updateNotificationCount() {
        int count = dbHelper.getUnreadNotificationCount(userId);
        if (count > 0) {
            tvNotificationCount.setText(String.valueOf(count));
            tvNotificationCount.setVisibility(View.VISIBLE);
        } else {
            tvNotificationCount.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBooks();
        updateNotificationCount();
    }
}