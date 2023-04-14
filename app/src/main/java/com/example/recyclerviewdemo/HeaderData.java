package com.example.recyclerviewdemo;

public class HeaderData
{
    private final String date;
    private final double income;
    private final double expense;

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
}