package vn.edu.tlu.cse.nhom21.appbansachbooknest.Database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.CartItem;
import vn.edu.tlu.cse.nhom21.appbansachbooknest.Model.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "BookNest.db";
    private static final int DATABASE_VERSION = 4; // Tăng version do thêm bảng mới

    // Bảng người dùng (users)
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_ROLE = "role";

    // Bảng sách (books)
    private static final String TABLE_BOOKS = "books";
    private static final String COLUMN_BOOK_ID = "book_id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_GENRE = "genre";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IMAGE = "image";

    // Bảng giỏ hàng (cart)
    private static final String TABLE_CART = "cart";
    private static final String COLUMN_CART_ID = "cart_id";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_BOOK_ID_FK = "book_id";
    private static final String COLUMN_QUANTITY = "quantity";

    // Bảng đơn hàng (orders)
    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ORDER_ID = "order_id";
    private static final String COLUMN_USER_ID_FK = "user_id";
    private static final String COLUMN_ORDER_DATE = "order_date";
    private static final String COLUMN_STATUS = "status";

    // Bảng chi tiết đơn hàng (order_details)
    private static final String TABLE_ORDER_DETAILS = "order_details";
    private static final String COLUMN_ORDER_ID_FK = "order_id";
    private static final String COLUMN_BOOK_ID_FK_ORDER = "book_id";
    private static final String COLUMN_QUANTITY_ORDER = "quantity";

    // Bảng thông báo (notifications) - Mới
    private static final String TABLE_NOTIFICATIONS = "notifications";
    private static final String COLUMN_NOTIFICATION_ID = "notification_id";
    private static final String COLUMN_NOTIFICATION_USER_ID = "user_id";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_IS_READ = "is_read"; // 0: chưa đọc, 1: đã đọc

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tạo bảng users
        String createUserTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT, " +
                COLUMN_ROLE + " TEXT)";
        db.execSQL(createUserTable);
        Log.d("DatabaseHelper", "Created table: " + TABLE_USERS);

        // Tạo bảng books
        String createBooksTable = "CREATE TABLE " + TABLE_BOOKS + " (" +
                COLUMN_BOOK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_GENRE + " TEXT, " +
                COLUMN_PRICE + " REAL, " +
                COLUMN_IMAGE + " TEXT)";
        db.execSQL(createBooksTable);
        Log.d("DatabaseHelper", "Created table: " + TABLE_BOOKS);

        // Tạo bảng cart
        String createCartTable = "CREATE TABLE " + TABLE_CART + " (" +
                COLUMN_CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID + " INTEGER, " +
                COLUMN_BOOK_ID_FK + " INTEGER, " +
                COLUMN_QUANTITY + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "), " +
                "FOREIGN KEY(" + COLUMN_BOOK_ID_FK + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + "))";
        db.execSQL(createCartTable);
        Log.d("DatabaseHelper", "Created table: " + TABLE_CART);

        // Tạo bảng orders
        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_ORDER_DATE + " TEXT, " +
                COLUMN_STATUS + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(createOrdersTable);
        Log.d("DatabaseHelper", "Created table: " + TABLE_ORDERS);

        // Tạo bảng order_details
        String createOrderDetailsTable = "CREATE TABLE " + TABLE_ORDER_DETAILS + " (" +
                COLUMN_ORDER_ID_FK + " INTEGER, " +
                COLUMN_BOOK_ID_FK_ORDER + " INTEGER, " +
                COLUMN_QUANTITY_ORDER + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_ORDER_ID_FK + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_BOOK_ID_FK_ORDER + ") REFERENCES " + TABLE_BOOKS + "(" + COLUMN_BOOK_ID + "))";
        db.execSQL(createOrderDetailsTable);
        Log.d("DatabaseHelper", "Created table: " + TABLE_ORDER_DETAILS);

        // Tạo bảng notifications - Mới
        String createNotificationsTable = "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                COLUMN_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NOTIFICATION_USER_ID + " INTEGER, " +
                COLUMN_MESSAGE + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_IS_READ + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COLUMN_NOTIFICATION_USER_ID + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_ID + "))";
        db.execSQL(createNotificationsTable);
        Log.d("DatabaseHelper", "Created table: " + TABLE_NOTIFICATIONS);

        // Thêm dữ liệu mẫu
        insertSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("DatabaseHelper", "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS); // Thêm bảng mới vào onUpgrade
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }/*

    // Thêm dữ liệu mẫu*/
    private void insertSampleData(SQLiteDatabase db) {
        // Dữ liệu mẫu cho bảng users
        insertUser(db, "Nguyen Van A", "nguyenvana@gmail.com", "password123", "customer");
        insertUser(db, "Tran Thi B", "tranthib@gmail.com", "password456", "employee");
        insertUser(db, "Le Van C", "levanc@gmail.com", "password789", "customer");
        insertUser(db, "Pham Thi D", "phamthid@gmail.com", "password101", "employee");
        insertUser(db, "Hoang Van E", "hoangvane@gmail.com", "password202", "customer");
        Log.d("DatabaseHelper", "Inserted 5 users into table: " + TABLE_USERS);

        // Dữ liệu mẫu cho bảng books
        insertBook(db, "Dế Mèn Phiêu Lưu Ký", "Tô Hoài", "Văn học thiếu nhi", 150000, "de_men_phieu_luu_ky.jpg");
        insertBook(db, "Nhà Giả Kim", "Paulo Coelho", "Tiểu thuyết", 200000, "nha_gia_kim.jpg");
        insertBook(db, "Harry Potter và Hòn Đá Phù Thủy", "J.K. Rowling", "Tiểu thuyết giả tưởng", 250000, "harry_potter_1.jpg");
        insertBook(db, "Đắc Nhân Tâm", "Dale Carnegie", "Kỹ năng sống", 180000, "dac_nhan_tam.jpg");
        insertBook(db, "Bí Mật Tư Duy Triệu Phú", "T. Harv Eker", "Kinh doanh", 220000, "bi_mat_tu_duy_trieu_phu.jpg");
        Log.d("DatabaseHelper", "Inserted 5 books into table: " + TABLE_BOOKS);

        // Dữ liệu mẫu cho bảng cart (giả sử user_id 1 và 3 là khách hàng)
        insertCartItem(db, 1, 1, 2);
        insertCartItem(db, 1, 2, 1);
        insertCartItem(db, 3, 3, 1);
        insertCartItem(db, 3, 4, 3);
        insertCartItem(db, 1, 5, 1);
        Log.d("DatabaseHelper", "Inserted 5 cart items into table: " + TABLE_CART);

        // Dữ liệu mẫu cho bảng orders và order_details
        long orderId1 = insertOrder(db, 1, "2025-04-08", "confirmed");
        insertOrderDetail(db, orderId1, 1, 2);
        insertOrderDetail(db, orderId1, 2, 1);

        long orderId2 = insertOrder(db, 3, "2025-04-07", "pending");
        insertOrderDetail(db, orderId2, 3, 1);
        insertOrderDetail(db, orderId2, 4, 3);

        long orderId3 = insertOrder(db, 1, "2025-04-06", "confirmed");
        insertOrderDetail(db, orderId3, 5, 1);
        Log.d("DatabaseHelper", "Inserted 3 orders and their details into tables: " + TABLE_ORDERS + " and " + TABLE_ORDER_DETAILS);

        // Dữ liệu mẫu cho bảng notifications - Mới
        insertNotification(db, 1, "Đơn hàng #" + orderId1 + " của bạn đã được xác nhận!", "2025-04-08");
        insertNotification(db, 1, "Sách mới 'Dế Mèn Phiêu Lưu Ký' đã được thêm!", "2025-04-07");
        insertNotification(db, 3, "Đơn hàng #" + orderId2 + " của bạn đang chờ xử lý.", "2025-04-07");
        insertNotification(db, 2, "Sách mới 'Nhà Giả Kim' đã được thêm!", "2025-04-07");
        insertNotification(db, 4, "Có đơn hàng mới cần xử lý!", "2025-04-08");
        Log.d("DatabaseHelper", "Inserted 5 notifications into table: " + TABLE_NOTIFICATIONS);
    }

    // Hàm hỗ trợ thêm dữ liệu vào bảng users
    private void insertUser(SQLiteDatabase db, String username, String email, String password, String role) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);
        long result = db.insert(TABLE_USERS, null, values);
        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert user: " + username);
        }
    }

    // Hàm hỗ trợ thêm dữ liệu vào bảng books
    private void insertBook(SQLiteDatabase db, String title, String author, String genre, double price, String image) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_GENRE, genre);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_IMAGE, image);
        long result = db.insert(TABLE_BOOKS, null, values);
        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert book: " + title);
        }
    }

    // Hàm hỗ trợ thêm dữ liệu vào bảng cart
    private void insertCartItem(SQLiteDatabase db, int userId, int bookId, int quantity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_BOOK_ID_FK, bookId);
        values.put(COLUMN_QUANTITY, quantity);
        long result = db.insert(TABLE_CART, null, values);
        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert cart item: userId=" + userId + ", bookId=" + bookId);
        }
    }

    // Hàm hỗ trợ thêm dữ liệu vào bảng orders
    private long insertOrder(SQLiteDatabase db, int userId, String orderDate, String status) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, userId);
        values.put(COLUMN_ORDER_DATE, orderDate);
        values.put(COLUMN_STATUS, status);
        return db.insert(TABLE_ORDERS, null, values);
    }

    // Hàm hỗ trợ thêm dữ liệu vào bảng order_details
    private void insertOrderDetail(SQLiteDatabase db, long orderId, int bookId, int quantity) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID_FK, orderId);
        values.put(COLUMN_BOOK_ID_FK_ORDER, bookId);
        values.put(COLUMN_QUANTITY_ORDER, quantity);
        long result = db.insert(TABLE_ORDER_DETAILS, null, values);
        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert order detail: orderId=" + orderId + ", bookId=" + bookId);
        }
    }

    // Hàm hỗ trợ thêm dữ liệu vào bảng notifications - Mới
    private void insertNotification(SQLiteDatabase db, int userId, String message, String date) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_USER_ID, userId);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_IS_READ, 0); // Mặc định là chưa đọc
        long result = db.insert(TABLE_NOTIFICATIONS, null, values);
        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert notification for userId: " + userId);
        }
    }

    // Thêm người dùng
    public boolean addUser(String username, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        values.put(COLUMN_ROLE, role);
        long result = db.insert(TABLE_USERS, null, values);
        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to add user: " + username);
        }
        return result != -1;
    }

    // Kiểm tra người dùng
    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=? AND " + COLUMN_PASSWORD + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email, password});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        return exists;
    }

    // Lấy vai trò người dùng
    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ROLE + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        String role = null;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String roleResult = cursor.getString(cursor.getColumnIndex(COLUMN_ROLE));
            role = roleResult;
        }
        cursor.close();
        return role;
    }

    // Lấy user_id dựa trên email
    public int getUserId(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_EMAIL + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        int userId = -1;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            userId = id;
        }
        cursor.close();
        return userId;
    }

    // Thêm sách
    public boolean addBook(String title, String author, String genre, double price, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_GENRE, genre);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_IMAGE, image);
        long result = db.insert(TABLE_BOOKS, null, values);
        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to add book: " + title);
        }
        return result != -1;
    }

    // Sửa sách
    public boolean updateBook(int bookId, String title, String author, String genre, double price, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, title);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_GENRE, genre);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_IMAGE, image);
        int result = db.update(TABLE_BOOKS, values, COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(bookId)});
        return result > 0;
    }

    // Xóa sách
    public boolean deleteBook(int bookId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COLUMN_BOOK_ID_FK + "=?", new String[]{String.valueOf(bookId)});
        db.delete(TABLE_ORDER_DETAILS, COLUMN_BOOK_ID_FK_ORDER + "=?", new String[]{String.valueOf(bookId)});
        int result = db.delete(TABLE_BOOKS, COLUMN_BOOK_ID + "=?", new String[]{String.valueOf(bookId)});
        return result > 0;
    }

    // Lấy tất cả sách
    public Cursor getAllBooks() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_BOOKS;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    // Thêm sách vào giỏ hàng
    public boolean addToCart(int userId, int bookId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT " + COLUMN_QUANTITY + ", " + COLUMN_CART_ID + " FROM " + TABLE_CART +
                " WHERE " + COLUMN_USER_ID + "=? AND " + COLUMN_BOOK_ID_FK + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), String.valueOf(bookId)});
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int currentQuantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
            @SuppressLint("Range") int cartId = cursor.getInt(cursor.getColumnIndex(COLUMN_CART_ID));
            cursor.close();
            ContentValues values = new ContentValues();
            values.put(COLUMN_QUANTITY, currentQuantity + quantity);
            int result = db.update(TABLE_CART, values, COLUMN_CART_ID + "=?", new String[]{String.valueOf(cartId)});
            return result > 0;
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_USER_ID, userId);
            values.put(COLUMN_BOOK_ID_FK, bookId);
            values.put(COLUMN_QUANTITY, quantity);
            long result = db.insert(TABLE_CART, null, values);
            return result != -1;
        }
    }

    // Lấy danh sách sách trong giỏ hàng của người dùng (trả về Cursor)
    public Cursor getCartItems(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c." + COLUMN_CART_ID + ", c." + COLUMN_BOOK_ID_FK + ", c." + COLUMN_QUANTITY + ", b." + COLUMN_TITLE + ", b." + COLUMN_AUTHOR + ", b." + COLUMN_PRICE + ", b." + COLUMN_IMAGE +
                " FROM " + TABLE_CART + " c" +
                " JOIN " + TABLE_BOOKS + " b ON c." + COLUMN_BOOK_ID_FK + "=b." + COLUMN_BOOK_ID +
                " WHERE c." + COLUMN_USER_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        return cursor;
    }

    // Lấy danh sách sách trong giỏ hàng của người dùng (trả về List<CartItem>)
    public List<CartItem> getCartItemsList(int userId) {
        List<CartItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT c." + COLUMN_CART_ID + ", c." + COLUMN_BOOK_ID_FK + ", c." + COLUMN_QUANTITY + ", b." + COLUMN_TITLE + ", b." + COLUMN_AUTHOR + ", b." + COLUMN_PRICE + ", b." + COLUMN_IMAGE +
                " FROM " + TABLE_CART + " c" +
                " JOIN " + TABLE_BOOKS + " b ON c." + COLUMN_BOOK_ID_FK + "=b." + COLUMN_BOOK_ID +
                " WHERE c." + COLUMN_USER_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int cartId = cursor.getInt(cursor.getColumnIndex(COLUMN_CART_ID));
                @SuppressLint("Range") int bookId = cursor.getInt(cursor.getColumnIndex(COLUMN_BOOK_ID_FK));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                @SuppressLint("Range") String author = cursor.getString(cursor.getColumnIndex(COLUMN_AUTHOR));
                @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex(COLUMN_PRICE));
                @SuppressLint("Range") String image = cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE));
                cartItems.add(new CartItem(cartId, bookId, quantity, title, author, price, image));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return cartItems;
    }

    // Xóa sách khỏi giỏ hàng
    public boolean removeFromCart(int cartId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CART, COLUMN_CART_ID + "=?", new String[]{String.valueOf(cartId)});
        return result > 0;
    }

    // Xóa toàn bộ giỏ hàng của người dùng
    public boolean clearCart(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_CART, COLUMN_USER_ID + "=?", new String[]{String.valueOf(userId)});
        return result > 0;
    }

    // Thêm thông báo - Mới
    public boolean addNotification(int userId, String message, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NOTIFICATION_USER_ID, userId);
        values.put(COLUMN_MESSAGE, message);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_IS_READ, 0); // Mặc định là chưa đọc
        long result = db.insert(TABLE_NOTIFICATIONS, null, values);
        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to add notification for userId: " + userId);
        }
        return result != -1;
    }

    // Lấy danh sách thông báo của người dùng - Mới
    public Cursor getNotifications(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NOTIFICATIONS +
                " WHERE " + COLUMN_NOTIFICATION_USER_ID + "=?" +
                " ORDER BY " + COLUMN_DATE + " DESC";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        return cursor;
    }

    // Đếm số thông báo chưa đọc - Mới
    public int getUnreadNotificationCount(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_NOTIFICATIONS +
                " WHERE " + COLUMN_NOTIFICATION_USER_ID + "=? AND " + COLUMN_IS_READ + "=0";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    // Cập nhật trạng thái thông báo (đã đọc) - Mới
    public boolean markNotificationAsRead(int notificationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IS_READ, 1);
        int result = db.update(TABLE_NOTIFICATIONS, values, COLUMN_NOTIFICATION_ID + "=?", new String[]{String.valueOf(notificationId)});
        return result > 0;
    }

    // Trong DatabaseHelper.java
    public boolean addOrder(int userId, List<CartItem> cartItems, String orderDate, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_USER_ID_FK, userId);
            orderValues.put(COLUMN_ORDER_DATE, orderDate);
            orderValues.put(COLUMN_STATUS, status);
            long orderId = db.insert(TABLE_ORDERS, null, orderValues);

            if (orderId == -1) {
                Log.e("DatabaseHelper", "Failed to insert order for userId: " + userId);
                return false;
            }

            for (CartItem item : cartItems) {
                if (item == null) {
                    Log.e("DatabaseHelper", "CartItem is null in cartItems list for userId: " + userId);
                    return false;
                }
                ContentValues detailValues = new ContentValues();
                detailValues.put(COLUMN_ORDER_ID_FK, orderId);
                detailValues.put(COLUMN_BOOK_ID_FK_ORDER, item.getBookId());
                detailValues.put(COLUMN_QUANTITY_ORDER, item.getQuantity());
                long result = db.insert(TABLE_ORDER_DETAILS, null, detailValues);
                if (result == -1) {
                    Log.e("DatabaseHelper", "Failed to insert order detail for orderId: " + orderId + ", bookId: " + item.getBookId());
                    return false;
                }
            }

            // Thêm thông báo khi đặt hàng thành công - Mới
            addNotification(userId, "Đơn hàng #" + orderId + " của bạn đã được đặt thành công!", orderDate);

            // Xóa giỏ hàng sau khi đặt hàng thành công
            boolean cartCleared = clearCart(userId);
            if (!cartCleared) {
                Log.e("DatabaseHelper", "Failed to clear cart for userId: " + userId);
            }

            db.setTransactionSuccessful();
            Log.d("DatabaseHelper", "Order added successfully for userId: " + userId + ", orderId: " + orderId);
            return true;
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding order for userId: " + userId + ": " + e.getMessage(), e);
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Lấy danh sách đơn hàng của người dùng
    public Cursor getOrders(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT o." + COLUMN_ORDER_ID + ", o." + COLUMN_ORDER_DATE + ", o." + COLUMN_STATUS +
                " FROM " + TABLE_ORDERS + " o" +
                " WHERE o." + COLUMN_USER_ID_FK + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        return cursor;
    }

    // Lấy chi tiết đơn hàng
    public Cursor getOrderDetails(int orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT od." + COLUMN_BOOK_ID_FK_ORDER + ", od." + COLUMN_QUANTITY_ORDER + ", b." + COLUMN_TITLE + ", b." + COLUMN_AUTHOR + ", b." + COLUMN_PRICE + ", b." + COLUMN_IMAGE +
                " FROM " + TABLE_ORDER_DETAILS + " od" +
                " JOIN " + TABLE_BOOKS + " b ON od." + COLUMN_BOOK_ID_FK_ORDER + "=b." + COLUMN_BOOK_ID +
                " WHERE od." + COLUMN_ORDER_ID_FK + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(orderId)});
        return cursor;
    }

    // Lấy tất cả đơn hàng (dành cho nhân viên)
    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT o." + COLUMN_ORDER_ID + ", o." + COLUMN_USER_ID_FK + ", o." + COLUMN_ORDER_DATE + ", o." + COLUMN_STATUS + ", u." + COLUMN_USERNAME +
                " FROM " + TABLE_ORDERS + " o" +
                " JOIN " + TABLE_USERS + " u ON o." + COLUMN_USER_ID_FK + "=u." + COLUMN_ID;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    // Cập nhật trạng thái đơn hàng
    public boolean updateOrderStatus(int orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        int result = db.update(TABLE_ORDERS, values, COLUMN_ORDER_ID + "=?", new String[]{String.valueOf(orderId)});
        return result > 0;
    }

    // Đếm số lượng sách
    public int getBookCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_BOOKS, null);
        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(0);
            cursor.close();
        }
        return count;
    }

    // Lấy thông tin người dùng - Mới
    public User getUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_ID + "=?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        User user = null;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String username = cursor.getString(cursor.getColumnIndex(COLUMN_USERNAME));
            @SuppressLint("Range") String email = cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL));
            @SuppressLint("Range") String password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
            @SuppressLint("Range") String role = cursor.getString(cursor.getColumnIndex(COLUMN_ROLE));
            user = new User(userId, username, email, password, role);
        }
        cursor.close();
        return user;
    }

    // Cập nhật thông tin người dùng - Mới
    public boolean updateUser(int userId, String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);
        int result = db.update(TABLE_USERS, values, COLUMN_ID + "=?", new String[]{String.valueOf(userId)});
        return result > 0;
    }

    // Thêm vào DatabaseHelper.java
    public Cursor getAllUsersWithRole(String role) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_USERS + " WHERE " + COLUMN_ROLE + "=?";
        return db.rawQuery(query, new String[]{role});
    }
}