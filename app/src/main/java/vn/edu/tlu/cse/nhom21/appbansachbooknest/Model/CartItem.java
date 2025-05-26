package vn.edu.tlu.cse.nhom21.appbansachbooknest.Model;

public class CartItem {
    private int cartId;
    private int bookId;
    private int quantity;
    private String bookTitle;
    private String bookAuthor;
    private double bookPrice;
    private String bookImage;

    public CartItem(int cartId, int bookId, int quantity, String bookTitle, String bookAuthor, double bookPrice, String bookImage) {
        this.cartId = cartId;
        this.bookId = bookId;
        this.quantity = quantity;
        this.bookTitle = bookTitle;
        this.bookAuthor = bookAuthor;
        this.bookPrice = bookPrice;
        this.bookImage = bookImage;
    }

    public int getCartId() {
        return cartId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public double getBookPrice() {
        return bookPrice;
    }

    public String getBookImage() {
        return bookImage;
    }
}