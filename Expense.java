package com.example.expensestracker;

public class Expense {
    private int id;
    private double amount;
    private String category;
    private String date;
    private String note;

    public Expense(int id, double amount, String category, String date, String note) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.note = note;
    }

    // Getters and setters
    public int getId() { return id; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getDate() { return date; }
    public String getNote() { return note; }
}