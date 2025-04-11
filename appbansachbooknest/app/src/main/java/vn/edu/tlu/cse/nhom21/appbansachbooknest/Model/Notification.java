package vn.edu.tlu.cse.nhom21.appbansachbooknest.Model;

public class Notification {
    private int id;
    private int userId;
    private String message;
    private String date;
    private boolean isRead;

    public Notification(int id, int userId, String message, String date, boolean isRead) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.date = date;
        this.isRead = isRead;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public boolean isRead() {
        return isRead;
    }
}