package com.example.barcodescanner;

public class Book {
    private float isbn;
    private String title;
    private String supplier;
    private float price;

    public Book(float isbn, String title, String supplier, float price) {
        this.title = title;
        this.supplier = supplier;
        this.price = price;
    }

    public String Printer() {
        return title + " : " + price + " - " + supplier;
    }

    public float getIsbn() {
        return isbn;
    }

    public void setIsbn(float isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
