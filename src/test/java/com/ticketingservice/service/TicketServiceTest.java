package com.ticketingservice.service;

import com.ticketingservice.dao.TicketServiceDaoImpl;
import com.ticketingservice.exception.TicketServiceException;
import com.ticketingservice.factories.StageFactory;
import com.ticketingservice.helpers.CollectAvailableSeats;
import com.ticketingservice.model.Level;
import com.ticketingservice.model.Seat;
import com.ticketingservice.model.SeatHold;
import com.ticketingservice.model.Stage;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Matchers;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by Chandramohan on 4/29/16.
 */
public class TicketServiceTest {

    private TicketServiceImpl ticketService;
    private Stage stage = createStage();

    @Rule
    public ExpectedException illegalArgument = ExpectedException.none();

    TicketServiceDaoImpl daoImplMock = mock(TicketServiceDaoImpl.class);

    @Before
    public void setUp() {
        ticketService = new TicketServiceImpl();
        ticketService.setStage(stage);
        ReflectionTestUtils.setField(ticketService, "ticketServiceDao", daoImplMock);
        doReturn(new SeatHold()).when(daoImplMock).saverUpdate(Matchers.<SeatHold>any());
    }

    private Stage createStage() {
        Level level1 = new Level(1, "orchestra", 100, 10, 15);
        Level level2 = new Level(2, "main", 75, 10, 25);
        Stage stage = StageFactory.createStage(level1, level2);
        return stage;
    }

    @Test
    public void testNumSeatsAvailable_onStart() {
        int totalNumOfSeats = ticketService.numSeatsAvailable(Optional.empty());
        int numOfSeats = 0;
        for (Level level : stage.getLevels()) {
            numOfSeats += level.getMaxSeats();
        }
        assertEquals(numOfSeats, totalNumOfSeats);
    }

    @Test
    public void testNumSeatsAvailable_afterHold() {
        Seat seat = new Seat();
        stage.getLevels().stream().filter((level) -> level.getLevelId() == 2).forEach((level) -> {
            Map<Integer, Seat> column = new HashMap<>();
            for (int i = level.getColumns() / 2; i < level.getColumns(); i++) {
                column.put(i, seat);
            }
            level.getSeatsMap().put(1, column);
        });
        int totalNumOfSeats = ticketService.numSeatsAvailable(Optional.empty());
        int numOfSeats = 0;
        for (Level level : stage.getLevels()) {
            numOfSeats += level.getMaxSeats();
        }
        assertEquals((numOfSeats - 13), totalNumOfSeats);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidMinLevel_negative() throws TicketServiceException {
        ticketService.findAndHoldSeats(10, Optional.of(-1), Optional.empty(), "abc@abc.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidMinLevel_gtmax() throws TicketServiceException {
        ticketService.findAndHoldSeats(10, Optional.of(stage.getLevels().size() + 1), Optional.empty(), "abc@abc.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidMaxLevel_negative() throws TicketServiceException {
        ticketService.findAndHoldSeats(10, Optional.of(1), Optional.of(-4), "abc@abc.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidMaxLevel_gtmax() throws TicketServiceException {
        ticketService.findAndHoldSeats(10, Optional.empty(), Optional.of(stage.getLevels().size() + 1), "abc@abc.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidnumSeats_zero() throws TicketServiceException {
        ticketService.findAndHoldSeats(0, Optional.of(1), Optional.of(4), "abc@abc.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidSeats_negative() throws TicketServiceException {
        ticketService.findAndHoldSeats(-10, Optional.empty(), Optional.of(stage.getLevels().size()), "abc@abc.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidCustomerEmail_null() throws TicketServiceException {
        ticketService.findAndHoldSeats(0, Optional.of(1), Optional.of(4), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidCustomerEmail_empty() throws TicketServiceException {
        ticketService.findAndHoldSeats(0, Optional.of(1), Optional.of(4), "  ");
    }


    @Test(expected = TicketServiceException.class)
    public void testFindAndHoldSeats_gtThanLevelLimit() throws TicketServiceException {
        ticketService.findAndHoldSeats(300, Optional.of(2), Optional.of(2), " asdf ");
    }

    @Test
    public void testFindAndHoldSeats_resetSeats() throws TicketServiceException {
        CollectAvailableSeats collectAvailableSeatsBeforeCall = CollectAvailableSeats.collectAvailableSeats(stage.getLevels());
        doThrow(SQLException.class).when(daoImplMock).saverUpdate(Matchers.<SeatHold>any());
        ticketService.findAndHoldSeats(170, Optional.of(1), Optional.empty(), " abc ");
        assertEquals(collectAvailableSeatsBeforeCall.getTotalAvailableSeats(), ticketService.numSeatsAvailable(Optional.empty()));
    }

    @Test
    public void testReserveSeats_success() throws TicketServiceException {
        when(daoImplMock.confirmBooking(Matchers.anyInt())).thenReturn(SeatHold.SeatHoldStatus.BOOKED.name());
        String confirmation = ticketService.reserveSeats(1, "customer@abc.com");
        assertEquals("BOOKED", confirmation);
    }

    @Test(expected = TicketServiceException.class)
    public void testReserveSeats_failure() throws TicketServiceException {
        when(daoImplMock.confirmBooking(Matchers.anyInt())).thenThrow(SQLException.class);
        ticketService.reserveSeats(1, "customer@abc.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReserveSeats_inputValidation_negative() throws TicketServiceException {
        ticketService.reserveSeats(-2, "customer@abc.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testReserveSeats_inputValidation_zero() throws TicketServiceException {
        ticketService.reserveSeats(0, "customer@abc.com");
    }

}
