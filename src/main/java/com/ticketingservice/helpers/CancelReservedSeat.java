package com.ticketingservice.helpers;

import com.ticketingservice.dao.TicketServiceDao;
import com.ticketingservice.model.SeatHold;

import java.util.concurrent.Callable;

/**
 * Created by lva833 on 4/29/16.
 */
public class CancelReservedSeat implements Callable<SeatHold> {


    private final TicketServiceDao ticketServiceDao;

    public CancelReservedSeat(TicketServiceDao ticketServiceDao) {
        this.ticketServiceDao = ticketServiceDao;
    }

    @Override
    public SeatHold call() throws Exception {
        return null;
    }


}
