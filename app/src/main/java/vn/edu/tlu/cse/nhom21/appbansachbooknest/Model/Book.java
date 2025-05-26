package vn.edu.tlu.cse.nhom21.appbansachbooknest.Model;

public class Book {
    private int id;
    private String title;
    private String author;
    private String genre;
    private double price;
    private String image;

    public Book(int id, String title, String author, String genre, double price, String image) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.price = price;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public double getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }
}