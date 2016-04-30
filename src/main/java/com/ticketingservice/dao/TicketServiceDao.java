package com.ticketingservice.dao;

import com.ticketingservice.model.SeatHold;

/**
 *
 * Created by Chandramohan on 4/29/16.
 */
public interface TicketServiceDao {

    SeatHold saverUpdate(SeatHold seatHold);

    String confirmBooking(int seatHoldId);

    SeatHold getSeatHoldByStatus(int seatHoldId, SeatHold.SeatHoldStatus hold);
}
