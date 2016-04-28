package com.ticketingservice.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Level {
    
    private int levelId;
    private String levelName;
    private double price;
    private int rows;
    private int columns;
    private Seat[][] seats;
    private Map<Integer,Map<Integer,Seat>> seatsMap;
    
    public Level(int levelId, String levelName, double price, int rows, int columns) {
        this.levelId = levelId;
        this.levelName = levelName;
        this.price = price;
        this.rows = rows;
        this.columns = columns;
        this.seats = new Seat[rows][columns];
    }

    public Seat[][] getSeats() {
        return seats;
    }


    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getLevelId() {
        return levelId;
    }

    public String getLevelName() {
        return levelName;
    }

    public double getPrice() {
        return price;
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }
    
    public int getMaxSeats(){
        return rows*columns;
    }

    public Map<Integer,Map<Integer,Seat>> getSeatsMap() {
        if(seatsMap==null){
            seatsMap = new HashMap<>();
        }
        return seatsMap;
    }

    public void setSeatsMap(Map<Integer,Map<Integer,Seat>> seatsMap) {
        this.seatsMap = seatsMap;
    }
    
    public static Comparator<Level> getComparatorbyLevelId(){
        Comparator<Level> byLevelId = (Level o1, Level o2)->Integer.valueOf(o1.getLevelId()).compareTo(o2.getLevelId());
        return byLevelId;
    }
}
