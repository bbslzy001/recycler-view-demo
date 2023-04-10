package com.example.recyclerviewdemo;

public class ItemData
{
    private String time;
    private String name;
    private double amount;

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