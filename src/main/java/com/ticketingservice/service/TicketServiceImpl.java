package com.ticketingservice.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.ticketingservice.helpers.CollectAvailableSeats;
import com.ticketingservice.model.Level;
import com.ticketingservice.model.Seat;
import com.ticketingservice.model.Seat.SeatStatus;
import com.ticketingservice.model.SeatHold;
import com.ticketingservice.model.Stage;

public class TicketServiceImpl implements TicketService {

    private Stage stage;
    
    private AtomicInteger atomicInteger = new AtomicInteger(1);

    public TicketServiceImpl(Stage stage) {
        this.stage = stage;
    }

    @Override
    public int numSeatsAvailable(Optional<Integer> venueLevel) {
        Set<Level> levels = stage.getLevels(venueLevel, venueLevel);
        CollectAvailableSeats collectAvailableSeats = CollectAvailableSeats.collectAvailableSeats(levels);
        return collectAvailableSeats.getTotalAvailableSeats();
    }

    private int numSeatsAvailable(Level level) {
        int numOfAvailableSeats = 0;
        /*
         * for(Seat[] row: level.getSeats()){ // numOfAvailableSeats +=
         * Arrays.stream(row).sorted(comparator).filter(seat->(seat==null)).count(); Seat seat; for(int j=0;j <
         * level.getColumns();j++){ seat = row[j]; if(seat == null || seat.getStatus() == SeatStatus.AVAILABLE){
         * numOfAvailableSeats += level.getColumns()-j; break; } } }
         */
        numOfAvailableSeats += (level.getRows() - level.getSeatsMap().size()) * level.getColumns();
        for (Map<Integer, Seat> column : level.getSeatsMap().values()) {
            numOfAvailableSeats += (level.getColumns() - column.size());
        }
        return numOfAvailableSeats;
    }

    @Override
    public SeatHold findAndHoldSeats(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
            String customerEmail) {
        int numOfLevels = stage.getLevels().size();
        validatefindAndHoldSeatsArguments(numSeats, minLevel, maxLevel, customerEmail, numOfLevels);
        int remainingSeats = numSeats;
        Set<Level> levels = stage.getLevels(minLevel, maxLevel);
        CollectAvailableSeats collectAvailableSeats = CollectAvailableSeats.collectAvailableSeats(levels);
        if (collectAvailableSeats.getTotalAvailableSeats() < numSeats) {
            throw new RuntimeException("Not enough seats available");
        }
        Set<Seat> reservedSeats = new HashSet<>(numSeats);
        int rowId = 1, availableSeats = 0;
        for (Level level : levels) {
            while (remainingSeats > 0 && rowId <= level.getRows()) {
                availableSeats = collectAvailableSeats.getAvailableSeatsByRow(level.getLevelId(), rowId);
                if (availableSeats > 0) {
                    int colId = 1;
                    while (remainingSeats > 0 && availableSeats >= remainingSeats) {
                        if (colId <= level.getColumns() && !level.getSeatsMap().get(rowId).containsKey(colId)) {
                            Seat seat = new Seat();
                            seat.setColId(colId);
                            seat.setRowId(rowId);
                            seat.setStatus(SeatStatus.HOLD);
                            reservedSeats.add(seat);
                            remainingSeats--;
                            availableSeats--;
                        }
                        colId++;
                    }
                }
                rowId++;
            }
            if (remainingSeats <= 0) break;
        }
        SeatHold seatHold = new SeatHold();
        seatHold.setCustomerEmail(customerEmail);
        seatHold.setSeats(reservedSeats);
        seatHold.setId(atomicInteger.getAndIncrement());
        return seatHold;
    }

    private void validatefindAndHoldSeatsArguments(int numSeats, Optional<Integer> minLevel, Optional<Integer> maxLevel,
            String customerEmail, int numOfLevels) {
        if (minLevel.isPresent() && (minLevel.get() <= 0 || minLevel.get() > numOfLevels)) {
            throw new IllegalArgumentException(
                    "Invalid minLevel argument. MinLevel must be between 1 to" + numOfLevels);
        }
        if (maxLevel.isPresent() && (maxLevel.get() <= 0 || maxLevel.get() > numOfLevels)) {
            throw new IllegalArgumentException(
                    "Invalid maxLevel argument. MaxLevel must be between 1 to" + numOfLevels);
        }

        if (customerEmail == null || customerEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid CustomerEmail argument. Please provide valid customerEmail");
        }
        if (numSeats <= 0) {
            throw new IllegalArgumentException("Invalid numOfSeats argument. NumOfSeats must be greater than 0");
        }

    }

    @Override
    public String reserveSeats(int seatHoldId, String customerEmail) {
        // TODO Auto-generated method stub
        return null;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

}
