package com.ticketingservice.helpers;

import com.ticketingservice.dao.TicketServiceDao;
import com.ticketingservice.model.Seat;
import com.ticketingservice.model.SeatHold;
import com.ticketingservice.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Created by lva833 on 4/29/16.
 */
public class CancelReservedSeatTest {
    @Mock
    private TicketServiceDao ticketServiceDao;

    private CancelReservedSeat cancelReservedSeat;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        cancelReservedSeat = new CancelReservedSeat(ticketServiceDao);
    }

    private SeatHold createSeatHold() {
        Seat[] seats = {TestUtils.createSeat(1, 2, 1, Seat.SeatStatus.HOLD), TestUtils.createSeat(1, 3, 1, Seat.SeatStatus.HOLD)};
        return TestUtils.createSeatHold("abc@abc.com", SeatHold.SeatHoldStatus.HOLD, seats);
    }

    @Test
    public void call() throws Exception {
        // when(ticketServiceDao.getSeatHold(1)).thenReturn(createSeatHold());
        //SeatHold seatHold = ticketServiceDao.getSeatHold(1);


    }

    @Test
    public void call1() throws Exception {

    }

}