package com.ticketingservice.service;

import com.ticketingservice.dao.TicketServiceDao;
import com.ticketingservice.exception.TicketServiceException;
import com.ticketingservice.helpers.CollectAvailableSeats;
import com.ticketingservice.helpers.ScheduledTaskExecutor;
import com.ticketingservice.model.Level;
import com.ticketingservice.model.Seat;
import com.ticketingservice.model.Seat.SeatStatus;
import com.ticketingservice.model.SeatHold;
import com.ticketingservice.model.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main class to find and book the tickets. As <code>Stage</code> is not provided in the interface it is provided as field variable.
 * It can be loaded from DB in the future.
 *
 */
@Service
public class TicketServiceImpl implements TicketService {

    private Stage stage;

    private final AtomicInteger atomicInteger = new AtomicInteger(1);

    @Resource
    private TicketServiceDao ticketServiceDao;

    /**
     * Task executor to expire the HOLD seats after timeout.
     */
    @Resource
    private ScheduledTaskExecutor scheduledTaskExecutor;

    /**
     * Timeout in seconds to expire the Hold seats if not booked already.
     */
    @Value("${ticket.timeoutinseconds}")
    private long timeoutInSecs;

    @Override
    public int numSeatsAvailable(Optional<Integer> venueLevel) {
        Set<Level> levels = stage.getLevels(venueLevel, venueLevel);
        CollectAvailableSeats collectAvailableSeats = CollectAvailableSeats.collectAvailableSeats(levels);
        return collectAvailableSeats.getTotalAvailableSeats();
    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
                                     String customerEmail) throws TicketServiceException {
        int numOfLevels = stage.getLevels().size();
        validatefindAndHoldSeatsArguments(numSeats, minLevel, maxLevel, customerEmail, numOfLevels);
        Set<Level> levels = stage.getLevels(minLevel, maxLevel);
        Set<Seat> reservedSeats;
        SeatHold seatHold = null;
        synchronized (atomicInteger) {
            CollectAvailableSeats collectAvailableSeats = CollectAvailableSeats.collectAvailableSeats(levels);
            if (collectAvailableSeats.getTotalAvailableSeats() < numSeats) {
                throw new TicketServiceException("Sorry there are no enough seats available. Available Seats: "+collectAvailableSeats.getTotalAvailableSeats());
            }
            reservedSeats = new HashSet<>(numSeats);
            try {
                //create requested seats
                holdSeats(numSeats, levels, reservedSeats, collectAvailableSeats);
                if (reservedSeats.size() == 0) return null;

                seatHold = new SeatHold();
                seatHold.setCustomerEmail(customerEmail);
                seatHold.addSeat(reservedSeats);
                seatHold.setStatus(SeatHold.SeatHoldStatus.HOLD.name());
                seatHold.setCreationTs(new Date());
                ticketServiceDao.saverUpdate(seatHold);
                //add timeout for the HOLD seats
                this.cancelTimedOutReservation(seatHold.getId());
            } catch (Exception e) {
                //release the hold seats from each level
                resetSeats(reservedSeats);
            }
        }
        return seatHold;
    }

    /**
     * Reserve the available seats in the given levels
     * *
     * @param remainingSeats
     * @param levels
     * @param reservedSeats
     * @param collectAvailableSeats
     */
    private void holdSeats(int remainingSeats, Set<Level> levels, Set<Seat> reservedSeats, CollectAvailableSeats collectAvailableSeats) {
        for (Level level : levels) {
            int rowId = 1, availableSeats;
            while (remainingSeats > 0 && rowId <= level.getRows()) {
                availableSeats = collectAvailableSeats.getAvailableSeatsByRow(level.getLevelId(), rowId);
                int colId = 1;
                Map<Integer, Seat> row = level.getSeatsMap().get(rowId);
                while (remainingSeats > 0 && availableSeats > 0) {
                    if (colId <= level.getColumns() && !(row != null && row.containsKey(colId))) {
                        Seat seat = new Seat();
                        seat.setColId(colId);
                        seat.setRowId(rowId);
                        seat.setLevelId(level.getLevelId());
                        seat.setStatus(SeatStatus.HOLD);
                        reservedSeats.add(seat);
                        if (row == null) {
                            row = new HashMap<>();
                            level.getSeatsMap().put(rowId, row);
                        }
                        row.put(colId, seat);
                        remainingSeats--;
                        availableSeats--;
                    }
                    colId++;
                }
                rowId++;
            }
            if (remainingSeats <= 0)
                break;
        }
    }

    /**
     * Release the Hold seats. After the seat is released from the level. The seat is removed from the input set during each iteration.
     *
     * @param reservedSeats
     */
    private void resetSeats(Set<Seat> reservedSeats) {
        if (reservedSeats == null || reservedSeats.size() == 0) return;
        synchronized (atomicInteger) {
            Iterator<Seat> iterator = reservedSeats.iterator();
            for (Level level : stage.getLevels()) {
                while (iterator.hasNext()) {
                    Seat seat = iterator.next();
                    if (seat.getLevelId().equals(level.getLevelId())) {
                        level.getSeatsMap().get(seat.getRowId()).remove(seat.getColId());
                        iterator.remove();
                    }
                }
                if (reservedSeats.size() == 0) break;
                iterator = reservedSeats.iterator();
            }
        }
    }

    /**
     * Attach a Timeout task to each reservation.The task will executed after the given seconds.
     * First verifies the status of the seathold object in the DB. If it is not HOLD then the method will be returned without doing anything.
     * If the status is HOLD then the seats are removed the level and then DB is updated to EXPIRED Status.
     *
     * @param seatHoldId
     */
    private void cancelTimedOutReservation(int seatHoldId) {
        try {
            Callable<SeatHold> callable = () -> {
                SeatHold seatHold = ticketServiceDao.getSeatHoldByStatus(seatHoldId, SeatHold.SeatHoldStatus.HOLD);
                if (seatHold == null) return null;
                synchronized (atomicInteger) {
                    resetSeats(seatHold.getSeats());
                    seatHold.setStatus(SeatHold.SeatHoldStatus.EXPIRED.name());
                    seatHold.getSeats().clear();
                    ticketServiceDao.saverUpdate(seatHold);
                }
                return seatHold;
            };
            scheduledTaskExecutor.schedule(callable, timeoutInSecs);
        } catch (Exception e) {
            System.err.println("Error happened while canceling the seats. " + e.getMessage());
        }
    }

    private void validatefindAndHoldSeatsArguments(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
                                                   String customerEmail, int numOfLevels) {
        if (minLevel.isPresent() && (minLevel.get() <= 0 || minLevel.get() > numOfLevels)) {
            throw new IllegalArgumentException(
                    "Invalid minLevel argument. MinLevel must be between 1 to " + numOfLevels);
        }
        if (maxLevel.isPresent() && (maxLevel.get() <= 0 || maxLevel.get() > numOfLevels)) {
            throw new IllegalArgumentException(
                    "Invalid maxLevel argument. MaxLevel must be between 1 to " + numOfLevels);
        }

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid email. Please provide valid customerEmail");
        }
        if (numSeats <= 0) {
            throw new IllegalArgumentException("Invalid value. NumOfSeats must be greater than 0");
        }

    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) throws TicketServiceException {
        if (seatHoldId <= 0) {
            throw new IllegalArgumentException("Invalid seatHoldId.");
        }
        try {
            return ticketServiceDao.confirmBooking(seatHoldId);
        } catch (Exception e) {
            throw new TicketServiceException("Unable to reserveSeats now. Please tryagain later.");
        }
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
