package com.ticketingservice.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Seat implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum SeatStatus {
        AVAILABLE,
        BOOKED,
        HOLD
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private SeatStatus status = SeatStatus.AVAILABLE;
    @Column(name = "row_id")
    private Integer rowId;
    @Column(name = "col_id")
    private Integer colId;
    @Column(name = "level_id")
    private Integer levelId;

    @ManyToOne(fetch = FetchType.LAZY)
    private SeatHold seatHold;

    public SeatHold getSeatHold() {
        return seatHold;
    }

    public void setSeatHold(SeatHold seatHold) {
        this.seatHold = seatHold;
    }

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
