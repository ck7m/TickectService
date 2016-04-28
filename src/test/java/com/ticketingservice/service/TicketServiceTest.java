package com.ticketingservice.service;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.ticketingservice.model.Level;
import com.ticketingservice.model.Seat;
import com.ticketingservice.model.SeatHold;
import com.ticketingservice.model.Stage;

public class TicketServiceTest {
    
    private TicketService ticketService;
    private Stage stage =  createStage();
    
    @Rule
    public ExpectedException illegalArgument = ExpectedException.none();
    
    @Before
    public void setUp(){       
        ticketService = new TicketServiceImpl(stage);
    }

    private Stage createStage() {
        Level level1 = new Level(1, "orchestra", 100, 10, 15);
        Level level2 = new Level(2, "main", 75, 10, 25);
        Stage stage = new Stage();
        stage.getLevels().add(level1);
        stage.getLevels().add(level2);
        return stage;
    }

    @Test
    public void testNumSeatsAvailable_onStart() {
        int totalNumOfSeats = ticketService.numSeatsAvailable(Optional.empty());
        int numOfSeats=0;
        for(Level level : stage.getLevels()){
            numOfSeats += level.getMaxSeats();
        }
        assertEquals(numOfSeats, totalNumOfSeats);
    }
    
    @Test
    public void testNumSeatsAvailable_afterHold() {
        Seat seat = new Seat();
        stage.getLevels().stream().filter((level)->level.getLevelId()==2).forEach((level)->{            
            Map<Integer,Seat> column = new HashMap<>();            
           for(int i = level.getColumns()/2;i<level.getColumns();i++){
              column.put(i,seat);
            }
           level.getSeatsMap().put(1, column);
        });
        int totalNumOfSeats = ticketService.numSeatsAvailable(Optional.empty());
        int numOfSeats=0;
        for(Level level : stage.getLevels()){
            numOfSeats += level.getMaxSeats();
        }
        assertEquals((numOfSeats-12), totalNumOfSeats);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidMinLevel_negative() {   
        ticketService.findAndHoldSeats(10, Optional.of(-1), Optional.empty(), "abc@abc.com");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidMinLevel_gtmax() {   
        ticketService.findAndHoldSeats(10, Optional.of(stage.getLevels().size()+1), Optional.empty(), "abc@abc.com");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidMaxLevel_negative() {   
        ticketService.findAndHoldSeats(10, Optional.of(1), Optional.of(-4), "abc@abc.com");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidMaxLevel_gtmax() {   
        ticketService.findAndHoldSeats(10, Optional.empty(),Optional.of(stage.getLevels().size()+1), "abc@abc.com");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidnumSeats_zero() {   
        ticketService.findAndHoldSeats(0, Optional.of(1), Optional.of(4), "abc@abc.com");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidSeats_negative() {   
        ticketService.findAndHoldSeats(-10, Optional.empty(),Optional.of(stage.getLevels().size()), "abc@abc.com");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidCustomerEmail_null() {   
        ticketService.findAndHoldSeats(0, Optional.of(1), Optional.of(4),null);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testFindAndHoldSeats_invalidCustomerEmail_empty() {   
        ticketService.findAndHoldSeats(0, Optional.of(1), Optional.of(4),"  ");
    }
    
    @Test
    public void testFindAndHoldSeats() {
        int availableSeats = ticketService.numSeatsAvailable(Optional.empty());
        SeatHold seatHold=ticketService.findAndHoldSeats(10, Optional.of(1), Optional.empty(),"abc@abc.com");
        assertNotNull(seatHold);
        assertNotNull(seatHold.getId());
        assertNotNull(seatHold.getSeats());
        assertNotNull(seatHold.getCustomerEmail());
        assertEquals((availableSeats-seatHold.getSeats().size()),ticketService.numSeatsAvailable(Optional.empty()));
    }

    @Test
    public void testReserveSeats() {
        fail("Not yet implemented");
    }

}
