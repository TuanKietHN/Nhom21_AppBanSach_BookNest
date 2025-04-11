package vn.edu.tlu.cse.nhom21.appbansachbooknest.Activity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Adapter.NotificationAdapter;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Database.DatabaseHelper;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.Notification;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.R;

public class NotificationsActivity extends AppCompatActivity {
    private RecyclerView rvNotifications;
    private NotificationAdapter notificationAdapter;
    private List<Notification> notificationList;
    private DatabaseHelper dbHelper;
    private int userId;
    private ImageButton btnBack; // Thêm biến cho nút quay lại

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Ánh xạ nút quay lại
        btnBack = findViewById(R.id.btnBack);
        rvNotifications = findViewById(R.id.rvNotifications);
        dbHelper = new DatabaseHelper(this);
        notificationList = new ArrayList<>();

        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        notificationAdapter = new NotificationAdapter(this, notificationList);
        rvNotifications.setAdapter(notificationAdapter);

        // Xử lý sự kiện nhấn nút quay lại
        btnBack.setOnClickListener(v -> finish());

        loadNotifications();
    }

    private void loadNotifications() {
        Cursor cursor = dbHelper.getNotifications(userId);
        notificationList.clear();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("notification_id"));
                @SuppressLint("Range") int userId = cursor.getInt(cursor.getColumnIndex("user_id"));
                @SuppressLint("Range") String message = cursor.getString(cursor.getColumnIndex("message"));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
                @SuppressLint("Range") int isReadInt = cursor.getInt(cursor.getColumnIndex("is_read"));
                boolean isRead = isReadInt == 1;
                notificationList.add(new Notification(id, userId, message, date, isRead));
            } while (cursor.moveToNext());
            cursor.close();
        }
        notificationAdapter.updateList(notificationList);

        if (notificationList.isEmpty()) {
            Toast.makeText(this, "Không có thông báo nào.", Toast.LENGTH_SHORT).show();
        }
    }
}