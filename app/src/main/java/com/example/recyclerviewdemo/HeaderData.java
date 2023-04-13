package com.example.recyclerviewdemo;

public class HeaderData
{
    private String date;
    private double income;
    private double expense;

    public HeaderData(String date, double income, double expense)
    {
        this.date = date;
        this.income = income;
        this.expense = expense;
    }

    public String getDate()
    {
        return date;
    }

    public double getIncome()
    {
        return income;
    }

    public double getExpense()
    {
        return expense;
    }

    @Override
    public String toString()
    {
        return "HeaderData{" +
                "date='" + date + '\'' +
                ", income=" + income +
                ", expense=" + expense +
                '}';
    }
}