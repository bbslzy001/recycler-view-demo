package com.example.recyclerviewdemo;

public class ItemData
{
    private final String time;
    private final String name;
    private final double amount;

    public ItemData(String time, String name, double amount)
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