package com.ticketingservice.model;

import java.io.Serializable;

public class Seat implements Serializable {

    private static final long serialVersionUID = 1L;
    
    public enum SeatStatus {    
        AVAILABLE,
        BOOKED,
        HOLD
    }
    
    private SeatStatus status = SeatStatus.AVAILABLE;    
    private Integer rowId;
    private Integer colId;
    private Integer levelId;
    
    public SeatStatus getStatus() {
        return status;
    }
    public void setStatus(SeatStatus status) {
        this.status = status;
    }
    public Integer getRowId() {
        return rowId;
    }
    public void setRowId(Integer rowId) {
        this.rowId = rowId;
    }
    public Integer getColId() {
        return colId;
    }
    public void setColId(Integer colId) {
        this.colId = colId;
    }
    public Integer getLevelId() {
        return levelId;
    }
    public void setLevelId(Integer levelId) {
        this.levelId = levelId;
    }

}
