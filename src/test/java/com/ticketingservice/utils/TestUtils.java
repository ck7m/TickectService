package com.ticketingservice.utils;

import com.ticketingservice.model.Seat;
import com.ticketingservice.model.SeatHold;

public class TestUtils {
    public TestUtils() {
    }

    public static SeatHold createSeatHold(String email, SeatHold.SeatHoldStatus status, Seat... seats) {
        SeatHold seatHold = new SeatHold();
        seatHold.setCustomerEmail(email);
        seatHold.setStatus(status.name());
        for (Seat seat : seats) {
            seatHold.addSeat(seat);
        }
        return seatHold;
    }

    public static Seat createSeat(int rowId, int colId, int levelId, Seat.SeatStatus seatStatus) {
        Seat seat = new Seat();
        seat.setColId(rowId);
        seat.setRowId(colId);
        seat.setLevelId(levelId);
        seat.setStatus(seatStatus);
        return seat;
    }
}