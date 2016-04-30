package com.ticketingservice.service;

import com.ticketingservice.SpringJunitRunner;
import com.ticketingservice.exception.TicketServiceException;
import com.ticketingservice.factories.StageFactory;
import com.ticketingservice.model.Level;
import com.ticketingservice.model.SeatHold;
import com.ticketingservice.model.Stage;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.*;


public class IT_TicketServiceTest extends SpringJunitRunner {

    @Autowired
    private TicketService ticketService;
    private Stage stage = createStage();

    @Before
    public void setUp() {
        TicketServiceImpl ticketService = (TicketServiceImpl) this.ticketService;
        ticketService.setStage(stage);
    }

    private Stage createStage() {
        Level level1 = new Level(1, "orchestra", 100, 10, 10);
        Level level2 = new Level(2, "main", 75, 10, 15);
        Stage stage = StageFactory.createStage(level1, level2);
        return stage;
    }


    @Test
    public void testFindAndHoldSeats() throws TicketServiceException {
        callfindAndHoldSeats(250, "abc@abc.com");
    }

    private SeatHold callfindAndHoldSeats(int numOfSeats, String email) throws TicketServiceException {
        int availableSeats = ticketService.numSeatsAvailable(Optional.empty());
        SeatHold seatHold = ticketService.findAndHoldSeats(numOfSeats, Optional.of(1), Optional.empty(), email);
        assertNotNull(seatHold);
        assertNotNull(seatHold.getId());
        assertNotNull(seatHold.getSeats());
        assertEquals(numOfSeats, seatHold.getSeats().size());
        assertEquals(email, seatHold.getCustomerEmail());
        assertEquals((availableSeats - seatHold.getSeats().size()), ticketService.numSeatsAvailable(Optional.empty()));
        return seatHold;
    }

    @Test
    public void testFindAndHoldSeats_parallelError() throws TicketServiceException, InterruptedException, ExecutionException {
        FutureTask<SeatHold> thread1 = new FutureTask<>(new HoldSeatsCallable(250, "th1@abc.com"));
        FutureTask<SeatHold> thread2 = new FutureTask<>(new HoldSeatsCallable(250, "th2@abc.com"));
        thread1.run();
        thread2.run();

        Thread.sleep(1000);
        validateParallelExecutionResult(thread1, thread2);
        int availableSeats = ticketService.numSeatsAvailable(Optional.empty());
        assertTrue(availableSeats == 0);
    }

    private void validateParallelExecutionResult(FutureTask<SeatHold> thread1, FutureTask<SeatHold> thread2) throws InterruptedException {
        try {
            SeatHold seatHold1 = thread1.get();
            SeatHold seatHold2 = thread2.get();
        } catch (ExecutionException e) {
            assertNotNull(e.getCause());
            assertTrue(e.getCause() instanceof TicketServiceException);
        }
    }

    @Test
    public void testFindAndHoldSeats_parallelSuccess() throws TicketServiceException, InterruptedException, ExecutionException {
        FutureTask<SeatHold> thread1 = new FutureTask<>(new HoldSeatsCallable(150, "th1@abc.com"));
        FutureTask<SeatHold> thread2 = new FutureTask<>(new HoldSeatsCallable(100, "th2@abc.com"));
        thread1.run();
        thread2.run();

        Thread.sleep(1000);
        validateParallelExecutionResult(thread1, thread2);
        int availableSeats = ticketService.numSeatsAvailable(Optional.empty());
        assertTrue(availableSeats == 0);
    }


    @Test(expected = TicketServiceException.class)
    public void testFindAndHoldSeats_notavailable() throws TicketServiceException {
        ticketService.findAndHoldSeats(1000, Optional.of(1), Optional.empty(), "abc@abc.com");
    }

    @Test
    public void testFindAndHoldSeats_timeout() throws TicketServiceException, InterruptedException {
        SeatHold seatHold = ticketService.findAndHoldSeats(20, Optional.empty(), Optional.empty(), "customer1@abc.com");
        Thread.sleep(25000);
        String confirmation = ticketService.reserveSeats(seatHold.getId(), "customer1@abc.com");
        assertNotNull(confirmation);
        assertEquals("EXPIRED",confirmation);
    }

    private class HoldSeatsCallable implements Callable<SeatHold> {
        private final int numOfSeats;
        private final String email;

        HoldSeatsCallable(int numOfSeats, String email) {
            this.numOfSeats = numOfSeats;
            this.email = email;
        }

        public SeatHold call() throws TicketServiceException {
            return IT_TicketServiceTest.this.callfindAndHoldSeats(numOfSeats, email);
        }
    }

}
