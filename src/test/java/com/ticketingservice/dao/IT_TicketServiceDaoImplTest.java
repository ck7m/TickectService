package com.ticketingservice.dao;

import com.ticketingservice.SpringJunitRunner;
import com.ticketingservice.model.Seat;
import com.ticketingservice.model.SeatHold;
import com.ticketingservice.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by lva833 on 4/29/16.
 */
public class IT_TicketServiceDaoImplTest extends SpringJunitRunner {
    @Resource
    private TicketServiceDao ticketServiceDao;

    private SeatHold createSeatHold(SeatHold.SeatHoldStatus status) {
        Seat[] seats = {TestUtils.createSeat(1, 2, 1, Seat.SeatStatus.HOLD), TestUtils.createSeat(1, 3, 1, Seat.SeatStatus.HOLD)};
        return TestUtils.createSeatHold("abc@abc.com", status, seats);
    }

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void save() throws Exception {
        SeatHold seathold = createSeatHold(SeatHold.SeatHoldStatus.HOLD);
        SeatHold seatHoldtmp = ticketServiceDao.saverUpdate(seathold);
        assertNotNull(seatHoldtmp);
        assertNotNull(seatHoldtmp.getId());
    }

    @Test
    public void update() throws Exception {
        SeatHold seathold = createSeatHold(SeatHold.SeatHoldStatus.HOLD);
        SeatHold seatHoldtmp = ticketServiceDao.saverUpdate(seathold);
        assertNotNull(seatHoldtmp);
        assertNotNull(seatHoldtmp.getId());
        assertNotNull(seatHoldtmp.getSeats());
        assertEquals(2, seatHoldtmp.getSeats().size());
        seatHoldtmp.setStatus(SeatHold.SeatHoldStatus.EXPIRED.name());
        seatHoldtmp.getSeats().clear();
        ticketServiceDao.saverUpdate(seatHoldtmp);
        assertNotNull(seatHoldtmp);
        assertNotNull(seatHoldtmp.getId());
        assertTrue(seatHoldtmp.getSeats().size() == 0);
    }

    @Test
    public void getSeatHoldByStatus() {
        createAndGetSeatHold();
    }

    private SeatHold createAndGetSeatHold(SeatHold.SeatHoldStatus status) {
        SeatHold seatHold = createSeatHold(status);
        ticketServiceDao.saverUpdate(seatHold);
        SeatHold seatHoldFromDb = getAndVerifySeatHold(seatHold, status);
        return seatHoldFromDb;
    }

    private SeatHold getAndVerifySeatHold(SeatHold seatHold, SeatHold.SeatHoldStatus status) {
        SeatHold seatHoldFromDb = ticketServiceDao.getSeatHoldByStatus(seatHold.getId(), status);
        assertNotNull(seatHoldFromDb);
        assertEquals(seatHold.getId(), seatHoldFromDb.getId());
        assertEquals(seatHold.getCustomerEmail(), seatHoldFromDb.getCustomerEmail());
        assertEquals(status.name(), seatHoldFromDb.getStatus());
        assertNotNull(seatHold.getSeats());
        assertNotNull(seatHoldFromDb.getSeats());
        assertEquals(seatHold.getSeats().size(), seatHoldFromDb.getSeats().size());
        return seatHoldFromDb;
    }


    @Test
    public void getSeatHoldByStatus_notmatchig() {
        SeatHold seatHold = createSeatHold(SeatHold.SeatHoldStatus.BOOKED);
        ticketServiceDao.saverUpdate(seatHold);
        SeatHold seatHoldFromDb = ticketServiceDao.getSeatHoldByStatus(seatHold.getId(), SeatHold.SeatHoldStatus.HOLD);
        assertNull(seatHoldFromDb);
    }

    @Test
    public void confirmBooking_success() throws Exception {
        SeatHold seatHold = createAndGetSeatHold();
        String confirmation = ticketServiceDao.confirmBooking(seatHold.getId());
        assertEquals(SeatHold.SeatHoldStatus.BOOKED.name(), confirmation);
        getAndVerifySeatHold(seatHold, SeatHold.SeatHoldStatus.BOOKED);
    }

    @Test
    public void confirmBooking_expired() throws Exception {
        SeatHold seatHold = createAndGetSeatHold(SeatHold.SeatHoldStatus.EXPIRED);
        String confirmation = ticketServiceDao.confirmBooking(seatHold.getId());
        assertNotNull(confirmation);
        assertNotEquals("BOOKED", confirmation);
        getAndVerifySeatHold(seatHold, SeatHold.SeatHoldStatus.EXPIRED);
    }

    @Test
    public void confirmBooking_invalidId() throws Exception {
        SeatHold seatHold = createAndGetSeatHold(SeatHold.SeatHoldStatus.HOLD);
        String confirmation = ticketServiceDao.confirmBooking(1000);
        assertNotNull(confirmation);
        assertEquals("NOT_FOUND", confirmation);
        getAndVerifySeatHold(seatHold, SeatHold.SeatHoldStatus.HOLD);
    }

    private SeatHold createAndGetSeatHold() {
        return this.createAndGetSeatHold(SeatHold.SeatHoldStatus.HOLD);
    }


}