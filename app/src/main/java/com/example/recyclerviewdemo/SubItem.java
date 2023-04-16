package com.example.recyclerviewdemo;

public class SubItem
{
    private final String time;
    private final String name;
    private final double amount;

    public SubItem(String time, String name, double amount)
    {
        this.time = time;
        this.name = name;
        this.amount = amount;
    }

    public String getTime()
    {
        return time;
    }

    public String getName()
    {
        return name;
    }

    public double getAmount()
    {
        return amount;
    }
}