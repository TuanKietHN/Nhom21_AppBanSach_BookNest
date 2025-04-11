package vn.edu.tlu.cse.nhom21.appbansachbooknest.Model;

public class OrderDetail {
    private int bookId;
    private String title;
    private String author;
    private double price;
    private String image;
    private int quantity;

    public OrderDetail(int bookId, String title, String author, double price, String image, int quantity) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public double getPrice() { return price; }
    public String getImage() { return image; }
    public int getQuantity() { return quantity; }
}